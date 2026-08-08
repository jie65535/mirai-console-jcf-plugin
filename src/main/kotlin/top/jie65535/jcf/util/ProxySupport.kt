package top.jie65535.jcf.util

import okhttp3.OkHttpClient
import top.jie65535.jcf.PluginConfig
import top.jie65535.jcf.ProxyType
import java.net.InetSocketAddress
import java.net.Proxy

object ProxySupport {
    fun configure(builder: OkHttpClient.Builder) {
        createProxy()?.let(builder::proxy)
    }

    fun validationError(): String? = when {
        PluginConfig.proxyType == ProxyType.NONE -> null
        PluginConfig.proxyHost.isBlank() -> "代理已启用，但 proxyHost 为空，将忽略该代理配置"
        PluginConfig.proxyPort !in 1..65535 ->
            "代理已启用，但 proxyPort 不在 1..65535 范围内，将忽略该代理配置"
        else -> null
    }

    fun description(): String =
        "${PluginConfig.proxyType} ${PluginConfig.proxyHost.trim()}:${PluginConfig.proxyPort}"

    private fun createProxy(): Proxy? {
        if (validationError() != null) return null

        val type = when (PluginConfig.proxyType) {
            ProxyType.NONE -> return null
            ProxyType.HTTP -> Proxy.Type.HTTP
            ProxyType.SOCKS -> Proxy.Type.SOCKS
        }
        val address = InetSocketAddress.createUnresolved(
            PluginConfig.proxyHost.trim(),
            PluginConfig.proxyPort,
        )
        return Proxy(type, address)
    }
}
