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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.colombo.whattodo.data.Thing
import com.colombo.whattodo.shared.SelectorGroup
import com.colombo.whattodo.shared.ThingFilters
import com.colombo.whattodo.viewmodels.AddThingViewModel
import com.colombo.whattodo.ads.AdManager

class AddThingActivity : ComponentActivity() {
    private val viewModel: AddThingViewModel by viewModels()
    private lateinit var adManager: AdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adManager = AdManager(this)
        
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AddThingScreen(
                        onBackClick = { finish() },
                        onSaveClick = { name, priceRange, weather, timeRequired, peopleNumber ->
                            viewModel.saveThing(
                                name = name,
                                priceRange = priceRange,
                                weatherRequirements = weather,
                                timeRequired = timeRequired,
                                peopleNumber = peopleNumber,
                                onSuccess = { 
                                    adManager.showInterstitialAd(this) { finish() }
                                }
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
    onSaveClick: (String, Thing.PriceRange, Thing.WeatherType, Thing.TimeRequired, Thing.PeopleNumber) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedPriceRange by remember { mutableStateOf(Thing.PriceRange.FREE) }
    var selectedWeather by remember { mutableStateOf(Thing.WeatherType.RAINY) }
    var selectedDuration by remember { mutableStateOf(Thing.TimeRequired.QUICK) }
    var selectedPeopleNumber by remember { mutableStateOf(Thing.PeopleNumber.ONE) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_new_activity)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
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
                label = { Text(stringResource(R.string.activity_name)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            ThingFilters (MaterialTheme.colorScheme.primary, true) { priceRange, weather, timeRequired, peopleNumber ->
                selectedPriceRange = priceRange
                selectedWeather = weather
                selectedDuration = timeRequired
                selectedPeopleNumber = peopleNumber
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onSaveClick(
                        name,
                        selectedPriceRange,
                        selectedWeather,
                        selectedDuration,
                        selectedPeopleNumber
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                enabled = name.isNotBlank()
            ) {
                Text(stringResource(R.string.save_activity), modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}