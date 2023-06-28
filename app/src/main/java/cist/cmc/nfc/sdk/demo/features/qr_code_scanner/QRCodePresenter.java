package cist.cmc.nfc.sdk.demo.features.qr_code_scanner;

import cist.cmc.nfc.sdk.core.qrcode.QRCodeCallback;
import cist.cmc.nfc.sdk.core.qrcode.QRService;

public class QRCodePresenter {
    private QRCodeCallback callback;

    public QRCodePresenter() {
    }

    public QRCodePresenter(QRCodeCallback callback) {
        this.callback = callback;
    }

    public void scanQrCode(QRService qrService) {
        callback.onResult(qrService);
    }
}
