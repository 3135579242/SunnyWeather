package com.along.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName

/**
 * 城市数据模型类（对应API接口返回的数据）
 */
data class PlaceResponse(
    // 状态
    val status: String,
    // 城市
    val places: List<Place>
)

data class Place(
    // 城市名称
    val name: String,
    // 城市经纬度
    val location: Location,
    @SerializedName("formatted_address") val address: String
)

data class Location(
    val lat: String,
    val lng: String
)
