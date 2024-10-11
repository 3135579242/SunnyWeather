package com.along.sunnyweather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.along.sunnyweather.SunnyWeatherApplication
import com.along.sunnyweather.logic.model.Place
import com.google.gson.Gson

object PlaceDao {

    /**
     * 保存数据
     */
    fun savePlace(place: Place) {
        sharedPreferences().edit {
            // 转json
            putString("place", Gson().toJson(place))
        }
    }

    /**
     * 获取数据
     */
    fun getSavedPlace(): Place {
        val placeJson = sharedPreferences().getString("place", "")
        // 转object
        return Gson().fromJson(placeJson, Place::class.java)
    }

    /**
     * 判断是否存在
     */
    fun isPlaceSaved() = sharedPreferences().contains("place")

    /**
     * 获取实例
     */
    private fun sharedPreferences() =
        SunnyWeatherApplication.context
            .getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)

}