package cist.cmc.nfc.sdk.demo.features.cp06_authendicate;

public interface CP06AuthListner {

    public void authSuccess(boolean isAuth,int message);

    public void authError(boolean isAuth,int message);
}
