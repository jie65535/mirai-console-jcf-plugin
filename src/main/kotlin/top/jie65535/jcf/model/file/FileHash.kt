package top.jie65535.jcf.model.file

@kotlinx.serialization.Serializable
class FileHash(
    val value: String,
    val algo: HashAlgo,
)
