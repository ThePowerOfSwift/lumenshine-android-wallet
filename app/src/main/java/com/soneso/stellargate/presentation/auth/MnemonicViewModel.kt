package com.soneso.stellargate.presentation.auth

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import java.util.*

class MnemonicViewModel : ViewModel() {

    private val mnemonic = ArrayList<String>()
    val liveCurrentQuestion = MutableLiveData<Int>()
    var currentWordIndex = 0
        private set

    fun init(mnemonic: String) {
        this.mnemonic.addAll(mnemonic.split(" "))
    }

    fun isMnemonicCurrentlyPresented() = liveCurrentQuestion.value == null

    fun randomizeQuestion() {
        currentWordIndex = Random().nextInt(mnemonic.size)

        val current = liveCurrentQuestion.value ?: -1
        liveCurrentQuestion.value = current + 1
    }

    fun resetQuiz() {
        liveCurrentQuestion.value = null
    }

    fun isAnswerCorrect(answer: CharSequence) = answer.toString() == mnemonic[currentWordIndex]
}