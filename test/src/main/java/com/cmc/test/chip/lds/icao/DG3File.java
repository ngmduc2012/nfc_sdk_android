/*
 * JMRTD - A Java API for accessing machine readable travel documents.
 *
 * Copyright (C) 2006 - 2017  The JMRTD team
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
 * $Id: DG3File.java 1808 2019-03-07 21:32:19Z martijno $
 */

package com.cmc.test.chip.lds.icao;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.cmc.test.chip.cbeff.BiometricDataBlock;
import com.cmc.test.chip.cbeff.BiometricDataBlockDecoder;
import com.cmc.test.chip.cbeff.BiometricDataBlockEncoder;
import com.cmc.test.chip.cbeff.CBEFFInfo;
import com.cmc.test.chip.cbeff.ComplexCBEFFInfo;
import com.cmc.test.chip.cbeff.ISO781611Decoder;
import com.cmc.test.chip.cbeff.ISO781611Encoder;
import com.cmc.test.chip.cbeff.SimpleCBEFFInfo;
import com.cmc.test.chip.cbeff.StandardBiometricHeader;
import com.cmc.test.chip.lds.CBEFFDataGroup;
import com.cmc.test.chip.lds.iso19794.FingerInfo;

/**
 * File structure for the EF_DG3 file.
 * Partially specified in ISO/IEC FCD 19794-4 aka Annex F.
 *
 * @author The JMRTD team (info@jmrtd.org)
 *
 * @version $Revision: 1808 $
 */
public class DG3File extends CBEFFDataGroup<FingerInfo> {

  private static final long serialVersionUID = -1037522331623814528L;

  private static final ISO781611Decoder DECODER = new ISO781611Decoder(new BiometricDataBlockDecoder<FingerInfo>() {
    public FingerInfo decode(InputStream inputStream, StandardBiometricHeader sbh, int index, int length) throws IOException {
      return new FingerInfo(sbh, inputStream);
    }
  });

  private static final ISO781611Encoder<FingerInfo> ENCODER = new ISO781611Encoder<FingerInfo>(new BiometricDataBlockEncoder<FingerInfo>() {
    public void encode(FingerInfo info, OutputStream outputStream) throws IOException {
      info.writeObject(outputStream);
    }
  });

  private boolean shouldAddRandomDataIfEmpty;

  /**
   * Creates a new file with the specified records.
   *
   * @param fingerInfos records
   */
  public DG3File(List<FingerInfo> fingerInfos) {
    this(fingerInfos, true);
  }

  /**
   * Creates a new file with the specified records.
   *
   * @param fingerInfos records
   * @param shouldAddRandomDataIfEmpty whether to add random data when there are no records to encode
   */
  public DG3File(List<FingerInfo> fingerInfos, boolean shouldAddRandomDataIfEmpty) {
    super(EF_DG3_TAG, fingerInfos);
    this.shouldAddRandomDataIfEmpty = shouldAddRandomDataIfEmpty;
  }

  /**
   * Creates a new file based on an input stream.
   *
   * @param inputStream an input stream
   *
   * @throws IOException on error reading from input stream
   */
  public DG3File(InputStream inputStream) throws IOException {
    super(EF_DG3_TAG, inputStream);
  }

  @Override
  protected void readContent(InputStream inputStream) throws IOException {
    ComplexCBEFFInfo cbeffInfo = DECODER.decode(inputStream);
    List<CBEFFInfo> records = cbeffInfo.getSubRecords();
    for (CBEFFInfo record: records) {
      if (!(record instanceof SimpleCBEFFInfo<?>)) {
        throw new IOException("Was expecting a SimpleCBEFFInfo, found " + record.getClass().getSimpleName());
      }
      BiometricDataBlock bdb = ((SimpleCBEFFInfo<?>)record).getBiometricDataBlock();
      if (!(bdb instanceof FingerInfo)) {
        throw new IOException("Was expecting a FingerInfo, found " + bdb.getClass().getSimpleName());
      }
      FingerInfo fingerInfo = (FingerInfo)bdb;
      add(fingerInfo);
    }

    /* FIXME: by symmetry, shouldn't there be a readOptionalRandomData here? */
  }

  @Override
  protected void writeContent(OutputStream outputStream) throws IOException {
    ComplexCBEFFInfo cbeffInfo = new ComplexCBEFFInfo();
    List<FingerInfo> fingerInfos = getSubRecords();
    for (FingerInfo fingerInfo: fingerInfos) {
      SimpleCBEFFInfo<FingerInfo> simpleCBEFFInfo = new SimpleCBEFFInfo<FingerInfo>(fingerInfo);
      cbeffInfo.add(simpleCBEFFInfo);
    }
    ENCODER.encode(cbeffInfo, outputStream);

    /* NOTE: Supplement to ICAO Doc 9303 R7-p1_v2_sIII_0057. */
    if (shouldAddRandomDataIfEmpty) {
      writeOptionalRandomData(outputStream);
    }
  }

  /**
   * Returns a textual representation of this file.
   *
   * @return a textual representation of this file
   */
  @Override
  public String toString() {
    return "DG3File [" + super.toString() + "]";
  }

  /**
   * Returns the finger infos embedded in this file.
   *
   * @return finger infos
   */
  public List<FingerInfo> getFingerInfos() {
    return getSubRecords();
  }

  /**
   * Adds a finger info to this file.
   *
   * @param fingerInfo the finger info to add
   */
  public void addFingerInfo(FingerInfo fingerInfo) {
    add(fingerInfo);
  }

  /**
   * Removes a finger info from this file.
   *
   * @param index the index of the finger info to remove
   */
  public void removeFingerInfo(int index) {
    remove(index);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (shouldAddRandomDataIfEmpty ? 1231 : 1237);
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

    DG3File other = (DG3File)obj;
    return shouldAddRandomDataIfEmpty == other.shouldAddRandomDataIfEmpty;
  }
}
