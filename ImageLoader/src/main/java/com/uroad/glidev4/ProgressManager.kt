package com.uroad.glidev4


import com.uroad.glidev4.listener.OnProgressListener
import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.Collections


object ProgressManager {

    private val listeners = Collections.synchronizedList(ArrayList<WeakReference<OnProgressListener>>())

    fun addProgressListener(progressListener: OnProgressListener?) {
        if (progressListener == null) return
        if (findProgressListener(progressListener) == null) {
            listeners?.add(WeakReference(progressListener))
        }
    }

    fun removeProgressListener(progressListener: OnProgressListener?) {
        if (progressListener == null) return

        val listener = findProgressListener(progressListener)
        if (listener != null) {
            listeners?.remove(listener)
        }
    }

    private fun findProgressListener(listener: OnProgressListener?): WeakReference<OnProgressListener>? {
        if (listener == null) return null
        if (listeners == null || listeners.size == 0) return null

        for (i in listeners.indices) {
            val progressListener = listeners[i]
            if (progressListener.get() === listener) {
                return progressListener
            }
        }
        return null
    }
}
