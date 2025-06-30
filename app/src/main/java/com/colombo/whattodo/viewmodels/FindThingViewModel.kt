package com.colombo.whattodo.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.colombo.whattodo.data.AppDatabase
import com.colombo.whattodo.data.Thing
import com.colombo.whattodo.data.Thing.FilterType
import com.colombo.whattodo.data.Thing.PriceRange
import com.colombo.whattodo.data.Thing.WeatherType
import com.colombo.whattodo.data.Thing.TimeRequired
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
    val priceRangeFilter = MutableStateFlow<PriceRange?>(null)
    val weatherFilter = MutableStateFlow<WeatherType?>(null)
    val timeRequiredFilter = MutableStateFlow<TimeRequired?>(null)
    val peopleNumberFilter = MutableStateFlow<Thing.PeopleNumber?>(null)

    init {
        viewModelScope.launch {
            // Combine all filters with the database content
            combine(
                thingDao.getAllThings(),
                priceRangeFilter,
                weatherFilter,
                timeRequiredFilter,
                peopleNumberFilter
            ) { things, price, weather, time, people ->
                things.map { thing ->
                    computeScore(thing, price, weather, time, people)
                }.sortedByDescending { it.score }
            }.collect {
                _matchingThings.value = it
            }
        }
    }

    fun updateFilters(
        priceRange: PriceRange?,
        weatherRequirements: WeatherType?,
        timeRequired: TimeRequired?,
        peopleNumber: Thing.PeopleNumber?
    ) {
        priceRangeFilter.value = priceRange
        weatherFilter.value = weatherRequirements
        timeRequiredFilter.value = timeRequired
        peopleNumberFilter.value = peopleNumber
    }

    private fun computeScore(
        thing: Thing,
        price: PriceRange?,
        weather: WeatherType?,
        time: TimeRequired?,
        people: Thing.PeopleNumber?
    ): ThingMatch {
        val mismatches = mutableSetOf<String>()
        var score = 4 // Start with max score

        // Check each criterion based on filter type
        val filters = listOf(
            Triple("priceRange", price, Thing.priceRangeFilterType to thing.priceRange),
            Triple("weather", weather, Thing.weatherFilterType to thing.weatherRequirements),
            Triple("time", time, Thing.timeFilterType to thing.timeRequired),
            Triple("people", people, Thing.peopleNumberFilterType to thing.peopleNumber)
        )

        filters.forEach { (fieldName, filterValue, filterTypeAndProperty) ->
            val (filterType, property) = filterTypeAndProperty
            if (filterValue != null) {
                val match = when (filterType) {
                    FilterType.INCLUSIVE -> property.ordinal <= filterValue.ordinal
                    FilterType.EXCLUSIVE -> property == filterValue
                }
                if (!match) {
                    mismatches.add(fieldName)
                    score--
                }
            }
        }

        return ThingMatch(thing, score, mismatches)
    }

    fun deleteThing(thing: Thing) {
        viewModelScope.launch {
            thingDao.deleteThing(thing)
        }
    }
} 