package top.jie65535.jcf.util

import net.mamoe.mirai.Bot
import net.mamoe.mirai.utils.MiraiLogger

/**
 * 订阅集
 *
 * @param    bot 每个 bot 维护一个订阅集
 * @param logger 日志（默认无）
 */
class SubscriptionSet(
    private val bot: Bot,
    private val logger: MiraiLogger?
) {
    /**
     * { mod : group/qq }
     * TODO 线程锁
     */
    private val receiverMap = mutableMapOf<Int, MutableSet<Long>>()

    /**
     * 记录订阅
     *
     * [id] 规则：
     * 等于 0 无效；
     * 大于 0 为qq；
     * 小于 0 为群号；
     *
     * @param mod 模组id
     * @param  id qq/群号（大于等于0）
     */
    operator fun set(mod: Int, id: Long) {
        if (id == 0L) return
        val set = receiverMap[mod] ?: mutableSetOf()
        receiverMap[mod] = set
        set +=  id
        logger?.info("")// TODO 日志
    }

    /**
     * 记录订阅
     *
     * @param mod 模组id
     * @param  qq q号（大于等于0）
     */
    fun subQQ(mod: Int, qq: Long) = set(mod, qq)

    /**
     * 记录订阅
     *
     * @param   mod 模组id
     * @param group 群号（大于等于0）
     */
    fun subGroup(mod: Int, group: Long) = set(mod, 0 - group)

    /**
     * 取消订阅
     *
     * [id] 规则：
     * 等于 0 无效；
     * 大于 0 为qq；
     * 小于 0 为群号；
     *
     * @param mod 模组id
     * @param  id qq/群号
     */
    fun unSub(mod: Int, id: Long) {
        if (id == 0L) return
        receiverMap[mod]?.let {
            it -= id
            logger?.info("")// TODO 日志
        }
    }

    /**
     * 撤销用户订阅
     *
     * @param   mod 模组id
     * @param    qq qq号（大于等于0）
     */
    fun unSubQQ(mod: Int, qq: Long) = unSub(mod, qq)

    /**
     * 撤销群订阅
     *
     * @param   mod 模组id
     * @param group 群号
     */
    fun unsubGroup(mod: Int, group: Long) = unSub(mod, 0 - group)

    /**
     * 撤销mod订阅
     *
     * TODO 日志
     *
     * @param mod 模组id
     */
    operator fun minusAssign(mod: Int) =
        if (mod < 0) {
            receiverMap.clear()
        } else {
            receiverMap -= mod
        }

    /**
     * 遍历订阅
     *
     * @param    mod 模组id
     * @param action qq号消费操作
     */
    fun eachQQ(mod: Int, action: (Long) -> Unit) =
        receiverMap[mod]?.let {
            for (qq in it) {
                if (qq <= 0) continue
                action(qq)
            }
        }

    /**
     * 遍历订阅
     *
     * @param    mod 模组id
     * @param action 群号消费操作
     */
    fun eachGroup(mod: Int, action: (Long) -> Unit) =
        receiverMap[mod]?.let {
            for (group in it) {
                if (group >= 0) continue
                action(0 - group)
            }
        }
}
