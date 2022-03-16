package com.example.simplemediaplayer.api

import com.example.simplemediaplayer.utils.Constants
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiFactory {
    val instance: ApiService = Retrofit.Builder().run {
        val gson = GsonBuilder()
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .create()

        baseUrl(Constants.BASE_URL )
        addConverterFactory(GsonConverterFactory.create())
        client(createRequestInterceptorClient())
        build()
    }.create(ApiService::class.java)

    private fun createRequestInterceptorClient(): OkHttpClient {
        val interceptor = Interceptor { chain ->
            val origin = chain.request()
            val requestBuilder = origin.newBuilder()
            val request = requestBuilder.build()
            chain.proceed(request)
        }

        return if (true) {
            OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .connectTimeout(1000.toLong(), TimeUnit.SECONDS)
                .readTimeout(1000.toLong(), TimeUnit.SECONDS)
                .writeTimeout(1000.toLong(), TimeUnit.SECONDS)
                .build()
        } else {
            OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(1000.toLong(), TimeUnit.SECONDS)
                .readTimeout(1000.toLong(), TimeUnit.SECONDS)
                .writeTimeout(1000.toLong(), TimeUnit.SECONDS)
                .build()
        }
    }
}