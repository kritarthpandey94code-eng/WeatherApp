package com.weatherassignment.DatabaseHelper

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(current: CurrentWeatherEntity)

    @Insert
    suspend fun insertForecastDay(day: ForecastDayEntity): Long

    @Insert
    suspend fun insertHourlyWeather(list: List<HourlyWeatherEntity>)

    @Query("DELETE FROM hourly_weather")
    suspend fun clearHourly()

    @Query("DELETE FROM forecast_day")
    suspend fun clearForecast()

    @Query("DELETE FROM current_weather")
    suspend fun clearCurrent()

    // Location
    @Query("SELECT * FROM LOCATION LIMIT 1")
    fun getLocation(): LiveData<LocationEntity>

    // Current Weather
    @Query("SELECT * FROM CURRENT_WEATHER LIMIT 1")
    fun getCurrentWeather(): LiveData<CurrentWeatherEntity>

    // Forecast Days
    @Query("SELECT * FROM FORECAST_DAY")
    fun getForecastDays(): LiveData<List<ForecastDayEntity>>

    // Hourly Weather
    @Query("SELECT * FROM HOURLY_WEATHER WHERE forecastDayId = :dayId")
    fun getHourlyWeather(dayId: Int): LiveData<List<HourlyWeatherEntity>>



}

