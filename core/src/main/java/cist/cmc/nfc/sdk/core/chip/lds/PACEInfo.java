/*
 * JMRTD - A Java API for accessing machine readable travel documents.
 *
 * Copyright (C) 2006 - 2018  The JMRTD team
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 * $Id: PACEInfo.java 1831 2019-12-03 15:31:22Z martijno $
 */

package cist.cmc.nfc.sdk.core.chip.lds;

import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECParameterSpec;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import javax.crypto.spec.DHParameterSpec;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.crypto.agreement.DHStandardGroups;
import org.bouncycastle.jce.ECNamedCurveTable;
import cist.cmc.nfc.sdk.core.chip.Util;

/**
 * PACE Info object as per SAC TR 1.01, November 11, 2010.
 *
 * @author The JMRTD team (info@jmrtd.org)
 *
 * @version $Revision: 1831 $
 *
 * @since 0.5.0
 */
public class PACEInfo extends SecurityInfo {

  private static final long serialVersionUID = 7960925013249578359L;

  /**
   * A DH parameter specification which also keeps track of
   * the prime order of the subgroup generated by the generator.
   *
   * @author The JMRTD team (info@jmrtd.org)
   *
   * @version $Revision: 1831 $
   */
  public static class DHCParameterSpec extends DHParameterSpec {

    private BigInteger q;

    /**
     * Creates a parameter specification.
     *
     * @param p the prime
     * @param g the generator
     * @param q the prime order of subgroup generated by {@code g}
     */
    public DHCParameterSpec(BigInteger p, BigInteger g, BigInteger q) {
      super(p, g);
      this.q = q;
    }

    /**
     * Returns the prime order of subgroup generated by {@code g}.
     *
     * @return the prime order of subgroup generated by {@code g}
     */
    public BigInteger getQ() {
      return q;
    }
  }

  /** Generic mapping and Integrated mapping and CAM mapping. */
  public enum MappingType {
    /** Generic Mapping. */
    GM,

    /** Integrated Mapping. */
    IM,

    /** Chip Authentication Mapping. */
    CAM
  }

  /** Standardized domain parameters. Based on Table 6. */
  public static final int PARAM_ID_GFP_1024_160 = 0;

  /** Standardized domain parameters. Based on Table 6. */
  public static final int PARAM_ID_GFP_2048_224 = 1;

  /** Standardized domain parameters. Based on Table 6. */
  public static final int PARAM_ID_GFP_2048_256 = 2;

  /* RFU 3 - 7 */

  /** Standardized domain parameters. Based on Table 6. */
  public static final int PARAM_ID_ECP_NIST_P192_R1 = 8;

  /** Standardized domain parameters. Based on Table 6. */
  public static final int PARAM_ID_ECP_BRAINPOOL_P192_R1 = 9;

  /** Standardized domain parameters. Based on Table 6. */
  public static final int PARAM_ID_ECP_NIST_P224_R1 = 10;

  /** Standardized domain parameters. Based on Table 6. */
  public static final int PARAM_ID_ECP_BRAINPOOL_P224_R1 = 11;

  /** Standardized domain parameters. Based on Table 6. */
  public static final int PARAM_ID_ECP_NIST_P256_R1 = 12;

  /** Standardized domain parameters. Based on Table 6. */
  public static final int  PARAM_ID_ECP_BRAINPOOL_P256_R1 = 13;

  /** Standardized domain parameters. Based on Table 6. */
  public static final int  PARAM_ID_ECP_BRAINPOOL_P320_R1 = 14;

  /** Standardized domain parameters. Based on Table 6. */
  public static final int   PARAM_ID_ECP_NIST_P384_R1 = 15;

  /** Standardized domain parameters. Based on Table 6. */
  public static final int   PARAM_ID_ECP_BRAINPOOL_P384_R1 = 16;

  /** Standardized domain parameters. Based on Table 6. */
  public static final int   PARAM_ID_ECP_BRAINPOOL_P512_R1 = 17;

  /** Standardized domain parameters. Based on Table 6. */  public static final int PARAM_ID_ECP_NIST_P521_R1 = 18;

  /* RFU 19-31 */

//  private static final DHParameterSpec PARAMS_GFP_1024_160 = Util.toExplicitDHParameterSpec(DHStandardGroups.rfc5114_1024_160);
//  private static final DHParameterSpec PARAMS_GFP_2048_224 = Util.toExplicitDHParameterSpec(DHStandardGroups.rfc5114_2048_224);
//  private static final DHParameterSpec PARAMS_GFP_2048_256 = Util.toExplicitDHParameterSpec(DHStandardGroups.rfc5114_2048_256);

