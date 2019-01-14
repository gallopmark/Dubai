package com.uroad.dubai.common

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.uroad.glidev4.GlideV4


abstract class BaseRecyclerAdapter(private val context: Context) : RecyclerView.Adapter<BaseRecyclerAdapter.RecyclerHolder>() {

    private var onItemClickListener: OnItemClickListener? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null
    private var onItemChildClickListener: OnItemChildClickListener? = null
    private var onItemChildLongClickListener: OnItemChildLongClickListener? = null

    abstract fun bindView(viewType: Int): Int

    open fun getItem(position: Int): Any? {
        return null
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewtype: Int): RecyclerHolder {
        return RecyclerHolder(LayoutInflater.from(context).inflate(bindView(viewtype), viewGroup, false))
    }

    inner class RecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        private var holder: SparseArray<View> = SparseArray()

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        /**
         * 获取子控件
         *
         * @param id
         * @param <T>
         * @return
        </T> */
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
         * @param v
         * @return
         */
        fun bindChildClick(v: View?) {
            v?.setOnClickListener(this)
        }

        fun bindChildLongClick(id: Int) {
            obtainView<View>(id).setOnLongClickListener(this)
        }

        fun bindChildLongClick(v: View?) {
            v?.setOnLongClickListener(this)
        }

        /**
         * 文本控件赋值
         *
         * @param id
         * @param text
         */
        fun setText(id: Int, text: CharSequence?) {
            obtainView<TextView>(id).text = text
        }

        fun setTypeface(id: Int, tf: Typeface) {
            obtainView<TextView>(id).typeface = tf
        }

        fun setDrawableLeft(id: Int, drawable: Drawable?) {
            obtainView<TextView>(id).setCompoundDrawables(drawable, null, null, null)
        }

        fun setDrawableRight(id: Int, drawable: Drawable?) {
            obtainView<TextView>(id).setCompoundDrawables(null, null, drawable, null)
        }

        fun setTextSize(id: Int, size: Float) {
            obtainView<TextView>(id).textSize = size
        }

        fun setTextSize(id: Int, unit: Int, size: Float) {
            obtainView<TextView>(id).setTextSize(unit, size)
        }

        //可以直接引用 R.color.xxx
        fun setTextColor(id: Int, color: Int) {
            obtainView<TextView>(id).setTextColor(color)
        }

        fun setTextColorRes(id: Int, color: Int) {
            obtainView<TextView>(id).setTextColor(ContextCompat.getColor(context, color))
        }

//        fun displayImage(id: Int, url: String?) {
//            val imageView = obtainView<ImageView>(id)
//            ImageLoaderV4.getInstance().displayImage(context, url, imageView)
//        }
//
//        fun displayImage(id: Int, url: String?, defRes: Int) {
//            val imageView = obtainView<ImageView>(id)
//            ImageLoaderV4.getInstance().displayImage(context, url, imageView, defRes)
//        }
//
//        fun displayImage(id: Int, url: String?, defRes: Int, transformations: BitmapTransformation) {
//            val imageView = obtainView<ImageView>(id)
//            ImageLoaderV4.getInstance().displayImage(context, url, imageView, defRes, transformations)
//        }
//
//        fun displayImageThumbnail(id: Int, url: String?, thumbUrl: String?, thumbnailSize: Int) {
//            val imageView = obtainView<ImageView>(id)
//            ImageLoaderV4.getInstance().displayImageThumbnail(context, url, thumbUrl, thumbnailSize, imageView)
//        }

        fun setImageResource(id: Int, resId: Int) {
            obtainView<ImageView>(id).setImageResource(resId)
        }

        fun displayImage(id: Int, url: String?) {
            val imageView = obtainView<ImageView>(id)
            GlideV4.getInstance().displayImage(context, url, imageView)
        }

        fun displayImage(id: Int, url: String?, defRes: Int) {
            val imageView = obtainView<ImageView>(id)
            GlideV4.getInstance().displayImage(context, url, imageView, defRes)
        }

        fun displayImage(id: Int, url: String?, defRes: Int, transformations: BitmapTransformation) {
            val imageView = obtainView<ImageView>(id)
            GlideV4.getInstance().displayImage(context, url, imageView, defRes, transformations)
        }

        fun setVisibility(id: Int, visibility: Int) {
            obtainView<View>(id).visibility = visibility
        }

        fun setVisibility(id: Int, visibile: Boolean) {
            obtainView<View>(id).visibility = if (visibile) View.VISIBLE else View.GONE
        }

        fun setInVisibility(id: Int) {
            obtainView<View>(id).visibility = View.INVISIBLE
        }

        fun isVisibility(id: Int): Boolean {
            val view = obtainView<View>(id)
            if (view.visibility == View.VISIBLE) return true
            return false
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

        fun setChecked(id: Int, checked: Boolean) {
            if (obtainView<View>(id) is CheckBox) {
                (obtainView<View>(id) as CheckBox).isChecked = checked
            } else if (obtainView<View>(id) is RadioButton) {
                (obtainView<View>(id) as RadioButton).isChecked = checked
            } else {
                (obtainView<View>(id) as Checkable).isChecked = checked
            }
        }

        fun setProgress(id: Int, progress: Int) {
            obtainView<ProgressBar>(id).progress = progress
        }

        fun setProgress(id: Int, progress: Int, max: Int) {
            obtainView<ProgressBar>(id).apply {
                this.progress = progress
                this.max = max
            }
        }

        fun setMax(id: Int, max: Int) {
            obtainView<ProgressBar>(id).max = max
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

        override fun onLongClick(v: View): Boolean {
            if (onItemLongClickListener != null && v.id == this.itemView.id) {
                onItemLongClickListener?.onItemLongClick(this@BaseRecyclerAdapter, this, v, adapterPosition)
                return true
            } else if (onItemChildLongClickListener != null && v.id != this.itemView.id) {
                onItemChildLongClickListener?.onItemChildLongClick(this@BaseRecyclerAdapter, this, v, adapterPosition)
                return true
            }
            return false
        }

        override fun onClick(v: View) {
            if (onItemClickListener != null && v.id == this.itemView.id) {
                onItemClickListener?.onItemClick(this@BaseRecyclerAdapter, this, v, adapterPosition)
            } else if (onItemChildClickListener != null && v.id != this.itemView.id) {
                onItemChildClickListener?.onItemChildClick(this@BaseRecyclerAdapter, this, v, adapterPosition)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(adapter: BaseRecyclerAdapter, holder: RecyclerHolder, view: View, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(adapter: BaseRecyclerAdapter, holder: RecyclerHolder, view: View, position: Int)
    }

    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener
    }

    interface OnItemChildClickListener {
        fun onItemChildClick(adapter: BaseRecyclerAdapter, holder: RecyclerHolder, view: View, position: Int)
    }

    fun setOnItemChildClickListener(onItemChildClickListener: OnItemChildClickListener) {
        this.onItemChildClickListener = onItemChildClickListener
    }

    interface OnItemChildLongClickListener {
        fun onItemChildLongClick(adapter: BaseRecyclerAdapter, holder: RecyclerHolder, view: View, position: Int)
    }

    fun setOnItemChildLongClickListener(onItemChildLongClickListener: OnItemChildLongClickListener) {
        this.onItemChildLongClickListener = onItemChildLongClickListener
    }
}
