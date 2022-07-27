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
     * 订阅表
     * { mod : group/qq }
     */
    private val receiverMap = mutableMapOf<Int, MutableSet<Long>>()

    /**
     * 订阅表线程锁
     */
    private val receiverLock = LockUtil(receiverMap)

    // region -- 参数

    /**
     * 清空订阅
     */
    fun clear() = receiverLock.withLock {
        clear()
    }

    /**
     * 记录订阅
     *
     * [id] 规则：
     * 等于 0 无效；
     * 大于 0 为qq；
     * 小于 0 为群号；
     *
     * @param mod 模组id
     * @param  id qq/群号（不等于0）
     */
    operator fun set(mod: Int, id: Long) {
        if (id == 0L) return
        receiverLock.withLock {
            val set = get(mod) ?: mutableSetOf()
            put(mod, set)
            set += id
            logger?.info("")// TODO 日志
        }
    }

    /**
     * 记录订阅
     *
     * @param mod 模组id
     * @param  qq q号（大于0）
     */
    fun subQQ(mod: Int, qq: Long) = set(mod, qq)

    /**
     * 记录订阅
     *
     * @param   mod 模组id
     * @param group 群号（大于0）
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
     * @param  id qq/群号（不等于0）
     */
    fun unSub(mod: Int, id: Long) {
        if (id == 0L) return
        receiverLock.withLock {
            get(mod)?.let {
                it -= id
                logger?.info("")// TODO 日志
            }
        }
    }

    /**
     * 撤销用户订阅
     *
     * @param   mod 模组id
     * @param    qq q号（大于0）
     */
    fun unSubQQ(mod: Int, qq: Long) = unSub(mod, qq)

    /**
     * 撤销群订阅
     *
     * @param   mod 模组id
     * @param group 群号（大于0）
     */
    fun unsubGroup(mod: Int, group: Long) = unSub(mod, 0 - group)

    /**
     * 撤销mod订阅
     *
     * TODO 日志
     *
     * @param mod 模组id
     */
    operator fun minusAssign(mod: Int) = receiverLock.withLock {
        if (mod < 0) {
            clear()
        } else {
            remove(mod)
        }
        Unit// return value
    }

    // endregion

    // region -- 消费

    /**
     * 遍历订阅
     *
     * @param    mod 模组id
     * @param action qq号消费操作
     */
    fun eachQQ(mod: Int, action: (Long) -> Unit) = receiverLock.withLock {
        get(mod)?.let {
            for (qq in it) {
                if (qq <= 0) continue
                action(qq)
            }
        }
    }

    /**
     * 遍历订阅
     *
     * @param    mod 模组id
     * @param action 群号消费操作
     */
    fun eachGroup(mod: Int, action: (Long) -> Unit) = receiverLock.withLock {
        get(mod)?.let {
            for (group in it) {
                if (group >= 0) continue
                action(0 - group)
            }
        }
    }

    // endregion
}
