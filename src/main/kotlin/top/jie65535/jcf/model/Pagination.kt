package top.jie65535.jcf.model

@kotlinx.serialization.Serializable
class Pagination(
    /**
     * A zero based index of the first item that is included in the response
     */
    val index: Int,

    /**
     * The requested number of items to be included in the response
     */
    val pageSize: Int,

    /**
     * The actual number of items that were included in the response
     */
    val resultCount: Int,

    /**
     * The total number of items available by the request
     */
    val totalCount: Long,
)