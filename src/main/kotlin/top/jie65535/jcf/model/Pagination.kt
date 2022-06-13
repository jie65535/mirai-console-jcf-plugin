package top.jie65535.jcf.model

@kotlinx.serialization.Serializable
class Pagination(
    val index: Int,
    val pageSize: Int,
    val resultCount: Int,
    val totalCount: Long,
)