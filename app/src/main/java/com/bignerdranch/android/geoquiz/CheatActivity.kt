package com.bignerdranch.android.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import java.security.AccessControlContext

const val EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.answer_shown"
private const val EXTRA_ANSWER_IS_TRUE =
    "com.bignerdranch.android.geoquiz.answer_is_true"
private const val KEY_CHEAT = "cheat"
private const val TAG = "CheatActivity"
class CheatActivity : AppCompatActivity() {
    private lateinit var apiNum : TextView
    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button
    private var answerIsTrue = false
    var answerShown = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)
        val ansShown = savedInstanceState?.getBoolean(KEY_CHEAT, false) ?: false
        answerShown = ansShown
        if (answerShown)
            setAnswerShownResult(true)
        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)
        apiNum = findViewById(R.id.apinum)
        var a = Build.VERSION.SDK_INT
        apiNum.setText("API Level $a")
        showAnswerButton.setOnClickListener{
            val answerText = when{
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }
            answerShown = true
            answerTextView.setText(answerText)
            setAnswerShownResult(true)
        }
    }
    private fun setAnswerShownResult(isAnswerShown:Boolean){
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }
    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext,CheatActivity::class.java).apply{
                putExtra(EXTRA_ANSWER_IS_TRUE,answerIsTrue)
            }
        }
    }
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(com.bignerdranch.android.geoquiz.TAG, "onSaveInstanceState")
        savedInstanceState.putBoolean(KEY_CHEAT, answerShown)
    }
}
