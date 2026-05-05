package com.example.data

import com.example.common.NetworkMonitor
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class DefaultNetworkMonitor @Inject constructor() : NetworkMonitor {
    private val _isOnline = MutableStateFlow(true)
    override val isOnline: Flow<Boolean> = _isOnline.asStateFlow()

    // 1. UI가 잠깐 'collect'를 놓쳐도 1개의 이벤트는 기억하도록 버퍼 설정
    // 2. 버퍼가 꽉 차면 가장 오래된 이벤트를 버림 (가장 최신 에러만 중요하므로)
    private val _networkError = MutableSharedFlow<String>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val networkError: Flow<String> = _networkError.asSharedFlow()

    override fun notifyError(message: String) {
        _networkError.tryEmit(message)
    }

    //todo: 네트워크 연결이 끊겼을시의 로직 구현
    fun setOnline(online: Boolean) {
        _isOnline.value = online
    }
}
