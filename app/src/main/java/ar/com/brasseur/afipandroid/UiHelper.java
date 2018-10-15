package ar.com.brasseur.afipandroid;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.concurrent.Callable;

import javax.inject.Inject;

public class UiHelper {
    @FunctionalInterface
    public interface Consumer<T> {
        void accept(T arg);
    }

    private final Activity activity;

    @Inject
    public UiHelper(Activity activity) {
        this.activity = activity;
    }

    public void run(Runnable bgCallback) {
        run(() -> {
            bgCallback.run();
            return null;
        }, (v) -> {
        });
    }

    public <T> void run(Callable<T> bgCallback,
                        Consumer<T> uiCallback) {
        new Task<>(activity, bgCallback, uiCallback).execute();
    }

    private static class Result<T> {
        private final T value;
        private final Exception error;

        private Result(T value,
                       Exception error) {
            this.value = value;
            this.error = error;
        }
    }


    private static class Task<T>
            extends AsyncTask<Void, Void, Result<T>> {
        private final Activity activity;
        private final Callable<T> bgCallback;
        private final Consumer<T> uiCallback;

        private Task(Activity activity,
                     Callable<T> bgCallback,
                     Consumer<T> uiCallback) {
            this.activity = activity;
            this.bgCallback = bgCallback;
            this.uiCallback = uiCallback;
        }

        @Override
        protected Result<T> doInBackground(Void... voids) {
            try {
                return new Result<>(bgCallback.call(), null);
            } catch (Exception e) {
                return new Result<>(null, e);
            }
        }

        @Override
        protected void onPostExecute(Result<T> result) {
            if (result.error == null) {
                activity.runOnUiThread(() -> uiCallback.accept(result.value));
            } else {
                result.error.printStackTrace();
                Toast.makeText(activity,
                        result.error.getMessage(),
                        Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
}
