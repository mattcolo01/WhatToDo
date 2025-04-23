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
        priceRange: String,
        isOutdoor: Boolean,
        weatherRequirements: String,
        timeRequired: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val thing = Thing(
                name = name,
                description = "", // For now we'll use the same field for name/description
                priceRange = priceRange,
                isOutdoor = isOutdoor,
                weatherRequirements = weatherRequirements,
                timeRequired = timeRequired
            )
            thingDao.insertThing(thing)
            onSuccess()
        }
    }
} 