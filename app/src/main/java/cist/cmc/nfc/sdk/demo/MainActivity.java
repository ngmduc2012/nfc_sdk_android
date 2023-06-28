package cist.cmc.nfc.sdk.demo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.ncapdevi.fragnav.FragNavController;

import java.util.Collections;

import cist.cmc.nfc.sdk.demo.base.BaseActivity;
import cist.cmc.nfc.sdk.demo.di.AppModule;
import cist.cmc.nfc.sdk.demo.features.input_pass.InputDialogListener;
import cist.cmc.nfc.sdk.demo.features.mrz_scanner.MrzScannerFragment;
import cist.cmc.nfc.sdk.demo.features.mrz_scanner_guide.MrzScannerGuideFragment;
import cist.cmc.nfc.sdk.demo.features.nfc_reader.NfcReaderFragment;

public class MainActivity extends BaseActivity {
    public AppModule appModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setUpDI();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpNavController(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(getLayoutInflater().inflate(R.layout.action_bar_home, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                ));

    }

    private void setUpDI() {
        appModule = AppModule.setInstance(getSupportFragmentManager(), R.id.container);
        navController = appModule.getNavController();
    }

    public void setUpNavController(Bundle savedInstanceState) {
        Fragment fragment = MrzScannerGuideFragment.newInstance();
        navController.setRootFragments(Collections.singletonList(fragment));
        navController.initialize(FragNavController.TAB1, savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (navController.getCurrentFrag() instanceof NfcReaderFragment) {
            ((NfcReaderFragment) navController.getCurrentFrag())
                    .onNewIntent(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (!navController.isRootFragment()) {
            if (navController.getCurrentFrag() instanceof NfcReaderFragment) {
                MrzScannerFragment.isSuccess = false;
                navController.replaceFragment(MrzScannerGuideFragment.newInstance());
                return;
            }
            pop(true);
        } else {
            finishAfterTransition();
        }
    }
}