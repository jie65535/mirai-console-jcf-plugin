package top.jie65535.jcf

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.MiraiLogger
import top.jie65535.jcf.model.modrinth.Project

/**
 * Handles Modrinth project update subscriptions and push notifications.
 *
 * @param service The Modrinth service.
 * @param logger  Logger instance.
 */
class ModrinthSubscribeHandler(
    private val service: ModrinthService,
    private val logger: MiraiLogger,
) {

    // region -- Subscription management

    /**
     * Clear subscriptions.
     *
     * @param projectId Project ID; null clears all subscriptions.
     * @param group     Group ID; null removes all subscriptions for the project.
     */
    fun clean(projectId: String? = null, group: Long? = null) {
        var updInner = false
        val projectSet = HashMap(PluginData.mrProjectsLastVersion)
        val subSet = HashMap(PluginData.mrSubscriptionSet)
        if (projectId == null) {
            subSet.clear()
            projectSet.clear()
            logger.info("清理所有 Modrinth 订阅")
        } else if (group == null) {
            projectSet -= projectId
            subSet -= projectId
            logger.info("清理 Modrinth 项目[$projectId]的订阅")
        } else {
            subSet[projectId]?.let {
                logger.info("清理 Modrinth 群/分组[$group]的订阅")
                updInner = true
                it -= group
            }
        }

        if (projectSet.size != PluginData.mrProjectsLastVersion.size) {
            PluginData.mrProjectsLastVersion = projectSet
        }
        if (subSet.size != PluginData.mrSubscriptionSet.size || updInner) {
            PluginData.mrSubscriptionSet = subSet
        }
    }

    /**
     * Cancel a subscription.
     *
     * @param projectId Project ID.
     * @param qq        The subscriber's QQ number.
     * @param group     Group ID; null means a personal subscription.
     */
    fun unsub(projectId: String, qq: Long, group: Long? = null) {
        val gid = group ?: GROUP_ID_SINGLE
        val subSet = HashMap(PluginData.mrSubscriptionSet)
        val groups = subSet[projectId] ?: return
        val members = groups[gid] ?: return
        members -= qq
        PluginData.mrSubscriptionSet = subSet
        logger.info("取消 Modrinth 订阅--{$projectId:{$gid:[$qq]}}")
    }

    /**
     * Add a subscription.
     *
     * @param projectId Project ID.
     * @param latestVersionId The currently latest version ID (used to track updates).
     * @param qq        The subscriber's QQ number.
     * @param group     Group ID; null means a personal subscription.
     */
    fun sub(projectId: String, latestVersionId: String?, qq: Long, group: Long? = null) {
        val gid = group ?: GROUP_ID_SINGLE
        val projectSet = HashMap(PluginData.mrProjectsLastVersion)
        val subSet = HashMap(PluginData.mrSubscriptionSet)
        if (projectId !in projectSet) projectSet[projectId] = latestVersionId ?: ""

        val groupSet = subSet[projectId] ?: mutableMapOf()
        val qqSet = groupSet[gid] ?: mutableListOf()
        var changed = gid !in groupSet
        subSet[projectId] = groupSet
        groupSet[gid] = qqSet
        if (qq !in qqSet) {
            qqSet += qq
            changed = true
            logger.info("添加 Modrinth 订阅--{$projectId:{$gid:[$qq]}}")
        }

        if (projectId !in PluginData.mrProjectsLastVersion) {
            PluginData.mrProjectsLastVersion = projectSet
        }
        if (changed) {
            PluginData.mrSubscriptionSet = subSet
        }
    }

    // endregion

    // region -- Update checking

    /**
     * Check all subscribed projects for updates.
     *
     * @param init When true, emit all projects even when unchanged (for initialization).
     * @return Flow of (projectId, latestVersionId) pairs; latestVersionId is empty if the project was not found.
     */
    private suspend fun checkUpdate(init: Boolean = false) = flow {
        val oldSet = PluginData.mrProjectsLastVersion
        if (oldSet.isNotEmpty()) {
            val fetchProjects = service.getProjects(oldSet.keys.toList())
                .associateBy { it.id }
            for ((projectId, oldVersionId) in oldSet) {
                try {
                    val project = fetchProjects[projectId]
                    if (project == null) {
                        emit(projectId to "")
                        continue
                    }
                    PROJECT_INFO_CACHE[projectId] = project
                    val latestVersionId = project.versions.firstOrNull() ?: ""
                    if (oldVersionId != latestVersionId || init) {
                        emit(projectId to latestVersionId)
                        if (!init) logger.info("Modrinth 项目更新【${project.title}】")
                    }
                } catch (e: Exception) {
                    logger.warning("Modrinth 检查更新异常: ${e.message}")
                    emit(projectId to "")
                }
            }
        }
    }

    /**
     * Fetch the changelog for a version.
     */
    private suspend fun getChangelog(versionId: String): String = try {
        val version = service.getVersion(versionId)
        version.changelog?.replace(Regex("\n+"), "\n") ?: ""
    } catch (e: Exception) {
        logger.warning("Modrinth 获取版本[$versionId]更新日志异常: ${e.message}")
        ""
    }

    /**
     * Send update notifications to all subscribers of a project.
     */
    private suspend fun send(sender: Bot, projectId: String, changelog: String) {
        if (changelog.isBlank()) return
        val subGroups = PluginData.mrSubscriptionSet[projectId] ?: return
        val projectTitle = PROJECT_INFO_CACHE[projectId]?.title ?: return
        val title = "你订阅的 Modrinth 项目【$projectTitle】更新啦！"
        val context = "更新日志：\n$changelog"
        subGroups.forEach { (group, qqs) ->
            if (group == GROUP_ID_SINGLE) {
                qqs.forEach {
                    sender.getFriend(it)?.apply {
                        sendMessage(title)
                        sendMessage(context)
                    }
                }
            } else {
                sender.getGroup(group)?.apply {
                    val titleChain = buildMessageChain {
                        qqs.forEach { +At(it) }
                        +"\n$title"
                    }
                    sendMessage(titleChain)
                    sendMessage(context)
                }
            }
        }
    }

    /**
     * Process a single update: fetch changelog and push notifications.
     */
    private suspend fun feedback(senderQQ: Long, projectId: String, versionId: String) {
        if (versionId.isBlank()) return
        Bot.instances.firstOrNull { it.isOnline && it.id == senderQQ }?.let { sender ->
            val changelog = getChangelog(versionId)
            send(sender, projectId, changelog)
        }
    }

    /**
     * The main subscription check loop.
     */
    private fun CoroutineScope.loop() = launch {
        val senderQQ = PluginConfig.subscribeSender
        if (senderQQ < 0) {
            logger.warning("Modrinth 订阅：未配置推送 bot，将收集订阅但无法推送消息。")
        }
        logger.info("Modrinth 订阅监听已启动")
        while (true) {
            delay(1000 * PluginConfig.checkInterval)
            if (isIdle) continue

            val subSet = HashMap(PluginData.mrSubscriptionSet)
            val projectVersions = HashMap(PluginData.mrProjectsLastVersion)
            checkUpdate()
                .buffer()
                .collect { (projectId, versionId) ->
                    if (versionId.isBlank()) {
                        projectVersions -= projectId
                        subSet -= projectId
                    } else {
                        projectVersions[projectId] = versionId
                        feedback(senderQQ, projectId, versionId)
                    }
                }
            PluginData.mrSubscriptionSet = subSet
            PluginData.mrProjectsLastVersion = projectVersions
        }
    }

    // endregion

    // region -- State

    /** Whether the handler is currently idle (not checking for updates). */
    var isIdle = true
        private set

    fun start() { isIdle = false }
    fun idle()  { isIdle = true  }

    /**
     * Initialize the handler: sync latest version IDs and start the update loop.
     */
    suspend fun load(scope: CoroutineScope) {
        logger.info("Modrinth 订阅：初始化中...")
        val subs = HashMap(PluginData.mrSubscriptionSet)
        val versions = HashMap(PluginData.mrProjectsLastVersion)
        checkUpdate(true)
            .buffer()
            .collect { (projectId, versionId) ->
                if (versionId.isBlank()) {
                    versions -= projectId
                    subs -= projectId
                } else {
                    versions[projectId] = versionId
                }
            }
        PluginData.mrSubscriptionSet = subs
        PluginData.mrProjectsLastVersion = versions
        scope.loop()
    }

    // endregion

    companion object {
        /** Marker for personal (non-group) subscriptions. */
        const val GROUP_ID_SINGLE: Long = 0

        /** Cache of recently seen project details. */
        private val PROJECT_INFO_CACHE: MutableMap<String, Project> = mutableMapOf()
    }
}
