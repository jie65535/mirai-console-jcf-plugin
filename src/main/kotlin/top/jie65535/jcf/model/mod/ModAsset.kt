package top.jie65535.jcf.model.mod

@kotlinx.serialization.Serializable
class ModAsset(
    val id: Int,
    val modId: Int,
    val title: String,
    val description: String,
    val thumbnailUrl: String?,
    val url: String?,
)