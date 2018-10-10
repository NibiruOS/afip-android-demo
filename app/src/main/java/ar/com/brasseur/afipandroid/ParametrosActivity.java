package ar.com.brasseur.afipandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.base.Function;

import javax.inject.Inject;

import ar.com.system.afip.wsaa.business.api.Service;
import ar.com.system.afip.wsaa.business.api.WsaaTemplate;
import ar.com.system.afip.wsaa.data.api.WsaaDao;
import ar.com.system.afip.wsaa.service.api.Credentials;
import ar.com.system.afip.wsfe.service.api.FEAuthRequest;
import ar.com.system.afip.wsfe.service.api.ServiceSoap;
import dagger.android.AndroidInjection;

public class ParametrosActivity extends AppCompatActivity {
    @Inject
    WsaaDao wsaaDao;
    @Inject
    WsaaTemplate.Factory wsaaTemplateFactory;
    @Inject
    ServiceSoap serviceSoap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(() -> {
            AndroidInjection.inject(this);
        }).start();
        setContentView(R.layout.activity_parametros);

        Spinner metodo = findViewById(R.id.method);
        MethodDef<?> methodDefs[] = new MethodDef[]{
                new MethodDef<>("feParamGetTiposIva",
                        credentials -> serviceSoap
                                .feParamGetTiposIva(feAuthRequest(credentials))
                                .getResultGet()
                                .getIvaTipo(),
                        item -> item.getId() + " - " + item.getDesc()),
                new MethodDef<>("feParamGetTiposCbte",
                        credentials -> serviceSoap
                                .feParamGetTiposCbte(feAuthRequest(credentials))
                                .getResultGet()
                                .getCbteTipo(),
                        item -> item.getId() + " - " + item.getDesc()),
                new MethodDef<>("feParamGetTiposConcepto",
                        credentials -> serviceSoap
                                .feParamGetTiposConcepto(feAuthRequest(credentials))
                                .getResultGet()
                                .getConceptoTipo(),
                        item -> item.getId() + " - " + item.getDesc()),
                new MethodDef<>("feParamGetTiposDoc",
                        credentials -> serviceSoap
                                .feParamGetTiposDoc(feAuthRequest(credentials))
                                .getResultGet()
                                .getDocTipo(),
                        item -> item.getId() + " - " + item.getDesc()),
                new MethodDef<>("feParamGetTiposMonedas",
                        credentials -> serviceSoap
                                .feParamGetTiposMonedas(feAuthRequest(credentials))
                                .getResultGet()
                                .getMoneda(),
                        item -> item.getId() + " - " + item.getDesc()),
                new MethodDef<>("feParamGetTiposOpcional",
                        credentials -> serviceSoap
                                .feParamGetTiposOpcional(feAuthRequest(credentials))
                                .getResultGet()
                                .getOpcionalTipo(),
                        item -> item.getId() + " - " + item.getDesc()),
                new MethodDef<>("feParamGetTiposPaises",
                        credentials -> serviceSoap
                                .feParamGetTiposPaises(feAuthRequest(credentials))
                                .getResultGet()
                                .getPaisTipo(),
                        item -> item.getId() + " - " + item.getDesc()),
                new MethodDef<>("feParamGetTiposTributos",
                        credentials -> serviceSoap
                                .feParamGetTiposTributos(feAuthRequest(credentials))
                                .getResultGet()
                                .getTributoTipo(),
                        item -> item.getId() + " - " + item.getDesc()),
        };

        LinearLayout itemList = findViewById(R.id.list);

        metodo.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                methodDefs));

        metodo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (wsaaTemplateFactory == null || serviceSoap == null) {
                    return;
                }

                MethodDef<Object> methodDef = (MethodDef<Object>) methodDefs[pos];
                itemList.removeAllViews();
                new Thread(() -> {
                    Iterable<Object> items = wsaaTemplateFactory.create(Service.WSFE)
                            .runAuhtenticated(credentials -> methodDef.supplier
                                    .apply(credentials));

                    runOnUiThread(() -> {
                        for (Object item : items) {
                            TextView itemTv = new TextView(ParametrosActivity.this);
                            itemTv.setText(methodDef.toString.apply(item));
                            itemList.addView(itemTv);
                        }
                    });
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
        private final Function<Credentials, Iterable<T>> supplier;
        private final Function<T, String> toString;

        private MethodDef(String name,
                          Function<Credentials, Iterable<T>> supplier, Function<T, String> toString) {
            this.name = name;
            this.supplier = supplier;
            this.toString = toString;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private FEAuthRequest feAuthRequest(Credentials credentials) {
        return FEAuthRequest
                .fromCredentials(credentials,
                        wsaaDao.loadActiveCompanyInfo().getCuit());
    }
}
