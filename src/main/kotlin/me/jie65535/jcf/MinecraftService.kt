package me.jie65535.jcf

import io.ktor.client.features.*
import io.ktor.http.*
import kotlinx.coroutines.TimeoutCancellationException
import me.jie65535.jcf.model.addon.Addon
import me.jie65535.jcf.model.addon.AddonSortMethod
import me.jie65535.jcf.model.addon.file.AddonFile
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.nextEventAsync
import net.mamoe.mirai.message.data.ForwardMessageBuilder
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.MiraiExperimentalApi
import java.lang.Exception
import java.util.regex.Pattern

@OptIn(MiraiExperimentalApi::class)
object MinecraftService {
    private val curseClient = CurseClient

    private const val GAME_ID_MINECRAFT = 432
    const val SECTION_ID_MODES = 6
    const val SECTION_ID_RESOURCE_PACKS = 12
    const val SECTION_ID_WORLDS = 17
    const val SECTION_ID_MODE_PACKS = 4471
    const val ALL = -1

    private const val WAIT_REPLY_TIMEOUT_MS = 60000L
    private const val PAGE_SIZE = 10
    private const val NEXT_PAGE_KEYWORD = "n"
    private const val SHOW_FILES_KEYWORD = "files"

    suspend fun search(sender: UserCommandSender, sectionId: Int, filter: String) {
        val addon: Addon
        var pageIndex = 0
        while (true) {
            val searchResult = doSearch(sectionId, filter, pageIndex++, PAGE_SIZE)
            val hasNextPage = searchResult.size == PAGE_SIZE

            if (searchResult.isEmpty()) {
                sender.sendMessage("未搜索到结果，请更换关键字重试。")
                return
            } else if (searchResult.size == 1) {
                addon = searchResult[0]
                break
            }

            val builder = ForwardMessageBuilder(sender.subject)
            builder.add(sender.bot, PlainText("${WAIT_REPLY_TIMEOUT_MS/1000}秒内回复编号查看"))
            MessageHandler.parseSearchResult(searchResult, builder, sender.subject)
            if (hasNextPage) builder.add(sender.bot, PlainText("回复[$NEXT_PAGE_KEYWORD]下一页"))
            sender.sendMessage(builder.build())

            try {
                val nextEvent = sender.nextEventAsync<MessageEvent>(
                    WAIT_REPLY_TIMEOUT_MS,
                    coroutineContext = sender.coroutineContext
                ) { it.sender == sender.user } .await()
                if (hasNextPage && nextEvent.message.contentEquals(NEXT_PAGE_KEYWORD, true))
                    continue
                addon = searchResult[nextEvent.message.content.toInt()]
                break
            } catch (e: TimeoutCancellationException) {
                sender.sendMessage("等待回复超时，请重新查询。")
            } catch (e: NumberFormatException) {
                sender.sendMessage("请正确回复序号，此次查询已取消，请重新查询。")
            } catch (e: IndexOutOfBoundsException) {
                sender.sendMessage("请回复正确的序号，此次查询已取消，请重新查询。")
            } catch (e: Exception) {
                sender.sendMessage("内部发生异常，此次查询已取消，请重新查询。")
                throw e
            }
            return
        }

        // addon handle
        showAddon(sender, addon)
    }

    /**
     * 根据项目ID搜索资源
     */
    suspend fun searchAddonByProjectId(sender: UserCommandSender, projectId: Int) {
        try {
            val addon = curseClient.getAddon(projectId)

            showAddon(sender, addon)
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.NotFound) {
                sender.sendMessage("未搜索到指定项目ID的资源，请使用正确的项目ID")
            } else {
                sender.sendMessage("请求异常，错误代码：" + e.response.status)
                throw e
            }
        } catch (e: Throwable) {
            sender.sendMessage("内部发生异常，此次查询已取消，请重新查询。")
            throw e
        }
    }

    private suspend fun showAddon(sender: UserCommandSender, addon: Addon) {
        val builder = MessageHandler.parseAddon(addon, sender.subject)
        if (addon.latestFiles.isNotEmpty())
            builder.add(sender.bot, PlainText("${WAIT_REPLY_TIMEOUT_MS/1000}秒内回复[${SHOW_FILES_KEYWORD}]查看所有文件，回复文件序号查看更新日志"))
        sender.sendMessage(builder.build())

        try {
            val nextEvent = sender.nextEventAsync<MessageEvent>(
                WAIT_REPLY_TIMEOUT_MS,
                coroutineContext = sender.coroutineContext
            ) { it.sender == sender.user } .await()
            if (nextEvent.message.contentEquals(SHOW_FILES_KEYWORD, true)) {
                showAllFiles(sender, addon)
            } else {
                val file = addon.latestFiles[nextEvent.message.content.toInt()]
                showChangedLog(sender, addon.id, file)
            }
        } catch (e: TimeoutCancellationException) {
            sender.sendMessage("等待回复超时，请重新查询。")
        } catch (e: NumberFormatException) {
            sender.sendMessage("请正确回复序号，此次查询已取消，请重新查询。")
        } catch (e: IndexOutOfBoundsException) {
            sender.sendMessage("请回复正确的序号，此次查询已取消，请重新查询。")
        } catch (e: Exception) {
            sender.sendMessage("内部发生异常，此次查询已取消，请重新查询。")
            throw e
        }
    }

    private suspend fun showAllFiles(sender: UserCommandSender, addon: Addon) {
        val files = CurseClient.getAddonFiles(addon.id).sortedByDescending { f -> f.fileDate }
        if (files.isEmpty()) {
            sender.sendMessage("没有任何文件 :(")
            return
        }
        val builder = ForwardMessageBuilder(sender.subject)
        builder.add(sender.bot, PlainText("${WAIT_REPLY_TIMEOUT_MS/1000}秒内回复文件序号查看更新日志"))
        MessageHandler.parseAddonFiles(files, builder, sender.subject)
        sender.sendMessage(builder.build())

        try { // cv大法好，回复功能有功夫再封装，先复制粘贴用着 XD
            val nextEvent = sender.nextEventAsync<MessageEvent>(
                WAIT_REPLY_TIMEOUT_MS,
                coroutineContext = sender.coroutineContext
            ) { it.sender == sender.user } .await()
            val file = files[nextEvent.message.content.toInt()]
            showChangedLog(sender, addon.id, file)
        } catch (e: TimeoutCancellationException) {
            sender.sendMessage("等待回复超时，请重新查询。")
        } catch (e: NumberFormatException) {
            sender.sendMessage("请正确回复序号，此次查询已取消，请重新查询。")
        } catch (e: IndexOutOfBoundsException) {
            sender.sendMessage("请回复正确的序号，此次查询已取消，请重新查询。")
        }
    }

    private val HTMLPattern = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE)
    private suspend fun showChangedLog(sender: UserCommandSender, addonId: Int, addonFile: AddonFile) {
        val changeLogHTML = curseClient.getAddonFileChangeLog(addonId, addonFile.id)
        val changeLog = HTMLPattern.matcher(changeLogHTML).replaceAll("")
        MessageHandler.sendLargeMessage(sender.subject, changeLog)
    }

    private suspend fun doSearch(sectionId: Int, filter: String, pageIndex: Int, pageSize: Int): List<Addon> {
        return curseClient.searchAddons(
            gameId = GAME_ID_MINECRAFT,
            sectionId = sectionId,
            categoryId = -1,
            sort = AddonSortMethod.POPULARITY,
            sortDescending = true,
            gameVersion = null,
            index = pageIndex * pageSize,
            pageSize = pageSize,
            searchFilter = filter,
        )
    }

}