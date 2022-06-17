package top.jie65535.jcf.model.request

import kotlinx.serialization.KSerializer
import top.jie65535.jcf.util.EnumIndexSerializer

@kotlinx.serialization.Serializable(with = ModsSearchSortField.IndexSerializer::class)
enum class ModsSearchSortField {
     Featured,
     Popularity,
     LastUpdated,
     Name,
     Author,
     TotalDownloads,
     Category,
     GameVersion;

     internal object IndexSerializer : KSerializer<ModsSearchSortField> by EnumIndexSerializer(values())
}