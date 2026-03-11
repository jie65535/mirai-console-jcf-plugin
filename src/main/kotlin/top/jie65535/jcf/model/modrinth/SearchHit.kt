package top.jie65535.jcf.model.modrinth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A single project returned by the Modrinth search endpoint.
 * See: https://docs.modrinth.com/api/#tag/projects/operation/searchProjects
 */
@Serializable
data class SearchHit(
    /** The project's ID */
    @SerialName("project_id")
    val projectId: String,

    /** The project type (mod, modpack, resourcepack, shader, plugin, datapack) */
    @SerialName("project_type")
    val projectType: String,

    /** The project's URL slug */
    val slug: String,

    /** The primary author's username */
    val author: String,

    /** The project's display title */
    val title: String,

    /** A short description of the project */
    val description: String,

    /** The categories/tags the project belongs to */
    val categories: List<String> = emptyList(),

    /** Categories shown in the UI */
    @SerialName("display_categories")
    val displayCategories: List<String> = emptyList(),

    /** Game versions supported by the project */
    val versions: List<String> = emptyList(),

    /** Total number of downloads */
    val downloads: Long,

    /** Total number of followers */
    val follows: Int,

    /** The URL of the project's icon */
    @SerialName("icon_url")
    val iconUrl: String? = null,

    /** The creation date of the project (ISO 8601) */
    @SerialName("date_created")
    val dateCreated: String,

    /** The last modification date (ISO 8601) */
    @SerialName("date_modified")
    val dateModified: String,

    /** The version string of the latest release */
    @SerialName("latest_version")
    val latestVersion: String? = null,

    /** The SPDX license identifier */
    val license: String? = null,

    /** Client-side requirement */
    @SerialName("client_side")
    val clientSide: String? = null,

    /** Server-side requirement */
    @SerialName("server_side")
    val serverSide: String? = null,
)
