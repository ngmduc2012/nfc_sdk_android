package cist.cmc.nfc.sdk.core.nfc.extend;

import android.util.Log;

import androidx.annotation.Keep;

import net.sf.scuba.smartcards.CardServiceException;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequence;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;

import cist.cmc.nfc.sdk.core.chip.BACKey;
import cist.cmc.nfc.sdk.core.chip.PACEKeySpec;
import cist.cmc.nfc.sdk.core.chip.PassportService;
import cist.cmc.nfc.sdk.core.chip.Util;
import cist.cmc.nfc.sdk.core.chip.cert.CVCPrincipal;
import cist.cmc.nfc.sdk.core.chip.cert.CardVerifiableCertificate;
import cist.cmc.nfc.sdk.core.chip.lds.AbstractTaggedLDSFile;
import cist.cmc.nfc.sdk.core.chip.lds.ActiveAuthenticationInfo;
import cist.cmc.nfc.sdk.core.chip.lds.CVCAFile;
import cist.cmc.nfc.sdk.core.chip.lds.CardAccessFile;
import cist.cmc.nfc.sdk.core.chip.lds.ChipAuthenticationInfo;
import cist.cmc.nfc.sdk.core.chip.lds.ChipAuthenticationPublicKeyInfo;
import cist.cmc.nfc.sdk.core.chip.lds.LDSFileUtil;
import cist.cmc.nfc.sdk.core.chip.lds.PACEInfo;
import cist.cmc.nfc.sdk.core.chip.lds.SODFile;
import cist.cmc.nfc.sdk.core.chip.lds.SecurityInfo;
import cist.cmc.nfc.sdk.core.chip.lds.icao.COMFile;
import cist.cmc.nfc.sdk.core.chip.lds.icao.DG11File;
import cist.cmc.nfc.sdk.core.chip.lds.icao.DG12File;
import cist.cmc.nfc.sdk.core.chip.lds.icao.DG14File;
import cist.cmc.nfc.sdk.core.chip.lds.icao.DG15File;
import cist.cmc.nfc.sdk.core.chip.lds.icao.DG1File;
import cist.cmc.nfc.sdk.core.chip.lds.icao.DG2File;
import cist.cmc.nfc.sdk.core.chip.lds.icao.DG3File;
import cist.cmc.nfc.sdk.core.chip.lds.icao.DG5File;
import cist.cmc.nfc.sdk.core.chip.lds.icao.DG7File;
import cist.cmc.nfc.sdk.core.chip.protocol.AAResult;
import cist.cmc.nfc.sdk.core.chip.protocol.BACResult;
import cist.cmc.nfc.sdk.core.chip.protocol.EACCAResult;
import cist.cmc.nfc.sdk.core.chip.protocol.EACTAResult;
import cist.cmc.nfc.sdk.core.chip.protocol.PACEResult;
import cist.cmc.nfc.sdk.core.models.DG13File;
import cist.cmc.nfc.sdk.core.mrz.MrzInfo;
import cist.cmc.nfc.sdk.core.utils.EACCredentials;
import cist.cmc.nfc.sdk.core.utils.PassportNfcUtils;

public class PassportNFC {

    private static Provider BC_PROVIDER = JMRTDSecurityProvider.getSpongyCastleProvider();
    private static List<BACKey> EMPTY_TRIED_BAC_ENTRY_LIST = new ArrayList<>();
    private static List<Certificate> EMPTY_CERTIFICATE_CHAIN = new ArrayList<>();
    private static String TAG = PassportNFC.class.getSimpleName();

    /**
     * The hash function for DG hashes.
     */
    private MessageDigest digest = null;
    /**
     * Gets the supported features (such as: BAC, AA, EAC) as
     * discovered during initialization of this document.
     *
     * @return the supported features
     * @since 0.4.9
     */
    /* The feature status has been created in constructor. */
    private FeatureStatus features = new FeatureStatus();
    /**
     * Gets the verification status thus far.
     *
     * @return the verification status
     * @since 0.4.9
     */
    private VerificationStatus verificationStatus = new VerificationStatus();

    /* We use a cipher to help implement Active Authentication RSA with ISO9796-2 message recovery. */
    private Signature rsaAASignature = Signature.getInstance("SHA1WithRSA/ISO9796-2", BC_PROVIDER);
    private MessageDigest rsaAADigest = MessageDigest.getInstance("SHA1");
    private Cipher rsaAACipher = Cipher.getInstance("RSA/NONE/NoPadding");
    private Signature ecdsaAASignature = Signature.getInstance("SHA256withECDSA", BC_PROVIDER);
    private MessageDigest ecdsaAADigest = MessageDigest.getInstance("SHA-256"); /* NOTE: for output length measurement only. -- MO */
    /**
     * Gets the CSCA, CVCA trust store.
     *
     * @return the trust store in use
     */
    private MRTDTrustStore trustManager = null;

    private PassportService service = null;
    private String canNumber = null;
    private MrzInfo mrzInfo = null;
    private Random random = new SecureRandom();

    private COMFile comFile = null;
    private SODFile sodFile = null;
    private DG1File dg1File = null;
    private DG2File dg2File = null;
    private DG3File dg3File = null;
    private DG5File dg5File = null;
    private DG7File dg7File = null;
    private DG11File dg11File = null;
    private DG12File dg12File = null;
    private DG13File dg13File = null;
    private DG14File dg14File = null;
    private DG15File dg15File = null;
    private CVCAFile cvcaFile = null;
    private int checkReadData = 0;
    private int checkPass = 0;

