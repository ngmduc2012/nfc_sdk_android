package cist.cmc.nfc.sdk.demo.features.input_pass;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;

import cist.cmc.nfc.sdk.core.input.PasswordInput;
import cist.cmc.nfc.sdk.core.input.DateInputMask;
import cist.cmc.nfc.sdk.core.mrz.MrzInfo;
import cist.cmc.nfc.sdk.core.utils.StringUtils;
import cist.cmc.nfc.sdk.demo.R;

public class InputDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = InputDialog.class.getSimpleName();
    private Activity activity;
    private Button yes, no;
    private InputDialogListener listener;

    private EditText docuText;
    private EditText dateOfBirthText;
    private EditText exprityText;
    private CheckBox checkUnlimited;

    private DateInputMask dateOfBirthMask;
    private DateInputMask exprityDateMask;

    private boolean checked;

    public InputDialog(@NonNull Context context) {
        super(context);
        this.activity = (Activity) context;
    }

    public void setDataListener(InputDialogListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_input);

        docuText = findViewById(R.id.documentNumber);
        dateOfBirthText = findViewById(R.id.dateOfBirt);
        exprityText = findViewById(R.id.exprityDate);
        checkUnlimited = findViewById(R.id.check_unlimited);
        checkUnlimited.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    checked = isChecked;
                    exprityText.setEnabled(false);
                } else {
                    checked = false;
                    exprityText.setEnabled(true);
                }
            }
        });
        if (checked == true) {
            dateOfBirthMask = new DateInputMask(dateOfBirthText);
        } else {
            dateOfBirthMask = new DateInputMask(dateOfBirthText);
            exprityDateMask = new DateInputMask(exprityText);
        }

        yes = findViewById(R.id.btn_yes);
        no = findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                String documentNumber = "031090013335";
                String dateBirthInput = "19/08/1990";
                String expiryDateInput = "19/08/2030";
                String dateOfIssue = "09/02/2021";
                PasswordInput pass = new PasswordInput();
                pass.setPassKeyToExpiryDate(documentNumber, dateBirthInput, expiryDateInput, "/");
                Log.d(TAG, "Document number pass: " + pass.getMrzInfo().getDocumentNumber());
                Log.d(TAG, "Date of birth pass: " + pass.getMrzInfo().getBirthDate());
                Log.d(TAG, "Expiry date pass: " + pass.getMrzInfo().getExpiryDate());
                Log.d(TAG, "Date Unlimited: " + PasswordInput.dateUnlimited);

                Log.d(TAG, "-----------------------------------------------------------------");
                pass.setPassKeyToDateOfIssue(documentNumber, dateBirthInput, dateOfIssue, "/");
                Log.d(TAG, "Document number pass: " + pass.getMrzInfo().getDocumentNumber());
                Log.d(TAG, "Date of birth pass: " + pass.getMrzInfo().getBirthDate());
                Log.d(TAG, "Expiry date pass: " + pass.getMrzInfo().getExpiryDate());
                Log.d(TAG, "Date Unlimited: " + PasswordInput.dateUnlimited);

                if (checked == true) {
                    listener.onIdCardReader(StringUtils.subStringWithIndex(docuText.getText().toString(), 3),
                            dateOfBirthMask.getPassKey(), MrzInfo.expiryDateUnlimited);
                    dismiss();
                } else {
                    listener.onIdCardReader(StringUtils.subStringWithIndex(docuText.getText().toString(), 3),
                            dateOfBirthMask.getPassKey(), exprityDateMask.getPassKey());
                    dismiss();
                }
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
