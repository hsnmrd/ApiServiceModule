package com.raika.apiservicemodule.data

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitSetting {

    private val webService: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setLenient().create()
                )
            )
    }

    fun getBuilder(
        baseUrl: String,
        headerValue: Map<String, String>,
        readTimeout: Long = 60,
        writeTimeout: Long = 60,
        connectTimeout: Long = 60,
    ): Retrofit.Builder {
        return webService
            .baseUrl(baseUrl)
            .client(
                OkHttpClient.Builder()
                    .readTimeout(readTimeout, TimeUnit.SECONDS)
                    .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                    .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                    .addInterceptor { chain: Interceptor.Chain ->
                        val request = chain.request()
                            .newBuilder()
                            .addHeader("Content-Type", "application/json")
                        headerValue.forEach { (name, value) ->
                            request.addHeader(name, value)
                        }
                        chain.proceed(request.build())
                    }
                    .build()
            )
    }
}