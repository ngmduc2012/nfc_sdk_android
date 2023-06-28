
package cist.cmc.nfc.sdk.core;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.os.Build;

import androidx.annotation.Keep;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import cist.cmc.nfc.sdk.core.mrz.MrzInfo;
import cist.cmc.nfc.sdk.core.nfc.INfcReaderCallback;
import cist.cmc.nfc.sdk.core.nfc.IdCardReaderStatus;
import cist.cmc.nfc.sdk.core.nfc.ReadNfcTag;
import cist.cmc.nfc.sdk.core.nfc.extend.MRTDTrustStore;
import cist.cmc.nfc.sdk.core.utils.KeyStoreUtils;

@Keep
public class IdCardReader {
    public String TAG = IdCardReader.class.getSimpleName();
    private NfcAdapter adapter;

    public boolean isNfcAvailable(Activity activity) {
        if (adapter == null) {
            NfcManager manager = (NfcManager) activity.getSystemService(Context.NFC_SERVICE);
            adapter = manager.getDefaultAdapter();
        }
        return adapter != null;
    }

    public boolean isNfcEnabled(Activity activity) {
        if (!isNfcAvailable(activity)) {
            return false;
        }
        return adapter.isEnabled();
    }


    public void enableReaderMode(Activity activity) {
        isNfcAvailable(activity);
        Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(activity,
                    0, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(activity,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        String[][] filter = new String[][]{new String[]{"android.nfc.tech.IsoDep"}};
        adapter.enableForegroundDispatch(activity, pendingIntent, null, filter);
    }


    public void disableReaderMode(Activity activity) {
        if (adapter != null) {
            adapter.disableForegroundDispatch(activity);
        }
    }

    public void readFromIdCard(Context context, INfcReaderCallback listener, Intent intent,
                               String documentNumber, String birthDate, String expirationDate, String canNumber) {
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            MRTDTrustStore mrtdTrustStore = null;
            try {
                mrtdTrustStore = setMRTDTrustStore(context);
            } catch (Exception e) {
                e.printStackTrace();
                listener.onStatusChanged(IdCardReaderStatus.Error5010);
                return;
            }
            if (Arrays.asList(tag.getTechList()).contains("android.nfc.tech.IsoDep")) {
                if (canNumber != null) {
                    if (canNumber == null || canNumber.isEmpty()) {
                        listener.onStatusChanged(IdCardReaderStatus.Error5007);
                        return;
                    }
                    new ReadNfcTag(tag, canNumber, mrtdTrustStore, listener).execute();
                }
                if (canNumber == null || canNumber.isEmpty()) {
                    if (documentNumber == null || documentNumber.isEmpty()) {
                        listener.onStatusChanged(IdCardReaderStatus.Error5003);
                        return;
                    }
                    if (expirationDate == null || expirationDate.isEmpty()) {
                        listener.onStatusChanged(IdCardReaderStatus.Error5003);
                        return;
                    }
                    if (birthDate == null || birthDate.isEmpty()) {
                        listener.onStatusChanged(IdCardReaderStatus.Error5003);
                        return;
                    }
                    MrzInfo mrzInfo = new MrzInfo(documentNumber, birthDate, expirationDate);
                    new ReadNfcTag(tag, mrzInfo, mrtdTrustStore, listener).execute();
                }
            }
        }
    }

    private MRTDTrustStore setMRTDTrustStore(Context context) throws Exception {
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open("csca.crt");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(inputStream);
            keyStore.setCertificateEntry("csca", cert);
            MRTDTrustStore mrtdTrustStore = new MRTDTrustStore();
            if (keyStore != null) {
                CertStore certStore = KeyStoreUtils.toCertStore(keyStore);
                mrtdTrustStore.addAsCSCACertStore(certStore);
            }
            return mrtdTrustStore;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
