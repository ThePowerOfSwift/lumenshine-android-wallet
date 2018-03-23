package com.soneso.stellargate.persistence.secureprefs

/*
 * Copyright 2013 The Android Open Source Project
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

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.math.BigInteger
import java.security.*
import java.security.spec.RSAKeyGenParameterSpec
import java.security.spec.RSAKeyGenParameterSpec.F4
import java.util.*
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.security.auth.x500.X500Principal

class KeyStoreHelper {

    companion object {

        const val TAG = "KeyStoreHelper"

        private val cipher: Cipher
            @Throws(NoSuchPaddingException::class, NoSuchAlgorithmException::class)
            get() = Cipher.getInstance(
                    String.format("%s/%s/%s",
                            TYPE_RSA,
                            BLOCKING_MODE,
                            PADDING_TYPE))

        /**
         * Creates a public and private key and stores it using the Android Key
         * Store, so that only this application will be able to access the keys.
         */
        @Throws(NoSuchProviderException::class, NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class)
        fun createKeys(context: Context, alias: String) {
            if (!isSigningKey(alias)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    createKeysM(alias, false)
                } else {
                    createKeysJBMR2(context, alias)
                }
            }
        }


        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Throws(NoSuchProviderException::class, NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class)
        internal fun createKeysJBMR2(context: Context, alias: String) {

            val start = GregorianCalendar()
            val end = GregorianCalendar()
            end.add(Calendar.YEAR, 30)

            val spec = KeyPairGeneratorSpec.Builder(context)
                    // You'll use the alias later to retrieve the key. It's a key
                    // for the key!
                    .setAlias(alias)
                    .setSubject(X500Principal("CN=$alias"))
                    .setSerialNumber(BigInteger.valueOf(Math.abs(alias.hashCode()).toLong()))
                    // Date range of validity for the generated pair.
                    .setStartDate(start.time).setEndDate(end.time)
                    .build()

            val kpGenerator = KeyPairGenerator.getInstance(
                    TYPE_RSA,
                    KEYSTORE_PROVIDER_ANDROID_KEYSTORE)
            kpGenerator.initialize(spec)
            val kp = kpGenerator.generateKeyPair()
            Log.d(TAG, "Public Key is: " + kp.public.toString())

        }

        @TargetApi(Build.VERSION_CODES.M)
        private fun createKeysM(alias: String, requireAuth: Boolean) {
            try {
                val keyPairGenerator = KeyPairGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_RSA, KEYSTORE_PROVIDER_ANDROID_KEYSTORE)
                keyPairGenerator.initialize(
                        KeyGenParameterSpec.Builder(
                                alias,
                                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                                .setAlgorithmParameterSpec(RSAKeyGenParameterSpec(1024, F4))
                                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                                .setDigests(KeyProperties.DIGEST_SHA256,
                                        KeyProperties.DIGEST_SHA384,
                                        KeyProperties.DIGEST_SHA512)
                                // Only permit the private key to be used if the user authenticated
                                // within the last five minutes.
                                .setUserAuthenticationRequired(requireAuth)
                                .build())
                val keyPair = keyPairGenerator.generateKeyPair()
                Log.d(TAG, "Public Key is: " + keyPair.public.toString())

            } catch (e: NoSuchProviderException) {
                throw RuntimeException(e)
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e)
            } catch (e: InvalidAlgorithmParameterException) {
                throw RuntimeException(e)
            }

        }

        /**
         * JBMR2+ If Key with the default alias exists, returns true, else false.
         * on pre-JBMR2 returns true always.
         */
        private fun isSigningKey(alias: String): Boolean {
            try {
                val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE)
                keyStore.load(null)
                return keyStore.containsAlias(alias)
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                return false
            }
        }

        /**
         * Returns the private key signature on JBMR2+ or else null.
         */
        fun getSigningKey(alias: String): String? {
            val cert = getPrivateKeyEntry(alias)!!.certificate ?: return null
            return Base64.encodeToString(cert.encoded, Base64.NO_WRAP)
        }

        private fun getPrivateKeyEntry(alias: String): KeyStore.PrivateKeyEntry? {
            try {
                val ks = KeyStore
                        .getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE)
                ks.load(null)
                val entry = ks.getEntry(alias, null)

                if (entry == null) {
                    Log.w(TAG, "No key found under alias: $alias")
                    Log.w(TAG, "Exiting signData()...")
                    return null
                }

                if (entry !is KeyStore.PrivateKeyEntry) {
                    Log.w(TAG, "Not an instance of a PrivateKeyEntry")
                    Log.w(TAG, "Exiting signData()...")
                    return null
                }
                return entry
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                return null
            }

        }

        fun encrypt(alias: String, plaintext: String): String {
            try {
                val publicKey = getPrivateKeyEntry(alias)!!.certificate.publicKey
                val cipher = cipher
                cipher.init(Cipher.ENCRYPT_MODE, publicKey)
                return Base64.encodeToString(cipher.doFinal(plaintext.toByteArray()), Base64.NO_WRAP)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        }

        fun decrypt(alias: String, ciphertext: String): String {
            try {
                val privateKey = getPrivateKeyEntry(alias)!!.privateKey
                val cipher = cipher
                cipher.init(Cipher.DECRYPT_MODE, privateKey)
                return String(cipher.doFinal(Base64.decode(ciphertext, Base64.NO_WRAP)))
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        }

        val KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore"
        val TYPE_RSA = "RSA"
        val PADDING_TYPE = "PKCS1Padding"
        val BLOCKING_MODE = "NONE"

        val SIGNATURE_SHA256withRSA = "SHA256withRSA"
        val SIGNATURE_SHA512withRSA = "SHA512withRSA"
    }

}