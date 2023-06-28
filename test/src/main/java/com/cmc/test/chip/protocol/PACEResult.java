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
 * $Id: PACEResult.java 1763 2018-02-18 07:41:30Z martijno $
 */

package com.cmc.test.chip.protocol;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.PublicKey;

import com.cmc.test.chip.AccessKeySpec;
import com.cmc.test.chip.Util;
import com.cmc.test.chip.lds.PACEInfo.MappingType;

/**
 * Result of PACE protocol.
 *
 * @author The JMRTD team (info@jmrtd.org)
 *
 * @version $Revision: 1763 $
 */
public class PACEResult implements Serializable {

  private static final long serialVersionUID = -6819675856205885052L;

  private MappingType mappingType;
  private String agreementAlg;
  private String cipherAlg;
  private String digestAlg;
  private int keyLength;

  private AccessKeySpec paceKey;

  private PACEMappingResult mappingResult;

  private PublicKey piccPublicKey;

  private KeyPair pcdKeyPair;

  private SecureMessagingWrapper wrapper;

  /**
   * The result of a PACE protocol run.
   *
   * @param paceKey the access key
   * @param mappingType the mapping type, {@code GM}, {@code IM}, or {@code CAM}
   * @param agreementAlg the agreement algorithm, {@code "DH"} or {@code "ECDH"}
   * @param cipherAlg the cipher algorithm
   * @param digestAlg the digest algorithm
   * @param keyLength the key length
   * @param mappingResult the result of the mapping step
   * @param pcdKeyPair the key pair generated by the PCD
   * @param piccPublicKey the public key sent by the PICC
   * @param wrapper the resulting secure messaging wrapper
   */
  public PACEResult(AccessKeySpec paceKey,
      MappingType mappingType, String agreementAlg, String cipherAlg, String digestAlg, int keyLength,
      PACEMappingResult mappingResult,
      KeyPair pcdKeyPair, PublicKey piccPublicKey, SecureMessagingWrapper wrapper) {
    this.paceKey = paceKey;
    this.mappingType = mappingType;
    this.agreementAlg = agreementAlg;
    this.cipherAlg = cipherAlg;
    this.digestAlg = digestAlg;
    this.keyLength = keyLength;
    this.mappingResult = mappingResult;
    this.pcdKeyPair = pcdKeyPair;
    this.piccPublicKey = piccPublicKey;
    this.wrapper = wrapper;
  }

  /**
   * Returns the access key that was used.
   *
   * @return the PACE key
   */
  public AccessKeySpec getPACEKey() {
    return paceKey;
  }

  /**
   * Returns the mapping result.
   *
   * @return the mapping result
   */
  public PACEMappingResult getMappingResult() {
    return mappingResult;
  }

  /**
   * Returns the secure messaging wrapper that was created after completion of the PACE protocol run.
   *
   * @return the secure messaging wrapper that was created after completion of the PACE protocol run
   */
  public SecureMessagingWrapper getWrapper() {
    return wrapper;
  }

  /**
   * Returns the mapping type.
   *
   * @return the mapping type
   */
  public MappingType getMappingType() {
    return mappingType;
  }

  /**
   * Returns the agreement algorithm that was used in the PACE protocol run.
   *
   * @return the agreement algorithm that was used in the PACE protocol run
   */
  public String getAgreementAlg() {
    return agreementAlg;
  }

  /**
   * Returns the cipher algorithm that was reported in the PACE info.
   *
   * @return the cipher algorithm
   */
  public String getCipherAlg() {
    return cipherAlg;
  }

  /**
   * Returns the digest algorithm that was reported in the PACE info.
   *
   * @return the digest algorithm
   */
  public String getDigestAlg() {
    return digestAlg;
  }

  /**
   * Returns the key length that was reported in the PACE info.
   *
   * @return the key length
   */
  public int getKeyLength() {
    return keyLength;
  }

