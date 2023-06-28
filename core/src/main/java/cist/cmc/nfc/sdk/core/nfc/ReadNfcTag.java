package cist.cmc.nfc.sdk.core.nfc;

import android.graphics.Bitmap;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.Keep;
import androidx.annotation.RequiresApi;

import net.sf.scuba.smartcards.CardService;
import net.sf.scuba.smartcards.CardServiceException;

import org.bouncycastle.util.encoders.Base64;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cist.cmc.nfc.sdk.core.chip.AccessDeniedException;
import cist.cmc.nfc.sdk.core.chip.BACDeniedException;
import cist.cmc.nfc.sdk.core.chip.PACEException;
import cist.cmc.nfc.sdk.core.chip.PassportService;
import cist.cmc.nfc.sdk.core.chip.lds.SODFile;
import cist.cmc.nfc.sdk.core.chip.lds.icao.MRZInfo;
import cist.cmc.nfc.sdk.core.chip.lds.iso19794.FaceImageInfo;
import cist.cmc.nfc.sdk.core.chip.lds.iso19794.FaceInfo;
import cist.cmc.nfc.sdk.core.exceptions.LicenseExpiredException;
import cist.cmc.nfc.sdk.core.exceptions.PassKeyException;
import cist.cmc.nfc.sdk.core.models.DocType;
import cist.cmc.nfc.sdk.core.models.EDocument;
import cist.cmc.nfc.sdk.core.models.PersonDetails;
import cist.cmc.nfc.sdk.core.models.SercurityFile;
import cist.cmc.nfc.sdk.core.mrz.MrzInfo;
import cist.cmc.nfc.sdk.core.nfc.extend.FeatureStatus;
import cist.cmc.nfc.sdk.core.nfc.extend.MRTDTrustStore;
import cist.cmc.nfc.sdk.core.nfc.extend.PassportNFC;
import cist.cmc.nfc.sdk.core.nfc.extend.VerificationStatus;
import cist.cmc.nfc.sdk.core.utils.CertificateUtils;
import cist.cmc.nfc.sdk.core.utils.DateUtils;
import cist.cmc.nfc.sdk.core.utils.ImageUtils;

public class ReadNfcTag extends AsyncTask<Void, IdCardReaderStatus, Exception> {

    private final Tag tag;
    private MrzInfo mrzInfo = null;
    private String canNumber = null;
    private MRTDTrustStore mrtdTrustStore;

    private final INfcReaderCallback listener;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    EDocument eDocument = new EDocument();
    DocType docType = DocType.OTHER;
    PersonDetails personDetails = new PersonDetails();

    public ReadNfcTag(Tag tag, MrzInfo mrzInfo, MRTDTrustStore mrtdTrustStore, INfcReaderCallback listener) {
        this.tag = tag;
        this.mrzInfo = mrzInfo;
        this.mrtdTrustStore = mrtdTrustStore;
        this.listener = listener;
    }

    public ReadNfcTag(Tag tag, String canNumber, MRTDTrustStore mrtdTrustStore, INfcReaderCallback listener) {
        this.tag = tag;
        this.canNumber = canNumber;
        this.mrtdTrustStore = mrtdTrustStore;
        this.listener = listener;
    }

    //private boolean licenseExpiry = false;
    private int checkReadData = 0;
    private int checkPass = 0;

