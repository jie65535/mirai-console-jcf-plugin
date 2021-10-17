/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.jie65535.jcf.model.addon

import kotlinx.serialization.Serializable
import me.jie65535.jcf.util.internal.EnumIntSerializer
import me.jie65535.jcf.util.internal.MODEL_PACKAGE

@Serializable(with = AddonPackageType.Ser::class)
enum class AddonPackageType {
    FOLDER,
    CTOC,
    SINGLE_FILE,
    CMOD2,
    MOD_PACK,
    MOD;

    internal object Ser : EnumIntSerializer<AddonPackageType>("$MODEL_PACKAGE.addon.AddonPackageType", values())
}