  private static final DHParameterSpec PARAMS_GFP_1024_160 = Util.toExplicitDHParameterSpec(DHStandardGroups.rfc3526_1536);
  private static final DHParameterSpec PARAMS_GFP_2048_224 = Util.toExplicitDHParameterSpec(DHStandardGroups.rfc3526_2048);
  private static final DHParameterSpec PARAMS_GFP_2048_256 = Util.toExplicitDHParameterSpec(DHStandardGroups.rfc3526_3072);

  private static final ECParameterSpec PARAMS_ECP_NIST_P192_R1 = Util.toExplicitECParameterSpec(ECNamedCurveTable.getParameterSpec("secp192r1"));
  private static final ECParameterSpec PARAMS_ECP_NIST_P224_R1 = Util.toExplicitECParameterSpec(ECNamedCurveTable.getParameterSpec("secp224r1"));
  private static final ECParameterSpec PARAMS_ECP_NIST_P256_R1 = Util.toExplicitECParameterSpec(ECNamedCurveTable.getParameterSpec("secp256r1"));
  private static final ECParameterSpec PARAMS_ECP_NIST_P384_R1 = Util.toExplicitECParameterSpec(ECNamedCurveTable.getParameterSpec("secp384r1"));
  private static final ECParameterSpec PARAMS_ECP_NIST_P521_R1 = Util.toExplicitECParameterSpec(ECNamedCurveTable.getParameterSpec("secp521r1"));
  private static final ECParameterSpec PARAMS_ECP_BRAINPOOL_P192_R1 = Util.toExplicitECParameterSpec(ECNamedCurveTable.getParameterSpec("brainpoolp192r1"));
  private static final ECParameterSpec PARAMS_ECP_BRAINPOOL_P224_R1 = Util.toExplicitECParameterSpec(ECNamedCurveTable.getParameterSpec("brainpoolp224r1"));
  private static final ECParameterSpec PARAMS_ECP_BRAINPOOL_P256_R1 = Util.toExplicitECParameterSpec(ECNamedCurveTable.getParameterSpec("brainpoolp256r1"));
  private static final ECParameterSpec PARAMS_ECP_BRAINPOOL_P320_R1 = Util.toExplicitECParameterSpec(ECNamedCurveTable.getParameterSpec("brainpoolp320r1"));
  private static final ECParameterSpec PARAMS_ECP_BRAINPOOL_P384_R1 = Util.toExplicitECParameterSpec(ECNamedCurveTable.getParameterSpec("brainpoolp384r1"));
  private static final ECParameterSpec PARAMS_ECP_BRAINPOOL_P512_R1 = Util.toExplicitECParameterSpec(ECNamedCurveTable.getParameterSpec("brainpoolp512r1"));

  private static final Set<String> ALLOWED_REQUIRED_IDENTIFIERS = new TreeSet<String>(Arrays.asList(new String[] {
      ID_PACE_DH_GM_3DES_CBC_CBC,
      ID_PACE_DH_GM_AES_CBC_CMAC_128,
      ID_PACE_DH_GM_AES_CBC_CMAC_192,
      ID_PACE_DH_GM_AES_CBC_CMAC_256,
      ID_PACE_DH_IM_3DES_CBC_CBC,
      ID_PACE_DH_IM_AES_CBC_CMAC_128,
      ID_PACE_DH_IM_AES_CBC_CMAC_192,
      ID_PACE_DH_IM_AES_CBC_CMAC_256,
      ID_PACE_ECDH_GM_3DES_CBC_CBC,
      ID_PACE_ECDH_GM_AES_CBC_CMAC_128,
      ID_PACE_ECDH_GM_AES_CBC_CMAC_192,
      ID_PACE_ECDH_GM_AES_CBC_CMAC_256,
      ID_PACE_ECDH_IM_3DES_CBC_CBC,
      ID_PACE_ECDH_IM_AES_CBC_CMAC_128,
      ID_PACE_ECDH_IM_AES_CBC_CMAC_192,
      ID_PACE_ECDH_IM_AES_CBC_CMAC_256,
      ID_PACE_ECDH_CAM_AES_CBC_CMAC_128,
      ID_PACE_ECDH_CAM_AES_CBC_CMAC_192,
      ID_PACE_ECDH_CAM_AES_CBC_CMAC_256 }));

