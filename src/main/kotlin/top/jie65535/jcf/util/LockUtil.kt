package top.jie65535.jcf.util

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * 线程锁工具
 */
class LockUtil<out T>(val state: T, val lock: Lock = ReentrantLock()) {

    inline fun <R> withLock(fn: T.()->R): R {
        try {
            lock.lock()
            return fn(state)
        } finally {
            lock.unlock()
        }
    }
}
