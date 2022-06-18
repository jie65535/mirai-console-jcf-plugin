package top.jie65535.jcf

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.EventChannel
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.subscribeMessages
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
                            val pagedList = service.search(modClass, filter)
                            val msg = with(pagedList.current()) {
                                if (size == 1) {
                                    handleShowMod(get(0))
                                } else {
                                    handleModsSearchResult(pagedList)
                                }
                            }
                            subject.sendMessage(msg)
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
     * 处理mod搜索结果
     */
    private suspend fun MessageEvent.handleModsSearchResult(pagedList: PagedList<Mod>): Message {
        val list = pagedList.current() // mod list
        val hasNext = pagedList.hasNext // 是否有下一页
        val hasPrev = pagedList.hasPrev // 是否有上一页
        return buildForwardMessage {
            bot says "30秒内回复编号查看"
            for ((x, mod) in list.withIndex()) {
                bot.id named x.toString() says PlainText(
                    """
                        [${mod.name}] by ${mod.authors[0].name}
                        ${formatCount(mod.downloadCount)} Downloads  Updated ${mod.dateModified.toLocalDate()}
                        ${mod.summary}
                        ${mod.links.websiteUrl}
                    """.trimIndent()
                )
            }
            var tmp = "回复："
            if (hasPrev) tmp += "[P]上一页 "
            if (hasNext) tmp += "[N]下一页"
            bot says tmp
        }
    }

    /**
     * 处理展示单个mod
     */
    private suspend fun MessageEvent.handleShowMod(mod: Mod): Message {
        return buildForwardMessage {
            // logo
            mod.logo.thumbnailUrl?.let {
                if (it.isNotBlank()) bot says loadImage(it)
            }
            // basic info
            bot says PlainText(with(mod) {
                """
                    $name
                    作者：${authors[0].name}
                    概括：$summary
                    项目ID：$id
                    创建时间：${dateCreated.toLocalDate()}
                    最近更新：${dateModified.toLocalDate()}
                    总下载：$downloadCount
                """.trimIndent()
            })
            // links
            bot says PlainText(with(mod.links) {
                """
                    主页：$websiteUrl
                    支持页：$issuesUrl
                    wiki：$wikiUrl
                    开源页：$sourceUrl
                """.trimIndent()
            })
        }
    }

    /**
     * 处理模组文件列表
     */
    private suspend fun MessageEvent.handleModFileList(pagedList: PagedList<File>): Message {
        val list = pagedList.current() // mod list
        val hasNext = pagedList.hasNext // 是否有下一页
        val hasPrev = pagedList.hasPrev // 是否有上一页
        return buildForwardMessage {
            bot says "30秒内回复编号查看"
            for ((x, file) in list.withIndex()) {
                bot.id named x.toString() says PlainText(
                    """
                        ${file.displayName} [${file.releaseType}] [${file.fileDate.toLocalDate()}]
                        ${file.downloadUrl}
                    """.trimIndent()
                )
            }
            var tmp = "回复："
            if (hasPrev) tmp += "[P]上一页 "
            if (hasNext) tmp += "[N]下一页"
            bot says tmp
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

    private val HTMLPattern = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE)
    private val ONE_GRP_SIZE = 5000
    private val ONE_MSG_SIZE = 500
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
