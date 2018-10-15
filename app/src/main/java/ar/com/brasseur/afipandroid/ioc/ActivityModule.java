package ar.com.brasseur.afipandroid.ioc;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule
        implements Application.ActivityLifecycleCallbacks {
    private WeakReference<Activity> currentActivity;

    @Provides
    public Activity provideActivity() {
        Activity activity = currentActivity.get();
        if (activity == null) {
            throw new IllegalStateException("No current activity");
        }
        return activity;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        currentActivity = new WeakReference<>(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = new WeakReference<>(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = new WeakReference<>(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
