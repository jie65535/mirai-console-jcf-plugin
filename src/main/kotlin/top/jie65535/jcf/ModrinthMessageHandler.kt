package top.jie65535.jcf

import kotlinx.coroutines.*
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.MiraiLogger
import top.jie65535.jcf.MessageHandler.Companion.HTMLPattern
import top.jie65535.jcf.MessageHandler.Companion.sendLargeMessage
import top.jie65535.jcf.model.modrinth.SearchHit
import top.jie65535.jcf.model.modrinth.Version
import top.jie65535.jcf.util.PagedList
import top.jie65535.jcf.util.HttpUtil
import java.text.DecimalFormat

/**
 * Handles QQ message events for Modrinth searches and project browsing.
 */
class ModrinthMessageHandler(
    private val service: ModrinthService,
    private val subsHandler: ModrinthSubscribeHandler,
    private val eventChannel: EventChannel<Event>,
    private val logger: MiraiLogger,
) {

    fun startListen() {
        eventChannel.subscribeMessages {
            for ((projectType, command) in PluginConfig.mrSearchCommands) {
                if (command.isBlank()) continue
                startsWith(command) {
                    val filter = it.trim()
                    if (filter.isEmpty()) {
                        subject.sendMessage(message.quote() + "必须输入关键字")
                    } else {
                        try {
                            logger.info("${sender.nameCardOrNick}(${sender.id}) Modrinth $projectType \"$filter\"")
                            val pagedList = service.search(projectType, filter)
                            with(pagedList.current()) {
                                if (isEmpty()) {
                                    subject.sendMessage("未搜索到相关结果")
                                } else if (size == 1) {
                                    handleShowProject(get(0))
                                } else {
                                    handleSearchResult(pagedList)
                                }
                            }
                        } catch (e: Throwable) {
                            subject.sendMessage(message.quote() + "发生内部错误，请稍后重试")
                            logger.error("消息\"$message\"引发异常", e)
                        }
                    }
                }
            }
        }
    }

    /**
     * Generic paged-list interaction: show items, wait for user selection, return selected item.
     */
    private suspend fun <T> MessageEvent.handlePagedList(
        pagedList: PagedList<T>,
        format: suspend (T) -> Message
    ): T? {
        do {
            var isContinue = false
            val list = pagedList.current()
            val listMessage = subject.sendMessage(buildForwardMessage {
                for ((i, it) in list.withIndex()) {
                    bot.id named i.toString() says format(it)
                }
                var msg = "$WAIT_REPLY_TIMEOUT_S 秒内回复编号查看"
                if (pagedList.hasPrev) msg += "\n回复 $PAGE_UP_KEYWORD 上一页"
                if (pagedList.hasNext) msg += "\n回复 $PAGE_DOWN_KEYWORD 下一页"
                bot says msg
            })
            try {
                val next = withTimeout(WAIT_REPLY_TIMEOUT_S * 1000L) {
                    eventChannel.nextEvent<MessageEvent>(EventPriority.MONITOR) { it.sender == sender }
                }
                val nextMessage = next.message.content
                if (nextMessage.equals(PAGE_DOWN_KEYWORD, true)) {
                    pagedList.next()
                } else if (nextMessage.equals(PAGE_UP_KEYWORD, true)) {
                    pagedList.prev()
                } else {
                    return list[nextMessage.toInt()]
                }
                isContinue = true
            } catch (e: TimeoutCancellationException) {
                subject.sendMessage("等待回复超时，请重新查询。")
            } catch (e: NumberFormatException) {
                subject.sendMessage("请回复正确的选项，此次查询已取消，请重新查询。")
            } catch (e: IndexOutOfBoundsException) {
                subject.sendMessage("请回复正确的序号，此次查询已取消，请重新查询。")
            } catch (e: Throwable) {
                subject.sendMessage(message.quote() + "发生内部错误，请稍后重试")
                logger.warning("回复消息\"$message\"引发意外的异常", e)
            } finally {
                listMessage.recall()
            }
        } while (isContinue)
        return null
    }

    /**
     * Show a paged list of search hits and handle selection.
     */
    private suspend fun MessageEvent.handleSearchResult(pagedList: PagedList<SearchHit>) {
        val selected = handlePagedList(pagedList) { hit ->
            val text = PlainText(
                """
                    [${hit.title}] by ${hit.author}
                    ${formatCount(hit.downloads)} Downloads  Updated ${hit.dateModified.take(10)}
                    ${hit.description}
                    https://modrinth.com/${hit.projectType}/${hit.slug}
                """.trimIndent()
            )
            hit.iconUrl?.let { loadImage(it) + text } ?: text
        }
        selected?.let { handleShowProject(it) }
    }

    /**
     * Show detailed information for a project (from a search hit).
     */
    private suspend fun MessageEvent.handleShowProject(hit: SearchHit) {
        // Fetch full project details
        val project = try {
            service.getProject(hit.projectId)
        } catch (e: Throwable) {
            logger.warning("获取 Modrinth 项目[${hit.projectId}]详情时异常", e)
            subject.sendMessage("获取项目详情时异常，请稍后重试")
            return
        }

        // Fetch latest versions (up to DEFAULT_SHOW_VERSIONS)
        val versions = try {
            service.getProjectVersions(project.id).current()
        } catch (e: Throwable) {
            logger.warning("获取 Modrinth 项目[${project.id}]版本列表时异常", e)
            emptyArray()
        }

        subject.sendMessage(buildForwardMessage {
            // Icon
            project.iconUrl?.let {
                if (it.isNotBlank()) bot says loadImage(it)
            }
            // Basic info
            bot says PlainText(with(project) {
                """
                    $title
                    作者：${hit.author}
                    概括：$description
                    项目ID：$id
                    发布时间：${published.take(10)}
                    最近更新：${updated.take(10)}
                    总下载：$downloads
                    主页：https://modrinth.com/$projectType/$slug
                """.trimIndent()
            })

            var msg = "$WAIT_REPLY_TIMEOUT_S 秒内回复 $SUBSCRIBE_KEYWORD 订阅项目更新"
            if (versions.isNotEmpty()) {
                msg += "\n回复编号查看版本详细信息\n" +
                        "回复 $VIEW_VERSIONS_KEYWORD 查看全部历史版本"
            }
            bot says msg

            for ((i, ver) in versions.withIndex()) {
                bot.id named i.toString() says PlainText(
                    """
                        ${ver.name} [${ver.versionType}] [${ver.datePublished.take(10)}]
                        支持版本：${ver.gameVersions.joinToString()}
                        加载器：${ver.loaders.joinToString()}
                        ${ver.files.firstOrNull { it.primary }?.url ?: ver.files.firstOrNull()?.url ?: ""}
                    """.trimIndent()
                )
            }
        })

        try {
            val next = withTimeout(WAIT_REPLY_TIMEOUT_S * 1000L) {
                eventChannel.nextEvent<MessageEvent>(EventPriority.MONITOR) { it.sender == sender }
            }
            val nextMessage = next.message.content
            if (nextMessage.equals(SUBSCRIBE_KEYWORD, true)) {
                val latestVersionId = project.versions.firstOrNull()
                if (next is GroupMessageEvent) {
                    subsHandler.sub(project.id, latestVersionId, next.sender.id, next.group.id)
                } else {
                    subsHandler.sub(project.id, latestVersionId, next.sender.id)
                }
                subject.sendMessage(QuoteReply(next.source) + "已添加 Modrinth 订阅")
            } else if (versions.isNotEmpty()) {
                if (nextMessage.equals(VIEW_VERSIONS_KEYWORD, true)) {
                    // Show all versions
                    handleVersionList(service.getProjectVersions(project.id))
                } else {
                    // Show selected version's changelog
                    handleVersion(versions[nextMessage.toInt()])
                }
            }
        } catch (_: Throwable) {
            // Silently ignore timeout, bad index, etc.
        }
    }

    /**
     * Show a paged list of versions and handle selection.
     */
    private suspend fun MessageEvent.handleVersionList(pagedList: PagedList<Version>) {
        val selected = handlePagedList(pagedList) { ver ->
            PlainText(
                """
                    ${ver.name} [${ver.versionType}] [${ver.datePublished.take(10)}]
                    支持版本：${ver.gameVersions.joinToString()}
                    加载器：${ver.loaders.joinToString()}
                    ${ver.files.firstOrNull { it.primary }?.url ?: ver.files.firstOrNull()?.url ?: ""}
                """.trimIndent()
            )
        }
        selected?.let { handleVersion(it) }
    }

    /**
     * Display the changelog for a version.
     */
    private suspend fun MessageEvent.handleVersion(version: Version) {
        val changelog = version.changelog?.replace(Regex("\n+"), "\n") ?: "暂无更新日志"
        subject.sendMessage(sendLargeMessage(changelog))
    }

    companion object {
        private const val WAIT_REPLY_TIMEOUT_S = 60
        private const val PAGE_UP_KEYWORD       = "P"
        private const val PAGE_DOWN_KEYWORD     = "N"
        private const val VIEW_VERSIONS_KEYWORD = "ALL"
        private const val SUBSCRIBE_KEYWORD     = "订阅"
        private val singleDecimalFormat = DecimalFormat("0.#")

        private fun formatCount(count: Long): String = when {
            count < 1_000L         -> count.toString()
            count < 1_000_000L     -> singleDecimalFormat.format(count / 1_000.0) + "K"
            count < 1_000_000_000L -> singleDecimalFormat.format(count / 1_000_000.0) + "M"
            else                   -> count.toString()
        }

        private suspend fun MessageEvent.loadImage(url: String): Image {
            val imgFileName = url.substringAfterLast("/").substringBefore("?")
            val file = PluginMain.resolveDataFile("cache/$imgFileName")
            val res = if (file.exists()) {
                file.readBytes().toExternalResource()
            } else {
                HttpUtil.downloadImage(url, file).toExternalResource()
            }
            val image = subject.uploadImage(res)
            withContext(Dispatchers.IO) { res.close() }
            return image
        }
    }
}
