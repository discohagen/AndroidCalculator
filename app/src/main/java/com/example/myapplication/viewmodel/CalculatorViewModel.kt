package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
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
                _calculatorState.value = _calculatorState.value.copy(
                    result = evaluate(calculatorState.value.input)
                )
            }

            else -> {
                _calculatorState.value = _calculatorState.value.copy(
                    result = "Error"
                )
            }
        }

    }

    //TODO: Improve evaluate to accept multiple Operators
    //TODO: Improve evaluate to do multiplying and dividing before plus and minus
    //TODO: Catch Errors and Edge-Cases (Number Overflows, Validation)

    private fun evaluate(expression: String): String {
        if (expression.isEmpty()) return "Error: No Expression to evaluate"

        if (expression.count { it in operators } != 1) return "Error: Exactly one Operator needed"

        if (!numbers.contains(expression.first())) return "Error: First Character needs to be a Number"

        if (!numbers.contains(expression.last())) return "Error: Last Character needs to be a Number"

        when {
            expression.contains("+") -> {
                val numberList = expression.split("+")
                return (numberList[0].toInt() + numberList[1].toInt()).toString()
            }

            expression.contains("-") -> {
                val numberList = expression.split("-")
                return (numberList[0].toInt() - numberList[1].toInt()).toString()
            }

            expression.contains("*") -> {
                val numberList = expression.split("*")
                return (numberList[0].toInt() * numberList[1].toInt()).toString()
            }

            expression.contains("/") -> {
                val numberList = expression.split("/")
                if (numberList[1].toInt() == 0) return "Error: Dividing by Zero"
                return (numberList[0].toInt() / numberList[1].toInt()).toString()
            }
        }

        return "Error: Unexpected Error"
    }
}