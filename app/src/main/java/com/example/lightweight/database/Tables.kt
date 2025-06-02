package com.example.lightweight.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "results")
data class ResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    @ColumnInfo(name = "ex_id") val exId: Long,
    val planned: Int = 0
)

@Entity(tableName = "approaches")
data class ApproachEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "res_id") val resId: Long,
    val firstResult: Int,
    val secondResult: Int? = null,
    val comment: String
)

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val unit: String,
    val unit2: String? = null
)

