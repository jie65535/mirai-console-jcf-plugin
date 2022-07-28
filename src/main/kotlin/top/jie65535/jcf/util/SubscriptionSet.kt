package top.jie65535.jcf.util

import net.mamoe.mirai.Bot
import net.mamoe.mirai.utils.MiraiLogger
import java.util.*

/**
 * 用户个人订阅的 group
 */
private const val SINGLE: Long = 0

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
     * 订阅集名称
     */
    private val name = "sub:${bot.id}:" + UUID.randomUUID().toString()

    /**
     * 订阅表
     * { mod : { group : qq } }
     */
    private val receiverMap = mutableMapOf<Int, MutableMap<Long, MutableSet<Long>>>()

    /**
     * 订阅表线程锁
     */
    private val receiverLock = LockUtil(receiverMap)

    /**
     * 订阅的mod数量
     */
    val countMod = receiverMap.size

    // region -- 参数

    /**
     * 获取所属bot
     */
    fun bot(): Bot = bot

    /**
     * mod被订阅的个人+群数量
     */
    infix fun countSub(mod: Int): Int = receiverLock.withLock {
        get(mod)?.let {
            val single = it[SINGLE]?.size ?: 0
            size - 1 + single
        } ?: 0
    }

    /**
     * 清空订阅
     */
    fun clear() = receiverLock.withLock {
        receiverMap.clear()
    }

    /**
     * 记录订阅
     *
     * @param   mod 模组id
     * @param    qq q号（大于0）
     * @param group 群号（大于0）
     */
    fun sub(mod: Int, qq: Long, group: Long = SINGLE) = receiverLock.withLock {
        get(mod)?.let {
            val qs = it[group] ?: mutableSetOf()
            it[group] = qs
            if (qs.add(qq)) {
                logger?.info("新增订阅{$mod:{$group:$qq}}--订阅集[$name]")
            }
        }
    }

    /**
     * 取消订阅
     *
     * @param   mod 模组id
     * @param    qq q号（大于0）
     * @param group 群号（大于0）
     */
    fun unsub(mod: Int, qq: Long, group: Long = SINGLE) = receiverLock.withLock {
        get(mod)?.let {
            it[group]?.let { qs ->
                if (qs.remove(qq)) {
                    logger?.info("取消订阅{$mod:{$group:$qq}}--订阅集[$name]")
                }
            }
        }
    }

    /**
     * 移除群订阅
     *
     * @param   mod 模组id
     * @param group 群号（大于0）
     */
    fun unsubGroup(mod: Int, group: Long) = receiverLock.withLock {
        get(mod)?.remove(group)?.let {
            logger?.info("移除群订阅{$mod:$group}--订阅集[$name]")
        }
    }

    /**
     * 移除mod
     *
     * @param mod 模组id
     */
    infix fun rmMod(mod: Int) = receiverLock.withLock {
        remove(mod)?.let {
            logger?.info("清除mod[$mod]，总mod量：$size--订阅集[$name]")
        }
        Unit// return value
    }

    /**
     * [rmMod]
     */
    operator fun minusAssign(mod: Int) = rmMod(mod)

    // endregion

    // region -- 消费

    /**
     * 遍历个人订阅
     *
     * @param    mod 模组id
     * @param action qq号消费操作
     */
    fun eachSingle(mod: Int, action: (qq: Long) -> Unit) = receiverLock.withLock {
        get(mod)?.get(SINGLE)?.forEach { action(it) }
    }

    /**
     * 遍历群订阅
     *
     * @param    mod 模组id
     * @param action 消费操作
     */
    fun eachGroup(mod: Int, action: (group: Long, member: List<Long>) -> Unit) = receiverLock.withLock {
        get(mod)?.filter {
            it.key != SINGLE
        }?.forEach { (g, m) ->
            action(g, m.toList())
        }
    }

    // endregion
}
