package ar.com.brasseur.afipandroid.ioc;

import android.app.Activity;

import ar.com.brasseur.afipandroid.ParametrosActivity;
import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

@Module(subcomponents = ParametrosActivitySubcomponent.class)
abstract class ParametrosActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(ParametrosActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindLoginActivityInjectorFactory(ParametrosActivitySubcomponent.Builder builder);
}