    public PassportNFC(PassportService ps, MRTDTrustStore trustManager, MrzInfo mrzInfo, String canNumber) throws CardServiceException, GeneralSecurityException {
        if (ps == null) {
            throw new IllegalArgumentException("Service cannot be null");
        }
        this.service = ps;
        this.trustManager = trustManager;
        this.mrzInfo = mrzInfo;
        this.canNumber = canNumber;

        boolean hasSAC;
        boolean isSACSucceeded = false;
        PACEResult paceResult = null;
        try {
            service.open();
            /* Find out whether this MRTD supports SAC. */
            try {
                Log.i(TAG, "Inspecting card access file");
                CardAccessFile cardAccessFile = new CardAccessFile(ps.getInputStream(PassportService.EF_CARD_ACCESS));
                Collection<SecurityInfo> securityInfos = cardAccessFile.getSecurityInfos();
                for (SecurityInfo securityInfo : securityInfos) {
                    if (securityInfo instanceof PACEInfo) {
                        features.setHasSAC(FeatureStatus.Verdict.PRESENT);
                    }
                }
            } catch (Exception e) {
                /* NOTE: No card access file, continue to test for BAC. */
                Log.i(TAG, "DEBUG: failed to get card access file: " + e.getMessage());
                e.printStackTrace();
            }

            hasSAC = features.getHasSAC().equals(FeatureStatus.Verdict.PRESENT);
            if (hasSAC) {
                try {
                    if (mrzInfo != null) {
                        paceResult = doPACE(ps, mrzInfo, null);
                        isSACSucceeded = true;
                    } else {
                        paceResult = doPACE(ps, null, canNumber);
                        isSACSucceeded = true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "PACE failed, falling back to BAC");
                    isSACSucceeded = false;
                    checkPass = 2;
                    return;
                }
            }
            service.sendSelectApplet(isSACSucceeded);

        } catch (CardServiceException cse) {
            throw cse;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("hau", "đã vào đây");
            throw new CardServiceException("Cannot open document. " + e.getMessage());
        }
        /* Find out whether this MRTD supports BAC. */
        try {
            /* Attempt to read EF.COM before BAC. */
            service.getInputStream(PassportService.EF_COM);
            if (isSACSucceeded) {
                verificationStatus.setSAC(VerificationStatus.Verdict.SUCCEEDED, "Succeeded");
                features.setHasBAC(FeatureStatus.Verdict.UNKNOWN);
                verificationStatus.setBAC(VerificationStatus.Verdict.NOT_CHECKED, "Using SAC, BAC not checked", EMPTY_TRIED_BAC_ENTRY_LIST);
            } else {
                /* We failed SAC, and we failed BAC. */
                features.setHasBAC(FeatureStatus.Verdict.NOT_PRESENT);
                verificationStatus.setBAC(VerificationStatus.Verdict.NOT_PRESENT, "Non-BAC document", EMPTY_TRIED_BAC_ENTRY_LIST);
            }
        } catch (Exception e) {
            Log.i(TAG, "Attempt to read EF.COM before BAC failed with: " + e.getMessage());
            features.setHasBAC(FeatureStatus.Verdict.PRESENT);
            verificationStatus.setBAC(VerificationStatus.Verdict.NOT_CHECKED, "BAC document", EMPTY_TRIED_BAC_ENTRY_LIST);
        }
        /* If we have to do BAC, try to do BAC. */
        boolean hasBAC = features.getHasBAC().equals(FeatureStatus.Verdict.PRESENT);
        if (hasBAC && !(hasSAC && isSACSucceeded)) {
            BACKey bacKey = new BACKey(mrzInfo.getDocumentNumber(), mrzInfo.getBirthDate(), mrzInfo.getExpiryDate());
            List<BACKey> triedBACEntries = new ArrayList<>();
            triedBACEntries.add(bacKey);
            try {
                doBAC(service, mrzInfo);
                verificationStatus.setBAC(VerificationStatus.Verdict.SUCCEEDED, "BAC succeeded with key " + bacKey, triedBACEntries);
            } catch (Exception e) {
                verificationStatus.setBAC(VerificationStatus.Verdict.FAILED, "BAC failed", triedBACEntries);
            }
        }
        /* Pre-read these files that are always present. */
        Set<Integer> dgNumbersAlreadyRead = new TreeSet<>();
        try {
            comFile = getComFile(ps);
            sodFile = getSodFile(ps);
            dg1File = getDg1File(ps);
            dgNumbersAlreadyRead.add(1);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            Log.w(TAG, "Could not read file");
        }
        try {
            dg14File = getDG14File(ps);
        } catch (Exception e) {
            e.printStackTrace();
            checkReadData = 2;
        }
        try {
            cvcaFile = getCVCAFile(ps);
        } catch (Exception e) {
            e.printStackTrace();
            checkReadData = 2;
        }

        /* Get the list of DGs from EF.SOd, we don't trust EF.COM. */
        List<Integer> dgNumbers = new ArrayList<>();
        if (sodFile != null) {
            dgNumbers.addAll(sodFile.getDataGroupHashes().keySet());
        } else if (comFile != null) {
            /* Get the list from EF.COM since we failed to parse EF.SOd. */
            Log.w(TAG, "Failed to get DG list from EF.SOd. Getting DG list from EF.COM.");
            int[] tagList = comFile.getTagList();
            dgNumbers.addAll(toDataGroupList(tagList));
        }
        Collections.sort(dgNumbers);
        Log.i(TAG, "Found DGs: " + dgNumbers);
        Map<Integer, VerificationStatus.HashMatchResult> hashResults = verificationStatus.getHashResults();
        if (hashResults == null) {
            hashResults = new TreeMap<>();
        }
        if (sodFile != null) {
            Map<Integer, byte[]> storedHashes = sodFile.getDataGroupHashes();
            for (int dgNumber : dgNumbers) {
                byte[] storedHash = storedHashes.get(dgNumber);
                VerificationStatus.HashMatchResult hashResult = hashResults.get(dgNumber);
                if (hashResult != null) {
                    continue;
                }
                if (dgNumbersAlreadyRead.contains(dgNumber)) {
                    hashResult = verifyHash(dgNumber);
                } else {
                    hashResult = new VerificationStatus.HashMatchResult(storedHash, null);
                }
                hashResults.put(dgNumber, hashResult);
            }
        }
        verificationStatus.setHT(VerificationStatus.Verdict.UNKNOWN, verificationStatus.getHtReason(), hashResults);
        /* Check EAC support by DG14 presence. */
        if (dgNumbers.contains(14)) {
            features.setHasEAC(FeatureStatus.Verdict.PRESENT);
            features.setHasCA(FeatureStatus.Verdict.PRESENT);
        } else {
            features.setHasEAC(FeatureStatus.Verdict.NOT_PRESENT);
            features.setHasCA(FeatureStatus.Verdict.NOT_PRESENT);
        }
        boolean hasCA = features.getHasCA().equals(FeatureStatus.Verdict.PRESENT);
        if (hasCA) {
            try {
                List<EACCAResult> eaccaResults = doEACCA(ps, mrzInfo, dg14File, sodFile);
                verificationStatus.setCA(VerificationStatus.Verdict.SUCCEEDED, "EAC succeeded", eaccaResults.get(0));
            } catch (Exception e) {
                verificationStatus.setCA(VerificationStatus.Verdict.FAILED, "CA Failed", null);
            }
        }
        boolean hasEAC = features.getHasEAC().equals(FeatureStatus.Verdict.PRESENT);
        List<KeyStore> cvcaKeyStores = trustManager.getCvcaStores();
        if (hasEAC && cvcaKeyStores != null && cvcaKeyStores.size() > 0 && verificationStatus.getCa().equals(VerificationStatus.Verdict.SUCCEEDED)) {
            try {
                List<EACTAResult> eactaResults = doEACTA(ps, mrzInfo, cvcaFile, paceResult, verificationStatus.getCaResult(), cvcaKeyStores);
                verificationStatus.setEAC(VerificationStatus.Verdict.SUCCEEDED, "EAC succeeded", eactaResults.get(0));
            } catch (Exception e) {
                e.printStackTrace();
                verificationStatus.setEAC(VerificationStatus.Verdict.FAILED, "EAC Failed", null);
            }
            dgNumbersAlreadyRead.add(14);
        }
        /* Check AA support by DG15 presence. */
        if (dgNumbers.contains(15)) {
            features.setHasAA(FeatureStatus.Verdict.PRESENT);
        } else {
            features.setHasAA(FeatureStatus.Verdict.NOT_PRESENT);
        }
        boolean hasAA = features.getHasAA().equals(FeatureStatus.Verdict.PRESENT);
        if (hasAA) {
            try {
                dg15File = getDg15File(ps);
                dgNumbersAlreadyRead.add(15);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                Log.w(TAG, "Could not read file");
            } catch (Exception e) {
                verificationStatus.setAA(VerificationStatus.Verdict.NOT_CHECKED, "Failed to read DG15");
            }
        } else {
            /* Feature status says: no AA, so verification status should say: no AA. */
            verificationStatus.setAA(VerificationStatus.Verdict.NOT_PRESENT, "AA is not supported");
        }
        try {
            dg2File = getDG2File(ps);
        } catch (Exception e) {
            e.printStackTrace();
            checkReadData = 2;
        }
        try {
            dg13File = getDg13File(ps);
        } catch (Exception e) {
            e.printStackTrace();
            checkReadData = 2;
        }
    }

