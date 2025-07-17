package com.example.myapplication.model

data class CalculatorState(
    var input: String = "",
    var result: String = "",
    var isHistoryShown: Boolean = false,
    var history: MutableList<String> = emptyList<String>().toMutableList()
)