  /**
   * Returns the ephemeral key pair that was generated by the terminal.
   *
   * @return the ephemeral key pair that was generated by the terminal
   */
  public KeyPair getPCDKeyPair() {
    return pcdKeyPair;
  }

  /**
   * Returns the public key that was sent by the ICC (the chip).
   *
   * @return the public key that was sent by the ICC
   */
  public PublicKey getPICCPublicKey() {
    return piccPublicKey;
  }

  /**
   * Returns a textual representation of this PACE result.
   *
   * @return a textual representation of this PACE result
   */
  @Override
  public String toString() {
    return new StringBuilder()
        .append("PACEResult [")
        .append("paceKey: ").append(paceKey)
        .append(", mappingType: ").append(mappingType)
        .append(", agreementAlg: " + agreementAlg)
        .append(", cipherAlg: " + cipherAlg)
        .append(", digestAlg: " + digestAlg)
        .append(", keyLength: " + keyLength)
        .append(", mappingResult: " + mappingResult)
        .append(", piccPublicKey: " + Util.getDetailedPublicKeyAlgorithm(piccPublicKey))
        .append(", pcdPrivateKey: " + Util.getDetailedPrivateKeyAlgorithm(pcdKeyPair.getPrivate()))
        .append(", pcdPublicKey: " + Util.getDetailedPublicKeyAlgorithm(pcdKeyPair.getPublic()))
        .toString();
  }

  @Override
  public int hashCode() {
    final int prime = 1991;
    int result = 11;
    result = prime * result + ((paceKey == null) ? 0 : paceKey.hashCode());
    result = prime * result + ((agreementAlg == null) ? 0 : agreementAlg.hashCode());
    result = prime * result + ((cipherAlg == null) ? 0 : cipherAlg.hashCode());
    result = prime * result + ((digestAlg == null) ? 0 : digestAlg.hashCode());
    result = prime * result + ((mappingResult == null) ? 0 : mappingResult.hashCode());
    result = prime * result + keyLength;
    result = prime * result + ((mappingType == null) ? 0 : mappingType.hashCode());
    result = prime * result + ((pcdKeyPair == null) ? 0 : pcdKeyPair.hashCode());
    result = prime * result + ((piccPublicKey == null) ? 0 : piccPublicKey.hashCode());
    result = prime * result + ((wrapper == null) ? 0 : wrapper.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    PACEResult other = (PACEResult)obj;
    if (paceKey == null) {
      if (other.paceKey != null) {
        return false;
      }
    } else if (!paceKey.equals(other.paceKey)) {
      return false;
    }
    if (agreementAlg == null) {
      if (other.agreementAlg != null) {
        return false;
      }
    } else if (!agreementAlg.equals(other.agreementAlg)) {
      return false;
    }
    if (cipherAlg == null) {
      if (other.cipherAlg != null) {
        return false;
      }
    } else if (!cipherAlg.equals(other.cipherAlg)) {
      return false;
    }
    if (digestAlg == null) {
      if (other.digestAlg != null) {
        return false;
      }
    } else if (!digestAlg.equals(other.digestAlg)) {
      return false;
    }
    if (mappingResult == null) {
      if (other.mappingResult != null) {
        return false;
      }
    } else if (!mappingResult.equals(other.mappingResult)) {
      return false;
    }
    if (keyLength != other.keyLength) {
      return false;
    }
    if (mappingType != other.mappingType) {
      return false;
    }
    if (pcdKeyPair == null) {
      if (other.pcdKeyPair != null) {
        return false;
      }
    } else if (!pcdKeyPair.equals(other.pcdKeyPair)) {
      return false;
    }
    if (piccPublicKey == null) {
      if (other.piccPublicKey != null) {
        return false;
      }
    } else if (!piccPublicKey.equals(other.piccPublicKey)) {
      return false;
    }
    if (wrapper == null) {
      if (other.wrapper != null) {
        return false;
      }
    } else if (!wrapper.equals(other.wrapper)) {
      return false;
    }

    return true;
  }
}