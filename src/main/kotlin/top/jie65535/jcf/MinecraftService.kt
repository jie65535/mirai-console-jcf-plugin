package top.jie65535.jcf

import top.jie65535.jcf.model.file.File
import top.jie65535.jcf.model.mod.Mod
import top.jie65535.jcf.model.request.ModsSearchSortField
import top.jie65535.jcf.model.request.SortOrder
import top.jie65535.jcf.util.PagedList

class MinecraftService(apiKey: String) {
    companion object {
        private const val GAME_ID_MINECRAFT = 432
        private const val DEFAULT_PAGE_SIZE = 10
        private val DEFAULT_SORT_FIELD = ModsSearchSortField.Popularity
    }

    /**
     * mod分类
     */
    enum class ModClass(val className: String, val classId: Int) {
        /**
         * 存档
         */
        WORLDS("存档",17),

        /**
         * 水桶服插件
         */
        BUKKIT_PLUGINS("水桶服插件", 5),

        /**
         * 自定义
         */
        CUSTOMIZATION("定制", 4546),

        /**
         * 整合包
         */
        MODPACKS("整合包", 4471),

        /**
         * 资源包
         */
        RESOURCE_PACKS("资源包", 12),

        /**
         * 附加
         */
        ADDONS("附加", 4559),

        /**
         * 模组
         */
        MODS("模组", 6);
    }

    /**
     * api客户端实例
     */
    private val api = CurseforgeApi(apiKey)

    /**
     * 根据分类与过滤器进行搜索，返回分页的列表
     * @param modClass mod分类
     * @param filter 过滤器
     * @return 模组分页列表
     */
    fun search(modClass: ModClass, filter: String): PagedList<Mod> =
        PagedList(DEFAULT_PAGE_SIZE) { index ->
            val response = api.searchMods(
                GAME_ID_MINECRAFT,
                modClass.classId,
                searchFilter = filter,
                sortField = DEFAULT_SORT_FIELD,
                sortOrder = SortOrder.DESC,
                index = index,
                pageSize = DEFAULT_PAGE_SIZE
            )
            response.data
        }

    /**
     * 根据模组ID获取指定模组
      */
    suspend fun getMod(modId: Int) = api.getMod(modId)

    /**
     * 根据模组Id列表获取指定模组列表
     */
    suspend fun getMods(modIds: IntArray) = api.getMods(modIds)

    /**
     * 获取指定模组文件
     */
    suspend fun getModFile(modId: Int, fileId: Int) = api.getModFile(modId, fileId)

    /**
     * 获取模组文件列表，返回分页的列表
     * @return 分页的列表
     */
    suspend fun getModFiles(modId: Int): PagedList<File> =
        PagedList(DEFAULT_PAGE_SIZE) { index ->
            val response = api.getModFiles(
                modId,
                index = index,
                pageSize = DEFAULT_PAGE_SIZE
            )
            response.data
        }

    /**
     * 获取文件更改日志，结果为HTML文本
     * @return Changelog HTML
     */
    suspend fun getModFileChangelog(modId: Int, fileId: Int) =
        api.getModFileChangelog(modId, fileId)

    /**
     * 获取文件下载地址
     */
    suspend fun getModFileDownloadURL(modId: Int, fileId: Int) =
        api.getModFileDownloadURL(modId, fileId)
}