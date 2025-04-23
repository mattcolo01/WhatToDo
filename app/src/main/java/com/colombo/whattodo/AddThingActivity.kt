package com.colombo.whattodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.colombo.whattodo.shared.SelectorGroup
import com.colombo.whattodo.viewmodels.AddThingViewModel

class AddThingActivity : ComponentActivity() {
    private val viewModel: AddThingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AddThingScreen(
                        onBackClick = { finish() },
                        onSaveClick = { name, priceRange, isOutdoor, weather, timeRequired ->
                            viewModel.saveThing(
                                name = name,
                                priceRange = priceRange,
                                isOutdoor = isOutdoor,
                                weatherRequirements = weather,
                                timeRequired = timeRequired,
                                onSuccess = { finish() }
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddThingScreen(
    onBackClick: () -> Unit,
    onSaveClick: (String, String, Boolean, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedPriceRange by remember { mutableStateOf(0) }
    var selectedLocation by remember { mutableStateOf(0) }
    var selectedWeather by remember { mutableStateOf(0) }
    var selectedDuration by remember { mutableStateOf(0) }

    val priceRanges = listOf("Free", "Cheap", "Moderate", "Expensive")
    val locations = listOf("Indoor", "Outdoor")
    val weatherOptions = listOf("Any", "Sunny", "Cloudy", "Rainy")
    val durations = listOf("Quick", "Half Day", "Full Day")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Activity") },
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
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Activity Name / Description") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
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

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onSaveClick(
                        name,
                        priceRanges[selectedPriceRange],
                        locations[selectedLocation] == "Outdoor",
                        weatherOptions[selectedWeather],
                        durations[selectedDuration]
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                enabled = name.isNotBlank()
            ) {
                Text("Save Activity", modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}