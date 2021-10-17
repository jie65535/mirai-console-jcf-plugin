/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.jie65535.jcf.model.game

import kotlinx.serialization.Serializable

@Serializable
data class GameFileParsingRule(
    val commentStripPattern: String,
    val fileExtension: String,
    val inclusionPattern: String,
    val gameId: Int,
    val id: Int
)