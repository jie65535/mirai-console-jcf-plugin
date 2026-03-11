package top.jie65535.jcf.model.modrinth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Full details of a Modrinth project.
 * See: https://docs.modrinth.com/api/#tag/projects/operation/getProject
 */
@Serializable
data class Project(
    /** The project's ID */
    val id: String,

    /** The project's URL slug */
    val slug: String,

    /** The project type (mod, modpack, resourcepack, shader, plugin, datapack) */
    @SerialName("project_type")
    val projectType: String,

    /** The team that manages the project */
    val team: String? = null,

    /** The project's display title */
    val title: String,

    /** A short description of the project */
    val description: String,

    /** Long-form description in Markdown */
    val body: String? = null,

    /** The publication date (ISO 8601) */
    val published: String,

    /** The last update date (ISO 8601) */
    val updated: String,

    /** Project status (approved, archived, rejected, etc.) */
    val status: String? = null,

    /** Total number of downloads */
    val downloads: Long,

    /** Total number of followers */
    val followers: Int,

    /** Primary categories the project belongs to */
    val categories: List<String> = emptyList(),

    /** Additional categories */
    @SerialName("additional_categories")
    val additionalCategories: List<String> = emptyList(),

    /** List of mod loaders supported */
    val loaders: List<String> = emptyList(),

    /** List of version IDs associated with the project (newest first) */
    val versions: List<String> = emptyList(),

    /** URL to the project icon */
    @SerialName("icon_url")
    val iconUrl: String? = null,

    /** URL to the issue tracker */
    @SerialName("issues_url")
    val issuesUrl: String? = null,

    /** URL to the source code repository */
    @SerialName("source_url")
    val sourceUrl: String? = null,

    /** URL to the project's wiki */
    @SerialName("wiki_url")
    val wikiUrl: String? = null,

    /** URL to the project's Discord */
    @SerialName("discord_url")
    val discordUrl: String? = null,

    /** License information */
    val license: ProjectLicense? = null,

    /** Client-side requirement */
    @SerialName("client_side")
    val clientSide: String? = null,

    /** Server-side requirement */
    @SerialName("server_side")
    val serverSide: String? = null,
)
