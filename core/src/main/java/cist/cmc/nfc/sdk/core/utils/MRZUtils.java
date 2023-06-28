package cist.cmc.nfc.sdk.core.utils;

public class MRZUtils {
    public static String ReplaceSimilarDigitsWithLetters(String text) {
        return text.replace("0", "O")
                .replace("1", "I")
                .replace("2", "Z")
                .replace("8", "B");
    }

    public static String ReplaceSimilarLettersWithDigits(String text) {
        return text.replace('O', '0')
                .replace('Q', '0')
                .replace('U', '0')
                .replace('D', '0')
                .replace('I', '1')
                .replace('Z', '2')
                .replace('B', '8')
                .replace("S", "5");
    }

    public static String ReplaceAngleBracketsWithSpaces(String text) {
        return text.replace("<", " ");
    }
}
