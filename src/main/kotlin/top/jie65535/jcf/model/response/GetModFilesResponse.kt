package top.jie65535.jcf.model.response

import top.jie65535.jcf.model.Pagination
import top.jie65535.jcf.model.file.File

@kotlinx.serialization.Serializable
class GetModFilesResponse(
    val data: Array<File>,
    val pagination: Pagination
)
