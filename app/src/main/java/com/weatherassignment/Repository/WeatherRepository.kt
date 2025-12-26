package com.weatherassignment.Repository

import com.weatherassignment.Model.ParentResponse
import com.weatherassignment.Network.RetrofitInstance



class WeatherRepository() {


            suspend fun getWeather(city: String): ParentResponse {
                return RetrofitInstance.api.getWeather(
                    apiKey = "dd018c4a8256482c8c3132452252512",
                    city = city
                )
            }
}


