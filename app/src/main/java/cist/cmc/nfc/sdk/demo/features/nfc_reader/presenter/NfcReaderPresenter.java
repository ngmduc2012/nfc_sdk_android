package cist.cmc.nfc.sdk.demo.features.nfc_reader.presenter;

import cist.cmc.nfc.sdk.core.nfc.IdCardReaderStatus;

public interface NfcReaderPresenter {
    void onDestroyView();
    void onUpdateView(IdCardReaderStatus status);
}
