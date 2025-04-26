package com.colombo.whattodo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "things")
data class Thing(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val priceRange: PriceRange,
    val weatherRequirements: WeatherType,
    val timeRequired: TimeRequired,
    val notes: String = ""
) {
    companion object {
        var priceRangeFilterType: FilterType = FilterType.INCLUSIVE
        var weatherFilterType: FilterType = FilterType.INCLUSIVE
        var timeFilterType: FilterType = FilterType.EXCLUSIVE
    }

    enum class FilterType {
        INCLUSIVE,
        EXCLUSIVE
    }

    enum class PriceRange(val value: Int) {
        FREE(0),
        CHEAP(1),
        MODERATE(2),
        EXPENSIVE(3)
    }

    enum class WeatherType(val value: Int) {
        RAINY(0),
        CLOUDY(1),
        SUNNY(2)
    }

    enum class TimeRequired(val value: Int) {
        QUICK(0),
        HALF_DAY(1),
        FULL_DAY(2)
    }
}