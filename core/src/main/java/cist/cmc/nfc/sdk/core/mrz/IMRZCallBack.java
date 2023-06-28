package cist.cmc.nfc.sdk.core.mrz;

import androidx.annotation.Keep;

@Keep
public interface IMRZCallBack {
    @Keep
    public void onResult(String documentNumber, String birthDate, String expiryDate,String documentNumberFull);
}
