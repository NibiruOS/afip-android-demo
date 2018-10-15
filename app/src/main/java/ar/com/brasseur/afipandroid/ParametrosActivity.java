package ar.com.brasseur.afipandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Function;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import ar.com.system.afip.wsfe.business.api.WsfeManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import dagger.android.AndroidInjection;

public class ParametrosActivity extends AppCompatActivity {
    @Inject
    WsfeManager wsfeManager;

    @BindView(R.id.list)
    LinearLayout itemList;

    @BindView(R.id.method)
    Spinner metodo;

    MethodDef<?> methodDefs[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(() -> {
            AndroidInjection.inject(this);
        }).start();
        setContentView(R.layout.activity_parametros);
        ButterKnife.bind(this);

        methodDefs = new MethodDef[]{
                new MethodDef<>("feParamGetTiposIva",
                        () -> wsfeManager
                                .feParamGetTiposIva(),
                        item -> item.getId() + " - " + item.getDesc()),
                new MethodDef<>("feParamGetTiposCbte",
                        () -> wsfeManager
                                .feParamGetTiposCbte(),
                        item -> item.getId() + " - " + item.getDesc()),
                new MethodDef<>("feParamGetTiposConcepto",
                        () -> wsfeManager
                                .feParamGetTiposConcepto(),
                        item -> item.getId() + " - " + item.getDesc()),
                new MethodDef<>("feParamGetTiposDoc",
                        () -> wsfeManager
                                .feParamGetTiposDoc(),
                        item -> item.getId() + " - " + item.getDesc()),
                new MethodDef<>("feParamGetTiposMonedas",
                        () -> wsfeManager
                                .feParamGetTiposMonedas(),
                        item -> item.getId() + " - " + item.getDesc()),
                new MethodDef<>("feParamGetTiposOpcional",
                        () -> wsfeManager
                                .feParamGetTiposOpcional(),
                        item -> item.getId() + " - " + item.getDesc()),
                new MethodDef<>("feParamGetTiposPaises",
                        () -> wsfeManager
                                .feParamGetTiposPaises(),
                        item -> item.getId() + " - " + item.getDesc()),
                new MethodDef<>("feParamGetTiposTributos",
                        () -> wsfeManager
                                .feParamGetTiposTributos(),
                        item -> item.getId() + " - " + item.getDesc()),
        };

        metodo.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                methodDefs));

        metodo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (wsfeManager == null) {
                    return;
                }

                MethodDef<Object> methodDef = (MethodDef<Object>) methodDefs[pos];
                itemList.removeAllViews();
                new Thread(() -> {
                    try {
                        Iterable<Object> items = methodDef.supplier.call();

                        runOnUiThread(() -> {
                            for (Object item : items) {
                                TextView itemTv = new TextView(ParametrosActivity.this);
                                itemTv.setText(methodDef.toString.apply(item));
                                itemList.addView(itemTv);
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(ParametrosActivity.this,
                                e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                }).start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                itemList.removeAllViews();
            }
        });
    }

    private static class MethodDef<T> {
        private final String name;
        private final Callable<Iterable<T>> supplier;
        private final Function<T, String> toString;

        private MethodDef(String name,
                          Callable<Iterable<T>> supplier, Function<T, String> toString) {
            this.name = name;
            this.supplier = supplier;
            this.toString = toString;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
