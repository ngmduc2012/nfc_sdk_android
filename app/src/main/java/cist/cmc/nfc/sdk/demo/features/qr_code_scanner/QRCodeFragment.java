package cist.cmc.nfc.sdk.demo.features.qr_code_scanner;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import org.jetbrains.annotations.NotNull;

import cist.cmc.nfc.sdk.core.mrz.MrzInfo;
import cist.cmc.nfc.sdk.core.qrcode.QRCodeCallback;
import cist.cmc.nfc.sdk.core.qrcode.QRPersonal;
import cist.cmc.nfc.sdk.core.qrcode.QRService;
import cist.cmc.nfc.sdk.demo.R;
import cist.cmc.nfc.sdk.demo.base.BaseFragment;
import cist.cmc.nfc.sdk.demo.features.collected_info.MrzCollectedInfoFragment;
import cist.cmc.nfc.sdk.demo.features.collected_info.QRCodeCollectedInfoFragment;


public class QRCodeFragment extends BaseFragment implements QRCodeCallback {

    private QRCodePresenter presenter;
    private CodeScanner mCodeScanner;
    //private QRService qrService;

    public QRCodeFragment() {
        // Required empty public constructor
        this.presenter = new QRCodePresenter(this);
    }

    public static QRCodeFragment newInstance() {
        QRCodeFragment fragment = new QRCodeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Activity activity = getActivity();
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_q_r_code, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull @NotNull Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        QRService qrService = new QRService(result.getText());
                        qrService.readObject("/");
                        presenter.scanQrCode(qrService);
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCodeScanner.startPreview();
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    @Override
    public void onResult(QRService qrService) {
        push(QRCodeCollectedInfoFragment.newInstance(qrService));
    }
}