package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.api.view.FavoriteRouteFMView
import com.uroad.dubai.model.RouteMDL
import com.uroad.dubai.utils.GsonUtils

class FavoriteRouteFMPresenter(val favoriteRouteFMView: FavoriteRouteFMView) : BasePresenter<FavoriteRouteFMView>(favoriteRouteFMView)  {
    fun getNewsList(method: String?, params: HashMap<String, String?>) {
        request(method, params, object : StringObserver(favoriteRouteFMView) {
            override fun onHttpResultOk(data: String?) {
                favoriteRouteFMView.onGetNewList(GsonUtils.fromDataToList(data, RouteMDL::class.java))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                favoriteRouteFMView.onHttpResultError(errorMsg, errorCode)
            }
        })
    }
}