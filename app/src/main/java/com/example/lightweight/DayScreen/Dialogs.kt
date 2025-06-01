@file:OptIn(ExperimentalMaterial3Api::class)


package com.example.lightweight.DayScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.lightweight.Database.ExerciseEntity

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
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {
                content()
                Row {
                    TextButton(
                        onClick = onConfirmation,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "confirm"
                        )
                    }

                    TextButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "dismiss"
                        )
                    }
                }

            }
        }
    }
}


@Composable
fun AddResultDialog(
    exercises: List<ExerciseEntity>,
    onDismissRequest: () -> Unit,
    onConfirmation: (Long) -> Unit,
    modelView: TrainResultViewModel
) {
    var chosenIndex by remember { mutableIntStateOf(0) }
    AddDialog(
        onDismissRequest = onDismissRequest,
        onConfirmation = {
            if (exercises.isNotEmpty())
                onConfirmation(exercises[chosenIndex].id)
        },
    ) {
        ExercisesDropDown(exercises, modelView::OpenAddExerciseDialog, chosenIndex)
        { index -> chosenIndex = index }
    }
}

@Composable
fun ExercisesDropDown(
    exercises: List<ExerciseEntity>,
    openAddExDialog: () -> Unit,
    chosenIndex: Int,
    choose: (Int) -> Unit
) {
    var expanded: Boolean by remember { mutableStateOf(false) }
    val dismissRequest = { expanded = false }

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
            onDismissRequest = dismissRequest
        ) {
            exercises.forEachIndexed { ind, ex ->
                DropdownMenuItem(
                    text = { Text(ex.name) },
                    onClick = {
                        choose(ind)
                        dismissRequest()
                    })
            }
            DropdownMenuItem(
                text = { Text("+") },
                onClick = openAddExDialog
            )
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
            if(r1 != null && (r2 != null || exercise.unit2 == null))
            onConfirmation(r1, r2 ?: 0, comment.text)
        },
    ) {
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
        Row {
            OutlinedTextField(
                value = name,
                onValueChange = { value -> name = value },
                label = { Text("name") },
                modifier = Modifier
                    .width(100.dp)
                    .padding(8.dp)
            )
            OutlinedTextField(
                value = unit,
                onValueChange = { value -> unit = value },
                label = { Text("unit") },
                modifier = Modifier
                    .width(100.dp)
                    .padding(8.dp)
            )
            OutlinedTextField(
                value = unit2,
                onValueChange = { value -> unit2 = value },
                label = { Text("unit2(opt.)") },
                modifier = Modifier
                    .width(100.dp)
                    .padding(8.dp)
            )
        }
    }

}

@Composable
fun CommentDialog(comment: String, onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(comment)
        }
    }
}


@Composable
fun PickDateDialog(srcDate: Long, onDismissRequest: () -> Unit, onConfirmation: (Long) -> Unit) {
    val pickerState = rememberDatePickerState()
    pickerState.selectedDateMillis = srcDate
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = { TextButton(onClick = {
            if (pickerState.selectedDateMillis != null)
                onConfirmation(pickerState.selectedDateMillis!!)
        }){ Text("ok") } },
        dismissButton = { TextButton(onClick = onDismissRequest){ Text("cancel") } },
    ) {
        DatePicker(
            state = pickerState,
        )
    }
}