package cist.cmc.nfc.sdk.core.utils;

import android.os.Build;

public class SDKUtils {
    static String getDeviceIp() {
        int serialNumber;
        if (Build.VERSION.SDK_INT >= 29) {
            return Build.SERIAL;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            return Build.SERIAL;
        }
        if (Build.VERSION.SDK_INT <= 25) {
            return Build.SERIAL;
        }
        return "";
    }
}
