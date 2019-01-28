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

        holder.setText(R.id.tvMsgTitle, t.content)
        holder.setText(R.id.tvMsgTime, t.created)

        when (t.readstatus) {
            "0" -> isNewMsg.visibility = View.VISIBLE
            else -> isNewMsg.visibility = View.GONE
        }

        /**
        1009001 event
        1009002 news
        1009003 notice
        1009004 system*/
        when (t.pushtype) {
            "1009001" -> imageView.setImageResource(R.mipmap.icon_msg_accident)
            "1009002" -> imageView.setImageResource(R.mipmap.icon_msg_news)
            "1009003" -> imageView.setImageResource(R.mipmap.icon_msg_setting)
            else -> imageView.setImageResource(R.mipmap.icon_msg_setting)
            //else -> imageView.setImageResource(R.mipmap.ic_new_message)
        }

    }
}