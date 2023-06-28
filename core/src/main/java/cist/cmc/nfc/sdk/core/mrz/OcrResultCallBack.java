package cist.cmc.nfc.sdk.core.mrz;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

@Keep
public interface OcrResultCallBack {
    @Keep
    void onSuccess(@NonNull MrzInfo info);
    @Keep
    void onFailure(@NonNull Exception error);
    @Keep
    void onComplete();
}
