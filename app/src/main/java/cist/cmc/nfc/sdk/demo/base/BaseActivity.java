package cist.cmc.nfc.sdk.demo.base;

import androidx.appcompat.app.AppCompatActivity;

import com.ncapdevi.fragnav.FragNavController;
import com.ncapdevi.fragnav.FragNavTransactionOptions;

import cist.cmc.nfc.sdk.demo.R;

public class BaseActivity extends AppCompatActivity {
    protected FragNavController navController;

    private FragNavTransactionOptions animationOptions() {
        FragNavTransactionOptions.Builder builder = new FragNavTransactionOptions.Builder();
        builder.setEnterAnimation(R.anim.slide_in_right);
        builder.setExitAnimation(R.anim.slide_out_left);
        builder.setPopEnterAnimation(R.anim.slide_in_left);
        builder.setPopExitAnimation(R.anim.slide_out_right);
        return builder.build();
    }

    protected void pop(boolean animated) {
        if (!navController.isRootFragment()) {
            if (animated) {
                navController.popFragment(animationOptions());
            } else {
                navController.popFragment();
            }
        }
    }
}
