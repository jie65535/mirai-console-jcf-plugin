package top.jie65535.jcf.model.response

import top.jie65535.jcf.model.Pagination
import top.jie65535.jcf.model.mod.Mod

@kotlinx.serialization.Serializable
class SearchModsResponse(
    val data: Array<Mod>,
    val pagination: Pagination,
)