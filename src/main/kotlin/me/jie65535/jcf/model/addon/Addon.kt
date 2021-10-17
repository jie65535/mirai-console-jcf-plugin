/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.jie65535.jcf.model.addon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.jie65535.jcf.model.addon.file.AddonFile
import me.jie65535.jcf.model.addon.file.AddonFileLatestForGameVersion
import me.jie65535.jcf.model.category.CategorySection
import me.jie65535.jcf.util.Date
import me.jie65535.jcf.util.internal.DateSerializer

@Serializable
data class Addon(
    val id: Int,
    val name: String,
    val authors: List<AddonAuthor>,
    val attachments: List<AddonAttachment>,
    val websiteUrl: String,
    val gameId: Int,
    val summary: String,
    val defaultFileId: Int,
    val downloadCount: Double,
    val latestFiles: List<AddonFile>,
    val categories: List<AddonCategory>,
    val status: AddonStatus,
    val primaryCategoryId: Int,
    val categorySection: CategorySection,
    val slug: String,
    val gameVersionLatestFiles: List<AddonFileLatestForGameVersion>,
    val isFeatured: Boolean,
    val popularityScore: Double,
    val gamePopularityRank: Int,
    val primaryLanguage: String,
    val gameSlug: String,
    val gameName: String,
    val portalName: String,
    @Serializable(with = DateSerializer::class)
    val dateModified: Date,
    @Serializable(with = DateSerializer::class)
    val dateCreated: Date,
    @Serializable(with = DateSerializer::class)
    val dateReleased: Date,
    val isAvailable: Boolean,
    @SerialName("isExperiemental")
    val isExperimental: Boolean
)