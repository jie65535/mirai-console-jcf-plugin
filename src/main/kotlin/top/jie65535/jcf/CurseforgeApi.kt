package top.jie65535.jcf

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import top.jie65535.jcf.model.Category
import top.jie65535.jcf.model.file.File
import top.jie65535.jcf.model.mod.*
import top.jie65535.jcf.model.request.*
import top.jie65535.jcf.model.request.SortOrder.*
import top.jie65535.jcf.model.response.*

/**
 * [Api docs](https://docs.curseforge.com/)
 */
@OptIn(ExperimentalSerializationApi::class)
class CurseforgeApi(apiKey: String) {
    companion object {
        private const val GAME_ID_MINECRAFT = 432
    }

    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        serializersModule
    }

    private val http = HttpClient(OkHttp) {
        install(HttpTimeout) {
            this.requestTimeoutMillis = 30_0000
            this.connectTimeoutMillis = 30_0000
            this.socketTimeoutMillis = 30_0000
        }
        defaultRequest {
            url.protocol = URLProtocol.HTTPS
            url.host = "api.curseforge.com"
            header("accept", "application/json")
            header("x-api-key", apiKey)
        }
    }

    //region - Game -

    // Minecraft Game ID is 432
    // Ignore Game APIs

    //endregion

    //region - Categories -

    /**
     * Get all available classes and categories of the specified game.
     * Specify a game id for a list of all game categories,
     * or a class id for a list of categories under that class.
     */
    suspend fun getCategories(gameId: Int, classId: Int?): Array<Category> {
        return json.decodeFromString<GetCategoriesResponse>(
            http.get("/v1/categories") {
                parameter("gameId", gameId)
                parameter("classId", classId)
            }
        ).data
    }

    //endregion

    //region - Mods -

    /**
     * Get all mods that match the search criteria.
     * @param gameId Filter by game id.
     * @param classId Filter by section id (discoverable via Categories)
     * @param categoryId Filter by category id
     * @param gameVersion Filter by game version string
     * @param searchFilter Filter by free text search in the mod name and author
     * @param sortField Filter by ModsSearchSortField enumeration
     * @param sortOrder 'asc' if sort is in ascending order, 'desc' if sort is in descending order
     * @param modLoaderType Filter only mods associated to a given modloader (Forge, Fabric ...). Must be coupled with gameVersion.
     * @param gameVersionTypeId Filter only mods that contain files tagged with versions of the given gameVersionTypeId
     * @param slug Filter by slug (coupled with classId will result in a unique result).
     * @param index A zero based index of the first item to include in the response,
     * @param pageSize The number of items to include in the response,
     */
    suspend fun searchMods(
        gameId: Int,
        classId: Int?,
        categoryId: Int?,
        gameVersion: String?,
        searchFilter: String?,
        sortField: ModsSearchSortField?,
        sortOrder: SortOrder?,
        modLoaderType: ModLoaderType?,
        gameVersionTypeId: Int?,
        slug: String?,
        index: Int?,
        pageSize: Int?
    ): SearchModsResponse {
        return json.decodeFromString(
            http.get("/v1/mods/search") {
                parameter("gameId", gameId)
                parameter("classId", classId)
                parameter("categoryId", categoryId)
                parameter("gameVersion", gameVersion)
                parameter("searchFilter", searchFilter)
                parameter("sortField", sortField)
                parameter("sortOrder", when(sortOrder){
                    ASC -> "asc"
                    DESC -> "asc"
                    null -> null
                })
                parameter("modLoaderType", modLoaderType)
                parameter("gameVersionTypeId", gameVersionTypeId)
                parameter("slug", slug)
                parameter("index", index)
                parameter("pageSize", pageSize)
            }
        )
    }

    /**
     * Get a single mod.
     */
    suspend fun getMod(modId: Int): Mod {
        return json.decodeFromString<GetModResponse>(
            http.get("/v1/mods/$modId")
        ).data
    }

    /**
     * Get a list of mods.
     */
    suspend fun getMods(modIds: IntArray): Array<Mod> {
        return json.decodeFromString<GetModsResponse>(
            http.post("/v1/mods") {
                body = json.encodeToString(GetModsByIdsListRequestBody(modIds))
            }
        ).data
    }

    /**
     * Get a list of featured, popular and recently updated mods.
     */
    suspend fun getFeaturedMods(
        gameId: Int,
        excludedModIds: IntArray,
        gameVersionTypeId: Int?
    ): FeaturedModsResponse {
        return json.decodeFromString<GetFeaturedModsResponse>(
            http.get("/v1/mods/featured") {
                body = json.encodeToString(GetFeaturedModsRequestBody(gameId, excludedModIds, gameVersionTypeId))
            }
        ).data
    }

    /**
     * Get the full description of a mod in HTML format.
     */
    suspend fun getModDescription(modId: Int): String {
        return json.decodeFromString<StringResponse>(
            http.get("/v1/mods/$modId/description")
        ).data
    }

    //endregion

    //region - Files -

    /**
     * Get a single file of the specified mod.
     */
    suspend fun getModFile(modId: Int, fileId: Int): File {
        return json.decodeFromString<GetModFileResponse>(
            http.get("/v1/mods/$modId/files/$fileId")
        ).data
    }

    /**
     * Get all files of the specified mod.
     */
    suspend fun getModFiles(
        modId: Int,
        gameVersion: String?,
        modLoaderType: ModLoaderType?,
        gameVersionTypeId: Int?,
        index: Int?,
        pageSize: Int?
    ): GetModFilesResponse {
        return json.decodeFromString(
            http.get("/v1/mods/$modId/files") {
                parameter("gameVersion", gameVersion)
                parameter("modLoaderType", modLoaderType)
                parameter("gameVersionTypeId", gameVersionTypeId)
                parameter("index", index)
                parameter("pageSize", pageSize)
            }
        )
    }

    /**
     * Get a list of files.
     */
    suspend fun getFiles(fileIds: IntArray): Array<File> {
        return json.decodeFromString<GetFilesResponse>(
            http.post("/v1/mods/files") {
                body = json.encodeToString(GetModFilesRequestBody(fileIds))
            }
        ).data
    }

    /**
     * Get the changelog of a file in HTML format
     */
    suspend fun getModFileChangelog(modId: Int, fileId: Int): String {
        return json.decodeFromString<StringResponse>(
            http.get("/v1/mods/$modId/files/$fileId/changelog")
        ).data
    }

    /**
     * Get a download url for a specific file
     */
    suspend fun getModFileDownloadURL(modId: Int, fileId: Int): String {
        return json.decodeFromString<StringResponse>(
            http.get("/v1/mods/$modId/files/$fileId/download-url")
        ).data
    }

    //endregion
}