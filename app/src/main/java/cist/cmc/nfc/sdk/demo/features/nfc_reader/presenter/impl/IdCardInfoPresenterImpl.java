package cist.cmc.nfc.sdk.demo.features.nfc_reader.presenter.impl;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import cist.cmc.nfc.sdk.core.models.EDocument;
import cist.cmc.nfc.sdk.core.models.OptionPersonal;
import cist.cmc.nfc.sdk.core.nfc.extend.FeatureStatus;
import cist.cmc.nfc.sdk.core.nfc.extend.VerificationStatus;
import cist.cmc.nfc.sdk.demo.R;
import cist.cmc.nfc.sdk.demo.features.nfc_reader.presenter.IdCardInfoPresenter;

public class IdCardInfoPresenterImpl implements IdCardInfoPresenter {
    public View view;
    private ImageView ivPhoto;
    private TextView valuePersonalName;
    private TextView valueCCCDNumber;
    private TextView valueDateOfBirth;
    private TextView valueSex;
    private TextView valueNationality;
    private TextView valueNation;
    private TextView valueReligion;
    private TextView valueDistrict;
    private TextView valuePermanentAddress;
    private TextView valueIdentifyingCharacteristics;
    private TextView valueDateRange;
    private TextView valueDateExpiration;
    private TextView valueFatherName;
    private TextView valueMotherName;
    private TextView valueHusbandAndWifeName;
    private TextView valueCMNDNumber;

    private CardView card_view_warning;
    private TextView textWarningTitle;
    private TextView textWarningMessage;
    private TableRow row_bac;
    private TableRow row_active;
    private TableRow row_pace;
    private TableRow row_chip;
    private TableRow row_eac;
    private ImageView value_bac;
    private ImageView value_pace;
    private ImageView value_passive;
    private ImageView value_active;
    private ImageView value_document_signing;
    private ImageView value_country_signing;
    private ImageView value_chip;
    private ImageView value_eac;

    public IdCardInfoPresenterImpl(View view) {
        this.view = view;
        ivPhoto = view.findViewById(R.id.view_photo);
        valueCCCDNumber = view.findViewById(R.id.value_cccd_number);
        valuePersonalName = view.findViewById(R.id.value_personal_name);
        valueDateOfBirth = view.findViewById(R.id.value_date_of_birth);
        valueSex = view.findViewById(R.id.value_sex);
        valueNationality = view.findViewById(R.id.value_nationality);
        valueNation = view.findViewById(R.id.value_nation);
        valueReligion = view.findViewById(R.id.value_religion);
        valueDistrict = view.findViewById(R.id.value_district);
        valuePermanentAddress = view.findViewById(R.id.value_permanent_address);
        valueIdentifyingCharacteristics = view.findViewById(R.id.value_identifying_characteristics);
        valueDateRange = view.findViewById(R.id.value_dateRange);
        valueDateExpiration = view.findViewById(R.id.value_date_expiration);
        valueFatherName = view.findViewById(R.id.value_father_name);
        valueMotherName = view.findViewById(R.id.value_mother_name);
        valueHusbandAndWifeName = view.findViewById(R.id.value_husband_an_wife_name);
        valueCMNDNumber = view.findViewById(R.id.value_CMND_number);

        card_view_warning = view.findViewById(R.id.card_view_warning);
        textWarningTitle = view.findViewById(R.id.textWarningTitle);
        textWarningMessage = view.findViewById(R.id.textWarningMessage);
        row_bac = view.findViewById(R.id.row_bac);
        row_active = view.findViewById(R.id.row_active);
        row_pace = view.findViewById(R.id.row_pace);
        row_chip = view.findViewById(R.id.row_chip);
        row_eac = view.findViewById(R.id.row_eac);
        value_bac = view.findViewById(R.id.value_bac);
        value_pace = view.findViewById(R.id.value_pace);
        value_passive = view.findViewById(R.id.value_passive);
        value_active = view.findViewById(R.id.value_active);
        value_document_signing = view.findViewById(R.id.value_document_signing);
        value_country_signing = view.findViewById(R.id.value_country_signing);
        value_chip = view.findViewById(R.id.value_chip);
        value_eac = view.findViewById(R.id.value_eac);

    }

    @Override
    public void onDestroyView() {

    }

    @Override
    public void onUpdateView(EDocument document) {
        view.setVisibility(View.VISIBLE);
        refreshData(document);
    }

