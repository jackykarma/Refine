package com.jacky.foundation.taskscheduler;

import android.util.Log
import java.util.concurrent.PriorityBlockingQueue

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 *
 * @author jacky.li
 * 2023/6/10, jacky.li, Create file
 * @version v1.0.00
 * @since 2023/6/10
 *
 * 严格控制任务执行的调度器
 * 1. HandlerThread: 更为可行,但只适合单线程。还需要创建和退出，似乎不是特别好的方案。
 * 2. 自己实现，用信号量或CountDownLatch或其他方法来控制。不是很好的方案。要自己实现线程复用的问题，难度大，复杂。
 * 3. 线程池实现：单线程、多线程都可以。但取消中断某个线程任务，还需要测试验证。
 */
public class SequenceTaskDispatcher {

    private val runningTasks: PriorityBlockingQueue<Task<*>> = PriorityBlockingQueue<Task<*>>()

    private val prepareTasks: PriorityBlockingQueue<Task<*>> = PriorityBlockingQueue<Task<*>>()

    fun notifyTaskAdded(task: Task<*>) {
        // 加入后自动排序
        prepareTasks.add(task)
        run()
    }

    private var thread: Thread? = null

    fun run() {
        val task = prepareTasks.peek()
        // FIXME:线程的复用处理不好搞啊，必须使用线程池，或者自己实现一套复用线程的逻辑，何必？
        if (thread == null) {
            thread = Thread(task)
        }
        // 线程未结束
        if (thread?.state != Thread.State.TERMINATED) {
            // 放到准备队列中
            prepareTasks.add(task)
        } else {
            runningTasks.add(task)
            thread?.start()
        }
    }

    fun notifyTaskRemoved(task: Task<*>, callback: TaskHandleCallback) {

    }

    fun pause() {
        Log.d(TAG, "pause")
    }

    fun resume() {
        Log.d(TAG, "resume")
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

    companion object {
        private const val TAG = "SequenceTaskDispatcher"
    }
}
