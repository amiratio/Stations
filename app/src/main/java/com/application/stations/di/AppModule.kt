package com.application.stations.di

import android.app.Application
import com.application.stations.remote.ApiService
import com.application.stations.remote.RemoteConstants
import com.application.stations.remote.Repository
import com.application.stations.utils.MapHelper
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule{

    @Provides
    @Singleton
    @Named("baseUrl")
    fun provideBaseUrl() = RemoteConstants.BASE_URL

    @Singleton
    @Provides
    @Named("apiClient")
    fun provideRetrofit(@Named("baseUrl") baseUrl: String) : Retrofit {
        val okhttp = OkHttpClient().newBuilder()
        val timeUnit= TimeUnit.MINUTES
        val timeOut= 1L
        okhttp.connectTimeout(timeOut, timeUnit)
            .readTimeout(timeOut, timeUnit)
            .writeTimeout(timeOut, timeUnit)
        okhttp.addInterceptor { chain ->
            val request= chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(request)
        }

        val gsonBuilder = GsonBuilder()
        gsonBuilder.setLenient()
        val gson = gsonBuilder.create()
        return Retrofit.Builder().baseUrl(baseUrl).client(okhttp.build()).addConverterFactory(
            GsonConverterFactory.create(gson)).build()
    }

    @Provides
    @Singleton
    fun provideApiService(@Named("apiClient") retrofit: Retrofit) =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideRepository(apiService: ApiService, application: Application): Repository {
        return Repository(apiService, application)
    }

    @Provides
    @Singleton
    fun provideMapHelper(repository: Repository): MapHelper {
        return MapHelper(repository)
    }

}