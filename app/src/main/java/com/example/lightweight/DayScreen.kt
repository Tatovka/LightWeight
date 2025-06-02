@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.lightweight

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.lightweight.DayScreen.TrainResultViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lightweight.DayScreen.AddApproachDialog
import com.example.lightweight.DayScreen.AddExerciseDialog
import com.example.lightweight.DayScreen.AddResultDialog
import com.example.lightweight.DayScreen.ChartBuilderDialog
import com.example.lightweight.DayScreen.ChartDialog
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
            Column {
                FloatingActionButton(appViewModel::openAddResultDialog) { Text(text = "+") }
                Spacer(Modifier.padding(6.dp))
                FloatingActionButton(appViewModel::openChartBuilderDialog) { Text(text = "Stat") }
            }
        },

        topBar = {
            TopBar(
                "${appViewModel.Date()}",
                scrollBehavior,
                appViewModel::openCalendar,
                appViewModel::previousDay,
                appViewModel::nextDay
            )
        }) { innerPadding ->

        val dialog = appViewModel.dialogOpened
        when (dialog) {
            is OpenedDialog.AddResult -> AddResultDialog(
                dialog,
                onDismissRequest = appViewModel::closeDialogs,
            )

            is OpenedDialog.AddExercise -> AddExerciseDialog(
                onDismissRequest = appViewModel::closeDialogs,
                onConfirmation = { name, unit, unit2 ->
                    appViewModel.addExercise(name, unit, unit2)
                    appViewModel.closeDialogs()
                },
            )

            is OpenedDialog.Comment -> CommentDialog(dialog.comment, appViewModel::closeDialogs)

            is OpenedDialog.AddApproach -> AddApproachDialog(
                exercise = dialog.ex,
                onDismissRequest = appViewModel::closeDialogs,
                onConfirmation = { r1, r2, com ->
                    appViewModel.addApproach(resId = dialog.resId, res1 = r1, res2 = r2, com)
                    appViewModel.closeDialogs()
                }
            )

            is OpenedDialog.DatePicker -> PickDateDialog(
                appViewModel.date,
                onDismissRequest = appViewModel::closeDialogs,
                onConfirmation = { date ->
                    appViewModel.setDay(date)
                    appViewModel.closeDialogs()
                }
            )

            is OpenedDialog.ChartBuilder -> ChartBuilderDialog(
                dialog,
                appViewModel::closeDialogs
            )

            is OpenedDialog.Chart -> ChartDialog(dialog.results, appViewModel::closeDialogs)

            is OpenedDialog.None -> {}
        }
        DayScreen(
            appViewModel,
            innerPadding,
            appViewModel.uiState,
            appViewModel.exMap,
            appViewModel.approachMap
        )
    }
}

@Composable
fun TopBar(
    text: String,
    scrollBehavior: TopAppBarScrollBehavior,
    pickDate: () -> Unit,
    pickPreviousDay: () -> Unit,
    pickNextDay: () -> Unit
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Row {
                TextButton(onClick = pickPreviousDay, Modifier.wrapContentSize()) {
                    Text(
                        text = "<",
                        fontSize = TextUnit(48f, TextUnitType.Sp),
                        textAlign = TextAlign.Left
                    )
                }
                Card(
                    onClick = pickDate,
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .align(Alignment.CenterVertically)
                        .offset(40.dp)
                ) {
                    Text(
                        text = text,
                        fontSize = TextUnit(30.0f, TextUnitType.Sp)
                    )
                }
                TextButton(onClick = pickNextDay, modifier = Modifier.offset(90.dp),) {
                    Text(
                        text = ">",
                        fontSize = TextUnit(48f, TextUnitType.Sp),
                        textAlign = TextAlign.Right
                    )
                }
            }
        },
    )
}

