package cist.cmc.nfc.sdk.core.utils;

import android.util.Log;

//import org.spongycastle.jce.provider.BouncyCastleProvider;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

import cist.cmc.nfc.sdk.core.chip.cert.CVCPrincipal;
import cist.cmc.nfc.sdk.core.chip.cert.CardVerifiableCertificate;

public class PassportNfcUtils {
    private static final String TAG = PassportNfcUtils.class.getSimpleName();

    private static boolean IS_PKIX_REVOCATION_CHECING_ENABLED = false;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static EACCredentials getEACCredentials(CVCPrincipal caReference, List<KeyStore> cvcaStores) throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        for (KeyStore cvcaStore : cvcaStores) {
            EACCredentials eacCredentials = getEACCredentials(caReference, cvcaStore);
            if (eacCredentials != null) {
                return eacCredentials;
            }
        }
        return null;
    }

    private static EACCredentials getEACCredentials(CVCPrincipal caReference, KeyStore cvcaStore) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException {
        if (caReference == null) {
            throw new IllegalArgumentException("CA reference cannot be null");
        }
        PrivateKey privateKey = null;
        Certificate[] chain = null;
        List<String> aliases = Collections.list(cvcaStore.aliases());
        for (String alias : aliases) {
            if (cvcaStore.isKeyEntry(alias)) {
                Key key = cvcaStore.getKey(alias, "".toCharArray());
                if (key instanceof PrivateKey) {
                    privateKey = (PrivateKey) key;
                } else {
                    Log.w(TAG, "skipping non-private key $alias");
                    continue;
                }
                chain = cvcaStore.getCertificateChain(alias);
                return new EACCredentials(privateKey, chain);
            } else if (cvcaStore.isCertificateEntry(alias)) {
                CardVerifiableCertificate certificate = (CardVerifiableCertificate) cvcaStore.getCertificate(alias);
                CVCPrincipal authRef = certificate.getAuthorityReference();
                CVCPrincipal holderRef = certificate.getHolderReference();
                if (caReference != authRef) {
                    continue;
                }
                /* See if we have a private key for that certificate. */
                privateKey = (PrivateKey) cvcaStore.getKey(holderRef.getName(), "".toCharArray());
                chain = cvcaStore.getCertificateChain(holderRef.getName());
                if (privateKey == null) {
                    continue;
                }
                Log.i(TAG, "found a key, privateKey = $privateKey");
                return new EACCredentials(privateKey, chain);
            }
            if (privateKey == null || chain == null) {
                Log.e(TAG, "null chain or key for entry " + alias + ": chain = " + Arrays.toString(chain) + ", privateKey = " + privateKey);
                continue;
            }
        }
        return null;
    }

    public static List<Certificate> getCertificateChain(X509Certificate docSigningCertificate, X500Principal sodIssuer,
                                                        BigInteger sodSerialNumber, List<CertStore> cscaStores, Set<TrustAnchor> cscaTrustAnchors) {
        List<Certificate> chain = new ArrayList<>();
        X509CertSelector selector = new X509CertSelector();
        try {
            if (docSigningCertificate != null) {
                selector.setCertificate(docSigningCertificate);

            } else {
                selector.setIssuer(sodIssuer);
                selector.setSerialNumber(sodSerialNumber);
            }
            CollectionCertStoreParameters docStoreParams = new CollectionCertStoreParameters(Collections.singleton(docSigningCertificate));
            CertStore docStore = CertStore.getInstance("Collection", docStoreParams);
            CertPathBuilder builder = CertPathBuilder.getInstance("PKIX", "SC");//Spungy castle
            PKIXBuilderParameters buildParams = new PKIXBuilderParameters(cscaTrustAnchors, selector);
            buildParams.addCertStore(docStore);
            for (CertStore trustStore : cscaStores) {
                buildParams.addCertStore(trustStore);
            }
            buildParams.setRevocationEnabled(IS_PKIX_REVOCATION_CHECING_ENABLED);/* NOTE: set to false for checking disabled. */
            PKIXCertPathBuilderResult result = null;
            try {
                result = (PKIXCertPathBuilderResult) builder.build(buildParams);
            } catch (CertPathBuilderException cpbe) {
                /* NOTE: ignore, result remain null */
            }
            if (result != null) {
                CertPath pkixCertPath = result.getCertPath();
                if (pkixCertPath != null) {
                    chain.addAll(pkixCertPath.getCertificates());
                }
            }
            if (docSigningCertificate != null && !chain.contains(docSigningCertificate)) {
                /* NOTE: if doc signing certificate not in list, we add it ourselves. */
                Log.w(TAG, "Adding doc signing certificate after PKIXBuilder finished");
                chain.add(0, docSigningCertificate);
            }
            if (result != null) {
                X509Certificate trustAnchorCertificate = result.getTrustAnchor().getTrustedCert();
                if (trustAnchorCertificate != null && !chain.contains(trustAnchorCertificate)) {
                    /* NOTE: if trust anchor not in list, we add it ourselves. */
                    Log.w(TAG, "Adding trust anchor certificate after PKIXBuilder finished");
                    chain.add(trustAnchorCertificate);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Building a chain failed (" + e.getMessage() + ").");
        }
        return chain;
    }
}
