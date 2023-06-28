package cist.cmc.nfc.sdk.core.models;

import androidx.annotation.Keep;

import java.io.Serializable;

import cist.cmc.nfc.sdk.core.chip.lds.SODFile;
import cist.cmc.nfc.sdk.core.nfc.extend.FeatureStatus;
import cist.cmc.nfc.sdk.core.nfc.extend.VerificationStatus;


@Keep
public class EDocument implements Serializable {
    private DocType docType;
    private OptionPersonal optionPerson;
    private PersonDetails personDetails;
    private SercurityFile sercurityFile;
    FeatureStatus featureStatus;
    VerificationStatus verificationStatus;

    public EDocument() {
    }

    public EDocument(FeatureStatus featureStatus, VerificationStatus verificationStatus) {
        this.featureStatus = featureStatus;
        this.verificationStatus = verificationStatus;
    }

    public EDocument(OptionPersonal optionPerson, PersonDetails personDetails, FeatureStatus featureStatus,
                     VerificationStatus verificationStatus) {
        this.optionPerson = optionPerson;
        this.personDetails = personDetails;
        this.featureStatus = featureStatus;
        this.verificationStatus = verificationStatus;
    }

    public EDocument(OptionPersonal optionPerson, PersonDetails personDetails, SercurityFile sercurityFile
            , FeatureStatus featureStatus, VerificationStatus verificationStatus) {
        this.optionPerson = optionPerson;
        this.personDetails = personDetails;
        this.sercurityFile = sercurityFile;
        this.featureStatus = featureStatus;
        this.verificationStatus = verificationStatus;
    }

    public DocType getDocType() {
        return docType;
    }

    public OptionPersonal getOptionPerson() {
        return optionPerson;
    }

    public PersonDetails getPersonDetails() {
        return personDetails;
    }

    public SercurityFile getSercurityFile() {
        return sercurityFile;
    }

    public FeatureStatus getFeatureStatus() {
        return featureStatus;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }
}
