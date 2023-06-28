package cist.cmc.nfc.sdk.core.nfc.extend;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import net.sf.scuba.util.Hex;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cist.cmc.nfc.sdk.core.chip.BACKey;
import cist.cmc.nfc.sdk.core.chip.protocol.EACCAResult;
import cist.cmc.nfc.sdk.core.chip.protocol.EACTAResult;

@Keep
public class VerificationStatus implements Serializable {
    /* Verdict for this verification feature. */

    @Nullable
    private Verdict aa;
    @Nullable
    private Verdict bac;
    @Nullable
    private Verdict sac;
    @Nullable
    private Verdict cs;
    @Nullable
    private Verdict ht;
    @Nullable
    private Verdict ds;
    @Nullable
    private Verdict eac;
    @Nullable
    private Verdict ca;
    @Nullable
    private String aaReason;
    @Nullable
    private String bacReason;
    @Nullable
    private String sacReason;
    @Nullable
    private String csReason;
    @Nullable
    private String htReason;
    @Nullable
    private String dsReason;
    @Nullable
    private String eacReason;
    @Nullable
    private String caReason;
    /* By products of the verification process that may be useful for relying parties to display. */
    private List<BACKey> triedBACEntries;/* As a result of BAC testing, this contains all tried BAC entries. */
    @Nullable
    private Map<Integer, HashMatchResult> hashResults;/* As a result of HT testing, this contains stored and computed hashes. */
    private List<Certificate> certificateChain; /* As a result of CS testing, this contains certificate chain from DSC to CSCA. */
    @Nullable
    private EACTAResult eacResult;
    @Nullable
    private EACCAResult caResult;


    @Nullable
    public final Verdict getAa() {
        return this.aa;
    }

    @Nullable
    public final Verdict getBac() {
        return this.bac;
    }

    @Nullable
    public final Verdict getSac() {
        return this.sac;
    }

    @Nullable
    public final Verdict getCs() {
        return this.cs;
    }

    @Nullable
    public final Verdict getHt() {
        return this.ht;
    }

    @Nullable
    public final Verdict getDs() {
        return this.ds;
    }

    @Nullable
    public final Verdict getEac() {
        return this.eac;
    }

    @Nullable
    public final Verdict getCa() {
        return this.ca;
    }

    @Nullable
    public final String getAaReason() {
        return this.aaReason;
    }

    @Nullable
    public final String getBacReason() {
        return this.bacReason;
    }

    @Nullable
    public final String getSacReason() {
        return this.sacReason;
    }

    @Nullable
    public final String getCsReason() {
        return this.csReason;
    }

    @Nullable
    public final String getHtReason() {
        return this.htReason;
    }

    @Nullable
    public final String getDsReason() {
        return this.dsReason;
    }

    @Nullable
    public final String getEacReason() {
        return this.eacReason;
    }

    @Nullable
    public final String getCaReason() {
        return this.caReason;
    }

    @Nullable
    public final Map getHashResults() {
        return this.hashResults;
    }

    public final void setHashResults(@Nullable Map var1) {
        this.hashResults = var1;
    }

    @Nullable
    public final EACTAResult getEacResult() {
        return this.eacResult;
    }

    @Nullable
    public final EACCAResult getCaResult() {
        return this.caResult;
    }

    public final void setAA(Verdict v, @Nullable String reason) {
        this.aa = v;
        this.aaReason = reason;
    }

    @Nullable
    public final List getTriedBACEntries() {
        return this.triedBACEntries;
    }

    public final void setBAC(Verdict v, @Nullable String reason, @Nullable List triedBACEntries) {
        this.bac = v;
        this.bacReason = reason;
        this.triedBACEntries = triedBACEntries;
    }

    public final void setSAC(Verdict v, String reason) {
        this.sac = v;
        this.sacReason = reason;
    }

    @Nullable
    public final List getCertificateChain() {
        return this.certificateChain;
    }

    public final void setCS(Verdict v, @Nullable String reason, @Nullable List certificateChain) {
        this.cs = v;
        this.csReason = reason;
        this.certificateChain = certificateChain;
    }

    public final void setDS(Verdict v, @Nullable String reason) {
        this.ds = v;
        this.dsReason = reason;
    }

