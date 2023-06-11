package com.jacky.foundation.taskscheduler

import androidx.annotation.IntRange
import com.jacky.foundation.log.HiLog

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description 线程任务对象抽象
 * @author jacky.li
 * 2023/6/10, jacky.li, Create file
 * @since 2023/6/10
 * @version v1.0.00
 */
abstract class Task<TaskResult> constructor(

    /**
     * 任务的分组名称
     */
    val groupName: String = DEFAULT_TASK_GROUP_NAME,

    /**
     * 任务的名称
     */
    val name: String = DEFAULT_TASK_NAME,

    /**
     * 任务的重要性评估: 任务是否可中断/停止/取消
     */
    val isInterruptable: Boolean = true,

    /**
     * 任务的紧急性评估：优先级
     * 高优先级范围：0-10
     * 低优先级范围：11-100
     *
     * 优先级可以动态调整
     */
    @IntRange(from = 0, to = 100) var priority: Int = DEFAULT_PRIORITY
) : Runnable, Comparable<Task<TaskResult>> {

    /**
     * 任务运行状态
     */
    var runningState = RunningState.CREATED
        set(value) {
            if (field != value) field = value
        }
        get() = field

    /**
     * 任务的ID号, 后续系统分配
     */
    private var id: Int = 0
        set(value) {
            if (field != value) field = value
        }

    private var taskResult: TaskResult? = null

    /**
     * 加上final为了禁止外部复写修改
     */
    final override fun run() {
        onStart()
        try {
            if (Thread.currentThread().isInterrupted) {
                onCancelled()
                return
            }
            // onRunning是任务执行的函数体，它可能会有返回结果
            taskResult = onRunning()
        } catch (e: InterruptedException) {
            onCancelled()
            return
        }
        onFinished(taskResult)
    }

    /**
     * 任务开始执行
     * 可以复写此方法，以在任务真正执行前做些骚操作
     */
    open fun onStart() {
        HiLog.d(TAG, "$this onStart")
    }

    /**
     * 任务执行过程中
     */
    open fun onRunning(): TaskResult? {
        HiLog.d(TAG, "$this onRunning")
        runningState = RunningState.RUNNING
        return taskResult
    }

    /**
     * 任务执行完成
     * 可以复写此方法，以在任务执行完成后做些骚操作
     */
    open fun onFinished(taskResult: TaskResult?) {
        runningState = RunningState.FINISHED
        HiLog.d(TAG, "$this onFinished")
    }

    /**
     * 任务被取消
     * 可以复写此方法，以在任务被取消后做些骚操作, 比如对异常中断后的处理与现场恢复。
     */
    open fun onCancelled() {
        runningState = RunningState.CANCELED
        HiLog.d(TAG, "$this onCancelled")
    }

    override fun compareTo(other: Task<TaskResult>): Int {
        return if (this.priority < other.priority) -1
        else if (this.priority > other.priority) 1
        else 0
    }

    override fun toString(): String {
        return "Task(groupName='$groupName', name='$name', isInterruptable=$isInterruptable, priority=$priority)"
    }


    enum class RunningState {
        /**
         * 任务创建的初始状态，还未开始运行
         */
        CREATED,

        /**
         * 任务在运行中
         */
        RUNNING,

        /**
         * 任务正常结束
         */
        FINISHED,

        /**
         * 任务被取消或中断了
         */
        CANCELED
    }

    companion object {

        private const val TAG = "Task"

        private const val DEFAULT_TASK_NAME = "UNDEFINED"

        private const val DEFAULT_TASK_GROUP_NAME = "UNDEFINED"

        /**
         * 最低优先级
         */
        private const val LOWEST_PRIORITY = 100

        /**
         * 最高优先级
         */
        private const val HIGHEST_PRIORITY = 0

        /**
         * 默认优先级设置为最低
         */
        private const val DEFAULT_PRIORITY = LOWEST_PRIORITY

    }
}