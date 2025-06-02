package com.example.lightweight.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [
        ResultEntity::class,
        ExerciseEntity::class,
        ApproachEntity::class
    ]
)
abstract class LwDatabase : RoomDatabase() {
    abstract fun getDayDao(): DayDAO
    abstract fun getExerciseDao(): ExerciseDAO
}
