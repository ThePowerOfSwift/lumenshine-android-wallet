package com.soneso.lumenshine.presentation.util

import com.google.authenticator.Base32
import java.nio.charset.Charset

fun String.decodeBase32(): String {
//    val decoded = BaseEncoding.base32().decode(this)
    val decoded = Base32.decode(this)
    return String(decoded, Charset.forName("UTF-8"))
}