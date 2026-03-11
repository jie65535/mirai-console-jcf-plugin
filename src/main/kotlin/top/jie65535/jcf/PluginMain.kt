package top.jie65535.jcf

import kotlinx.coroutines.launch
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.utils.info

object PluginMain: KotlinPlugin(
    JvmPluginDescription(
        id = "top.jie65535.jcf",
        name = "J Curseforge Util",
        version = "1.2.0",
    ) {
        author("jie65535")
        info("MC Curseforge & Modrinth Util\n" +
                "https://github.com/jie65535/mirai-console-jcf-plugin")
    }
) {
    /**
     * CurseForge 订阅处理类
     */
    lateinit var subscribeHandler: SubscribeHandler private set

    /**
     * Modrinth 订阅处理类
     */
    lateinit var modrinthSubscribeHandler: ModrinthSubscribeHandler private set

    /**
     * CurseForge is enabled only when an API key is configured.
     */
    var isCurseForgeEnabled = false
        private set

    override fun onEnable() {
        logger.info { "Plugin loaded" }
        PluginData.reload()
        PluginConfig.reload()
        PluginCommands.register()

        val eventChannel = GlobalEventChannel.parentScope(this)

        // Initialize Modrinth (no API key required)
        val modrinthService = ModrinthService()
        modrinthSubscribeHandler = ModrinthSubscribeHandler(modrinthService, logger)
        val modrinthMessageHandler = ModrinthMessageHandler(modrinthService, modrinthSubscribeHandler, eventChannel, logger)
        modrinthMessageHandler.startListen()
        launch {
            modrinthSubscribeHandler.load(this)
        }
        modrinthSubscribeHandler.start()

        // Initialize CurseForge (requires API key)
        if (PluginConfig.apiKey.isBlank()) {
            logger.error("未配置 Curseforge Api Key，CurseForge 相关功能不可用！\n" +
                    "请使用 /jcf setApiKey <apiKey> 命令来设置key\n" +
                    "Api key 可以在开发者控制台生成：https://console.curseforge.com/")
        } else {
            val service = MinecraftService(PluginConfig.apiKey)
            val messageHandler = MessageHandler(service, eventChannel, logger)
            subscribeHandler = SubscribeHandler(service, logger)
            messageHandler.startListen()
            launch {
                subscribeHandler.load(this)
            }
            subscribeHandler.start()
            isCurseForgeEnabled = true
        }

        logger.info { "Plugin Enabled" }
    }
}
