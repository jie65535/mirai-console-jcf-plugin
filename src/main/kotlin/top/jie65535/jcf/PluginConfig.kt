package top.jie65535.jcf

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object PluginConfig : AutoSavePluginConfig("JCurseforgeConfig") {
    @ValueDescription("Curseforge API KEY")
    var apiKey: String by value()

    @ValueDescription("搜索命令 (MODS,MODPACKS,RESOURCE_PACKS,WORLDS,BUKKIT_PLUGINS,ADDONS,CUSTOMIZATION)")
    val searchCommands: MutableMap<MinecraftService.ModClass, String> by value(
        mutableMapOf(
            MinecraftService.ModClass.MODS          to "cfmod ",
            MinecraftService.ModClass.MODPACKS      to "cfpack ",
            MinecraftService.ModClass.RESOURCE_PACKS to "cfres ",
            MinecraftService.ModClass.WORLDS        to "cfworld ",
            MinecraftService.ModClass.BUKKIT_PLUGINS to "cfbukkit ",
            MinecraftService.ModClass.ADDONS        to "cfaddon ",
            MinecraftService.ModClass.CUSTOMIZATION to "cfcustom ",
        )
    )

    @ValueDescription("Modrinth 搜索命令 (MODS,MODPACKS,RESOURCE_PACKS,SHADERS,PLUGINS,DATA_PACKS)")
    val mrSearchCommands: MutableMap<ModrinthService.ProjectType, String> by value(
        mutableMapOf(
            ModrinthService.ProjectType.MODS          to "mrmod ",
            ModrinthService.ProjectType.MODPACKS      to "mrpack ",
            ModrinthService.ProjectType.RESOURCE_PACKS to "mrres ",
            ModrinthService.ProjectType.SHADERS       to "mrshader ",
            ModrinthService.ProjectType.PLUGINS       to "mrplugin ",
            ModrinthService.ProjectType.DATA_PACKS    to "mrdata ",
        )
    )

    /**
     * 订阅信息推送bot
     */
    @ValueDescription("订阅信息推送bot（qq id）")
    var subscribeSender: Long by value(-1L)

    /**
     * 检查间隔
     */
    @ValueDescription("检查间隔（单位：秒）")
    var checkInterval: Long by value(60 * 60 * 4L)
}
