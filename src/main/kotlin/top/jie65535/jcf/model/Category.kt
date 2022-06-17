package top.jie65535.jcf.model

import kotlinx.serialization.Serializable
import top.jie65535.jcf.util.OffsetDateTimeSerializer
import java.time.OffsetDateTime

@Serializable
class Category(
    /**
     * The category id
     */
    val id: Int,

    /**
     * The game id related to the category
     */
    val gameId: Int,

    /**
     * Category name
     */
    val name: String,

    /**
     * The category slug as it appear in the URL
     */
    val slug: String?,

    /**
     * 	The category URL
     */
    val url: String?,

    /**
     * 	URL for the category icon
     */
    val iconUrl: String?,

    /**
     * Last modified date of the category
     */
    @Serializable(OffsetDateTimeSerializer::class)
    val dateModified: OffsetDateTime,

    /**
     * 	A top level category for other categories
     */
    val isClass: Boolean? = null,

    /**
     * The class id of the category, meaning - the class of which this category is under
     */
    val classId: Int? = null,

    /**
     * The parent category for this category
     */
    val parentCategoryId: Int? = null,

    /**
     * The display index for this category
     */
    val displayIndex: Int? = null
)
