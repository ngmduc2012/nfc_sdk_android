package cist.cmc.nfc.sdk.core.models;

import android.graphics.Bitmap;

import androidx.annotation.Keep;

import java.util.List;

@Keep
public class PersonDetails {

    private String name;
    private String surname;
    private String personalNumber;
    private String gender;
    private String birthDate;
    private String expiryDate;
    private String serialNumber;
    private String nationality;
    private String issuerAuthority;
    private Bitmap faceImage;
    private Bitmap portraitImage;
    private Bitmap signature;
    private String signatureBase64;
    private List<Bitmap> fingerprints;
    private String mrzInfo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getIssuerAuthority() {
        return issuerAuthority;
    }

    public void setIssuerAuthority(String issuerAuthority) {
        this.issuerAuthority = issuerAuthority;
    }

    public Bitmap getFaceImage() {
        return faceImage;
    }

    public void setFaceImage(Bitmap faceImage) {
        this.faceImage = faceImage;
    }


    public Bitmap getPortraitImage() {
        return portraitImage;
    }

    public void setPortraitImage(Bitmap portraitImage) {
        this.portraitImage = portraitImage;
    }


    public Bitmap getSignature() {
        return signature;
    }

    public void setSignature(Bitmap signature) {
        this.signature = signature;
    }

    public String getSignatureBase64() {
        return signatureBase64;
    }

    public void setSignatureBase64(String signatureBase64) {
        this.signatureBase64 = signatureBase64;
    }

    public List<Bitmap> getFingerprints() {
        return fingerprints;
    }

    public void setFingerprints(List<Bitmap> fingerprints) {
        this.fingerprints = fingerprints;
    }

    public String getMrzInfo() {
        return mrzInfo;
    }

    public void setMrzInfo(String mrzInfo) {
        this.mrzInfo = mrzInfo;
    }
}
