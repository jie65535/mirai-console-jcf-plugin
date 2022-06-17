package top.jie65535.jcf.model.response

import top.jie65535.jcf.model.game.GameVersionType

@kotlinx.serialization.Serializable
class GetVersionTypesResponse(
    val data: Array<GameVersionType>
)