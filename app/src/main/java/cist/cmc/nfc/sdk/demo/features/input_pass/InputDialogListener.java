package cist.cmc.nfc.sdk.demo.features.input_pass;

public interface InputDialogListener {
    void onIdCardReader(String documentNumber, String dateOfBirth, String exprityDate);
}
