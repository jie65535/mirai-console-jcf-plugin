package top.jie65535.jcf

import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.ConsoleCommandSender.name
import net.mamoe.mirai.utils.MiraiLogger
import top.jie65535.jcf.util.LockUtil
import java.util.*
import kotlin.concurrent.thread

/**
 * 任务调度
 *
 * @property    bot 每个 bot 维护一个调度器
 * @property logger 日志
 */
class ScheduleHandler(private val bot: Bot, private val logger: MiraiLogger) {
    // region -- 常量、变量
    /**
     * 任务表
     */
    private val taskSet = mutableMapOf<String, (String) -> Unit>()

    /**
     * 时刻表；
     * 仅时与分用于校对
     */
    private val periodList = mutableListOf<Calendar>()

    /**
     * 线程锁：时刻表
     */
    private val periodLock = LockUtil(periodList)

    /**
     * 线程锁：任务表
     */
    private val taskLock = LockUtil(taskSet)

    // 变量

    /**
     * 标识：暂停校对和调度
     */
    private var pause = true

    /**
     * 暂存
     */
    private var swap: Calendar? = null

    /**
     * 线程：循环执行核对与调度
     */
    private val loopThread = thread(name = "schedule:${bot.id}:${bot.nick}") { loop() }

    // endregion

    // region -- 校对与调度

    /**
     * 比较时与分是否相等
     */
    private infix fun Calendar.eq(then: Calendar?): Boolean =
        if (then == null)
            false
        else get(Calendar.MINUTE) == then.get(Calendar.MINUTE)
            && get(Calendar.HOUR_OF_DAY) == then.get(Calendar.HOUR_OF_DAY)

    /**
     * 取反 [eq] 函数
     */
    private infix fun Calendar.neq(then: Calendar): Boolean = !eq(then)

    /**
     * 暂存的时刻重入时刻表
     *
     * @param ref 用以校准的指标
     */
    private fun reSwap(ref: Calendar) {
        if (ref eq swap) {
            periodLock.withLock { add(swap!!) }
            swap = null
        }
    }

    /**
     * 时辰到？
     *
     * @param then 校对时刻
     */
    private fun isThen(then: Calendar): Boolean = periodLock.withLock {
        var isNow = false
        val ii = periodList.iterator()
        while (ii.hasNext()) {
            val time = ii.next()
            if (then eq time) {
                isNow = true
                swap = time
                ii.remove()
                break
            }
        }
        isNow// return value
    }

    /**
     * 等待
     *
     * @receiver   当前线程
     * @param time 等待时间（毫秒）
     * @return     是否继续
     */
    private fun Thread.delay(time: Long): Boolean =
        try {
            Thread.sleep(1000 * 20)
            true
        } catch (_: Exception) {
            false
        }

    /**
     * 循环执行校准与调度
     */
    private fun loop() {
        val thread = Thread.currentThread()
        while (!thread.isInterrupted) {
            if (pause) continue
            if (periodList.isEmpty()) continue
            if (!thread.delay(1000 * 20))
                break

            val now = Calendar.getInstance()
            reSwap(now)
            if (!isThen(now)) continue

            taskLock.withLock {
                // TODO 异步、限时
                forEach { (id, task) -> task(id) }
            }
        }// while
        logger.info("线程[$name]已结束")
        // TODO clean
    }
    // endregion

    // region -- TODO 参数与状态

    // endregion
}
