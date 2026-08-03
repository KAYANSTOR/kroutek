package com.example.core.network

import android.util.Log

/**
 * Default Logger → Logcat. نستبدله بـ Remote Logger في الإنتاج دون تعديل المحرك.
 */
class LogcatLogger : Logger {
    override fun d(tag: String, message: String) { Log.d(tag, message) }
    override fun e(tag: String, message: String, throwable: Throwable?) { Log.e(tag, message, throwable) }
    override fun i(tag: String, message: String) { Log.i(tag, message) }
}

/**
 * Default MetricsHook → No-op. يتم استبداله بـ Analytics SDK لاحقاً.
 */
class NoOpMetricsHook : MetricsHook {
    override fun onRequestStart(endpoint: String) {}
    override fun onRequestSuccess(endpoint: String, durationMs: Long, bytesReceived: Long) {}
    override fun onRequestError(endpoint: String, durationMs: Long, errorType: String) {}
}
