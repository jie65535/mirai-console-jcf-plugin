/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.jie65535.jcf.model.game

import me.jie65535.jcf.util.internal.FlagSerializer
import me.jie65535.jcf.util.internal.MODEL_PACKAGE

enum class GameDetectionHintOption {
    NONE,
    INCLUDE_SUB_FOLDERS,
    FOLDER_ONLY;

    internal object Ser : FlagSerializer<GameDetectionHintOption>("$MODEL_PACKAGE.game.GameDetectionHintOption", NONE to 0x1, INCLUDE_SUB_FOLDERS to 0x2, FOLDER_ONLY to 0x4)
}