package top.jie65535.jcf.model.response

@kotlinx.serialization.Serializable
class GetVersionsResponse(
    val data: Array<GameVersionsByType>
)