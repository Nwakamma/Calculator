package com.emmystudio.calculatorapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.emmystudio.calculatorapp.addons.HandleCalculation
import com.emmystudio.calculatorapp.addons.types.MainOperations
import com.google.android.material.button.MaterialButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Switch to Scientific Activity
        val scientificBtn = findViewById<ImageView>(R.id.btnSwitchMode)
        scientificBtn.setOnClickListener {
            val intent = Intent(this, ScientificActivity::class.java)
            startActivity(intent)
        }

        val solve = HandleCalculation()

        var operations: MainOperations? = null
        var firstNumber = ""
        var secondNumber = ""

        // UI Element Declarations
        val textResult = findViewById<TextView>(R.id.tvResult)
        val textExpression = findViewById<TextView>(R.id.tvExpression)
        val btnClear = findViewById<MaterialButton>(R.id.btnClear)
        val btnDelete = findViewById<MaterialButton>(R.id.btnDelete)
        val btnPercent = findViewById<MaterialButton>(R.id.btnPercent)
        val btnDivide = findViewById<MaterialButton>(R.id.btnDivide)
        val btnMultiply = findViewById<MaterialButton>(R.id.btnMultiply)
        val btnSubtract = findViewById<MaterialButton>(R.id.btnSubtract)
        val btnAdd = findViewById<MaterialButton>(R.id.btnAdd)
        val btnEquals = findViewById<MaterialButton>(R.id.btnEquals)

        val btnDot = findViewById<MaterialButton>(R.id.btnDot)
        val btn0 = findViewById<MaterialButton>(R.id.btn0)
        val btn1 = findViewById<MaterialButton>(R.id.btn1)
        val btn2 = findViewById<MaterialButton>(R.id.btn2)
        val btn3 = findViewById<MaterialButton>(R.id.btn3)
        val btn4 = findViewById<MaterialButton>(R.id.btn4)
        val btn5 = findViewById<MaterialButton>(R.id.btn5)
        val btn6 = findViewById<MaterialButton>(R.id.btn6)
        val btn7 = findViewById<MaterialButton>(R.id.btn7)
        val btn8 = findViewById<MaterialButton>(R.id.btn8)
        val btn9 = findViewById<MaterialButton>(R.id.btn9)

        // Helper function to handle math evaluation
        fun executePendingOperation(second: String, operation: MainOperations): String {
            val first = secondNumber.ifEmpty { "0" }

            return when (operation) {
                MainOperations.ADD -> solve.solveAddition(first, second)
                MainOperations.MULTIPLY -> solve.solveMultiplication(first, second)
                MainOperations.SUBTRACT -> solve.solveSubtraction(first, second)
                MainOperations.DIVIDE -> solve.solveDivision(first, second)
            }
        }

        // Shared logic when an operator button (+, -, *, /) is clicked
        fun handleOperatorClick(selectedOp: MainOperations, symbol: String) {
            if (firstNumber.isNotEmpty()) {
                if (secondNumber.isEmpty()) {
                    secondNumber = firstNumber
                } else {
                    // Chain calculations if there's already a pending operation
                    val pendingOp = operations ?: selectedOp
                    secondNumber = executePendingOperation(firstNumber, pendingOp)
                }
                firstNumber = ""
            } else if (secondNumber.isEmpty()) {
                // Default to zero if they click an operator before typing a number
                secondNumber = "0"
            }

            operations = selectedOp
            textExpression.text = "$secondNumber $symbol"
            textResult.text = "0"
        }

        // Number Input Click Listeners
        btn0.setOnClickListener {
            if (firstNumber.isNotEmpty() && firstNumber != "0") {
                firstNumber += "0"
                textResult.text = firstNumber
            }
        }

        val numberButtons = mapOf(
            btn1 to "1", btn2 to "2", btn3 to "3",
            btn4 to "4", btn5 to "5", btn6 to "6",
            btn7 to "7", btn8 to "8", btn9 to "9"
        )

        for ((button, value) in numberButtons) {
            button.setOnClickListener {
                if (firstNumber == "0") firstNumber = "" // Clear leading zero
                firstNumber += value
                textResult.text = firstNumber
            }
        }

        btnDot.setOnClickListener {
            if (firstNumber.isEmpty()) {
                firstNumber = "0."
            } else if (!firstNumber.contains(".")) {
                firstNumber += "."
            }
            textResult.text = firstNumber
        }

        // Action Buttons Click Listeners
        btnDelete.setOnClickListener {
            if (firstNumber.isEmpty()) return@setOnClickListener

            if (firstNumber.length == 1) {
                firstNumber = ""
                textResult.text = "0"
            } else {
                firstNumber = firstNumber.dropLast(1)
                textResult.text = firstNumber
            }
        }

        btnClear.setOnClickListener {
            firstNumber = ""
            secondNumber = ""
            operations = null
            textResult.text = "0"
            textExpression.text = ""
        }

        // Operator Assignments
        btnAdd.setOnClickListener { handleOperatorClick(MainOperations.ADD, "+") }
        btnSubtract.setOnClickListener { handleOperatorClick(MainOperations.SUBTRACT, "-") }
        btnMultiply.setOnClickListener { handleOperatorClick(MainOperations.MULTIPLY, "×") }
        btnDivide.setOnClickListener { handleOperatorClick(MainOperations.DIVIDE, "÷") }

        // Evaluation Button
        btnEquals.setOnClickListener {
            val currentOp = operations

            // Allow evaluating either the current typed number or using the stored total
            val inputToEvaluate = firstNumber.ifEmpty { secondNumber.ifEmpty { "0" } }

            if (currentOp != null) {
                val finalResult = executePendingOperation(inputToEvaluate, currentOp)
                textExpression.text = ""
                textResult.text = finalResult

                // Set up state for next operations
                secondNumber = finalResult
                firstNumber = ""
                operations = null // Reset the operation selector so it doesn't loop stickily!
            }
        }

        btnPercent.setOnClickListener {
            val inputToEvaluate = firstNumber.ifEmpty { secondNumber.ifEmpty { "0" } }
            val result = solve.solvePercentage(inputToEvaluate)
            textResult.text = result
            textExpression.text = "$firstNumber % = $result"
            firstNumber = result
            operations = null
        }
    }
}