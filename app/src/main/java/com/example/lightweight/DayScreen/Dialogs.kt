@file:OptIn(ExperimentalMaterial3Api::class)


package com.example.lightweight.DayScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.lightweight.Database.ExerciseEntity
import com.example.lightweight.charts.ExerciseStat

@Composable
fun AddDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
                Button(
                    onClick = onConfirmation,
                ) {
                    Text("Ok")
                }
            }
        }
    }
}


@Composable
fun AddResultDialog(
    openedDialog: OpenedDialog.AddResult,
    onDismissRequest: () -> Unit,
) {
    var chosenIndex by remember { mutableIntStateOf(0) }
    AddDialog(
        onDismissRequest = onDismissRequest,
        onConfirmation = {
            if (openedDialog.exercises.isNotEmpty()) {
                openedDialog.onConfirmation(openedDialog.exercises[chosenIndex].id)
                onDismissRequest()
            }
        },
    ) {
        Text(
            "Add result",
            Modifier
                .fillMaxWidth()
                .padding(4.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        ExercisesDropDown(
            openedDialog.exercises,
            chosenIndex, { index -> chosenIndex = index },
            openedDialog.openAddExercise,
            plusButton = true,
        )

    }
}

@Composable
fun ExercisesDropDown(
    exercises: List<ExerciseEntity>,
    chosenIndex: Int,
    selector: (Int) -> Unit,
    openAddExDialog: () -> Unit = {},
    plusButton: Boolean = false,
) {
    var expanded: Boolean by remember { mutableStateOf(false) }
    val dismissRequest = { expanded = false }

    Box {
        if (exercises.isEmpty())
            TextButton(
                onClick = openAddExDialog,
            ) { Text("+") }
        else {
            TextButton(onClick = { expanded = !expanded }) {
                Text(
                    text = exercises[chosenIndex].name
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = dismissRequest,
                offset = DpOffset.Zero
            ) {
                exercises.forEachIndexed { ind, ex ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                ex.name,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        onClick = {
                            selector(ind)
                            dismissRequest()
                        },
                    )
                }
                if (plusButton) DropdownMenuItem(
                    text = {
                        Text(
                            "+",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    onClick = openAddExDialog,
                )
            }
        }
    }
}

@Composable
fun AddApproachDialog(
    exercise: ExerciseEntity,
    onDismissRequest: () -> Unit,
    onConfirmation: (Int, Int, String) -> Unit
) {
    var res1 by remember { mutableStateOf(TextFieldValue("")) }
    var res2 by remember { mutableStateOf(TextFieldValue("")) }
    var comment by remember { mutableStateOf(TextFieldValue("")) }

    AddDialog(
        onDismissRequest = onDismissRequest,
        onConfirmation = {
            val r1 = res1.text.toIntOrNull()
            val r2 = res2.text.toIntOrNull()
            if (r1 != null && (r2 != null || exercise.unit2 == null))
                onConfirmation(r1, r2 ?: 0, comment.text)
        },
    ) {
        Text(
            "Add approach",
            Modifier
                .fillMaxWidth()
                .padding(4.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Row {
            OutlinedTextField(
                value = res1,
                onValueChange = { value -> res1 = value },
                label = { Text(exercise.unit) },
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(1.8f)
                    .padding(horizontal = 4.dp),
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            if (exercise.unit2 != null) OutlinedTextField(
                value = res2,
                onValueChange = { value -> res2 = value },
                label = { Text(exercise.unit2) },
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(1.8f)
                    .padding(horizontal = 4.dp),
                maxLines = 1,

                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = comment,
                onValueChange = { value -> comment = value },
                label = { Text("comment") },
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(1.8f)
                    .padding(horizontal = 4.dp),
                maxLines = 1
            )

        }
    }
}

@Composable
fun AddExerciseDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String, String, String) -> Unit,
) {

    var name by remember { mutableStateOf(TextFieldValue("")) }
    var unit by remember { mutableStateOf(TextFieldValue("")) }
    var unit2 by remember { mutableStateOf(TextFieldValue("")) }

    AddDialog(
        onDismissRequest = onDismissRequest,
        onConfirmation = {
            if (name.text.isNotEmpty() && unit.text.isNotEmpty())
                onConfirmation(name.text, unit.text, unit2.text)
        },
    ) {
        Text(
            "Add exercise",
            Modifier
                .fillMaxWidth()
                .padding(4.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = name,
            onValueChange = { value -> name = value },
            label = { Text("name") },
            modifier = Modifier
                .width(200.dp)
                .padding(8.dp),
            singleLine = true
        )
        Row {
            OutlinedTextField(
                value = unit,
                onValueChange = { value -> unit = value },
                label = { Text("unit") },
                modifier = Modifier
                    .width(100.dp)
                    .padding(8.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = unit2,
                onValueChange = { value -> unit2 = value },
                label = { Text("unit2") },
                modifier = Modifier
                    .width(100.dp)
                    .padding(8.dp),
                singleLine = true
            )
        }
    }

}

@Composable
fun CommentDialog(comment: String, onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(Alignment.CenterVertically),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                comment,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                fontSize = TextUnit(24f, TextUnitType.Sp),
                textAlign = TextAlign.Center,
            )
        }
    }
}


@Composable
fun PickDateDialog(srcDate: Long, onDismissRequest: () -> Unit, onConfirmation: (Long) -> Unit) {
    val pickerState = rememberDatePickerState()
    pickerState.selectedDateMillis = srcDate
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                if (pickerState.selectedDateMillis != null)
                    onConfirmation(pickerState.selectedDateMillis!!)
            }) { Text("ok") }
        },
        dismissButton = { TextButton(onClick = onDismissRequest) { Text("cancel") } },
    ) {
        DatePicker(
            state = pickerState,
        )
    }
}

@Composable
fun ChartBuilderDialog(
    builder: OpenedDialog.ChartBuilder,
    onDismissRequest: () -> Unit
) {
    var chosenIndex by remember { mutableIntStateOf(0) }
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .wrapContentHeight(Alignment.CenterVertically),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Statistic by",
                    Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                ExercisesDropDown(
                    builder.exercises,
                    chosenIndex,
                    { ind -> chosenIndex = ind }
                )

                Button(
                    onClick = {
                        if (builder.exercises.isNotEmpty())
                            builder.buildChart(builder.exercises[chosenIndex].id)
                    },
                ) {
                    Text("Show")
                }
            }
        }
    }
}

@Composable
fun ChartDialog(values: List<Int>, onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        ExerciseStat(values)
    }
}

@Composable
fun LabelDialog(onDismissRequest: () -> Unit) {

}