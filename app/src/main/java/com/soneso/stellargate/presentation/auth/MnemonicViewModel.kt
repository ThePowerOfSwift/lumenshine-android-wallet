package com.soneso.stellargate.presentation.auth

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import java.util.*

class MnemonicViewModel : ViewModel() {

    private var questionToAskCount = 0
    private val mnemonic = ArrayList<String>()
    var liveCurrentQuestion = MutableLiveData<Int>()
    var currentWordIndex = 0
        private set

    fun init(mnemonic: String, questionCount: Int) {
        questionToAskCount = questionCount
        this.mnemonic.addAll(mnemonic.split(" "))
    }

    fun isMnemonicCurrentlyPresented() = liveCurrentQuestion.value == null

    fun randomizeQuestion() {
        currentWordIndex = Random().nextInt(mnemonic.size)

        val current = liveCurrentQuestion.value ?: -1
        liveCurrentQuestion.value = current + 1
    }

    fun isAnswerCorrect(answer: CharSequence) = /*answer == mnemonic[currentWordIndex]*/ true
}