  private String protocolOID;
  private int version;
  private BigInteger parameterId;

  /**
   * Constructs a PACE info object.
   *
   * @param oid the object identifier, indicating what PACE variant
   *        is to be used (agreement protocol, mapping type, and secure channel properties)
   * @param version a version number, which should be 2
   * @param parameterId either a standardized domain parameter id from table 6 or a proprietary domain parameter
   */
  public PACEInfo(String oid, int version, int parameterId) {
    this(oid, version, BigInteger.valueOf(parameterId));
  }

  /**
   * Creates a PACE info object.
   *
   * @param oid the object identifier, indicating what PACE variant
   *        is to be used (agreement protocol, mapping type, and secure channel properties)
   * @param version a version number, which should be 2
   * @param parameterId either a standardized domain parameter id from table 6 or a proprietary domain parameter
   */
  public PACEInfo(String oid, int version, BigInteger parameterId) {
    if (!checkRequiredIdentifier(oid)) {
      throw new IllegalArgumentException("Invalid OID");
    }
    if (version != 2) {
      throw new IllegalArgumentException("Invalid version, must be 2");
    }
    this.protocolOID = oid;
    this.version = version;
    this.parameterId = parameterId;
  }

  /**
   * Creates a PACE info from an encoding.
   *
   * @param paceInfoBytes the encoded bytes
   *
   * @return a PACE info object
   */
  public static PACEInfo createPACEInfo(byte[] paceInfoBytes) {
    /*
     * FIXME: Should add a constructor to PACEInfo that takes byte[] or InputStream, or
     * align this with SecurityInfo.getInstance().
     */
    ASN1Sequence sequence = ASN1Sequence.getInstance(paceInfoBytes);
    String oid = ((ASN1ObjectIdentifier)sequence.getObjectAt(0)).getId();
    ASN1Primitive requiredData = sequence.getObjectAt(1).toASN1Primitive();
    ASN1Primitive optionalData = null;
    if (sequence.size() == 3) {
      optionalData = sequence.getObjectAt(2).toASN1Primitive();
    }

    int version = ((ASN1Integer)requiredData).getValue().intValue();
    BigInteger parameterId = null;
    if (optionalData != null) {
      parameterId = ((ASN1Integer)optionalData).getValue();
    }

    return new PACEInfo(oid, version, parameterId);
  }

  /**
   * Returns the PACE protocol object identifier.
   *
   * @return the PACE protocol object identifier
   */
  @Override
  public String getObjectIdentifier() {
    return protocolOID;
  }

  /**
   * Returns the protocol object identifier as a human readable string.
   *
   * @return a string describing the PACE protocol object identifier
   */
  @Override
  public String getProtocolOIDString() {
    return toProtocolOIDString(protocolOID);
  }

  /**
   * Returns the version.
   *
   * @return the version
   */
  public int getVersion() {
    return version;
  }

  /**
   * Returns the parameter identifier.
   *
   * @return the parameter identifier
   */
  public BigInteger getParameterId() {
    return parameterId;
  }

  /**
   * Returns a DER object with this SecurityInfo data (DER sequence).
   *
   * @return a DER object with this SecurityInfo data
   *
   * @deprecated this method will be removed from visible interface (because of dependency on BC API)
   */
  @Deprecated
  @Override
  public ASN1Primitive getDERObject() {
    ASN1EncodableVector vector = new ASN1EncodableVector();

    /* Protocol */
    vector.add(new ASN1ObjectIdentifier(protocolOID));

    /* Required data */
    vector.add(new ASN1Integer(version));

    /* Optional data */
    if (parameterId != null) {
      vector.add(new ASN1Integer(parameterId));
    }
    return new DLSequence(vector);
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("PACEInfo [");
    result.append("protocol: ").append(toProtocolOIDString(protocolOID));
    result.append(", version: ").append(version);
    if (parameterId != null) {
      result.append(", parameterId: ").append(toStandardizedParamIdString(parameterId));
    }
    result.append("]");
    return result.toString();
  }

  @Override
  public int hashCode() {
    return 1234567891
        + 7 * protocolOID.hashCode()
        + 5 * version
        + 3 * (parameterId == null ? 1991 : parameterId.hashCode());
  }

