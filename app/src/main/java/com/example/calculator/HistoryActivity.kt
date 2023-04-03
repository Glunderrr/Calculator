package com.example.calculator

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val historyList = intent?.getStringArrayExtra("history_list") ?: arrayOf()
        cards(historyList)
        clickListeners(historyList)
    }

    private fun clickListeners(historyList: Array<String>) = with(binding) {
        oneText.setOnClickListener { returnValues(oneText.text.toString(), historyList) }
        twoText.setOnClickListener { returnValues(twoText.text.toString(), historyList) }
        threeText.setOnClickListener { returnValues(threeText.text.toString(), historyList) }
        fourText.setOnClickListener { returnValues(fourText.text.toString(), historyList) }
        fifeText.setOnClickListener { returnValues(fifeText.text.toString(), historyList) }
        sixText.setOnClickListener { returnValues(sixText.text.toString(), historyList) }
    }

    private fun cards(historyList: Array<String>) = with(binding) {
        val textArray = arrayOf(oneText, twoText, threeText, fourText, fifeText, sixText)
        historyList.forEachIndexed { index, element -> textArray[index].text = element }
    }

    private fun returnValues(value: String, historyList: Array<String>) {
        val intent = Intent()
        intent.putExtra("return_value_key", value)
        setResult(RESULT_OK, intent)
        finish()
    }
}