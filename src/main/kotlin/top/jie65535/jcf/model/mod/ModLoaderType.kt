package top.jie65535.jcf.model.mod

import kotlinx.serialization.KSerializer
import top.jie65535.jcf.util.EnumIndexSerializer

@kotlinx.serialization.Serializable(with = ModLoaderType.IndexSerializer::class)
enum class ModLoaderType {
    Any,
    Forge,
    Cauldron,
    LiteLoader,
    Fabric,
    Quilt;

    internal object IndexSerializer : KSerializer<ModLoaderType> by EnumIndexSerializer()
}