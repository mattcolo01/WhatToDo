package com.colombo.whattodo.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.colombo.whattodo.data.Thing
import androidx.compose.ui.res.stringResource
import com.colombo.whattodo.R

@Composable
fun ThingFilters(
    color: Color,
    overrideEverythingAsExclusive: Boolean = false,
    onFiltersChanged: (Thing.PriceRange, Thing.WeatherType, Thing.TimeRequired, Thing.PeopleNumber) -> Unit
) {
    var selectedPriceRange by remember { mutableIntStateOf(0) }
    var selectedWeather by remember { mutableIntStateOf(0) }
    var selectedDuration by remember { mutableIntStateOf(0) }
    var selectedPeopleNumber by remember { mutableIntStateOf(0) }

    val priceRanges = Thing.PriceRange.entries.toList()
    val weatherOptions = Thing.WeatherType.entries.toList()
    val durations = Thing.TimeRequired.entries.toList()
    val peopleNumbers = Thing.PeopleNumber.entries.toList()

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

    val peopleNumberIndices = if (Thing.peopleNumberFilterType == Thing.FilterType.INCLUSIVE && !overrideEverythingAsExclusive) {
        (0..selectedPeopleNumber).toList()
    } else {
        listOf(selectedPeopleNumber)
    }

    SelectorGroup(
        title = stringResource(R.string.price_range),
        options = priceRanges.map { it.getDisplayName() }.toList(),
        selectedIndex = selectedPriceRange,
        selectedIndices = priceRangeIndices,
        onSelectedChange = { 
            selectedPriceRange = it
            onFiltersChanged(
                priceRanges[selectedPriceRange],
                weatherOptions[selectedWeather],
                durations[selectedDuration],
                peopleNumbers[selectedPeopleNumber]
            )
        },
        exclusive = Thing.priceRangeFilterType == Thing.FilterType.EXCLUSIVE || overrideEverythingAsExclusive,
        color = color
    )

    SelectorGroup(
        title = stringResource(R.string.weather),
        options = weatherOptions.map { it.getDisplayName() }.toList(),
        selectedIndex = selectedWeather,
        selectedIndices = weatherIndices,
        onSelectedChange = { 
            selectedWeather = it
            onFiltersChanged(
                priceRanges[selectedPriceRange],
                weatherOptions[selectedWeather],
                durations[selectedDuration],
                peopleNumbers[selectedPeopleNumber]
            )
        },
        exclusive = Thing.weatherFilterType == Thing.FilterType.EXCLUSIVE || overrideEverythingAsExclusive,
        color = color
    )

    SelectorGroup(
        title = stringResource(R.string.time_required),
        options = durations.map { it.getDisplayName() }.toList(),
        selectedIndex = selectedDuration,
        selectedIndices = durationIndices,
        onSelectedChange = { 
            selectedDuration = it
            onFiltersChanged(
                priceRanges[selectedPriceRange],
                weatherOptions[selectedWeather],
                durations[selectedDuration],
                peopleNumbers[selectedPeopleNumber]
            )
        },
        exclusive = Thing.timeFilterType == Thing.FilterType.EXCLUSIVE || overrideEverythingAsExclusive,
        color = color
    )

    SelectorGroup(
        title = stringResource(R.string.people_number),
        options = peopleNumbers.map { it.getDisplayName() }.toList(),
        selectedIndex = selectedPeopleNumber,
        selectedIndices = peopleNumberIndices,
        onSelectedChange = {
            selectedPeopleNumber = it
            onFiltersChanged(
                priceRanges[selectedPriceRange],
                weatherOptions[selectedWeather],
                durations[selectedDuration],
                peopleNumbers[selectedPeopleNumber]
            )
        },
        exclusive = Thing.peopleNumberFilterType == Thing.FilterType.EXCLUSIVE || overrideEverythingAsExclusive,
        color = color
    )
}