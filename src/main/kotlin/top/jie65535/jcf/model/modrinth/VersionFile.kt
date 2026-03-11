package top.jie65535.jcf.model.modrinth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A file attached to a Modrinth version.
 */
@Serializable
data class VersionFile(
    /** The download URL for this file */
    val url: String,

    /** The file name */
    val filename: String,

    /** Whether this is the primary file for the version */
    val primary: Boolean = false,

    /** The file size in bytes */
    val size: Long,
)
