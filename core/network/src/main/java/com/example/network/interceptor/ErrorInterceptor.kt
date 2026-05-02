package com.example.network.interceptor

import android.content.Context
import com.example.common.NetworkMonitor
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


class ErrorInterceptor @Inject constructor(
    private val networkMonitor: NetworkMonitor
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response

        try {
            response = chain.proceed(request)
        } catch (e: IOException) {
            val message = when(e) {
                is SocketTimeoutException -> "서버 응답 시간이 초과되었습니다."
                is UnknownHostException -> "인터넷 연결 상태를 확인해주세요"
                else -> "네트워크 통신 중 오류가 발생했습니다."
            }

            networkMonitor.notifyError(message)
            throw e
        }

        if(response.isSuccessful.not()) {
            val code = response.code

            // 401 에러는 AuthInterceptor 에서 별도로 처리
            if(code != 401) {
                val responseBody = response.peekBody(1024 * 1024).string()
                val message = parseMessage(responseBody) ?: "오류가 발생했습니다."
                networkMonitor.notifyError("(Code: $code) $message")
            }
        }

        return response
    }

    private fun parseMessage(json: String): String? {
        return try {
            val jsonObject = JSONObject(json)
            if(jsonObject.has("message")) jsonObject.getString("message") else null
        } catch (e: Exception) {
            null
        }
    }
}