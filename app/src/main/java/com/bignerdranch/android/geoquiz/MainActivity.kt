package com.bignerdranch.android.geoquiz

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.view.*

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: Button
    private lateinit var prevButton: Button
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView
    private lateinit var cheatsLeftText: TextView
    private var correct = 0
    private var cheatsLeft = -1
    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }
    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        if (userAnswer == correctAnswer)
            correct++
        val messageResId = when {
            quizViewModel.howAnswered[quizViewModel.currentIndex] == -1 -> R.string.judgement_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
            .show()
    }
    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)
        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex
        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        cheatButton = findViewById(R.id.cheat_button)
        questionTextView = findViewById(R.id.question)
        cheatsLeftText = findViewById(R.id.cheats_left_text)
        var answered = false
        correct = 0
        if (cheatsLeft == -1) { cheatsLeft = 3}
        cheatsLeftText.setText("Cheats remaining: $cheatsLeft")
        trueButton.setOnClickListener { view: View ->
            // Do something in response to the click here
            if (quizViewModel.howAnswered[quizViewModel.currentIndex] <= 0)
                checkAnswer(true)
            if (quizViewModel.howAnswered[quizViewModel.currentIndex] == 0)
                quizViewModel.howAnswered[quizViewModel.currentIndex] = 1
            answered = true
        }
        falseButton.setOnClickListener { view: View ->
            // Do something in response to the click here
            if (quizViewModel.howAnswered[quizViewModel.currentIndex] <= 0)
                checkAnswer(false)
            if (quizViewModel.howAnswered[quizViewModel.currentIndex] == 0)
                quizViewModel.howAnswered[quizViewModel.currentIndex] = 1
            answered = true
        }
        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            if (quizViewModel.currentIndex == 0){
                var c = (correct.toDouble() / quizViewModel.bankSize * 100).toInt()
                Toast.makeText(this, "$c %", Toast.LENGTH_SHORT)
                    .show()
                correct = 0
                for (n in 0..5)
                    quizViewModel.howAnswered[n] = 0
            }
            updateQuestion()
            answered = false
        }
        prevButton.setOnClickListener {
            if (quizViewModel.currentIndex == 0)
                quizViewModel.currentIndex = quizViewModel.bankSize-1
            else
                quizViewModel.moveToPrev()
            updateQuestion()
            answered = false
        }
        cheatButton.setOnClickListener{view ->
            if (cheatsLeft > 0) {
                val answerIsTrue = quizViewModel.currentQuestionAnswer
                val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val options = ActivityOptions
                        .makeClipRevealAnimation(view, 0, 0, view.width, view.height)
                    startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
                } else {
                    startActivityForResult(intent, REQUEST_CODE_CHEAT)
                }
            }
        }
        questionTextView.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
            answered = false
        }
        updateQuestion()
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK)
            return
        if (requestCode == REQUEST_CODE_CHEAT){
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN,false) ?: false
            quizViewModel.howAnswered[quizViewModel.currentIndex] = -1
            cheatsLeft--
            cheatsLeftText.setText("Cheats remaining: $cheatsLeft")
        }
    }
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }
    //saves data when app is closed (destroyed)
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }
}
