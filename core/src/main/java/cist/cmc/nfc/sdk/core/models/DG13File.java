package cist.cmc.nfc.sdk.core.models;

import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cist.cmc.nfc.sdk.core.chip.lds.AbstractTaggedLDSFile;
import cist.cmc.nfc.sdk.core.utils.StringUtils;

public class DG13File {
    private OptionPersonal optionPersonal;
    private String allHex;
    private byte[] arr;
    private static final String CCCD_NUMBER_TAG = "020101";
    private static final String NAME_PERSONAL_TAG = "020102";
    private static final String DATE_OF_BIRTH_TAG = "020103";
    private static final String SEX_TAG = "020104";
    private static final String NATIONALITY_TAG = "020105";
    private static final String NATION_TAG = "020106";
    private static final String RELIGION_TAG = "020107";
    private static final String DISTRICT_TAG = "020108";
    private static final String PERMANENT_ADDRESS_TAG = "020109";
    private static final String IDENTIFY_CHARACTER_TAG = "02010A";
    private static final String DATE_OF_ISSUE_TAG = "02010B";
    private static final String DATE_EXPIRATION_TAG = "02010C";
    private static final String FATHER_NAME_TAG = "02010D";
    private static final String HUSBAND_WIFE_NAME_TAG = "02010E";
    private static final String CMND_NUMBER_TAG = "02010F";
    private static final String UNKNOW_TAG = "020110";

    private static final int TAG_LENGTH = 6;
    private static final int BIT_COUNT = 2;


    public DG13File(InputStream inputStream) throws IOException {
        this.arr = IOUtils.toByteArray(inputStream);
        this.allHex = StringUtils.bytesToHex(arr);
    }

    public void readPersonal() throws Exception {

        String CCCDNumber = readDataPersonal(CCCD_NUMBER_TAG);
        String personName = readDataPersonal(NAME_PERSONAL_TAG);
        String dateOfBirth = readDataPersonal(DATE_OF_BIRTH_TAG);
        String sex = readDataPersonal(SEX_TAG);
        String nationality = readDataPersonal(NATIONALITY_TAG);
        String nation = readDataPersonal(NATION_TAG);
        String religion = readDataPersonal(RELIGION_TAG);
        String district = readDataPersonal(DISTRICT_TAG);
        String permanent = readDataPersonal(PERMANENT_ADDRESS_TAG);
        String identifile = readDataPersonal(IDENTIFY_CHARACTER_TAG);
        String dateOfIssue = readDataPersonal(DATE_OF_ISSUE_TAG);
        String dateExprity = readDataPersonal(DATE_EXPIRATION_TAG);
        String fatherName = readFatherName(FATHER_NAME_TAG);
        String motherName = readMotherName();
        String husbanOrWifeName = readHusbanOrWifeName(HUSBAND_WIFE_NAME_TAG, CMND_NUMBER_TAG);
        String CMNDNumber = readCCNDNumber(CMND_NUMBER_TAG, UNKNOW_TAG);

        this.optionPersonal = new OptionPersonal(CCCDNumber, personName, dateOfBirth, sex, nationality, nation, religion,
                district, permanent, identifile, dateOfIssue, dateExprity, fatherName, motherName,
                husbanOrWifeName, CMNDNumber);
    }


    private String readDataPersonal(String tag) throws Exception {
        int startIndexLengthValue = 0;
        if (tag.equals(CCCD_NUMBER_TAG)) {
            startIndexLengthValue = allHex.lastIndexOf(tag) + TAG_LENGTH + BIT_COUNT;
        } else {
            startIndexLengthValue = allHex.indexOf(tag) + TAG_LENGTH + BIT_COUNT;
        }
        int endIndexLengthValue = startIndexLengthValue + BIT_COUNT;
        String lengthValue = allHex.substring(startIndexLengthValue, endIndexLengthValue);
        int length = Integer.parseInt(lengthValue, 16);
        String value = allHex.substring(endIndexLengthValue, endIndexLengthValue + length * BIT_COUNT);
        String valueString = StringUtils.hexToUTF8(value);
        return valueString;
    }

