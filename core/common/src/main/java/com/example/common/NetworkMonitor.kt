package com.example.common

import kotlinx.coroutines.flow.Flow

/* 네트워크 전역 오류 처리 */
interface NetworkMonitor {
    val isOnline: Flow<Boolean>
    val networkError: Flow<String?>
    fun notifyError(message: String)
}