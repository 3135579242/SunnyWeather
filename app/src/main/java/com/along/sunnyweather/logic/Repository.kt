package com.along.sunnyweather.logic

import androidx.lifecycle.liveData
import com.along.sunnyweather.logic.dao.PlaceDao
import com.along.sunnyweather.logic.model.Place
import com.along.sunnyweather.logic.model.Weather
import com.along.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

/**
 * 仓库层代码
 */
object Repository {

    // Dispatchers.IO 使其代码块运行在子线程中
    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {
        val result = try {
            // 执行网络请求
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok") {
                // 处理成功数据
                val places = placeResponse.places
                Result.success(places)
            } else {
                // 失败的处理
                Result.failure(RuntimeException("响应状态错误 ${placeResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure<List<Place>>(e)
        }
        // 将包装的结果发射出去
        emit(result)
    }

    fun refreshWeather(
        lng: String,
        lat: String
    ) = fire(Dispatchers.IO) {
        // coroutineScope 协程作用域
        coroutineScope {
            // 并发执行
            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            // 并发执行
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }

            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()

            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather =
                    Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)

                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status}"
                                +
                                "daily response status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }

    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavePlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()


}