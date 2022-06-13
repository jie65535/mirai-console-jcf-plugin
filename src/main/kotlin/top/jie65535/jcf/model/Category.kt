package top.jie65535.jcf.model

import kotlinx.serialization.Serializable
import top.jie65535.jcf.util.OffsetDateTimeSerializer
import java.time.OffsetDateTime

@Serializable
class Category(
    val id: Int,
    val gameId: Int,
    val name: String,
    val slug: String,
    val url: String,
    val iconUrl: String,
    @Serializable(OffsetDateTimeSerializer::class)
    val dateModified: OffsetDateTime,
    val isClass: Boolean?,
    val classId: Int?,
    val parentCategoryId: Int?,
    val displayIndex: Int?
)
