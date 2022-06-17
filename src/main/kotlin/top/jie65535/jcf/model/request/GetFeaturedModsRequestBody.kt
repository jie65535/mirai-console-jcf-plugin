package top.jie65535.jcf.model.request

@kotlinx.serialization.Serializable
class GetFeaturedModsRequestBody(
    val gameId: Int,
    val excludedModIds: IntArray,
    val gameVersionTypeId: Int? = null,
)
