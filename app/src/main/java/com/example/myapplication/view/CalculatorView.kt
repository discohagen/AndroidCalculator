package com.example.myapplication.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.viewmodel.CalculatorViewModel

const val columnCount = 4
const val rowCount = 4
val backgroundColor: Color = Color(0xff404040)
val buttonBackgroundColor: Color = Color(0xffb06000)
val displayBackgroundColor: Color = Color(0xff505050)
val textColor: Color = Color(0xffffffff)

// TODO: Calculation History
// TODO: Dark and Light Theme

@Preview
@Composable
fun CalculatorView(
    calculatorViewModel: CalculatorViewModel = viewModel()
) {
    val calculatorState by calculatorViewModel.calculatorState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
            .padding(bottom = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
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
        ButtonPad(calculatorViewModel::onButtonClick)
    }
}

@Composable
fun Display(text: String) {
    Box(
        modifier = Modifier
            .padding(20.dp)
            .background(color = displayBackgroundColor)
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.Center
    )
    {
        Text(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
            text = text,
            color = textColor,
            fontSize = 34.sp
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ButtonPad(
    onButtonClick: (String) -> Unit
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
                Button(
                    modifier = Modifier
                        .size(90.dp)
                        .padding(5.dp),
                    onClick = { onButtonClick(text) },
                    colors = ButtonDefaults.buttonColors(containerColor = buttonBackgroundColor)
                ) { Text(fontSize = 18.sp, text = text) }
            }
        }
    }
}
