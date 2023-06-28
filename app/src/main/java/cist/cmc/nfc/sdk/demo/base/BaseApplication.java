package cist.cmc.nfc.sdk.demo.base;

import android.app.Application;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;


public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }
}
