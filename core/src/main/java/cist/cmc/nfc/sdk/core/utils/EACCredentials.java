package cist.cmc.nfc.sdk.core.utils;

import java.security.PrivateKey;
import java.security.cert.Certificate;

public class EACCredentials {
    private PrivateKey privateKey;
    private Certificate[] chain;

    public EACCredentials(PrivateKey privateKey, Certificate[] chain) {
        this.privateKey = privateKey;
        this.chain = chain;
    }

    public final PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public final Certificate[] getChain() {
        return this.chain;
    }
}
