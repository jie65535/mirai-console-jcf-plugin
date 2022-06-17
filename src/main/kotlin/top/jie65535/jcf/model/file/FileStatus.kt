package top.jie65535.jcf.model.file

import kotlinx.serialization.KSerializer
import top.jie65535.jcf.util.EnumIndexSerializer

@kotlinx.serialization.Serializable(with = FileStatus.IndexSerializer::class)
enum class FileStatus{
    Processing,
    ChangesRequired,
    UnderReview,
    Approved,
    Rejected,
    MalwareDetected,
    Deleted,
    Archived,
    Testing,
    Released,
    ReadyForReview,
    Deprecated,
    Baking,
    AwaitingPublishing,
    FailedPublishing;

    internal object IndexSerializer : KSerializer<FileStatus> by EnumIndexSerializer(values())
}
