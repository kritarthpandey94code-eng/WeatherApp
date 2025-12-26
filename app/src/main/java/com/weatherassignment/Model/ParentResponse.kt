package com.weatherassignment.Model

import com.google.gson.annotations.SerializedName

data class ParentResponse(
    @SerializedName("location" ) var location : Location? = Location(),
    @SerializedName("current"  ) var current  : Current?  = Current(),
    @SerializedName("forecast" ) var forecast : Forecast? = Forecast()
)
