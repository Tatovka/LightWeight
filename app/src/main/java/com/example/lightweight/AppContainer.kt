package com.example.lightweight

import android.content.Context
import androidx.room.Room
import com.example.lightweight.database.ExerciseRepo
import com.example.lightweight.database.LwDatabase
import com.example.lightweight.database.ResultsRepo



class AppContainer(val ctx: Context) {


    private val appDatabase: LwDatabase by lazy {
        Room.databaseBuilder(
            ctx,
            LwDatabase::class.java,
            ctx.getDatabasePath("database.db").absolutePath
        ).build()
    }

    val dayRepo = ResultsRepo(appDatabase.getDayDao())

    val exRepo = ExerciseRepo(appDatabase.getExerciseDao())
}