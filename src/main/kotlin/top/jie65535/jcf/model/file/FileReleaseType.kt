package top.jie65535.jcf.model.file

import kotlinx.serialization.KSerializer
import top.jie65535.jcf.util.EnumIndexSerializer

@kotlinx.serialization.Serializable(with = FileReleaseType.IndexSerializer::class)
enum class FileReleaseType {
    Release,
    Beta,
    Alpha;

    internal object IndexSerializer : KSerializer<FileReleaseType> by EnumIndexSerializer(values())
}
