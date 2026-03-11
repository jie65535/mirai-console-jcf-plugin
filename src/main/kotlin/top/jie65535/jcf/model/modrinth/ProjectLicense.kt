package top.jie65535.jcf.model.modrinth

import kotlinx.serialization.Serializable

/**
 * License information for a Modrinth project.
 */
@Serializable
data class ProjectLicense(
    /** The SPDX license identifier */
    val id: String,

    /** The human-readable name of the license */
    val name: String,

    /** URL to the full license text */
    val url: String? = null,
)
