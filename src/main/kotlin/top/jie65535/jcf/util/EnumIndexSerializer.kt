package top.jie65535.jcf.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Suppress("FunctionName")
inline fun <reified T : Enum<T>> EnumIndexSerializer(offset: Int = 1): KSerializer<T> {
    return object : KSerializer<T> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor(T::class.qualifiedName!!, PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, value: T) =
            encoder.encodeInt(value.ordinal + offset)

        override fun deserialize(decoder: Decoder): T =
            requireNotNull(enumValues<T>().getOrNull(decoder.decodeInt())) {
                "index: ${decoder.decodeInt()} not in ${enumValues<T>()}"
            }
    }
}