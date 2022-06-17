package top.jie65535.jcf.model.response

import top.jie65535.jcf.model.game.Game
import top.jie65535.jcf.model.Pagination

@kotlinx.serialization.Serializable
class GetGamesResponse(
    val data: Array<Game>,
    val pagination: Pagination
)