  @Override
  public boolean equals(Object other) {
    if (other == null) {
      return false;
    }
    if (other == this) {
      return true;
    }
    if (!PACEInfo.class.equals(other.getClass())) {
      return false;
    }

    PACEInfo otherPACEInfo = (PACEInfo)other;

    if (protocolOID == null && otherPACEInfo.protocolOID != null) {
      return false;
    }
    if (protocolOID != null && !protocolOID.equals(otherPACEInfo.protocolOID)) {
      return false;
    }

    if (version != otherPACEInfo.version) {
      return false;
    }

    if (parameterId == null && otherPACEInfo.parameterId != null) {
      return false;
    }
    if (parameterId != null && !parameterId.equals(otherPACEInfo.parameterId)) {
      return false;
    }

    return true;
  }

  /**
   * Checks whether the object identifier is valid for describing a PACE protocol.
   *
   * @param oid a PACE object identifier
   *
   * @return a boolean indicating whether the object identifier describes a known PACE protocol
   */
  public static boolean checkRequiredIdentifier(String oid) {
    return ALLOWED_REQUIRED_IDENTIFIERS.contains(oid);
  }

  /*
   * FIXME: perhaps we should introduce an enum for PACE identifiers (with a String toOID() method),
   * so that we can get rid of static methods below. -- MO
   */

  /**
   * Returns the mapping type for a given PACE protocol object identifier.
   *
   * @param oid a PACE protocol object identifier
   *
   * @return the mapping type
   */
  public static MappingType toMappingType(String oid) {
    if (ID_PACE_DH_GM_3DES_CBC_CBC.equals(oid)
        || ID_PACE_DH_GM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_DH_GM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_DH_GM_AES_CBC_CMAC_256.equals(oid)
        || ID_PACE_ECDH_GM_3DES_CBC_CBC.equals(oid)
        || ID_PACE_ECDH_GM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_ECDH_GM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_ECDH_GM_AES_CBC_CMAC_256.equals(oid)) {
      return MappingType.GM;
    } else if (ID_PACE_DH_IM_3DES_CBC_CBC.equals(oid)
        || ID_PACE_DH_IM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_DH_IM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_DH_IM_AES_CBC_CMAC_256.equals(oid)
        || ID_PACE_ECDH_IM_3DES_CBC_CBC.equals(oid)
        || ID_PACE_ECDH_IM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_ECDH_IM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_ECDH_IM_AES_CBC_CMAC_256.equals(oid)) {
      return MappingType.IM;
    } else if (ID_PACE_ECDH_CAM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_ECDH_CAM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_ECDH_CAM_AES_CBC_CMAC_256.equals(oid)) {
      return MappingType.CAM;
    }

    throw new NumberFormatException("Unknown OID: \"" + oid + "\"");
  }

  /**
   * Returns the key agreement algorithm ({@code "DH"} or {@code "ECDH"})
   * for a PACE protocol object identifier.
   *
   * @param oid a PACE protocol object identifier
   *
   * @return a key agreement algorithm as JCE mnemonic string
   */
  public static String toKeyAgreementAlgorithm(String oid) {
    if (ID_PACE_DH_GM_3DES_CBC_CBC.equals(oid)
        || ID_PACE_DH_GM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_DH_GM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_DH_GM_AES_CBC_CMAC_256.equals(oid)
        || ID_PACE_DH_IM_3DES_CBC_CBC.equals(oid)
        || ID_PACE_DH_IM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_DH_IM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_DH_IM_AES_CBC_CMAC_256.equals(oid)) {
      return "DH";
    } else if (ID_PACE_ECDH_GM_3DES_CBC_CBC.equals(oid)
        || ID_PACE_ECDH_GM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_ECDH_GM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_ECDH_GM_AES_CBC_CMAC_256.equals(oid)
        || ID_PACE_ECDH_IM_3DES_CBC_CBC.equals(oid)
        || ID_PACE_ECDH_IM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_ECDH_IM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_ECDH_IM_AES_CBC_CMAC_256.equals(oid)
        || ID_PACE_ECDH_CAM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_ECDH_CAM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_ECDH_CAM_AES_CBC_CMAC_256.equals(oid)) {
      return "ECDH";
    }

    throw new NumberFormatException("Unknown OID: \"" + oid + "\"");
  }

