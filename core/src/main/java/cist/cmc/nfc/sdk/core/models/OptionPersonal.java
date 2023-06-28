package cist.cmc.nfc.sdk.core.models;

import androidx.annotation.Keep;

@Keep
public class OptionPersonal {
    private String CCCDNumber;
    private String name;
    private String dateOfBirth;
    private String sex;
    private String nationality;
    private String nation;
    private String religion;
    private String district;
    private String permanentAddress;
    private String identifyingCharacteristics;
    private String dateOfIssue;
    private String dateExpiration;
    private String fatherName;
    private String motherName;
    private String husbandOrWifeName;
    private String CMNDNumber;

    public OptionPersonal(String CCCDNumber, String name, String dateOfBirth, String sex, String nationality,
                          String nation, String religion, String district, String permanentAddress,
                          String identifyingCharacteristics, String dateOfIssue, String dateExpiration,
                          String fatherName, String motherName, String husbandOrWifeName, String CMNDNumber) {
        this.CCCDNumber = CCCDNumber;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
        this.nationality = nationality;
        this.nation = nation;
        this.religion = religion;
        this.district = district;
        this.permanentAddress = permanentAddress;
        this.identifyingCharacteristics = identifyingCharacteristics;
        this.dateOfIssue = dateOfIssue;
        this.dateExpiration = dateExpiration;
        this.fatherName = fatherName;
        this.motherName = motherName;
        this.husbandOrWifeName = husbandOrWifeName;
        this.CMNDNumber = CMNDNumber;
    }

    public String getCCCDNumber() {
        return CCCDNumber;
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

    public String getNationality() {
        return nationality;
    }

    public String getNation() {
        return nation;
    }

    public String getReligion() {
        return religion;
    }

    public String getDistrict() {
        return district;
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public String getIdentifyingCharacteristics() {
        return identifyingCharacteristics;
    }

    public String getDateOfIssue() {
        return dateOfIssue;
    }

    public String getDateExpiration() {
        return dateExpiration;
    }

    public String getFatherName() {
        return fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public String getHusbandOrWifeName() {
        return husbandOrWifeName;
    }

    public String getCMNDNumber() {
        return CMNDNumber;
    }

    @Override
    public String toString() {
        return "OptionPersonal{" +
                "CCCDNumber='" + CCCDNumber + '\'' +
                ", name='" + name + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", sex='" + sex + '\'' +
                ", nationality='" + nationality + '\'' +
                ", nation='" + nation + '\'' +
                ", religion='" + religion + '\'' +
                ", district='" + district + '\'' +
                ", permanentAddress='" + permanentAddress + '\'' +
                ", identifyingCharacteristics='" + identifyingCharacteristics + '\'' +
                ", dateOfIssue='" + dateOfIssue + '\'' +
                ", dateExpiration='" + dateExpiration + '\'' +
                ", fatherName='" + fatherName + '\'' +
                ", motherName='" + motherName + '\'' +
                ", husbandOrWifeName='" + husbandOrWifeName + '\'' +
                ", CMNDNumber='" + CMNDNumber + '\'' +
                '}';
    }
}
