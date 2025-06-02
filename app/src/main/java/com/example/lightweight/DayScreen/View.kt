package com.example.lightweight.DayScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lightweight.Database.ApproachEntity
import com.example.lightweight.Database.ExerciseEntity
import com.example.lightweight.Database.ResultEntity
import com.example.lightweight.charts.ExerciseStat
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
    else Column(horizontalAlignment = Alignment.Start) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .height(48.dp)
        ) {
            Card(
                onClick = { expanded = !expanded },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            )
            {
                Text(
                    text = exercise.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(20.0f, TextUnitType.Sp),
                    modifier = Modifier
                        .padding(6.dp)
                        .fillMaxHeight(),
                    lineHeight = TextUnit(38f, TextUnitType.Sp)
                )
            }
            Button(
                onClick = { appViewModel.openAddApproachDialog(result.id, exercise) },
                modifier = Modifier
                    .align(Alignment.Top)
                    .size(16.dp)
                    .wrapContentSize()
                    .offset(x = 4.dp),
                contentPadding = PaddingValues(0.dp)
            )
            {
                Text(
                    "+",
                    fontSize = TextUnit(12.0f, TextUnitType.Sp),
                    lineHeight = TextUnit(12.0f, TextUnitType.Sp)
                )
            }
        }

        Card(
            modifier = Modifier
                .wrapContentSize()
                .offset(x = 6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            (if (expanded) approaches else emptyList()).forEach { result ->
                Approach(
                    result, exercise, Modifier
                        //.align(Alignment.CenterHorizontally)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun Approach(approach: ApproachEntity, exercise: ExerciseEntity, modifier: Modifier = Modifier) {
    Row(modifier = modifier
        .height(48.dp)
        .padding(horizontal = 6.dp)) {

        BgText(
            text = "${approach.firstResult} (${exercise.unit})",
            color = MaterialTheme.colorScheme.secondary
        )

        if (exercise.unit2 != null)
            BgText(
                text = "${approach.secondResult} (${exercise.unit2})",
                color = MaterialTheme.colorScheme.secondary
            )

        if (approach.comment.isNotEmpty())
            Button(
                onClick = { appViewModel.openCommentDialog(approach.comment) },
                modifier = Modifier
                    .align(Alignment.Top)
                    .size(16.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "i",
                    modifier = Modifier.fillMaxSize(),
                    fontSize = TextUnit(12.0f, TextUnitType.Sp),
                    textAlign = TextAlign.Center,
                    lineHeight = TextUnit(16f, TextUnitType.Sp)
                )
            }
    }
}

@Composable
fun BgText(text: String, color: Color) {
    Card(
        modifier = Modifier.padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    )
    {
        Text(
            text = text,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxHeight(),
            textAlign = TextAlign.Center,
            lineHeight = TextUnit(38f, TextUnitType.Sp)
        )
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LightWeightTheme {
        Exercise(
            ExerciseEntity(0, "example ex", "unit", "unit2"),
            ResultEntity(0, 0, 0, 0),
            listOf(ApproachEntity(0, 0, 69, 69, "sample comment"))
        )
    }
}