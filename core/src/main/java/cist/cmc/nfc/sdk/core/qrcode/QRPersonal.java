package cist.cmc.nfc.sdk.core.qrcode;

import androidx.annotation.Keep;

import java.io.Serializable;

/**
 * QRPersonal is a class where the owner's information is stored in the qrcode
 */
@Keep
public class QRPersonal implements Serializable {
    private String cccdNumber;
    private String name;
    private String dateOfBirth;
    private String sex;
    private String permanentAddress;
    private String dateOfIssue;
    private String dateExpiration;
    private String cmndNumber;

    public QRPersonal(String cccdNumber, String name, String dateOfBirth, String sex, String permanentAddress, String dateOfIssue, String dateExpiration, String cmndNumber) {
        this.cccdNumber = cccdNumber;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
        this.permanentAddress = permanentAddress;
        this.dateOfIssue = dateOfIssue;
        this.dateExpiration = dateExpiration;
        this.cmndNumber = cmndNumber;
    }

    public String getCccdNumber() {
        return cccdNumber;
    }

    public String getName() {
        return name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public String getDateOfIssue() {
        return dateOfIssue;
    }

    public String getDateExpiration() {
        return dateExpiration;
    }

    public String getCmndNumber() {
        return cmndNumber;
    }

}
