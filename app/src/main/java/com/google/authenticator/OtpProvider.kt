/*
 * Copyright 2010 Google Inc. All Rights Reserved.
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

import android.content.Context
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Class containing implementation of HOTP/TOTP.
 * Generates OTP codes for one or more accounts.
 *
 * @author Steve Weis (sweis@google.com)
 * @author Cem Paya (cemp@google.com)
 */
class OtpProvider(context: Context) {
    /**
     * Counter for time-based OTPs (TOTP).
     */
    private val totpCounter: TotpCounter = TotpCounter(DEFAULT_INTERVAL.toLong())
    private val totpClock = TotpClock(context)

    fun getCurrentTotpCode(secret: String, challenge: ByteArray?): String {

        // For time-based OTP, the state is derived from clock.
        val otpState = totpCounter.getValueAtTime(totpClock.currentTimeMillis() / 1000)
        return computePin(secret, otpState, challenge)
    }

    fun getCurrentHotpCode(secret: String, counter: Int, challenge: ByteArray?): String {

        // For counter-based OTP, the state is obtained by incrementing stored counter.
        val otpState = counter.toLong()
        return computePin(secret, otpState, challenge)
    }

    /**
     * Computes the one-time PIN given the secret key.
     *
     * @param secret    the secret key
     * @param otp_state current token state (counter or time-interval)
     * @param challenge optional challenge bytes to include when computing passcode.
     * @return the PIN
     */
    private fun computePin(secret: String, otp_state: Long, challenge: ByteArray?): String {
        if (secret.isEmpty()) {
            throw RuntimeException("Empty secret")
        }

        val codeLength = if (challenge == null) PIN_LENGTH else REFLECTIVE_PIN_LENGTH
        val pcg = PassCodeGenerator(codeLength, {
            val mac = Mac.getInstance("HMACSHA1")
            mac.init(SecretKeySpec(secret.toByteArray(), ""))
            mac.doFinal(it)
        })

        return pcg.generateResponseCode(otp_state, challenge)
    }

    companion object {

        const val TAG = "OtpProvider"
        /**
         * Default passcode timeout period (in seconds)
         */
        private const val DEFAULT_INTERVAL = 30
        private const val PIN_LENGTH = 6 // HOTP or TOTP
        private const val REFLECTIVE_PIN_LENGTH = 9 // ROTP
    }
}
