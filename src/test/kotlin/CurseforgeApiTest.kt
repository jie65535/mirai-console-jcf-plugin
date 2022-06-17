package top.jie65535

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import top.jie65535.jcf.CurseforgeApi
import top.jie65535.jcf.model.request.ModsSearchSortField
import top.jie65535.jcf.model.request.SortOrder
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class CurseforgeApiTest {
    companion object {
        private const val GAME_ID_MINECRAFT = 432
        private const val MOD_ID_JEI = 238222
        private const val FILE_ID_JEI = 3835406
        private const val CLASS_ID_WORLDS = 17
        private const val CLASS_ID_BUKKIT_PLUGINS = 5
        private const val CLASS_ID_CUSTOMIZATION = 4546
        private const val CLASS_ID_MODPACKS = 4471
        private const val CLASS_ID_RESOURCE_PACKS = 12
        private const val CLASS_ID_ADDONS = 4559
        private const val CLASS_ID_MODS = 6
    }

    // 从 api_key.txt 文件中读取
    private val api = CurseforgeApi(File("api_key.txt").readText())

    @Test
    fun getGames() = runTest {
        val games = api.getGames()
        val game = assertNotNull(games.data.find { it.name == "Minecraft" })
        assert(game.id == GAME_ID_MINECRAFT)
        printResult("getGames", games)
    }

    @Test
    fun getGame() = runTest {
        val game = api.getGame(GAME_ID_MINECRAFT)
        assertEquals(game.name, "Minecraft")
        printResult("getGame", game)
    }

    @Test
    fun getVersions() = runTest {
        val versions = api.getVersions(GAME_ID_MINECRAFT)
        printResult("getVersions", versions)
    }

    @Test
    fun getVersionTypes() = runTest {
        val versionTypes = api.getVersionTypes(GAME_ID_MINECRAFT)
        printResult("getVersionTypes", versionTypes)
    }

    @Test
    fun getCategories() = runTest {
        val categories = api.getCategories(GAME_ID_MINECRAFT)
        val classes = categories.filter { it.isClass == true }
        for (gameClass in classes) {
            println("[${gameClass.id}] ${gameClass.name}")
            for (category in categories.filter { it.classId == gameClass.id }) {
                println(" | [${category.id}] ${category.name}")
            }
        }
        printResult("getCategories", categories)
    }

    @Test
    fun searchMods() = runTest {
        val response = api.searchMods(GAME_ID_MINECRAFT, searchFilter = "create", sortField = ModsSearchSortField.TotalDownloads, sortOrder = SortOrder.DESC, pageSize = 10)
        for (mod in response.data) {
            println("[${mod.id}] ${mod.name}\t ${mod.summary}\t DownloadCount: ${mod.downloadCount}")
        }
        printResult("searchMods", response)
    }

    @Test
    fun getMod() = runTest { 
        val mod = api.getMod(MOD_ID_JEI)
        assertEquals(mod.id, MOD_ID_JEI)
        printResult("getMod", mod)
    }
    
    @Test
    fun getMods() = runTest { 
        val mods = api.getMods(intArrayOf(MOD_ID_JEI))
        assertEquals(mods.size, 1)
        assertEquals(mods[0].id, MOD_ID_JEI)
        printResult("getMods", mods)
    }
    
    @Test
    fun getFeaturedMods() = runTest {
        // Error: HTTP 404
        val featuredMods = api.getFeaturedMods(GAME_ID_MINECRAFT)
        printResult("getFeaturedMods", featuredMods)
    }

    @Test
    fun getModDescription() = runTest {
        val modDescription = api.getModDescription(MOD_ID_JEI)
        printResult("getModDescription", modDescription)
    }

    @Test
    fun getModFile() = runTest {
        val modFile = api.getModFile(MOD_ID_JEI, FILE_ID_JEI)
        printResult("getModFile", modFile)
    }

    @Test
    fun getModFiles() = runTest {
        val modFiles = api.getModFiles(GAME_ID_MINECRAFT)
        printResult("getModFiles", modFiles)
    }

    @Test
    fun getFiles() = runTest {
        val files = api.getFiles(intArrayOf(FILE_ID_JEI))
        printResult("getFiles", files)
    }

    @Test
    fun getModFileChangelog() = runTest {
        val modFileChangelog = api.getModFileChangelog(MOD_ID_JEI, FILE_ID_JEI)
        printResult("getModFileChangelog", modFileChangelog)
    }

    @Test
    fun getModFileDownloadURL() = runTest {
        val modFileDownloadUrl = api.getModFileDownloadURL(MOD_ID_JEI, FILE_ID_JEI)
        printResult("getModFileDownloadURL", modFileDownloadUrl)
    }
    
    private inline fun <reified T> printResult(name: String, obj: T) {
        println("$name result: ${Json.encodeToString(obj)}")
    }
}