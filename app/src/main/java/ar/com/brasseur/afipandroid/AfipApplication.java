package ar.com.brasseur.afipandroid;

import ar.com.brasseur.afipandroid.ioc.ApplicationModule;
import ar.com.brasseur.afipandroid.ioc.DaggerAfipApplicationComponent;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

public class AfipApplication
        extends DaggerApplication {
    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAfipApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }
}