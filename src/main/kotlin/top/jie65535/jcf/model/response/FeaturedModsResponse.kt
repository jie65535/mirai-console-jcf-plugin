package top.jie65535.jcf.model.response

import top.jie65535.jcf.model.mod.Mod

@kotlinx.serialization.Serializable
class FeaturedModsResponse(
    val featured: Array<Mod>,
    val popular: Array<Mod>,
    val recentlyUpdated: Array<Mod>,
)
