package com.emmystudio.calculatorapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.google.android.material.button.MaterialButton
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

class ScientificActivity : ComponentActivity() {

    private val TAG = "ScientificDebug"

    // State management variables
    private var expressionString = ""
    private var isAngleModeRad = true // true = RAD, false = DEG

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scientific)

        // Navigation back to Main (Normal) Activity
        val btnSwitchMode = findViewById<ImageView>(R.id.btnSwitchMode)
        btnSwitchMode.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Optional: Closes scientific activity from memory stack
        }

        // UI Displays
        val textResult = findViewById<TextView>(R.id.tvResult)
        val textExpression = findViewById<TextView>(R.id.tvExpression)

        // Scientific & Functions Buttons
        val btnSin = findViewById<MaterialButton>(R.id.btnSin)
        val btnCos = findViewById<MaterialButton>(R.id.btnCos)
        val btnTan = findViewById<MaterialButton>(R.id.btnTan)
        val btnLog = findViewById<MaterialButton>(R.id.btnLog)
        val btnLn = findViewById<MaterialButton>(R.id.btnLn)
        val btnAsin = findViewById<MaterialButton>(R.id.btnAsin)
        val btnAcos = findViewById<MaterialButton>(R.id.btnAcos)
        val btnAtan = findViewById<MaterialButton>(R.id.btnAtan)
        val btnSqrt = findViewById<MaterialButton>(R.id.btnSqrt)
        val btnPow = findViewById<MaterialButton>(R.id.btnPow)
        val btnPi = findViewById<MaterialButton>(R.id.btnPi)
        val btnE = findViewById<MaterialButton>(R.id.btnE)
        val btnFactorial = findViewById<MaterialButton>(R.id.btnFactorial)
        val btnAbs = findViewById<MaterialButton>(R.id.btnAbs)
        val btnInv = findViewById<MaterialButton>(R.id.btnInv)
        val btnMod = findViewById<MaterialButton>(R.id.btnMod)
        val btnExp = findViewById<MaterialButton>(R.id.btnExp)
        val btnRadDeg = findViewById<MaterialButton>(R.id.btnRadDeg)

        // Actions & Operators
        val btnClear = findViewById<MaterialButton>(R.id.btnClear)
        val btnDelete = findViewById<MaterialButton>(R.id.btnDelete)
        val btnPercent = findViewById<MaterialButton>(R.id.btnPercent)
        val btnOpenParen = findViewById<MaterialButton>(R.id.btnOpenParen)
        val btnCloseParen = findViewById<MaterialButton>(R.id.btnCloseParen)
        val btnDivide = findViewById<MaterialButton>(R.id.btnDivide)
        val btnMultiply = findViewById<MaterialButton>(R.id.btnMultiply)
        val btnSubtract = findViewById<MaterialButton>(R.id.btnSubtract)
        val btnAdd = findViewById<MaterialButton>(R.id.btnAdd)
        val btnEquals = findViewById<MaterialButton>(R.id.btnEquals)

        // Numeric Keys
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

        // Map layout simple appending buttons
        val standardAppends = mapOf(
            btn0 to "0", btn1 to "1", btn2 to "2", btn3 to "3",
            btn4 to "4", btn5 to "5", btn6 to "6", btn7 to "7",
            btn8 to "8", btn9 to "9", btnDot to ".", btnAdd to " + ",
            btnSubtract to " - ", btnMultiply to " × ", btnDivide to " ÷ ",
            btnOpenParen to "(", btnCloseParen to ")", btnMod to " mod "
        )

        for ((button, rawString) in standardAppends) {
            button.setOnClickListener {
                expressionString += rawString
                textExpression.text = expressionString
            }
        }

        // Scientific Function appends (Trig, logs require a starting parentheses block)
        val functionalAppends = mapOf(
            btnSin to "sin(", btnCos to "cos(", btnTan to "tan(",
            btnAsin to "asin(", btnAcos to "acos(", btnAtan to "atan(",
            btnLog to "log(", btnLn to "ln(", btnSqrt to "sqrt(",
            btnAbs to "abs("
        )

        for ((button, funcString) in functionalAppends) {
            button.setOnClickListener {
                expressionString += funcString
                textExpression.text = expressionString
            }
        }

        // Special Constants & Values
        btnPi.setOnClickListener {
            expressionString += PI.toString()
            textExpression.text = expressionString
        }

        btnE.setOnClickListener {
            expressionString += E.toString()
            textExpression.text = expressionString
        }

        btnPow.setOnClickListener { expressionString += "^"; textExpression.text = expressionString }
        btnInv.setOnClickListener { expressionString += "^(-1)"; textExpression.text = expressionString }
        btnFactorial.setOnClickListener { expressionString += "!"; textExpression.text = expressionString }
        btnPercent.setOnClickListener { expressionString += "%"; textExpression.text = expressionString }
        btnExp.setOnClickListener { expressionString += "E"; textExpression.text = expressionString }

        // Radian vs Degree Toggle
        btnRadDeg.setOnClickListener {
            isAngleModeRad = !isAngleModeRad
            btnRadDeg.text = if (isAngleModeRad) "RAD" else "DEG"
            Log.d(TAG, "Angle configuration switched. Rad Mode Active = $isAngleModeRad")
        }

        // Clear Everything
        btnClear.setOnClickListener {
            expressionString = ""
            textExpression.text = ""
            textResult.text = "0"
        }

        // Character Backspace
        btnDelete.setOnClickListener {
            if (expressionString.isNotEmpty()) {
                // If ending with customized spaced operators, handle safe slice drops
                expressionString = if (expressionString.endsWith(" ") && expressionString.length >= 3) {
                    expressionString.dropLast(3)
                } else {
                    expressionString.dropLast(1)
                }
                textExpression.text = expressionString
            }
        }

        // Evaluate Scientific String Logic Pipeline
        btnEquals.setOnClickListener {
            if (expressionString.isEmpty()) return@setOnClickListener

            try {
                Log.d(TAG, "Parsing Formula String: $expressionString")
                // 1. Automatically close any dangling parentheses first!
                val balancedExpression = autoCloseParentheses(expressionString)
                Log.d(TAG, "Balanced Formula String: $balancedExpression")
                val evaluationTarget = formatExpressionForParsing(balancedExpression)
                val finalValue = evaluateStringExpression(evaluationTarget)

                val absoluteValue = abs(finalValue)

                textResult.text = when {
                    // Case 1: The result is exactly zero
                    finalValue == 0.0 -> "0"

                    // Case 2: Incredibly tiny decimal (Too far below 0, e.g., 0.00001)
                    absoluteValue < 0.001 -> formatScientific(finalValue)

                    // Case 3: Gigantic number (Too far above, e.g., 10,000,000)
                    absoluteValue >= 10000000.0 -> formatScientific(finalValue)

                    // Case 4: Perfect clean whole number
                    finalValue % 1.0 == 0.0 -> finalValue.toLong().toString()

                    // Case 5: Standard normal decimal (e.g., 12.3456)
                    else -> {
                        // Limits normal decimals to 6 decimal places so they don't break the UI
                        val normalFormat = DecimalFormat("#.######", DecimalFormatSymbols(Locale.US))
                        normalFormat.format(finalValue)
                    }
                }

//                // Format output values cleanly
//                textResult.text = if (finalValue % 1.0 == 0.0) {
//                    finalValue.toLong().toString()
//                } else {
//                    finalValue.toString()
//                }
            } catch (e: Exception) {
                Log.e(TAG, "Evaluation Failure caught", e)
                textResult.text = "Error"
            }
        }
    }

    private fun formatScientific(value: Double): String {
        // Format to standard scientific format (e.g., 1.2345E-5)
        val formatter = DecimalFormat("0.###E0", DecimalFormatSymbols(Locale.UK))
        val parts = formatter.format(value).split("E")

        if (parts.size < 2) return value.toString()

        val base = parts[0]
        val exponent = parts[1].toInt()

        // Map regular numbers to crisp superscript unicode characters for the power engine
        val superscripts = mapOf(
            '-' to '⁻', '0' to '⁰', '1' to '¹', '2' to '²', '3' to '³',
            '4' to '⁴', '5' to '⁵', '6' to '⁶', '7' to '⁷', '8' to '⁸', '9' to '⁹'
        )

        val superscriptExponent = parts[1].map { superscripts[it] ?: it }.joinToString("")

        return "$base × 10$superscriptExponent"
    }

    // Swaps visual human symbols (×, ÷, mod) into pure machine tokens (*, /, %) before compiling
    private fun formatExpressionForParsing(raw: String): String {
        return raw.replace("×", "*")
            .replace("÷", "/")
            .replace("mod", "%")
    }

    private fun autoCloseParentheses(raw: String): String {
        var openCount = 0
        var closeCount = 0

        // Count how many open and close parentheses exist in the string
        for (ch in raw) {
            if (ch == '(') openCount++
            if (ch == ')') closeCount++
        }

        // If there are more open brackets than closing ones, append the missing ones
        if (openCount > closeCount) {
            val missingBrackets = ")".repeat(openCount - closeCount)
            return raw + missingBrackets
        }

        return raw
    }

    // Native custom string token dynamic execution engine parser (recursive descent parser)
    private fun evaluateStringExpression(str: String): Double {
        return object {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < str.length) str[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected token: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x += parseTerm() // addition
                    else if (eat('-'.code)) x -= parseTerm() // subtraction
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x *= parseFactor() // multiplication
                    else if (eat('/'.code)) x /= parseFactor() // division
                    else if (eat('%'.code)) x %= parseFactor() // modulus
                    else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return +parseFactor() // unary plus
                if (eat('-'.code)) return -parseFactor() // unary minus

                var x: Double
                val startPos = pos
                if (eat('('.code)) { // parentheses
                    x = parseExpression()
                    if (!eat(')'.code)) throw RuntimeException("Missing closing parenthesis")
                } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) { // numbers
                    while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                    x = str.substring(startPos, pos).toDouble()
                } else if (ch >= 'a'.code && ch <= 'z'.code) { // functions
                    while (ch >= 'a'.code && ch <= 'z'.code) nextChar()
                    val func = str.substring(startPos, pos)
                    if (eat('('.code)) {
                        x = parseExpression()
                        if (!eat(')'.code)) throw RuntimeException("Missing closing parenthesis after function '$func'")

                        x = when (func) {
                            "sin" -> if (isAngleModeRad) sin(x) else sin(Math.toRadians(x))
                            "cos" -> if (isAngleModeRad) cos(x) else cos(Math.toRadians(x))
                            "tan" -> if (isAngleModeRad) tan(x) else tan(Math.toRadians(x))
                            "asin" -> if (isAngleModeRad) asin(x) else Math.toDegrees(asin(x))
                            "acos" -> if (isAngleModeRad) acos(x) else Math.toDegrees(acos(x))
                            "atan" -> if (isAngleModeRad) atan(x) else Math.toDegrees(atan(x))
                            "log" -> log10(x)
                            "ln" -> ln(x)
                            "sqrt" -> sqrt(x)
                            "abs" -> abs(x)
                            else -> throw RuntimeException("Unknown function parameter context: $func")
                        }
                    } else {
                        throw RuntimeException("Missing dynamic parentheses following algebraic function tag")
                    }
                } else {
                    throw RuntimeException("Unexpected parsing argument token standard: " + ch.toChar())
                }

                // Handle post-fix operators like exponents and factorials
                if (eat('^'.code)) x = x.pow(parseFactor()) // exponentiation
                if (eat('!'.code)) x = runFactorial(x)      // factorial logic
                if (eat('%'.code)) x /= 100.0                // trailing percentage logic

                return x
            }
        }.parse()
    }

    // Helper mathematical computation method for factorials (n!)
    private fun runFactorial(n: Double): Double {
        if (n < 0.0) return Double.NaN
        val intPart = n.toLong()
        var result = 1.0
        for (i in 1..intPart) {
            result *= i
        }
        return result
    }
}