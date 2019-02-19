package com.uroad.dubai.push

import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService

class PushJobService :JobService(){

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        return false
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        return false
    }

}