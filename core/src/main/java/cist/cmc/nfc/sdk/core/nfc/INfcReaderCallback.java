package cist.cmc.nfc.sdk.core.nfc;

import androidx.annotation.Keep;

import cist.cmc.nfc.sdk.core.models.EDocument;

@Keep
public interface INfcReaderCallback {
    void onStatusChanged(IdCardReaderStatus status);

    void onSuccess(EDocument document);
}
