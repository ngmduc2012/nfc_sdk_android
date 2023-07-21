package cist.cmc.nfc.sdk.core.exceptions;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

@Keep
public class CP06AuthException extends Exception {
    private String message;

    CP06AuthException(String message) {
        this.message = message;
    }

    @Nullable
    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
