package cist.cmc.nfc.sdk.core.utils;

import android.util.Log;

import androidx.annotation.Keep;

import java.util.HashMap;
import java.util.Map;

@Keep
public class StringUtils {
    private static final String TAG = StringUtils.class.getName();
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexToByte(String hex) {
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(hex.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    public static String hexToASCII(String hex) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    public static String hexToUTF8(String hex) throws Exception {
        byte[] arr = hexToByte(hex);
        String a = new String(arr, "UTF-8");
        return a;
    }

    public static String cutTLVString(String hex, String tag) {
        String countString = "";
        int index = 0;
        if (tag == null) {
            Log.d(TAG, "Tag is not null!");
            return null;
        }

        index = hex.indexOf(tag) + tag.length();
        countString = hex.substring(index, index + 2);
        return countString;
    }

    public static int indexOfTagWithLength(String hex, String tag) {
        int index = 0;
        if (tag == null) {
            Log.d(TAG, "Tag length not null!");
            return -1;
        }
        index = hex.indexOf(tag) + tag.length() + 2;
        return index;
    }

    public static String convertStringToDate(String text, String separation) {
        char[] c_arr = text.toCharArray();
        StringBuilder dates = new StringBuilder();
        for (int i = 0; i < c_arr.length; i++) {
            if (i == 2 || i == 4) {
                dates.append(separation);//("/") ("-")
            }
            dates.append(c_arr[i]);
        }
        return dates.toString();
    }

    @Keep
    public static Map<String, String> cutString(int sum, String text) {
        switch (sum) {
            case 1:
                return subStringOneElement(text);
            case 2:
                return subStringTowElement(text);
            case 3:
                return subStringThreeElement(text);
            default:
                return null;
        }

    }

    public static final String DATE = "DATE";
    public static final String MONTH = "MONTH";
    public static final String YEAR = "YEAR";
    public static final String FIRST_YEAR = "FIRST_YEAR";
    public static final String LAST_YEAR = "LAST_YEAR";

    private static Map<String, String> subStringOneElement(String text) {
        Map<String, String> stringElement = new HashMap<>();
        if (text == null) return null;
        stringElement.put(FIRST_YEAR, text.substring(0, 2));//19 82
        stringElement.put(LAST_YEAR, text.substring(2));
        return stringElement;
    }

    /**
     * ex 24071992 => 24 07 1992
     * @param text
     * @return
     */
    public static Map<String, String> subStringTowElement(String text) {
        Map<String, String> stringElement = new HashMap<>();
        if (text == null) return null;
        if (text.length() != 8) return null;
        stringElement.put(DATE, text.substring(0, 2));
        stringElement.put(MONTH, text.substring(2, 4));//24 07 1982
        stringElement.put(YEAR, text.substring(4));
        return stringElement;
    }

    /**
     * ex 24071992 => 24 07 19 92
     * @param text
     * @return
     */
    public static Map<String, String> subStringThreeElement(String text) {
        Map<String, String> stringElement = new HashMap<>();
        if (text == null) return null;
        stringElement.put(DATE, text.substring(0, 2));
        stringElement.put(MONTH, text.substring(2, 4));//24 07 19 82
        stringElement.put(FIRST_YEAR, text.substring(4, 6));
        stringElement.put(LAST_YEAR, text.substring(6));
        return stringElement;
    }

    /**
     * The method retrieves a substring starting at position index
     *
     * @param text
     * @param index
     * @return
     */
    @Keep
    public static String subStringWithIndex(String text, int index) {
        if (text == null || text.equals("")) return "";
        else return text.substring(index);
    }
}
