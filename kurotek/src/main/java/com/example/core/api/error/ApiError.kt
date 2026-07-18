package com.example.core.api.error

/**
 * ApiError
 * يحدد الأخطاء الشائعة القادمة من الـ API (Go Backend).
 * يتم تحويل الـ HTTP Codes و Body Errors إلى هذه الكائنات.
 */
sealed class ApiError(message: String) : Exception(message) {
    class NetworkError(message: String = "No internet connection") : ApiError(message)
    class TimeoutError(message: String = "Request timed out") : ApiError(message)
    class UnauthorizedError(message: String = "Invalid or expired token") : ApiError(message)
    class ServerError(val code: Int, message: String) : ApiError(message)
    class UnknownError(message: String) : ApiError(message)
}
