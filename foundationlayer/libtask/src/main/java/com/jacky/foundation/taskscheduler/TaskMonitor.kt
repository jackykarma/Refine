package com.jacky.foundation.taskscheduler

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/11, jacky.li, Create file
 * @since 2023/6/11
 * @version v1.0.00
 *
 * 收集各类信息，然后输出给开发者，开发者用于监控。仅在Debug模式下运行。
 *
 * 监控任务的运行情况，防止孵化
 * 1.用于评估任务的优先级是否设置合理、执行顺序是否合理
 * 2.用于评估任务的数量情况、各种等级的任务的数量情况是否合理
 * 3.任务的执行时序是否有问题
 * 4.每个任务的执行耗时情况：单线程的统计应该是准确的
 *
 * Task会给它数据
 * TaskCenter也会给它数据
 * TaskDispatcher也会给它数据
 *
 * 然后由它对数据做一个整理和输出。这就是它的职责。
 */
object TaskMonitor {

    private val taskGroups: MutableMap<String, MutableList<Task<*>>> = mutableMapOf()

    /**
     * 收集任务的执行耗时
     */
    fun <TaskResult> collectTaskCostTime(task: Task<TaskResult>, taskRunCost: Long) {
        TODO("Not yet implemented")
    }

    fun updateTasks(taskGroups: MutableMap<String, MutableList<Task<*>>>) {
        taskGroups.clear()
        taskGroups.putAll(taskGroups)
    }

    /**
     * 向开发者输出监控信息
     */
    fun printMonitorMessage() {
        // 以一种非常易读和直观的方式输出task
    }
}