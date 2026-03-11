package top.jie65535.jcf.model.modrinth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response from the Modrinth search endpoint.
 * See: https://docs.modrinth.com/api/#tag/projects/operation/searchProjects
 */
@Serializable
data class SearchResponse(
    /** List of search results */
    val hits: List<SearchHit>,

    /** The number of results skipped */
    val offset: Int,

    /** The number of results per page */
    val limit: Int,

    /** Total number of matching results */
    @SerialName("total_hits")
    val totalHits: Int,
)
