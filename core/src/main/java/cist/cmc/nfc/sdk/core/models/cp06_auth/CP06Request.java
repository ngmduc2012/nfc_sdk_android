package cist.cmc.nfc.sdk.core.models.cp06_auth;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class CP06Request {
    @SerializedName("deviceIp")
    @Expose
    private String deviceIp;
    @SerializedName("idCard")
    @Expose
    private String idCard;
    @SerializedName("regPlaceAddress")
    @Expose
    private String regPlaceAddress;
    @SerializedName("userws")
    @Expose
    private String userWS;
    @SerializedName("password")
    private String password;
    @SerializedName("dsCert")
    private String dsCert;

    public CP06Request() {
    }

    public CP06Request(String deviceIp, String idCard, String regPlaceAddress, String userWS, String password, String dsCert) {
        this.deviceIp = deviceIp;
        this.idCard = idCard;
        this.regPlaceAddress = regPlaceAddress;
        this.userWS = userWS;
        this.password = password;
        this.dsCert = dsCert;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getRegPlaceAddress() {
        return regPlaceAddress;
    }

    public void setRegPlaceAddress(String regPlaceAddress) {
        this.regPlaceAddress = regPlaceAddress;
    }

    public String getUserWS() {
        return userWS;
    }

    public void setUserWS(String userWS) {
        this.userWS = userWS;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDsCert() {
        return dsCert;
    }

    public void setDsCert(String dsCert) {
        this.dsCert = dsCert;
    }


}
