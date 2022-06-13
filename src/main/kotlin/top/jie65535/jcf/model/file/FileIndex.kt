package top.jie65535.jcf.model.file

import top.jie65535.jcf.model.mod.ModLoaderType

@kotlinx.serialization.Serializable
class FileIndex(
    val gameVersion: String,
    val fileId: Int,
    val filename: String,
    val releaseType: FileReleaseType,
    val gameVersionTypeId: Int?,
    val modLoader: ModLoaderType,
)
