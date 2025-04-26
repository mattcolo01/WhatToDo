package com.colombo.whattodo.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.colombo.whattodo.data.AppDatabase
import com.colombo.whattodo.data.Thing
import kotlinx.coroutines.launch

class AddThingViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val thingDao = database.thingDao()

    fun saveThing(
        name: String,
        priceRange: Thing.PriceRange,
        weatherRequirements: Thing.WeatherType,
        timeRequired: Thing.TimeRequired,
        peopleNumber: Thing.PeopleNumber,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val thing = Thing(
                name = name,
                priceRange = priceRange,
                weatherRequirements = weatherRequirements,
                timeRequired = timeRequired,
                peopleNumber = peopleNumber
            )
            thingDao.insertThing(thing)
            onSuccess()
        }
    }
} 