package com.example.lightweight.Database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface DayDAO {
    @Query("Select * From results where date = :dayDate and planned = 0")
    fun getDayResults(dayDate: Long): List<ResultEntity>

    @Insert(entity = ResultEntity::class)
    fun insertNewResult(entity: ResultEntity)

    @Query("Select * From approaches where res_id = :resId")
    fun getApproaches(resId: Long): List<ApproachEntity>

    @Insert(entity = ApproachEntity::class)
    fun insertNewApproach(entity: ApproachEntity)

    @Query("Select * From approaches")
    fun getAllApproaches(): List<ApproachEntity>

    @Query("Select * From results where ex_id = :exId")
    fun getAllExResults(exId: Long): List<ResultEntity>
}

@Dao
interface ExerciseDAO {
    @Query("Select * From exercises where id = :exId")
    fun getExercise(exId: Long): List<ExerciseEntity>

    @Query("Select * From exercises")
    fun getAll(): List<ExerciseEntity>

    @Insert(entity = ExerciseEntity::class)
    fun insertNewExercise(entity: ExerciseEntity)

    @Query("Delete From exercises where id = :exId")
    fun deleteExercise(exId: Long)
}