    private PACEResult doPACE(PassportService ps, MrzInfo mrzInfo, String canNumber) throws GeneralSecurityException, IOException, CardServiceException {
        PACEResult paceResult = null;
        InputStream isCardAccessFile = null;
        try {
            PACEKeySpec paceKeySpec = null;
            if (mrzInfo != null) {
                BACKey bacKey = new BACKey(mrzInfo.getDocumentNumber(), mrzInfo.getBirthDate(), mrzInfo.getExpiryDate());
                paceKeySpec = PACEKeySpec.createMRZKey(bacKey);
            } else {
                paceKeySpec = PACEKeySpec.createCANKey(canNumber);
            }
            isCardAccessFile = ps.getInputStream(PassportService.EF_CARD_ACCESS);
            CardAccessFile cardAccessFile = new CardAccessFile(isCardAccessFile);
            Collection<SecurityInfo> securityInfos = cardAccessFile.getSecurityInfos();
            SecurityInfo securityInfo = securityInfos.iterator().next();
            List<PACEInfo> paceInfos = new ArrayList<>();
            if (securityInfo instanceof PACEInfo) {
                paceInfos.add((PACEInfo) securityInfo);
            }
            if (paceInfos.size() > 0) {
                PACEInfo paceInfo = paceInfos.iterator().next();
                paceResult = ps.doPACE(paceKeySpec, paceInfo.getObjectIdentifier(), PACEInfo.toParameterSpec(paceInfo.getParameterId()));
            }

        } finally {
            if (isCardAccessFile != null) {
                isCardAccessFile.close();
                isCardAccessFile = null;
            }
        }
        return paceResult;
    }

    private BACResult doBAC(PassportService ps, MrzInfo mrzInfo) throws CardServiceException {
        BACKey bacKey = new BACKey(mrzInfo.getDocumentNumber(), mrzInfo.getBirthDate(), mrzInfo.getExpiryDate());
        return ps.doBAC(bacKey);
    }

    private COMFile getComFile(PassportService ps) throws CardServiceException, IOException {
        InputStream isComFile = null;
        try {
            isComFile = ps.getInputStream(PassportService.EF_COM);
            return (COMFile) LDSFileUtil.getLDSFile(PassportService.EF_COM, isComFile);
        } finally {
            if (isComFile != null) {
                isComFile.close();
                isComFile = null;
            }
        }
    }

    private SODFile getSodFile(PassportService ps) throws CardServiceException, IOException {
        InputStream isSodFile = null;
        try {
            isSodFile = ps.getInputStream(PassportService.EF_SOD);
            return (SODFile) LDSFileUtil.getLDSFile(PassportService.EF_SOD, isSodFile);
        } finally {
            if (isSodFile != null) {
                isSodFile.close();
                isSodFile = null;
            }
        }
    }

    private DG1File getDg1File(PassportService ps) throws CardServiceException, IOException {
        InputStream isDG1 = null;
        try {
            isDG1 = ps.getInputStream(PassportService.EF_DG1);
            return (DG1File) LDSFileUtil.getLDSFile(PassportService.EF_DG1, isDG1);
        } finally {
            if (isDG1 != null) {
                isDG1.close();
                isDG1 = null;
            }
        }
    }

    private DG2File getDG2File(PassportService ps) throws CardServiceException, IOException {
        InputStream isDG2 = null;
        try {
            isDG2 = ps.getInputStream(PassportService.EF_DG2);
            return (DG2File) LDSFileUtil.getLDSFile(PassportService.EF_DG2, isDG2);
        } finally {
            if (isDG2 != null) {
                isDG2.close();
                isDG2 = null;
            }
        }
    }


