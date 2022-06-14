package top.jie65535.jcf.model

import kotlinx.serialization.Serializable
import top.jie65535.jcf.util.OffsetDateTimeSerializer
import java.time.OffsetDateTime

@Serializable
class SortableGameVersion(
    /**
     * Original version name (e.g. 1.5b)
     */
    val gameVersionName: String,

    /**
     * Used for sorting (e.g. 0000000001.0000000005)
     */
    val gameVersionPadded: String,

    /**
     * 	game version clean name (e.g. 1.5)
     */
    val gameVersion: String,

    /**
     * 	Game version release date
     */
    @Serializable(OffsetDateTimeSerializer::class)
    val gameVersionReleaseDate: OffsetDateTime,

    /**
     * Game version type id
     */
    val gameVersionTypeId: Int?
)
