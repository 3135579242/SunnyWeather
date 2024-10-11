package com.along.sunnyweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * 全局获取Context对象
 */
class SunnyWeatherApplication : Application() {

    companion object {

        // 天气预报的Token
        const val TOKEN = "5FiCL7DjeReNzA5r"

        // 全局可使用的 Context 对象
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}