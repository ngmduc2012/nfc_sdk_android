package cist.cmc.nfc.sdk.core.nfc;

import androidx.annotation.Keep;

@Keep
public enum IdCardReaderStatus {
    AccessingNfcChip,
    ReadingPersonalData,
    ReadingDsCert,
    ReadingPhoto,
    ReadingOptionData,
    Veryfication,//
    CheckFeature,
    // Invalid MRZ key
    Error5001,
    // ConnectionError
    Error5002,
    // mrz key is null or empty
    Error5003,
    // chipAuthUnSucceeded
    Error5004,
    // License has expired
    Error5005,
    Error5006,
    Error5007,//Invalid CAN key
    Error5008,//Invalid user's input key
    Error5009,//Read data error
    Error5010,//get certificate error
    Error5011,//Exception for signaling failed BAC
    Error5012,//An exception to signal errors during execution of the PACE protocol
    Error5013,//CardServiceExceptions are used to signal error Response APDUs
    Error5014,//check input pass error
    Success
}
