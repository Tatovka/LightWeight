package com.example.lightweight.DayScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.lightweight.Database.ApproachEntity
import com.example.lightweight.Database.ExerciseEntity
import com.example.lightweight.Database.ResultEntity
import com.example.lightweight.ui.theme.LightWeightTheme

private lateinit var appViewModel: TrainResultViewModel

@Composable
fun DayScreen(
    viewModel: TrainResultViewModel,
    padding: PaddingValues,
    uiState: TrainResultUiState,
    exercises: Map<Long, ExerciseEntity>,
    approaches: Map<Long, List<ApproachEntity>>
) {
    appViewModel = viewModel
    when (uiState) {
        is TrainResultUiState.Loading -> LoadingScreen()
        is TrainResultUiState.Error -> ErrorScreen()
        is TrainResultUiState.Success -> SuccessScreen(
            exercises,
            uiState.results,
            approaches,
            padding
        )
    }
}

@Composable
fun LoadingScreen() {
    Text(
        text = "Loading..."
    )
}

@Composable
fun ErrorScreen() {
    Text(
        text = "Error!"
    )
}

@Composable
fun Exercise(exercise: ExerciseEntity?, result: ResultEntity, approaches: List<ApproachEntity>) {
    var expanded: Boolean by remember { mutableStateOf(true) }
    if (exercise == null)
        Text(
            text = "Unknown exercise id ${result.exId}",
            fontStyle = FontStyle.Italic
        )
    else Column {
        Row {
            Card(modifier = Modifier.padding(vertical = 8.dp), onClick = { expanded = !expanded })
            {
                Text(
                    text = exercise.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(20.0f, TextUnitType.Sp),
                    modifier = Modifier.padding(6.dp)
                )
            }
            Button(onClick = { appViewModel.OpenAddApproachDialog(result.id, exercise) })
            { Text("+") }
        }

        (if (expanded) approaches else emptyList()).forEach { result ->
            Approach(result, exercise)
            println(result)
        }
    }
}

@Composable
fun Approach(approach: ApproachEntity, exercise: ExerciseEntity) {
    Row {
        Card { Text("${approach.firstResult} (${exercise.unit})") }
        if (exercise.unit2 != null)
            Card { Text("${approach.secondResult} (${exercise.unit2})") }
        if (approach.comment.isNotEmpty())
            Card(onClick = { appViewModel.OpenedCommentDialog(approach.comment) }) { Text("i") }
    }
}

@Composable
fun SuccessScreen(
    exercises: Map<Long, ExerciseEntity>,
    results: List<ResultEntity>,
    approaches: Map<Long, List<ApproachEntity>>,
    padding: PaddingValues
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = Modifier.padding(padding),
        contentPadding = PaddingValues(8.dp),
    ) {
        items(items = results, key = { result -> result.id }) { result ->
            Exercise(exercises[result.exId], result, approaches[result.id] ?: emptyList())
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    LightWeightTheme {
//        Greeting("Android")
//    }
//}