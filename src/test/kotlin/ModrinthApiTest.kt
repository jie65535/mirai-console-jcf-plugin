package top.jie65535

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import top.jie65535.jcf.ModrinthApi
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ModrinthApiTest {
    companion object {
        // Sodium - one of the most popular Minecraft optimization mods on Modrinth
        private const val PROJECT_SLUG = "sodium"
        private const val PROJECT_TYPE = "mod"
    }

    // No API key required for Modrinth read operations
    private val api = ModrinthApi()

    @Test
    fun search() = runTest {
        val response = api.search(query = "sodium", facets = """[["project_type:mod"]]""", limit = 10)
        assertTrue(response.hits.isNotEmpty(), "Search should return results")
        assertTrue(response.totalHits > 0, "Total hits should be positive")
        printResult("search", response)
    }

    @Test
    fun searchPagination() = runTest {
        val page1 = api.search(query = "fabric", facets = """[["project_type:mod"]]""", offset = 0, limit = 5)
        val page2 = api.search(query = "fabric", facets = """[["project_type:mod"]]""", offset = 5, limit = 5)
        assertEquals(5, page1.hits.size, "First page should have 5 results")
        assertEquals(5, page2.hits.size, "Second page should have 5 results")
        // Pages should not overlap
        val ids1 = page1.hits.map { it.projectId }.toSet()
        val ids2 = page2.hits.map { it.projectId }.toSet()
        assertTrue(ids1.intersect(ids2).isEmpty(), "Pages should not overlap")
        printResult("searchPagination page1", page1)
        printResult("searchPagination page2", page2)
    }

    @Test
    fun getProject() = runTest {
        val project = api.getProject(PROJECT_SLUG)
        assertEquals(PROJECT_SLUG, project.slug, "Slug should match")
        assertEquals(PROJECT_TYPE, project.projectType, "Project type should be mod")
        assertNotNull(project.id, "Project ID should not be null")
        assertTrue(project.downloads > 0, "Downloads should be positive")
        printResult("getProject", project)
    }

    @Test
    fun getProjects() = runTest {
        // First get the project to find its ID
        val project = api.getProject(PROJECT_SLUG)
        val projects = api.getProjects(listOf(project.id))
        assertEquals(1, projects.size, "Should return exactly one project")
        assertEquals(project.id, projects[0].id, "Project ID should match")
        printResult("getProjects", projects)
    }

    @Test
    fun getProjectVersions() = runTest {
        val versions = api.getProjectVersions(PROJECT_SLUG)
        assertTrue(versions.isNotEmpty(), "Project should have at least one version")
        // Verify version fields
        val first = versions.first()
        assertTrue(first.id.isNotBlank(), "Version ID should not be blank")
        assertTrue(first.name.isNotBlank(), "Version name should not be blank")
        assertTrue(first.files.isNotEmpty(), "Version should have at least one file")
        printResult("getProjectVersions (first)", first)
    }

    @Test
    fun getVersion() = runTest {
        val versions = api.getProjectVersions(PROJECT_SLUG)
        assertNotNull(versions.firstOrNull(), "Project should have at least one version")
        val versionId = versions.first().id
        val version = api.getVersion(versionId)
        assertEquals(versionId, version.id, "Version ID should match")
        printResult("getVersion", version)
    }

    @Test
    fun searchModpack() = runTest {
        val response = api.search(query = "all of fabric", facets = """[["project_type:modpack"]]""", limit = 5)
        assertTrue(response.hits.isNotEmpty(), "Modpack search should return results")
        assertTrue(response.hits.all { it.projectType == "modpack" }, "All results should be modpacks")
        printResult("searchModpack", response)
    }

    @Test
    fun searchResourcePack() = runTest {
        val response = api.search(query = "faithful", facets = """[["project_type:resourcepack"]]""", limit = 5)
        assertTrue(response.hits.isNotEmpty(), "Resource pack search should return results")
        printResult("searchResourcePack", response)
    }

    private inline fun <reified T> printResult(name: String, obj: T) {
        println("$name result: ${Json.encodeToString(obj)}")
    }
}
