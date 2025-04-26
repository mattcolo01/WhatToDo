package com.colombo.whattodo.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.colombo.whattodo.data.Thing

@Composable
fun ThingFilters(
    overrideEverythingAsExclusive: Boolean = false,
    onFiltersChanged: (Thing.PriceRange, Thing.WeatherType, Thing.TimeRequired) -> Unit
) {
    var selectedPriceRange by remember { mutableIntStateOf(0) }
    var selectedWeather by remember { mutableIntStateOf(0) }
    var selectedDuration by remember { mutableIntStateOf(0) }

    val priceRanges = Thing.PriceRange.entries.toList()
    val weatherOptions = Thing.WeatherType.entries.toList()
    val durations = Thing.TimeRequired.entries.toList()

    // Calculate inclusive indices for each filter type
    val priceRangeIndices = if (Thing.priceRangeFilterType == Thing.FilterType.INCLUSIVE && !overrideEverythingAsExclusive) {
        (0..selectedPriceRange).toList()
    } else {
        listOf(selectedPriceRange)
    }

    val weatherIndices = if (Thing.weatherFilterType == Thing.FilterType.INCLUSIVE && !overrideEverythingAsExclusive) {
        (0..selectedWeather).toList()
    } else {
        listOf(selectedWeather)
    }

    val durationIndices = if (Thing.timeFilterType == Thing.FilterType.INCLUSIVE && !overrideEverythingAsExclusive) {
        (0..selectedDuration).toList()
    } else {
        listOf(selectedDuration)
    }

    SelectorGroup(
        title = "Price Range",
        options = priceRanges.map { it.toString() }.toList(),
        selectedIndex = selectedPriceRange,
        selectedIndices = priceRangeIndices,
        onSelectedChange = { 
            selectedPriceRange = it
            onFiltersChanged(
                priceRanges[selectedPriceRange],
                weatherOptions[selectedWeather],
                durations[selectedDuration]
            )
        },
        exclusive = Thing.priceRangeFilterType == Thing.FilterType.EXCLUSIVE || overrideEverythingAsExclusive
    )

    SelectorGroup(
        title = "Weather Requirements",
        options = weatherOptions.map { it.toString() }.toList(),
        selectedIndex = selectedWeather,
        selectedIndices = weatherIndices,
        onSelectedChange = { 
            selectedWeather = it
            onFiltersChanged(
                priceRanges[selectedPriceRange],
                weatherOptions[selectedWeather],
                durations[selectedDuration]
            )
        },
        exclusive = Thing.weatherFilterType == Thing.FilterType.EXCLUSIVE || overrideEverythingAsExclusive
    )

    SelectorGroup(
        title = "Time Required",
        options = durations.map { it.toString() }.toList(),
        selectedIndex = selectedDuration,
        selectedIndices = durationIndices,
        onSelectedChange = { 
            selectedDuration = it
            onFiltersChanged(
                priceRanges[selectedPriceRange],
                weatherOptions[selectedWeather],
                durations[selectedDuration]
            )
        },
        exclusive = Thing.timeFilterType == Thing.FilterType.EXCLUSIVE || overrideEverythingAsExclusive
    )
} 