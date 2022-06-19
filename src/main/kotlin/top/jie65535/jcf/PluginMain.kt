package top.jie65535.jcf

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.utils.info

object PluginMain: KotlinPlugin(
    JvmPluginDescription(
        id = "top.jie65535.jcf",
        name = "J Curseforge Util",
        version = "1.0.0",
    ) {
        author("jie65535")
        info("MC Curseforge Util\n" +
                "https://github.com/jie65535/mirai-console-jcf-plugin")
    }
) {
    override fun onEnable() {
        logger.info { "Plugin loaded" }
        PluginConfig.reload()
        PluginCommands.register()

        if (PluginConfig.apiKey.isBlank()) {
            logger.error("必须配置 Curseforge Api Key 才可以使用本插件！\n" +
                    "请使用 /jcf setApiKey <apiKey> 命令来设置key\n" +
                    "Api key 可以在开发者控制台生成：https://console.curseforge.com/")
            return
        }
        val service = MinecraftService(PluginConfig.apiKey)
        val eventChannel = GlobalEventChannel.parentScope(this)
        val messageHandler = MessageHandler(service, eventChannel, logger)
        messageHandler.startListen()
        logger.info { "Plugin Enabled" }
    }
}