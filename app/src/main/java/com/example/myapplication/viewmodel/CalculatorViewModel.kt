package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.model.CalculatorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

const val numbers = "0123456789"
const val operators = "+-*/"
const val clear = "C"
const val equals = "="

class CalculatorViewModel : ViewModel() {
    private val _calculatorState = MutableStateFlow(CalculatorState())
    val calculatorState: StateFlow<CalculatorState> = _calculatorState.asStateFlow()

    fun toggleHistoryDialogue() {
        _calculatorState.value = _calculatorState.value.copy(
            isHistoryShown = !_calculatorState.value.isHistoryShown
        )
    }

    fun onButtonClick(newSymbol: String) {
        if (newSymbol.isEmpty()) {
            throw Error()
        }

        val isCurrentInputEmpty = calculatorState.value.input.isEmpty()
        val isNewSymbolNumber = numbers.contains(newSymbol)
        val isNewSymbolOperator = operators.contains(newSymbol)
        val isNewSymbolClear = (newSymbol === clear)
        val isNewSymbolEquals = (newSymbol === equals)

        var lastSymbol = ""

        if (!isCurrentInputEmpty) {
            lastSymbol = calculatorState.value.input.last().toString()
        }

        var isLastSymbolNumber = false
        var isLastSymbolOperator = false

        if (lastSymbol.isNotEmpty()) {
            isLastSymbolNumber = numbers.contains(lastSymbol)
            isLastSymbolOperator = operators.contains(lastSymbol)
        }

        when {
            isNewSymbolNumber -> {
                if (isCurrentInputEmpty || isLastSymbolNumber || isLastSymbolOperator) {
                    _calculatorState.value = _calculatorState.value.copy(
                        input = _calculatorState.value.input + newSymbol
                    )
                }
            }

            isNewSymbolOperator -> {
                if (isLastSymbolNumber) {
                    _calculatorState.value = _calculatorState.value.copy(
                        input = _calculatorState.value.input + newSymbol
                    )
                }
            }

            isNewSymbolClear -> {
                if (!isCurrentInputEmpty) {
                    _calculatorState.value = _calculatorState.value.copy(
                        result = "",
                        input = _calculatorState.value.input.dropLast(1)
                    )
                }
            }

            isNewSymbolEquals -> {
                val res = evaluate(calculatorState.value.input)
                _calculatorState.value = _calculatorState.value.copy(
                    result = res
                )
                if (!res.startsWith("Error")) {
                    _calculatorState.value.history.add("${_calculatorState.value.input} = $res")
                }
            }

            else -> {
                _calculatorState.value = _calculatorState.value.copy(
                    result = "Error"
                )
            }
        }

    }

    //TODO: Allow unary minus
    //TODO: Allow Floats
    //TODO: Catch Errors and Edge-Cases (Number Overflows, Validation)

    private fun evaluate(expression: String): String {
        if (expression.isEmpty()) return "Error: Invalid Expression"

        if (!numbers.contains(expression.first())) return "Error: Invalid Expression"

        if (!numbers.contains(expression.last())) return "Error: Invalid Expression"

        var currNumber = ""
        val tokenList = emptyList<String>().toMutableList()

        val e = expression.iterator()
        while (e.hasNext()) {
            val curr = e.next()
            if (numbers.contains(curr)) {
                currNumber += curr
            } else if (operators.contains(curr)) {
                if (currNumber.isNotEmpty()) {
                    tokenList.add(currNumber)
                    currNumber = ""
                }
                tokenList.add(curr.toString())
            }
        }
        if (currNumber.isNotEmpty()) tokenList.add(currNumber)

        // primary: "*", "/"
        // secondary: "+", "-"

        try {
            while (tokenList.count() > 1) {
                val indexOfFirstPrimaryOperator =
                    tokenList.indexOfFirst { token -> token == "*" || token == "/" }
                if (indexOfFirstPrimaryOperator != -1) {
                    when (tokenList[indexOfFirstPrimaryOperator]) {
                        "*" -> {
                            val res =
                                tokenList[indexOfFirstPrimaryOperator - 1].toInt() * tokenList[indexOfFirstPrimaryOperator + 1].toInt()
                            tokenList[indexOfFirstPrimaryOperator - 1] = res.toString();
                            tokenList.removeAt(indexOfFirstPrimaryOperator + 1)
                            tokenList.removeAt(indexOfFirstPrimaryOperator)
                        }

                        "/" -> {
                            if (tokenList[indexOfFirstPrimaryOperator + 1].toInt() == 0) throw Error(
                                "Zero Division"
                            )
                            val res =
                                tokenList[indexOfFirstPrimaryOperator - 1].toInt() / tokenList[indexOfFirstPrimaryOperator + 1].toInt()
                            tokenList[indexOfFirstPrimaryOperator - 1] = res.toString();
                            tokenList.removeAt(indexOfFirstPrimaryOperator + 1)
                            tokenList.removeAt(indexOfFirstPrimaryOperator)
                        }
                    }
                } else {
                    val indexOfFirstSecondaryOperator =
                        tokenList.indexOfFirst { token -> token == "+" || token == "-" }
                    when (tokenList[indexOfFirstSecondaryOperator]) {
                        "+" -> {
                            val res =
                                tokenList[indexOfFirstSecondaryOperator - 1].toInt() + tokenList[indexOfFirstSecondaryOperator + 1].toInt()
                            tokenList[indexOfFirstSecondaryOperator - 1] = res.toString();
                            tokenList.removeAt(indexOfFirstSecondaryOperator + 1)
                            tokenList.removeAt(indexOfFirstSecondaryOperator)
                        }

                        "-" -> {
                            val res =
                                tokenList[indexOfFirstSecondaryOperator - 1].toInt() - tokenList[indexOfFirstSecondaryOperator + 1].toInt()
                            tokenList[indexOfFirstSecondaryOperator - 1] = res.toString();
                            tokenList.removeAt(indexOfFirstSecondaryOperator + 1)
                            tokenList.removeAt(indexOfFirstSecondaryOperator)
                        }
                    }
                }

            }
        } catch (e: Error) {
            return "Error: $e"
        }

        return tokenList[0]
    }
}