package top.jie65535.jcf.model.modrinth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A dependency declared by a Modrinth version.
 */
@Serializable
data class VersionDependency(
    /** The version ID of the dependency (nullable) */
    @SerialName("version_id")
    val versionId: String? = null,

    /** The project ID of the dependency (nullable) */
    @SerialName("project_id")
    val projectId: String? = null,

    /** The filename hint for the dependency */
    @SerialName("file_name")
    val fileName: String? = null,

    /** The dependency type (required, optional, incompatible, embedded) */
    @SerialName("dependency_type")
    val dependencyType: String,
)
