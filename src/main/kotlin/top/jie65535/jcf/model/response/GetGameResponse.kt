package top.jie65535.jcf.model.response

import top.jie65535.jcf.model.game.Game

@kotlinx.serialization.Serializable
class GetGameResponse(
    val data: Game
)