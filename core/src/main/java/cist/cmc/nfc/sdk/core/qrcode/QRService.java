package cist.cmc.nfc.sdk.core.qrcode;

import android.os.Build;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.time.Period;

import cist.cmc.nfc.sdk.core.mrz.MrzInfo;
import cist.cmc.nfc.sdk.core.utils.StringUtils;

/**
 * QRService is the class that handles the error
 * received from scanning the qrcode. The returned result
 * is the identity owner's information, the nfc chip access pass is stored in MrzInfo.
 */
@Keep
public class QRService implements Serializable {
    private static final String TAG = QRService.class.getName();
    private String text;
    private QRPersonal personal;
    private MrzInfo mrzInfo;

    public static final String CCCD_NUMBER = "cccd_number";
    public static final String NAME = "name";
    public static final String DATE_OF_BIRTH = "date_Of_Birth";
    public static final String SEX = "sex";
    public static final String PERMAN_ADDRESS = "permanent_ddress";
    public static final String DATE_OF_ISSUE = "date_of_issue";
    public static final String CMND_NUMBER = "cmnd_number";
    Map<String, String> qrMap;

    @Keep
    public QRService(String text) {
        this.text = text;
        handText();
    }

    private void handText() {
        String regex = "\\|";
        if (text != null) {
            String[] arr = text.split(regex);
            qrMap = new HashMap<>();
            qrMap.put(CCCD_NUMBER, arr[0]);
            qrMap.put(NAME, arr[2]);
            qrMap.put(DATE_OF_BIRTH, arr[3]);
            qrMap.put(SEX, arr[4]);
            qrMap.put(PERMAN_ADDRESS, arr[5]);
            qrMap.put(CMND_NUMBER, arr[1]);
            qrMap.put(DATE_OF_ISSUE, arr[6]);
        } else {
            Log.d(TAG, "Text is not null");
        }

    }

    /**
     * Method to get information of QRPersonal object, MrzInfo object from QR code.
     *
     * @param separation separator character in date format
     */
    @Keep
    public void readObject(String separation) {
        Map<String, String> allExDate = getExpressDate(qrMap.get(DATE_OF_BIRTH), qrMap.get(DATE_OF_ISSUE), separation);
        personal = new QRPersonal(qrMap.get(CCCD_NUMBER), qrMap.get(NAME), StringUtils.convertStringToDate(qrMap.get(DATE_OF_BIRTH), separation)
                , qrMap.get(SEX), qrMap.get(PERMAN_ADDRESS), StringUtils.convertStringToDate(qrMap.get(DATE_OF_ISSUE), separation),
                allExDate.get(EXPRESS_DATE_KEY), qrMap.get(CMND_NUMBER));

        String documentNumber = qrMap.get(CCCD_NUMBER).substring(3);
        String dateOfBirth = getDateOfBirthPass(qrMap.get(DATE_OF_BIRTH));
        String exDate = allExDate.get(EXPRESS_DATE_PASS_KEY);
        this.mrzInfo = new MrzInfo(documentNumber, dateOfBirth, exDate);
    }

    /**
     * return date of birth pass in mrz info
     *
     * @param dateOfBirth
     * @return
     */
    @Keep
    public String getDateOfBirthPass(String dateOfBirth) {
        //text 24071982 convert format 820724
        //flag=0 convert dateOfBirth field of MrzInfo
        StringBuilder s = new StringBuilder();
        Map<String, String> birMap = StringUtils.cutString(3, dateOfBirth);
        s.append(birMap.get(StringUtils.LAST_YEAR));
        s.append(birMap.get(StringUtils.MONTH));
        s.append(birMap.get(StringUtils.DATE));
        return s.toString();
    }

    private static final int FIXE_AGE_25 = 25;
    private static final int FIXE_AGE_40 = 40;
    private static final int FIXE_AGE_60 = 60;

    private static final String EXPRESS_DATE_KEY = "express_date";
    private static final String EXPRESS_DATE_PASS_KEY = "express_date_pass";

