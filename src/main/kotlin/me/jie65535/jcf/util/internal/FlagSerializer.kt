/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.jie65535.jcf.util.internal

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

internal open class FlagSerializer<T : Enum<T>>(serialName: String, private val map: Map<T, Int>) : KSerializer<Set<T>> {
    constructor(serialName: String, vararg pairs: Pair<T, Int>) : this(serialName, mapOf(*pairs))

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(serialName, PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Set<T>) {
        var bits = 0
        for(flag in value) {
            bits = bits or (map[flag] ?: error(""))
        }
        encoder.encodeInt(bits)
    }

    override fun deserialize(decoder: Decoder): Set<T> {
        val set = mutableSetOf<T>()
        val bits = decoder.decodeInt()
        for((flag, shift) in map) {
            if(bits and shift == shift)
                set += flag
        }
        return set
    }
}