package com.jacky.foundation.taskscheduler

import android.util.Log
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

    private val runningTasks: MutableList<Task<*>> = mutableListOf()

    private val prepareTasks: MutableList<Task<*>> = mutableListOf()

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
        runningTasks.add(task)
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
     */
    fun notifyTaskRemoved(task: Task<*>, callback: TaskHandleCallback) {
        if (task.runningState == Task.RunningState.RUNNING) {
            if (!task.isInterruptable) {
                // 此任务不可以移除哦, 告诉任务管理中心
                callback.taskCantRemove(task)
            } else {
                // FIXME:触发中断取消任务; 如何终止线程池某个任务
                // FIXME:如何知道某个task是被线程池分配到哪个线程执行的
                val ret = executor.remove(task)
                if (ret) {
                    callback.taskRemoved(task)
                } else {
                    callback.taskCantRemove(task)
                }
            }
        } else {
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