    private void refreshData(EDocument document) {
        if (document == null) {
            Toast.makeText(view.getContext(), "Mã mrz không hợp lệ", Toast.LENGTH_SHORT).show();
        } else if (document.getPersonDetails() == null) {
            Toast.makeText(view.getContext(), "Mã mrz không hợp lệ", Toast.LENGTH_SHORT).show();
        } else {
            ivPhoto.setImageBitmap(document.getPersonDetails().getFaceImage());
            OptionPersonal personal = document.getOptionPerson();
            if (personal != null) {
                valuePersonalName.setText(document.getOptionPerson().getName());
                valueCCCDNumber.setText(document.getOptionPerson().getCCCDNumber());
                valueDateOfBirth.setText(document.getOptionPerson().getDateOfBirth());
                valueSex.setText(document.getOptionPerson().getSex());
                valueNationality.setText(document.getOptionPerson().getNationality());
                valueNation.setText(document.getOptionPerson().getNation());
                valueReligion.setText(document.getOptionPerson().getReligion());
                valueDistrict.setText(document.getOptionPerson().getDistrict());
                valuePermanentAddress.setText(document.getOptionPerson().getPermanentAddress());
                valueIdentifyingCharacteristics.setText(document.getOptionPerson().getIdentifyingCharacteristics());
                valueDateRange.setText(document.getOptionPerson().getDateOfIssue());
                valueDateExpiration.setText(document.getOptionPerson().getDateExpiration());
                valueFatherName.setText(document.getOptionPerson().getFatherName());
                valueMotherName.setText(document.getOptionPerson().getMotherName());
                valueHusbandAndWifeName.setText(document.getOptionPerson().getHusbandOrWifeName());
                valueCMNDNumber.setText(document.getOptionPerson().getCMNDNumber());
            }
            displayAuthenticationStatus(document);
            displayWarningTitle(document);
        }
    }

    private void displayWarningTitle(EDocument eDocument) {
        VerificationStatus verificationStatus = eDocument.getVerificationStatus();
        FeatureStatus featureStatus = eDocument.getFeatureStatus();
        int colorCard = R.color.teal_700;
        String message = "";
        String title = "";
        if (featureStatus.getHasCA().equals(FeatureStatus.Verdict.PRESENT) == true) {
            if ((verificationStatus.getCa().equals(VerificationStatus.Verdict.SUCCEEDED) == true) &&
                    (verificationStatus.getHt().equals(VerificationStatus.Verdict.SUCCEEDED) == true) &&
                    (verificationStatus.getCs().equals(VerificationStatus.Verdict.SUCCEEDED) == true)) {
                colorCard = R.color.teal_700;
                title = view.getResources().getString(R.string.document_valid_passport);
                message = view.getResources().getString(R.string.document_chip_content_success);
            } else if (verificationStatus.getCa().equals(VerificationStatus.Verdict.FAILED) == true) {
                //Chip authentication failed
                colorCard = android.R.color.holo_red_light;
                title = view.getResources().getString(R.string.document_invalid_passport);
                message = view.getResources().getString(R.string.document_chip_failure);
            } else if (verificationStatus.getHt().equals(VerificationStatus.Verdict.FAILED) == true) {
                //Document information
                colorCard = android.R.color.holo_red_light;
                title = view.getResources().getString(R.string.document_invalid_passport);
                message = view.getResources().getString(R.string.document_document_failure);
            } else if (verificationStatus.getCs().equals(VerificationStatus.Verdict.FAILED) == true) {
                //CSCA information
                colorCard = android.R.color.holo_red_light;
                title = view.getResources().getString(R.string.document_invalid_passport);
                message = view.getResources().getString(R.string.document_csca_failure);
            } else {
                //Unknown
                colorCard = android.R.color.darker_gray;
                title = view.getResources().getString(R.string.document_unknown_passport_title);
                message = view.getResources().getString(R.string.document_unknown_passport_message);
            }
        } else if (featureStatus.getHasCA().equals(FeatureStatus.Verdict.NOT_PRESENT) == true) {
            if (verificationStatus.getHt().equals(VerificationStatus.Verdict.SUCCEEDED) == true) {
                //Document information is fine
                colorCard = android.R.color.holo_green_light;
                title = view.getResources().getString(R.string.document_valid_passport);
                message = view.getResources().getString(R.string.document_content_success);
            } else if (verificationStatus.getHt().equals(VerificationStatus.Verdict.FAILED) == true) {
                //Document information
                colorCard = android.R.color.holo_red_light;
                title = view.getResources().getString(R.string.document_invalid_passport);
                message = view.getResources().getString(R.string.document_document_failure);
            } else if (verificationStatus.getCs().equals(VerificationStatus.Verdict.FAILED) == true) {
                //CSCA information
                colorCard = android.R.color.holo_red_light;
                title = view.getResources().getString(R.string.document_invalid_passport);
                message = view.getResources().getString(R.string.document_csca_failure);
            }
        } else {
            //Unknown
            colorCard = android.R.color.darker_gray;
            title = view.getResources().getString(R.string.document_unknown_passport_title);
            message = view.getResources().getString(R.string.document_unknown_passport_message);
        }
        card_view_warning.setCardBackgroundColor(view.getContext().getResources().getColor(colorCard));
        textWarningTitle.setText(message);
        textWarningMessage.setText(title);
    }

