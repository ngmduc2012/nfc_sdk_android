package cist.cmc.nfc.sdk.demo.features.input_can_number;

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
import android.widget.Toast;

import androidx.annotation.NonNull;

import cist.cmc.nfc.sdk.core.input.CanNumberMask;
import cist.cmc.nfc.sdk.core.input.DateInputMask;
import cist.cmc.nfc.sdk.core.mrz.MrzInfo;
import cist.cmc.nfc.sdk.core.utils.StringUtils;
import cist.cmc.nfc.sdk.demo.R;
import cist.cmc.nfc.sdk.demo.features.input_pass.InputDialogListener;

public class CanNumberDialog extends Dialog implements View.OnClickListener {

    private Activity activity;
    private Button yes, no;
    private CanNumberListner listener;

    private EditText canNumber;

    public CanNumberDialog(@NonNull Context context) {
        super(context);
        this.activity = (Activity) context;
    }

    public void setDataListener(CanNumberListner listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.can_number_dialog);
        canNumber = findViewById(R.id.canNumber);
        yes = findViewById(R.id.btn_can_yes);
        no = findViewById(R.id.btn_can_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_can_yes:
                if ((canNumber.getText().toString() != null) || (canNumber.getText().toString() != "")) {
                    listener.onCanNumberCardReader(new CanNumberMask(canNumber.getText().toString()).getCanNumber());
                    dismiss();
                } else {
                    Toast.makeText(activity.getBaseContext(), "Số căn cước công dân không được để trống", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_can_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
