package ar.com.brasseur.afipandroid.ioc;

import android.app.Activity;

import ar.com.brasseur.afipandroid.SettingsActivity;
import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

@Module(subcomponents = SettingsActivitySubcomponent.class)
abstract class SettingsActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(SettingsActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindLoginActivityInjectorFactory(SettingsActivitySubcomponent.Builder builder);
}