package com.example.calculatorapp

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.calculatorapp.data.model.Calculation
import com.example.calculatorapp.data.viewmodel.CalculationViewModel
import java.lang.RuntimeException

class MainActivity : AppCompatActivity() {

    private lateinit var mCalculationViewModel: CalculationViewModel
    private lateinit var textViewCalcInput: TextView
    private lateinit var historyButton: ImageButton
    lateinit var rotateButton: ImageButton
    lateinit var backSpaceButton: ImageButton
    lateinit var includeKeyboard: View
    lateinit var includeCalculations: View
    private var actionAdded = false
    private var showingHistory = true

    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName
        private const val MAX_CAPACITY = 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mCalculationViewModel = ViewModelProvider(this)[CalculationViewModel::class.java]

        textViewCalcInput = findViewById(R.id.textViewCalcInput)
        historyButton = findViewById(R.id.buttonPreviousCalcs)
        rotateButton = findViewById(R.id.buttonChangePortrait)
        backSpaceButton = findViewById(R.id.buttonBackspace)
        includeKeyboard = findViewById(R.id.includeKeyboard)
        includeCalculations = findViewById(R.id.includeCalculations)
        includeCalculations.visibility = View.GONE

    // TODO for some reason it keeps loading that,
//         even though it is currently in another activity and
//         onCreate() should be unreachable?

//        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
//        val nightMode = sharedPref.getBoolean(getString(R.string.nightMode), false)
//        if (nightMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun onDestroy() {
        super.onDestroy()

        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        val nightModeFlags = this.resources
            .configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
        var nightMode = false
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> nightMode = true
            Configuration.UI_MODE_NIGHT_NO -> nightMode = false
        }

        with (sharedPref.edit()) {
            putBoolean(getString(R.string.nightMode), nightMode)
            commit()
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
        textViewCalcInput.text = getString(R.string.num0)
    }

    fun actionOnClick(view: View) {
        if (maxCapacityReached()){
            Toast.makeText(this, "Max capacity of $MAX_CAPACITY symbols reached!", Toast.LENGTH_LONG).show()
            return
        }
        try {
            when (view.id) {
                R.id.buttonMinus -> handleSimpleAction(getString(R.string.actionMinus)) // handleSimpleAction(R.string.actionMinus.toString())
                R.id.buttonDivision -> handleSimpleAction(getString(R.string.actionDiv)) // handleSimpleAction(R.string.actionDiv.toString())
                R.id.buttonMultiplication -> handleSimpleAction(getString(R.string.actionMultiplication)) // handleSimpleAction(R.string.actionMultiplication.toString())
                R.id.buttonPlus -> handleSimpleAction(getString(R.string.actionPlus)) // handleSimpleAction(R.string.actionPlus.toString())
                R.id.buttonDot -> handleSimpleAction(getString(R.string.actionDot)) // handleSimpleAction(R.string.actionDot.toString())
                R.id.buttonPercentage -> calculatePercentage()
                R.id.buttonMore -> openMoreOperations()
                R.id.buttonBrackets -> Toast.makeText(
                    this, "Operations with brackets not supported :(", Toast.LENGTH_LONG).show() // putCorrectBracket()
            }
        } catch (e: RuntimeException) {
            Toast.makeText(this, "Invalid input!", Toast.LENGTH_LONG).show()
        }

    }

    // Not implemented for now :(
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
            R.id.buttonNum1 -> getString(R.string.num1)
            R.id.buttonNum2 -> getString(R.string.num2)
            R.id.buttonNum3 -> getString(R.string.num3)
            R.id.buttonNum4 -> getString(R.string.num4)
            R.id.buttonNum5 -> getString(R.string.num5)
            R.id.buttonNum6 -> getString(R.string.num6)
            R.id.buttonNum7 -> getString(R.string.num7)
            R.id.buttonNum8 -> getString(R.string.num8)
            R.id.buttonNum9 -> getString(R.string.num9)
            R.id.buttonSquareRoot -> getString(R.string.subSquareRoot)
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
        try {
//            if (lastBracketWasOpen()) putCorrectBracket()
            val beforeCalculation = textViewCalcInput.text.toString()
            val afterCalculation = EvaluationHandler.evaluate(textViewCalcInput.text.toString())
            textViewCalcInput.text = afterCalculation
            insertDataToDataBase(beforeCalculation, afterCalculation)
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, "Invalid input format!", Toast.LENGTH_LONG).show()
        }
    }

    private fun insertDataToDataBase(beforeCalculation: String, afterCalculation: String) {

        if (fieldsCheck(beforeCalculation, afterCalculation)) {
            val calculation = Calculation(0, beforeCalculation, afterCalculation)
            // Adding data to database
            mCalculationViewModel.addCalculation(calculation)
        }
    }

    private fun fieldsCheck(expression: String, result: String): Boolean {
        return !(TextUtils.isEmpty(expression) || TextUtils.isEmpty(result)) && (expression != result)
    }

    fun clearHistoryOnClick(view: View) {
        mCalculationViewModel.deleteAllCalculations()
        Toast.makeText(this, "History cleared!", Toast.LENGTH_LONG).show()
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
        if (showingHistory) {
            includeKeyboard.visibility = View.VISIBLE
            includeCalculations.visibility = View.GONE
        } else {
            includeKeyboard.visibility = View.GONE
            includeCalculations.visibility = View.VISIBLE
        }
        showingHistory = !showingHistory
    }

    private fun openMoreOperations() {
        Toast.makeText(this, "This must open such operations like sin^-1", Toast.LENGTH_LONG).show()
    }

    private fun calculatePercentage() {
        Toast.makeText(this, "This must calculate % if valid expression", Toast.LENGTH_LONG).show()
    }
}