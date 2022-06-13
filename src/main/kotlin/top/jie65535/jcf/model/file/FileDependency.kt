package top.jie65535.jcf.model.file

@kotlinx.serialization.Serializable
class FileDependency(
    val modId: Int,
    val relationType: FileRelationType,
)
