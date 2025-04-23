package com.colombo.whattodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.colombo.whattodo.shared.SelectorGroup
import com.colombo.whattodo.viewmodels.FindThingViewModel
import com.colombo.whattodo.viewmodels.ThingMatch

class FindThingActivity : ComponentActivity() {
    private val viewModel: FindThingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FindThingScreen(
                        viewModel = viewModel,
                        onBackClick = { finish() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindThingScreen(
    viewModel: FindThingViewModel,
    onBackClick: () -> Unit
) {
    var selectedPriceRange by remember { mutableStateOf(0) }
    var selectedLocation by remember { mutableStateOf(0) }
    var selectedWeather by remember { mutableStateOf(0) }
    var selectedDuration by remember { mutableStateOf(0) }

    val priceRanges = listOf("Free", "Cheap", "Moderate", "Expensive")
    val locations = listOf("Indoor", "Outdoor")
    val weatherOptions = listOf("Any", "Sunny", "Cloudy", "Rainy")
    val durations = listOf("Quick", "Half Day", "Full Day")

    val matchingThings by viewModel.matchingThings.collectAsState()

    // Update filters whenever any selection changes
    LaunchedEffect(selectedPriceRange, selectedLocation, selectedWeather, selectedDuration) {
        viewModel.updateFilters(
            priceRange = priceRanges[selectedPriceRange],
            isOutdoor = locations[selectedLocation] == "Outdoor",
            weatherRequirements = weatherOptions[selectedWeather],
            timeRequired = durations[selectedDuration]
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find Activity") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                modifier = Modifier.background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filters section
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "What would you like to do today?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )

                SelectorGroup(
                    title = "Price Range",
                    options = priceRanges,
                    selectedIndex = selectedPriceRange,
                    onSelectedChange = { selectedPriceRange = it }
                )

                SelectorGroup(
                    title = "Location",
                    options = locations,
                    selectedIndex = selectedLocation,
                    onSelectedChange = { selectedLocation = it }
                )

                SelectorGroup(
                    title = "Weather Requirements",
                    options = weatherOptions,
                    selectedIndex = selectedWeather,
                    onSelectedChange = { selectedWeather = it }
                )

                SelectorGroup(
                    title = "Time Required",
                    options = durations,
                    selectedIndex = selectedDuration,
                    onSelectedChange = { selectedDuration = it }
                )
            }

            // Results section
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(matchingThings) { match ->
                    ThingCard(match = match)
                }
            }
        }
    }
}

@Composable
fun ThingCard(match: ThingMatch) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = match.thing.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AttributeChip(
                    text = match.thing.priceRange,
                    isError = match.mismatchedFields.contains("priceRange")
                )
                AttributeChip(
                    text = if (match.thing.isOutdoor) "Outdoor" else "Indoor",
                    isError = match.mismatchedFields.contains("location")
                )
                AttributeChip(
                    text = match.thing.weatherRequirements,
                    isError = match.mismatchedFields.contains("weather")
                )
                AttributeChip(
                    text = match.thing.timeRequired,
                    isError = match.mismatchedFields.contains("time")
                )
            }
        }
    }
}

@Composable
fun AttributeChip(
    text: String,
    isError: Boolean
) {
    Surface(
        color = if (isError) {
            Color(0xFFFFE5E5) // Light bright red background
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        },
        contentColor = if (isError) {
            Color(0xFFE50000) // Bright red text
        } else {
            MaterialTheme.colorScheme.onSecondaryContainer
        },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(end = 4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}