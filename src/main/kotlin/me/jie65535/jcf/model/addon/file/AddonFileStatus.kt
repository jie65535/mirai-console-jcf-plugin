/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.jie65535.jcf.model.addon.file

import kotlinx.serialization.Serializable
import me.jie65535.jcf.util.internal.EnumIntSerializer
import me.jie65535.jcf.util.internal.MODEL_PACKAGE

@Serializable(with = AddonFileStatus.Ser::class)
enum class AddonFileStatus {
    PROCESSING,
    CHANGES_REQUIRED,
    UNDER_REVIEW,
    APPROVED,
    REJECTED,
    MALWARE_DETECTED,
    DELETED,
    ARCHIVED,
    TESTING,
    RELEASED,
    READY_FOR_REVIEW,
    DEPRECATED,
    BAKING,
    AWAITING_FOR_PUBLISHING,
    FAILED_PUBLISHING;

    internal object Ser : EnumIntSerializer<AddonFileStatus>("$MODEL_PACKAGE.addon.file.AddonFileStatus", values())
}