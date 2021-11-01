package me.jie65535.jcf

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info

object JCurseforge : KotlinPlugin(
    JvmPluginDescription(
        id = "me.jie65535.jcf",
        name = "J Curseforge Util",
        version = "0.1.1",
    ) {
        author("jie65535")
        info("""
            MC Curseforge Util
            https://github.com/jie65535/mirai-console-jcf-plugin
        """.trimIndent())
    }
) {
    override fun onEnable() {
        logger.info { "Plugin loaded" }
        JcfCommand.register()
    }

    override fun onDisable() {
        JcfCommand.unregister()
    }
}