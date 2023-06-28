package cist.cmc.nfc.sdk.core.utils;

//import org.spongycastle.jce.provider.BouncyCastleProvider;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class KeyStoreUtils {
    public static CertStore toCertStore(KeyStore keyStore) throws KeyStoreException, InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        String type = "Collection";
        return CertStore.getInstance(type, new CollectionCertStoreParameters(toList(keyStore)));
    }

    public static List<Certificate> toList(KeyStore keyStore) throws KeyStoreException {
        Enumeration<String> aliases = keyStore.aliases();
        List<Certificate> list = new ArrayList<>();
        while (aliases.hasMoreElements()) {
            Certificate certificate = keyStore.getCertificate(aliases.nextElement());
            list.add(certificate);
        }
        return list;
    }


    static {
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }
}
