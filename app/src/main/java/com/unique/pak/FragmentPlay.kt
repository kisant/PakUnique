package com.unique.pak

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import java.util.*


class FragmentPlay : Fragment(R.layout.fragment_play), View.OnClickListener {
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, exception ->
        println("CoroutineExceptionHandler got $exception in $coroutineContext")
    }

    private var scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Default + exceptionHandler
    )

    private lateinit var coins: TextView
    private lateinit var score: TextView
    private lateinit var timer: TextView
    private var cCoins = 0
    private var cScore = 0
    private var cTimer = 30
    private var uniqueButtonId = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViews(parent = view)
        scope.launch {
            setTimer()
        }
        scope.launch {
            callRandom()
        }
    }

    private fun findViews(parent: View) {
        coins = parent.findViewById(R.id.tv_coins)
        coins.text = cCoins.toString()
        score = parent.findViewById(R.id.tv_score)
        score.text = cScore.toString()
        timer = parent.findViewById(R.id.tv_time_left)
        for (i in 0..5) {
            for (j in 0..5) {
                buttons[i][j] = parent.findViewById(idButtons[i][j])
            }
        }
        val a = 1
    }

    private suspend fun setTimer() {
        while (cTimer != -1) {
            showResults(cTimer.toString(), timer, null)
            delay(1000)
            cTimer--
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.persistent_container, FragmentWin.create())
            .commit()
    }

    private suspend fun callRandom() {
        val rand = Random()
        val randomIndexX = rand.nextInt(6)
        val randomIndexY = rand.nextInt(6)
        var uniqueNumber: Int
        var randomNumber: Int

        do {
            randomNumber = rand.nextInt(10)
            uniqueNumber = rand.nextInt(10)
        } while (randomNumber == uniqueNumber)

        for (i in 0..5) {
            for (j in 0..5) {
                if (i == randomIndexX && j == randomIndexY) {
                    showResults(uniqueNumber.toString(), null, buttons[i][j])
                    uniqueButtonId = idButtons[i][j]
                } else {
                    showResults(randomNumber.toString(), null, buttons[i][j])
                }
                buttons[i][j]?.setOnClickListener(this as View.OnClickListener)
            }
        }
    }

    private suspend fun showResults(
        text: String,
        resultView: TextView?,
        resultButtonView: Button?
    ) {
        withContext(Dispatchers.Main) {
            resultView?.text = text
            resultButtonView?.text = text
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == uniqueButtonId) {
            cScore += 5
        }
        score.text = cScore.toString()
        scope.launch {
            callRandom()
        }
    }

    override fun onDestroyView() {
        scope.cancel()
        Log.d("Play screen", "onDestroyView")
        super.onDestroyView()
    }

    companion object {
        fun create() = FragmentPlay()

        private val idButtons = arrayOf(
            intArrayOf(R.id.b11, R.id.b12, R.id.b13, R.id.b14, R.id.b15, R.id.b16),
            intArrayOf(R.id.b21, R.id.b22, R.id.b23, R.id.b24, R.id.b25, R.id.b26),
            intArrayOf(R.id.b31, R.id.b32, R.id.b33, R.id.b34, R.id.b35, R.id.b36),
            intArrayOf(R.id.b41, R.id.b42, R.id.b43, R.id.b44, R.id.b45, R.id.b46),
            intArrayOf(R.id.b51, R.id.b52, R.id.b53, R.id.b54, R.id.b55, R.id.b56),
            intArrayOf(R.id.b61, R.id.b62, R.id.b63, R.id.b64, R.id.b65, R.id.b66)
        )

        private val buttons = Array(7) {
            arrayOfNulls<Button>(
                8
            )
        }
    }
}
