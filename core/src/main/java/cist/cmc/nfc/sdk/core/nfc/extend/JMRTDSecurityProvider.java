package cist.cmc.nfc.sdk.core.nfc.extend;

import androidx.annotation.Nullable;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Security provider for JMRTD specific implementations.
 * Main motivation is to make JMRTD less dependent on the BouncyCastle provider.
 * Provides:
 * <p>
 * * [java.security.cert.CertificateFactory] &quot;CVC&quot;
 * (a factory for [org.jmrtd.cert.CardVerifiableCertificate] instances)
 * <p>
 * * [java.security.cert.CertStore] &quot;PKD&quot;
 * (LDAP based `CertStore`,
 * where the directory contains CSCA and document signer certificates)
 * <p>
 * * [java.security.cert.CertStore] &quot;JKS&quot;
 * (`KeyStore` based `CertStore`,
 * where the JKS formatted `KeyStore` contains CSCA certificates)
 * <p>
 * * [java.security.cert.CertStore] &quot;PKCS12&quot;
 * (`KeyStore` based `CertStore`,
 * where the PKCS#12 formatted `KeyStore` contains CSCA certificates)
 *
 * @author The levanhau (lvhau@cmc.com.vn)
 * @version $Revision: $
 */
public class JMRTDSecurityProvider extends Provider {
    public static final long serialVersionUID = -2881416441551680704L;
    public static final Logger LOGGER = Logger.getLogger("org.jmrtd");
    //    public static final String SUN_PROVIDER_CLASS_NAME = "sun.security.provider.Sun";
//    public static final String BC_PROVIDER_CLASS_NAME = "org.bouncycastle.jce.provider.BouncyCastleProvider";
//    public static final String SC_PROVIDER_CLASS_NAME = "org.spongycastle.jce.provider.BouncyCastleProvider";
    public static final BouncyCastleProvider BC_PROVIDER = new BouncyCastleProvider();
    // public static final org.spongycastle.jce.provider.BouncyCastleProvider SC_PROVIDER = new org.spongycastle.jce.provider.BouncyCastleProvider();
    public static final BouncyCastleProvider SC_PROVIDER = new BouncyCastleProvider();
    public static Provider instance = new JMRTDSecurityProvider();

    public JMRTDSecurityProvider() {
        super("JMRTD", 0.1D, "JMRTD Security Provider");
        this.put("CertificateFactory.CVC", "org.jmrtd.cert.CVCertificateFactorySpi");
        this.put("CertStore.PKD", "org.jmrtd.cert.PKDCertStoreSpi");
        this.put("CertStore.JKS", "org.jmrtd.cert.KeyStoreCertStoreSpi");
        this.put("CertStore.BKS", "org.jmrtd.cert.KeyStoreCertStoreSpi");
        this.put("CertStore.PKCS12", "org.jmrtd.cert.KeyStoreCertStoreSpi");
        if (BC_PROVIDER != null) {
            Provider provider = getBouncyCastleProvider();
            /* But these work fine. */
            if (provider != null) {
                replicateFromProvider("CertificateFactory", "X.509", provider);
                replicateFromProvider("CertStore", "Collection", provider);
                //			replicateFromProvider("KeyStore", "JKS", SUN_PROVIDER);
                replicateFromProvider("MessageDigest", "SHA1", provider);
                replicateFromProvider("Signature", "SHA1withRSA/ISO9796-2", provider);
                replicateFromProvider("Signature", "MD2withRSA", provider);
                replicateFromProvider("Signature", "MD4withRSA", provider);
                replicateFromProvider("Signature", "MD5withRSA", provider);
                replicateFromProvider("Signature", "SHA1withRSA", provider);
                replicateFromProvider("Signature", "SHA1withRSA/ISO9796-2", provider);
                replicateFromProvider("Signature", "SHA256withRSA", provider);
                replicateFromProvider("Signature", "SHA256withRSA/ISO9796-2", provider);
                replicateFromProvider("Signature", "SHA384withRSA", provider);
                replicateFromProvider("Signature", "SHA384withRSA/ISO9796-2", provider);
                replicateFromProvider("Signature", "SHA512withRSA", provider);
                replicateFromProvider("Signature", "SHA512withRSA/ISO9796-2", provider);
                replicateFromProvider("Signature", "SHA224withRSA", provider);
                replicateFromProvider("Signature", "SHA224withRSA/ISO9796-2", provider);
                replicateFromProvider("Signature", "SHA256withRSA/PSS", getBouncyCastleProvider());
            }

            /* Testing 0.4.7 -- MO */
            //			replicateFromProvider("KeyStore", "UBER", getBouncyCastleProvider());
            //			replicateFromProvider("KeyPairGenerator", "ECDHC", getBouncyCastleProvider());
            //			replicateFromProvider("KeyPairGenerator", "ECDSA", getBouncyCastleProvider());
            //			replicateFromProvider("X509StreamParser", "CERTIFICATE", getBouncyCastleProvider());

            put("Alg.Alias.Mac.ISO9797Alg3Mac", "ISO9797ALG3MAC");
            put("Alg.Alias.CertificateFactory.X509", "X.509");
        }
    }


    /**
     * Constructs a provider with the specified name, version number,
     * and information.
     *
     * @param name    the provider name.
     * @param version the provider version number.
     * @param info    a description of the provider and its services.
     */
    protected JMRTDSecurityProvider(String name, double version, String info) {
        super(name, version, info);
    }

    private void replicateFromProvider(String serviceName, String algorithmName, Provider provider) {
        String name = serviceName + '.' + algorithmName;
        Object service = provider.get(name);
        if (service != null) {
            this.put(name, service);
        }
    }

    public static Provider getInstance() {
        return instance;
    }

    static {
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    /**
     * Temporarily puts the BC provider on number one in the list of
     * providers, until caller calls [.endPreferBouncyCastleProvider].
     *
     * @return the index of BC, if it was present, in the list of providers
     * @see .endPreferBouncyCastleProvider
     */
    public static int beginPreferBouncyCastleProvider() {
        Provider bcProvider = getBouncyCastleProvider();
        if (bcProvider != null) {
            return -1;
        }
        Provider[] providers = Security.getProviders();
        for (int i = 0; i < providers.length; i++) {
            Provider provider = providers[i];
            if (bcProvider.getClass().getCanonicalName().equals(provider.getClass().getCanonicalName())) {
                Security.removeProvider(provider.getName());
                Security.insertProviderAt(bcProvider, 1);
                return i + 1;
            }
        }
        return -1;

    }

    /**
     * Removes the BC provider from the number one position and puts it back
     * at its original position, after a call to [.beginPreferBouncyCastleProvider].
     *
     * @param i the original index of the BC provider
     * @see .beginPreferBouncyCastleProvider
     */
    public static void endPreferBouncyCastleProvider(int i) {
        Provider bcProvider = getBouncyCastleProvider();
        if (bcProvider != null) {
            Security.removeProvider(bcProvider.getName());
        }
        if (i > 0) {
            Security.insertProviderAt(bcProvider, i);
        }
    }

    /**
     * Gets the BC provider, if present.
     *
     * @return the BC provider, the SC provider, or `null`
     */
    @Nullable
    public static Provider getBouncyCastleProvider() {
        if (BC_PROVIDER != null) {
            return BC_PROVIDER;
        } else if (SC_PROVIDER != null) {
            return SC_PROVIDER;
        } else {
            LOGGER.severe("No Bouncy or Spongy provider");
            return null;
        }

    }

    /**
     * Gets the SC provider, if present.
     *
     * @return the SC provider, the BC provider, or `null`
     */
    @Nullable
    public static Provider getSpongyCastleProvider() {
        if (SC_PROVIDER != null) {
            return SC_PROVIDER;
        } else if (BC_PROVIDER != null) {
            return BC_PROVIDER;
        } else {
            LOGGER.severe("No Bouncy or Spongy provider");
            return null;
        }

    }

    @Nullable
    private static Provider getProvider(String serviceName, String algorithmName) {
        List<Provider> providers = getProviders(serviceName, algorithmName);
        if (providers != null && providers.size() > 0) {
            return providers.get(0);
        } else {
            return null;
        }
    }

    @Nullable
    private static List<Provider> getProviders(String serviceName, String algorithmName) {
        if (Security.getAlgorithms(serviceName).contains(algorithmName)) {
            Provider[] providers = Security.getProviders(serviceName + '.' + algorithmName);
            return new ArrayList<>(Arrays.asList(Arrays.copyOf(providers, providers.length)));
        } else if (BC_PROVIDER != null && BC_PROVIDER.getService(serviceName, algorithmName) != null) {
            return new ArrayList<>(Arrays.asList(BC_PROVIDER));
        } else if (SC_PROVIDER != null && SC_PROVIDER.getService(serviceName, algorithmName) != null) {
            return new ArrayList<>(Arrays.asList(SC_PROVIDER));
        } else if (instance != null && instance.getService(serviceName, algorithmName) != null) {
            return new ArrayList<>(Arrays.asList(instance));
        } else {
            return null;
        }
    }
}
