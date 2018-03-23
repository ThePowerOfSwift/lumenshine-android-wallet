package com.soneso.stellargate.persistence.secureprefs;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

/**
 * Created by cristi.paval on 3/22/18.
 */

public class Example {

    private static final String KEYSTORE_PASSWORD_NAME = "";


    public static void example(Context context) {
        try {
            //This will only create a certificate once as it checks
            //internally whether a certificate with the given name
            //already exists.
            KeyStoreHelper.Companion.createKeys(context, KEYSTORE_PASSWORD_NAME);
        } catch (Exception e) {
            //Probably will never happen.
            throw new RuntimeException(e);
        }
        String pass = KeyStoreHelper.Companion.getSigningKey(KEYSTORE_PASSWORD_NAME);
        if (pass == null) {
            //This is a device less than JBMR2 or something went wrong.
            //I recommend eitehr not supporting it or fetching device hardware ID as shown below.
            //do note this is barely better than obfuscation.
            //Compromised security but may prove to be better than nothing
            pass = getDeviceSerialNumber(context);
            //bitshift everything by some pre-determined amount for added seurity
            pass = bitshiftEntireString(pass);
        }
//        SharedPreferences securePref = new SecurePreferences(context, pass, "monkey");
    }


    /**
     * Bitshift the entire string to obfuscate it further
     * and make it harder to guess the password.
     */
    public static String bitshiftEntireString(String str) {
        StringBuilder msg = new StringBuilder(str);
        int userKey = 6;
        for (int i = 0; i < msg.length(); i++) {
            msg.setCharAt(i, (char) (msg.charAt(i) + userKey));
        }
        return msg.toString();
    }

    /**
     * Gets the hardware serial number of this device.
     *
     * @return serial number or Settings.Secure.ANDROID_ID if not available.
     * Credit: SecurePreferences for Android
     */
    private static String getDeviceSerialNumber(Context context) {
        // We're using the Reflection API because Build.SERIAL is only available
        // since API Level 9 (Gingerbread, Android 2.3).
        try {
            String deviceSerial = (String) Build.class.getField("SERIAL").get(
                    null);
            if (TextUtils.isEmpty(deviceSerial)) {
                return Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            } else {
                return deviceSerial;
            }
        } catch (Exception ignored) {
            // Fall back  to Android_ID
            return Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
    }
}
