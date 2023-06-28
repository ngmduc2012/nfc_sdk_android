package cist.cmc.nfc.sdk.demo.base;

import androidx.fragment.app.Fragment;

import com.ncapdevi.fragnav.FragNavController;
import com.ncapdevi.fragnav.FragNavTransactionOptions;

import cist.cmc.nfc.sdk.demo.R;
import cist.cmc.nfc.sdk.demo.di.AppModule;

public class BaseFragment extends Fragment {
    public FragNavController navController = AppModule.getInstance().getNavController();


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

    protected void push(Fragment fragment) {
        navController.pushFragment(fragment, animationOptions());
    }
}
