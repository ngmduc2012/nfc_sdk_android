package cist.cmc.nfc.sdk.core.nfc.extend;

import androidx.annotation.Nullable;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.security.auth.x500.X500Principal;

/**
 * Provides lookup for certificates, keys, CRLs used in
 * document validation and access control for data groups.
 *
 * @author The levanhau (lvhau@cmc.com.vn)
 * @version $Revision: $
 */
public class MRTDTrustStore {

    @Nullable
    private Set<TrustAnchor> cscaAnchors;
    @Nullable
    private List<CertStore> cscaStores;
    @Nullable
    private List<KeyStore> cvcaStores;

    public MRTDTrustStore() {
        this.cscaAnchors = new HashSet<>();
        this.cvcaStores = new ArrayList<>();
        this.cscaStores = new ArrayList<>();
    }

    public void clear() {
        this.cscaAnchors = new HashSet<>();
        this.cscaStores = new ArrayList<>();
        this.cvcaStores = new ArrayList<>();
    }

    @Nullable
    public Set<TrustAnchor> getCscaAnchors() {
        return cscaAnchors;
    }

    @Nullable
    public List<CertStore> getCscaStores() {
        return cscaStores;
    }

    @Nullable
    public List<KeyStore> getCvcaStores() {
        return cvcaStores;
    }

    public void addCSCAAnchor(TrustAnchor trustAnchor) {
        if (trustAnchor != null) {
            cscaAnchors.add(trustAnchor);
        }
    }

    /**
     * Adds root certificates for document validation.
     *
     * @param trustAnchors a collection of trustAnchors
     */
    public void addCSCAAnchors(Collection<TrustAnchor> trustAnchors) {
        if (cscaAnchors != null) {
            cscaAnchors.addAll(trustAnchors);
        }
    }

    /**
     * Adds a certificate store for document validation.
     *
     * @param certStore
     */
    public void addCSCAStore(CertStore certStore) {
        if (certStore != null) {
            cscaStores.add(certStore);
        }
    }

    /**
     * Returns a set of trust anchors based on the X509 certificates in `certificates`.
     *
     * @param certificates a collection of X509 certificates
     * @return a set of trust anchors
     */
    private static Set<TrustAnchor> getAsAnchors(Collection<? extends Certificate> certificates) {
        Set<TrustAnchor> anchors = new HashSet<>();
        for (Certificate certificate : certificates) {
            if (certificate instanceof X509Certificate) {
                anchors.add(new TrustAnchor((X509Certificate) certificate, null));
            }
        }
        return anchors;
    }

    public void addAsCSCACertStore(CertStore certStore) throws CertStoreException {
        addCSCAStore(certStore);
        Collection<? extends Certificate> rootCerts = certStore.getCertificates(SELF_SIGNED_X509_CERT_SELECTOR);
        addCSCAAnchors(getAsAnchors(rootCerts));
    }


    static {
        Security.insertProviderAt(new BouncyCastleProvider(), 1);

    }

    private static CertificateSelector SELF_SIGNED_X509_CERT_SELECTOR = new CertificateSelector();


    private static final Provider JMRTD_PROVIDER = JMRTDSecurityProvider.getInstance();
    private static final Logger LOGGER = Logger.getLogger("org.jmrtd");

    private static class CertificateSelector extends X509CertSelector {

        @Override
        public boolean match(Certificate cert) {
            if (!(cert instanceof X509Certificate)) {
                return false;
            } else {
                X500Principal issuer = ((X509Certificate) cert).getIssuerX500Principal();
                X500Principal subject = ((X509Certificate) cert).getSubjectX500Principal();
                return issuer == null && subject == null || subject.equals(issuer);
            }
        }

        @Override
        public Object clone() {
            return this;
        }
    }


}
