package com.soneso.lumenshine.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

fun Context.convertLabels() {

    try {
        val reader = BufferedReader(InputStreamReader(assets.open("Localizable.strings")))


        val fileBuilder = StringBuilder()
        var line = reader.readLine()

        while (line != null) {
            line = line.removeSuffix("\";")

            if (line.isNotBlank()) {
                line = if (line.startsWith("/*")) {
                    line.replace("/*", "<!--").replace("*/", "-->")
                } else {
                    val pair = line.split("=")
                    "<string name=\"${pair[0].trim().toLowerCase().replace(".", "_")}\">${pair[1].trim().substring(1)}</string>"
                }
            }

            Log.d("BlaBla", line)
            fileBuilder.append(line).append("\n")
            line = reader.readLine()
        }

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Strings", fileBuilder.toString())
        clipboard.setPrimaryClip(clip)
    } catch (e: Exception) {
        Log.e("FAILURE", e.javaClass.simpleName, e)
    }
}

fun Context.getAssetFileContent(fileName: String): String {

    return try {
        val reader = BufferedReader(InputStreamReader(assets.open(fileName)))
        var line = reader.readLine()

        val fileBuilder = StringBuilder()
        while (line != null) {
            fileBuilder.append(line).append("\n")
            line = reader.readLine()
        }
        reader.close()
        fileBuilder.toString()

    } catch (e: Exception) {
        Log.e("FAILURE", e.javaClass.simpleName, e)
        ""
    }
}