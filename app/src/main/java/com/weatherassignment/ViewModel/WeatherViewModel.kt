package com.weatherassignment.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weatherassignment.Model.ParentResponse
import com.weatherassignment.Network.Resource
import com.weatherassignment.Repository.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weather = MutableLiveData<Resource<ParentResponse>>()
    val weather: LiveData<Resource<ParentResponse>> = _weather


    fun fetchWeather(city: String) {
        viewModelScope.launch {
            _weather.value = Resource.Loading()
            try {
                val response = repository.getWeather(city)
                _weather.value = Resource.Success(response)
            } catch (e: Exception) {
                _weather.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }

    class WeatherViewModelFactory(private val repository: WeatherRepository) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return WeatherViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }


}
