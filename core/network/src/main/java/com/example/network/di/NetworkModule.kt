package com.example.network.di

import android.content.Context
import com.example.network.AuthService
import com.example.network.BuildConfig
import com.example.network.PostService
import com.example.network.interceptor.AuthInterceptor
import com.example.network.interceptor.ErrorInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = BuildConfig.BASE_URL;

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        authInterceptor: AuthInterceptor,
        errorInterceptor: ErrorInterceptor
    ): OkHttpClient {
        val cacheSize = (10 * 1024 * 1024).toLong() // 10 MB
        val cache = Cache(File(context.cacheDir, "http_cache"), cacheSize)

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if(BuildConfig.DEBUG) Level.BODY else Level.NONE
        }

        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(errorInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        httpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        // 서버에 PUT, POST 를 보낼때 컨텐츠가 Json 타입 이라는것을 명시함
        // addConverterFactory() : 서버에서 온 JSON을 객체로 변환하거나 객체를 JSON으로 변환하는 컨버터를 설정함
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun providePostService(retrofit: Retrofit): PostService {
        return retrofit.create(PostService::class.java)
    }
}