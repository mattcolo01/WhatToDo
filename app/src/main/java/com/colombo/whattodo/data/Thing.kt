package com.colombo.whattodo.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.colombo.whattodo.R

@Entity(tableName = "things")
data class Thing(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val priceRange: PriceRange,
    val weatherRequirements: WeatherType,
    val timeRequired: TimeRequired,
    val peopleNumber: PeopleNumber,
    val notes: String = ""
) {
    companion object {
        var priceRangeFilterType: FilterType = FilterType.INCLUSIVE
        var weatherFilterType: FilterType = FilterType.INCLUSIVE
        var timeFilterType: FilterType = FilterType.EXCLUSIVE
        var peopleNumberFilterType: FilterType = FilterType.EXCLUSIVE
    }

    enum class FilterType {
        INCLUSIVE,
        EXCLUSIVE
    }

    enum class PriceRange(val value: Int) {
        FREE(0),
        CHEAP(1),
        MODERATE(2),
        EXPENSIVE(3);

        @Composable
        fun getDisplayName(): String = when (this) {
            FREE -> stringResource(R.string.free)
            CHEAP -> stringResource(R.string.cheap)
            MODERATE -> stringResource(R.string.moderate)
            EXPENSIVE -> stringResource(R.string.expensive)
        }
    }

    enum class WeatherType(val value: Int) {
        RAINY(0),
        CLOUDY(1),
        SUNNY(2);

        @Composable
        fun getDisplayName(): String = when (this) {
            RAINY -> stringResource(R.string.rainy)
            CLOUDY -> stringResource(R.string.cloudy)
            SUNNY -> stringResource(R.string.sunny)
        }
    }

    enum class TimeRequired(val value: Int) {
        QUICK(0),
        HALF_DAY(1),
        FULL_DAY(2);

        @Composable
        fun getDisplayName(): String = when (this) {
            QUICK -> stringResource(R.string.quick)
            HALF_DAY -> stringResource(R.string.half_day)
            FULL_DAY -> stringResource(R.string.full_day)
        }
    }

    enum class PeopleNumber(val value: Int) {
        ONE(0),
        TWO(1),
        SMALL_GROUP(2),
        LARGE_GROUP(3);

        @Composable
        fun getDisplayName(): String = when (this) {
            ONE -> stringResource(R.string.one)
            TWO -> stringResource(R.string.two)
            SMALL_GROUP -> stringResource(R.string.small_group)
            LARGE_GROUP -> stringResource(R.string.large_group)
        }
    }
}