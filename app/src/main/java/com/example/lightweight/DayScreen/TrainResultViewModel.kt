package com.example.lightweight.DayScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.lightweight.Database.ApproachEntity
import com.example.lightweight.Database.ExerciseEntity
import com.example.lightweight.Database.ExerciseRepo
import com.example.lightweight.Database.ResultEntity
import com.example.lightweight.Database.ResultsRepo
import com.example.lightweight.LwApplication
import kotlinx.coroutines.launch
import java.io.IOException
import java.sql.Date
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

sealed interface TrainResultUiState {
    object Loading : TrainResultUiState
    object Error : TrainResultUiState
    data class Success(val results: List<ResultEntity>) : TrainResultUiState
}

sealed interface OpenedDialog {
    object None : OpenedDialog
    object AddExercise : OpenedDialog
    object AddResult : OpenedDialog
    data class AddApproach(val resId: Long, val ex: ExerciseEntity) : OpenedDialog
    data class Comment(val comment: String) : OpenedDialog
    object DatePicker : OpenedDialog
}

class TrainResultViewModel(val resRepo: ResultsRepo, val exRepo: ExerciseRepo) : ViewModel() {

    var uiState: TrainResultUiState by mutableStateOf(TrainResultUiState.Loading)
    var date: Long by mutableLongStateOf(0)
    var dialogOpened: OpenedDialog by mutableStateOf(OpenedDialog.None)
    var exMap: ConcurrentHashMap<Long, ExerciseEntity> = ConcurrentHashMap<Long, ExerciseEntity>()
    var approachMap: ConcurrentHashMap<Long, List<ApproachEntity>>
            by mutableStateOf(ConcurrentHashMap<Long, List<ApproachEntity>>())

    private val dayOffset = 24 * 3600 * 1000

    init {
        date = LocalDate.now().toEpochDay() * dayOffset
        println(date)
        loadExercises()
        loadDayResults()
    }

    fun loadExercises() {
        viewModelScope.launch {
            exMap = exRepo.getAll()
        }
    }

    fun loadApproaches(results: List<ResultEntity>) {
        viewModelScope.launch {
            val approaches = ConcurrentHashMap<Long, List<ApproachEntity>>()
            val allAp = resRepo.getAllApproaches()
            println(allAp)
            results.asSequence().forEach {
                approaches[it.id] = resRepo.getApproaches(it.id)
                //println(approaches[it.id])
            }
            approachMap = approaches
            approachMap.values.forEach { println(it) }
        }
    }

    fun loadDayResults() {
        uiState = TrainResultUiState.Loading
        viewModelScope.launch {
            uiState = try {
                val res = TrainResultUiState.Success(resRepo.getResults(date))
                loadApproaches(res.results)
                res
            } catch (e: IOException) {
                TrainResultUiState.Error
            }
        }
    }

    fun addApproach(resId: Long, res1: Int, res2: Int, comment: String) {
        viewModelScope
            .launch {
                val entity = ApproachEntity(
                    resId = resId,
                    firstResult = res1,
                    secondResult = res2,
                    comment = comment
                )
                resRepo.insertApproach(
                    ApproachEntity(
                        resId = resId,
                        firstResult = res1,
                        secondResult = res2,
                        comment = comment
                    )
                )
                approachMap[resId] = approachMap[resId]!! + entity
                loadDayResults()
                //updateUi = !updateUi
            }
    }

    fun addResult(exId: Long) {
        viewModelScope
            .launch {
                resRepo.insertResult(ResultEntity(date = date, exId = exId))
                loadDayResults()
            }
    }

    fun addExercise(name: String, unit: String, unit2: String) {
        viewModelScope.launch {
            exRepo.insertExercise(
                ExerciseEntity(name = name, unit = unit, unit2 = unit2.ifEmpty { null })
            )
            loadExercises()
        }
    }

    fun Date() = Date(date)

    fun OpenAddExerciseDialog() {
        dialogOpened = OpenedDialog.AddExercise
    }

    fun OpenAddResultDialog() {
        dialogOpened = OpenedDialog.AddResult
    }

    fun OpenAddApproachDialog(id: Long, ex: ExerciseEntity) {
        dialogOpened = OpenedDialog.AddApproach(id, ex)
    }

    fun OpenedCommentDialog(comment: String) {
        dialogOpened = OpenedDialog.Comment(comment)
    }

    fun OpenCalendar() {
        dialogOpened = OpenedDialog.DatePicker
    }

    fun setDay(millis: Long) {
        date = millis
        loadDayResults()
        println(date)
    }

    fun nextDay() = setDay(date + dayOffset)

    fun previousDay() = setDay(date - dayOffset)

    fun CloseDialogs() {
        dialogOpened = OpenedDialog.None
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as LwApplication)
                val repo = application.container.dayRepo
                val exRepo = application.container.exRepo
                TrainResultViewModel(repo, exRepo)
            }
        }
    }
}