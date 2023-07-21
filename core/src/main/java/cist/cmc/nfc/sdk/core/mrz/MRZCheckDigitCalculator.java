package cist.cmc.nfc.sdk.core.mrz;

import android.util.Log;

public class MRZCheckDigitCalculator {
    private static String TAG = MRZCheckDigitCalculator.class.getSimpleName();
    private static final int[] MRZ_WEIGHTS = new int[]{7, 3, 1};
    public static final char FILLER = '<';

    public static int GetCheckDigit(String input) {
        int result = 0;
        for (int i = 0; i < input.length(); i++) {
            try {
                result += getCharacterValue(input.charAt(i)) * MRZ_WEIGHTS[i % MRZ_WEIGHTS.length];
            } catch (RuntimeException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return result % 10;
    }

    private static int getCharacterValue(char c) {
        if (c == FILLER) {
            return 0;
        }
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'A' && c <= 'Z') {
            return c - 'A' + 10;
        }
        throw new RuntimeException("Invalid character in MRZ record: " + c);
    }
}
