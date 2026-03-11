package top.jie65535.jcf

import top.jie65535.jcf.model.modrinth.Project
import top.jie65535.jcf.model.modrinth.SearchHit
import top.jie65535.jcf.model.modrinth.Version
import top.jie65535.jcf.util.PagedList

class ModrinthService {

    companion object {
        private const val DEFAULT_PAGE_SIZE = 10
    }

    /**
     * Modrinth project types.
     */
    enum class ProjectType(val typeName: String, val typeId: String) {
        /** 模组 */
        MODS("模组", "mod"),

        /** 整合包 */
        MODPACKS("整合包", "modpack"),

        /** 资源包 */
        RESOURCE_PACKS("资源包", "resourcepack"),

        /** 光影 */
        SHADERS("光影", "shader"),

        /** 服务器插件 */
        PLUGINS("服务器插件", "plugin"),

        /** 数据包 */
        DATA_PACKS("数据包", "datapack"),
    }

    private val api = ModrinthApi()

    /**
     * Search Modrinth projects by type and filter string.
     * @param projectType The type of project to search for.
     * @param filter The search query.
     * @return A paged list of search hits.
     */
    fun search(projectType: ProjectType, filter: String): PagedList<SearchHit> =
        PagedList(DEFAULT_PAGE_SIZE) { offset ->
            val facets = """[["project_type:${projectType.typeId}"]]"""
            val response = api.search(
                query = filter,
                facets = facets,
                offset = offset,
                limit = DEFAULT_PAGE_SIZE,
            )
            response.hits.toTypedArray()
        }

    /**
     * Fetch full details for a single project.
     */
    suspend fun getProject(idOrSlug: String): Project = api.getProject(idOrSlug)

    /**
     * Fetch all versions of a project and return them as a paged list.
     */
    suspend fun getProjectVersions(idOrSlug: String): PagedList<Version> {
        val allVersions = api.getProjectVersions(idOrSlug)
        return PagedList(DEFAULT_PAGE_SIZE) { offset ->
            if (offset >= allVersions.size) {
                emptyArray()
            } else {
                val end = minOf(offset + DEFAULT_PAGE_SIZE, allVersions.size)
                allVersions.subList(offset, end).toTypedArray()
            }
        }
    }

    /**
     * Get multiple projects at once.
     */
    suspend fun getProjects(ids: List<String>): List<Project> = api.getProjects(ids)

    /**
     * Fetch a single version by ID.
     */
    suspend fun getVersion(versionId: String): Version = api.getVersion(versionId)
}
