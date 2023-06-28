package cist.cmc.nfc.sdk.core.models.cp06_auth;

import com.google.gson.annotations.SerializedName;

public class CP06Response {
    @SerializedName("Result")
    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
