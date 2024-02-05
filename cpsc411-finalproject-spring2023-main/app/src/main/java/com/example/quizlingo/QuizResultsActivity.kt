package com.example.quizlingo

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

private const val TAG = "QuizResultsActivity"
const val CORRECT1 = "correct1"
const val CORRECT2 = "correct2"
const val CORRECT3 = "correct3"
const val CORRECT4 = "correct4"

class QuizResultsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var arrayList: ArrayList<Questions>
    private lateinit var word: Array<String>
    private lateinit var correctOrIncorrect: Array<String>

    private lateinit var scoreTextView: TextView
    private lateinit var resetButton: Button

    private val mainActivityViewModel: MainActivityViewModel by lazy {
        AppPreferencesRepository.initialize(this)
        ViewModelProvider(this)[MainActivityViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val celebrationSoundPlayer = MediaPlayer.create(this, R.raw.celebration_sound)
        val lossSoundPlayer = MediaPlayer.create(this, R.raw.loss_sound)

        scoreTextView = findViewById(R.id.score)
        resetButton = findViewById(R.id.resetButton)

        mainActivityViewModel.loadCorrect1()
        mainActivityViewModel.loadCorrect2()
        mainActivityViewModel.loadCorrect3()
        mainActivityViewModel.loadCorrect4()

        val score =
            mainActivityViewModel.getCorrect1() + mainActivityViewModel.getCorrect2() + mainActivityViewModel.getCorrect3() + mainActivityViewModel.getCorrect4()

        scoreTextView.text =
            getString(R.string.scoremsg1) + " " + score + " " + getString(R.string.scoremsg2)
        Log.d(TAG, "This is the total score: $score")

        if (score >= 3) {
            celebrationSoundPlayer.start()
            celebrationSoundPlayer.setOnCompletionListener {
                celebrationSoundPlayer.release()
            }
        } else {
            lossSoundPlayer.start()
            lossSoundPlayer.setOnCompletionListener {
                lossSoundPlayer.release()
            }
        }

        resetButton.setOnClickListener {
            mainActivityViewModel.setCorrect1(0)
            mainActivityViewModel.setCorrect2(0)
            mainActivityViewModel.setCorrect3(0)
            mainActivityViewModel.setCorrect4(0)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            MainActivity.setCurrentActivityIndex(this, 0)
            Log.d(TAG, "Going back to MainActivity.")
        }

        val wordBank = WordBank().wordBank

        // Display words in the RecyclerView list
        word = arrayOf(
            wordBank[0].replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
            wordBank[1].replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
            wordBank[2].replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
            wordBank[3].replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        )

        // Get question score (0 or 1)
        val correct1 = mainActivityViewModel.getCorrect1()
        val correct2 = mainActivityViewModel.getCorrect2()
        val correct3 = mainActivityViewModel.getCorrect3()
        val correct4 = mainActivityViewModel.getCorrect4()

        // Display "Correct" or "Incorrect" in the RecyclerView list
        correctOrIncorrect = arrayOf(
            printAnswer(correct1),
            printAnswer(correct2),
            printAnswer(correct3),
            printAnswer(correct4)
        )

        val imageView = findViewById<ImageView>(R.id.imgScoreView)
        if (score >= 3) {
            imageView.setImageResource(R.drawable.great_job)
        } else {
            imageView.setImageResource(R.drawable.bad_job)
        }

        recyclerView = findViewById(R.id.quizScoreDisplay)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        arrayList = arrayListOf()
        getUserData()
    }

    private fun getUserData() {
        for (i in word.indices) {
            val questions = Questions(correctOrIncorrect[i], word[i])
            arrayList.add(questions)
        }
        recyclerView.adapter = MyAdapter(arrayList)
    }

    private fun printAnswer(questionScore: Int): String {
        return if (questionScore == 0) {
            getString(R.string.incorrect)
        } else {
            getString(R.string.correct)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Store and retrieve, key-value pair
        outState.putInt(CORRECT1, mainActivityViewModel.getCorrect1())
        outState.putInt(CORRECT2, mainActivityViewModel.getCorrect2())
        outState.putInt(CORRECT3, mainActivityViewModel.getCorrect3())
        outState.putInt(CORRECT4, mainActivityViewModel.getCorrect4())
    }
}