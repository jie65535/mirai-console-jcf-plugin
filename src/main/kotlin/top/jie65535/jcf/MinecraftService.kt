package top.jie65535.jcf

import top.jie65535.jcf.model.file.File
import top.jie65535.jcf.model.mod.Mod
import top.jie65535.jcf.model.request.ModsSearchSortField
import top.jie65535.jcf.model.request.SortOrder
import top.jie65535.jcf.util.PagedList

class MinecraftService(apiKey: String) {
    companion object {
        private const val GAME_ID_MINECRAFT = 432
        private const val CLASS_ID_WORLDS = 17
        private const val CLASS_ID_BUKKIT_PLUGINS = 5
        private const val CLASS_ID_CUSTOMIZATION = 4546
        private const val CLASS_ID_MODPACKS = 4471
        private const val CLASS_ID_RESOURCE_PACKS = 12
        private const val CLASS_ID_ADDONS = 4559
        private const val CLASS_ID_MODS = 6
        private const val DEFAULT_PAGE_SIZE = 20
        private val DEFAULT_SORT_FIELD = ModsSearchSortField.Popularity
    }

    /**
     * api客户端实例
     */
    private val api = CurseforgeApi(apiKey)

    /**
     * 搜索存档
     */
    suspend fun searchWords(filter: String): PagedList<Mod> =
        doSearch(CLASS_ID_WORLDS, filter)

    /**
     * 搜索资源包（材质包、光影之类的）
     */
    suspend fun searchResourcePacks(filter: String): PagedList<Mod> =
        doSearch(CLASS_ID_RESOURCE_PACKS, filter)

    /**
     * 搜索整合包
     */
    suspend fun searchModPacks(filter: String): PagedList<Mod> =
        doSearch(CLASS_ID_MODPACKS, filter)

    /**
     * 搜索mod
     */
    suspend fun searchMods(filter: String): PagedList<Mod> =
        doSearch(CLASS_ID_MODS, filter)

    /**
     * 根据分类与过滤器进行搜索，返回分页的列表
     * @param classId 类别ID
     * @param filter 过滤器
     * @return 模组分页列表
     */
    private suspend fun doSearch(classId: Int, filter: String): PagedList<Mod> =
        PagedList(DEFAULT_PAGE_SIZE) { index ->
            val response = api.searchMods(
                GAME_ID_MINECRAFT,
                classId,
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