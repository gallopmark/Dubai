package com.uroad.dubai.common

import android.content.Context


abstract class BaseArrayRecyclerAdapter<T>(context: Context, var mDatas: MutableList<T>)
    : BaseRecyclerAdapter(context) {

    override fun getItem(position: Int): T {
        return mDatas[position]
    }

    fun bindData(isRefresh: Boolean, datas: List<T>?): Boolean {
        if (datas == null) return false
        if (isRefresh) mDatas.clear()
        val b = mDatas.addAll(datas)
        notifyDataSetChanged()
        return b
    }

    fun clearData() {
        mDatas.clear()
        notifyDataSetChanged()
    }

    fun addItem(position: Int, t: T?): Boolean {
        if (t == null) return false
        if (position < 0 || position > mDatas.size) return false
        if (mDatas.contains(t)) return false
        mDatas.add(position, t)
        notifyItemInserted(position)
        return true
    }

    fun addItems(pos: Int, datas: List<T>?): Boolean {
        if (datas == null) return false
        if (mDatas.containsAll(datas)) return false
        mDatas.addAll(pos, datas)
        notifyItemRangeInserted(pos, datas.size)
        return true
    }

    fun addItems(datas: List<T>?): Boolean {
        if (datas == null) return false
        if (mDatas.containsAll(datas)) return false
        mDatas.addAll(datas)
        notifyItemRangeInserted(if (itemCount - datas.size >= 0) itemCount - datas.size else 0, datas.size)
        return true
    }

    fun addItem(t: T?): Boolean {
        if (t == null) return false
        if (mDatas.contains(t)) return false
        val b = mDatas.add(t)
        notifyItemInserted(mDatas.size - 1)
        return b
    }


    fun updateItem(position: Int): Boolean {
        if (position < 0 || position >= mDatas.size) return false
        notifyItemChanged(position)
        return true
    }

    fun updateItem(t: T?): Boolean {
        if (t == null) return false
        val index = mDatas.indexOf(t)
        if (index >= 0) {
            mDatas[index] = t
            notifyItemChanged(index)
            return true
        }
        return false
    }

    fun updateItem(position: Int, t: T?): Boolean {
        if (position < 0 || position >= mDatas.size) return false
        if (t == null) return false
        mDatas[position] = t
        notifyItemChanged(position)
        return true
    }

    fun removeItem(position: Int): Boolean {
        if (position < 0 || position >= mDatas.size) return false
        mDatas.removeAt(position)
        notifyItemRemoved(position)
        return true
    }

    fun removeItem(t: T?): Boolean {
        if (t == null) return false
        val index = mDatas.indexOf(t)
        if (index >= 0) {
            mDatas.removeAt(index)
            notifyItemRemoved(index)
            return true
        }
        return false
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        onBindHoder(holder, mDatas[position], position)
    }

    abstract fun onBindHoder(holder: RecyclerHolder, t: T, position: Int)

    override fun getItemCount(): Int {
        return mDatas.size
    }
}