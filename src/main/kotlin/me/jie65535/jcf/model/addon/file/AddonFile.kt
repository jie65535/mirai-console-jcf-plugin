/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.jie65535.jcf.model.addon.file

import kotlinx.serialization.Serializable
import me.jie65535.jcf.model.addon.AddonPackageType
import me.jie65535.jcf.model.addon.AddonRestrictFileAccess
import me.jie65535.jcf.model.addon.AddonStatus
import me.jie65535.jcf.model.addon.SortableGameVersion
import me.jie65535.jcf.util.Date
import me.jie65535.jcf.util.internal.DateSerializer

@Serializable
data class AddonFile(
    val id: Int,
    val displayName: String,
    val fileName: String,
    @Serializable(with = DateSerializer::class)
    val fileDate: Date,
    val fileLength: Long,
    val releaseType: AddonFileReleaseType,
    val fileStatus: AddonFileStatus,
    val downloadUrl: String,
    val isAlternate: Boolean,
    val alternateFileId: Int,
    val dependencies: List<AddonFileDependency>,
    val isAvailable: Boolean,
    val modules: List<AddonFileModule>,
    val packageFingerprint: Long,
    val gameVersion: List<String>,
    val sortableGameVersion: List<SortableGameVersion>? = null,
    val installMetadata: String?,
    val changelog: String? = null,
    val hasInstallScript: Boolean,
    val isCompatibleWithClient: Boolean? = null,
    val categorySectionPackageType: AddonPackageType? = null,
    val restrictProjectFileAccess: AddonRestrictFileAccess? = null,
    val projectStatus: AddonStatus? = null,
    val renderCacheId: Int? = null,
    val fileLegacyMappingId: Int? = null,
    val projectId: Int? = null,
    val parentProjectFileId: Int? = null,
    val parentFileLegacyMappingId: Int? = null,
    val fileTypeId: Int? = null,
    val exposeAsAlternative: Boolean? = null,
    val packageFingerprintId: Int? = null,
    @Serializable(with = DateSerializer::class)
    val gameVersionDateReleased: Date?,
    val gameVersionMappingId: Int? = null,
    val gameVersionId: Int? = null,
    val gameId: Int? = null,
    val isServerPack: Boolean = false,
    val serverPackFileId: Int?,
    val gameVersionFlavor: String?
)