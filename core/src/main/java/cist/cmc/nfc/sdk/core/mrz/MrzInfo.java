package cist.cmc.nfc.sdk.core.mrz;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class MrzInfo implements Serializable {
    private String documentNumber;
    private String birthDate;
    private String expiryDate;
    private String documentNumberFull;
    public static final String expiryDateUnlimited = "991231";

    public MrzInfo(String documentNumber, String birthDate, String expiryDate) {
        this.documentNumber = documentNumber;
        this.birthDate = birthDate;
        this.expiryDate = expiryDate;
    }

    public MrzInfo(String documentNumber, String birthDate, String expiryDate, String documentNumberFull) {
        this.documentNumber = documentNumber;
        this.birthDate = birthDate;
        this.expiryDate = expiryDate;
        this.documentNumberFull = documentNumberFull;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getDocumentNumberFull() {
        return documentNumberFull;
    }

    public void setDocumentNumberFull(String documentNumberFull) {
        this.documentNumberFull = documentNumberFull;
    }

    @Override
    public String toString() {
        return "MrzInfo{" +
                "documentNumber='" + documentNumber + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                '}';
    }
}
