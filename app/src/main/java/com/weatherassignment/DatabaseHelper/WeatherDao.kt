package com.weatherassignment.DatabaseHelper

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {

    // ------------------ INSERT ------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(current: CurrentWeatherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecastDay(day: ForecastDayEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyWeather(list: List<HourlyWeatherEntity>)


    // ------------------ CLEAR ------------------

    @Query("DELETE FROM LOCATION")
    suspend fun clearLocation()

    @Query("DELETE FROM CURRENT_WEATHER")
    suspend fun clearCurrent()

    @Query("DELETE FROM FORECAST_DAY")
    suspend fun clearForecast()

    @Query("DELETE FROM HOURLY_WEATHER")
    suspend fun clearHourly()


    // ------------------ READ ------------------

    @Query("SELECT * FROM LOCATION LIMIT 1")
    fun getLocation(): LiveData<LocationEntity?>

    @Query("SELECT * FROM CURRENT_WEATHER LIMIT 1")
    fun getCurrentWeather(): LiveData<CurrentWeatherEntity?>

    @Query("SELECT * FROM FORECAST_DAY ORDER BY date ASC")
    fun getForecastDays(): LiveData<List<ForecastDayEntity>>

    @Query("SELECT * FROM HOURLY_WEATHER WHERE forecastDayId = :dayId ORDER BY time ASC")
    fun getHourlyWeather(dayId: Int): LiveData<List<HourlyWeatherEntity>>
}


