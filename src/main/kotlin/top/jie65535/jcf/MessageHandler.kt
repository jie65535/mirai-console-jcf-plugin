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
import top.jie65535.jcf.model.file.File
import top.jie65535.jcf.model.mod.Mod
import top.jie65535.jcf.util.PagedList
import top.jie65535.jcf.util.HttpUtil
import java.text.DecimalFormat
import java.util.regex.Pattern
import kotlin.math.min

class MessageHandler(
    private val service: MinecraftService,
    private val eventChannel: EventChannel<Event>,
    private val logger: MiraiLogger
) {

    fun startListen() {
        eventChannel.subscribeMessages {
            for ((modClass, command) in PluginConfig.searchCommands) {
                if (command.isBlank()) continue
                startsWith(command) {
                    val filter = it.trim()
                    if (filter.isEmpty()) {
                        subject.sendMessage(message.quote() + "必须输入关键字")
                    } else {
                        try {
                            logger.info("${sender.nameCardOrNick}(${sender.id}) $modClass \"$filter\"")
                            val pagedList = service.search(modClass, filter)
                            with(pagedList.current()) {
                                if (isEmpty()) {
                                    subject.sendMessage("未搜索到关键字\"$filter\"相关结果")
                                } else if (size == 1) {
                                    handleShowMod(get(0))
                                } else {
                                    handleModsSearchResult(pagedList)
                                }
                            }
                        } catch (e: Throwable) {
                            subject.sendMessage(message.quote() + "发生内部错误，请稍后重试")
                            logger.error("消息\"$message\"引发异常", e)
                        }
                    }// if
                }
            }// for
        }
    }

    /**
     * 处理分页列表选择功能，返回用户选中项，返回null表示未选中任何项
     * @param pagedList 分页的列表
     * @param format 格式化方法
     * @return 用户选中项，null表示未选择任何项
     */
    private suspend fun <T> MessageEvent.handlePagedList(pagedList: PagedList<T>, format: suspend (T)->Message): T? {
        do {
            var isContinue = false
            val list = pagedList.current()
            val listMessage = subject.sendMessage(buildForwardMessage {
                for ((i, it) in list.withIndex()) {
                    bot.id named i.toString() says format(it)
                }
                var msg = "$WAIT_REPLY_TIMEOUT_S 秒内回复编号查看"
                if (pagedList.hasPrev)
                    msg += "\n回复 $PAGE_UP_KEYWORD 上一页"
                if (pagedList.hasNext)
                    msg += "\n回复 $PAGE_DOWN_KEYWORD 下一页"
                bot says msg
            })
            try {
                // 获取用户回复
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
     * 处理mod搜索结果
     */
    private suspend fun MessageEvent.handleModsSearchResult(pagedList: PagedList<Mod>) {
        val selectedMod = handlePagedList(pagedList) { mod ->
            val text = PlainText(
                """
                    [${mod.name}] by ${mod.authors.firstOrNull()?.name}
                    ${formatCount(mod.downloadCount)} Downloads  Updated ${mod.dateModified.toLocalDate()}
                    ${mod.summary}
                    ${mod.links.websiteUrl}
                """.trimIndent())
            mod.logo?.thumbnailUrl?.let { loadImage(it) + text } ?: text
        }
        selectedMod?.let { handleShowMod(it) }
    }

    /**
     * 处理展示单个mod
     */
    private suspend fun MessageEvent.handleShowMod(mod: Mod) {
        subject.sendMessage(
            buildForwardMessage {
                // logo
                mod.logo?.thumbnailUrl?.let {
                    if (it.isNotBlank()) bot says loadImage(it)
                }
                // basic info
                bot says PlainText(with(mod) {
                    """
                        $name
                        作者：${authors.joinToString { it.name }}
                        概括：$summary
                        项目ID：$id
                        创建时间：${dateCreated.toLocalDate()}
                        最近更新：${dateModified.toLocalDate()}
                        总下载：$downloadCount
                        主页：${mod.links.websiteUrl}
                    """.trimIndent()
                })
                var msg = "$WAIT_REPLY_TIMEOUT_S 秒内回复 $SUBSCRIBE_KEYWORD 订阅模组更新"
                if (mod.latestFiles.isNotEmpty()) {
                    msg += "\n回复编号查看文件详细信息\n" +
                            "回复 $VIEW_FILES_KEYWORD 查看全部历史文件"
                }
                bot says msg
                for ((i, file) in mod.latestFiles.withIndex()) {
                    bot.id named i.toString() says PlainText(
                        """
                            ${file.displayName} [${file.releaseType}] [${file.fileDate.toLocalDate()}]
                            ${file.downloadUrl}
                        """.trimIndent()
                    )
                }
            }
        )
        try {
            // 获取用户回复
            val next = withTimeout(WAIT_REPLY_TIMEOUT_S * 1000L) {
                eventChannel.nextEvent<MessageEvent>(EventPriority.MONITOR) { it.sender == sender }
            }
            val nextMessage = next.message.content
            val subsHandler = PluginMain.subscribeHandler
            if (nextMessage.equals(SUBSCRIBE_KEYWORD, true)) {
                if (next is GroupMessageEvent) {
                    subsHandler.sub(mod.id, next.sender.id, next.group.id)
                } else {
                    subsHandler.sub(mod.id, next.sender.id)
                }
                subject.sendMessage(QuoteReply(next.source) + "已添加订阅")
            } else if (mod.latestFiles.isNotEmpty()) {
                if (nextMessage.equals(VIEW_FILES_KEYWORD, true)) {
                    // 查看所有文件
                    handleModFileList(service.getModFiles(mod.id))
                } else {
                    // 查看文件详情
                    handleModFile(mod.latestFiles[nextMessage.toInt()])
                }
            }
        } catch (e: Throwable) {
            // 忽略因回复引发的异常，无论是超时、越界还是格式不正确，不提示错误
        }
    }

    /**
     * 处理模组文件列表
     */
    private suspend fun MessageEvent.handleModFileList(pagedList: PagedList<File>) {
        val selectedFile = handlePagedList(pagedList) { file ->
            PlainText(
                """
                    ${file.displayName} [${file.releaseType}] [${file.fileDate.toLocalDate()}]
                    ${file.downloadUrl}
                """.trimIndent()
            )
        }
        selectedFile?.let { handleModFile(it) }
    }

    /**
     * 处理模组文件具体信息展示
     */
    private suspend fun MessageEvent.handleModFile(file: File) {
        try {
            // 暂时仅展示文件更改日志，可以添加文件依赖相关信息的显示
            subject.sendMessage(handleModFileChangelog(service.getModFileChangelog(file.modId, file.id)))
        } catch (e: Throwable) {
            logger.warning("获取文件[${file.fileName}]更改日志时异常", e)
            subject.sendMessage("获取文件更改日志时异常，请稍后重试")
        }
    }

    /**
     * 处理模组文件更改日志
     * @param changelog 更改日志（HTML）
     */
    private fun MessageEvent.handleModFileChangelog(changelog: String): Message {
        val logs = HTMLPattern.matcher(changelog).replaceAll("")
        return sendLargeMessage(logs)
    }

    companion object {
        private const val WAIT_REPLY_TIMEOUT_S = 60
        private const val PAGE_UP_KEYWORD    = "P"
        private const val PAGE_DOWN_KEYWORD  = "N"
        private const val VIEW_FILES_KEYWORD = "ALL"
        private const val SUBSCRIBE_KEYWORD  = "订阅"
        private const val ONE_GRP_SIZE = 5000
        private const val ONE_MSG_SIZE = 500
        private val singleDecimalFormat = DecimalFormat("0.#")

        private fun formatCount(count: Long): String = when {
            count < 1000000 -> singleDecimalFormat.format(count / 1000) + "K"
            count < 1000000000 -> singleDecimalFormat.format(count / 1000000) + "M"
            else -> count.toString()
        }

        private suspend fun MessageEvent.loadImage(url: String): Image {
            val imgFileName = url.substringAfterLast("/")
            val file = PluginMain.resolveDataFile("cache/$imgFileName")
            val res = if (file.exists()) {
                file.readBytes().toExternalResource()
            } else {
                HttpUtil.downloadImage(url, file).toExternalResource()
            }
            val image = subject.uploadImage(res)
            withContext(Dispatchers.IO) {
                res.close()
            }
            return image
        }

        val HTMLPattern: Pattern = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE)
        fun MessageEvent.sendLargeMessage(message: String): Message {
            return buildForwardMessage {
                for (g in message.indices step ONE_GRP_SIZE) {
                    for (i in g until g + min(ONE_GRP_SIZE, message.length - g) step ONE_MSG_SIZE) {
                        bot says PlainText(message.subSequence(i, i + (min(ONE_MSG_SIZE, message.length - i))))
                    }
                }
            }
        }// fun
    }
}