  /**
   * Returns the encryption algorithm described in the PACE protocol object identifier.
   *
   * @param oid the PACE protocol object identifier
   *
   * @return a encryption algorithm as JCE mnemonic string
   */
  public static String toCipherAlgorithm(String oid) {
    if (ID_PACE_DH_GM_3DES_CBC_CBC.equals(oid)
        || ID_PACE_DH_IM_3DES_CBC_CBC.equals(oid)
        || ID_PACE_ECDH_GM_3DES_CBC_CBC.equals(oid)
        || ID_PACE_ECDH_IM_3DES_CBC_CBC.equals(oid)
        ) {
      return "DESede";
    } else if (ID_PACE_DH_GM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_DH_GM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_DH_GM_AES_CBC_CMAC_256.equals(oid)
        || ID_PACE_DH_IM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_DH_IM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_DH_IM_AES_CBC_CMAC_256.equals(oid)
        || ID_PACE_ECDH_GM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_ECDH_GM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_ECDH_GM_AES_CBC_CMAC_256.equals(oid)
        || ID_PACE_ECDH_IM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_ECDH_IM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_ECDH_IM_AES_CBC_CMAC_256.equals(oid)
        || ID_PACE_ECDH_CAM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_ECDH_CAM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_ECDH_CAM_AES_CBC_CMAC_256.equals(oid)) {
      return "AES";
    }

    throw new NumberFormatException("Unknown OID: \"" + oid + "\"");
  }

  /**
   * Returns the digest algorithm described in the PACE protocol object identifier.
   *
   * @param oid the PACE protocol object identifier
   *
   * @return a digest algorithm as JCE mnemonic string
   */
  public static String toDigestAlgorithm(String oid) {
    if (ID_PACE_DH_GM_3DES_CBC_CBC.equals(oid)
        || ID_PACE_DH_IM_3DES_CBC_CBC.equals(oid)
        || ID_PACE_ECDH_GM_3DES_CBC_CBC.equals(oid)
        || ID_PACE_ECDH_IM_3DES_CBC_CBC.equals(oid)
        || ID_PACE_DH_GM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_DH_IM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_ECDH_GM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_ECDH_IM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_ECDH_CAM_AES_CBC_CMAC_128.equals(oid)) {
      return "SHA-1";
    } else if (ID_PACE_DH_GM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_DH_IM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_ECDH_GM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_ECDH_IM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_ECDH_CAM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_DH_GM_AES_CBC_CMAC_256.equals(oid)
        || ID_PACE_DH_IM_AES_CBC_CMAC_256.equals(oid)
        || ID_PACE_ECDH_GM_AES_CBC_CMAC_256.equals(oid)
        || ID_PACE_ECDH_IM_AES_CBC_CMAC_256.equals(oid)
        || ID_PACE_ECDH_CAM_AES_CBC_CMAC_256.equals(oid)) {
      return "SHA-256";
    }

    throw new NumberFormatException("Unknown OID: \"" + oid + "\"");
  }

  /**
   * Returns the key length (128, 192, or 256) described in the given PACE protocol object identifier.
   *
   * @param oid a PACE protocol object identifier
   *
   * @return the key length in bits
   */
  public static int toKeyLength(String oid) {
    if (ID_PACE_DH_GM_3DES_CBC_CBC.equals(oid)
        || ID_PACE_DH_IM_3DES_CBC_CBC.equals(oid)
        || ID_PACE_ECDH_GM_3DES_CBC_CBC.equals(oid)
        || ID_PACE_ECDH_IM_3DES_CBC_CBC.equals(oid)
        || ID_PACE_DH_GM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_DH_IM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_ECDH_GM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_ECDH_IM_AES_CBC_CMAC_128.equals(oid)
        || ID_PACE_ECDH_CAM_AES_CBC_CMAC_128.equals(oid)) {
      return 128;
    } else if (ID_PACE_DH_GM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_ECDH_GM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_DH_IM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_ECDH_IM_AES_CBC_CMAC_192.equals(oid)
        || ID_PACE_ECDH_CAM_AES_CBC_CMAC_192.equals(oid)) {
      return 192;
    } else if (ID_PACE_DH_GM_AES_CBC_CMAC_256.equals(oid)
        || ID_PACE_DH_IM_AES_CBC_CMAC_256.equals(oid)
        || ID_PACE_ECDH_GM_AES_CBC_CMAC_256.equals(oid)
        || ID_PACE_ECDH_IM_AES_CBC_CMAC_256.equals(oid)
        || ID_PACE_ECDH_CAM_AES_CBC_CMAC_256.equals(oid)) {
      return 256;
    }

    throw new NumberFormatException("Unknown OID: \"" + oid + "\"");
  }

