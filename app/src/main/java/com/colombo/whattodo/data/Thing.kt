package com.colombo.whattodo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "things")
data class Thing(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val priceRange: String, // e.g., "Free", "Cheap", "Moderate", "Expensive"
    val isOutdoor: Boolean,
    val weatherRequirements: String, // e.g., "Any", "Sunny", "Rainy", "Cloudy"
    val timeRequired: String, // e.g., "Quick", "Half day", "Full day"
    val notes: String = ""
) 