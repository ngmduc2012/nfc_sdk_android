package cist.cmc.nfc.sdk.core.exceptions;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

@Keep
public class PassKeyException extends Exception {
    private String message;

    public PassKeyException(String message) {
        this.message = message;
    }

    public PassKeyException(String message, String message1) {
        super(message);
        this.message = message1;
    }

    public PassKeyException(String message, Throwable cause, String message1) {
        super(message, cause);
        this.message = message1;
    }

    public PassKeyException(Throwable cause, String message) {
        super(cause);
        this.message = message;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public String getMessage() {
        return message;
    }
}
