package cist.cmc.nfc.sdk.core.input;

import androidx.annotation.Keep;

import java.util.Map;

import cist.cmc.nfc.sdk.core.mrz.MrzInfo;
import cist.cmc.nfc.sdk.core.utils.StringUtils;
import cist.cmc.nfc.sdk.core.validate.DateRule;
import cist.cmc.nfc.sdk.core.validate.DocumentNumberRule;

@Keep
public class PasswordInput {
    public static final String dateUnlimited = MrzInfo.expiryDateUnlimited;
    private MrzInfo mrzInfo;
    private String canNumberPass = "";

    /**
     * Method get can number input pass.
     *
     * @param documentNumber
     * @return
     */
    public void canNumberInput(String documentNumber) {
        if (DocumentNumberRule.isValid(documentNumber) == true) {
            this.canNumberPass = documentNumber.substring(6);
        }
    }

    /**
     * Method get MrzInfo access chip
     *
     * @param documentNumber
     * @param dateOfBirth
     * @param expiryDate
     * @param separator
     */
    public void setPassKeyToExpiryDate(String documentNumber, String dateOfBirth, String expiryDate, String separator) {
        String documentNumberPass = documentInput(documentNumber);
        String dateOfBirthPass = passInput(dateOfBirth, separator);
        String expiryDatePass = passInput(expiryDate, separator);
        this.mrzInfo = new MrzInfo(documentNumberPass, dateOfBirthPass, expiryDatePass);
    }

    /**
     * Method get MrzInfo access chip
     *
     * @param documentNumber
     * @param dateOfBirth
     * @param dateOfIssue
     * @param separator
     */
    public void setPassKeyToDateOfIssue(String documentNumber, String dateOfBirth, String dateOfIssue, String separator) {
        String documentNumberPass = documentInput(documentNumber);
        String dateOfBirthPass = passInput(dateOfBirth, separator);
        String expiryDate = getExpressDate(dateOfBirth, dateOfIssue, separator);
        String expiryDatePass = passInput(expiryDate, separator);
        this.mrzInfo = new MrzInfo(documentNumberPass, dateOfBirthPass, expiryDatePass);
    }

    /**
     * Method get document number password
     *
     * @param documentNumber
     * @return
     */
    public String documentInput(String documentNumber) {
        if (DocumentNumberRule.isValid(documentNumber) == true) {
            return StringUtils.subStringWithIndex(documentNumber, 3);
        } else {
            return null;
        }

    }

    /**
     * Method get dateOfBirthPass or expiryDatePass
     *
     * @param passKey
     * @param separation
     * @return
     */
    public String passInput(String passKey, String separation) {
        String pass = passKey.replace(separation, "");
        Map<String, String> mapKey = StringUtils.subStringThreeElement(pass);
        StringBuilder passInput = new StringBuilder();
        passInput.append(mapKey.get(StringUtils.LAST_YEAR));
        passInput.append(mapKey.get(StringUtils.MONTH));
        passInput.append(mapKey.get(StringUtils.DATE));
        if (DateRule.isValid(passInput.toString()) == true) {
            return passInput.toString();
        } else {
            return null;
        }

    }

    private static final int FIXE_AGE_25 = 25;
    private static final int FIXE_AGE_40 = 40;
    private static final int FIXE_AGE_60 = 60;

    public String getExpressDate(String dateOfBirth, String dateOfIssue, String separation) {
        String dateOfBirthText = dateOfBirth.replace(separation, "");
        String dateOfIssueText = dateOfIssue.replace(separation, "");
        Map<String, String> dateOfBirthMap = StringUtils.cutString(2, dateOfBirthText);
        Map<String, String> dateExpressMap = StringUtils.cutString(2, dateOfIssueText);

        int age = Integer.parseInt(dateExpressMap.get(StringUtils.YEAR)) - Integer.parseInt(dateOfBirthMap.get(StringUtils.YEAR));
        StringBuilder expiryDate = new StringBuilder();
        if (age < (FIXE_AGE_25 - 2)) {
            int exYear = Integer.parseInt(dateExpressMap.get(StringUtils.YEAR)) + (FIXE_AGE_25 - age);
            expiryDate.append(dateOfBirthMap.get(StringUtils.DATE));
            expiryDate.append(separation);
            expiryDate.append(dateOfBirthMap.get(StringUtils.MONTH));
            expiryDate.append(separation);
            expiryDate.append(exYear);
            return expiryDate.toString();
        } else if (age >= (FIXE_AGE_25 - 2) && age < (FIXE_AGE_40 - 2)) {
            int exYear = Integer.parseInt(dateExpressMap.get(StringUtils.YEAR)) + (FIXE_AGE_40 - age);
            expiryDate.append(dateOfBirthMap.get(StringUtils.DATE));
            expiryDate.append(separation);
            expiryDate.append(dateOfBirthMap.get(StringUtils.MONTH));
            expiryDate.append(separation);
            expiryDate.append(exYear);
            return expiryDate.toString();
        } else if (age >= (FIXE_AGE_40 - 2) && age < (FIXE_AGE_60 - 2)) {
            int exYear = Integer.parseInt(dateExpressMap.get(StringUtils.YEAR)) + (FIXE_AGE_60 - age);
            expiryDate.append(dateOfBirthMap.get(StringUtils.DATE));
            expiryDate.append(separation);
            expiryDate.append(dateOfBirthMap.get(StringUtils.MONTH));
            expiryDate.append(separation);
            expiryDate.append(exYear);
            return expiryDate.toString();
        } else {
            expiryDate.append("31");
            expiryDate.append(separation);
            expiryDate.append("12");
            expiryDate.append(separation);
            expiryDate.append("2099");
            return expiryDate.toString();
        }
    }


    public String getCanNumberPass() {
        return canNumberPass;
    }

    public MrzInfo getMrzInfo() {
        return mrzInfo;
    }
}
