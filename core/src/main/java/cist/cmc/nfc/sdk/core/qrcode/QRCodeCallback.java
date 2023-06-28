package cist.cmc.nfc.sdk.core.qrcode;

import androidx.annotation.Keep;

import cist.cmc.nfc.sdk.core.mrz.MrzInfo;
@Keep
public interface QRCodeCallback {
    @Keep
    public void onResult(QRService qrService);
}
