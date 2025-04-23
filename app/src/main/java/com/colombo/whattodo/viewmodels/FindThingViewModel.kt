package com.colombo.whattodo.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.colombo.whattodo.data.AppDatabase
import com.colombo.whattodo.data.Thing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class ThingMatch(
    val thing: Thing,
    val score: Int,
    val mismatchedFields: Set<String> = emptySet()
)

class FindThingViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val thingDao = database.thingDao()

    private val _matchingThings = MutableStateFlow<List<ThingMatch>>(emptyList())
    val matchingThings: StateFlow<List<ThingMatch>> = _matchingThings

    // Filters as StateFlows to trigger recomputation when they change
    private val priceRangeFilter = MutableStateFlow("")
    private val isOutdoorFilter = MutableStateFlow(false)
    private val weatherFilter = MutableStateFlow("")
    private val timeRequiredFilter = MutableStateFlow("")

    init {
        viewModelScope.launch {
            // Combine all filters with the database content
            combine(
                thingDao.getAllThings(),
                priceRangeFilter,
                isOutdoorFilter,
                weatherFilter,
                timeRequiredFilter
            ) { things, price, outdoor, weather, time ->
                things.map { thing ->
                    val mismatches = mutableSetOf<String>()
                    var score = 4 // Start with max score

                    // Check each criterion
                    if (price.isNotEmpty() && thing.priceRange != price) {
                        mismatches.add("priceRange")
                        score--
                    }
                    if (thing.isOutdoor != outdoor) {
                        mismatches.add("location")
                        score--
                    }
                    if (weather.isNotEmpty() && thing.weatherRequirements != weather && 
                        thing.weatherRequirements != "Any") {
                        mismatches.add("weather")
                        score--
                    }
                    if (time.isNotEmpty() && thing.timeRequired != time) {
                        mismatches.add("time")
                        score--
                    }

                    ThingMatch(thing, score, mismatches)
                }.sortedByDescending { it.score }
            }.collect {
                _matchingThings.value = it
            }
        }
    }

    fun updateFilters(
        priceRange: String,
        isOutdoor: Boolean,
        weatherRequirements: String,
        timeRequired: String
    ) {
        priceRangeFilter.value = priceRange
        isOutdoorFilter.value = isOutdoor
        weatherFilter.value = weatherRequirements
        timeRequiredFilter.value = timeRequired
    }

    fun deleteThing(thing: Thing) {
        viewModelScope.launch {
            thingDao.deleteThing(thing)
        }
    }
} 