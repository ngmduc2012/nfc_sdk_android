package cist.cmc.nfc.sdk.demo.features.collected_info;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import cist.cmc.nfc.sdk.core.qrcode.QRService;
import cist.cmc.nfc.sdk.demo.R;
import cist.cmc.nfc.sdk.demo.base.BaseFragment;
import cist.cmc.nfc.sdk.demo.features.nfc_reader.NfcReaderFragment;

public class QRCodeCollectedInfoFragment extends BaseFragment implements View.OnClickListener {

    private static final String QR_CODE_SERVICE = "qr_code_service";
    private QRService qrService;
    private TextView valueName;
    private TextView valueCCCDNumber;
    private TextView valueSex;
    private TextView valueDateBirth;
    private TextView valueDateOfIssue;
    private TextView valueExpriyDate;
    private TextView valueHomeTown;
    private TextView valueCMNDNumber;
    private Button btnRead;


    public QRCodeCollectedInfoFragment() {
        // Required empty public constructor
    }

    public static QRCodeCollectedInfoFragment newInstance(QRService qrService) {
        QRCodeCollectedInfoFragment fragment = new QRCodeCollectedInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(QR_CODE_SERVICE, qrService);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            qrService = (QRService) getArguments().getSerializable(QR_CODE_SERVICE);
        }
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        valueCCCDNumber = view.findViewById(R.id.value_cccd_number);
        valueName = view.findViewById(R.id.value_personal_name);
        valueSex = view.findViewById(R.id.value_sex);
        valueDateBirth = view.findViewById(R.id.value_date_of_birth);
        valueDateOfIssue = view.findViewById(R.id.valueDateOfIssue);
        valueExpriyDate = view.findViewById(R.id.value_date_expiration);
        valueHomeTown = view.findViewById(R.id.value_district);
        valueCMNDNumber = view.findViewById(R.id.value_CMND_number);
        btnRead = view.findViewById(R.id.btn_read);
        setData();
    }

    private void setData() {
        valueCCCDNumber.setText(qrService.getPersonal().getCccdNumber());
        valueName.setText(qrService.getPersonal().getName());
        valueSex.setText(qrService.getPersonal().getSex());
        valueDateBirth.setText(qrService.getPersonal().getDateOfBirth());
        valueExpriyDate.setText(qrService.getPersonal().getDateExpiration());
        valueDateOfIssue.setText(qrService.getPersonal().getDateOfIssue());
        valueHomeTown.setText(qrService.getPersonal().getPermanentAddress());
        valueCMNDNumber.setText(qrService.getPersonal().getCmndNumber());
        btnRead.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_q_r_code_collected_info, container, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_read:
                push(NfcReaderFragment.newInstance(qrService.getMrzInfo().getDocumentNumber(),
                        qrService.getMrzInfo().getBirthDate(), qrService.getMrzInfo().getExpiryDate(), null, null));
                break;
            default:
                break;
        }

    }
}