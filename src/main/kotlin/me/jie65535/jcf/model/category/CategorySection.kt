/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.jie65535.jcf.model.category

import kotlinx.serialization.Serializable
import me.jie65535.jcf.model.addon.AddonPackageType

@Serializable
data class CategorySection(
    val extraIncludePattern: String?,
    val gameCategoryId: Int,
    val gameId: Int,
    val id: Int,
    val initialInclusionPattern: String,
    val name: String,
    val packageType: AddonPackageType,
    val path: String
)