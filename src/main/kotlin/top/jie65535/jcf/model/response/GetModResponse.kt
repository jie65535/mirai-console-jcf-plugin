package top.jie65535.jcf.model.response

import top.jie65535.jcf.model.mod.Mod

@kotlinx.serialization.Serializable
class GetModResponse(
    val data: Mod
)