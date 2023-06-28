package cist.cmc.nfc.sdk.core.nfc.extend;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class FeatureStatus implements Serializable {
    private Verdict hasSAC;
    private Verdict hasBAC;
    private Verdict hasAA;
    private Verdict hasEAC;
    private Verdict hasCA;

    @Keep
    public enum Verdict {
        UNKNOWN, /* Presence unknown */
        PRESENT, /* Present */
        NOT_PRESENT
        /* Not present */
    }

    public FeatureStatus() {
        this.hasSAC = Verdict.UNKNOWN;
        this.hasBAC = Verdict.UNKNOWN;
        this.hasAA = Verdict.UNKNOWN;
        this.hasEAC = Verdict.UNKNOWN;
        this.hasCA = Verdict.UNKNOWN;
    }

    public Verdict getHasSAC() {
        return hasSAC;
    }

    public void setHasSAC(Verdict hasSAC) {
        this.hasSAC = hasSAC;
    }

    public Verdict getHasBAC() {
        return hasBAC;
    }

    public void setHasBAC(Verdict hasBAC) {
        this.hasBAC = hasBAC;
    }

    public Verdict getHasAA() {
        return hasAA;
    }

    public void setHasAA(Verdict hasAA) {
        this.hasAA = hasAA;
    }

    public Verdict getHasEAC() {
        return hasEAC;
    }

    public void setHasEAC(Verdict hasEAC) {
        this.hasEAC = hasEAC;
    }

    public Verdict getHasCA() {
        return hasCA;
    }

    public void setHasCA(Verdict hasCA) {
        this.hasCA = hasCA;
    }

}
