package com.uroad.dubai.local

import android.content.Context
import android.text.TextUtils
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.uroad.dubai.model.MultiItem
import com.uroad.dubai.model.PoiSearchPoiMDL
import com.uroad.dubai.model.PoiSearchTextMDL
import com.uroad.dubai.utils.GsonUtils
import java.util.ArrayList

class PoiSearchSource {
    companion object {
        private const val POI_FILE = "poi_history"
        private const val POI_KEY = "poi_key"
        private const val split = "✈"

        private fun getSharedPreferences(context: Context) = context.getSharedPreferences(POI_FILE, Context.MODE_PRIVATE)

        fun saveContent(context: Context, content: String) {
            val sp = getSharedPreferences(context)
            var spHistory = sp.getString(POI_KEY, "") ?: ""
            if (TextUtils.isEmpty(spHistory)) {
                spHistory = content
            } else {
                val data = spHistory.split(split).toMutableList()
                if (data.contains(content)) data.remove(content) //如果有相同的记录 则删除旧的，添加新的
                val sb = StringBuilder()
                for (i in 0 until data.size) {
                    sb.append(data[i])
                    if (i < data.size - 1) sb.append(split)
                }
                spHistory = sb.toString()
                if (TextUtils.isEmpty(spHistory)) spHistory = content
                else spHistory += "$split$content"
            }
            sp.edit().putString(POI_KEY, spHistory).apply()
        }

        fun clear(context: Context) {
            getSharedPreferences(context).edit().clear().apply()
        }

        fun deleteItem(context: Context, content: String) {
            val sp = getSharedPreferences(context)
            val spHistory = sp.getString(POI_KEY, "") ?: ""
            if (!TextUtils.isEmpty(spHistory)) {
                val data = spHistory.split(split).toMutableList()
                data.remove(content)
                val sb = StringBuilder()
                for (i in 0 until data.size) {
                    sb.append(data[i])
                    if (i < data.size - 1) sb.append(split)
                }
                sp.edit().putString(POI_KEY, sb.toString()).apply()
            }
        }

        fun getHistoryList(context: Context): MutableList<MultiItem> {
            val spHistory = getSharedPreferences(context).getString(POI_KEY, "") ?: ""
            val list = ArrayList<MultiItem>()
            if (!TextUtils.isEmpty(spHistory)) {
                val data = spHistory.split(split).toMutableList()
                data.reverse()   //倒序排序
                for (item in data) {
                    if (GsonUtils.isJson(item)) {
                        list.add(PoiSearchPoiMDL().apply { carmenFeature = GsonUtils.fromJsonToObject(item, CarmenFeature::class.java) })
                    } else {
                        list.add(PoiSearchTextMDL().apply { this.content = item })
                    }
                }
            }
            return list
        }
    }
}