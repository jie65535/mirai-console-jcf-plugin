/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.jie65535.jcf.model.addon

/**
 * 附加排序方法
 */
enum class AddonSortMethod {
    /**
     * 精选
     */
    FEATURED,

    /**
     * 人气
     */
    POPULARITY,

    /**
     * 最后更新时间
     */
    LAST_UPDATED,

    /**
     * 名称
     */
    NAME,

    /**
     * 作者
     */
    AUTHOR,

    /**
     * 总下载数
     */
    TOTAL_DOWNLOADS,

    /**
     * 类别
     */
    CATEGORY,

    /**
     * 游戏版本
     */
    GAME_VERSION
}