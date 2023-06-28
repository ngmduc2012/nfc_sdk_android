package cist.cmc.nfc.sdk.core.input;

import androidx.annotation.Keep;

@Keep
public class CanNumberMask {
    private String cccdNumber;
    private String canNumber;

    public CanNumberMask(String cccdNumber) {
        this.cccdNumber = cccdNumber;
        this.canNumber = getCANNumber();
    }

    private String getCANNumber() {
        if (cccdNumber == null || cccdNumber == "" || (cccdNumber.length() != 12)) {
            return "";
        }
        return cccdNumber.substring(6);
    }

    public String getCanNumber() {
        return canNumber;
    }
}
