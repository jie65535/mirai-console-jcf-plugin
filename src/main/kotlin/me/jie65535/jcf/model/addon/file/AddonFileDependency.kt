/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.jie65535.jcf.model.addon.file

import kotlinx.serialization.Serializable

@Serializable
data class AddonFileDependency(
    val id: Int? = null,
    val addonId: Int,
    val type: AddonFileRelationType,
    val fileId: Int? = null
)