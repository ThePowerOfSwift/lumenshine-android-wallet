package com.soneso.stellargate.domain.util

fun CharSequence.toCharArray(): CharArray {
    val array = CharArray(length)
    asSequence().forEachIndexed { index, c ->
        array[index] = c
    }
    return array
}