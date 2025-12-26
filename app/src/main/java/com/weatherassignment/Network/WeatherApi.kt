package com.weatherassignment.Network

import com.weatherassignment.Model.ParentResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("forecast.json")
    suspend fun getWeather(
        @Query("key") apiKey: String,
        @Query("q") city: String,
        @Query("days") days: Int = 6,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"
    ): ParentResponse
}
