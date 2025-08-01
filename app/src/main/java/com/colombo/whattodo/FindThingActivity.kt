package com.colombo.whattodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.colombo.whattodo.data.Thing
import com.colombo.whattodo.shared.LoadingSpinner
import com.colombo.whattodo.shared.ThingFilters
import com.colombo.whattodo.viewmodels.FindThingViewModel
import com.colombo.whattodo.viewmodels.ThingMatch
import com.colombo.whattodo.ui.theme.WhatToDoTheme

class FindThingActivity : ComponentActivity() {
    private val viewModel: FindThingViewModel by viewModels()
    private val adManager by lazy { (application as WhatToDoApplication).adManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            WhatToDoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FindThingScreen(
                        viewModel = viewModel,
                        onBackClick = { finish() },
                        onShowInterstitialAd = { adManager.showInterstitialAd(this) {} },
                        bannerAd = { callback ->
                            adManager.BannerAd(callback)
                        }
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
    onBackClick: () -> Unit,
    onShowInterstitialAd: () -> Unit,
    bannerAd: @Composable (() -> Unit) -> Unit = {}
) {
    var thingToDelete by remember { mutableStateOf<Thing?>(null) }
    val matchingThings by viewModel.matchingThings.collectAsState()
    var filtersExpanded by remember { mutableStateOf(true) }
    val arrowRotation by animateFloatAsState(if (filtersExpanded) 180f else 0f, label = "arrowRotation")

    // Delete confirmation dialog
    if (thingToDelete != null) {
        AlertDialog(
            onDismissRequest = { thingToDelete = null },
            title = { Text(stringResource(R.string.delete_activity)) },
            text = { Text(stringResource(R.string.delete_activity_confirmation, thingToDelete?.name ?: "")) },
            confirmButton = {
                TextButton(
                    onClick = {
                        thingToDelete?.let { 
                            viewModel.deleteThing(it)
                            thingToDelete = null
                            onShowInterstitialAd()
                        }
                    }
                ) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { thingToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.find_activity)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
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
                    text = stringResource(R.string.what_would_you_like_to_do),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                if (filtersExpanded) {
                    ThingFilters(MaterialTheme.colorScheme.secondary) { priceRange, weather, timeRequired, peopleNumber ->
                        viewModel.updateFilters(
                            priceRange = priceRange,
                            weatherRequirements = weather,
                            timeRequired = timeRequired,
                            peopleNumber = peopleNumber
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(vertical = 4.dp)
                        .align(Alignment.CenterHorizontally)
                        .let { Modifier.clickable { filtersExpanded = !filtersExpanded } },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Divider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outline
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (filtersExpanded) stringResource(R.string.collapse) else stringResource(R.string.expand),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .rotate(arrowRotation),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    androidx.compose.material3.Divider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
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
                items(matchingThings.size) { index ->
                    val match = matchingThings[index]
                    ThingCard(
                        match = match,
                        onDelete = { thingToDelete = match.thing }
                    )
                    
                    // Show banner ad after every fourth item
                    if (index % 3 == 0) {
                        Spacer(modifier = Modifier.height(16.dp))
                        ThingCard(
                            match = null,
                            ad = bannerAd
                        ) {}
                    }
                }
            }
        }
    }
}

@Composable
fun ThingCard(
    match: ThingMatch?,
    ad: @Composable (() -> Unit) -> Unit = {},
    onDelete: () -> Unit
) {
    var isAdLoaded by remember { mutableStateOf(false) }
    
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
            if (match == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    if (!isAdLoaded) {
                        LoadingSpinner(
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        ad {
                            isAdLoaded = true
                        }
                    }
                }
                return@Column
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.thing.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDelete,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_activity)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AttributeChip(
                    text = match.thing.priceRange.getDisplayName(),
                    isError = match.mismatchedFields.contains("priceRange")
                )
                AttributeChip(
                    text = match.thing.weatherRequirements.getDisplayName(),
                    isError = match.mismatchedFields.contains("weather")
                )
                AttributeChip(
                    text = match.thing.timeRequired.getDisplayName(),
                    isError = match.mismatchedFields.contains("time")
                )
                AttributeChip(
                    text = match.thing.peopleNumber.getDisplayName(),
                    isError = match.mismatchedFields.contains("people")
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