  /**
   * Derives a JCE algorithm parameter specification from a PACE standard domain parameter integer.
   *
   * @param stdDomainParam the standard domain parameter
   *
   * @return a JCE algorithm parameter specification
   */
  public static AlgorithmParameterSpec toParameterSpec(BigInteger stdDomainParam) {
    return toParameterSpec(stdDomainParam.intValue());
  }

  /**
   * Derives a JCE algorithm parameter specification from a PACE standard domain parameter integer.
   *
   * @param stdDomainParam the standard domain parameter
   *
   * @return a JCE algorithm parameter specification
   */
  public static AlgorithmParameterSpec toParameterSpec(int stdDomainParam) {
    switch (stdDomainParam) {
      case PARAM_ID_GFP_1024_160:
        return PARAMS_GFP_1024_160;
      case PARAM_ID_GFP_2048_224:
        return PARAMS_GFP_2048_224;
      case PARAM_ID_GFP_2048_256:
        return PARAMS_GFP_2048_256;
      case PARAM_ID_ECP_NIST_P192_R1:
        return PARAMS_ECP_NIST_P192_R1;
      case PARAM_ID_ECP_BRAINPOOL_P192_R1:
        return PARAMS_ECP_BRAINPOOL_P192_R1;
      case PARAM_ID_ECP_NIST_P224_R1:
        return PARAMS_ECP_NIST_P224_R1;
      case PARAM_ID_ECP_BRAINPOOL_P224_R1:
        return PARAMS_ECP_BRAINPOOL_P224_R1;
      case PARAM_ID_ECP_NIST_P256_R1:
        return PARAMS_ECP_NIST_P256_R1;
      case PARAM_ID_ECP_BRAINPOOL_P256_R1:
        return PARAMS_ECP_BRAINPOOL_P256_R1;
      case PARAM_ID_ECP_BRAINPOOL_P320_R1:
        return PARAMS_ECP_BRAINPOOL_P320_R1;
      case PARAM_ID_ECP_NIST_P384_R1:
        return PARAMS_ECP_NIST_P384_R1;
      case PARAM_ID_ECP_BRAINPOOL_P384_R1:
        return PARAMS_ECP_BRAINPOOL_P384_R1;
      case PARAM_ID_ECP_BRAINPOOL_P512_R1:
        return PARAMS_ECP_BRAINPOOL_P512_R1;
      case PARAM_ID_ECP_NIST_P521_R1:
        return PARAMS_ECP_NIST_P521_R1;
      default:
        throw new NumberFormatException("Unknown standardized domain parameters " + stdDomainParam);
    }
  }

  /**
   * Derives a human readable algorithm description from a PACE standard domain parameter integer.
   *
   * @param stdDomainParam the standard domain parameter
   *
   * @return a human readable algorithm description
   */
  public static String toStandardizedParamIdString(BigInteger stdDomainParam) {
    if (stdDomainParam == null) {
      return "null";
    }

    switch (stdDomainParam.intValue()) {
      case PARAM_ID_GFP_1024_160: /* 0 */
        return "1024-bit MODP Group with 160-bit Prime Order Subgroup";
      case PARAM_ID_GFP_2048_224: /* 1 */
        return "2048-bit MODP Group with 224-bit Prime Order Subgroup";
      case PARAM_ID_GFP_2048_256: /* 2 */
        return "2048-bit MODP Group with 256-bit Prime Order Subgroup";
        /* 3 - 7 RFU */
      case PARAM_ID_ECP_NIST_P192_R1: /* 8 */
        return "NIST P-192 (secp192r1)";
      case PARAM_ID_ECP_BRAINPOOL_P192_R1:/* 9 */
        return "BrainpoolP192r1";
      case PARAM_ID_ECP_NIST_P224_R1: /* 10 */
        return "NIST P-224 (secp224r1)";
      case PARAM_ID_ECP_BRAINPOOL_P224_R1: /* 11 */
        return "BrainpoolP224r1";
      case PARAM_ID_ECP_NIST_P256_R1: /* 12 */
        return "NIST P-256 (secp256r1)";
      case PARAM_ID_ECP_BRAINPOOL_P256_R1: /* 13 */
        return "BrainpoolP256r1";
      case PARAM_ID_ECP_BRAINPOOL_P320_R1: /* 14 */
        return "BrainpoolP320r1";
      case PARAM_ID_ECP_NIST_P384_R1: /* 15 */
        return "NIST P-384 (secp384r1)";
      case PARAM_ID_ECP_BRAINPOOL_P384_R1: /* 16 */
        return "BrainpoolP384r1";
      case PARAM_ID_ECP_BRAINPOOL_P512_R1: /* 17 */
        return "BrainpoolP512r1";
      case PARAM_ID_ECP_NIST_P521_R1: /* 18 */
        return "NIST P-521 (secp521r1)";
        /* 19-31 RFU */
      default:
        return stdDomainParam.toString();
    }
  }

