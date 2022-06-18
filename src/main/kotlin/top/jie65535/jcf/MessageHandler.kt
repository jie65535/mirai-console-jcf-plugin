package top.jie65535.jcf

import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.EventChannel
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.MiraiLogger
import top.jie65535.jcf.model.file.File
import top.jie65535.jcf.model.mod.Mod
import top.jie65535.jcf.util.PagedList

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
                            handleModsSearchResult(pagedList)
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
     * 处理mod搜索结果
     */
    private suspend fun MessageEvent.handleModsSearchResult(pagedList: PagedList<Mod>): Message {
        val list = pagedList.current() // mod list
        val hasNext = pagedList.hasNext // 是否有下一页
        val hasPrev = pagedList.hasPrev // 是否有上一页
        TODO("将搜索列表转为QQ消息")
    }

    /**
     * 处理展示单个mod
     */
    private fun handleShowMod(mod: Mod): Message {
        TODO("将mod转为QQ消息")
    }

    /**
     * 处理模组文件列表
     */
    private suspend fun handleModFileList(pagedList: PagedList<File>): Message {
        val list = pagedList.current() // mod list
        val hasNext = pagedList.hasNext // 是否有下一页
        val hasPrev = pagedList.hasPrev // 是否有上一页
        TODO("将文件列表转为QQ消息")
    }

    /**
     * 处理模组文件更改日志
     * @param changelog 更改日志（HTML）
     */
    private fun handleModFileChangelog(changelog: String): Message {
        TODO("将文件更改日志渲染为QQ消息")
    }
}