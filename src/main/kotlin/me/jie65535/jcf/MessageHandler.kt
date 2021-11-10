package me.jie65535.jcf

import me.jie65535.jcf.model.addon.Addon
import me.jie65535.jcf.model.addon.file.AddonFile
import me.jie65535.jcf.util.HttpUtil
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.text.DecimalFormat
import kotlin.math.min

object MessageHandler {

    fun parseSearchResult(addons: List<Addon>, builder: ForwardMessageBuilder, contact: Contact) {
        for ((index, addon) in addons.withIndex()) {
            builder.add(contact.bot.id, index.toString(),
                    PlainText("""
                        $index | [${addon.name}] by ${addon.authors[0].name}
                        ${formatCount(addon.downloadCount)} Downloads  Updated ${addon.dateModified.toLocalDate()}
                        ${addon.summary}
                        ${addon.websiteUrl}
                    """.trimIndent()))
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun parseAddon(addon: Addon, contact: Contact): ForwardMessageBuilder {
        val builder = ForwardMessageBuilder(contact)
            .add(contact.bot, loadImage(addon.attachments.find{ it.isDefault }!!.thumbnailUrl, contact))
            .add(contact.bot, PlainText("""
                ${addon.name}
                作者：${addon.authors[0].name}
                概括：${addon.summary}
                项目ID：${addon.id}
                创建时间：${addon.dateCreated.toLocalDate()}
                最近更新：${addon.dateModified.toLocalDate()}
                总下载：${addon.downloadCount.toLong()}
                主页：${addon.websiteUrl}
            """.trimIndent()))
        if (addon.latestFiles.isNotEmpty()) {
            builder.add(contact.bot, PlainText("最新文件列表："))
            parseAddonFiles(addon.latestFiles, builder, contact)
        }
//        JCurseforge.logger.info(addon.latestFiles.toString())
//        JCurseforge.logger.info(addon.gameVersionLatestFiles.toString())
        return builder
    }

    fun parseAddonFiles(addonFiles: Collection<AddonFile>, builder: ForwardMessageBuilder, contact: Contact) {
        for ((index, file) in addonFiles.withIndex()) {
            builder.add(contact.bot.id, index.toString(), PlainText("""
                    $index | ${file.displayName} [${file.releaseType}] [${file.fileDate.toLocalDate()}]
                    ${file.downloadUrl}
                """.trimIndent()))
            // 暂时只允许构造21项
            if (index >= 20)
                break
        }
    }

    private const val ONE_GRP_SIZE = 5000
    private const val ONE_MSG_SIZE = 500
    suspend fun sendLargeMessage(contact: Contact, message: String) {
        for (g in message.indices step ONE_GRP_SIZE) {
            val builder = ForwardMessageBuilder(contact)
            for (i in g until g + min(ONE_GRP_SIZE, message.length-g) step ONE_MSG_SIZE) {
                builder.add(contact.bot, PlainText(message.subSequence(i, i+(min(ONE_MSG_SIZE, message.length-i)))))
            }
            contact.sendMessage(builder.build())
        }
    }

    private val singleDecimalFormat = DecimalFormat("0.#")
    private fun formatCount(count: Double): String = when {
        count < 1000000 -> singleDecimalFormat.format(count / 1000) + "K"
        count < 1000000000 -> singleDecimalFormat.format(count / 1000000) + "M"
        else -> count.toString()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun loadImage(url: String, contact: Contact): Image {
        val imgFileName = url.substringAfterLast("/")
        val file = JCurseforge.resolveDataFile("cache/$imgFileName")
        val res = if (file.exists()) {
            file.readBytes().toExternalResource()
        } else {
            HttpUtil.downloadImage(url, file).toExternalResource()
        }
        val image = contact.uploadImage(res)
        res.close()
        return image
    }
}