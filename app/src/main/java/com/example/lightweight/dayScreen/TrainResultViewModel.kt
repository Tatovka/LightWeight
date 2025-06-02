package com.example.lightweight.dayScreen

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
import com.example.lightweight.database.ApproachEntity
import com.example.lightweight.database.ExerciseEntity
import com.example.lightweight.database.ExerciseRepo
import com.example.lightweight.database.ResultEntity
import com.example.lightweight.database.ResultsRepo
import com.example.lightweight.LwApplication
import kotlinx.coroutines.launch
import java.io.IOException
import java.sql.Date
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

sealed interface TrainResultUiState {
    data object Loading : TrainResultUiState
    data object Error : TrainResultUiState
    data class Success(val results: List<ResultEntity>) : TrainResultUiState
}

sealed interface OpenedDialog {
    data object None : OpenedDialog
    data object AddExercise : OpenedDialog
    data class AddResult(
        val exercises: List<ExerciseEntity>,
        val onConfirmation: (Long) -> Unit,
        val openAddExercise: () -> Unit,
    ) : OpenedDialog
    data class AddApproach(val resId: Long, val ex: ExerciseEntity) : OpenedDialog
    data class Comment(val comment: String) : OpenedDialog
    data object DatePicker : OpenedDialog
    data class ChartBuilder(val exercises: List<ExerciseEntity>, val buildChart: (Long) -> Unit) :
        OpenedDialog
    data class Chart(val results: List<Int>) : OpenedDialog
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

    private fun loadExercises() {
        viewModelScope.launch {
            exMap = exRepo.getAll()
        }
    }

    private fun loadApproaches(results: List<ResultEntity>) {
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
            }
    }

    fun addResult(exId: Long) {
        viewModelScope
            .launch {
                resRepo.insertResult(ResultEntity(date = date, exId = exId))
                loadDayResults()
            }
    }

    fun addLabel(text: String) {
        viewModelScope
            .launch {
                resRepo.insertResult(ResultEntity(date = date, exId = -1, planned = 2))
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

    fun openAddExerciseDialog() {
        dialogOpened = OpenedDialog.AddExercise
    }

    fun openAddResultDialog() {
        dialogOpened = OpenedDialog.AddResult(
            exMap.values.toList(),
            this::addResult,
            this::openAddExerciseDialog
        )
    }

    fun openAddApproachDialog(id: Long, ex: ExerciseEntity) {
        dialogOpened = OpenedDialog.AddApproach(id, ex)
    }

    fun openCommentDialog(comment: String) {
        dialogOpened = OpenedDialog.Comment(comment)
    }

    fun openChartBuilderDialog() {
        dialogOpened =
            OpenedDialog.ChartBuilder(exMap.values.toList(), { id -> openChartDialog(id) })
    }

    fun openChartDialog(exId: Long) {
        viewModelScope.launch {
            val results = resRepo.getAllExResults(exId).sortedBy { res: ResultEntity -> res.date }
            val best = results.map {
                resRepo.getBestApproach(it.id)?.firstResult
            }.filterNotNull()
            if (best.isNotEmpty())
                dialogOpened = OpenedDialog.Chart(best)
        }
    }

    fun openCalendar() {
        dialogOpened = OpenedDialog.DatePicker
    }

    fun setDay(millis: Long) {
        date = millis
        loadDayResults()
        println(date)
    }

    fun nextDay() = setDay(date + dayOffset)

    fun previousDay() = setDay(date - dayOffset)

    fun closeDialogs() {
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