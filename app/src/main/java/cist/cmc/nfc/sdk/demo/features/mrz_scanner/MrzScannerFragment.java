package cist.cmc.nfc.sdk.demo.features.mrz_scanner;

import android.os.Bundle;

import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

import cist.cmc.nfc.sdk.core.mrz.IMRZCallBack;
import cist.cmc.nfc.sdk.demo.R;
import cist.cmc.nfc.sdk.demo.base.BaseFragment;
import cist.cmc.nfc.sdk.demo.features.collected_info.MrzCollectedInfoFragment;


public class MrzScannerFragment extends BaseFragment implements IMRZCallBack {
    public static boolean isSuccess = false;


    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private ImageAnalysis imageAnalyzer;
    private Camera camera;

    public MrzScannerFragment() {
        // Required empty public constructor
    }


    public static MrzScannerFragment newInstance() {
        return new MrzScannerFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        bindCameraUseCases();
    }

    public void bindCameraUseCases() {
        imageAnalyzer = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        imageAnalyzer.setAnalyzer(
                ContextCompat.getMainExecutor(requireContext()),
                new MrzAnalyzer(this)
        );
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    cameraProvider.unbindAll();
                    bindPreview(cameraProvider);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }


    public void bindPreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalyzer);
    }

    @Override
    public void onResult(String documentNumber, String birthDate, String expiryDate, String documentNumberFull) {
        if (!isSuccess) {
            isSuccess = true;
            push(MrzCollectedInfoFragment.newInstance(documentNumber, birthDate, expiryDate, documentNumberFull));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mrz_scanner, container, false);
        previewView = view.findViewById(R.id.mrz_camera_preview);
        return view;
    }
}