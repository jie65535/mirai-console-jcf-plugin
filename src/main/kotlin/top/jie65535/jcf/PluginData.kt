package top.jie65535.jcf

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.data.ValueDescription

object PluginData : AutoSavePluginData("JCurseforgeData") {

    /**
     * 模组最新文件的id集合
     * ```json
     * {
     *    mod_id: file_id,
     *    ...
     * }
     * ```
     */
    @ValueDescription("模组最新文件的id集合")
    var modsLastFile: MutableMap<Int, Int> by value()

    /**
     * 订阅记录
     * ```json
     * {
     *    mod_id: {
     *       group_id: [ qq_id, ... ],
     *       ...
     *    },
     *    ...
     * }
     * ```
     * 个人订阅时，group为0
     */
    @ValueDescription("订阅记录")
    var subscriptionSet: MutableMap<Int, MutableMap<Long, MutableList<Long>>> by value()
}
