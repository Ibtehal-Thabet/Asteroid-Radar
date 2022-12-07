package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.network.NetworkPictureOfDay
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

enum class ApiFilter(val value: String) { SHOW_WEEK("week"), SHOW_TODAY("today"), SHOW_SAVED("saved") }

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .build()

interface ApiService{
    @GET("planetary/apod")
    fun getPictureOfDay(@Query("api_key") apiKey: String): Deferred<NetworkPictureOfDay>

    @GET("neo/rest/v1/feed")
    fun getAsteroids(@Query("api_key") apiKey: String): Deferred<String>
}

object Api {
        val ret = retrofit.create(ApiService::class.java)

}