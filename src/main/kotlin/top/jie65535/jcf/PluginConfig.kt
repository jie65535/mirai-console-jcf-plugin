package top.jie65535.jcf

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object PluginConfig : AutoSavePluginConfig("jcf") {
    @ValueDescription("Curseforge API KEY")
    val apiKey: String by value()

    @ValueDescription("搜索命令 (MODS,MODPACKS,RESOURCE_PACKS,WORLDS,BUKKIT_PLUGINS,ADDONS,CUSTOMIZATION)")
    val searchCommands: MutableMap<MinecraftService.ModClass, String> by value(
        mutableMapOf(
            MinecraftService.ModClass.MODS          to "jcfmod ",
            MinecraftService.ModClass.MODPACKS      to "jcfpack ",
            MinecraftService.ModClass.RESOURCE_PACKS to "jcfres ",
            MinecraftService.ModClass.WORLDS        to "jcfword ",
            MinecraftService.ModClass.BUKKIT_PLUGINS to "jcfbukkit ",
            MinecraftService.ModClass.ADDONS        to "jcfaddon ",
            MinecraftService.ModClass.CUSTOMIZATION to "jcfcustom ",
        )
    )
}
