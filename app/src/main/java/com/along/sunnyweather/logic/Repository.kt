package com.along.sunnyweather.logic

import androidx.lifecycle.liveData
import com.along.sunnyweather.logic.model.Place
import com.along.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers

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

}