package cist.cmc.nfc.sdk.core.mrz;


import cist.cmc.nfc.sdk.core.utils.MRZUtils;

public class MRZFieldRecognitionDefectsFixer {
    public static String fixDocumentType(String input) {
        return MRZUtils.ReplaceSimilarDigitsWithLetters(input);
    }

    public static String fixDocumentNumber(String input) {
        return MRZUtils.ReplaceSimilarLettersWithDigits(input);
    }

    public static String fixCheckDigit(String input) {
        return MRZUtils.ReplaceSimilarLettersWithDigits(input);
    }

    public static String fixDate(String input) {
        return MRZUtils.ReplaceSimilarLettersWithDigits(input);
    }

    public static String fixSex(String input) {
        return input.replace("P", "F");
    }

    public static String fixCountryCode(String input) {
        return MRZUtils.ReplaceSimilarDigitsWithLetters(input);
    }

    public static String fixNames(String input) {
        return MRZUtils.ReplaceSimilarDigitsWithLetters(input);
    }

    public static String fixNationality(String input) {
        return MRZUtils.ReplaceSimilarDigitsWithLetters(input);
    }
}
