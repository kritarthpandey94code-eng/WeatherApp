package com.weatherassignment.Model

import com.google.gson.annotations.SerializedName

data class Forecast(
    @SerializedName("forecastday" ) var forecastday : ArrayList<Forecastday> = arrayListOf()

)
