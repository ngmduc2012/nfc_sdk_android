package cist.cmc.nfc.sdk.core.utils;

import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.spongycastle.jce.provider.PEMUtil;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class CertificateUtils {
    public static String x509CertificateToPemString(X509Certificate cert) throws IOException, CertificateEncodingException {
        StringWriter writer = new StringWriter();
        PemWriter pemWriter = new PemWriter(writer);
        pemWriter.writeObject(new PemObject("CERTIFICATE", cert.getEncoded()));
        pemWriter.flush();
        pemWriter.close();
        return writer.toString();

    }

    public static byte[] x509CertificateToPemByteArray(X509Certificate cert) throws IOException, CertificateEncodingException {
        StringWriter writer = new StringWriter();
        PemWriter pemWriter = new PemWriter(writer);
        pemWriter.writeObject(new PemObject("CERTIFICATE", cert.getEncoded()));
        pemWriter.flush();
        pemWriter.close();
        String pemString = writer.toString();
        return pemString.getBytes();
    }

    /**
     * Method require android 8 and above <=> Build.VERSION.SDK_INT>= android.os.Build.VERSION_CODES.O
     *
     * @param cert
     * @return
     * @throws IOException
     * @throws CertificateEncodingException
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encodePemStringToBase64WithStandardCharsetsASCII(X509Certificate cert) throws IOException, CertificateEncodingException {
        byte[] pemByteArray = x509CertificateToPemByteArray(cert);
        byte[] encoded = Base64.getEncoder().encode(pemByteArray);
        return new String(encoded, StandardCharsets.US_ASCII);
    }
}
