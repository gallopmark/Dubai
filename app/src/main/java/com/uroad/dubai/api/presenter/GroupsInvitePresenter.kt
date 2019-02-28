package com.uroad.dubai.api.presenter

import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.view.GroupsInviteView
import com.uroad.dubai.model.GroupsInviteMDL
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class GroupsInvitePresenter(private val inviteView: GroupsInviteView)
    : BasePresenter<GroupsInviteView>(inviteView) {

    fun getInviteList(userid: String?, keyword: String?) {
        addDisposable(Observable.timer(3000, TimeUnit.MILLISECONDS)
                .map { getData() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ mdl -> inviteView.onGetInviteList(mdl.lastteammember, mdl.follow) }
                        , { inviteView.onHideLoading() }
                        , { inviteView.onHideLoading() },
                        { inviteView.onShowLoading() }))
    }

    private fun getData(): GroupsInviteMDL {
        return GroupsInviteMDL().apply {
            lastteammember = ArrayList<GroupsInviteMDL.TeamMember>().apply {
                add(GroupsInviteMDL.TeamMember().apply {
                    this.userid = "1"
                    this.username = "Lucia"
                    this.isInvitation = 0
                    this.iconfile = "https://pic.qqtn.com/up/2018-12/2018122808054199339.jpg"
                })
                add(GroupsInviteMDL.TeamMember().apply {
                    this.userid = "2"
                    this.username = "Emma"
                    this.isInvitation = 1
                    this.iconfile = "https://pic.qqtn.com/up/2018-1/15158072467576384.jpg"
                })
                add(GroupsInviteMDL.TeamMember().apply {
                    this.userid = "3"
                    this.username = "Dasiy"
                    this.isInvitation = 1
                    this.iconfile = "https://pic.qqtn.com/up/2018-1/15169317525571104.jpg"
                })
                add(GroupsInviteMDL.TeamMember().apply {
                    this.userid = "4"
                    this.username = "Ben"
                    this.isInvitation = 1
                    this.iconfile = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1547270128563&di=1b4257672e3446d24820e3558b03f7e6&imgtype=0&src=http%3A%2F%2Fwanzao2.b0.upaiyun.com%2Fsystem%2Fpictures%2F37120723%2Foriginal%2F1468384135_600x600.png"
                })
            }
            follow = ArrayList<GroupsInviteMDL.TeamMember>().apply {
                add(GroupsInviteMDL.TeamMember().apply {
                    this.userid = "5"
                    this.username = "Michael"
                    this.isInvitation = 0
                    this.iconfile = "https://img2.woyaogexing.com/2019/01/10/e4c6290220514f9bb3eaa30d86656bd7!400x400.jpeg"
                })
                add(GroupsInviteMDL.TeamMember().apply {
                    this.userid = "6"
                    this.username = "Jackson"
                    this.isInvitation = 1
                    this.iconfile = "https://img2.woyaogexing.com/2019/01/10/a97a8a2dbf85488dba5c7f787a8fac99!400x400.jpeg"
                })
                add(GroupsInviteMDL.TeamMember().apply {
                    this.userid = "7"
                    this.username = "Mike"
                    this.isInvitation = 1
                    this.iconfile = "https://img2.woyaogexing.com/2019/01/10/d47059edc3f843e2a1113bdd6552b2eb!400x400.jpeg"
                })
                add(GroupsInviteMDL.TeamMember().apply {
                    this.userid = "8"
                    this.username = "Mark"
                    this.isInvitation = 1
                    this.iconfile = "https://img2.woyaogexing.com/2019/01/10/018330c21882400d90c1789c8ec5f596!400x400.jpeg"
                })
            }
        }
    }
}