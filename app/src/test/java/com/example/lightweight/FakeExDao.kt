package com.example.lightweight

import com.example.lightweight.Database.ExerciseDAO
import com.example.lightweight.Database.ExerciseEntity

class FakeExDao : ExerciseDAO {
    var exList = listOf(FakeData.sampleEx)
    override fun getAll(): List<ExerciseEntity> =
        exList

    override fun getExercise(exId: Long): List<ExerciseEntity> =
        if (exId == 42L) listOf(FakeData.sampleEx) else emptyList()

    override fun insertNewExercise(entity: ExerciseEntity) {
        exList = exList + entity
    }

    override fun deleteExercise(exId: Long) {

    }
}