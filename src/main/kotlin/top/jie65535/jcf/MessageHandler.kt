package top.jie65535.jcf

import net.mamoe.mirai.message.data.Message
import top.jie65535.jcf.model.file.File
import top.jie65535.jcf.model.mod.Mod
import top.jie65535.jcf.util.PagedList

object MessageHandler {

    /**
     * 处理mod搜索结果
     */
    suspend fun handleModsSearchResult(pagedList: PagedList<Mod>): Message {
        val list = pagedList.current() // mod list
        val hasNext = pagedList.hasNext // 是否有下一页
        val hasPrev = pagedList.hasPrev // 是否有上一页
        TODO("将搜索列表转为QQ消息")
    }

    /**
     * 处理展示单个mod
     */
    fun handleShowMod(mod: Mod): Message {
        TODO("将mod转为QQ消息")
    }

    /**
     * 处理模组文件列表
     */
    suspend fun handleModFileList(pagedList: PagedList<File>): Message {
        val list = pagedList.current() // mod list
        val hasNext = pagedList.hasNext // 是否有下一页
        val hasPrev = pagedList.hasPrev // 是否有上一页
        TODO("将文件列表转为QQ消息")
    }

    /**
     * 处理模组文件更改日志
     * @param changelog 更改日志（HTML）
     */
    fun handleModFileChangelog(changelog: String): Message {
        TODO("将文件更改日志渲染为QQ消息")
    }
}