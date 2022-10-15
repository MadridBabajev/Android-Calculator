package com.example.calculatorapp

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // TODO History button (clear history)
    // TODO Rotation button
    // TODO Evaluate and Save Calculations
    // TODO Save calcs using SQLite
    private lateinit var textViewCalcInput: TextView
    lateinit var historyButton: ImageButton
    lateinit var rotateButton: ImageButton
    lateinit var backSpaceButton: ImageButton
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    private var actionAdded = false

    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName
        private const val MAX_CAPACITY = 20;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textViewCalcInput = findViewById(R.id.textViewCalcInput)
        historyButton = findViewById(R.id.buttonPreviousCalcs)
        rotateButton = findViewById(R.id.buttonChangePortrait)
        backSpaceButton = findViewById(R.id.buttonBackspace)

        sharedPreferences = getSharedPreferences("nightNodeMain", MODE_PRIVATE)
        editor = sharedPreferences.edit()

    // TODO for some reason it keeps loading that,
//         even though it is currently in another activity and
//         onCreate() should be unreachable?

//        val nightMode = sharedPreferences.getBoolean("nightMode", false)
//        if (nightMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun onDestroy() {
        super.onDestroy()
        val nightModeFlags = this.resources
            .configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
        var nightMode = false
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> {
                nightMode = true
            }

            Configuration.UI_MODE_NIGHT_NO -> {
                nightMode = false
            }
        }
        editor.apply {
            putBoolean("nightMode", nightMode)
            apply()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val savedCalcInput = textViewCalcInput.text

        outState.putString("savedCalcInput", savedCalcInput.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textViewCalcInput.text = savedInstanceState.getString("savedCalcInput", "0")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.calculator_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miSettings -> redirectToSettings()
        }
        return true
    }

    private fun redirectToSettings() {
        Log.d(TAG, "Went to settings")
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun clearInputOnClick(view: View) {
        textViewCalcInput.text = "0"
    }

    fun actionOnClick(view: View) {
        if (maxCapacityReached()){
            Toast.makeText(this, "Max capacity of $MAX_CAPACITY symbols reached!", Toast.LENGTH_LONG).show()
            return
        }
        when (view.id) {
            R.id.buttonMinus -> handleSimpleAction("-") // handleSimpleAction(R.string.actionMinus.toString())
            R.id.buttonDivision -> handleSimpleAction("÷") // handleSimpleAction(R.string.actionDiv.toString())
            R.id.buttonMultiplication -> handleSimpleAction("×") // handleSimpleAction(R.string.actionMultiplication.toString())
            R.id.buttonPlus -> handleSimpleAction("+") // handleSimpleAction(R.string.actionPlus.toString())
            R.id.buttonDot -> handleSimpleAction(".") // handleSimpleAction(R.string.actionDot.toString())
            R.id.buttonPercentage -> calculatePercentage()
            R.id.buttonMore -> openMoreOperations()
            R.id.buttonBrackets -> putCorrectBracket()
        }
    }

    private fun putCorrectBracket() {
        val output: String = if (lastBracketWasOpen()) textViewCalcInput.text.toString() + ")"
        else textViewCalcInput.text.toString() + "("

        textViewCalcInput.text = output
    }

    private fun lastBracketWasOpen(): Boolean {
        var opened = false
        for (chr: Char in textViewCalcInput.text.toString()) {
            if (chr != '(' && chr != ')') continue
            opened = chr == '('
        }
        return opened
    }

    private fun handleSimpleAction(sign: String) {
        if (maxCapacityReached()){
            Toast.makeText(this, "Max capacity of $MAX_CAPACITY symbols reached!", Toast.LENGTH_LONG).show()
            return
        }
        if (sign == ".") {
            handleDot()
            return
        }
        if (textViewCalcInput.text.toString()[textViewCalcInput.text.toString().length - 1]
                .toString() != sign) {
            val output: String = if (actionAdded) {
                textViewCalcInput.text.toString()
                    .substring(0, textViewCalcInput.text.toString().length - 1) + sign
            } else {
                textViewCalcInput.text.toString()
                    .substring(0, textViewCalcInput.text.toString().length) + sign
            }
            actionAdded = true
            textViewCalcInput.text = output
            return
        }
        Toast.makeText(this, "Invalid Format", Toast.LENGTH_LONG).show()
    }

    private fun handleDot() {
        var dotInNum = false
        val symbols = setOf('+', '-', '÷', '×')
        for (chr: Char in textViewCalcInput.text.toString()) {
            if (symbols.contains(chr)) {
                dotInNum = false
                continue
            }
            if (chr != '.') continue
            else dotInNum = true
        }
        if (!dotInNum) {
            var output = textViewCalcInput.text.toString()
            output += if (symbols.contains(textViewCalcInput.text.toString()[textViewCalcInput.text.toString().length - 1])) {
                "0."
            } else "."
            textViewCalcInput.text = output
        }
    }

    fun putNumberOrSubOnClick(view: View) {
        if (maxCapacityReached()){
            Toast.makeText(this, "Max capacity of 25 symbols reached!", Toast.LENGTH_LONG).show()
            return
        }
        if (view.id == R.id.buttonNum0) {
            handleZeroes()
            return
        }
        val outputSymbol: String = when (view.id) {
            R.id.buttonNum1 -> "1"// R.string.num1.toString()
            R.id.buttonNum2 -> "2"// R.string.num2.toString()
            R.id.buttonNum3 -> "3"// R.string.num3.toString()
            R.id.buttonNum4 -> "4"// R.string.num4.toString()
            R.id.buttonNum5 -> "5"// R.string.num5.toString()
            R.id.buttonNum6 -> "6"// R.string.num6.toString()
            R.id.buttonNum7 -> "7"// R.string.num7.toString()
            R.id.buttonNum8 -> "8"// R.string.num8.toString()
            R.id.buttonNum9 -> "9"// R.string.num9.toString()
            R.id.buttonSquareRoot -> "√("// R.string.subSquareRoot.toString() + "("
            R.id.buttonRad, R.id.buttonSin, R.id.buttonCos, R.id.buttonTan, R.id.buttonUpsideDown,
            R.id.buttonLog, R.id.buttonLn, R.id.buttonToThePowerOf, R.id.buttonRoot,
            R.id.buttonExponentToThePowerOf, R.id.buttonExponent, R.id.buttonPi, R.id.buttonAbsValue
            -> {
                Toast.makeText(this, "Operation not implemented yet", Toast.LENGTH_LONG).show()
                return
            }
            else -> ""
        }
        if (textViewCalcInput.text.toString() == "0") {
            textViewCalcInput.text = outputSymbol
        } else{
            val output = textViewCalcInput.text.toString() + outputSymbol
            textViewCalcInput.text = output
        }
        actionAdded = false
    }

    private fun maxCapacityReached(): Boolean {
        return textViewCalcInput.text.toString().length == MAX_CAPACITY
    }

    private fun handleZeroes() {
        if (textViewCalcInput.text.toString() != "0") {
            val output = textViewCalcInput.text.toString() + "0"
            textViewCalcInput.text = output
            return
        }
        return
    }

    fun backspacePressed(view: View) {
        val output = textViewCalcInput.text.toString().substring(0, textViewCalcInput.text.toString().length - 1)
        textViewCalcInput.text = output
    }

    fun evaluateOnClick(view: View) {
        Toast.makeText(this, "Not implemented yet", Toast.LENGTH_LONG).show()
    }

    fun rotationPressed(view: View) {
        val orientation = this.resources.configuration.orientation
        requestedOrientation = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    fun historyPressed(view: View) {

    }

    private fun openMoreOperations() {
        Toast.makeText(this, "This must open such operations like sin^-1", Toast.LENGTH_LONG).show()
    }

    private fun calculatePercentage() {
        Toast.makeText(this, "This must calculate % if valid expression", Toast.LENGTH_LONG).show()
    }
}