package cist.cmc.nfc.sdk.demo.features.mrz_scanner_guide;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import cist.cmc.nfc.sdk.core.models.cp06_auth.CP06Request;
import cist.cmc.nfc.sdk.core.mrz.MrzInfo;
import cist.cmc.nfc.sdk.core.mrz.OcrMrzService;
import cist.cmc.nfc.sdk.core.mrz.OcrResultCallBack;
import cist.cmc.nfc.sdk.demo.R;
import cist.cmc.nfc.sdk.demo.api_service.APIClient;
import cist.cmc.nfc.sdk.demo.base.BaseFragment;
import cist.cmc.nfc.sdk.demo.features.collected_info.MrzCollectedInfoFragment;
import cist.cmc.nfc.sdk.demo.features.cp06_authendicate.CP06AuthListner;
import cist.cmc.nfc.sdk.demo.features.input_can_number.CanNumberDialog;
import cist.cmc.nfc.sdk.demo.features.input_can_number.CanNumberListner;
import cist.cmc.nfc.sdk.demo.features.input_pass.InputDialog;
import cist.cmc.nfc.sdk.demo.features.input_pass.InputDialogListener;
import cist.cmc.nfc.sdk.demo.features.mrz_scanner.MrzScannerFragment;
import cist.cmc.nfc.sdk.demo.features.nfc_reader.NfcReaderFragment;
import cist.cmc.nfc.sdk.demo.features.qr_code_scanner.QRCodeFragment;
import cist.cmc.nfc.sdk.demo.views.CustomDialog;
import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MrzScannerGuideFragment extends BaseFragment implements EasyImage.EasyImageStateHandler, InputDialogListener, CanNumberListner {
    private String TAG = MrzScannerGuideFragment.class.getSimpleName();
    private static final String STATE_KEY = "image_state";
    private final int REQUEST_CODE_CAMERA_PERMISSION = 123;
    private final int REQUEST_CODE_TAKE_PHOTO_PERMISSION = 456;
    private EasyImage easyImage;
    public OcrMrzService ocrMrzService = new OcrMrzService();

    private boolean isAuthOnline = false;

    public MrzScannerGuideFragment() {
    }

    public static MrzScannerGuideFragment newInstance() {
        return new MrzScannerGuideFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        String dsCert = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUVIakNDQTZPZ0F3SUJBZ0lVY1pHdG5hZ0c2Qm1kR1BwVVRFSFc1U2Fsc3pvd0NnWUlLb1pJemowRUF3TXcKYnpFTE1Ba0dBMVVFQmhNQ1ZrNHhPekE1QmdOVkJBb01NbFpwWlhSdVlXMGdSMjkyWlhKdWJXVnVkQ0JKYm1adgpjbTFoZEdsdmJpQlRaV04xY21sMGVTQkRiMjF0YVhOemFXOXVNUXd3Q2dZRFZRUUZFd013TURFeEZUQVRCZ05WCkJBTU1ERU5UUTBFZ1ZtbGxkRzVoYlRBZUZ3MHlNVEV3TWpFd05qRXdORGhhRncwME5EQXhNVFF3TmpFd05EaGEKTUlHMU1Rc3dDUVlEVlFRR0V3SldUakVrTUNJR0ExVUVDZ3diVFdsdWFYTjBjbmtnYjJZZ1VIVmliR2xqSUZObApZM1Z5YVhSNU1VZ3dSZ1lEVlFRTEREOVFiMnhwWTJVZ1JHVndZWEowYldWdWRDQm1iM0lnUVdSdGFXNXBjM1J5CllYUnBkbVVnVFdGdVlXZGxiV1Z1ZENCdlppQlRiMk5wWVd3Z1QzSmtaWEl4TmpBMEJnTlZCQU1NTFVSdlkzVnQKWlc1MElGTnBaMjVsY2lCT1lYUnBiMjVoYkNCSlpHVnVkR2xtYVdOaGRHbHZiaUF3TURBek1qQjZNQlFHQnlxRwpTTTQ5QWdFR0NTc2tBd01DQ0FFQkN3TmlBQVIyZWt2bmRPY2lzbW8wdit0N2RSSWs3UWpuV21ldit5RGM1R1JaCk1sV2hQbWlUQ0VKbFhaNnoveG53cUhaSGdzWWtQTE1iMmtVdjFMRGlKNXhsUDY0cTdaY3A0RlB6U3h0YlZ2b3AKSlZTK1VwbURQakxGWU9mZHIvYWxZdUdiUHUyamdnR3pNSUlCcnpBTUJnTlZIUk1CQWY4RUFqQUFNQjhHQTFVZApJd1FZTUJhQUZGZXJkUWdpUklQd1NjeGpXeXlWeE9SR3BZZ0lNSDRHQ0NzR0FRVUZCd0VCQkhJd2NEQTNCZ2dyCkJnRUZCUWN3QW9ZcmFIUjBjRG92TDI1d2EyUXVaMjkyTG5adUwyTnlkQzlsYVdRdFkzTmpZUzEyYVdWMGJtRnQKTG1OeWREQTFCZ2dyQmdFRkJRY3dBb1lwYUhSMGNEb3ZMMk5oTG1kdmRpNTJiaTlqY25RdlpXbGtMV056WTJFdApkbWxsZEc1aGJTNWpjblF3TXdZRFZSMFJCQ3d3S3FRUE1BMHhDekFKQmdOVkJBY01BbFpPaGhkb2RIUndPaTh2CmJuQnJaQzVuYjNZdWRtNHZZM05qWVRCdEJnTlZIUjhFWmpCa01ER2dMNkF0aGl0b2RIUndPaTh2Ym5CclpDNW4KYjNZdWRtNHZZM0pzTDJWcFpDMWpjMk5oTFhacFpYUnVZVzB1WTNKc01DK2dMYUFyaGlsb2RIUndPaTh2WTJFdQpaMjkyTG5adUwyTnliQzlsYVdRdFkzTmpZUzEyYVdWMGJtRnRMbU55YkRBZEJnTlZIUTRFRmdRVUR4K0RMNUJLCkNOWjlhTGJMa0dEM3JXOW1keGd3S3dZRFZSMFFCQ1F3SW9BUE1qQXlNVEV3TWpFd05qRXdORGhhZ1E4eU1ESXkKTURFeE9UQTJNVEEwT0Zvd0RnWURWUjBQQVFIL0JBUURBZ2VBTUFvR0NDcUdTTTQ5QkFNREEya0FNR1lDTVFEcQp2UDUvZmUwTmpTRVNYNUdFQzdWK2hBaFhzak1zYkMyZmxOSm13L1I3OSs1TzFDeUIwdzZheWZRTXB4THlFamdDCk1RRDRjcEhxdGh2eGZ3TlJYZVNhOFV6bGdoQWlLOXZJRDlqRHJmSWJ5eDZZZzVQa0hKSXlaQnBXeFplblFHdTIKdVdvPQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCg==";
//        CP06Request cp06Request = new CP06Request("R58R344F7ML", "040092011980", "Ba Đình, Hà Nội", "LTC", "LTC@1234aAcokyvina*!@#", dsCert);
//        CustomDialog dialog = new CustomDialog(cp06Request);
//        dialog.show(getActivity().getSupportFragmentManager(), "showPopup");
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpEasyImage();
        setUpView(view);
    }

    private void setUpEasyImage() {
        easyImage = new EasyImage.Builder(requireContext())
                .setChooserTitle("Chụp ảnh mặt sau CCCD")
                .setCopyImagesToPublicGalleryFolder(true) // THIS requires granting WRITE_EXTERNAL_STORAGE permission for devices running Android 9 or lower
                .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                .setFolderName("cccd")
                .allowMultiple(true)
                .setStateHandler(this)
                .build();
    }

    private void setUpView(@NotNull View view) {

        ((Button) view.findViewById(R.id.qrCodeBtn)).setOnClickListener(v -> navigateToQRCodeScanner());
        ((Button) view.findViewById(R.id.scanBtn)).setOnClickListener(v -> navigateToMrzScanner());

        ((Button) view.findViewById(R.id.takePhoto)).setOnClickListener(v -> takePhoto());
        ((Button) view.findViewById(R.id.inputText)).setOnClickListener(v -> {
            InputDialog cdd = new InputDialog(getContext());
            cdd.setDataListener(MrzScannerGuideFragment.this);
            cdd.show();
        });
        ((Button) view.findViewById(R.id.inputCanNumber)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CanNumberDialog cnn = new CanNumberDialog(getContext());
                cnn.setDataListener(MrzScannerGuideFragment.this);
                cnn.show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        easyImage.handleActivityResult(requestCode, resultCode, data, requireActivity(), new EasyImage.Callbacks() {
            @Override
            public void onMediaFilesPicked(MediaFile[] imageFiles, MediaSource source) {
                Toast.makeText(requireContext(), "Đang nhận dạng mã MRZ", Toast.LENGTH_SHORT).show();
                for (MediaFile imageFile : imageFiles) {
                    try {
                        ocrMrzService.progressFromFilePath(requireContext(), Uri.fromFile(imageFile.getFile()), new OcrResultCallBack() {
                            @Override
                            public void onSuccess(MrzInfo info) {
                                push(MrzCollectedInfoFragment.newInstance(info.getDocumentNumber(), info.getBirthDate(), info.getExpiryDate(), info.getDocumentNumberFull()));
                            }

                            @Override
                            public void onFailure(Exception error) {
                                Toast.makeText(requireContext(), "Không tìm thấy mã MRZ trong ảnh", Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                            }

                            @Override
                            public void onComplete() {
                                Toast.makeText(getContext(), "complete", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (IOException e) {
                        Toast.makeText(getContext(), "Có lỗi trong quá trình trích xuất", Toast.LENGTH_SHORT).show();
                        // throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onImagePickerError(@NonNull Throwable error, @NonNull MediaSource source) {
                error.printStackTrace();
            }

            @Override
            public void onCanceled(@NonNull MediaSource source) {
            }
        });
    }

    @AfterPermissionGranted(REQUEST_CODE_TAKE_PHOTO_PERMISSION)
    private void takePhoto() {
        if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            easyImage.openCameraForImage(MrzScannerGuideFragment.this);
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "App cần quyền để bật camera",
                    REQUEST_CODE_TAKE_PHOTO_PERMISSION,
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
            );
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_CAMERA_PERMISSION)
    private void navigateToQRCodeScanner() {
        if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.CAMERA)) {
            push(QRCodeFragment.newInstance());
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "App cần quyền để bật camera",
                    REQUEST_CODE_CAMERA_PERMISSION,
                    Manifest.permission.CAMERA
            );
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_CAMERA_PERMISSION)
    private void navigateToMrzScanner() {
        if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.CAMERA)) {
            push(MrzScannerFragment.newInstance());
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "App cần quyền để bật camera",
                    REQUEST_CODE_CAMERA_PERMISSION,
                    Manifest.permission.CAMERA
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mrz_scanner_guide, container, false);
    }

    private Bundle easyImageState = new Bundle();

    @NotNull
    @Override
    public Bundle restoreEasyImageState() {
        return easyImageState;
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_KEY, easyImageState);
    }

    @Override
    public void saveEasyImageState(@org.jetbrains.annotations.Nullable Bundle bundle) {
        easyImageState = bundle;
    }

    @Override
    public void onIdCardReader(String documentNumber, String dateOfBirth, String exprityDate) {
        push(NfcReaderFragment.newInstance(documentNumber, dateOfBirth, exprityDate, null, null));
    }

    @Override
    public void onCanNumberCardReader(String canNumber) {
        push(NfcReaderFragment.newInstance(null, null, null, null, null));
    }

}