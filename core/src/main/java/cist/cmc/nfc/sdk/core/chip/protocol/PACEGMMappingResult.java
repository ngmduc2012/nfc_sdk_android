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
 * $Id: PACEGMMappingResult.java 1764 2018-02-19 16:19:25Z martijno $
 */

package cist.cmc.nfc.sdk.core.chip.protocol;

import java.security.KeyPair;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;

/**
 * The result of the PACE nonce mapping step in Generic Mapping setting.
 *
 * @author The JMRTD team (info@jmrtd.org)
 *
 * @version $Revision: 1764 $
 */
public abstract class PACEGMMappingResult extends PACEMappingResult {

  private static final long serialVersionUID = -3373471956987358728L;

  private PublicKey piccMappingPublicKey;

  private KeyPair pcdMappingKeyPair;

  /**
   * Constructs a result.
   *
   * @param staticParameters the static parameters
   * @param piccNonce the nonce that was sent by the PICC
   * @param piccMappingPublicKey the mapping public key sent by the PICC
   * @param pcdMappingKeyPair the key-pair generated by the PCD
   * @param ephemeralParameters the ephemeral parameters that were derived
   */
  public PACEGMMappingResult(AlgorithmParameterSpec staticParameters, byte[] piccNonce,
      PublicKey piccMappingPublicKey, KeyPair pcdMappingKeyPair,
      AlgorithmParameterSpec ephemeralParameters) {
    super(staticParameters, piccNonce, ephemeralParameters);
    this.piccMappingPublicKey = piccMappingPublicKey;
    this.pcdMappingKeyPair = pcdMappingKeyPair;
  }

  /**
   * Returns the public key that was sent by the PICC.
   *
   * @return the PICC's public key
   */
  public PublicKey getPICCMappingPublicKey() {
    return piccMappingPublicKey;
  }

  /**
   * Returns the key-pair generated by the PCD.
   *
   * @return the PCD's key-pair
   */
  public KeyPair getPCDMappingKeyPair() {
    return pcdMappingKeyPair;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((piccMappingPublicKey == null) ? 0 : piccMappingPublicKey.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    PACEGMMappingResult other = (PACEGMMappingResult) obj;
    if (piccMappingPublicKey == null) {
      if (other.piccMappingPublicKey != null) {
        return false;
      }
    } else if (!piccMappingPublicKey.equals(other.piccMappingPublicKey)) {
      return false;
    }

    if (pcdMappingKeyPair == null) {
      if (other.pcdMappingKeyPair != null) {
        return false;
      }
    } else if (!pcdMappingKeyPair.equals(other.pcdMappingKeyPair)) {
      return false;
    }

    return true;
  }
}
