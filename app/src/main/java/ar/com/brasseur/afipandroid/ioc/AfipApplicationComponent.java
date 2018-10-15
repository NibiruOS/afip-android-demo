package ar.com.brasseur.afipandroid.ioc;

import javax.inject.Singleton;

import ar.com.brasseur.afipandroid.AfipApplication;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        AndroidModule.class,
        ActivityModule.class,
        ApplicationModule.class,
        BusinessModule.class,
        DatabaseModule.class,
        ServiceModule.class,
        ParametrosActivityModule.class,
        SettingsActivityModule.class,
})
public interface AfipApplicationComponent
        extends AndroidInjector<AfipApplication> {
    void inject(AfipApplication application);
}