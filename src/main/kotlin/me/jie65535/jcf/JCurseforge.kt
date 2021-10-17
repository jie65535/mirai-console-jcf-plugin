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
        version = "0.1.0",
    ) {
        author("jie65535")
        info("""Curseforge Util""")
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