    private DG13File getDg13File(PassportService ps) throws CardServiceException, IOException {
        InputStream isDG13 = null;
        try {
            isDG13 = ps.getInputStream(PassportService.EF_DG13);
            return new DG13File(isDG13);
        } finally {
            if (isDG13 != null) {
                isDG13.close();
                isDG13 = null;
            }
        }
    }

    private DG14File getDG14File(PassportService ps) throws CardServiceException, IOException {
        // Basic data
        InputStream isDG14 = null;
        try {
            isDG14 = ps.getInputStream(PassportService.EF_DG14);
            return (DG14File) LDSFileUtil.getLDSFile(PassportService.EF_DG14, isDG14);
        } finally {
            if (isDG14 != null) {
                isDG14.close();
                isDG14 = null;
            }
        }
    }

    private DG15File getDg15File(PassportService ps) throws CardServiceException, IOException {
        InputStream isDG15 = null;
        try {
            isDG15 = ps.getInputStream(PassportService.EF_DG15);
            return (DG15File) LDSFileUtil.getLDSFile(PassportService.EF_DG15, isDG15);
        } finally {
            if (isDG15 != null) {
                isDG15.close();
                isDG15 = null;
            }
        }
    }

    private CVCAFile getCVCAFile(PassportService ps) throws CardServiceException, IOException {
        // Basic data
        InputStream isEF_CVCA = null;
        try {
            isEF_CVCA = ps.getInputStream(PassportService.EF_CVCA);
            return (CVCAFile) LDSFileUtil.getLDSFile(PassportService.EF_CVCA, isEF_CVCA);
        } finally {
            if (isEF_CVCA != null) {
                isEF_CVCA.close();
                isEF_CVCA = null;
            }
        }
    }

    private List<Integer> toDataGroupList(int[] tagList) {
        if (tagList == null) {
            return null;
        }
        List<Integer> dgNumberList = new ArrayList<>(tagList.length);

        for (int tag : tagList) {
            try {
                int dgNumber = LDSFileUtil.lookupDataGroupNumberByTag(tag);
                dgNumberList.add(dgNumber);
            } catch (NumberFormatException nfe) {
                Log.w(TAG, "Could not find DG number for tag: " + Integer.toHexString(tag));
                nfe.printStackTrace();
            }

        }
        return dgNumberList;
    }

    private VerificationStatus.HashMatchResult verifyHash(int dgNumber) {
        Map<Integer, VerificationStatus.HashMatchResult> hashResults = verificationStatus.getHashResults();
        if (hashResults == null) {
            hashResults = new TreeMap<>();
        }
        return verifyHash(dgNumber, hashResults);
    }

    /**
     * Verifies the hash for the given datagroup.
     * Note that this will block until all bytes of the datagroup
     * are loaded.
     *
     * @param dgNumber
     * @param hashResults
     * @return
     */
    private VerificationStatus.HashMatchResult verifyHash(int dgNumber, Map<Integer, VerificationStatus.HashMatchResult> hashResults) {
        short fid = LDSFileUtil.lookupFIDByTag(LDSFileUtil.lookupTagByDataGroupNumber(dgNumber));
        /* Get the stored hash for the DG. */
        byte[] storedHash = null;

        try {
            Map<Integer, byte[]> storedHashes = sodFile.getDataGroupHashes();
            storedHash = storedHashes.get(dgNumber);
        } catch (Exception e) {
            verificationStatus.setHT(VerificationStatus.Verdict.FAILED, "DG" + dgNumber + " failed, could not get stored hash", hashResults);
            return null;
        }
        /* Initialize hash. */
        String digestAlgorithm = sodFile.getDigestAlgorithm();
        try {
            digest = getDigest(digestAlgorithm);
        } catch (NoSuchAlgorithmException nsae) {
            verificationStatus.setHT(VerificationStatus.Verdict.FAILED, "Unsupported algorithm \"" + digestAlgorithm + "\"", null);
            return null; // DEBUG -- MO
        }
        /* Read the DG. */
        byte[] dgBytes = null;
        try {
            AbstractTaggedLDSFile abstractTaggedLDSFile = getDG(fid);
            if (abstractTaggedLDSFile != null) {
                dgBytes = abstractTaggedLDSFile.getEncoded();
            }
            if (abstractTaggedLDSFile == null && verificationStatus.getEac() != VerificationStatus.Verdict.SUCCEEDED &&
                    (fid == PassportService.EF_DG3 || fid == PassportService.EF_DG4)) {
                Log.w(TAG, "Skipping DG" + dgNumber + " during HT verification because EAC failed.");
                VerificationStatus.HashMatchResult hashResult = new VerificationStatus.HashMatchResult(storedHash, null);
                hashResults.put(dgNumber, hashResult);
                return hashResult;
            }
            if (abstractTaggedLDSFile == null) {
                if (fid == PassportService.EF_DG13) {
                    dgBytes = dg13File.getArr();
                    /* Compute the hash and compare. */
                    try {
                        byte[] computedHash = digest.digest(dgBytes);
                        VerificationStatus.HashMatchResult hashResult = new VerificationStatus.HashMatchResult(storedHash, computedHash);
                        hashResults.put(dgNumber, hashResult);
                        if (!Arrays.equals(storedHash, computedHash)) {
                            verificationStatus.setHT(VerificationStatus.Verdict.FAILED, "Hash mismatch", hashResults);
                        }
                        return hashResult;
                    } catch (Exception ioe) {
                        VerificationStatus.HashMatchResult hashResult = new VerificationStatus.HashMatchResult(storedHash, null);
                        hashResults.put(dgNumber, hashResult);
                        verificationStatus.setHT(VerificationStatus.Verdict.FAILED, "Hash failed due to exception", hashResults);
                        return hashResult;
                    }
                } else {
                    Log.w(TAG, "Skipping DG" + dgNumber + " during HT verification because file could not be read.");
                    VerificationStatus.HashMatchResult hashResult = new VerificationStatus.HashMatchResult(storedHash, null);
                    hashResults.put(dgNumber, hashResult);
                    return hashResult;
                }

            }

        } catch (Exception e) {
            VerificationStatus.HashMatchResult hashResult = new VerificationStatus.HashMatchResult(storedHash, null);
            hashResults.put(dgNumber, hashResult);
            verificationStatus.setHT(VerificationStatus.Verdict.FAILED, "DG" + dgNumber + " failed due to exception", hashResults);
            return hashResult;
        }
        /* Compute the hash and compare. */
        try {
            byte[] computedHash = digest.digest(dgBytes);
            VerificationStatus.HashMatchResult hashResult = new VerificationStatus.HashMatchResult(storedHash, computedHash);
            hashResults.put(dgNumber, hashResult);
            if (!Arrays.equals(storedHash, computedHash)) {
                verificationStatus.setHT(VerificationStatus.Verdict.FAILED, "Hash mismatch", hashResults);
            }
            return hashResult;
        } catch (Exception ioe) {
            VerificationStatus.HashMatchResult hashResult = new VerificationStatus.HashMatchResult(storedHash, null);
            hashResults.put(dgNumber, hashResult);
            verificationStatus.setHT(VerificationStatus.Verdict.FAILED, "Hash failed due to exception", hashResults);
            return hashResult;
        }
    }

