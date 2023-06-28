package cist.cmc.nfc.sdk.demo.di;

import androidx.fragment.app.FragmentManager;

import com.ncapdevi.fragnav.FragNavController;


public class AppModule {
    private static AppModule module = null;

    private FragNavController navController;

    public static AppModule setInstance(FragmentManager fragmentManager, int containerId) {
        if (module == null) {
            module = new AppModule();
            module.navController = new FragNavController(fragmentManager, containerId);
            module.navController.setFragmentHideStrategy(FragNavController.DETACH);
            module.navController.setCreateEager(true);
        }

        return module;
    }

    public static AppModule getInstance() {
        return module;
    }

    private AppModule() {
    }

    public FragNavController getNavController() {
        return navController;
    }
}
