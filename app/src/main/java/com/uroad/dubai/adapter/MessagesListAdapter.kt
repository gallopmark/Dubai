package com.uroad.dubai.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.MessagesMDL

class MessagesListAdapter(context: Context, data: MutableList<MessagesMDL>)
      : BaseArrayRecyclerAdapter<MessagesMDL>(context,data) {
    override fun bindView(viewType: Int): Int = R.layout.item_messages

    override fun onBindHoder(holder: RecyclerHolder, t: MessagesMDL, position: Int) {
        val imageView = holder.obtainView<ImageView>(R.id.ivMsgType)
        val isNewMsg = holder.obtainView<TextView>(R.id.tvNewMsg)

        holder.setText(R.id.tvMsgTitle, t.title)
        holder.setText(R.id.tvMsgTime, t.time)

        when (t.isnew) {
            true -> isNewMsg.visibility = View.VISIBLE
            else -> isNewMsg.visibility = View.GONE
        }

        when (t.type) {
            "100" -> imageView.setImageResource(R.mipmap.icon_msg_accident)
            "111" -> imageView.setImageResource(R.mipmap.icon_msg_news)
            "211" -> imageView.setImageResource(R.mipmap.icon_msg_setting)
            else -> imageView.setImageResource(R.mipmap.ic_new_message)
        }

    }
}