    @Override
    protected void onProgressUpdate(IdCardReaderStatus... values) {
        super.onProgressUpdate(values);
        listener.onStatusChanged(values[0]);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected Exception doInBackground(Void... voids) {
        PassportService ps;
        try {
            publishProgress(IdCardReaderStatus.AccessingNfcChip);
            IsoDep nfc = IsoDep.get(tag);
            nfc.setTimeout(5 * 1000);
            CardService cs = CardService.getInstance(nfc);
            ps = new PassportService(cs, PassportService.NORMAL_MAX_TRANCEIVE_LENGTH, PassportService.DEFAULT_MAX_BLOCKSIZE, false, true);
            ps.open();
            PassportNFC passportNFC = null;
            if (mrzInfo != null) {
                passportNFC = new PassportNFC(ps, mrtdTrustStore, mrzInfo, null);
                checkReadData = passportNFC.getCheckReadData();
                checkPass = passportNFC.getCheckPass();
                if (checkPass == 2) {
                    throw new PassKeyException("PASS ERROR");
                }
                if (checkReadData == 2) {
                    throw new PassKeyException("READ DATA ERROR");
                }
            }
            if (mrzInfo == null) {
                passportNFC = new PassportNFC(ps, mrtdTrustStore, null, canNumber);
                checkReadData = passportNFC.getCheckReadData();
                checkPass = passportNFC.getCheckPass();
                if (checkPass == 2) {
                    throw new PassKeyException("PASS ERROR");
                }
                if (checkReadData == 2) {
                    throw new PassKeyException("READ DATA ERROR");
                }

            }
            publishProgress(IdCardReaderStatus.Veryfication);
            VerificationStatus verificationStatus = passportNFC.verifySecurity();

            publishProgress(IdCardReaderStatus.CheckFeature);
            FeatureStatus featureStatus = passportNFC.getFeatures();

            // -- Personal Details -- //
            publishProgress(IdCardReaderStatus.ReadingPersonalData);
            readDG1(passportNFC);

            // -- Face Image -- //
            publishProgress(IdCardReaderStatus.ReadingPhoto);
            readDG2(passportNFC);

            //-- Read dg13 --
            publishProgress(IdCardReaderStatus.ReadingOptionData);
            readDG13(passportNFC);
            //
            publishProgress(IdCardReaderStatus.ReadingDsCert);
            SODFile sodFile = passportNFC.getSodFile();
            X509Certificate docSigningCertificate = sodFile.getDocSigningCertificate();
            String dsCert = CertificateUtils.encodePemStringToBase64WithStandardCharsetsASCII(docSigningCertificate);

            SercurityFile sercurityFile = new SercurityFile(dsCert, docSigningCertificate.getSerialNumber().toString(), docSigningCertificate.getPublicKey().getAlgorithm(),
                    docSigningCertificate.getSigAlgName(), docSigningCertificate.getEncoded(), docSigningCertificate.getIssuerDN().getName(), docSigningCertificate.getSubjectDN().getName(),
                    simpleDateFormat.format(docSigningCertificate.getNotBefore()), simpleDateFormat.format(docSigningCertificate.getNotAfter()), passportNFC.getHashMapResults());

//            final String date = "30/09/2021";
//            if (System.currentTimeMillis() > DateUtils.getUnixTime(date)) {
//                licenseExpiry = true;
//                throw new LicenseExpiredException();
//            }
            eDocument = new EDocument(passportNFC.getDg13File().getOptionPersonal(), personDetails, sercurityFile, featureStatus, verificationStatus);

        } catch (CardServiceException e) {
            if (e.getSW() == 25344) {
                // Invalid MRZ key
                publishProgress(IdCardReaderStatus.Error5001);
            }
            if (e.getSW() == -1) {
                // ConnectionError
                publishProgress(IdCardReaderStatus.Error5002);
            }
            if (e instanceof AccessDeniedException || e instanceof BACDeniedException) {
                publishProgress(IdCardReaderStatus.Error5011);
            }
            if (e instanceof PACEException) {
                publishProgress(IdCardReaderStatus.Error5012);
            }
            if (e instanceof CardServiceException) {
                publishProgress(IdCardReaderStatus.Error5013);
            }
            return e;
        } catch (IOException e) {
            // ConnectionError
            publishProgress(IdCardReaderStatus.Error5002);
            return e;
        } catch (LicenseExpiredException e) {
            publishProgress(IdCardReaderStatus.Error5005);
            return e;
        } catch (Exception e) {
            if (e.getMessage().equals("READ DATA ERROR") == true) {
                publishProgress(IdCardReaderStatus.Error5009);
            } else if (e.getMessage().equals("PASS ERROR") == true) {
                //user's input key error
                publishProgress(IdCardReaderStatus.Error5014);
            } else {
                publishProgress(IdCardReaderStatus.Error5006);
            }
            return e;
        }

        return null;
    }

    private void readDG2(PassportNFC passportNFC) {
        if (passportNFC.getDg2File() != null) {
            //Get the picture
            try {
                List<FaceInfo> faceInfos = passportNFC.getDg2File().getFaceInfos();
                List<FaceImageInfo> allFaceImageInfos = new ArrayList<>();
                for (FaceInfo faceInfo : faceInfos) {
                    allFaceImageInfos.addAll(faceInfo.getFaceImageInfos());
                }
                if (!allFaceImageInfos.isEmpty()) {
                    FaceImageInfo faceImageInfo = allFaceImageInfos.iterator().next();
                    Bitmap image = ImageUtils.getBitmap(faceImageInfo);
                    personDetails.setFaceImage(image);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void readDG1(PassportNFC passportNFC) {
        if (passportNFC.getDg1File() != null) {
            MRZInfo mrzInfo = passportNFC.getDg1File().getMRZInfo();
            personDetails.setName(mrzInfo.getSecondaryIdentifier().replace("<", " ").trim());
            personDetails.setSurname(mrzInfo.getPrimaryIdentifier().replace("<", " ").trim());
            personDetails.setPersonalNumber(mrzInfo.getPersonalNumber());
            personDetails.setGender(mrzInfo.getGender().toString());
            personDetails.setBirthDate(DateUtils.convertFromMrzDate(mrzInfo.getDateOfBirth()));
            personDetails.setExpiryDate(DateUtils.convertFromMrzDate(mrzInfo.getDateOfExpiry()));
            personDetails.setSerialNumber(mrzInfo.getDocumentNumber());
            personDetails.setNationality(mrzInfo.getNationality());
            personDetails.setIssuerAuthority(mrzInfo.getIssuingState());
            personDetails.setMrzInfo(mrzInfo.toString().replace("\n", ""));
            if ("I".equals(mrzInfo.getDocumentCode())) {
                docType = DocType.ID_CARD;
            } else if ("P".equals(mrzInfo.getDocumentCode())) {
                docType = DocType.PASSPORT;
            }
        }
    }

    private void readDG13(PassportNFC passportNFC) throws Exception {
        if (passportNFC.getDg13File() != null) {
            passportNFC.getDg13File().readPersonal();
        }
    }

    @Override
    protected void onPostExecute(Exception e) {
        super.onPostExecute(e);
        if (checkReadData == 0 && checkPass == 0) {//&& (licenseExpiry == false)
            listener.onSuccess(eDocument);
            listener.onStatusChanged(IdCardReaderStatus.Success);
        }
    }
}