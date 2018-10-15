package ar.com.brasseur.afipandroid;

import ar.com.brasseur.afipandroid.ioc.ActivityModule;
import ar.com.brasseur.afipandroid.ioc.ApplicationModule;
import ar.com.brasseur.afipandroid.ioc.DaggerAfipApplicationComponent;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

public class AfipApplication
        extends DaggerApplication {
    private final ActivityModule activityModule = new ActivityModule();

    @Override
    public void onCreate() {
        registerActivityLifecycleCallbacks(activityModule);
        super.onCreate();
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAfipApplicationComponent.builder()
                .activityModule(activityModule)
                .applicationModule(new ApplicationModule(this))
                .build();
    }
}