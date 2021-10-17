/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.jie65535.jcf.model.game

import kotlinx.serialization.Serializable
import me.jie65535.jcf.util.internal.EnumIntSerializer
import me.jie65535.jcf.util.internal.MODEL_PACKAGE

@Serializable(with = GamePlatformType.Ser::class)
enum class GamePlatformType {
    GENERIC,
    WINDOWS_32,
    WINDOWS_64,
    WINDOWS,
    OSX;

    internal object Ser : EnumIntSerializer<GamePlatformType>("$MODEL_PACKAGE.game.GamePlatformType", values())
}