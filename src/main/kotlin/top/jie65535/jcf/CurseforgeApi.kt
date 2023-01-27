package top.jie65535.jcf

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import top.jie65535.jcf.model.Category
import top.jie65535.jcf.model.file.File
import top.jie65535.jcf.model.game.Game
import top.jie65535.jcf.model.game.GameVersionType
import top.jie65535.jcf.model.mod.*
import top.jie65535.jcf.model.request.*
import top.jie65535.jcf.model.request.SortOrder.*
import top.jie65535.jcf.model.response.*

/**
 * [Api docs](https://docs.curseforge.com/)
 * @author jie65535
 */
@OptIn(ExperimentalSerializationApi::class)
class CurseforgeApi(apiKey: String) {

    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        serializersModule
    }

    private val http = HttpClient(OkHttp) {
        install(HttpTimeout) {
            this.requestTimeoutMillis = 60_000
            this.connectTimeoutMillis = 60_000
            this.socketTimeoutMillis  = 60_000
        }
        defaultRequest {
            url.protocol = URLProtocol.HTTPS
            url.host = "api.curseforge.com"
            header("accept", "application/json")
            header("x-api-key", apiKey)
        }
    }

    //region - Game -

    /**
     * Get all games that are available to the provided API key.
     */
    suspend fun getGames(index: Int? = null, pageSize: Int? = null): GetGamesResponse {
        return json.decodeFromString(
            http.get("/v1/games") {
                parameter("index", index)
                parameter("pageSize", pageSize)
            }.body()
        )
    }

    /**
     * Get a single game. A private game is only accessible by its respective API key.
     */
    suspend fun getGame(gameId: Int): Game {
        return json.decodeFromString<GetGameResponse>(
            http.get("/v1/games/$gameId").body()
        ).data
    }

    /**
     * Get all available versions for each known version type of the specified game.
     * A private game is only accessible to its respective API key.
     */
    suspend fun getVersions(gameId: Int): Array<GameVersionsByType> {
        return json.decodeFromString<GetVersionsResponse>(
            http.get("/v1/games/$gameId/versions").body()
        ).data
    }

    /**
     * Get all available version types of the specified game.
     *
     * A private game is only accessible to its respective API key.
     *
     * Currently, when creating games via the CurseForge Core Console,
     * you are limited to a single game version type.
     * This means that this endpoint is probably not useful in most cases
     * and is relevant mostly when handling existing games that have
     * multiple game versions such as World of Warcraft and Minecraft
     * (e.g. 517 for wow_retail).
     */
    suspend fun getVersionTypes(gameId: Int): Array<GameVersionType> {
        return json.decodeFromString<GetVersionTypesResponse>(
            http.get("/v1/games/$gameId/versions").body()
        ).data
    }

    //endregion

    //region - Categories -

    /**
     * Get all available classes and categories of the specified game.
     * Specify a game id for a list of all game categories,
     * or a class id for a list of categories under that class.
     */
    suspend fun getCategories(gameId: Int, classId: Int? = null): Array<Category> {
        return json.decodeFromString<GetCategoriesResponse>(
            http.get("/v1/categories") {
                parameter("gameId", gameId)
                parameter("classId", classId)
            }.body()
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
        classId: Int? = null,
        categoryId: Int? = null,
        gameVersion: String? = null,
        searchFilter: String? = null,
        sortField: ModsSearchSortField? = null,
        sortOrder: SortOrder? = null,
        modLoaderType: ModLoaderType? = null,
        gameVersionTypeId: Int? = null,
        slug: String? = null,
        index: Int? = null,
        pageSize: Int? = null
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
                    DESC -> "desc"
                    null -> null
                })
                parameter("modLoaderType", modLoaderType)
                parameter("gameVersionTypeId", gameVersionTypeId)
                parameter("slug", slug)
                parameter("index", index)
                parameter("pageSize", pageSize)
            }.body()
        )
    }

    /**
     * Get a single mod.
     */
    suspend fun getMod(modId: Int): Mod {
        return json.decodeFromString<GetModResponse>(
            http.get("/v1/mods/$modId").body()
        ).data
    }

    /**
     * Get a list of mods.
     */
    suspend fun getMods(modIds: IntArray): Array<Mod> {
        return json.decodeFromString<GetModsResponse>(
            http.post("/v1/mods") {
                headers.append("Content-Type", "application/json")
                setBody(json.encodeToString(GetModsByIdsListRequestBody(modIds)))
            }.body()
        ).data
    }

    /**
     * Get a list of featured, popular and recently updated mods.
     */
    suspend fun getFeaturedMods(
        gameId: Int,
        excludedModIds: IntArray = intArrayOf(),
        gameVersionTypeId: Int? = null
    ): FeaturedModsResponse {
        return json.decodeFromString<GetFeaturedModsResponse>(
            http.get("/v1/mods/featured") {
                headers.append("Content-Type", "application/json")
                setBody(json.encodeToString(GetFeaturedModsRequestBody(gameId, excludedModIds, gameVersionTypeId)))
            }.body()
        ).data
    }

    /**
     * Get the full description of a mod in HTML format.
     */
    suspend fun getModDescription(modId: Int): String {
        return json.decodeFromString<StringResponse>(
            http.get("/v1/mods/$modId/description").body()
        ).data
    }

    //endregion

    //region - Files -

    /**
     * Get a single file of the specified mod.
     */
    suspend fun getModFile(modId: Int, fileId: Int): File {
        return json.decodeFromString<GetModFileResponse>(
            http.get("/v1/mods/$modId/files/$fileId").body()
        ).data
    }

    /**
     * Get all files of the specified mod.
     */
    suspend fun getModFiles(
        modId: Int,
        gameVersion: String? = null,
        modLoaderType: ModLoaderType? = null,
        gameVersionTypeId: Int? = null,
        index: Int? = null,
        pageSize: Int? = null
    ): GetModFilesResponse {
        return json.decodeFromString(
            http.get("/v1/mods/$modId/files") {
                parameter("gameVersion", gameVersion)
                parameter("modLoaderType", modLoaderType)
                parameter("gameVersionTypeId", gameVersionTypeId)
                parameter("index", index)
                parameter("pageSize", pageSize)
            }.body()
        )
    }

    /**
     * Get a list of files.
     */
    suspend fun getFiles(fileIds: IntArray): Array<File> {
        return json.decodeFromString<GetFilesResponse>(
            http.post("/v1/mods/files") {
                headers.append("Content-Type", "application/json")
                setBody(json.encodeToString(GetModFilesRequestBody(fileIds)))
            }.body()
        ).data
    }

    /**
     * Get the changelog of a file in HTML format
     */
    suspend fun getModFileChangelog(modId: Int, fileId: Int): String {
        return json.decodeFromString<StringResponse>(
            http.get("/v1/mods/$modId/files/$fileId/changelog").body()
        ).data
    }

    /**
     * Get a download url for a specific file
     */
    suspend fun getModFileDownloadURL(modId: Int, fileId: Int): String {
        return json.decodeFromString<StringResponse>(
            http.get("/v1/mods/$modId/files/$fileId/download-url").body()
        ).data
    }

    //endregion
}