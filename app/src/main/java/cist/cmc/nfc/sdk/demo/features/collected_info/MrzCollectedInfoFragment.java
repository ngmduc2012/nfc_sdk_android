package cist.cmc.nfc.sdk.demo.features.collected_info;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import cist.cmc.nfc.sdk.demo.R;
import cist.cmc.nfc.sdk.demo.base.BaseFragment;
import cist.cmc.nfc.sdk.demo.features.mrz_scanner.MrzScannerFragment;
import cist.cmc.nfc.sdk.demo.features.nfc_reader.NfcReaderFragment;


public class MrzCollectedInfoFragment extends BaseFragment {


    private static final String DOCUMENT_NUMBER = "DOCUMENT_NUMBER";
    private static final String BIRTH_DATE = "BIRTH_DATE";
    private static final String EXPIRY_DATE = "EXPIRY_DATE";
    private static final String DOCUMENT_NUMBER_FULL = "DOCUMENT_NUMBER_FULL";

    private String documentNumber;
    private String birthDate;
    private String expiryDate;
    private String documentNumberFull;


    public MrzCollectedInfoFragment() {
        // Required empty public constructor
    }


    public static MrzCollectedInfoFragment newInstance(String documentNumber,
                                                       String birthDate,
                                                       String expiryDate,
                                                       String documentNumberFull) {
        MrzCollectedInfoFragment fragment = new MrzCollectedInfoFragment();
        Bundle args = new Bundle();
        args.putString(DOCUMENT_NUMBER, documentNumber);
        args.putString(BIRTH_DATE, birthDate);
        args.putString(EXPIRY_DATE, expiryDate);
        args.putString(DOCUMENT_NUMBER_FULL, documentNumberFull);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            documentNumber = getArguments().getString(DOCUMENT_NUMBER);
            birthDate = getArguments().getString(BIRTH_DATE);
            expiryDate = getArguments().getString(EXPIRY_DATE);
            documentNumberFull = getArguments().getString(DOCUMENT_NUMBER_FULL);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MrzScannerFragment.isSuccess = false;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView) view.findViewById(R.id.documentNumber)).setText(documentNumber);
        ((TextView) view.findViewById(R.id.birthDate)).setText(birthDate);
        ((TextView) view.findViewById(R.id.expiryDate)).setText(expiryDate);
        ((Button) view.findViewById(R.id.btn_next)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                push(NfcReaderFragment.newInstance(documentNumber, birthDate, expiryDate, null, documentNumberFull));
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mrz_collected_info, container, false);
    }
}