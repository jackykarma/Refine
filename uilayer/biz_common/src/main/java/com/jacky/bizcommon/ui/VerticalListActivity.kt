package com.jacky.bizcommon.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jacky.bizcommon.R

abstract class VerticalListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    protected lateinit var listAdapter: VerticalListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.biz_common_activity_vertical_list)
        recyclerView = findViewById(R.id.rv)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        listAdapter = VerticalListAdapter()
        recyclerView.adapter = listAdapter
    }

    inner class VerticalListAdapter : RecyclerView.Adapter<VerticalListAdapter.ViewHolder>() {

        var itemClickListener: ItemClickListener? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.biz_common_rv_list_item, parent, false)
            itemView.setOnClickListener { itemClickListener?.onItemClick(itemView, itemView.tag as Int) }
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.itemView.findViewById<Button>(R.id.btn).text = getListData()[position].first
            holder.itemView.tag = position
        }

        override fun getItemCount(): Int {
            return getListData().size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

    abstract fun getListData(): MutableList<Pair<String, Any?>>

    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.onBackPressed()",
        "com.jacky.bizcommon.ui.VerticalListActivity"
    )
    )
    override fun onBackPressed() {
        // 堆栈中Fragment的数量
        val fragmentCountInBackStack = supportFragmentManager.backStackEntryCount
        if (fragmentCountInBackStack > 0) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    protected fun showFragment(fragment: Fragment) {
        // 插件apk用的fg_container资源是来自biz_common，该组件属于base apk中
        supportFragmentManager.beginTransaction()
            .replace(R.id.fg_container, fragment).addToBackStack(fragment.javaClass.name)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN) // onCreateAnimation/onCreateAnimator动画接收的transit参数
            .commit()
    }
}