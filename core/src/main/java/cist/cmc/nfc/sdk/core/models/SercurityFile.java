package cist.cmc.nfc.sdk.core.models;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.Map;

import cist.cmc.nfc.sdk.core.nfc.extend.VerificationStatus;

@Keep
public class SercurityFile implements Serializable {
    private String countrySigningCertificateName;
    private String docSigningCertificateSerialNumber;
    private String docSigningCertificatePublicKeyAlgorithm;
    private String docSigningCertificateSigAlgName;
    private byte[] docSigningCertificateEncoded;
    private String docSigningCertificateIssuerDNNname;
    private String docSigningCertificateSubjectDNName;
    private String docSigningCertificateNotBefore;
    private String docSigningCertificateNotAfter;
    private String dsCert;
    private Map<Integer, VerificationStatus.HashMatchResult> hashMatchResults;

    public SercurityFile(
            String dsCert,
            String docSigningCertificateSerialNumber, String docSigningCertificatePublicKeyAlgorithm,
                         String docSigningCertificateSigAlgName, byte[] docSigningCertificateEncoded,
                         String docSigningCertificateIssuerDNNname, String docSigningCertificateSubjectDNName,
                         String docSigningCertificateNotBefore, String docSigningCertificateNotAfter,
                         Map<Integer, VerificationStatus.HashMatchResult> hashMatchResults) {
        this.dsCert=dsCert;
        this.docSigningCertificateSerialNumber = docSigningCertificateSerialNumber;
        this.docSigningCertificatePublicKeyAlgorithm = docSigningCertificatePublicKeyAlgorithm;
        this.docSigningCertificateSigAlgName = docSigningCertificateSigAlgName;
        this.docSigningCertificateEncoded = docSigningCertificateEncoded;
        this.docSigningCertificateIssuerDNNname = docSigningCertificateIssuerDNNname;
        this.docSigningCertificateSubjectDNName = docSigningCertificateSubjectDNName;
        this.docSigningCertificateNotBefore = docSigningCertificateNotBefore;
        this.docSigningCertificateNotAfter = docSigningCertificateNotAfter;
        this.hashMatchResults = hashMatchResults;
    }

    public String getDsCert() {
        return dsCert;
    }

    public void setDsCert(String dsCert) {
        this.dsCert = dsCert;
    }

    public String getDocSigningCertificateSerialNumber() {
        return docSigningCertificateSerialNumber;
    }

    public String getDocSigningCertificatePublicKeyAlgorithm() {
        return docSigningCertificatePublicKeyAlgorithm;
    }

    public String getDocSigningCertificateSigAlgName() {
        return docSigningCertificateSigAlgName;
    }

    public byte[] getDocSigningCertificateEncoded() {
        return docSigningCertificateEncoded;
    }

    public String getDocSigningCertificateIssuerDNNname() {
        return docSigningCertificateIssuerDNNname;
    }

    public String getDocSigningCertificateSubjectDNName() {
        return docSigningCertificateSubjectDNName;
    }

    public String getDocSigningCertificateNotBefore() {
        return docSigningCertificateNotBefore;
    }

    public String getDocSigningCertificateNotAfter() {
        return docSigningCertificateNotAfter;
    }

    public Map<Integer, VerificationStatus.HashMatchResult> getHashMatchResults() {
        return hashMatchResults;
    }
}
