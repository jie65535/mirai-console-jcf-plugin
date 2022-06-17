package top.jie65535.jcf.model.game

import kotlinx.serialization.KSerializer
import top.jie65535.jcf.util.EnumIndexSerializer

@kotlinx.serialization.Serializable(with = CoreApiStatus.IndexSerializer::class)
enum class CoreApiStatus {
    Private,
    Public;

    internal object IndexSerializer : KSerializer<CoreApiStatus> by EnumIndexSerializer(values())
}
