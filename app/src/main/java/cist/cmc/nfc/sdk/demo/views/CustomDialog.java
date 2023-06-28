package cist.cmc.nfc.sdk.demo.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.List;

import cist.cmc.nfc.sdk.core.CP06Authenticate;
import cist.cmc.nfc.sdk.core.models.cp06_auth.CP06Request;
import cist.cmc.nfc.sdk.core.models.cp06_auth.CP06Response;
import cist.cmc.nfc.sdk.demo.R;
import cist.cmc.nfc.sdk.demo.api_service.APIClient;
import cist.cmc.nfc.sdk.demo.features.cp06_authendicate.CP06AuthListner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CustomDialog extends DialogFragment {

    private String TAG = CustomDialog.class.getSimpleName();

    public CustomDialog(CP06Request cp06Request, CP06AuthListner listner) {
        this.cp06Request = cp06Request;
        this.listner = listner;
    }

    private CP06Request cp06Request;
    private CP06AuthListner listner;
    private TextView txtTitle;
    private TextView txtMessage;
    private TextView btnOk;
    private Thread authThread;
    private LinearLayout loadingAuth;
    private LinearLayout loadingSuccess;
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.custom_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingAuth = view.findViewById(R.id.loading_auth);
        loadingSuccess = view.findViewById(R.id.loading_success);
        txtTitle = view.findViewById(R.id.txt_title);
        txtMessage = view.findViewById(R.id.txt_message);
        btnOk = view.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authThread = new Thread(this::cp06Authenticate, "Authenticate Thread");
        authThread.start();
    }

    private void cp06Authenticate() {
        Retrofit client = APIClient.getClient();
        CP06Authenticate cp06Auth = new CP06Authenticate(client, cp06Request);
        Call<List<CP06Response>> call = cp06Auth.cp06Authenticate();
        call.enqueue(new Callback<List<CP06Response>>() {
            @Override
            public void onResponse(Call<List<CP06Response>> call, Response<List<CP06Response>> response) {
                List<CP06Response> cp06ResponseList = response.body();
                int message = processMess(cp06ResponseList.get(0).getResult().getCode(), listner);
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        loadingAuth.setVisibility(View.GONE);
                        loadingSuccess.setVisibility(View.VISIBLE);
                        txtMessage.setText(message);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<CP06Response>> call, Throwable t) {
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listner.authError(false, R.string.error_auth);
                        loadingAuth.setVisibility(View.GONE);
                        loadingSuccess.setVisibility(View.VISIBLE);
                        txtMessage.setText(R.string.error_auth);
                    }
                });
            }
        });
    }

    private int processMess(String code, CP06AuthListner listner) {
        int requestCode = Integer.valueOf(code);
        switch (requestCode) {
            case 200:
                listner.authSuccess(true, R.string.auth_ok);
                return R.string.auth_ok;
            case 201:
                listner.authSuccess(false, R.string.not_enough);
                return R.string.not_enough;
            case 202:
                listner.authSuccess(false, R.string.account_is_not_active);
                return R.string.account_is_not_active;
            case 203:
                listner.authSuccess(false, R.string.wrong_account);
                return R.string.wrong_account;
            case 204:
                listner.authSuccess(false, R.string.system_error);
                return R.string.system_error;
            case 205:
                listner.authSuccess(false, R.string.wrong_parameter);
                return R.string.wrong_parameter;
            case 206:
                listner.authSuccess(false, R.string.id_card_not_found);
                return R.string.id_card_not_found;
            case 207:
                listner.authSuccess(false, R.string.device_not_found);
                return R.string.device_not_found;
            case 500:
                listner.authSuccess(false, R.string.method_not_support);
                return R.string.method_not_support;
            default:
                listner.authSuccess(false, R.string.unknown);
                return R.string.unknown;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
