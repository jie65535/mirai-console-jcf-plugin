package top.jie65535.jcf.model.mod

import kotlinx.serialization.KSerializer
import top.jie65535.jcf.util.EnumIndexSerializer

@kotlinx.serialization.Serializable(with = ModStatus.IndexSerializer::class)
enum class ModStatus {
    New,
    ChangesRequired,
    UnderSoftReview,
    Approved,
    Rejected,
    ChangesMade,
    Inactive,
    Abandoned,
    Deleted,
    UnderReview;

    internal object IndexSerializer : KSerializer<ModStatus> by EnumIndexSerializer(values())
}
