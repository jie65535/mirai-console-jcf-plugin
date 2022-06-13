package top.jie65535.jcf.model.file

import kotlinx.serialization.KSerializer
import top.jie65535.jcf.util.EnumIndexSerializer

@kotlinx.serialization.Serializable(with = HashAlgo.IndexSerializer::class)
enum class HashAlgo(val value: Int) {
    Sha1(1),
    Md5(2);

    internal object IndexSerializer : KSerializer<HashAlgo> by EnumIndexSerializer()
}
