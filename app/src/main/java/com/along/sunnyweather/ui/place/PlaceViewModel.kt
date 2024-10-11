package com.along.sunnyweather.ui.place

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.along.sunnyweather.logic.Repository
import com.along.sunnyweather.logic.model.Place

class PlaceViewModel : ViewModel() {

    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<Place>()

    val placeLiveData = searchLiveData.switchMap { query ->
        Repository.searchPlaces(query)
    }


    fun  searchPlaces(query: String) {
        searchLiveData.value = query
    }

}