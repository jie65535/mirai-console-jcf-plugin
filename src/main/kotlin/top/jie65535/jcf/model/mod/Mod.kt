package top.jie65535.jcf.model.mod

import kotlinx.serialization.Serializable
import top.jie65535.jcf.model.Category
import top.jie65535.jcf.model.file.File
import top.jie65535.jcf.model.file.FileIndex
import top.jie65535.jcf.util.OffsetDateTimeSerializer
import java.time.OffsetDateTime

@kotlinx.serialization.Serializable
class Mod(
    /**
     * The mod id
     */
    val id: Int,
    /**
     * The game id this mod is for
     */
    val gameId: Int,
    /**
     * 	The name of the mod
     */
    val name: String,
    /**
     * The mod slug that would appear in the URL
     */
    val slug: String,
    /**
     * 	Relevant links for the mod such as Issue tracker and Wiki
     */
    val links: ModLinks,
    /**
     * Mod summary
     */
    val summary: String,
    /**
     * Current mod status
     */
    val status: ModStatus,
    /**
     * Number of downloads for the mod
     */
    val downloadCount: Long,
    /**
     * Whether the mod is included in the featured mods list
     */
    val isFeatured: Boolean,
    /**
     * The main category of the mod as it was chosen by the mod author
     */
    val primaryCategoryId: Int,
    /**
     * List of categories that this mod is related to
     */
    val categories: Array<Category>,
    /**
     * The class id this mod belongs to
     */
    val classId: Int?,
    /**
     * List of the mod's authors
     */
    val authors: Array<ModAuthor>,
    /**
     * The mod's logo asset
     */
    val logo: ModAsset,
    /**
     * List of screenshots assets
     */
    val screenshots: Array<ModAsset>,
    /**
     * The id of the main file of the mod
     */
    val mainFileId: Int,
    /**
     * List of latest files of the mod
     */
    val latestFiles: Array<File>,
    /**
     * List of file related details for the latest files of the mod
     */
    val latestFilesIndexes: Array<FileIndex>,
    /**
     * The creation date of the mod
     */
    @Serializable(OffsetDateTimeSerializer::class)
    val dateCreated: OffsetDateTime,
    /**
     * The last time the mod was modified
     */
    @Serializable(OffsetDateTimeSerializer::class)
    val dateModified: OffsetDateTime,
    /**
     * The release date of the mod
     */
    @Serializable(OffsetDateTimeSerializer::class)
    val dateReleased: OffsetDateTime,
    /**
     * Is mod allowed to be distributed
     */
    val allowModDistribution: Boolean?,
    /**
     * The mod popularity rank for the game
     */
    val gamePopularityRank: Int,
    /**
     * Is the mod available for search. This can be false when a mod is experimental,
     * in a deleted state or has only alpha files
     */
    val isAvailable: Boolean,
    /**
     *	The mod's thumbs up count
     */
    val thumbsUpCount: Int,
)
