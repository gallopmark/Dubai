package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.api.view.FavoriteEventFMView
import com.uroad.dubai.model.EventsMDL
import com.uroad.dubai.utils.GsonUtils

class FavoriteEventFMPresenter(val favoriteEventFMView: FavoriteEventFMView) : BasePresenter<FavoriteEventFMView>(favoriteEventFMView)  {
    fun getNewsList(method: String?, params: HashMap<String, String?>) {
        request(method, params, object : StringObserver(favoriteEventFMView) {
            override fun onHttpResultOk(data: String?) {
                favoriteEventFMView.onGetNewList(GsonUtils.fromDataToList(data, EventsMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                favoriteEventFMView.onHttpResultError(errorMsg, errorCode)
            }
        })
    }
}