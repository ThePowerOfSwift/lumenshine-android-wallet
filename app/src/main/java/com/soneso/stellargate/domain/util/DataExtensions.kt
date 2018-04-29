package com.soneso.stellargate.domain.util

import java.nio.CharBuffer
import java.nio.charset.Charset
import java.util.*

fun CharSequence.toCharArray(): CharArray {
    val array = CharArray(length)
    asSequence().forEachIndexed { index, c ->
        array[index] = c
    }
    return array
}

fun CharArray.toByteArray(): ByteArray {
    val charBuffer = CharBuffer.wrap(this)
    val byteBuffer = Charset.forName("UTF-8").encode(charBuffer)
    return Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit())
}