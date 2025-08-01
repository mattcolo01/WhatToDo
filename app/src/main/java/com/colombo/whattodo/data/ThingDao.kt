package com.colombo.whattodo.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ThingDao {
    @Query("SELECT * FROM things")
    fun getAllThings(): Flow<List<Thing>>

    @Query("SELECT * FROM things WHERE id = :id")
    suspend fun getThingById(id: Long): Thing?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThing(thing: Thing)

    @Delete
    suspend fun deleteThing(thing: Thing)
} 