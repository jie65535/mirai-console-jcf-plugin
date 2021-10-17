package me.jie65535.jcf

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand

object JcfCommand : CompositeCommand(
    JCurseforge, "jcf",
    description = "Curseforge Util"
) {
    private val curse = CurseClient()

    @SubCommand
    @Description("帮助")
    suspend fun CommandSender.help() {
        sendMessage(usage)
    }

}