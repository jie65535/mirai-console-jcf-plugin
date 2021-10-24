package me.jie65535.jcf

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.console.plugin.author
import net.mamoe.mirai.console.plugin.info
import net.mamoe.mirai.console.plugin.name
import net.mamoe.mirai.console.plugin.version

object JcfCommand : CompositeCommand(
    JCurseforge, "jcf",
    description = "Curseforge Util"
) {
    @SubCommand
    @Description("帮助")
    suspend fun CommandSender.help() {
        sendMessage("${JCurseforge.name} by ${JCurseforge.author} ${JCurseforge.version}\n" +
                "${JCurseforge.info}\n" + usage)
    }

    @SubCommand("ss")
    @Description("直接搜索")
    suspend fun UserCommandSender.search(filter: String) {
        MinecraftService.search(this, MinecraftService.ALL, filter)
    }

    @SubCommand("ssmod")
    @Description("搜索模组")
    suspend fun UserCommandSender.searchMods(filter: String) {
        MinecraftService.search(this, MinecraftService.SECTION_ID_MODES, filter)
    }

    @SubCommand("sspack")
    @Description("搜索整合包")
    suspend fun UserCommandSender.searchModPacks(filter: String) {
        MinecraftService.search(this, MinecraftService.SECTION_ID_MODE_PACKS, filter)
    }

    @SubCommand("ssres")
    @Description("搜索资源包")
    suspend fun UserCommandSender.searchResourcePacks(filter: String) {
        MinecraftService.search(this, MinecraftService.SECTION_ID_RESOURCE_PACKS, filter)
    }

    // 可能用不上，先注释掉，有需要再说
//    @SubCommand("ssworld")
//    @Description("搜索存档")
//    suspend fun UserCommandSender.searchWorlds(filter: String) {
//        MinecraftService.search(this, MinecraftService.SECTION_ID_WORLDS, filter)
//    }

}