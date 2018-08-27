package com.soneso.lumenshine.presentation.auth

import java.util.*

class MnemonicQuizHelper(mnemonic: String) {

    private val words = mnemonic.split(" ")
    private var availablePositions = mutableListOf<Int>()
    private var currentPosition = -1
    private var correctAnswerCount = 0

    init {
        reset()
    }

    fun generatePositionToAsk(): Int {
        currentPosition = availablePositions.shuffled()[0]
        return currentPosition
    }

    fun getRandomWords(): ArrayList<String> {
        val randomWords = ArrayList<String>()
        for (i in 0..3) {
            val pos = availablePositions.shuffled()[0]
            availablePositions.remove(pos)
            randomWords.add(words[pos])
        }
        return randomWords
    }

    fun checkCurrentWord(word: String): Boolean {
        if (currentPosition == -1) {
            currentPosition = 0
            return true
        }
        val isOk = words[currentPosition] == word
        if (isOk) {
            availablePositions.remove(currentPosition)
            correctAnswerCount++
        }
        return isOk
    }

    fun checkWord(word: String, position: Int): Boolean {
        if (position < words.size) {
            val isOk = words[position - 1] == word
            if (isOk) {
                correctAnswerCount++
            }
            return isOk
        }
        return false
    }

    fun isCompleted() = correctAnswerCount >= ANSWER_TARGET

    fun reset() {
        currentPosition = -1
        correctAnswerCount = 0
        availablePositions.clear()
        availablePositions.addAll(words.mapIndexed { index, _ ->
            index
        })
    }

    companion object {
        const val ANSWER_TARGET = 3
    }
}