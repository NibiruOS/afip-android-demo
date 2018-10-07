package ar.com.brasseur.afipandroid;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.base.Function;

import org.xmlpull.v1.XmlPullParser;

import javax.inject.Provider;

import ar.com.brasseur.afipandroid.data.AfipDatabase;
import ar.com.brasseur.afipandroid.ioc.XmlPullParserProvider;
import ar.com.brasseur.afipandroid.ioc.XmlSerializerProvider;
import ar.com.system.afip.android.common.service.RetrofitBuilderProvider;
import ar.com.system.afip.android.wsaa.business.SerializerProvider;
import ar.com.system.afip.android.wsaa.business.SimpleXmlConverter;
import ar.com.system.afip.android.wsaa.data.RoomCredentialsDao;
import ar.com.system.afip.android.wsaa.data.RoomWsaaDao;
import ar.com.system.afip.android.wsaa.service.LoginCmsImpl;
import ar.com.system.afip.android.wsaa.service.RetrofitLoginCmsProvider;
import ar.com.system.afip.android.wsfe.service.RetrofitServiceSoapProvider;
import ar.com.system.afip.android.wsfe.service.ServiceSoapImpl;
import ar.com.system.afip.wsaa.business.api.Service;
import ar.com.system.afip.wsaa.business.api.WsaaManager;
import ar.com.system.afip.wsaa.business.api.WsaaTemplate;
import ar.com.system.afip.wsaa.business.impl.BouncyCastleWsaaManager;
import ar.com.system.afip.wsaa.business.impl.WsaaTemplateImpl;
import ar.com.system.afip.wsaa.data.api.WsaaDao;
import ar.com.system.afip.wsaa.data.impl.HomoSetupDao;
import ar.com.system.afip.wsaa.service.api.Credentials;
import ar.com.system.afip.wsfe.service.api.FEAuthRequest;
import ar.com.system.afip.wsfe.service.api.ServiceSoap;
import io.github.nibiruos.retrosoap.ServiceFactory;
import io.github.nibiruos.retrosoap.WsdlParser;

public class ParametrosActivity extends AppCompatActivity {
    private WsaaDao wsaaDao;
    private WsaaTemplate wsaaTemplate;
    private ServiceSoap serviceSoap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametros);

        new Thread(() -> {
            AfipDatabase afipDatabase = Room.databaseBuilder(getApplication(),
                    AfipDatabase.class,
                    AfipDatabase.DB_NAME).build();

            wsaaDao = new RoomWsaaDao(afipDatabase.companyDao());

            Provider<XmlPullParser> pullParserProvider = new XmlPullParserProvider();
            WsaaManager wsaaManager = new BouncyCastleWsaaManager(wsaaDao,
                    new HomoSetupDao(),
                    new LoginCmsImpl(new RetrofitLoginCmsProvider(new HomoSetupDao(),
                            new ServiceFactory(
                                    new WsdlParser(pullParserProvider),
                                    new RetrofitBuilderProvider(new XmlSerializerProvider(),
                                            pullParserProvider)))
                            .get()),
                    new SimpleXmlConverter(new SerializerProvider().get()));

            wsaaTemplate = new WsaaTemplateImpl.FactoryImpl(wsaaManager, new RoomCredentialsDao(afipDatabase.companyDao()))
                    .create(Service.WSFE);

            serviceSoap = new ServiceSoapImpl(new RetrofitServiceSoapProvider(new HomoSetupDao(),
                    new ServiceFactory(
                            new WsdlParser(pullParserProvider),
                            new RetrofitBuilderProvider(new XmlSerializerProvider(),
                                    pullParserProvider)))
                    .get());

        }).start();

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
                if (wsaaTemplate == null || serviceSoap == null) {
                    return;
                }

                MethodDef<Object> methodDef = (MethodDef<Object>) methodDefs[pos];
                itemList.removeAllViews();
                new Thread(() -> {
                    Iterable<Object> items = wsaaTemplate
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
