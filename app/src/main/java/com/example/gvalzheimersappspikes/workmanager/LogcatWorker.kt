package com.example.gvalzheimersappspikes.workmanager

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class LogcatWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    companion object {
        private const val TAG = "LogcatTask"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "The device is connected to the internet")
        return Result.retry()
    }
}

