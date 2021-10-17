/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.jie65535.jcf.model.game

import kotlinx.serialization.Serializable
import me.jie65535.jcf.model.category.CategorySection
import me.jie65535.jcf.util.Date
import me.jie65535.jcf.util.internal.DateSerializer

@Serializable
data class Game(
    val id: Int,
    val name: String,
    val slug: String,
    @Serializable(with = DateSerializer::class)
    val dateModified: Date,
    val gameFiles: List<GameFile>,
    val gameDetectionHints: List<GameDetectionHint>,
    val fileParsingRules: List<GameFileParsingRule>,
    val categorySections: List<CategorySection>,
    val maxFreeStorage: Long,
    val maxPremiumStorage: Long,
    val maxFileSize: Long,
    val addonSettingsFolderFilter: String?,
    val addonSettingsStartingFolder: String?,
    val addonSettingsFileFilter: String?,
    val addonSettingsFileRemovalFilter: String?,
    val supportsAddons: Boolean,
    val supportsPartnerAddons: Boolean,
    @Serializable(with = SupportedClientConfiguration.Ser::class)
    val supportedClientConfiguration: Set<SupportedClientConfiguration>,
    val supportsNotifications: Boolean,
    val profilerAddonId: Int,
    val twitchGameId: Int,
    val clientGameSettingsId: Int
)