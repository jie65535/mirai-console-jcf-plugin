package top.jie65535.jcf.model.response

@kotlinx.serialization.Serializable
class GameVersionsByType(
    val type: Int,
    val versions: Array<String>
)
