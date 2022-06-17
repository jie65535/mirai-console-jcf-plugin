package top.jie65535.jcf.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
object OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(OffsetDateTime::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return try {
            OffsetDateTime.parse(decoder.decodeString(), formatter)
        } catch(ignore: Throwable) {
            OffsetDateTime.MIN
        }
    }

    override fun serialize(encoder: Encoder, value: OffsetDateTime) =
        encoder.encodeString(formatter.format(value))

}

