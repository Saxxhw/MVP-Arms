package com.zkyc.arms.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.core.content.res.use
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fondesa.recyclerviewdivider.dividerBuilder
import com.zkyc.arms.R
import com.zkyc.arms.base.adapter.recyclerview.BaseAdapter
import com.zkyc.arms.feature.PicturePreviewActivity
import com.zkyc.arms.glide.load

class NineGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr), BaseAdapter.OnItemClickListener {

    companion object {
        private const val DEFAULT_EDITABLE = false
        private const val DEFAULT_MAX_ITEM_COUNT = 9
        private const val DEFAULT_ITEM_SPAN_COUNT = 3
        private const val DEFAULT_SPACING = 4
    }

    private var mEditable: Boolean = DEFAULT_EDITABLE
    private var mMaxItemCount: Int = DEFAULT_MAX_ITEM_COUNT
    private var mItemSpanCount: Int = DEFAULT_ITEM_SPAN_COUNT
    private var mSpacing: Int = DEFAULT_SPACING

    // 适配器
    private val mAdapter: PicturesAdapter

    // 添加按钮点击监听
    var onAddClickListener: OnAddClickListener? = null

    init {
        // 初始化自定义参数
        context.obtainStyledAttributes(attrs, R.styleable.NineGridView).use {
            mEditable = it.getBoolean(R.styleable.NineGridView_ngv_editable, DEFAULT_EDITABLE)
            mMaxItemCount = it.getInt(R.styleable.NineGridView_ngv_maxItemCount, DEFAULT_MAX_ITEM_COUNT)
            mMaxItemCount = it.getInt(R.styleable.NineGridView_ngv_maxItemCount, DEFAULT_MAX_ITEM_COUNT)
            mItemSpanCount = it.getInt(R.styleable.NineGridView_ngv_itemSpanCount, DEFAULT_ITEM_SPAN_COUNT)
        }
        // 展示网格布局
        layoutManager = GridLayoutManager(context, mItemSpanCount)
        // 绑定适配器
        mAdapter = PicturesAdapter().apply {
            maxItemCount = mMaxItemCount
            editable = mEditable
            onItemClickListener = this@NineGridView
        }
        adapter = mAdapter
        // 添加分割线
        context.dividerBuilder().asSpace().size(mSpacing, TypedValue.COMPLEX_UNIT_DIP).build().addTo(this)
    }

    override fun onItemClick(adapter: BaseAdapter<*, *>, v: View, position: Int) {
        if (mEditable && position >= adapter.count) {
            onAddClickListener?.onAddClick()
        } else {
            PicturePreviewActivity.startWith(context, mAdapter.getList(), position)
        }
    }

    val missingCount: Int
        get() = mMaxItemCount - mAdapter.count

    fun addData(data: String?) {
        if (data.isNullOrEmpty()) return
        mAdapter.addItem(data)
    }

    fun addData(list: List<String>?) {
        if (list.isNullOrEmpty()) return
        mAdapter.addItems(list)
    }

    fun setNewList(list: List<String>?) {
        if (list.isNullOrEmpty()) return
        mAdapter.setNewList(list)
    }

    fun setEditable(editable: Boolean) {
        mAdapter.editable = editable
    }

    fun setMaxItemCount(count: Int) {
        mAdapter.maxItemCount = count
    }

    /**
     * 适配器
     */
    private class PicturesAdapter : BaseAdapter<String, PicturesAdapter.ViewHolder>(R.layout.widget_item_nine_grid_view) {

        var maxItemCount: Int = 9

        var editable: Boolean = false
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun getItemCount(): Int {
            if (editable && count < maxItemCount) {
                return count + 1
            }
            return super.getItemCount()
        }

        override fun createViewHolder(itemView: View) = ViewHolder(itemView)

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            // 运行编辑
            if (editable) {
                // 已添加图片数量未达上限
                if (count < maxItemCount) {
                    // 展示已添加图片
                    if (position < count) {
                        holder.ivDelete.visibility = View.VISIBLE
                        holder.ivImage.load(getItem(position))
                    }
                    // 展示添加图片按钮
                    else {
                        holder.ivDelete.visibility = View.GONE
                        holder.ivImage.load(R.drawable.ngv_ic_plus)
                    }
                }
                // 图片数量达到上限
                else {
                    holder.ivDelete.visibility = View.VISIBLE
                    holder.ivImage.load(getItem(position))
                }
            }
            // 禁止编辑
            else {
                holder.ivDelete.visibility = View.GONE
                holder.ivImage.load(getItem(position))
            }
            // 监听事件
            holder.ivDelete.setOnClickListener { removeAt(position) }
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val ivDelete: ImageView = itemView.findViewById(R.id.iv_delete)
            var ivImage: ImageView = itemView.findViewById(R.id.iv_image)
        }
    }

    /**
     * 接口
     */
    interface OnAddClickListener {
        fun onAddClick()
    }
}