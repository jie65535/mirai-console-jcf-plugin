package top.jie65535.jcf

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand

object PluginCommands : CompositeCommand(PluginMain, "jcf") {
    @SubCommand
    @Description("设置Curseforge API Key")
    suspend fun CommandSender.setApiKey(apiKey: String) {
        PluginConfig.apiKey = apiKey
        sendMessage("OK! 重启插件生效")
    }

    @SubCommand
    @Description("查看插件帮助")
    suspend fun CommandSender.help() {
        val msg = StringBuilder()
        for ((modClass, cmd) in PluginConfig.searchCommands) {
            msg.appendLine("搜索${modClass.className}: $cmd")
        }
        sendMessage(msg.toString())
    }
}