package top.jie65535.jcf.model.file

import kotlinx.serialization.Serializable
import top.jie65535.jcf.model.SortableGameVersion
import top.jie65535.jcf.util.OffsetDateTimeSerializer
import java.time.OffsetDateTime

@Serializable
class File(
    /**
     * The file id
     */
    val id: Int,
    /**
     * The game id related to the mod that this file belongs to
     */
    val gameId: Int,
    /**
     * The mod id
     */
    val modId: Int,
    /**
     * 	Whether the file is available to download
     */
    val isAvailable: Boolean,
    /**
     * Display name of the file
     */
    val displayName: String,
    /**
     * 	Exact file name
     */
    val fileName: String,
    /**
     * 	The file release type
     */
    val releaseType: FileReleaseType,
    /**
     * 	Status of the file
     */
    val fileStatus: FileStatus,
    /**
     * 	The file hash (i.e. md5 or sha1)
     */
    val hashes: Array<FileHash>,
    /**
     * 	The file timestamp
     */
    @Serializable(OffsetDateTimeSerializer::class)
    val fileDate: OffsetDateTime,
    /**
     * 	The file length in bytes
     */
    val fileLength: Long,
    /**
     * 	The number of downloads for the file
     */
    val downloadCount: Long,
    /**
     * 	The file download URL
     */
    val downloadUrl: String?,
    /**
     * 	List of game versions this file is relevant for
     */
    val gameVersions: Array<String>,
    /**
     * 	Metadata used for sorting by game versions
     */
    val sortableGameVersions: Array<SortableGameVersion>,
    /**
     * 	List of dependencies files
     */
    val dependencies: Array<FileDependency>,
    /**
     * 	none
     */
    val exposeAsAlternative: Boolean? = null,
    /**
     * 	none
     */
    val parentProjectFileId: Int? = null,
    /**
     * 	none
     */
    val alternateFileId: Int? = null,
    /**
     * 	none
     */
    val isServerPack: Boolean? = null,
    /**
     * 	none
     */
    val serverPackFileId: Int? = null,
    /**
     * 	none
     */
    val fileFingerprint: Long,
    /**
     * 	none
     */
    val modules: Array<FileModule>,
)