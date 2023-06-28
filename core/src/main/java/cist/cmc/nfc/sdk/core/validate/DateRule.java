package cist.cmc.nfc.sdk.core.validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateRule {
    private static final String REGEX = "[0-9]{6}$";

    public static boolean isValid(String date) {
        Pattern patternDate = Pattern.compile(REGEX);
        Matcher matcherDate = patternDate.matcher(date);
        return matcherDate.find();
    }
}
