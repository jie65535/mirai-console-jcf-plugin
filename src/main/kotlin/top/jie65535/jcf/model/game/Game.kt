package top.jie65535.jcf.model.game

import kotlinx.serialization.Serializable
import top.jie65535.jcf.util.OffsetDateTimeSerializer
import java.time.OffsetDateTime

@Serializable
class Game(
    val id: Int,
    val name: String,
    val slug: String?,
    @Serializable(OffsetDateTimeSerializer::class)
    val dateModified: OffsetDateTime,
    val assets: GameAssets,
    val status: CoreStatus,
    val apiStatus: CoreApiStatus,
)