package com.along.sunnyweather.logic.network

import com.along.sunnyweather.SunnyWeatherApplication
import com.along.sunnyweather.logic.model.DailyResponse
import com.along.sunnyweather.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherService {

    // 当天天气
    @GET("v2.6/${SunnyWeatherApplication.TOKEN}/{lat},{lng}/realtime")
    fun getRealtimeWeather(
        @Path("lng") lng: String,
        @Path("lat") lat: String,
    ): Call<RealtimeResponse>

    // 未来天气
    @GET("v2.6/${SunnyWeatherApplication.TOKEN}/{lat},{lng}/daily?dailysteps=5")
    fun getDailyWeather(
        @Path("lng") lng: String,
        @Path("lat") lat: String
    ): Call<DailyResponse>

}