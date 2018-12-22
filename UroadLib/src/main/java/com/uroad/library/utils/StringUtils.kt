package com.uroad.library.utils

import android.text.TextUtils

class StringUtils {
    companion object {
        fun isEmpty(str: CharSequence?): Boolean = TextUtils.isEmpty(str)

        fun isEquals(a: CharSequence?, b: CharSequence?) = TextUtils.equals(a, b)

        fun isContains(origin: CharSequence?, key: CharSequence): Boolean {
            origin?.let { return origin.contains(key) }
            return false
        }

        // 根据Unicode编码判断中文汉字和符号
        private fun isChinese(c: Char): Boolean {
            val ub = Character.UnicodeBlock.of(c)
            return (ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                    || ub === Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub === Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                    || ub === Character.UnicodeBlock.GENERAL_PUNCTUATION)
        }

        // 判断中文汉字和符号
        fun isChinese(strName: String): Boolean {
            val ch = strName.toCharArray()
            for (c in ch) {
                if (isChinese(c)) {
                    return true
                }
            }
            return false
        }
    }
}