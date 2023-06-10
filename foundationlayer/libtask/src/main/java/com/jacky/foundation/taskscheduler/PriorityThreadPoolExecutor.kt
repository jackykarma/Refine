package com.jacky.foundation.taskscheduler;

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.BlockingQueue
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

/**
 * 优先级线程池执行器
 *
 * 支持按任务的优先级去执行,
 * 支持线程池暂停.恢复(批量文件下载，上传) ，
 * 异步结果主动回调主线程
 * todo 线程池能力监控,耗时任务检测,定时,延迟,
 */
object PriorityThreadPoolExecutor {

    private const val TAG: String = "PriorityThreadPoolExecutor"

    /**
     * 线程池是否暂停了
     */
    private var isPaused: Boolean = false

    private var executor: ThreadPoolExecutor

    /**
     * 可重入锁
     */
    private var lock: ReentrantLock = ReentrantLock()

    private var pauseCondition: Condition = lock.newCondition()

    /**
     * 主线程Handler对象
     */
    private val mainHandler = Handler(Looper.getMainLooper());

    init {
        val cpuCount = Runtime.getRuntime().availableProcessors()
        // 来自《Java并发编程-实战》中的建议
        val corePoolSize = cpuCount + 1
        val maxPoolSize = cpuCount * 2 + 1
        // 创建一个优先级阻塞队列
        val blockingQueue: PriorityBlockingQueue<out Runnable> = PriorityBlockingQueue()

        val threadSeqNumber = AtomicInteger()
        val threadFactory = ThreadFactory {
            val thread = Thread(it)
            thread.name = "priority-executor-" + threadSeqNumber.getAndIncrement()
            // thread对象是工厂创建的返回值
            return@ThreadFactory thread
        }

        executor = object : ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            30L,
            TimeUnit.SECONDS,
            blockingQueue as BlockingQueue<Runnable>,
            threadFactory
        ) {
            override fun beforeExecute(t: Thread?, r: Runnable?) {
                if (isPaused) {
                    lock.lock()
                    try {
                        pauseCondition.await()
                    } finally {
                        lock.unlock()
                    }
                }
            }

            override fun afterExecute(r: Runnable?, t: Throwable?) {
                //监控线程池耗时任务,线程创建数量,正在运行的数量
                Log.e(TAG, "已执行完的任务的优先级是：" + (r as PriorityRunnable).priority)
            }
        }
    }

    fun execute(@androidx.annotation.IntRange(from = 0, to = 10) priority: Int = 0, runnable: Runnable) {
        executor.execute(PriorityRunnable(priority, runnable))
    }

    abstract class HiExecutor<T> : Runnable {
        override fun run() {
            mainHandler.post { onPrepare() }

            val t: T? = onBackground()

            //移除所有消息.防止需要执行onCompleted了，onPrepare还没被执行，那就不需要执行了
            mainHandler.removeCallbacksAndMessages(null)
            mainHandler.post { onCompleted(t) }
        }

        open fun onPrepare() {
            //转菊花
        }

        abstract fun onBackground(): T?
        abstract fun onCompleted(t: T?)
    }

    class PriorityRunnable(val priority: Int, private val runnable: Runnable) : Runnable,
        Comparable<PriorityRunnable> {
        override fun compareTo(other: PriorityRunnable): Int {
            return if (this.priority < other.priority) 1 else if (this.priority > other.priority) -1 else 0
        }

        override fun run() {
            runnable.run()
        }

    }


    fun pause() {
        lock.lock()
        try {
            isPaused = true
            Log.e(TAG, "hiExecutor is paused")
        } finally {
            lock.unlock()
        }
    }

    fun resume() {
        lock.lock()
        try {
            isPaused = false
            pauseCondition.signalAll()
        } finally {
            lock.unlock()
        }
        Log.e(TAG, "hiExecutor is resumed")
    }
}