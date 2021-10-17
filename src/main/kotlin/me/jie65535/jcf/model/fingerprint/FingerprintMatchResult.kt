/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.jie65535.jcf.model.fingerprint

import kotlinx.serialization.Serializable

@Serializable
data class FingerprintMatchResult(
    val isCacheBuilt: Boolean,
    val exactMatches: List<FingerprintMatch>,
    val exactFingerprints: List<Long>,
    val partialMatches: List<FingerprintMatch>,
    val partialMatchFingerprints: Map<String, List<Long>>,
    val installedFingerprints: List<Long>,
    val unmatchedFingerprints: List<Long>
)