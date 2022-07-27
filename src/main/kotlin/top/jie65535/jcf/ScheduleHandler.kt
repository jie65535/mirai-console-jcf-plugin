package top.jie65535.jcf

import net.mamoe.mirai.Bot
import net.mamoe.mirai.utils.MiraiLogger
import top.jie65535.jcf.util.LockUtil
import java.util.*
import kotlin.concurrent.thread

/**
 * 任务调度
 *
 * @param    bot 每个 bot 维护一个调度器
 * @param logger 日志（默认无）
 */
class ScheduleHandler(
    private val bot: Bot,
    private val logger: MiraiLogger? = null,
) {
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
     * 调度器名称
     */
    private val name = "sch:${bot.id}:" + UUID.randomUUID().toString()

    /**
     * 线程：循环执行核对与调度
     */
    private val loopThread = thread(name = "$name:thr") {
        logger?.info("调度器[$name]启动")
        loop()
    }

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
    private infix fun Calendar.neq(then: Calendar?): Boolean = !eq(then)

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
            Thread.sleep(time)
            true
        } catch (e: Exception) {
            logger?.warning("调度器[$name]异常", e)
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
            if (!thread.delay(1000 * 20)) break

            val now = Calendar.getInstance()
            reSwap(now)
            if (!isThen(now)) continue

            logger?.info("调度器[$name]执行任务...")
            taskLock.withLock {
                // TODO 异步、限时
                for ((id, task) in this) {
                    if (pause) {
                        logger?.info("调度器[$name]执行在任务中暂停")
                        break
                    }
                    else task(id)
                }
            }
        }
        taskLock.withLock { clear() }
        periodLock.withLock { clear() }
        logger?.info("调度器[$name]已关闭")
    }
    // endregion

    // region -- 状态

    /**
     * 暂停调度
     */
    fun pause() {
        pause = true
        logger?.info("暂停调度器[$name]...")
    }

    /**
     * 关闭调度，清理资源
     */
    fun close() {
        pause = true
        loopThread.interrupt()
        taskLock.withLock { clear() }
        periodLock.withLock { clear() }
        logger?.info("已关闭调度器[$name]")
    }

    /**
     * 继续执行调度
     */
    fun rerun(): Boolean {
        pause = false
        logger?.apply {
            if (loopThread.isAlive) info("调度器[$name]正常运行...")
            else warning("调度器[$name]已关闭，无法继续")
        }
        return loopThread.isAlive
    }

    /**
     * 是否暂停
     */
    fun isPause() = pause

    /**
     * 是否可用
     */
    fun isAlive() = loopThread.isAlive

    // endregion

    // region -- 参数

    /**
     * 检查 id 是否已添加
     */
    infix fun haveTask(id: String): Boolean = taskLock.withLock {
        id in this
    }

    /**
     * 添加任务。
     * 不允许覆盖任务；
     * 若要修改任务，需删除后重新添加。
     *
     * @param     id 任务 id
     * @param action 待执行的任务
     * @return       id 是否已添加
     */
    fun addTask(id: String, action: (String) -> Unit): Boolean = taskLock.withLock {
        if (id in this)
            false
        else {
            put(id, action)
            logger?.info("新增任务[$id]，总任务量：$size--调度器[$name]")
            true
        }
    }

    /**
     * 删除任务
     *
     * @param id 任务 id
     */
    infix fun rmTask(id: String) = taskLock.withLock {
        if (id in this) {
            this -= id
            logger?.info("移除任务[$id]，总任务量：$size--调度器[$name]")
        }
    }

    /**
     * 任务数量
     */
    fun taskCount(): Int = taskSet.size

    /**
     * 是否存在于时刻表
     */
    infix fun havePeriod(then: Calendar): Boolean = periodLock.withLock {
        any { it eq then } || then eq swap
    }

    /**
     * 添加时刻。
     * 时刻表中时与分作为唯一检查，
     * 因此若参数所包含的时与分已存在与时刻表，则本次输入被忽视。
     */
    infix fun addPeriod(then: Calendar) = periodLock.withLock {
        if (none { it eq then } && then neq swap) {
            add(then)
            logger?.apply {
                val m = then.get(Calendar.MINUTE)
                val h = then.get(Calendar.HOUR_OF_DAY)
                info("添加时刻[$h:$m]，时刻表长度：${periodList.size}--调度器[$name]")
            }
        }
    }

    /**
     * 移除匹配的时刻
     */
    infix fun rmPeriod(then: Calendar) = periodLock.withLock {
        if (isThen(then)) {
            swap = null
            logger?.apply {
                val m = then.get(Calendar.MINUTE)
                val h = then.get(Calendar.HOUR_OF_DAY)
                info("移除时刻[$h:$m]，时刻表长度：${periodList.size}--调度器[$name]")
            }
        }
    }

    /**
     * 时刻表长度
     */
    fun periodCount(): Int = periodList.size

    // endregion
}
