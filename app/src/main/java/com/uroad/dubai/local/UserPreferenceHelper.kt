package com.uroad.zhgs.helper

import android.content.Context
import android.content.SharedPreferences
import com.uroad.dubai.model.UserMDL

/**
 *Created by MFB on 2018/8/6.
 */
class UserPreferenceHelper {
    companion object {
        //历史版本Prefs_user、Prefs_user_v2
        private const val PREFS_USER = "userInfo_v2"
        const val USER_ID = "userid"
        const val USER_UUID = "useruuid"
        const val PUSH_ID = "pushid"
        const val REAL_NAME = "realname"
        const val CARD_NO = "cardno"
        const val PHONE = "phone"
        const val USER_NAME = "username"
        const val USER_PASSWORD = "userpassword"
        const val STATUS = "status"
        const val ICON_FILE = "iconfile"
        const val SEX = "sex"
        const val LOGIN_STAYUS = "login_status"
        const val REQUEST_CODE = "requestcode"
        const val ISFOLLOW = "isfollow"
        const val QRCODE = "QRCode"
        const val ISAUTH = "isAuth"
        const val AIMLESS_NAV = "AimlessNav"
        const val USER_ACCOUNT = "account"
        const val SHOW_SEARCH_HISTORY = "SHOW_SEARCH_HISTORY"
        const val LOADING_PHOTOS = "LOADING_PHOTOS"

        private fun from(context: Context): SharedPreferences {
            return context.getSharedPreferences(PREFS_USER, Context.MODE_PRIVATE)
        }

        fun save(context: Context, userMDL: UserMDL) {
            from(context).edit().apply {
                putString(USER_ID, userMDL.userid)
                putString(REAL_NAME, userMDL.name)
                putString(SEX, userMDL.sex)
            }.apply()
        }

        fun getUserId(context: Context): String? {
            return from(context).getString(USER_ID, "")
        }

        fun getUserUUID(context: Context): String? = from(context).getString(USER_UUID, "")
        fun getPushID(context: Context): String? = from(context).getString(PUSH_ID, "")
        fun saveRealName(context: Context, realName: String) {
            from(context).edit().putString(REAL_NAME, realName).apply()
        }

        fun getRealName(context: Context): String? {
            return from(context).getString(REAL_NAME, "")
        }

        fun saveCardNo(context: Context, cardNo: String) {
            from(context).edit().putString(CARD_NO, cardNo).apply()
        }

        fun getCardNo(context: Context): String? {
            return from(context).getString(CARD_NO, "")
        }

        fun savePhone(context: Context, phone: String) {
            from(context).edit().putString(PHONE, phone).apply()
        }

        fun getPhone(context: Context): String? {
            return from(context).getString(PHONE, "")
        }

        fun saveUserName(context: Context, userName: String) {
            from(context).edit().putString(USER_NAME, userName).apply()
        }

        fun getUserName(context: Context): String? {
            return from(context).getString(USER_NAME, "")
        }

        fun getUserPassword(context: Context): String? {
            return from(context).getString(USER_PASSWORD, "")
        }

        fun saveUserPassword(context: Context,password : String) = from(context).edit().putString(USER_PASSWORD,password).apply()

        fun getStatus(context: Context): Int {
            return from(context).getInt(STATUS, 0)
        }

        fun saveIconFile(context: Context, iconFile: String) {
            from(context).edit().putString(ICON_FILE, iconFile).apply()
        }

        fun getIconFile(context: Context): String? {
            return from(context).getString(ICON_FILE, "")
        }

        fun saveSex(context: Context, sex: Int) {
            from(context).edit().putInt(SEX, sex).apply()
        }

        fun getSex(context: Context): Int {
            return from(context).getInt(SEX, 0)
        }

        fun logOut(context: Context) {
            from(context).edit().putBoolean(LOGIN_STAYUS, false).apply()
        }

        fun login(context: Context){
            from(context).edit().putBoolean(LOGIN_STAYUS,true).apply()
        }

        fun isLogin(context: Context): Boolean {
            return from(context).getBoolean(LOGIN_STAYUS, false)
        }

        fun saveQRCode(context: Context, qrCode: String) = from(context).edit().putString(QRCODE, qrCode).apply()
        fun getQRCode(context: Context): String? = from(context).getString(QRCODE, "")

        fun saveRequestCode(context: Context, requestCode: String?) = from(context).edit().putString(REQUEST_CODE, requestCode).apply()
        fun getRequestCode(context: Context): String? = from(context).getString(REQUEST_CODE, "")

        fun saveFollow(context: Context, isFollow: Boolean) = from(context).edit().putBoolean(ISFOLLOW, isFollow).apply()
        fun isFollow(context: Context): Boolean = from(context).getBoolean(ISFOLLOW, true)
        fun clear(context: Context) {
            from(context).edit().clear().apply()
        }

        fun saveAuth(context: Context, isAuth: Boolean) = from(context).edit().putBoolean(ISAUTH, isAuth).apply()
        fun isAuth(context: Context): Boolean = from(context).getBoolean(ISAUTH, false)

        fun isAimlessNav(context: Context) = from(context).getBoolean(AIMLESS_NAV, true)
        fun saveAimlessNav(context: Context, isAimlessNav: Boolean) = from(context).edit().putBoolean(AIMLESS_NAV, isAimlessNav).apply()

        fun saveAccount(context: Context,account:String) = from(context).edit().putString(USER_ACCOUNT,account).apply()
        fun getAccount(context: Context) = from(context).getString(USER_ACCOUNT,"")

        fun saveSearchHistoryState(context: Context,isShow: Boolean) = from(context).edit().putBoolean(SHOW_SEARCH_HISTORY,isShow).apply()
        fun isShowSearchHistory(context: Context) = from(context).getBoolean(SHOW_SEARCH_HISTORY,true)

        fun saveLoadingPhotoState(context: Context,canLoad : Boolean) = from(context).edit().putBoolean(LOADING_PHOTOS,canLoad).apply()
        fun canLoadingPhotos(context: Context) = from(context).getBoolean(LOADING_PHOTOS,true)
    }

}