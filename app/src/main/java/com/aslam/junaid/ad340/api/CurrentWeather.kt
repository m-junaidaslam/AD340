package com.aslam.junaid.ad340.api

import com.squareup.moshi.Json

data class Forecast(val temp: Float)
data class Coordinates(val lat: Float, val lon: Float)
data class CurrentWeatherDescription(val main: String, val description: String, val icon: String)

data class CurrentWeather(
    val name: String,
    val coord: Coordinates,
    @field:Json(name = "main") val forecast: Forecast,
    val weather: List<CurrentWeatherDescription>
)