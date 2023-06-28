package cist.cmc.nfc.sdk.demo.features.mrz_scanner;

import android.annotation.SuppressLint;
import android.media.Image;
import android.nfc.Tag;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import org.jetbrains.annotations.NotNull;

import cist.cmc.nfc.sdk.core.mrz.IMRZCallBack;
import cist.cmc.nfc.sdk.core.mrz.MrzInfo;
import cist.cmc.nfc.sdk.core.mrz.OcrMrzService;
import cist.cmc.nfc.sdk.core.mrz.OcrResultCallBack;

public class MrzAnalyzer implements ImageAnalysis.Analyzer {
    private String TAG = MrzAnalyzer.class.getSimpleName();
    private final IMRZCallBack listener;

    public MrzAnalyzer(IMRZCallBack listener) {
        this.listener = listener;
    }

    public OcrMrzService ocrMrzService = new OcrMrzService();

    @Override
    public void analyze(@NonNull @NotNull ImageProxy image) {
        @SuppressLint("UnsafeOptInUsageError") Image mediaImage = image.getImage();

        ocrMrzService.progressFromMediaImage(mediaImage,
                image.getImageInfo().getRotationDegrees(),
                new OcrResultCallBack() {
                    @Override
                    public void onSuccess(@NonNull @NotNull MrzInfo info) {
                        listener.onResult(info.getDocumentNumber(), info.getBirthDate(), info.getExpiryDate(), info.getDocumentNumberFull());
                        Log.d(TAG, "Document number full: " + info.getDocumentNumberFull());
                    }

                    @Override
                    public void onFailure(@NonNull @NotNull Exception error) {
                        Log.e(TAG, error.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        image.close();
                        Log.i(TAG, "Image analyzer complete");
                    }
                }
        );
    }
}