    private int fatherLength = 0;
    private String fatherValueHex = "";

    private String readFatherName(String tag) throws Exception {
        if (tag.equals(FATHER_NAME_TAG) == false) {
            return "";
        }
        int startIndexLengthValue = allHex.indexOf(tag) + TAG_LENGTH * 2;

        int endIndexLengthValue = startIndexLengthValue + BIT_COUNT;
        String lengthValue = allHex.substring(startIndexLengthValue, endIndexLengthValue);
        int length = Integer.parseInt(lengthValue, 16);
        fatherLength = length;
        String value = allHex.substring(endIndexLengthValue, endIndexLengthValue + length * BIT_COUNT);
        fatherValueHex = value;
        String valueString = StringUtils.hexToUTF8(value);
        return valueString;
    }

    private String readMotherName() throws Exception {
        int startIndexLengthValue = allHex.indexOf(fatherValueHex) + fatherLength * BIT_COUNT + TAG_LENGTH;
        int endIndexLengthValue = startIndexLengthValue + BIT_COUNT;
        String lengthValue = allHex.substring(startIndexLengthValue, endIndexLengthValue);
        int length = Integer.parseInt(lengthValue, 16);
        String value = allHex.substring(endIndexLengthValue, endIndexLengthValue + length * BIT_COUNT);
        String valueString = StringUtils.hexToUTF8(value);
        return valueString;
    }

    private String readHusbanOrWifeName(String startTag, String endTag) throws Exception {
        if (startTag.equals(HUSBAND_WIFE_NAME_TAG) == false) {
            return "";
        }
        if (endTag.equals(CMND_NUMBER_TAG) == false) {
            return "";
        }

        String nextTag = getNextTag(startTag);
        if (endTag.equals(nextTag) == true) {
            return "";
        }
        int startIndexLengthValue = allHex.indexOf(startTag) + TAG_LENGTH * 2;
        int endIndexLengthValue = startIndexLengthValue + BIT_COUNT;
        String lengthValue = allHex.substring(startIndexLengthValue, endIndexLengthValue);
        int length = Integer.parseInt(lengthValue, 16);
        String value = allHex.substring(endIndexLengthValue, endIndexLengthValue + length * BIT_COUNT);
        String valueString = StringUtils.hexToUTF8(value);
        return valueString;
    }

    private String getNextTag(String currentTag) {
        int indexStart = allHex.indexOf(currentTag) + TAG_LENGTH + BIT_COUNT * 2;
        int indexEnd = indexStart + TAG_LENGTH;
        String nextTag = allHex.substring(indexStart, indexEnd);
        return nextTag;
    }

    private String readCCNDNumber(String startTag, String endTag) throws Exception {
        if (startTag.equals(CMND_NUMBER_TAG) == false) {
            return "";
        }
        if (endTag.equals(UNKNOW_TAG) == false) {
            return "";
        }
        String nextTag = getNextTag(startTag);
        if (endTag.equals(nextTag) == true) {
            return "";
        }
        int startIndexLengthValue = allHex.indexOf(startTag) + TAG_LENGTH + BIT_COUNT;
        int endIndexLengthValue = startIndexLengthValue + BIT_COUNT;
        String lengthValue = allHex.substring(startIndexLengthValue, endIndexLengthValue);
        int length = Integer.parseInt(lengthValue, 16);
        String value = allHex.substring(endIndexLengthValue, endIndexLengthValue + length * BIT_COUNT);
        String valueString = StringUtils.hexToUTF8(value);
        return valueString;
    }

    public OptionPersonal getOptionPersonal() {
        return optionPersonal;
    }

    public String getAllHex() {
        return allHex;
    }

    public byte[] getArr() {
        return arr;
    }
}
