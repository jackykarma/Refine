package com.jacky.biz_task.gui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jacky.biz_task.vm.Page1ViewModel
import com.jacky.biz_taskmanager.databinding.LayoutPage1Binding
import com.jacky.foundation.log.HiLog
import com.jacky.foundation.taskscheduler.Task
import com.jacky.foundation.taskscheduler.TaskCenter
import java.util.PriorityQueue

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/10, jacky.li, Create file
 * @since 2023/6/10
 * @version v1.0.00
 */
class Page1Fragment : Fragment() {

    private var page1ViewModel: Page1ViewModel? = null

    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutPage1Binding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        page1ViewModel = ViewModelProvider(this)[Page1ViewModel::class.java]
        init()
    }

    private fun init() {
        createPageTasks()
        oneByOneTaskRun()
        // batchTaskRun()
    }

    override fun onPause() {
        super.onPause()
        HiLog.d(TAG, "onPause")
        clearTask()
    }

    override fun onStop() {
        super.onStop()
        HiLog.d(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        HiLog.d(TAG, "onDestroy")
    }

    override fun onResume() {
        super.onResume()
        HiLog.d(TAG, "onResume")
    }

    private lateinit var task1: Task<*>
    private lateinit var task2: Task<*>
    private lateinit var task3: Task<*>
    private lateinit var task4: Task<*>
    private lateinit var task5: Task<*>

    private fun createPageTasks() {
        task1 = object : Task<Unit>(
            groupName = "page1", name = "task1",
            isInterruptable = true, priority = 10
        ) {
            override fun onRunning(): Unit? {
                super.onRunning()
                Thread.sleep(5000)
                return Unit
            }
        }
        task2 = object : Task<Unit>(
            groupName = "page1", name = "task2",
            isInterruptable = false, priority = 2
        ) {
            override fun onRunning(): Unit? {
                super.onRunning()
                Thread.sleep(3000)
                return Unit
            }
        }
        task3 = object : Task<Unit>(
            groupName = "page1", name = "task3",
            isInterruptable = false, priority = 5
        ) {
            override fun onRunning(): Unit? {
                super.onRunning()
                Thread.sleep(10000)
                return Unit
            }
        }
        task4 = object : Task<Unit>(
            groupName = "page1", name = "task4",
            isInterruptable = true, priority = 1
        ) {
            override fun onRunning(): Unit? {
                super.onRunning()
                Thread.sleep(5000)
                return Unit
            }
        }
        task5 = object : Task<Unit>(
            groupName = "page1", name = "task5",
            isInterruptable = true, priority = 1
        ) {
            override fun onRunning(): Unit? {
                super.onRunning()
                Thread.sleep(5000)
                return Unit
            }
        }
    }

    private fun batchTaskRun() {
        // 批量注册，不同的是会在执行前先进行排序; 特点：任务是同一时间产生的
        val queue = PriorityQueue<Task<*>>()
        queue.add(task1)
        queue.add(task2)
        queue.add(task3)
        queue.add(task4)
        queue.add(task5)
        TaskCenter.registerBatch(queue)
    }

    private fun oneByOneTaskRun() {
        // 逐个提交注册，特点：任务的产生是随机的，不是都在同一时间产生
        TaskCenter.register(task1)
        TaskCenter.register(task2)
        TaskCenter.register(task3)
        TaskCenter.register(task4)
        TaskCenter.register(task5)

        // 突然有个任务不需要了，提交注销申请，试试能否停止、中断、取消掉。
        TaskCenter.unregister(task3)
    }

    private fun clearTask() {
        TaskCenter.unregister(task1)
        TaskCenter.unregister(task2)
        TaskCenter.unregister(task3)
        TaskCenter.unregister(task4)
        TaskCenter.unregister(task5)
    }

    companion object {
        private const val TAG = "Page1Fragment"
    }
}