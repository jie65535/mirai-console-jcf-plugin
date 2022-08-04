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
import top.jie65535.jcf.model.mod.Mod

/**
 * 处理订阅
 *
 * @param service api服务
 * @param  logger 日志
 */
class SubscribeHandler(
    private val service: MinecraftService,
    private val logger: MiraiLogger,
) {

    // region -- 参数

    /**
     * 清理订阅
     *
     * @param   mod 模组id；为null将清空所有订阅
     * @param group 群号；为null将移除指定mod下所有订阅
     */
    fun clean(mod: Int? = null, group: Long? = null) {
        var updInner = false
        val modSet = HashMap(PluginData.modsLastFile)
        val subSet = HashMap(PluginData.subscriptionSet)
        if (mod == null) {
            subSet.clear()
            modSet.clear()
            logger.info("清理所有订阅")
        } else if (group == null) {
            modSet -= mod
            subSet -= mod
            logger.info("清理mod[${MOD_INFO_CACHE[mod]?.name}]的订阅")
        } else {
            subSet[mod]?.let {
                logger.info("清理群/分组[$group]的订阅")
                updInner = true
                it -= group
            }
        }

        if (modSet.size != PluginData.modsLastFile.size) {
            PluginData.modsLastFile = modSet
        }
        if (subSet.size != PluginData.subscriptionSet.size || updInner) {
            PluginData.subscriptionSet = subSet
        }
    }

    /**
     * 取消订阅
     *
     * @param   mod 模组id
     * @param    qq 个人q号或群成员q号
     * @param group 群号；为null时（默认）表示个人订阅
     */
    fun unsub(mod: Int, qq: Long, group: Long? = null) {
        if (mod < 0 || qq < 0) return

        val gid = group ?: GROUP_ID_SINGLE
        val subSet = HashMap(PluginData.subscriptionSet)

        val groups = subSet[mod] ?: return
        val members = groups[gid] ?: return
        members -= qq
        PluginData.subscriptionSet = subSet
        logger.info("取消订阅--{$mod:{$gid:[$qq]}}")
    }

    /**
     * 记录订阅
     *
     * @param   mod 模组id
     * @param    qq q号
     * @param group 群号；为null时（默认）表示个人订阅
     */
    fun sub(mod: Int, qq: Long, group: Long? = null) {
        if (mod < 0 || qq < 0) return
        val gid = group ?: GROUP_ID_SINGLE
        val modSet = HashMap(PluginData.modsLastFile)
        val subSet = HashMap(PluginData.subscriptionSet)
        if (mod !in modSet) modSet[mod] = -1

        val groupSet = subSet[mod] ?: mutableMapOf()
        val qqSet = groupSet[gid] ?: mutableListOf()
        var changed = gid !in groupSet
        subSet[mod] = groupSet
        groupSet[gid] = qqSet
        if (qq !in qqSet) {
            qqSet += qq
            changed = true
            logger.info("添加订阅--{$mod:{$gid:[$qq]}}")
        }

        if (mod !in PluginData.modsLastFile) {
            PluginData.modsLastFile = modSet
        }
        if (changed) {
            PluginData.subscriptionSet = subSet
        }
    }
    // endregion

    // region -- 流程

    /**
     * 检查更新
     *
     * @param init 是否初始化
     * @return 检查到更新的{ mod : newFileId }
     */
    private suspend fun checkUpdate(init: Boolean = false) = flow {
        val oldSet = PluginData.modsLastFile
        if (oldSet.isNotEmpty()) {
            val fetchMods = service.getMods(oldSet.keys.toIntArray())
                .asSequence()
                .map { it.id to it }
                .toMap()
            for ((mod, old) in oldSet) {
                try {
                    val new = fetchMods[mod]
                    if (new == null) {
                        emit(mod to -1)
                        continue
                    }
                    MOD_INFO_CACHE[mod] = new
                    val last = new.latestFilesIndexes[0].fileId
                    if (old != last || init) {
                        emit(mod to last)
                        logger.info("模组更新【${new.name}】")
                    }
                } catch (e: Exception) {
                    logger.warning("err msg: ${e.message}")
                    emit(mod to -1)
                    continue
                }
            }// for
        }
    }

    /**
     * 获取更新日志
     *
     * @param  mod 模组id
     * @param file 文件id
     * @return 更新日志
     */
    private suspend fun getChangeLogs(mod: Int, file: Int): String = try {
        val changelog = service.getModFileChangelog(mod, file)
        MessageHandler.HTMLPattern.matcher(changelog)
            .replaceAll("")
            .replace(Regex("\n+"), "\n")
    } catch (e: Exception) {
        logger.warning("err msg: ${e.message}")
        ""
    }

    /**
     * 执行发送
     *
     * @param  sender 发送消息的bot
     * @param modLogs { mod : changeLog }
     */
    private suspend fun send(sender: Bot, modLogs: Pair<Int, String>) {
        val (mod, logs) = modLogs
        if (logs.isBlank()) return

        val subGroups = PluginData.subscriptionSet[mod] ?: return
        val modName = MOD_INFO_CACHE[mod]?.name ?: return
        val title = "你订阅的mod【$modName】更新啦！"
        val context = "更新日志：\n$logs"
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
            }// if else
        }// foreach
    }

    /**
     * 准备发送更新日志
     *
     * @param senderQQ 指定发送消息的机器人id
     * @param   updMod { mod : file }
     */
    private suspend fun feedback(senderQQ: Long, updMod: Pair<Int, Int>) {
        val (mod, file) = updMod
        if (mod < 0) return

        Bot.instances.firstOrNull {
            it.isOnline && it.id == senderQQ
        }?.let { sender ->
            val log = getChangeLogs(mod, file)
            send(sender, mod to log)
        }// let
    }

    /**
     * 循环执行
     */
    private fun CoroutineScope.loop() = launch {
        val senderQQ = PluginConfig.subscribeSender
        val interval = PluginConfig.checkInterval
        if (senderQQ < 0) {
            logger.warning("必须配置订阅信息推送bot（qq id）才可以进行订阅推送！")
            logger.warning("插件会持续收集订阅与检查mod更新，但无法进行消息推送。")
        }
        logger.info("subscription listening")
        while (true) {
            delay(1000 * interval)
            if (isIdle) continue

            val subSet = HashMap(PluginData.subscriptionSet)
            val modFiles = HashMap(PluginData.modsLastFile)
            checkUpdate()
                .buffer()
                .collect {
                    val (mod, file) = it
                    if (file < 0) {
                        modFiles -= mod
                        subSet -= mod
                    } else {
                        modFiles[mod] = file
                        feedback(senderQQ, it)
                    }
                }
            PluginData.subscriptionSet = subSet
            PluginData.modsLastFile = modFiles
        }// while
    }

    // endregion

    // region -- 状态

    /**
     * 是否闲置
     */
    var isIdle = true
        private set

    /**
     * 取消闲置
     */
    fun start() {
        isIdle = false
    }

    /**
     * 进入闲置
     */
    fun idle() {
        isIdle = true
    }

    /**
     * 初始化，并开始订阅循环
     *
     * @param scope 指定协程上下文
     */
    suspend fun load(scope: CoroutineScope) {
        logger.info("loading plugin data...")
        val subs = HashMap(PluginData.subscriptionSet)
        val files = HashMap(PluginData.modsLastFile)
        checkUpdate(true)
            .buffer()
            .collect { (mod, file) ->
                if (file < 0) {
                    files -= mod
                    subs -= mod
                } else {
                    files[mod] = file
                }
            }
        PluginData.subscriptionSet = subs
        PluginData.modsLastFile = files
        scope.loop()
    }
    // endregion

    companion object {

        /**
         * 标识个人订阅
         */
        const val GROUP_ID_SINGLE: Long = 0

        /**
         * 缓存模组信息
         */
        private val MOD_INFO_CACHE: MutableMap<Int, Mod> = mutableMapOf()
    }
}