    private MessageDigest getDigest(String digestAlgorithm) throws NoSuchAlgorithmException {
        if (digest != null) {
            digest.reset();
            return digest;
        }
        Log.i(TAG, "Using hash algorithm " + digestAlgorithm);
        if (Security.getAlgorithms("MessageDigest").contains(digestAlgorithm)) {
            digest = MessageDigest.getInstance(digestAlgorithm);
        } else {
            digest = MessageDigest.getInstance(digestAlgorithm, BC_PROVIDER);
        }
        return digest;
    }

    private List<EACCAResult> doEACCA(PassportService ps, MrzInfo mrzInfo, DG14File dg14File, SODFile sodFile) {
        if (dg14File == null) {
            throw new NullPointerException("dg14File is null");
        }
        if (sodFile == null) {
            throw new NullPointerException("sodFile is null");
        }
        List<EACCAResult> eaccaResults = new ArrayList<>();
        ChipAuthenticationInfo chipAuthenticationInfo = null;
        List<ChipAuthenticationPublicKeyInfo> chipAuthenticationPublicKeyInfos = new ArrayList<>();
        Collection<SecurityInfo> securityInfos = dg14File.getSecurityInfos();
        Iterator securityInfoIterator = securityInfos.iterator();
        while (securityInfoIterator.hasNext()) {
            SecurityInfo securityInfo = (SecurityInfo) securityInfoIterator.next();
            if (securityInfo instanceof ChipAuthenticationInfo) {
                chipAuthenticationInfo = (ChipAuthenticationInfo) securityInfo;
            } else if (securityInfo instanceof ChipAuthenticationPublicKeyInfo) {
                chipAuthenticationPublicKeyInfos.add((ChipAuthenticationPublicKeyInfo) securityInfo);
            }
        }
        Iterator publicKeyInfoIterator = chipAuthenticationPublicKeyInfos.iterator();
        while (publicKeyInfoIterator.hasNext()) {
            ChipAuthenticationPublicKeyInfo authenticationPublicKeyInfo = (ChipAuthenticationPublicKeyInfo) publicKeyInfoIterator.next();
            try {
                Log.i("EMRTD", "Chip Authentication starting");
                EACCAResult doEACCA = ps.doEACCA(chipAuthenticationInfo.getKeyId(), chipAuthenticationInfo.getObjectIdentifier(),
                        chipAuthenticationInfo.getProtocolOIDString(), authenticationPublicKeyInfo.getSubjectPublicKey());
                eaccaResults.add(doEACCA);
                Log.i("EMRTD", "Chip Authentication succeeded");
            } catch (CardServiceException cse) {
                cse.printStackTrace();
                /* NOTE: Failed? Too bad, try next public key. */
            }
        }
        return eaccaResults;
    }

