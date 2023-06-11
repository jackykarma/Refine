package com.jacky.foundation.taskscheduler

import com.jacky.foundation.log.HiLog
import java.util.PriorityQueue

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/10, jacky.li, Create file
 * @since 2023/6/10
 * @version v1.0.00
 *
 * 任务中心
 *
 * 问题：任务管理的时候，是需要知道任务的状态的，比如任务是否在运行中，而这个信息却只有调度器知道。这个信息该如何同步？
 */
object TaskCenter {

    private const val TAG = "TaskCenter"

    /**
     * 任务调度器
     */
    private val taskDispatcher = TaskDispatcher(TaskDispatcher.RunMode.SINGLE_THREAD)

    /**
     * 记录所有的任务
     * 任务分组-分组下任务列表的映射表
     */
    private val taskGroups = mutableMapOf<String, MutableList<Task<*>>>()

    private val highPriTasks = taskGroups.map { }

    private val lowPriTasks = taskGroups.map { }

    /**
     * 统计有所已经运行的任务, 按照分组来统计
     */
    private val runFinishedTasks: MutableList<Task<*>>
        get() {
            return mutableListOf()
        }

    /**
     * 注册任何任务对象到任务中心
     */
    @Synchronized
    fun register(task: Task<*>) {
        HiLog.d(TAG, "register: $task")
        taskGroups[getTaskGroup(task)]?.add(task)
        taskDispatcher.notifyTaskAdded(task)
    }

    @Synchronized
    fun registerBatch(tasks: PriorityQueue<Task<*>>) {
        HiLog.d(TAG, "registerBatch: $tasks")
        // 排序
        tasks.forEach {
            register(it)
        }
    }

    /**
     * 注销任务
     */
    @Synchronized
    fun unregister(task: Task<*>) {
        HiLog.d(TAG, "unregister $task")
        taskDispatcher.notifyTaskRemoved(task, object : TaskDispatcher.TaskHandleCallback {
            override fun taskCantRemove(task: Task<*>) {
                HiLog.w(TAG, "taskCantRemove $task")
            }

            override fun taskRemoved(task: Task<*>) {
                HiLog.d(TAG, "taskRemoved $task")
                taskGroups[getTaskGroup(task)]?.remove(task)
            }
        })
    }

    /**
     * 暂停任务中心所有任务
     * 似乎没有什么意义
     */
    fun pause() {
        taskDispatcher.pause()
    }

    /**
     * 恢复任务中心所有任务运行
     * 似乎没有什么意义
     */
    fun resume() {
        taskDispatcher.resume()
    }

    /**
     * 注销一个任务组
     */
    @Synchronized
    fun unregister(groupName: String) {
        taskDispatcher.notifyTaskGroupRemoved(taskGroups[groupName],
            object : TaskDispatcher.TaskHandleCallback {
                override fun taskCantRemove(task: Task<*>) {
                    HiLog.w(TAG, "taskCantRemove task:$task")
                }

                override fun taskRemoved(task: Task<*>) {
                    HiLog.d(TAG, "taskRemoved task:$task")
                    taskGroups[getTaskGroup(task)]?.remove(task)
                }
            })
    }

    /**
     * 注销所有不再需要的Task.
     * 当一个页面进入后台时，它的任务不应该再执行了。
     *
     * 场景：从一个页面进入另外一个页面，当前页面进入后台，其他页面进入前台
     */
    @Synchronized
    fun unregisterAllNoNeedTask() {
        taskGroups.forEach { (groupName, _) ->
            unregister(groupName)
        }
    }

    private fun getTaskGroup(task: Task<*>): String {
        return task.groupName
    }
}