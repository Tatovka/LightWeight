package com.example.lightweight.Database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

class ResultsRepo(val dao: DayDAO) {

    suspend fun getResults(key: Long): List<ResultEntity> =
        withContext(Dispatchers.IO) {
            dao.getDayResults(key)
        }

    suspend fun insertResult(resultTuple: ResultEntity) =
        withContext(Dispatchers.IO) {
            dao.insertNewResult(resultTuple)
        }

    suspend fun getApproaches(resId: Long) =
        withContext(Dispatchers.IO) {
            dao.getApproaches(resId)
        }

    suspend fun getAllApproaches() =
        withContext(Dispatchers.IO) {
            dao.getAllApproaches()
        }

    suspend fun insertApproach(approachEntity: ApproachEntity) =
        withContext(Dispatchers.IO) {
        dao.insertNewApproach(approachEntity)
    }

}

class ExerciseRepo(val dao: ExerciseDAO) {
    suspend fun getAll(): ConcurrentHashMap<Long, ExerciseEntity> {
        val list = withContext(Dispatchers.IO) {
            dao.getAll()
        }
        return ConcurrentHashMap(list.groupBy { it.id }.mapValues { it.value[0] })
    }

    suspend fun insertExercise(exerciseTuple: ExerciseEntity) =
        withContext(Dispatchers.IO) { dao.insertNewExercise(exerciseTuple) }
}