package cist.cmc.nfc.sdk.demo.features.nfc_reader;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import cist.cmc.nfc.sdk.core.IdCardReader;
import cist.cmc.nfc.sdk.core.models.EDocument;
import cist.cmc.nfc.sdk.core.models.cp06_auth.CP06Request;
import cist.cmc.nfc.sdk.core.nfc.INfcReaderCallback;
import cist.cmc.nfc.sdk.core.nfc.IdCardReaderStatus;
import cist.cmc.nfc.sdk.demo.R;
import cist.cmc.nfc.sdk.demo.api_service.APIClient;
import cist.cmc.nfc.sdk.demo.features.cp06_authendicate.CP06AuthListner;
import cist.cmc.nfc.sdk.demo.features.nfc_reader.presenter.IdCardInfoPresenter;
import cist.cmc.nfc.sdk.demo.features.nfc_reader.presenter.NfcReaderPresenter;
import cist.cmc.nfc.sdk.demo.features.nfc_reader.presenter.impl.IdCardInfoPresenterImpl;
import cist.cmc.nfc.sdk.demo.features.nfc_reader.presenter.impl.NfcReaderPresenterImpl;
import cist.cmc.nfc.sdk.demo.views.CustomDialog;
import retrofit2.Retrofit;


public class NfcReaderFragment extends Fragment implements INfcReaderCallback {
    private String TAG = NfcReaderFragment.class.getSimpleName();
    private static final String DOCUMENT_NUMBER = "DOCUMENT_NUMBER";
    private static final String BIRTH_DATE = "BIRTH_DATE";
    private static final String EXPIRY_DATE = "EXPIRY_DATE";
    private static final String DOCUMENT_NUMBER_FULL = "DOCUMENT_NUMBER_FULL";
    private static final String CAN_NUMBER = "CAN_NUMBER";

    private String documentNumber;
    private String birthDate;
    private String expiryDate;
    private String documentNumberFull;
    private String canNumber;
    private final IdCardReader cardReader = new IdCardReader();

    private View idCardView;
    private IdCardInfoPresenter idCardInfoPresenter;

    private View nfcReaderStatusView;
    private NfcReaderPresenter nfcReaderPresenter;


    public NfcReaderFragment() {
        // Required empty public constructor

    }


    @Override
    public void onResume() {
        super.onResume();
        if (!cardReader.isNfcAvailable(requireActivity())) {
            Toast.makeText(requireContext(), R.string.driver_not_support_nfc, Toast.LENGTH_LONG).show();
            return;
        }
        if (!cardReader.isNfcEnabled(requireActivity())) {
            Toast.makeText(requireContext(), R.string.driver_turn_of_nfc, Toast.LENGTH_LONG).show();
            return;
        }
        cardReader.enableReaderMode(requireActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        cardReader.disableReaderMode(requireActivity());
    }

    public void onNewIntent(Intent intent) {
        cardReader.readFromIdCard(getContext(), this, intent,
                documentNumber, birthDate, expiryDate, canNumber);
    }

    @Override
    public void onStatusChanged(IdCardReaderStatus status) {
        nfcReaderPresenter.onUpdateView(status);
    }

    @Override
    public void onSuccess(EDocument document) {
        idCardInfoPresenter.onUpdateView(document, documentNumberFull);
    }

    public static NfcReaderFragment newInstance(String documentNumber,
                                                String birthDate,
                                                String expiryDate, String canNumber, String documentNumberFull) {
        NfcReaderFragment fragment = new NfcReaderFragment();
        Bundle args = new Bundle();
        args.putString(DOCUMENT_NUMBER, documentNumber);
        args.putString(BIRTH_DATE, birthDate);
        args.putString(EXPIRY_DATE, expiryDate);
        args.putString(CAN_NUMBER, canNumber);
        args.putString(DOCUMENT_NUMBER_FULL, documentNumberFull);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        if (getArguments() != null) {
            documentNumber = getArguments().getString(DOCUMENT_NUMBER);
            birthDate = getArguments().getString(BIRTH_DATE);
            expiryDate = getArguments().getString(EXPIRY_DATE);
            canNumber = getArguments().getString(CAN_NUMBER);
            documentNumberFull = getArguments().getString(DOCUMENT_NUMBER_FULL);
        }
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpView(view);
    }

    private void setUpView(View view) {
        nfcReaderStatusView = view.findViewById(R.id.nfcReaderStatus);
        idCardView = view.findViewById(R.id.idCardView);
        nfcReaderPresenter = new NfcReaderPresenterImpl(nfcReaderStatusView);
        FragmentManager manager = getActivity().getSupportFragmentManager();
        idCardInfoPresenter = new IdCardInfoPresenterImpl(idCardView, manager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nfc_reader, container, false);
    }
}