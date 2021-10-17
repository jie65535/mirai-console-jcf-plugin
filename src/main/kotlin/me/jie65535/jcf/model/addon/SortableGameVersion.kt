/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.jie65535.jcf.model.addon

import kotlinx.serialization.Serializable
import me.jie65535.jcf.util.Date
import me.jie65535.jcf.util.internal.DateSerializer

@Serializable
data class SortableGameVersion(
    val gameVersionPadded: String,
    val gameVersion: String,
    @Serializable(with = DateSerializer::class)
    val gameVersionReleaseDate: Date,
    val gameVersionName: String
)