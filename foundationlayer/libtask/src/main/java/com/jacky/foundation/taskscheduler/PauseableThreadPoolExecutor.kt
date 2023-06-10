package com.jacky.foundation.taskscheduler

import android.util.Log
import java.util.concurrent.BlockingQueue
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description 具备暂停、恢复功能的线程池
 * @author jacky.li
 * 2023/6/10, jacky.li, Create file
 * @since 2023/6/10
 * @version v1.0.00
 *
 * 钩子方法，给线程池加点料
 * 1.每个任务执行前后
 * 2.日志、统计
 */
class PauseableThreadPoolExecutor(
    corePoolSize: Int,
    maxPoolSize: Int,
    keepAliveTime: Long,
    timeUnit: TimeUnit,
    blockingQueue: BlockingQueue<out Runnable>,
    threadFactory: ThreadFactory,
    rejectedExecutionHandler: RejectedExecutionHandler
) : ThreadPoolExecutor(
    corePoolSize,
    maxPoolSize,
    keepAliveTime,
    timeUnit,
    blockingQueue as BlockingQueue<Runnable>,
    threadFactory,
    rejectedExecutionHandler
) {

    constructor(
        corePoolSize: Int,
        maxPoolSize: Int,
        keepAliveTime: Long,
        timeUnit: TimeUnit,
        blockingQueue: BlockingQueue<out Runnable>,
        threadFactory: ThreadFactory,
    ) : this(corePoolSize, maxPoolSize, keepAliveTime, timeUnit, blockingQueue, threadFactory, object : RejectedExecutionHandler {
        override fun rejectedExecution(r: Runnable?, executor: ThreadPoolExecutor?) {
            Log.d(TAG, "rejectedExecution")
        }
    })

    /**
     * 线程池是否处于暂停状态
     */
    private var isPaused: Boolean = false

    private var lock: Lock = ReentrantLock()

    /**
     * 每次使用都会创建一个新的ConditionObject对象
     */
    private var pauseCondition: Condition = lock.newCondition()

    fun pause() {
        lock.lock()
        try {
            isPaused = true
        } finally {
            lock.unlock()
        }
    }

    fun resume() {
        lock.lock()
        try {
            isPaused = false
        } finally {
            lock.unlock()
        }
    }

    override fun beforeExecute(t: Thread?, r: Runnable?) {
        if (isPaused) {
            lock.lock()
            try {
                // 线程池暂停了，那么任务应该在此等待
                pauseCondition.await()
            } finally {
                lock.unlock()
            }
            return
        }
    }

    override fun afterExecute(r: Runnable?, t: Throwable?) {
        super.afterExecute(r, t)
    }

    companion object {
        private const val TAG = "PauseableThreadPoolExec"
    }
}