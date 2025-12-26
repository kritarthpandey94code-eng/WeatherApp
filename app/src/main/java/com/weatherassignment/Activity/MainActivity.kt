package com.weatherassignment.Activity

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.weatherassignment.DatabaseHelper.CurrentWeatherEntity
import com.weatherassignment.DatabaseHelper.ForecastDayEntity
import com.weatherassignment.DatabaseHelper.HourlyWeatherEntity
import com.weatherassignment.DatabaseHelper.LocationEntity
import com.weatherassignment.DatabaseHelper.WeatherDao
import com.weatherassignment.DatabaseHelper.WeatherDatabase
import com.weatherassignment.Model.ParentResponse
import com.weatherassignment.Network.Resource
import com.weatherassignment.R
import com.weatherassignment.Repository.WeatherRepository
import com.weatherassignment.ViewModel.WeatherViewModel
import com.weatherassignment.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: WeatherViewModel
    private lateinit var database: WeatherDatabase
    private lateinit var weatherDao: WeatherDao


    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = WeatherRepository()
        database = WeatherDatabase.get(applicationContext)
        weatherDao = database.weatherDao()
        val factory = WeatherViewModel.WeatherViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

        // Default city
//        viewModel.fetchWeather("Munich")

        binding.btnRefresh.setOnClickListener {
            val city = binding
                .searchView.query.toString()
            if (city.isNotEmpty()) {
                viewModel.fetchWeather(city)
            }
            else Toast.makeText(this@MainActivity,"Please provide city",Toast.LENGTH_SHORT).show()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { city ->
                    if(isNetworkAvailable(this@MainActivity)) {
                        if (city.isNotBlank()) viewModel.fetchWeather(city)
                        binding.searchView.clearFocus()
                    } else{
                        Toast.makeText(this@MainActivity,"No active connection, One search is required",Toast.LENGTH_SHORT).show()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?) = false
        })

        viewModel.weather.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> showLoading()
                is Resource.Success -> showWeather(resource.data)
                is Resource.Error -> showError(resource.message)
            }
        }
    }

    private fun showLoading() {
        binding.tvTemp.text = "Loading..."
        binding.tvCondition.text = ""
        binding.imgWeather.setImageResource(R.drawable.baseline_cloud_24)
    }

    private fun showWeather(data: ParentResponse) {
        binding.tvCity.text = data.location!!.name
        binding.tvFeelsLike.text = data.forecast!!.forecastday.get(0).date.toString()
        binding.tvCountry.text = data.location!!.country
        binding.tvTemp.text = "${data.current!!.tempC}°C"
        binding.tvCondition.text = data.current!!.condition!!.text
        binding.stat1.tvValue.text = data.current!!.windKph.toString()
        binding.stat1.tvLabel.text = "Wind Speed"
        binding.stat2.tvLabel.text = "UV"
        binding.stat2.tvValue.text = data.current!!.uv.toString()
        binding.stat3.tvLabel.text = "Humidity"
        binding.stat3.tvValue.text = data.current!!.humidity.toString()
        binding.stat4.tvLabel.text = "Air Pressure"
        binding.stat4.tvValue.text = data.current!!.pressureMb.toString()


        var nextday = data.forecast!!.forecastday[1].date
        var nextdayTemp = data.forecast!!.forecastday[1].day!!.maxtempC
        var nextdayIcon = data.forecast!!.forecastday[1].day!!.condition!!.icon.toString()
        binding.tvDay1.text = nextday
        binding.tvTempDay1.text = nextdayTemp.toString()

        Glide.with(this)
            .load("https:${nextdayIcon}")
            .into(binding.imgDay1)


        var nextday2 = data.forecast!!.forecastday[2].date
        var nextdayTemp2 = data.forecast!!.forecastday[2].day!!.maxtempC
        var nextdayIcon2 = data.forecast!!.forecastday[2].day!!.condition!!.icon.toString()
        binding.tvDay2.text = nextday2
        binding.tvTempDay2.text = nextdayTemp2.toString()

        Glide.with(this)
            .load("https:${nextdayIcon2}")
            .into(binding.imgDay2)

        var nextday3 = data.forecast!!.forecastday[3].date
        var nextdayTemp3 = data.forecast!!.forecastday[3].day!!.maxtempC
        var nextdayIcon3 = data.forecast!!.forecastday[3].day!!.condition!!.icon.toString()
        binding.tvDay3.text = nextday3
        binding.tvTempDay3.text = nextdayTemp3.toString()

        Glide.with(this)
            .load("https:${nextdayIcon3}")
            .into(binding.imgDay3)




        Glide.with(this)
            .load("https:${data.current!!.condition!!.icon}")
            .into(binding.imgWeather)

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch() {
            saveWeather(data, weatherDao)
        }
        weatherDao.getLocation().observe(this) { location ->
            location?.let {
                Log.d("ROOM_READ", "Location: ${it.name}, ${it.country}")
            }
        }
    }

    private fun showError(message: String) {
        binding.tvTemp.text = "Error"
        binding.tvCondition.text = message
        binding.imgWeather.setImageResource(R.drawable.baseline_cloud_24)
        weatherDao.getLocation().observe(this) { location ->
            location?.let {
                Log.d("ROOM_READ ERROR", "Location: ${it.name}, ${it.country}")
            }
        }
    }

    suspend fun saveWeather(
        api: ParentResponse,
        dao: WeatherDao
    ) {
        dao.clearHourly()
        dao.clearForecast()
        dao.clearCurrent()

        // Location
        api.location?.let {
            LocationEntity(
                name = it.name!!.toString(),
                region = it.region.toString(),
                country = it.country.toString(),
                lat = it.lat!!.toDouble(),
                lon = it.lon!!.toDouble(),
                tzId = it.tzId.toString(),
                localtimeEpoch = it.localtimeEpoch!!.toLong(),
                localtime = it.localtime!!.toString()
            )
        }?.let {
            dao.insertLocation(
                it
            )
        }

        // Current
        dao.insertCurrentWeather(
            CurrentWeatherEntity(
                lastUpdatedEpoch = api.current!!.lastUpdatedEpoch!!.toLong(),
                feelsLikeC = api.current!!.feelslikeC!!.toDouble(),
                humidity = api.current!!.humidity!!.toInt(),
                windKph = api.current!!.windKph!!.toDouble(),
                pressureMb = api.current!!.pressureMb!!.toDouble(),
                uv = api.current!!.uv!!.toDouble(),
                conditionText = api.current!!.condition!!.text.toString(),
                conditionIcon = api.current!!.condition!!.icon.toString(),
                tempC = api.current!!.tempC!!.toDouble(),
                locationName = ""
            )
        )

        // Forecast + Hours
        api.forecast!!.forecastday.forEach { day ->

            val dayId = dao.insertForecastDay(
                ForecastDayEntity(
                    date = day.date.toString(),
                    avgTempC = day.day!!.avgtempC!!.toDouble(),
                    humidity = day.day!!.avghumidity!!.toInt(),
                    conditionText = day.day!!.condition!!.text.toString(),
                    conditionIcon = day.day!!.condition!!.icon.toString(),
                    sunrise = day.astro!!.sunrise.toString(),
                    sunset = day.astro!!.sunset.toString(),
                    maxtempC = day.day!!.maxtempC!!.toDouble(),
                    locationName = ""
                )
            ).toInt()

            val hours = day.hour.map {
                HourlyWeatherEntity(
                    forecastDayId = dayId,
                    time = it.time!!.toString(),
                    tempC = it.tempC!!.toDouble(),
                    isDay = it.isDay!!.toInt(),
                    conditionText = it.condition!!.text!!.toString(),
                    conditionIcon = it.condition!!.icon!!.toString(),
                    windKph = it.windKph!!.toDouble(),
                    humidity = it.humidity!!.toInt(),
                    feelsLikeC = it.feelslikeC!!.toDouble(),
                    uv = it.uv!!.toDouble()
                )
            }

            dao.insertHourlyWeather(hours)
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    override fun onStart() {
        super.onStart()
        setOfflineValues()
    }

    private fun setOfflineValues(){
        if (!isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show()

            val data: LiveData<List<ForecastDayEntity>> = weatherDao.getForecastDays()
            val LocationData: LiveData<LocationEntity> = weatherDao.getLocation()
            val CurrentData: LiveData<CurrentWeatherEntity> = weatherDao.getCurrentWeather()


            LocationData.observe(this, Observer {
                if (!it.name.isNullOrEmpty() && !it.country.isNullOrEmpty()){
                    binding.tvCity.text = it.name
                    binding.tvCountry.text = it.country
                } else{
                    Toast.makeText(this,"Location not available, Search once",Toast.LENGTH_LONG).show()
                }
            })

            CurrentData.observe(this, Observer {
                if (!it.tempC.toString().isNullOrEmpty() && !it.conditionText.isNullOrEmpty() && !it.windKph.toString().isNullOrEmpty()
                    && !it.uv.toString().isNullOrEmpty() && !it.humidity.toString().isNullOrEmpty()&&
                    !it.pressureMb.toString().isNullOrEmpty()) {
                    binding.tvTemp.text = "${it.tempC}°C"
                    binding.tvCondition.text = it.conditionText
                    binding.stat1.tvValue.text = it.windKph.toString()
                    binding.stat1.tvLabel.text = "Wind Speed"
                    binding.stat2.tvLabel.text = "UV"
                    binding.stat2.tvValue.text = it.uv.toString()
                    binding.stat3.tvLabel.text = "Humidity"
                    binding.stat3.tvValue.text = it.humidity.toString()
                    binding.stat4.tvLabel.text = "Air Pressure"
                    binding.stat4.tvValue.text = it.pressureMb.toString()
                } else Toast.makeText(this,"Details not available, Search once",Toast.LENGTH_SHORT).show()
            })

            data.observe(this, Observer { list ->
                if (!list.isNullOrEmpty()) {
                    if (list.size>1){
                        if (!list[0].date.isNullOrEmpty()){
                            binding.tvFeelsLike.text = list[0].date.toString()

                        }
                    }
                    if (list.size > 2) {
                        if (!list[2].date.isNullOrEmpty() && !list[2].avgTempC.toString().isNullOrEmpty()) {
                            binding.tvDay1.text = list[2].date
                            binding.tvTempDay1.text = "${list[2].avgTempC}°C"
                            val nextdayIcon = list[2].conditionIcon
                            Glide.with(this)
                                .load("https:${nextdayIcon}")
                                .into(binding.imgDay1)

                        } else {
                            Toast.makeText(this,"Details not available, Search once",Toast.LENGTH_SHORT).show()
                        }
                    }
                    if (list.size > 3) {
                        if (!list[3].date.isNullOrEmpty() && !list[3].avgTempC.toString().isNullOrEmpty()) {
                            binding.tvDay2.text = list[3].date
                            binding.tvTempDay2.text = "${list[3].avgTempC}°C"
                            val nextdayIcon = list[3].conditionIcon
                            Glide.with(this)
                                .load("https:${nextdayIcon}")
                                .into(binding.imgDay2)
                        }else {
                            Toast.makeText(this,"Details not available, Search once",Toast.LENGTH_SHORT).show()

                        }
                    }
                    if (list.size > 4) {

                        if (!list[4].date.isNullOrEmpty() && !list[4].avgTempC.toString().isNullOrEmpty()) {
                            binding.tvDay3.text = list[4].date
                            binding.tvTempDay3.text = "${list[4].avgTempC}°C"

                            val nextdayIcon = list[4].conditionIcon
                            Glide.with(this)
                                .load("https:${nextdayIcon}")
                                .into(binding.imgDay3)
                        }else{
                            Toast.makeText(this,"Details not available, Search once",Toast.LENGTH_SHORT).show()

                        }
                    }

                } else {
                    Toast.makeText(this, "Some data not available", Toast.LENGTH_SHORT).show()
                }
            })



        }
    }
}

//push check
