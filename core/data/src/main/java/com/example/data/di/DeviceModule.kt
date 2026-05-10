package com.example.data.di

import com.example.common.NetworkMonitor
import com.example.data.DefaultNetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class DeviceModule {

    // @Binds: 주입할 인터페이스에 어느 구현체를 주입할건지 바인딩 하는 어노테이션
    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(
        impl: DefaultNetworkMonitor
    ): NetworkMonitor
}