package com.example.myapplication.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.model.CalculatorState
import com.example.myapplication.viewmodel.CalculatorViewModel

const val columnCount = 4
const val rowCount = 4

// TODO: Calculation History
// TODO: Holding C turns C to AC (turns red) and deletes whole input
// TODO: Launch Screen

@Preview
@Composable
fun CalculatorView(
    calculatorViewModel: CalculatorViewModel = viewModel()
) {
    val calculatorState by calculatorViewModel.calculatorState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(bottom = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(calculatorViewModel::toggleHistoryDialogue) {
            Text("History")
        }
        if (calculatorState.isHistoryShown) {
            HistoryDialogue(calculatorState, calculatorViewModel::toggleHistoryDialogue)
        }
        Display(
            "${
                calculatorState.input.ifEmpty {
                    ""
                }
            }${
                if (calculatorState.input.isNotEmpty() && calculatorState.result.isNotEmpty()) {
                    " = " + calculatorState.result
                } else {
                    ""
                }
            }"
        )
        ButtonPad(calculatorViewModel::onButtonClick , calculatorViewModel::onLongClick  )
    }
}

@Composable
fun Display(text: String) {
    Box(
        modifier = Modifier
            .padding(20.dp)
            .clip(shape = RoundedCornerShape(20.dp))
            .background(color = MaterialTheme.colorScheme.tertiary)
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.Center
    )
    {
        Text(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
            text = text,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 34.sp
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun ButtonPad(
    onButtonClick: (String) -> Unit,
    onLongClick: (String) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        maxLines = columnCount,
        maxItemsInEachRow = rowCount,
        horizontalArrangement = Arrangement.Center
    ) {
        val buttonTexts = listOf(
            listOf("7", "8", "9", "+"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "*"),
            listOf("C", "0", "=", "/")
        )

        buttonTexts.forEach { buttonTextRow ->
            buttonTextRow.forEach { text ->
                Box(
                    modifier = Modifier
                        .combinedClickable(onClick = {onButtonClick(text)}, onLongClick = {onLongClick(text)} )
                        .size(90.dp)
                        .padding(5.dp)
                        .clip( shape = CircleShape)
                        .background( color = MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center)

                     {
                    Text(
                        fontSize = 18.sp,
                        text = text,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryDialogue(
    state: CalculatorState,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.tertiary
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        fontSize = 30.sp,
                        color = MaterialTheme.colorScheme.primary,
                        text = "History"
                    )
                    Button(modifier = Modifier, onClick = onDismiss) {
                        Text("Close")
                    }
                }
                state.history.forEach { e ->
                    Text(text = e, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}