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
        msg.appendLine("=== CurseForge 命令 ===")
        for ((modClass, cmd) in PluginConfig.searchCommands) {
            msg.appendLine("搜索${modClass.className}: $cmd")
        }
        msg.appendLine("=== Modrinth 命令 ===")
        for ((projectType, cmd) in PluginConfig.mrSearchCommands) {
            msg.appendLine("搜索${projectType.typeName}: $cmd")
        }
        sendMessage(msg.toString())
    }

    @SubCommand
    @Description("设置订阅信息推送bot（qq id）")
    suspend fun CommandSender.setSubsSender(sender: Long) {
        PluginConfig.subscribeSender = sender
        sendMessage("OK! ")
    }

    @SubCommand
    @Description("设置检查间隔（单位：秒）")
    suspend fun CommandSender.setCheckInterval(second: Long) {
        PluginConfig.checkInterval = second
        sendMessage("OK! 将在下次检查结束后应用")
    }

    @SubCommand
    @Description("查看 CurseForge 订阅处理的状态")
    suspend fun CommandSender.subStat() {
        if (!PluginMain.isCurseForgeEnabled) {
            sendMessage("CurseForge 未配置 API Key，订阅功能不可用")
            return
        }
        val subs = PluginMain.subscribeHandler
        if (subs.isIdle) {
            sendMessage("CurseForge 订阅器闲置中")
        } else {
            sendMessage("CurseForge 订阅处理正常运行中")
        }
    }

    @SubCommand
    @Description("使 CurseForge 订阅器闲置")
    suspend fun CommandSender.idleSubs() {
        if (!PluginMain.isCurseForgeEnabled) {
            sendMessage("CurseForge 未配置 API Key，订阅功能不可用")
            return
        }
        PluginMain.subscribeHandler.idle()
        sendMessage("OK，已闲置")
    }

    @SubCommand
    @Description("使 CurseForge 订阅器恢复运行")
    suspend fun CommandSender.runSubs() {
        if (!PluginMain.isCurseForgeEnabled) {
            sendMessage("CurseForge 未配置 API Key，订阅功能不可用")
            return
        }
        PluginMain.subscribeHandler.start()
        sendMessage("OK，已恢复 CurseForge 订阅处理")
    }

    @SubCommand
    @Description("查看 Modrinth 订阅处理的状态")
    suspend fun CommandSender.mrSubStat() {
        val subs = PluginMain.modrinthSubscribeHandler
        if (subs.isIdle) {
            sendMessage("Modrinth 订阅器闲置中")
        } else {
            sendMessage("Modrinth 订阅处理正常运行中")
        }
    }

    @SubCommand
    @Description("使 Modrinth 订阅器闲置")
    suspend fun CommandSender.mrIdleSubs() {
        PluginMain.modrinthSubscribeHandler.idle()
        sendMessage("OK，Modrinth 订阅器已闲置")
    }

    @SubCommand
    @Description("使 Modrinth 订阅器恢复运行")
    suspend fun CommandSender.mrRunSubs() {
        PluginMain.modrinthSubscribeHandler.start()
        sendMessage("OK，已恢复 Modrinth 订阅处理")
    }
}
