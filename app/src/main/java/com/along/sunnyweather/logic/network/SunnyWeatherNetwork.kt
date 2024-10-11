package com.along.sunnyweather.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * 统一管理网络数据源入口，对所有网络请求API进行封装
 */
object SunnyWeatherNetwork {

    // 创建API实例
    private val placeService = ServiceCreator.create<PlaceService>()

    // 执行网络请求得到数据[此时协程会被堵塞]
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()

    // Call的扩展函数
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                // 成功的数据处理
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("响应数据体为空")
                    )
                }

                // 失败的数据处理
                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

}