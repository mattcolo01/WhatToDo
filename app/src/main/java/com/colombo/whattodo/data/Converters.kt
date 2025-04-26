package com.colombo.whattodo.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromPriceRange(value: Thing.PriceRange): String = value.name

    @TypeConverter
    fun toPriceRange(value: String): Thing.PriceRange = Thing.PriceRange.valueOf(value)

    @TypeConverter
    fun fromWeatherType(value: Thing.WeatherType): String = value.name

    @TypeConverter
    fun toWeatherType(value: String): Thing.WeatherType = Thing.WeatherType.valueOf(value)

    @TypeConverter
    fun fromTimeRequired(value: Thing.TimeRequired): String = value.name

    @TypeConverter
    fun toTimeRequired(value: String): Thing.TimeRequired = Thing.TimeRequired.valueOf(value)
}