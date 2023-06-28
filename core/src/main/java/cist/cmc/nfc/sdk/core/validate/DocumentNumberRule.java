package cist.cmc.nfc.sdk.core.validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class DocumentNumberRule {
    private static final String REGEX = "[A-Z0-9<]{12}$";

    public static boolean isValid(String documentNumber) {
        Pattern patternDate = Pattern.compile(REGEX);
        Matcher matcherDate = patternDate.matcher(documentNumber);
        return matcherDate.find();
    }
}
