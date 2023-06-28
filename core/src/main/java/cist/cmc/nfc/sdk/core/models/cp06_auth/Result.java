package cist.cmc.nfc.sdk.core.models.cp06_auth;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class Result {
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("msgid")
    @Expose
    private String msGid;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsGid() {
        return msGid;
    }

    public void setMsGid(String msGid) {
        this.msGid = msGid;
    }
}