  /**
   * Returns an ASN1 name for the given object identifier.
   *
   * @param oid a PACE protocol object identifier
   *
   * @return an ASN1 name if known, or the original object identifier if not
   */
  private String toProtocolOIDString(String oid) {
    if (ID_PACE_DH_GM_3DES_CBC_CBC.equals(oid)) {
      return "id-PACE-DH-GM-3DES-CBC-CBC";
    }
    if (ID_PACE_DH_GM_AES_CBC_CMAC_128.equals(oid)) {
      return "id-PACE-DH-GM-AES-CBC-CMAC-128";
    }
    if (ID_PACE_DH_GM_AES_CBC_CMAC_192.equals(oid)) {
      return "id-PACE-DH-GM-AES-CBC-CMAC-192";
    }
    if (ID_PACE_DH_GM_AES_CBC_CMAC_256.equals(oid)) {
      return "id-PACE-DH-GM-AES-CBC-CMAC-256";
    }
    if (ID_PACE_DH_IM_3DES_CBC_CBC.equals(oid)) {
      return "id-PACE-DH-IM-3DES-CBC-CBC";
    }
    if (ID_PACE_DH_IM_AES_CBC_CMAC_128.equals(oid)) {
      return "id-PACE-DH-IM-AES-CBC-CMAC-128";
    }
    if (ID_PACE_DH_IM_AES_CBC_CMAC_192.equals(oid)) {
      return "id-PACE-DH-IM-AES-CBC-CMAC-192";
    }
    if (ID_PACE_DH_IM_AES_CBC_CMAC_256.equals(oid)) {
      return "id-PACE-DH-IM-AES-CBC-CMAC-256";
    }
    if (ID_PACE_ECDH_GM_3DES_CBC_CBC.equals(oid)) {
      return "id-PACE-ECDH-GM-3DES-CBC-CBC";
    }
    if (ID_PACE_ECDH_GM_AES_CBC_CMAC_128.equals(oid)) {
      return "id-PACE-ECDH-GM-AES-CBC-CMAC-128";
    }
    if (ID_PACE_ECDH_GM_AES_CBC_CMAC_192.equals(oid)) {
      return "id-PACE-ECDH-GM-AES-CBC-CMAC-192";
    }
    if (ID_PACE_ECDH_GM_AES_CBC_CMAC_256.equals(oid)) {
      return "id-PACE-ECDH-GM-AES-CBC-CMAC-256";
    }
    if (ID_PACE_ECDH_IM_3DES_CBC_CBC.equals(oid)) {
      return "id-PACE-ECDH-IM-3DES-CBC-CBC";
    }
    if (ID_PACE_ECDH_IM_AES_CBC_CMAC_128.equals(oid)) {
      return "id-PACE-ECDH-IM-AES-CBC-CMAC-128";
    }
    if (ID_PACE_ECDH_IM_AES_CBC_CMAC_192.equals(oid)) {
      return "id-PACE-ECDH-IM-AES-CBC-CMAC-192";
    }
    if (ID_PACE_ECDH_IM_AES_CBC_CMAC_256.equals(oid)) {
      return "id-PACE-ECDH-IM-AES-CBC-CMAC-256";
    }
    if (ID_PACE_ECDH_CAM_AES_CBC_CMAC_128.equals(oid)) {
      return "id-PACE-ECDH-CAM-AES-CBC-CMAC-128";
    }
    if (ID_PACE_ECDH_CAM_AES_CBC_CMAC_192.equals(oid)) {
      return "id-PACE-ECDH-CAM-AES-CBC-CMAC-192";
    }
    if (ID_PACE_ECDH_CAM_AES_CBC_CMAC_256.equals(oid)) {
      return "id-PACE-ECDH-CAM-AES-CBC-CMAC-256";
    }

    return oid;
  }
}
