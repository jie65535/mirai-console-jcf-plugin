package top.jie65535.jcf.model.modrinth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A specific version/release of a Modrinth project.
 * See: https://docs.modrinth.com/api/#tag/versions/operation/getVersion
 */
@Serializable
data class Version(
    /** The version's unique ID */
    val id: String,

    /** The ID of the project this version belongs to */
    @SerialName("project_id")
    val projectId: String,

    /** The ID of the author who published this version */
    @SerialName("author_id")
    val authorId: String,

    /** Whether this version is marked as featured */
    val featured: Boolean = false,

    /** The version's display name */
    val name: String,

    /** The version number string */
    @SerialName("version_number")
    val versionNumber: String,

    /** The version changelog in Markdown format */
    val changelog: String? = null,

    /** The publication date (ISO 8601) */
    @SerialName("date_published")
    val datePublished: String,

    /** Total downloads for this version */
    val downloads: Long,

    /** The release channel (release, beta, alpha) */
    @SerialName("version_type")
    val versionType: String,

    /** Version status */
    val status: String? = null,

    /** Files attached to this version */
    val files: List<VersionFile> = emptyList(),

    /** Dependencies this version requires */
    val dependencies: List<VersionDependency> = emptyList(),

    /** Minecraft versions this release supports */
    @SerialName("game_versions")
    val gameVersions: List<String> = emptyList(),

    /** Mod loaders this release supports */
    val loaders: List<String> = emptyList(),
)
