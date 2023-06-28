package cist.cmc.nfc.sdk.demo.features.nfc_reader.presenter.impl;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import cist.cmc.nfc.sdk.core.nfc.IdCardReaderStatus;
import cist.cmc.nfc.sdk.demo.R;
import cist.cmc.nfc.sdk.demo.features.nfc_reader.presenter.NfcReaderPresenter;

public class NfcReaderPresenterImpl implements NfcReaderPresenter {
    private View view;
    private ProgressBar indicator;
    private TextView statusText;
    private CardView cccdMatTruoc;
    private CardView cardReader, cardReading;

    public NfcReaderPresenterImpl(View view) {
        this.view = view;
        this.indicator = view.findViewById(R.id.indicator);
        this.statusText = view.findViewById(R.id.statusText);
        this.cccdMatTruoc = view.findViewById(R.id.cccd_mat_truoc);
        this.cardReader = view.findViewById(R.id.cardReader);
        this.cardReading = view.findViewById(R.id.cardReading);

    }

    @Override
    public void onDestroyView() {

    }

    @Override
    public void onUpdateView(IdCardReaderStatus status) {
        this.view.setVisibility(View.VISIBLE);
        this.cardReader.setVisibility(View.GONE);
        this.cardReading.setVisibility(View.VISIBLE);
        this.indicator.setVisibility(View.VISIBLE);
        this.statusText.setVisibility(View.VISIBLE);
        this.cccdMatTruoc.setVisibility(View.GONE);
        switch (status) {
            case AccessingNfcChip:
                statusText.setText(R.string.status_access_chip);
                break;
            case ReadingPersonalData:
                statusText.setText(R.string.status_read_personal_data);
                break;
            case ReadingPhoto:
                statusText.setText(R.string.status_read_photo);
                break;
            case ReadingOptionData:
                statusText.setText(R.string.status_read_option_data);
                break;
            case Veryfication:
                statusText.setText(R.string.status_verification);
                break;
            case CheckFeature:
                statusText.setText(R.string.status_check_feature);
                break;
            case Error5001:
                indicator.setVisibility(View.INVISIBLE);
                statusText.setText(R.string.status_check_mrz_pass);
                break;
            case Error5002:
                indicator.setVisibility(View.INVISIBLE);
                statusText.setText(R.string.status_error_connect);
                break;
            case Error5003:
                indicator.setVisibility(View.INVISIBLE);
                statusText.setText(R.string.status_error_mrz_info);
                break;
            case Error5004:
                indicator.setVisibility(View.INVISIBLE);
                statusText.setText(R.string.status_error_chip_authentication);
                break;
            case Error5005:
                indicator.setVisibility(View.INVISIBLE);
                statusText.setText(R.string.status_error_license);
                break;
            case Error5006:
                indicator.setVisibility(View.INVISIBLE);
                statusText.setText(R.string.status_error_unknown);
                break;
            case Error5007:
                indicator.setVisibility(View.INVISIBLE);
                statusText.setText(R.string.status_error_can_number);
                break;
            case Error5008:
                indicator.setVisibility(View.INVISIBLE);
                statusText.setText(R.string.status_error_user_input_key);
                break;
            case Error5009:
                indicator.setVisibility(View.INVISIBLE);
                statusText.setText(R.string.status_error_read_personal_image);
                break;
            case Error5010:
                indicator.setVisibility(View.INVISIBLE);
                statusText.setText(R.string.status_error_create_certification);
                break;
            case Error5011:
                indicator.setVisibility(View.INVISIBLE);
                statusText.setText(R.string.status_error_bac);
                break;
            case Error5012:
                indicator.setVisibility(View.INVISIBLE);
                statusText.setText(R.string.status_error_pace);
                break;
            case Error5013:
                indicator.setVisibility(View.INVISIBLE);
                statusText.setText(R.string.status_error_card_access);
                break;
            case Error5014:
                indicator.setVisibility(View.INVISIBLE);
                statusText.setText(R.string.status_error_pass_access);
                break;
            case Success:
                view.setVisibility(View.GONE);
                break;
        }
    }
}
