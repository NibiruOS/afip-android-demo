package ar.com.brasseur.afipandroid.ioc;

import ar.com.brasseur.afipandroid.ParametrosActivity;
import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent
public interface ParametrosActivitySubcomponent
        extends AndroidInjector<ParametrosActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<ParametrosActivity> {
    }
}