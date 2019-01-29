package com.uroad.dubai.rx2bus

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

object Rx2Bus {
    private val bus: Subject<Any> = PublishSubject.create<Any>().toSerialized()

    fun <T> toObservable(eventType: Class<T>): Flowable<T> {
        return bus.toFlowable(BackpressureStrategy.BUFFER).ofType(eventType)
    }

    fun post(o: Any) {
        bus.onNext(o)
    }
}