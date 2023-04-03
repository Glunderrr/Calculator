package com.example.calculator

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.databinding.ActivityMainBinding
import com.ezylang.evalex.Expression
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    private val numberBuilderString = StringBuilder()
    private val launcher = registerForActivityResult(HistoryResultClass())
    { returnValue ->
        if (returnValue != "") {
            numberBuilderString.clear().append(returnValue)
            binding.resultTextView.text = numberBuilderString
        }
    }
    private lateinit var historyList: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = getSharedPreferences("history_list_file", MODE_PRIVATE)
        historyList = getList()
        setButtonsClickListener()
    }

    private fun setButtonsClickListener() = with(binding) {
        val appendSymbols = arrayOf(
            zeroButton, oneButton, twoButton, threeButton, fourButton,
            fiveButton, sixButton, sevenButton, eightButton, nineButton,
            plusButton, minusButton, multiplyButton, divideButton, pointButton
        )
        val chars =
            arrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '*', '/', '.')
        appendSymbols.forEachIndexed { index, button -> button.setOnClickListener { check(chars[index]) } }

        clearButton.setOnClickListener { resultTextView.text = numberBuilderString.clear() }
        resetButton.setOnClickListener {
            if (resultTextView.text.isNotEmpty())
                resultTextView.text =
                    numberBuilderString.deleteCharAt(numberBuilderString.lastIndex)
        }
        historyButton.setOnClickListener { launcher.launch(historyList.toTypedArray()) }
        equalsButton.setOnClickListener { calculate() }
    }

    private fun check(char: Char) {
        if (numberBuilderString.lastIndex > 46) Toast.makeText(
            this@MainActivity,
            "Exception: too many characters ",
            Toast.LENGTH_LONG
        ).show()
        else binding.resultTextView.text = numberBuilderString.append(char)
    }

    private fun calculate() {
        try {
            val expression = Expression(numberBuilderString.toString())
            var resultExpression = expression.evaluate().numberValue.toPlainString()
            if (historyList.size < 6) historyList.add(0, numberBuilderString.toString())
            else {
                historyList.add(0, numberBuilderString.toString())
                historyList.remove(historyList.last())
            }
            saveList(historyList)
            for (element in resultExpression) {
                if (element == '.') {
                    resultExpression =
                        ((resultExpression.toDouble() * 1000000.0).roundToInt() / 1000000.0).toString()
                    break
                }
            }
            numberBuilderString.clear().append(resultExpression)
            binding.resultTextView.text = numberBuilderString
        } catch (t: Throwable) {
            Toast.makeText(this@MainActivity, "Exception:Incorrect expression", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun saveList(historyList: MutableList<String>) {
        val editor = sharedPref.edit()
        editor.putStringSet("history_key", historyList.toSet())
        editor.apply()
    }

    private fun getList(): MutableList<String> {
        val historyList = sharedPref.getStringSet("history_key", emptySet())
        return historyList?.toMutableList() ?: mutableListOf()
    }
}

class HistoryResultClass : ActivityResultContract<Array<String>, String>() {
    override fun createIntent(context: Context, historyList: Array<String>): Intent {
        val intent = Intent(context, HistoryActivity::class.java)
        intent.putExtra("history_list", historyList)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String {
        val value = intent?.getStringExtra("return_value_key")
        return value ?: ""
    }
}