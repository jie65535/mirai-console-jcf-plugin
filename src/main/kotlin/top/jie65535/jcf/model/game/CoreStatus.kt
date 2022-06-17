package top.jie65535.jcf.model.game

import kotlinx.serialization.KSerializer
import top.jie65535.jcf.util.EnumIndexSerializer

@kotlinx.serialization.Serializable(with = CoreStatus.IndexSerializer::class)
enum class CoreStatus {
    Draft,
    Test,
    PendingReview,
    Rejected,
    Approved,
    Live;

    internal object IndexSerializer : KSerializer<CoreStatus> by EnumIndexSerializer(values())
}
