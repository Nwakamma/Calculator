package com.emmystudio.calculatorapp.addons

import android.util.Log


data class CalculationHistory(val expression: String, val result: String)
class HandleCalculation {

    private val TAG = "CalcDebug"

    private val _history = mutableListOf<CalculationHistory>()
    val history: List<CalculationHistory> get() = _history
    var currentExpression = ""
    fun addCalculation(expression: String, result: String) {
        _history.add(CalculationHistory(expression, result))
    }
    fun clearHistory() {
        _history.clear()
    }

    fun getAllHistory(): List<CalculationHistory> {
        return _history
    }
    fun getMainCurrentExpression(): String {
        return currentExpression
    }
    fun setMainCurrentExpression(expression: String) {
        currentExpression = expression
    }

    fun solveAddition(first: String, second: String): String {
        val firstNum = first.toDoubleOrNull() ?: 0.0
        val secondNum = second.toDoubleOrNull() ?: 0.0
        val result = (firstNum + secondNum).toString()
        currentExpression = "$first + $second = $result"
        _history.add(CalculationHistory(currentExpression, result))
        return result
    }

    fun solveSubtraction(first: String, second: String): String {
        val result = first.toDouble() - second.toDouble()
        currentExpression = "$first - $second = $result"
        _history.add(CalculationHistory(currentExpression, result.toString()))
        return result.toString()
    }

    fun solveMultiplication(first: String, second: String): String {
        val result = first.toDouble() * second.toDouble()
        currentExpression = "$first * $second = $result"
        _history.add(CalculationHistory(currentExpression, result.toString()))
        return result.toString()
    }
    fun solveDivision(first: String, second: String): String {
        val result = first.toDouble() / second.toDouble()
        currentExpression = "$first / $second = $result"
        _history.add(CalculationHistory(currentExpression, result.toString()))
        return result.toString()
    }
    fun solvePercentage(first: String): String {
        val result = first.toDouble() / 100
        currentExpression = "$first % = $result"
        _history.add(CalculationHistory(currentExpression, result.toString()))
        return result.toString()
    }
}