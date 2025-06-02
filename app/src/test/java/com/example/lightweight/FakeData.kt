package com.example.lightweight

import com.example.lightweight.Database.ApproachEntity
import com.example.lightweight.Database.ExerciseEntity
import com.example.lightweight.Database.ExerciseRepo
import com.example.lightweight.Database.ResultEntity

object FakeData {
    val sampleEx: ExerciseEntity = ExerciseEntity(42, "sample", "unit1", "unit2")
    val sampleRes: ResultEntity = ResultEntity(43, 52, 42)
    val sampleApproach: ApproachEntity = ApproachEntity(44, 43, 6, 9, "ABOBA")
}
