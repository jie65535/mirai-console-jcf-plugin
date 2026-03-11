package top.jie65535.jcf

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import top.jie65535.jcf.model.modrinth.Project
import top.jie65535.jcf.model.modrinth.SearchResponse
import top.jie65535.jcf.model.modrinth.Version

/**
 * HTTP client for the Modrinth API. No API key is required for read operations.
 * [Api docs](https://docs.modrinth.com/api/)
 * @author jie65535
 */
@OptIn(ExperimentalSerializationApi::class)
class ModrinthApi {

    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    private val http = HttpClient(OkHttp) {
        install(HttpTimeout) {
            this.requestTimeoutMillis = 60_000
            this.connectTimeoutMillis = 60_000
            this.socketTimeoutMillis  = 60_000
        }
        defaultRequest {
            url.protocol = URLProtocol.HTTPS
            url.host = "api.modrinth.com"
            header("accept", "application/json")
            // Modrinth recommends a descriptive User-Agent to identify the application.
            // No API key is required for read-only (search/get) endpoints.
            header("User-Agent", "jie65535/mirai-console-jcf-plugin (https://github.com/jie65535/mirai-console-jcf-plugin)")
        }
    }

    //region - Projects -

    /**
     * Search for projects.
     * @param query The search query string.
     * @param facets JSON-encoded facet filter, e.g. `[["project_type:mod"]]`.
     * @param index The sorting method (relevance, downloads, follows, newest, updated).
     * @param offset Number of results to skip (for pagination).
     * @param limit Maximum number of results to return (max 100).
     */
    suspend fun search(
        query: String? = null,
        facets: String? = null,
        index: String = "relevance",
        offset: Int = 0,
        limit: Int = 10,
    ): SearchResponse {
        val response = http.get("/v2/search") {
            parameter("query", query)
            parameter("facets", facets)
            parameter("index", index)
            parameter("offset", offset)
            parameter("limit", limit)
        }
        return json.decodeFromString(response.body<String>())
    }

    /**
     * Get details of a single project by ID or slug.
     */
    suspend fun getProject(idOrSlug: String): Project {
        val response = http.get("/v2/project/$idOrSlug")
        return json.decodeFromString(response.body<String>())
    }

    /**
     * Get multiple projects at once by their IDs.
     * @param ids List of project IDs.
     */
    suspend fun getProjects(ids: List<String>): List<Project> {
        val response = http.get("/v2/projects") {
            parameter("ids", json.encodeToString(ListSerializer(String.serializer()), ids))
        }
        return json.decodeFromString(response.body<String>())
    }

    //endregion

    //region - Versions -

    /**
     * List all versions of a project.
     * @param idOrSlug Project ID or slug.
     * @param loaders Filter by mod loader(s).
     * @param gameVersions Filter by Minecraft version(s).
     * @param featured When true, only return featured versions.
     */
    suspend fun getProjectVersions(
        idOrSlug: String,
        loaders: List<String>? = null,
        gameVersions: List<String>? = null,
        featured: Boolean? = null,
    ): List<Version> {
        val response = http.get("/v2/project/$idOrSlug/version") {
            loaders?.let { parameter("loaders", json.encodeToString(ListSerializer(String.serializer()), it)) }
            gameVersions?.let { parameter("game_versions", json.encodeToString(ListSerializer(String.serializer()), it)) }
            parameter("featured", featured)
        }
        return json.decodeFromString(response.body<String>())
    }

    /**
     * Get details of a single version by ID.
     */
    suspend fun getVersion(versionId: String): Version {
        val response = http.get("/v2/version/$versionId")
        return json.decodeFromString(response.body<String>())
    }

    //endregion
}
