package com.jacky.foundation.taskscheduler

import android.util.Log
import com.jacky.foundation.log.HiLog
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/10, jacky.li, Create file
 * @since 2023/6/10
 * @version v1.0.00
 *
 * 任务调度器
 */
class TaskDispatcher(private val runMode: RunMode) {

    private val executor: PauseableThreadPoolExecutor by lazy {
        if (runMode == RunMode.SINGLE_THREAD) {
            createSingleThreadPoolExecutor()
        } else {
            createMultiThreadPoolExecutor()
        }
    }

    private fun createSingleThreadPoolExecutor(): PauseableThreadPoolExecutor {
        val corePoolSize = 1
        val maxPoolSize = 1
        val keepAliveTime = 30L
        // 优先级队列，根据Task的priority进行排序
        val blockingQueue = PriorityBlockingQueue<Task<*>>()
        val threadSeqNumber = AtomicInteger()
        return PauseableThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            keepAliveTime,
            TimeUnit.SECONDS,
            blockingQueue
        ) { runnable ->
            val thread = Thread(runnable)
            thread.name = "SingleThreadTaskExecutor-$threadSeqNumber"
            thread
        }
    }

    private fun createMultiThreadPoolExecutor(): PauseableThreadPoolExecutor {
        val cpuCount = Runtime.getRuntime().availableProcessors()
        val corePoolSize = cpuCount + 1
        val maxPoolSize = cpuCount * 2 + 1
        val keepAliveTime = 30L
        val blockingQueue = LinkedBlockingQueue<Task<*>>()
        val threadSeqNumber = AtomicInteger()
        return PauseableThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            keepAliveTime,
            TimeUnit.SECONDS,
            blockingQueue
        ) { runnable ->
            val thread = Thread(runnable)
            thread.name = "MultiThreadTaskExecutor-$threadSeqNumber"
            thread
        }
    }

    /**
     * 通知有新的任务进来了
     * 允许重复添加同一个任务，执行多次嘛
     */
    fun notifyTaskAdded(task: Task<*>) {
        HiLog.d(TAG, "notifyTaskAdded $task")
        executor.execute(task)
    }

    /**
     * 通知调度器有任务需要被移除
     */
    fun notifyTaskGroupRemoved(tasks: List<Task<*>>?, callback: TaskHandleCallback) {
        tasks?.forEach { task: Task<*> ->
            notifyTaskRemoved(task, callback)
        }
    }

    /**
     * 通知调度器有任务需要被移除
     * 调度器只是尝试看看能否移除，这个是不能保证的
     */
    fun notifyTaskRemoved(task: Task<*>, callback: TaskHandleCallback) {
        if (task.runningState == Task.RunningState.RUNNING) {
            if (!task.isInterruptable) {
                // 此任务非常重要，不可以移除哦, 告知到任务管理中心
                callback.taskCantRemove(task)
            } else {
                // FIXME:还未发现线程池中的指定任务的中断方法
            }
        } else if (task.runningState == Task.RunningState.CREATED){
            // TODO:注意，移除只能移除还在队列中的任务，如果任务已经从队列中取出交给线程去运行了，那么remove总是返回false，
            //  这个remove也无法中断某个指定任务的执行。
            val ret = executor.remove(task)
            HiLog.d(TAG, "executor remove task ret:$ret")
            if (ret) {
                callback.taskRemoved(task)
            } else {
                callback.taskCantRemove(task)
            }
            callback.taskRemoved(task)
        } else if (task.runningState == Task.RunningState.CANCELED ||
                task.runningState == Task.RunningState.FINISHED) {
            // 已经执行完的任务或是被取消的任务都是可以直接移除的
            callback.taskRemoved(task)
        }
    }

    fun pause() {
        Log.d(TAG, "pause")
        executor.pause()
    }

    fun resume() {
        Log.d(TAG, "resume")
        executor.resume()
    }

    interface TaskHandleCallback {
        /**
         * 此任务不可移除
         */
        fun taskCantRemove(task: Task<*>) {}

        /**
         * 此任务已移除
         */
        fun taskRemoved(task: Task<*>) {}

        /**
         * 任务组已全部移除
         */
        fun taskGroupRemoved(groupName: String) {}
    }

    /**
     * 运行模式
     */
    enum class RunMode {
        /**
         * 单线程模式
         */
        SINGLE_THREAD,

        /**
         * 多线程并发模式
         */
        MULTI_THREAD
    }

    companion object {
        private const val TAG = "TaskDispatcher"
    }
}