    public final void setHT(Verdict v, @Nullable String reason, @Nullable Map hashResults) {
        this.ht = v;
        this.htReason = reason;
        this.hashResults = hashResults;
    }

    public final void setEAC(Verdict v, @Nullable String reason, @Nullable EACTAResult eacResult) {
        this.eac = v;
        this.eacReason = reason;
        this.eacResult = eacResult;
    }

    public final void setCA(Verdict v, String reason, @Nullable EACCAResult eaccaResult) {
        this.ca = v;
        this.caReason = reason;
        this.caResult = eaccaResult;
    }

    public final void setAll(Verdict verdict, @Nullable String reason) {
        this.setAA(verdict, reason);
        this.setBAC(verdict, reason, (List) null);
        this.setCS(verdict, reason, (List) null);
        this.setDS(verdict, reason);
        this.setHT(verdict, reason, (Map) null);
        this.setEAC(verdict, reason, (EACTAResult) null);
    }


    public VerificationStatus() {
        this.setAll(Verdict.UNKNOWN, (String) null);
    }

    @Keep
    public enum Verdict {
        UNKNOWN, /* Unknown */
        NOT_PRESENT, /* Not present */
        NOT_CHECKED, /* Present, not checked */
        FAILED, /* Present, checked, and not ok */
        SUCCEEDED
        /* Present, checked, and ok */
    }

    /**
     * The result of matching the stored and computed hashes of a single datagroup.
     * <p>
     * FIXME: perhaps that boolean should be more like verdict, including a reason for mismatch if known (e.g. access denied for EAC datagroup) -- MO
     */
    @Keep
    public static class HashMatchResult implements Serializable {
        @Nullable
        private byte[] storedHash;
        @Nullable
        private byte[] computedHash;
        private static final long serialVersionUID = 263961258911936111L;

        @Nullable
        public final byte[] getStoredHash() {
            return this.storedHash;
        }

        @Nullable
        public final byte[] getComputedHash() {
            return this.computedHash;
        }

        public final boolean isMatch() {
            return Arrays.equals(this.storedHash, this.computedHash);
        }

        public HashMatchResult(byte[] storedHash) {
            this.storedHash = storedHash;
        }

        /**
         * Use null for computed hash if access was denied.
         *
         * @param storedHash
         * @param computedHash
         */
        public HashMatchResult(byte[] storedHash, byte[] computedHash) {
            this.storedHash = storedHash;
            this.computedHash = computedHash;
        }

        @Override
        public String toString() {
            return "HashResult [" + this.isMatch() + ", stored: " + Hex.bytesToHexString(this.storedHash) + ", computed: " + Hex.bytesToHexString(this.computedHash);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (o == this) {
                return true;
            }
            if (o.getClass() != this.getClass()) {
                return false;
            }
            HashMatchResult otherResult = (HashMatchResult) o;
            return Arrays.equals(otherResult.computedHash, computedHash) && Arrays.equals(otherResult.storedHash, storedHash);
        }

        @Override
        public int hashCode() {
            return 11 + 3 * Arrays.hashCode(storedHash) + 5 * Arrays.hashCode(computedHash);
        }

        private final void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
            this.storedHash = this.readBytes(inputStream);
            this.computedHash = this.readBytes(inputStream);
        }

        private final void writeObject(ObjectOutputStream outputStream) throws IOException {
            this.writeByteArray(this.storedHash, outputStream);
            this.writeByteArray(this.computedHash, outputStream);
        }

        private byte[] readBytes(ObjectInputStream inputStream) throws IOException {
            int length = inputStream.readInt();
            if (length < 0) {
                return null;
            }
            byte[] bytes = new byte[length];
            for (int i = 0; i < length; i++) {
                int b = inputStream.readInt();
                bytes[i] = (byte) b;
            }
            return bytes;
        }

        private void writeByteArray(byte[] bytes, ObjectOutputStream outputStream) throws IOException {
            if (bytes == null) {
                outputStream.writeInt(-1);
            } else {
                outputStream.writeInt(bytes.length);
                for (byte b : bytes) {
                    outputStream.writeInt(b);
                }
            }
        }
    }
}