    private List<EACTAResult> doEACTA(PassportService ps, MrzInfo mrzInfo, CVCAFile cvcaFile,
                                      PACEResult paceResult, EACCAResult eaccaResult,
                                      List<KeyStore> cvcaKeyStores) throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        if (cvcaFile == null) {
            throw new NullPointerException("CVCAFile is null");
        }
        if (eaccaResult == null) {
            throw new NullPointerException("EACCAResult is null");
        }
        List<EACTAResult> eactaResults = new ArrayList<>();
        CVCPrincipal[] possibleCVCAReferences = new CVCPrincipal[2];
        possibleCVCAReferences[0] = cvcaFile.getCAReference();
        possibleCVCAReferences[1] = cvcaFile.getAltCAReference();
        for (CVCPrincipal caReference : possibleCVCAReferences) {
            EACCredentials eacCredentials = PassportNfcUtils.getEACCredentials(caReference, cvcaKeyStores);
            if (eacCredentials == null) {
                continue;
            }
            PrivateKey privateKey = eacCredentials.getPrivateKey();
            Certificate[] chain = eacCredentials.getChain();
            List<CardVerifiableCertificate> terminalCerts = new ArrayList<>(chain.length);
            for (Certificate c : chain) {
                terminalCerts.add((CardVerifiableCertificate) c);
            }
            try {
                if (paceResult == null) {
                    EACTAResult eactaResult = ps.doEACTA(caReference, terminalCerts, privateKey, null, eaccaResult, mrzInfo.getDocumentNumber());
                    eactaResults.add(eactaResult);
                } else {
                    EACTAResult eactaResult = ps.doEACTA(caReference, terminalCerts, privateKey, null, eaccaResult, paceResult);
                    eactaResults.add(eactaResult);
                }
            } catch (CardServiceException cse) {
                cse.printStackTrace();
                /* NOTE: Failed? Too bad, try next public key. */
                continue;
            }
            break;
        }
        return eactaResults;
    }

    private AbstractTaggedLDSFile getDG(int dg) {
        switch (dg) {
            case PassportService.EF_DG1:
                return dg1File;
            case PassportService.EF_DG2:
                return dg2File;
            case PassportService.EF_DG3:
                return dg3File;
            case PassportService.EF_DG5:
                return dg5File;
            case PassportService.EF_DG7:
                return dg7File;
            case PassportService.EF_DG11:
                return dg11File;
            case PassportService.EF_DG12:
                return dg12File;
            case PassportService.EF_DG14:
                return dg14File;
            case PassportService.EF_DG15:
                return dg15File;
            default:
                return null;
        }
    }

    public VerificationStatus verifySecurity() {
        /* Verify whether the Document Signing Certificate is signed by a Trust Anchor in our CSCA store. */
        verifyCS();
        /* Verify whether hashes in EF.SOd signed with document signer certificate. */
        verifyDS();
        /* Verify hashes. */
        verifyHT();
        if (service != null && dg15File != null) {
            verifyAA();
        }
        return verificationStatus;
    }

    private void verifyCS() {
        try {
            List<Certificate> chain = new ArrayList<>();
            if (sodFile == null) {
                verificationStatus.setCS(VerificationStatus.Verdict.FAILED, "Unable to build certificate chain", chain);
                return;
            }
            /* Get doc signing certificate and issuer info. */
            X509Certificate docSigningCertificate = null;
            X500Principal sodIssuer = null;
            BigInteger sodSerialNumber = null;
            try {
                sodIssuer = sodFile.getIssuerX500Principal();
                sodSerialNumber = sodFile.getSerialNumber();
                docSigningCertificate = sodFile.getDocSigningCertificate();
            } catch (Exception e) {
                Log.w(TAG, "Error getting document signing certificate: " + e.getMessage());
            }
            if (docSigningCertificate != null) {
                chain.add(docSigningCertificate);
            } else {
                Log.w(TAG, "Error getting document signing certificate from EF.SOd");
            }
            /* Get trust anchors. */
            List<CertStore> cscaStores = trustManager.getCscaStores();
            if (cscaStores == null || cscaStores.size() <= 0) {
                Log.w(TAG, "No CSCA certificate stores found.");
                verificationStatus.setCS(VerificationStatus.Verdict.FAILED, "No CSCA certificate stores found", chain);
            }
            Set<TrustAnchor> cscaTrustAnchors = trustManager.getCscaAnchors();
            if (cscaTrustAnchors == null || cscaTrustAnchors.size() <= 0) {
                Log.w(TAG, "No CSCA trust anchors found.");
                verificationStatus.setCS(VerificationStatus.Verdict.FAILED, "No CSCA trust anchors found", chain);
            }
            /* Optional internal EF.SOd consistency check. */
            if (docSigningCertificate != null) {
                X500Principal docIssuer = docSigningCertificate.getIssuerX500Principal();
                if (sodIssuer != null && sodIssuer != docIssuer) {
                    Log.e(TAG, "Security object issuer principal is different from embedded DS certificate issuer!");
                }
                BigInteger docSerialNumber = docSigningCertificate.getSerialNumber();
                if (sodSerialNumber != null && sodSerialNumber != docSerialNumber) {
                    Log.w(TAG, "Security object serial number is different from embedded DS certificate serial number!");
                }
            }
            /* Run PKIX algorithm to build chain to any trust anchor. Add certificates to our chain. */
            List<Certificate> pkixChain = PassportNfcUtils.getCertificateChain(docSigningCertificate, sodIssuer, sodSerialNumber, cscaStores, cscaTrustAnchors);
            if (pkixChain == null) {
                verificationStatus.setCS(VerificationStatus.Verdict.FAILED, "Could not build chain to trust anchor (pkixChain == null)", chain);
                return;
            }
            for (Certificate certificate : pkixChain) {
                if (certificate == docSigningCertificate) {
                    continue;
                }/* Ignore DS certificate, which is already in chain. */
                chain.add(certificate);
            }
            int chainDepth = chain.size();
            if (chainDepth <= 1) {
                verificationStatus.setCS(VerificationStatus.Verdict.FAILED, "Could not build chain to trust anchor", chain);
                return;
            }
            if (chainDepth > 1 && verificationStatus.getCs().equals(VerificationStatus.Verdict.UNKNOWN)) {
                verificationStatus.setCS(VerificationStatus.Verdict.SUCCEEDED, "Found a chain to a trust anchor", chain);
            }
        } catch (Exception e) {
            e.printStackTrace();
            verificationStatus.setCS(VerificationStatus.Verdict.FAILED, "Signature failed", EMPTY_CERTIFICATE_CHAIN);
        }
    }

    /**
     * Checks the security object's signature.
     * <p>
     */
    private void verifyDS() {
        try {
            verificationStatus.setDS(VerificationStatus.Verdict.UNKNOWN, "Unknown");
            /* Check document signing signature. */
            X509Certificate docSigningCert = sodFile.getDocSigningCertificate();
            if (docSigningCert == null) {
                Log.w(TAG, "Could not get document signer certificate from EF.SOd");
            }
            if (checkDocSignature(docSigningCert)) {
                verificationStatus.setDS(VerificationStatus.Verdict.SUCCEEDED, "Signature checked");

            } else {
                verificationStatus.setDS(VerificationStatus.Verdict.FAILED, "Signature incorrect");
            }
        } catch (NoSuchAlgorithmException nsae) {
            verificationStatus.setDS(VerificationStatus.Verdict.FAILED, "Unsupported signature algorithm");
            return;  /* NOTE: Serious enough to not perform other checks, leave method. */
        } catch (Exception e) {
            e.printStackTrace();
            verificationStatus.setDS(VerificationStatus.Verdict.FAILED, "Unexpected exception");
            return;  /* NOTE: Serious enough to not perform other checks, leave method. */
        }
    }

    private boolean checkDocSignature(Certificate docSigningCert) throws SignatureException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        byte[] eContent = sodFile.getEContent();
        byte[] signature = sodFile.getEncryptedDigest();
        String digestEncryptionAlgorithm = null;
        try {
            digestEncryptionAlgorithm = sodFile.getDigestEncryptionAlgorithm();
        } catch (Exception e) {
            digestEncryptionAlgorithm = null;
        }
        /*
         * For the cases where the signature is simply a digest (haven't seen a passport like this,
         * thus this is guessing)
         */
        if (digestEncryptionAlgorithm == null) {
            String digestAlg = sodFile.getDigestAlgorithm();
            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance(digestAlg);
            } catch (Exception e) {
                digest = MessageDigest.getInstance(digestAlg, BC_PROVIDER);
            }
            digest.update(eContent);
            byte[] digestBytes = digest.digest();
            return Arrays.equals(digestBytes, signature);
        }
        /* For RSA_SA_PSS
         *    1. the default hash is SHA1,
         *    2. The hash id is not encoded in OID
         * So it has to be specified "manually".
         */
        if ("SSAwithRSA/PSS".equals(digestEncryptionAlgorithm) == true) {
            String digestAlg = sodFile.getSignerInfoDigestAlgorithm();
            digestEncryptionAlgorithm = digestAlg.replace("-", "") + "withRSA/PSS";
        }
        if ("RSA".equals(digestEncryptionAlgorithm) == true) {
            String digestJavaString = sodFile.getSignerInfoDigestAlgorithm();
            digestEncryptionAlgorithm = digestJavaString.replace("-", "") + "withRSA";
        }
        Log.i(TAG, "digestEncryptionAlgorithm = $digestEncryptionAlgorithm");
        Signature sig = null;
        sig = Signature.getInstance(digestEncryptionAlgorithm, BC_PROVIDER);
        if (digestEncryptionAlgorithm.endsWith("withRSA/PSS")) {
            int saltLength = findSaltRSA_PSS(digestEncryptionAlgorithm, docSigningCert, eContent, signature);//Unknown salt so we try multiples until we get a success or failure
            MGF1ParameterSpec mgf1ParameterSpec = new MGF1ParameterSpec("SHA-256");
            PSSParameterSpec pssParameterSpec = new PSSParameterSpec("SHA-256", "MGF1", mgf1ParameterSpec, saltLength, 1);
            sig.setParameter(pssParameterSpec);
        }
        sig.initVerify(docSigningCert);
        sig.update(eContent);
        return sig.verify(signature);
    }

    private int findSaltRSA_PSS(String digestEncryptionAlgorithm, Certificate docSigningCert, byte[] eContent, byte[] signature) {
        for (int i = 0; i <= 512; i++) {
            try {
                Signature sig = null;
                sig = Signature.getInstance(digestEncryptionAlgorithm, BC_PROVIDER);
                if (digestEncryptionAlgorithm.endsWith("withRSA/PSS")) {
                    MGF1ParameterSpec mgf1ParameterSpec = new MGF1ParameterSpec("SHA-256");
                    PSSParameterSpec pssParameterSpec = new PSSParameterSpec("SHA-256", "MGF1", mgf1ParameterSpec, i, 1);
                    sig.setParameter(pssParameterSpec);
                }
                sig.initVerify(docSigningCert);
                sig.update(eContent);
                boolean verify = sig.verify(signature);
                if (verify) {
                    return i;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * Checks hashes in the SOd correspond to hashes we compute.
     */
    Map<Integer, VerificationStatus.HashMatchResult> hashMapResults = new TreeMap<>();

    private void verifyHT() {
        /* Compare stored hashes to computed hashes. */
        Map<Integer, VerificationStatus.HashMatchResult> hashResults = verificationStatus.getHashResults();
        if (hashResults == null) {
            verificationStatus.setHT(VerificationStatus.Verdict.FAILED, "No SOD", hashResults);
            return;
        }
        Map<Integer, byte[]> storedHashes = sodFile.getDataGroupHashes();
        for (int dgNumber : storedHashes.keySet()) {
            VerificationStatus.HashMatchResult hashMatchResult = verifyHash(dgNumber, hashResults);//print hashStore and hash result
            if (hashMatchResult != null) {
                hashMapResults.put(dgNumber, hashMatchResult);
            }
        }
        if (verificationStatus.getHt().equals(VerificationStatus.Verdict.UNKNOWN) == true) {
            verificationStatus.setHT(VerificationStatus.Verdict.SUCCEEDED, "All hashes match", hashResults);
        } else {
            /* Update storedHashes and computedHashes. */
            verificationStatus.setHT(verificationStatus.getHt(), verificationStatus.getHtReason(), hashResults);
        }
    }

    /**
     * Check active authentication.
     */
    private void verifyAA() {
        if (dg15File == null && service == null) {
            verificationStatus.setAA(VerificationStatus.Verdict.FAILED, "AA failed");
            return;
        }
        try {
            PublicKey pubKey = dg15File.getPublicKey();
            String pubKeyAlgorithm = pubKey.getAlgorithm();
            String digestAlgorithm = "SHA1";
            String signatureAlgorithm = "SHA1WithRSA/ISO9796-2";
            if ("EC".equals(pubKeyAlgorithm) == true || "ECDSA".equals(pubKeyAlgorithm) == true) {
                List<ActiveAuthenticationInfo> activeAuthenticationInfoList = new ArrayList<>();
                Collection<SecurityInfo> securityInfos = dg14File.getSecurityInfos();
                for (SecurityInfo securityInfo : securityInfos) {
                    if (securityInfo instanceof ActiveAuthenticationInfo) {
                        activeAuthenticationInfoList.add((ActiveAuthenticationInfo) securityInfo);
                    }
                }
                int activeAuthenticationInfoCount = activeAuthenticationInfoList.size();
                if (activeAuthenticationInfoCount < 1) {
                    verificationStatus.setAA(VerificationStatus.Verdict.FAILED, "Found no active authentication info in EF.DG14");
                    return;
                } else if (activeAuthenticationInfoCount > 1) {
                    Log.w(TAG, "Found " + activeAuthenticationInfoCount + " in EF.DG14, expected 1.");
                }
                ActiveAuthenticationInfo activeAuthenticationInfo = activeAuthenticationInfoList.get(0);
                String signatureAlgorithmOID = activeAuthenticationInfo.getSignatureAlgorithmOID();
                signatureAlgorithm = ActiveAuthenticationInfo.lookupMnemonicByOID(signatureAlgorithmOID);
                digestAlgorithm = Util.inferDigestAlgorithmFromSignatureAlgorithm(signatureAlgorithm);
            }
            int challengeLength = 8;
            byte[] challenge = new byte[challengeLength];
            random.nextBytes(challenge);
            AAResult aaResult = service.doAA(dg15File.getPublicKey(), sodFile.getDigestAlgorithm(), sodFile.getSignerInfoDigestAlgorithm(), challenge);
            verificationStatus.setAA(VerificationStatus.Verdict.SUCCEEDED, "AA succeeded");
            if (verifyAA(pubKey, digestAlgorithm, signatureAlgorithm, challenge, aaResult.getResponse())) {
                verificationStatus.setAA(VerificationStatus.Verdict.SUCCEEDED, "AA succeeded");
            } else {
                verificationStatus.setAA(VerificationStatus.Verdict.FAILED, "AA failed due to signature failure");
            }
        } catch (CardServiceException cse) {
            cse.printStackTrace();
            verificationStatus.setAA(VerificationStatus.Verdict.FAILED, "AA failed due to exception");
        } catch (Exception e) {
            Log.e(TAG, "DEBUG: this exception wasn't caught in verification logic (< 0.4.8) -- MO 3. Type is " + e.getClass().getCanonicalName());
            e.printStackTrace();
            verificationStatus.setAA(VerificationStatus.Verdict.FAILED, "AA failed due to exception");
        }
    }

    private boolean verifyAA(PublicKey publicKey, String digestAlgorithm, String signatureAlgorithm, byte[] challenge, byte[] response) throws CardServiceException {
        try {
            String pubKeyAlgorithm = publicKey.getAlgorithm();
            if ("RSA".equals(pubKeyAlgorithm) == true) {
                if (digestAlgorithm == null) {
                    Log.w(TAG, "Unexpected algorithms for RSA AA: digest algorithm = null" + ", signature algorithm = " + signatureAlgorithm);
                } else if (signatureAlgorithm == null) {
                    Log.w(TAG, "Unexpected algorithms for RSA AA: digest algorithm = " + digestAlgorithm + ", signature algorithm = null");
                } else if (digestAlgorithm == null && signatureAlgorithm == null) {
                    Log.w(TAG, "Unexpected algorithms for RSA AA: digest algorithm = null, signature algorithm = null");
                } else {
                    Log.w(TAG, "Unexpected algorithms for RSA AA: " + "digest algorithm = " + digestAlgorithm + ", signature algorithm = " + signatureAlgorithm);
                }
                rsaAADigest = MessageDigest.getInstance(digestAlgorithm);
                rsaAASignature = Signature.getInstance(signatureAlgorithm, BC_PROVIDER);
                RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
                rsaAACipher.init(Cipher.DECRYPT_MODE, rsaPublicKey);
                rsaAASignature.initVerify(rsaPublicKey);
                int digestLength = rsaAADigest.getDigestLength();
                if (digestLength != 20) {
                    throw new AssertionError();
                }
                byte[] plaintext = rsaAACipher.doFinal(response);
                byte[] m1 = Util.recoverMessage(digestLength, plaintext);
                rsaAASignature.update(m1);
                rsaAASignature.update(challenge);
                return rsaAASignature.verify(response);
            } else if ("EC".equals(pubKeyAlgorithm) == true || "ECDSA".equals(pubKeyAlgorithm) == true) {
                ECPublicKey ecdsaPublicKey = (ECPublicKey) publicKey;
                if (ecdsaAASignature == null || signatureAlgorithm != null && signatureAlgorithm != ecdsaAASignature.getAlgorithm()) {
                    Log.w(TAG, "Re-initializing ecdsaAASignature with signature algorithm " + signatureAlgorithm);
                    ecdsaAASignature = Signature.getInstance(signatureAlgorithm);
                }
                if (ecdsaAADigest == null || digestAlgorithm != null && digestAlgorithm != ecdsaAADigest.getAlgorithm()) {
                    Log.w(TAG, "Re-initializing ecdsaAADigest with digest algorithm " + digestAlgorithm);
                    ecdsaAADigest = MessageDigest.getInstance(digestAlgorithm);
                }
                ecdsaAASignature.initVerify(ecdsaPublicKey);
                if (response.length % 2 != 0) {
                    Log.w(TAG, "Active Authentication response is not of even length");
                }
                int l = response.length / 2;
                BigInteger r = Util.os2i(response, 0, l);
                BigInteger s = Util.os2i(response, l, l);
                ecdsaAASignature.update(challenge);
                try {
                    ASN1Encodable[] asn1Encodables = new ASN1Encodable[2];
                    asn1Encodables[0] = new ASN1Integer(r);
                    asn1Encodables[1] = new ASN1Integer(s);
                    DERSequence asn1Sequence = new DERSequence(asn1Encodables);
                    return ecdsaAASignature.verify(asn1Sequence.getEncoded());
                } catch (IOException ioe) {
                    Log.e(TAG, "Unexpected exception during AA signature verification with ECDSA");
                    ioe.printStackTrace();
                    return false;
                }
            } else {
                Log.e(TAG, "Unsupported AA public key type " + publicKey.getClass().getSimpleName());
                return false;
            }
        } catch (IllegalArgumentException iae) {
            // iae.printStackTrace();
            throw new CardServiceException(iae.toString());
        } catch (GeneralSecurityException iae) {
            throw new CardServiceException(iae.toString());
        }
    }

    public FeatureStatus getFeatures() {
        return features;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public COMFile getComFile() {
        return comFile;
    }

    public SODFile getSodFile() {
        return sodFile;
    }

    public DG1File getDg1File() {
        return dg1File;
    }

    public DG2File getDg2File() {
        return dg2File;
    }

    public DG3File getDg3File() {
        return dg3File;
    }

    public DG5File getDg5File() {
        return dg5File;
    }

    public DG7File getDg7File() {
        return dg7File;
    }

    public DG11File getDg11File() {
        return dg11File;
    }

    public DG12File getDg12File() {
        return dg12File;
    }

    public DG13File getDg13File() {
        return dg13File;
    }

    public DG14File getDg14File() {
        return dg14File;
    }

    public DG15File getDg15File() {
        return dg15File;
    }

    public CVCAFile getCvcaFile() {
        return cvcaFile;
    }

    public int getCheckReadData() {
        return checkReadData;
    }

    public int getCheckPass() {
        return checkPass;
    }

    public Map<Integer, VerificationStatus.HashMatchResult> getHashMapResults() {
        return hashMapResults;
    }
}