    private void displayAuthenticationStatus(EDocument eDocument) {
        VerificationStatus verificationStatus = eDocument.getVerificationStatus();
        FeatureStatus featureStatus = eDocument.getFeatureStatus();
        if (featureStatus.getHasBAC().equals(FeatureStatus.Verdict.PRESENT) == true) {
            row_bac.setVisibility(View.VISIBLE);
        } else {
            row_bac.setVisibility(View.GONE);
        }

        if (featureStatus.getHasAA().equals(FeatureStatus.Verdict.PRESENT) == true) {
            row_active.setVisibility(View.VISIBLE);
        } else {
            row_active.setVisibility(View.GONE);
        }

        if (featureStatus.getHasSAC().equals(FeatureStatus.Verdict.PRESENT) == true) {
            row_pace.setVisibility(View.VISIBLE);
        } else {
            row_pace.setVisibility(View.GONE);
        }

        if (featureStatus.getHasCA().equals(FeatureStatus.Verdict.PRESENT) == true) {
            row_chip.setVisibility(View.VISIBLE);
        } else {
            row_chip.setVisibility(View.GONE);
        }

        if (featureStatus.getHasEAC().equals(FeatureStatus.Verdict.PRESENT) == true) {
            row_eac.setVisibility(View.VISIBLE);
        } else {
            row_eac.setVisibility(View.GONE);
        }

        displayVerificationStatusIcon(value_bac, verificationStatus.getBac());
        displayVerificationStatusIcon(value_pace, verificationStatus.getSac());
        displayVerificationStatusIcon(value_passive, verificationStatus.getHt());
        displayVerificationStatusIcon(value_active, verificationStatus.getAa());
        displayVerificationStatusIcon(value_document_signing, verificationStatus.getDs());
        displayVerificationStatusIcon(value_country_signing, verificationStatus.getCs());
        displayVerificationStatusIcon(value_chip, verificationStatus.getCa());
        displayVerificationStatusIcon(value_eac, verificationStatus.getEac());
    }

    private void displayVerificationStatusIcon(ImageView imageView, VerificationStatus.Verdict verdicts) {
        VerificationStatus.Verdict verdict = verdicts;

        if (verdict == null) {
            verdict = VerificationStatus.Verdict.UNKNOWN;
        }
        int resourceIconId;
        int resourceColorId;
        if (verdict.equals(VerificationStatus.Verdict.SUCCEEDED) == true) {
            resourceIconId = R.drawable.ic_check_circle_outline;
            resourceColorId = android.R.color.holo_green_light;

        } else if (verdict.equals(VerificationStatus.Verdict.FAILED) == true) {
            resourceIconId = R.drawable.ic_close_circle_outline;
            resourceColorId = android.R.color.holo_red_light;

        } else if (verdict.equals(VerificationStatus.Verdict.NOT_PRESENT) == true) {
            resourceIconId = R.drawable.ic_close_circle_outline;
            resourceColorId = android.R.color.darker_gray;

        } else if (verdict.equals(VerificationStatus.Verdict.NOT_CHECKED) == true) {
            resourceIconId = R.drawable.ic_help_circle_outline;
            resourceColorId = android.R.color.holo_orange_light;

        } else if (verdict.equals(VerificationStatus.Verdict.UNKNOWN) == true) {
            resourceIconId = R.drawable.ic_close_circle_outline;
            resourceColorId = android.R.color.darker_gray;

        } else {
            resourceIconId = R.drawable.ic_close_circle_outline;
            resourceColorId = android.R.color.darker_gray;

        }

        imageView.setImageResource(resourceIconId);
        imageView.setColorFilter(ContextCompat.getColor(view.getContext(), resourceColorId));
    }
}
