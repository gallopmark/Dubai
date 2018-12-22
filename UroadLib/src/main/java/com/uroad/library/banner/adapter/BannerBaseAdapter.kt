package com.uroad.library.banner.adapter

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

abstract class BannerBaseAdapter(private val context: Context) : RecyclerView.Adapter<BannerBaseAdapter.RecyclerHolder>() {
    private var onItemClickListener: OnItemClickListener? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null
    private var onItemChildClickListener: OnItemChildClickListener? = null
    private var onItemChildLongClickListener: OnItemChildLongClickListener? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(LayoutInflater.from(context).inflate(bindView(viewType), viewGroup, false))
    }

    protected abstract fun bindView(viewType: Int): Int

    inner class RecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        private val holder = SparseArray<View>()

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : View> obtainView(id: Int): T {
            var view = holder.get(id)
            if (view != null) return view as T
            view = itemView.findViewById(id)
            holder.put(id, view)
            return view as T
        }

        fun bindChildClick(id: Int) {
            obtainView<View>(id).setOnClickListener(this)
        }

        /**
         * 子控件绑定局部点击事件
         *
         * @return
         */
        fun bindChildClick(v: View) {
            v.setOnClickListener(this)
        }

        fun bindChildLongClick(id: Int) {
            obtainView<View>(id).setOnLongClickListener(this)
        }

        fun bindChildLongClick(v: View) {
            v.setOnLongClickListener(this)
        }

        /**
         * 文本控件赋值
         */
        fun setText(id: Int, text: CharSequence?) {
            (obtainView<View>(id) as TextView).text = text
        }

        fun setTypeface(id: Int, tf: Typeface) {
            (obtainView<View>(id) as TextView).typeface = tf
        }

        fun setDrawableLeft(id: Int, drawable: Drawable) {
            (obtainView<View>(id) as TextView).setCompoundDrawables(drawable, null, null, null)
        }

        fun setDrawableRight(id: Int, drawable: Drawable) {
            (obtainView<View>(id) as TextView).setCompoundDrawables(null, null, drawable, null)
        }

        fun setTextSize(id: Int, size: Float) {
            (obtainView<View>(id) as TextView).textSize = size
        }

        fun setTextSize(id: Int, unit: Int, size: Float) {
            (obtainView<View>(id) as TextView).setTextSize(unit, size)
        }

        //可以直接引用 R.color.xxx
        fun setTextColor(id: Int, color: Int) {
            (obtainView<View>(id) as TextView).setTextColor(color)
        }

        fun setTextColorRes(id: Int, color: Int) {
            (obtainView<View>(id) as TextView).setTextColor(ContextCompat.getColor(context, color))
        }

        fun setImageResource(id: Int, resId: Int) {
            (obtainView<View>(id) as ImageView).setImageResource(resId)
        }

        fun setVisibility(id: Int, visibility: Int) {
            obtainView<View>(id).visibility = visibility
        }

        fun setBackgroundColor(id: Int, color: Int) {
            obtainView<View>(id).setBackgroundColor(color)
        }

        fun setBackgroundColorRes(id: Int, color: Int) {
            obtainView<View>(id).setBackgroundColor(ContextCompat.getColor(context, color))
        }

        fun setBackgroundResource(id: Int, resId: Int) {
            obtainView<View>(id).setBackgroundResource(resId)
        }

        fun setOnClickListener(id: Int, onClickListener: View.OnClickListener) {
            obtainView<View>(id).setOnClickListener(onClickListener)
        }

        fun setLayoutParams(id: Int, params: ViewGroup.LayoutParams) {
            obtainView<View>(id).layoutParams = params
        }

        fun setEnabled(id: Int, enabled: Boolean) {
            obtainView<View>(id).isEnabled = enabled
        }

        override fun onClick(view: View) {
            if (view.id == this.itemView.id) {
                onItemClickListener?.onItemClick(this@BannerBaseAdapter, this, view, adapterPosition)
            } else if (view.id != this.itemView.id) {
                onItemChildClickListener?.onItemChildClick(this@BannerBaseAdapter, this, view, adapterPosition)
            }
        }

        override fun onLongClick(view: View): Boolean {
            if (view.id == this.itemView.id) {
                onItemLongClickListener?.onItemLongClick(this@BannerBaseAdapter, this, view, adapterPosition)
                return true
            } else if (view.id == this.itemView.id) {
                onItemChildLongClickListener?.onItemChildLongClick(this@BannerBaseAdapter, this, view, adapterPosition)
                return true
            }
            return false
        }
    }

    interface OnItemClickListener {
        fun onItemClick(adapter: BannerBaseAdapter, holder: RecyclerHolder, view: View, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(adapter: BannerBaseAdapter, holder: RecyclerHolder, view: View, position: Int)
    }

    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener
    }

    interface OnItemChildClickListener {
        fun onItemChildClick(adapter: BannerBaseAdapter, holder: RecyclerHolder, view: View, position: Int)
    }

    fun setOnItemChildClickListener(onItemChildClickListener: OnItemChildClickListener) {
        this.onItemChildClickListener = onItemChildClickListener
    }

    interface OnItemChildLongClickListener {
        fun onItemChildLongClick(adapter: BannerBaseAdapter, holder: RecyclerHolder, view: View, position: Int)
    }

    fun setOnItemChildLongClickListener(onItemChildLongClickListener: OnItemChildLongClickListener) {
        this.onItemChildLongClickListener = onItemChildLongClickListener
    }
}

