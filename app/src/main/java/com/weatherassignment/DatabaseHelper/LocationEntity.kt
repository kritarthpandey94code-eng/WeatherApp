package com.weatherassignment.DatabaseHelper

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class LocationEntity(
    @PrimaryKey val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val tzId: String,
    val localtimeEpoch: Long,
    val localtime: String
)

@Entity(
    tableName = "current_weather")
data class CurrentWeatherEntity(
    @PrimaryKey val lastUpdatedEpoch: Long,
    val locationName: String,
    val tempC: Double,
    val feelsLikeC: Double,
    val humidity: Int,
    val windKph: Double,
    val pressureMb: Double,
    val uv: Double,
    val conditionText: String,
    val conditionIcon: String
)


@Entity(
    tableName = "forecast_day"
)
data class ForecastDayEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val locationName: String,
    val maxtempC: Double,
    val date: String,
    val avgTempC: Double,
    val humidity: Int,
    val conditionText: String,
    val conditionIcon: String,
    val sunrise: String,
    val sunset: String
)


@Entity(
    tableName = "hourly_weather"
)
data class HourlyWeatherEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val forecastDayId: Int,

    val time: String,
    val tempC: Double,
    val isDay: Int,

    val conditionText: String,
    val conditionIcon: String,

    val windKph: Double,
    val humidity: Int,
    val feelsLikeC: Double,
    val uv: Double
)
