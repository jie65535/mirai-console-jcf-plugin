package top.jie65535.jcf.model.response

import top.jie65535.jcf.model.file.File

@kotlinx.serialization.Serializable
class GetModFileResponse(
    val data: File
)
