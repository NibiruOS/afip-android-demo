package ar.com.brasseur.afipandroid;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import ar.com.system.afip.wsfe.service.api.FEAuthRequest;
import ar.com.system.afip.wsfe.service.api.IvaTipo;
import ar.com.system.afip.wsfe.service.api.IvaTipoResponse;
import ar.com.system.afip.wsfe.service.api.ServiceSoap;
import io.github.nibiruos.retrosoap.ServiceFactory;
import io.github.nibiruos.retrosoap.WsdlParser;

public class TiposIvaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipos_iva);

        new Thread(() -> {
            AfipDatabase afipDatabase = Room.databaseBuilder(getApplication(),
                    AfipDatabase.class,
                    AfipDatabase.DB_NAME).build();

            WsaaDao wsaaDao = new RoomWsaaDao(afipDatabase.companyDao());

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

            WsaaTemplate wsaaTemplate = new WsaaTemplateImpl.FactoryImpl(wsaaManager, new RoomCredentialsDao(afipDatabase.companyDao()))
                    .create(Service.WSFE);

            ServiceSoap serviceSoap = new ServiceSoapImpl(new RetrofitServiceSoapProvider(new HomoSetupDao(),
                    new ServiceFactory(
                            new WsdlParser(pullParserProvider),
                            new RetrofitBuilderProvider(new XmlSerializerProvider(),
                                    pullParserProvider)))
                    .get());

            IvaTipoResponse response = wsaaTemplate
                    .runAuhtenticated(credentials -> serviceSoap
                            .feParamGetTiposIva(FEAuthRequest
                                    .fromCredentials(credentials,
                                            wsaaDao.loadActiveCompanyInfo().getCuit())));

            runOnUiThread(() -> {
                LinearLayout taxList = findViewById(R.id.taxList);
                for (IvaTipo ivaTipo : response.getResultGet().getIvaTipo()) {
                    TextView item = new TextView(this);
                    item.setText(ivaTipo.getId() + " - " + ivaTipo.getDesc());
                    taxList.addView(item);
                }
            });
        }).start();


    }
}
