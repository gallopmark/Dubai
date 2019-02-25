package com.uroad.dubai.utils

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import java.util.ArrayList
import android.text.TextUtils
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 *Created by MFB on 2018/6/6.
 */
class GsonUtils {

    companion object {
        class ParameterizedTypeImpl<T>(private val clazz: Class<T>) : ParameterizedType {
            override fun getRawType(): Type {
                return MutableList::class.java
            }

            override fun getOwnerType(): Type? {
                return null
            }

            override fun getActualTypeArguments(): Array<Type> {
                return arrayOf(clazz)
            }
        }

        /**
         * 判断接口返回的数据是否正常
         * @param json json数据
         */
        fun isStatusOk(json: String?): Boolean {
            if (TextUtils.isEmpty(json)) return false
            return try {
                val je = JsonParser().parse(json)
                je.asJsonObject.get("status").asString.equals("ok", true)
            } catch (e: Exception) {
                false
            }
        }

        fun isResultOk(json: String?): Boolean {
            if (TextUtils.isEmpty(json)) return false
            return try {
                val je = JsonParser().parse(json)
                je.asJsonObject.get("status").asBoolean
            } catch (e: Exception) {
                false
            }
        }

        /**
         * 判断接口返回的数据是否正常
         * @param json json数据
         */
        fun isStatusOkByInt(json: String?): Boolean {
            if (TextUtils.isEmpty(json)) return false
            return try {
                val je = JsonParser().parse(json)
                je.asJsonObject.get("status").asInt == 1
            } catch (e: Exception) {
                false
            }
        }

        /**
         * 将json数据中 'data' 字段解析为实体类
         */
        fun <T> fromDataBean(json: String?, clazz: Class<T>): T? {
            if (TextUtils.isEmpty(json)) return null
            return try {
                val data = getData(json)
                Gson().fromJson(data, clazz)
            } catch (e: Exception) {
                null
            }
        }

        /**
         * 将json数据中 'data' 字段解析为 List
         */
        fun <T> fromDataToList(json: String?, clazz: Class<T>): MutableList<T> {
            if (TextUtils.isEmpty(json)) return ArrayList()
            return try {
                val data = getData(json)
                val type = ParameterizedTypeImpl(clazz)
                Gson().fromJson(data, type)
            } catch (e: Exception) {
                ArrayList()
            }
        }

        /**
         * 得到json数据中 'data' 字段
         */
        fun getData(json: String?): String? {
            if (TextUtils.isEmpty(json)) return null
            return try {
                JsonParser().parse(json).asJsonObject.get("data").toString()
            } catch (e: Exception) {
                null
            }
        }

        fun getDataString(json: String?, key: String?): String? {
            if (TextUtils.isEmpty(json)) return ""
            return try {
                JsonParser().parse(json).asJsonObject.get("data").asJsonObject.get(key).asString
            } catch (e: Exception) {
                ""
            }
        }

        fun getDataAsString(json: String?): String {
            if (TextUtils.isEmpty(json)) return ""
            return try {
                JsonParser().parse(json).asJsonObject.get("data").asString
            } catch (e: Exception) {
                ""
            }
        }

        fun getMsg(json: String?): String {
            if (TextUtils.isEmpty(json)) return ""
            return try {
                JsonParser().parse(json).asJsonObject.get("msg").asString
            } catch (e: Exception) {
                ""
            }
        }

        fun getCode(json: String?): Int {
            if (TextUtils.isEmpty(json)) return -1
            return try {
                JsonParser().parse(json).asJsonObject.get("CODE").asInt
            } catch (e: Exception) {
                -1
            }
        }

        /**
         * 获取接口返回的错误信息
         * @param key
         * @return: String
         */
        fun getErrorString(json: String?, key: String): String {
            if (TextUtils.isEmpty(json)) return ""
            return try {
                JsonParser().parse(json).asJsonObject.get(key).asString
            } catch (e: Exception) {
                ""
            }
        }

        fun getAsJsonArray(json: String?, key: String): JsonArray? {
            if (TextUtils.isEmpty(json)) return null
            return try {
                JsonParser().parse(json).asJsonObject.getAsJsonArray(key)
            } catch (e: Exception) {
                null
            }
        }

        fun getAsJsonObject(json: String?, key: String): JsonObject? {
            if (TextUtils.isEmpty(json)) return null
            return try {
                JsonParser().parse(json).asJsonObject.getAsJsonObject(key)
            } catch (e: Exception) {
                null
            }
        }

        fun getAsString(json: String?, key: String): String? {
            if (TextUtils.isEmpty(json)) return null
            return try {
                JsonParser().parse(json).asJsonObject.get(key).asString
            } catch (e: Exception) {
                null
            }
        }

        fun getString(json: String?, key: String): String? {
            if (TextUtils.isEmpty(json)) return null
            return try {
                JsonParser().parse(json).asJsonObject.get(key).toString()
            } catch (e: Exception) {
                null
            }
        }

        fun getAsInt(json: String?, key: String): Int {
            if (TextUtils.isEmpty(json)) return -1
            return try {
                JsonParser().parse(json).asJsonObject.get(key).asInt
            } catch (e: Exception) {
                -1
            }
        }

        fun getAsBoolean(json: String?, key: String): Boolean {
            if (TextUtils.isEmpty(json)) return false
            return try {
                JsonParser().parse(json).asJsonObject.get(key).asBoolean
            } catch (e: Exception) {
                false
            }
        }

        fun <T> fromJsonToList(json: String?, clazz: Class<T>): MutableList<T> {
            if (TextUtils.isEmpty(json)) return ArrayList()
            return try {
                val type = ParameterizedTypeImpl(clazz)
                Gson().fromJson(json, type)
            } catch (e: Exception) {
                ArrayList()
            }
        }

        /**
         * json转map
         */
        fun <K, V> fromJsonToMaps(json: String?): Map<K, V> {
            if (TextUtils.isEmpty(json)) return HashMap()
            return try {
                Gson().fromJson(json, object : TypeToken<Map<K, V>>() {}.type)
            } catch (e: Exception) {
                HashMap()
            }
        }

        /**
         * json转list<Map>
         */
        fun <K, V> fromJsonToListMaps(json: String?): MutableList<Map<K, V>> {
            if (TextUtils.isEmpty(json)) return ArrayList()
            return try {
                Gson().fromJson(json, object : TypeToken<MutableList<Map<K, V>>>() {}.type)
            } catch (e: Exception) {
                ArrayList()
            }
        }

        /**
         * obj转json数据
         * 可以是任意对象 list、map等
         */
        fun fromObjectToJson(obj: Any?): String {
            if (obj == null) return "{}"
            return try {
                Gson().toJson(obj)
            } catch (e: Exception) {
                "{}"
            }
        }

        fun <T> fromJsonToObject(json: String?, clazz: Class<T>): T? {
            if (TextUtils.isEmpty(json)) return null
            return try {
                Gson().fromJson(json, clazz)
            } catch (e: Exception) {
                null
            }
        }

        fun isJson(json: String?): Boolean {
            if (TextUtils.isEmpty(json)) return false
            return try {
                JsonParser().parse(json).asJsonObject
                true
            } catch (e: Exception) {
                false
            }
        }
    }

}