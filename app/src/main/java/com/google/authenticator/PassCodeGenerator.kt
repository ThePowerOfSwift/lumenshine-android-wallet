/*
 * Copyright 2009 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.authenticator

import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.security.GeneralSecurityException
import kotlin.experimental.and

/**
 * An implementation of the HOTP generator specified by RFC 4226. Generates
 * short passcodes that may be used in challenge-response protocols or as
 * timeout passcodes that are only valid for a short period.
 *
 *
 * The default passcode is a 6-digit decimal code. The maximum passcode length is 9 digits.
 *
 * @author sweis@google.com (Steve Weis)
 */
class PassCodeGenerator(private val codeLength: Int, private val signer: ((ByteArray) -> ByteArray)) {

    init {
        if (codeLength < 0 || codeLength > MAX_PASSCODE_LENGTH) {
            throw IllegalArgumentException("PassCodeLength must be between 1 and $MAX_PASSCODE_LENGTH digits.")
        }
    }

    private fun padOutput(value: Int): String {
        val result = StringBuilder(Integer.toString(value))
        for (i in result.length until codeLength) {
            result.insert(0, "0")
        }
        return result.toString()
    }

    /**
     * @param state 8-byte integer value representing internal OTP state.
     * @return A decimal response code
     * @throws GeneralSecurityException If a JCE exception occur
     */
    @Throws(GeneralSecurityException::class)
    fun generateResponseCode(state: Long): String {
        val value = ByteBuffer.allocate(8).putLong(state).array()
        return generateResponseCode(value)
    }


    /**
     * @param state     8-byte integer value representing internal OTP state.
     * @param challenge Optional challenge as array of bytes.
     * @return A decimal response code
     * @throws GeneralSecurityException If a JCE exception occur
     */
    @Throws(GeneralSecurityException::class)
    fun generateResponseCode(state: Long, challenge: ByteArray?): String {
        return if (challenge == null) {
            generateResponseCode(state)
        } else {
            // Allocate space for combination and store.
            val value = ByteBuffer.allocate(8 + challenge.size)
                    .putLong(state)  // Write out OTP state
                    .put(challenge, 0, challenge.size) // Concatenate with challenge.
                    .array()
            generateResponseCode(value)
        }
    }

    /**
     * @param challenge An arbitrary byte array used as a challenge
     * @return A decimal response code
     * @throws GeneralSecurityException If a JCE exception occur
     */
    @Throws(GeneralSecurityException::class)
    private fun generateResponseCode(challenge: ByteArray): String {
        val hash = signer.invoke(challenge)

        // Dynamically truncate the hash
        // OffsetBits are the low order bits of the last byte of the hash
        val offset = hash[hash.size - 1] and 0xF.toByte()
        // Grab a positive integer value starting at the given offset.
        val truncatedHash = hashToInt(hash, offset.toInt()) and 0x7FFFFFFF
        val pinValue = truncatedHash % DIGITS_POWER[codeLength]
        return padOutput(pinValue)
    }

    /**
     * Grabs a positive integer value from the input array starting at
     * the given offset.
     *
     * @param bytes the array of bytes
     * @param start the index into the array to start grabbing bytes
     * @return the integer constructed from the four bytes in the array
     */
    private fun hashToInt(bytes: ByteArray, start: Int): Int {
        val input = DataInputStream(
                ByteArrayInputStream(bytes, start, bytes.size - start))
        val `val`: Int
        try {
            `val` = input.readInt()
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }

        return `val`
    }

    companion object {

        private const val MAX_PASSCODE_LENGTH = 9

        /**
         * Powers of 10 used to shorten the pin to the desired number of digits
         */
        private val DIGITS_POWER = intArrayOf(1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000)// 0 1  2   3    4     5      6       7        8         9
    }

}
