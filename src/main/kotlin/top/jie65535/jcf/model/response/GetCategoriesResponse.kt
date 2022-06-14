package top.jie65535.jcf.model.response

import top.jie65535.jcf.model.Category

@kotlinx.serialization.Serializable
class GetCategoriesResponse(
    val data: Array<Category>
)