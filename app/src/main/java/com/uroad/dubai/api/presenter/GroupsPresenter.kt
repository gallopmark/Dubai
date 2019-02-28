package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.StringObserver
import com.uroad.dubai.api.view.GroupsEditView
import com.uroad.dubai.model.GroupsStateMDL
import com.uroad.dubai.model.GroupsTeamDataMDL
import com.uroad.dubai.utils.GsonUtils
import com.uroad.dubai.webService.WebApi

class GroupsPresenter() : BasePresenter<GroupsEditView>() {

    private var editView: GroupsEditView? = null

    constructor(editView: GroupsEditView?) : this() {
        this.editView = editView
    }

    fun createGroup(teamName: String?, toPlace: String?,
                    longitude: Double?, latitude: Double?, teamLeader: String?) {
        request(WebApi.CREATE_CAR_TEAM, WebApi.editCarTeamParams("", toPlace, teamName, longitude, latitude, teamLeader), object : StringObserver(editView) {
            override fun onHttpResultOk(data: String?) {
                editView?.onCreateGroupResult(GsonUtils.getDataString(data, "teamid"))
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                editView?.onShowError(errorMsg)
            }
        })
    }

    fun getCarTeamDataWithId(teamId: String?, callback: OnGetCarTeamCallback?) {
        getCarTeamData(WebApi.carTeamDataWithId(teamId), callback)
    }

    fun getCarTeamDataWithToken(inToken: String?, callback: OnGetCarTeamCallback?) {
        getCarTeamData(WebApi.carTeamDataWithToken(inToken), callback)
    }

    private fun getCarTeamData(params: HashMap<String, String?>, callback: OnGetCarTeamCallback?) {
        request(WebApi.CAR_TEAM_DATA, params, object : StringObserver() {
            override fun onHttpResultOk(data: String?) {
                callback?.onGetCarTeamData(GsonUtils.fromDataBean(data, GroupsTeamDataMDL::class.java))
            }

            override fun onError(e: Throwable) {
                callback?.onShowError(e.message)
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                callback?.onShowError(errorMsg)
            }
        })
    }

    fun checkCarTeam(userUUID: String?, callback: OnCheckCarStateCallback?) {
        request(WebApi.CHECK_CAR_TEAM, WebApi.simpleParams(userUUID), object : StringObserver() {
            override fun onHttpResultOk(data: String?) {
                callback?.onCheckCarResult(GsonUtils.fromDataBean(data, GroupsStateMDL::class.java))
            }

            override fun onError(e: Throwable) {
                callback?.onCheckCarFailure(e.message)
            }

            override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
                callback?.onCheckCarFailure(errorMsg)
            }
        })
    }

    interface OnGetCarTeamCallback {
        fun onGetCarTeamData(mdl: GroupsTeamDataMDL?)
        fun onShowError(errorMsg: String?)
    }

    interface OnCheckCarStateCallback {
        fun onCheckCarResult(mdl: GroupsStateMDL?)
        fun onCheckCarFailure(errorMsg: String?)
    }
}