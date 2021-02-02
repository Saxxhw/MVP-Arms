package com.zkyc.arms.base.adapter.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T, VH : RecyclerView.ViewHolder>(@LayoutRes private val itemLayoutId: Int) :
    RecyclerView.Adapter<VH>() {

    var onItemClickListener: OnItemClickListener? = null

    private val mList = mutableListOf<T>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context).inflate(itemLayoutId, parent, false)
        return createViewHolder(itemView)
    }

    abstract fun createViewHolder(itemView: View): VH

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.setOnClickListener { onItemClickListener?.onItemClick(this, it, position) }
    }

    override fun getItemCount(): Int {
        return count
    }

    val count get() = mList.size

    fun addItem(data: T) {
        mList.add(data)
        notifyDataSetChanged()
    }

    fun addItems(collection: Collection<T>) {
        mList.addAll(collection)
        notifyDataSetChanged()
    }

    fun setNewList(collection: Collection<T>) {
        mList.clear()
        mList.addAll(collection)
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        mList.removeAt(position)
        notifyDataSetChanged()
    }

    fun clear() {
        mList.clear()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): T {
        return mList[position]
    }

    fun getList(): List<T> {
        return mList
    }

    interface OnItemClickListener {
        fun onItemClick(adapter: BaseAdapter<*, *>, v: View, position: Int)
    }
}