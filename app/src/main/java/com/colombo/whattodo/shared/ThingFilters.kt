package com.colombo.whattodo.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.colombo.whattodo.data.Thing

@Composable
fun ThingFilters(
    onFiltersChanged: (Thing.PriceRange, Thing.WeatherType, Thing.TimeRequired) -> Unit
) {
    var selectedPriceRange by remember { mutableIntStateOf(0) }
    var selectedWeather by remember { mutableIntStateOf(0) }
    var selectedDuration by remember { mutableIntStateOf(0) }

    val priceRanges = Thing.PriceRange.entries.toList()
    val weatherOptions = Thing.WeatherType.entries.toList()
    val durations = Thing.TimeRequired.entries.toList()

    SelectorGroup(
        title = "Price Range",
        options = priceRanges.map { it.toString() }.toList(),
        selectedIndex = selectedPriceRange,
        onSelectedChange = { 
            selectedPriceRange = it
            onFiltersChanged(
                priceRanges[selectedPriceRange],
                weatherOptions[selectedWeather],
                durations[selectedDuration]
            )
        }
    )

    SelectorGroup(
        title = "Weather Requirements",
        options = weatherOptions.map { it.toString() }.toList(),
        selectedIndex = selectedWeather,
        onSelectedChange = { 
            selectedWeather = it
            onFiltersChanged(
                priceRanges[selectedPriceRange],
                weatherOptions[selectedWeather],
                durations[selectedDuration]
            )
        }
    )

    SelectorGroup(
        title = "Time Required",
        options = durations.map { it.toString() }.toList(),
        selectedIndex = selectedDuration,
        onSelectedChange = { 
            selectedDuration = it
            onFiltersChanged(
                priceRanges[selectedPriceRange],
                weatherOptions[selectedWeather],
                durations[selectedDuration]
            )
        }
    )
} 