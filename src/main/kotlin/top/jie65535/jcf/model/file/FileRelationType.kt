package top.jie65535.jcf.model.file

import kotlinx.serialization.KSerializer
import top.jie65535.jcf.util.EnumIndexSerializer

@kotlinx.serialization.Serializable(with = FileRelationType.IndexSerializer::class)
enum class FileRelationType {
    EmbeddedLibrary,
    OptionalDependency,
    RequiredDependency,
    Tool,
    Incompatible,
    Include;

    internal object IndexSerializer : KSerializer<FileRelationType> by EnumIndexSerializer()
}