    /**
     * This method will return a map containing the data of the exprity date formatted in time with separation,
     * and the exprity date pass is the third data logged into the nfc chip.
     * get the exprity_date data (expiry date), through the EXPRESS_DATE_KEY key.
     * get express_date_pass data via key EXPRESS_DATE_PASS_KEY
     *
     * @param dateOfBirth
     * @param dateRange
     * @param separation
     * @return
     */
    @Keep
    public Map<String, String> getExpressDate(String dateOfBirth, String dateRange, String separation) {
        Map<String, String> allExpressDate;
        Map<String, String> dateOfBirthMap = StringUtils.cutString(2, dateOfBirth);
        Map<String, String> dateExpressMap = StringUtils.cutString(2, dateRange);

        int age = Integer.parseInt(dateExpressMap.get(StringUtils.YEAR)) - Integer.parseInt(dateOfBirthMap.get(StringUtils.YEAR));

        if (age < (FIXE_AGE_25 - 2)) {
            int exYear = Integer.parseInt(dateExpressMap.get(StringUtils.YEAR)) + (FIXE_AGE_25 - age);
            allExpressDate = setExpressDate(exYear, dateOfBirthMap, separation);
            return allExpressDate;
        } else if (age >= (FIXE_AGE_25 - 2) && age < (FIXE_AGE_40 - 2)) {
            int exYear = Integer.parseInt(dateExpressMap.get(StringUtils.YEAR)) + (FIXE_AGE_40 - age);
            allExpressDate = setExpressDate(exYear, dateOfBirthMap, separation);
            return allExpressDate;
        } else if (age >= (FIXE_AGE_40 - 2) && age < (FIXE_AGE_60 - 2)) {
            int exYear = Integer.parseInt(dateExpressMap.get(StringUtils.YEAR)) + (FIXE_AGE_60 - age);
            allExpressDate = setExpressDate(exYear, dateOfBirthMap, separation);
            return allExpressDate;
        } else {
            allExpressDate = new HashMap<>();
            allExpressDate.put(EXPRESS_DATE_KEY, "31" + separation + "12" + separation + "2099");
            allExpressDate.put(EXPRESS_DATE_PASS_KEY, "991231");
            return allExpressDate;
        }
    }

    private Map<String, String> setExpressDate(int exYear, Map<String, String> dateOfBirthMap, String separation) {

        Map<String, String> allExpressDate = new HashMap<>();
        StringBuilder sPass = new StringBuilder();
        StringBuilder sExpress = new StringBuilder();
        //Get pass Mrz expressDate (360126)
        Map<String, String> yearExpressMap = StringUtils.cutString(1, String.valueOf(exYear));
        sPass.append(yearExpressMap.get(StringUtils.LAST_YEAR));
        sPass.append(dateOfBirthMap.get(StringUtils.MONTH));
        sPass.append(dateOfBirthMap.get(StringUtils.DATE));

        //Get Express date (26/01/2036)
        sExpress.append(dateOfBirthMap.get(StringUtils.DATE));
        sExpress.append(separation);//separation="/"
        sExpress.append(dateOfBirthMap.get(StringUtils.MONTH));
        sExpress.append(separation);
        sExpress.append(exYear);

        allExpressDate.put(EXPRESS_DATE_KEY, sExpress.toString());
        allExpressDate.put(EXPRESS_DATE_PASS_KEY, sPass.toString());
        return allExpressDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int getAge(String dateOfBirth, String dateRange) {
        Map<String, String> dateOfBirthMap = StringUtils.cutString(2, dateOfBirth);
        Map<String, String> dateExpressMap = StringUtils.cutString(2, dateRange);
        LocalDate dateOfBirthLC = LocalDate.of(Integer.parseInt(dateOfBirthMap.get(StringUtils.YEAR)),
                Integer.parseInt(dateOfBirthMap.get(StringUtils.MONTH)), Integer.parseInt(dateOfBirthMap.get(StringUtils.DATE)));

        LocalDate dateExpressLC = LocalDate.of(Integer.parseInt(dateExpressMap.get(StringUtils.YEAR)),
                Integer.parseInt(dateExpressMap.get(StringUtils.MONTH)), Integer.parseInt(dateExpressMap.get(StringUtils.DATE)));
        return Period.between(dateOfBirthLC, dateExpressLC).getYears();
    }

    public QRPersonal getPersonal() {
        return personal;
    }

    public void setPersonal(QRPersonal personal) {
        this.personal = personal;
    }

    public MrzInfo getMrzInfo() {
        return mrzInfo;
    }

    public void setMrzInfo(MrzInfo mrzInfo) {
        this.mrzInfo = mrzInfo;
    }
}