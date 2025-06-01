@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.lightweight

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.lightweight.DayScreen.TrainResultViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lightweight.Database.ExerciseEntity
import com.example.lightweight.DayScreen.AddApproachDialog
import com.example.lightweight.DayScreen.AddExerciseDialog
import com.example.lightweight.DayScreen.AddResultDialog
import com.example.lightweight.DayScreen.CommentDialog
import com.example.lightweight.DayScreen.DayScreen
import com.example.lightweight.DayScreen.OpenedDialog
import com.example.lightweight.DayScreen.PickDateDialog

@Composable
fun DayWindow() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val appViewModel: TrainResultViewModel = viewModel(factory = TrainResultViewModel.Factory)
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),

        floatingActionButton = {
            FloatingActionButton(appViewModel::OpenAddResultDialog) { Text(text = "+") }
        },

        topBar = { TopBar(
            "${appViewModel.Date()}",
            scrollBehavior,
            appViewModel::OpenCalendar,
            appViewModel::previousDay,
            appViewModel::nextDay
        ) }) { innerPadding ->

        val dialog = appViewModel.dialogOpened
        when (dialog) {
            is OpenedDialog.AddResult -> AddResultDialog(
                onDismissRequest = appViewModel::CloseDialogs,
                onConfirmation = { exId ->
                    appViewModel.addResult(exId)
                    appViewModel.CloseDialogs()
                },
                exercises = appViewModel.exMap.values.toList(),
                modelView = appViewModel
            )

            is OpenedDialog.AddExercise -> AddExerciseDialog(
                onDismissRequest = appViewModel::CloseDialogs,
                onConfirmation = { name, unit, unit2 ->
                    appViewModel.addExercise(name, unit, unit2)
                    appViewModel.CloseDialogs()
                },
            )

            is OpenedDialog.Comment -> CommentDialog(dialog.comment, appViewModel::CloseDialogs)

            is OpenedDialog.AddApproach -> AddApproachDialog(
                exercise = dialog.ex,
                onDismissRequest = appViewModel::CloseDialogs,
                onConfirmation = { r1, r2, com ->
                    appViewModel.addApproach(resId = dialog.resId, res1 = r1, res2 = r2, com)
                    appViewModel.CloseDialogs()
                }
            )

            is OpenedDialog.DatePicker -> PickDateDialog(
                appViewModel.date,
                onDismissRequest = appViewModel::CloseDialogs,
                onConfirmation = {
                    date -> appViewModel.setDay(date)
                    appViewModel.CloseDialogs()
                }
                )

            is OpenedDialog.None -> {}
        }
        DayScreen(appViewModel, innerPadding, appViewModel.uiState, appViewModel.exMap, appViewModel.approachMap)
    }
}

@Composable
fun TopBar(text: String,
           scrollBehavior: TopAppBarScrollBehavior,
           pickDate: () -> Unit,
           pickPreviousDay: () -> Unit,
           pickNextDay: () -> Unit
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Row {
                Button(onClick = pickPreviousDay) { Text("<") }
                Card(onClick = pickDate) {
                    Text(
                        text = text,
                        fontSize = TextUnit(30.0f, TextUnitType.Sp)
                    )
                }
                Button(onClick = pickNextDay) { Text(">") }
            }
        },
    )
}

