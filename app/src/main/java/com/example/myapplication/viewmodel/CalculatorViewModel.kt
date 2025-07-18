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

    //TODO: Catch Errors and Edge-Cases (Number Overflows, Validation)

    private fun evaluate(expression: String): String {
        val tokens = tokenize(expression)
        val postfix = toPostfix(tokens)
        return evaluatePostfix(postfix)
    }

    private fun tokenize(expression: String): MutableList<String> {
        val tokens = emptyList<String>().toMutableList()
        val chars = expression.toList()
        var i = 0

        while (i < chars.size) {
            when (chars[i]) {
                ' ' -> {
                    i += 1
                }

                in '0'..'9', '.' -> {
                    val start = i
                    while (i < chars.size && (chars[i].isDigit() || chars[i] == '.')) {
                        i += 1
                    }
                    tokens.add(chars.subList(start, i).joinToString(""))
                }

                '+', '*', '/', '(', ')' -> {
                    tokens.add(chars[i].toString())
                    i += 1
                }

                '-' -> {
                    val isUnary =
                        tokens.isEmpty() || tokens.lastOrNull() in listOf("(", "+", "-", "*", "/")

                    if (isUnary) {
                        i += 1
                        if (i < chars.size && (chars[i].isDigit() || chars[i] == '.')) {
                            val start = i
                            while (i < chars.size && (chars[i].isDigit() || chars[i] == '.')) {
                                i += 1
                            }
                            val number = chars.subList(start, i).joinToString("")
                            tokens.add("-$number")
                        } else {
                            throw Error("Invalid unary minus")
                        }
                    } else {
                        tokens.add("-")
                        i += 1
                    }
                }

                else -> {
                    throw Error("Unexpected character: ${chars[i]}")
                }
            }
        }

        return tokens
    }

    private fun toPostfix(tokens: MutableList<String>): MutableList<String> {
        val output = emptyList<String>().toMutableList()
        val operators = emptyList<String>().toMutableList()
        val precedence = mapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2)

        for (token in tokens) {
            if (token.toDoubleOrNull() != null) {
                output.add(token)
            } else if (token in "+-*/") {
                while (operators.lastOrNull() != null) {
                    val top = operators.last()
                    if (top == "(") break

                    val precedence1 = precedence[token] ?: 0
                    val precedence2 = precedence[top] ?: 0

                    if (precedence1 <= precedence2) {
                        output.add(operators.removeAt(operators.size - 1))
                    } else {
                        break
                    }
                }
                operators.add(token)
            } else if (token == "(") {
                operators.add(token)
            } else if (token == ")") {
                while (operators.isNotEmpty()) {
                    val top = operators.removeAt(operators.size - 1)
                    if (top == "(") {
                        break
                    } else {
                        output.add(top)
                    }
                }
            } else {
                throw Error("Unknown token: $token")
            }
        }

        while (operators.isNotEmpty()) {
            val operator = operators.removeAt(operators.size - 1)
            if (operator == "(") {
                throw Error("Mismatched parentheses")
            }
            output.add(operator)
        }

        return output
    }

    private fun evaluatePostfix(postfix: MutableList<String>): String {
        val stack = emptyList<Double>().toMutableList()

        for (token in postfix) {
            val number = token.toDoubleOrNull()
            if (number != null) {
                stack.add(number)
            } else if (token in "+-*/") {
                if (stack.size < 2) {
                    throw Error("Not enough operands")
                }
                val b = stack.removeAt(stack.size - 1)
                val a = stack.removeAt(stack.size - 1)
                val result = when (token) {
                    "+" -> a + b
                    "-" -> a - b
                    "*" -> a * b
                    "/" -> {
                        if (b == 0.0) throw Error("Division by zero")
                        a / b
                    }

                    else -> throw Error("Invalid operator: $token")
                }
                stack.add(result)
            } else {
                throw Error("Unknown token: $token")
            }
        }

        if (stack.size != 1) throw Error("Invalid expression")

        return stack[0].toString()
    }
}