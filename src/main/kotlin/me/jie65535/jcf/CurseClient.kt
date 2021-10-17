package me.jie65535.jcf

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.jie65535.jcf.model.addon.*
import me.jie65535.jcf.model.addon.file.*
import me.jie65535.jcf.model.addon.request.*
import me.jie65535.jcf.model.category.*
import me.jie65535.jcf.model.fingerprint.*
import me.jie65535.jcf.model.fingerprint.request.*
import me.jie65535.jcf.model.game.*
import me.jie65535.jcf.model.minecraft.*
import me.jie65535.jcf.model.minecraft.modloader.*
import me.jie65535.jcf.util.Date
import me.jie65535.jcf.util.internal.DateSerializer

@OptIn(ExperimentalSerializationApi::class)
class CurseClient {
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
            url.host = "addons-ecs.forgesvc.net"
            header("accept", "*/*")
        }
    }

    suspend fun getGames(supportsAddons: Boolean = false): List<Game> {
        return json.decodeFromString(
            http.get("api/v2/game") {
                parameter("supportsAddons", supportsAddons.toString())
            }
        )
    }

    suspend fun getGame(gameId: Int): Game {
        return json.decodeFromString(http.get("api/v2/game/$gameId"))
    }

    suspend fun getGameDatabaseTimestamp(): Date {
        return json.decodeFromString(http.get("api/v2/game/timestamp"))
    }

    suspend fun getAddon(projectId: Int): Addon {
        return json.decodeFromString(http.get("api/v2/addon/$projectId"))
    }

    suspend fun getAddons(projectIds: Collection<Int>): List<Addon> {
        return json.decodeFromString(
            http.post("api/v2/addon") {
                contentType(ContentType.Application.Json)
                body = projectIds
            }
        )
    }

    suspend fun getAddons(vararg projectIds: Int): List<Addon> = getAddons(projectIds.toList())

    suspend fun getAddonDescription(projectId: Int): String {
        return json.decodeFromString(http.get("api/v2/addon/$projectId/description"))
    }

    suspend fun getAddonFiles(projectId: Int): List<AddonFile> {
        return json.decodeFromString(http.get("api/v2/addon/$projectId/files"))
    }

    suspend fun getAddonFiles(keys: Collection<Int>): Map<Int, List<AddonFile>> {
        return json.decodeFromString(
            http.post("api/v2/addon/files") {
                contentType(ContentType.Application.Json)
                body = keys
            }
        )
    }

    suspend fun getAddonFiles(vararg keys: Int): Map<Int, List<AddonFile>> = getAddonFiles(keys.toList())

    suspend fun getAddonFileDownloadUrl(projectId: Int, fileId: Int): String {
        return json.decodeFromString(http.get("api/v2/addon/$projectId/file/$fileId/download-url"))
    }

    suspend fun getAddonFile(projectId: Int, fileId: Int): AddonFile {
        return json.decodeFromString(http.get("api/v2/addon/$projectId/file/$fileId"))
    }

    suspend fun searchAddons(
        gameId: Int,
        sectionId: Int = -1,
        categoryId: Int = -1,
        sort: AddonSortMethod = AddonSortMethod.FEATURED,
        sortDescending: Boolean = true,
        gameVersion: String? = null,
        index: Int = 0,
        pageSize: Int = 1000,
        searchFilter: String? = null
    ): List<Addon> {
        return json.decodeFromString(
            http.get("api/v2/addon/search") {
                parameter("gameId", gameId)
                parameter("sectionId", sectionId)
                parameter("categoryId", categoryId)
                parameter("gameVersion", gameVersion)
                parameter("index", index)
                parameter("pageSize", pageSize)
                parameter("searchFilter", searchFilter)
                parameter("sort", sort)
                parameter("sortDescending", sortDescending)
            }
        )
    }

    suspend fun getFeaturedAddons(
        gameId: Int,
        featuredCount: Int = 6,
        popularCount: Int = 14,
        updatedCount: Int = 14,
        excludedAddons: Collection<Int> = listOf()
    ): Map<FeaturedAddonType, List<Addon>> {
        return json.decodeFromString(
            http.post("api/v2/addon/featured") {
                contentType(ContentType.Application.Json)
                body = FeaturedAddonsRequest(gameId, featuredCount, popularCount, updatedCount, excludedAddons)
            }
        )
    }

    suspend fun getFeaturedAddons(
        gameId: Int,
        featuredCount: Int = 6,
        popularCount: Int = 14,
        updatedCount: Int = 14,
        vararg excludedAddons: Int
    ): Map<FeaturedAddonType, List<Addon>> = getFeaturedAddons(gameId, featuredCount, popularCount, updatedCount, excludedAddons.toList())

    suspend fun getCategory(categoryId: Int): Category {
        return json.decodeFromString(http.get("api/v2/category/$categoryId"))
    }

    suspend fun getCategory(slug: String): List<Category> {
        return json.decodeFromString(
            http.get("api/v2/category") {
                parameter("slug", slug)
            }
        )
    }

    suspend fun getCategorySection(sectionId: Int): List<Category> {
        return json.decodeFromString(http.get("api/v2/category/section/$sectionId"))
    }

    suspend fun getCategories(): List<Category> {
        return json.decodeFromString(http.get("api/v2/category"))
    }

    suspend fun getCategoryDatabaseTimestamp(): Date {
        return json.decodeFromString(http.get("api/v2/category/timestamp"))
    }

    suspend fun getFingerprintMatches(fingerprints: Collection<Long>): FingerprintMatchResult {
        return json.decodeFromString(
            http.post("api/v2/fingerprint") {
                contentType(ContentType.Application.Json)
                body = fingerprints
            }
        )
    }

    suspend fun getFingerprintMatches(vararg fingerprints: Long): FingerprintMatchResult = getFingerprintMatches(fingerprints.toList())

    suspend fun getFuzzyFingerprintMatches(gameId: Int, fingerprints: List<FolderFingerprint>): List<FuzzyFingerprintMatch> {
        return json.decodeFromString(
            http.post("api/v2/fingerprint/fuzzy") {
                contentType(ContentType.Application.Json)
                body = FuzzyMatchesRequest(gameId, fingerprints)
            }
        )
    }

    suspend fun getModloader(key: String): ModloaderVersion {
        return json.decodeFromString(http.get("api/v2/minecraft/modloader/$key"))
    }

    suspend fun getModloaders(): List<ModloaderIndex> {
        return json.decodeFromString(http.get("api/v2/minecraft/modloader"))
    }

    suspend fun getModloaders(gameVersion: String): List<ModloaderIndex> {
        return json.decodeFromString(
            http.get("api/v2/minecraft/modloader") {
                parameter("version", gameVersion)
            }
        )
    }

    suspend fun getModloadersDatabaseTimestamp(): Date {
        return json.decodeFromString(http.get("api/v2/minecraft/modloader/timestamp"))
    }

    suspend fun getMinecraftVersions(): List<MinecraftVersion> {
        return json.decodeFromString(http.get("api/v2/minecraft/version"))
    }

    suspend fun getMinecraftVersion(gameVersion: String): MinecraftVersion {
        return json.decodeFromString(http.get("api/v2/minecraft/version/$gameVersion"))
    }

    suspend fun getMinecraftVersionsDatabaseTimestamp(): Date {
        return json.decodeFromString(http.get("api/v2/minecraft/version/timestamp"))
    }
}