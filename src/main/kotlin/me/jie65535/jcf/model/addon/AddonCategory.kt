/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.jie65535.jcf.model.addon

import kotlinx.serialization.Serializable

@Serializable
data class AddonCategory(
    val categoryId: Int,
    val name: String,
    val url: String,
    val avatarUrl: String?,
    val parentId: Int?,
    val rootId: Int?,
    val projectId: Int,
    val avatarId: Int?,
    val gameId: Int
)