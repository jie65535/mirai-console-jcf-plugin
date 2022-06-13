package top.jie65535.jcf.model.request

@kotlinx.serialization.Serializable
class GetModsByIdsListRequestBody(
    val modIds: IntArray
)