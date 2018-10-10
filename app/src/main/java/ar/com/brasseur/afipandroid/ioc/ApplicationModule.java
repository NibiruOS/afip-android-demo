package ar.com.brasseur.afipandroid.ioc;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;

@Module
public class ApplicationModule {
    private final Application application;

    public ApplicationModule(Application application) {
        this.application = checkNotNull(application);
    }

    @Provides
    public Application getApplication() {
        return this.application;
    }
}