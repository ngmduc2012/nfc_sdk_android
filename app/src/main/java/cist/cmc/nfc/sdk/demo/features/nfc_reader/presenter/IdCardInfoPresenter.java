package cist.cmc.nfc.sdk.demo.features.nfc_reader.presenter;

import cist.cmc.nfc.sdk.core.models.EDocument;

public interface IdCardInfoPresenter {
    void onDestroyView();

    void onUpdateView(EDocument model);
}
