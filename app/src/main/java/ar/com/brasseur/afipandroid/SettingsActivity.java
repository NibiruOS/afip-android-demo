package ar.com.brasseur.afipandroid;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import org.xmlpull.v1.XmlPullParser;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Provider;

import ar.com.brasseur.afipandroid.data.AfipDatabase;
import ar.com.brasseur.afipandroid.ioc.XmlPullParserProvider;
import ar.com.brasseur.afipandroid.ioc.XmlSerializerProvider;
import ar.com.system.afip.android.common.service.RetrofitBuilderProvider;
import ar.com.system.afip.android.wsaa.business.SerializerProvider;
import ar.com.system.afip.android.wsaa.business.SimpleXmlConverter;
import ar.com.system.afip.android.wsaa.data.RoomWsaaDao;
import ar.com.system.afip.android.wsaa.service.LoginCmsImpl;
import ar.com.system.afip.android.wsaa.service.RetrofitLoginCmsProvider;
import ar.com.system.afip.wsaa.business.api.WsaaManager;
import ar.com.system.afip.wsaa.business.impl.BouncyCastleWsaaManager;
import ar.com.system.afip.wsaa.data.api.CompanyInfo;
import ar.com.system.afip.wsaa.data.api.TaxCategory;
import ar.com.system.afip.wsaa.data.api.WsaaDao;
import ar.com.system.afip.wsaa.data.impl.HomoSetupDao;
import io.github.nibiruos.retrosoap.ServiceFactory;
import io.github.nibiruos.retrosoap.WsdlParser;

public class SettingsActivity extends AppCompatActivity {
    private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private Serializable id;

    EditText name;

    EditText unit;

    EditText cuit;

    EditText publicKey;

    EditText privateKey;

    EditText csr;

    EditText certificate;

    EditText grossIncome;

    EditText activityStartDate;

    EditText taxCategory;

    EditText address;

    EditText location;

    EditText alias;

    private WsaaDao wsaaDao;

    private WsaaManager wsaaManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show());
        name = findViewById(R.id.name);
        unit = findViewById(R.id.unit);
        cuit = findViewById(R.id.cuit);
        publicKey = findViewById(R.id.publicKey);
        privateKey = findViewById(R.id.privateKey);
        csr = findViewById(R.id.csr);
        certificate = findViewById(R.id.certificate);
        grossIncome = findViewById(R.id.grossIncome);
        activityStartDate = findViewById(R.id.activityStartDate);
        taxCategory = findViewById(R.id.taxCategory);
        address = findViewById(R.id.address);
        location = findViewById(R.id.location);
        alias = findViewById(R.id.alias);

        findViewById(R.id.buildKeys)
                .setOnClickListener(this::buildKeys);
        findViewById(R.id.buildCsr)
                .setOnClickListener(this::buildCsr);

        // TODO: reemplazar por inyeccion de dependencias
        new Thread(() -> {
            AfipDatabase afipDatabase = Room.databaseBuilder(getApplication(),
                    AfipDatabase.class,
                    AfipDatabase.DB_NAME).build();

            wsaaDao = new RoomWsaaDao(afipDatabase.companyDao());

            Provider<XmlPullParser> pullParserProvider = new XmlPullParserProvider();
            wsaaManager = new BouncyCastleWsaaManager(wsaaDao,
                    new HomoSetupDao(),
                    new LoginCmsImpl(new RetrofitLoginCmsProvider(new HomoSetupDao(),
                            new ServiceFactory(
                                    new WsdlParser(pullParserProvider),
                                    new RetrofitBuilderProvider(new XmlSerializerProvider(),
                                            pullParserProvider)))
                            .get()),
                    new SimpleXmlConverter(new SerializerProvider().get()));
            runOnUiThread(this::read);
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        read();
    }

    @Override
    protected void onPause() {
        super.onPause();
        new Thread(() -> {
            save();
        }).start();
    }

    private void read() {
        new Thread(() -> {
            if (wsaaDao != null) {
                CompanyInfo companyInfo = loadCompanyInfo();
                runOnUiThread(() -> {
                    id = companyInfo.getId();
                    name.setText(companyInfo.getName());
                    unit.setText(companyInfo.getUnit());
                    cuit.setText(companyInfo.getCuit());
                    publicKey.setText(companyInfo.getPublicKey());
                    privateKey.setText(companyInfo.getPrivateKey());
                    certificate.setText(companyInfo.getCertificate());
                    grossIncome.setText(companyInfo.getGrossIncome());
                    activityStartDate.setText(DATE_FORMAT.format(companyInfo.getActivityStartDate()));
                    taxCategory.setText(companyInfo.getTaxCategory().toString());
                    address.setText(companyInfo.getAddress());
                    location.setText(companyInfo.getLocation());
                    alias.setText(companyInfo.getAlias());
                });
            }
        }).start();
    }

    private CompanyInfo loadCompanyInfo() {
        CompanyInfo companyInfo = wsaaDao.loadActiveCompanyInfo();
        return companyInfo != null
                ? companyInfo
                : new CompanyInfo(0,
                "",
                true,
                "",
                "",
                "",
                "",
                "",
                "",
                new Date(),
                TaxCategory.MONOTRIBUTO,
                "",
                "",
                "");
    }

    private void save() {
        if (wsaaDao != null) {
            wsaaDao.saveCompanyInfo(buildCompanyInfo());
        }
    }

    private CompanyInfo buildCompanyInfo() {
        try {
            return new CompanyInfo(id,
                    name.getText().toString(),
                    true,
                    unit.getText().toString(),
                    cuit.getText().toString(),
                    publicKey.getText().toString(),
                    privateKey.getText().toString(),
                    certificate.getText().toString(),
                    grossIncome.getText().toString(),
                    DATE_FORMAT.parse(activityStartDate.getText().toString()),
                    TaxCategory.valueOf(taxCategory.getText().toString()),
                    address.getText().toString(),
                    location.getText().toString(),
                    alias.getText().toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void buildKeys(View view) {
        new Thread(() -> {
            save();
            wsaaManager.initializeKeys();
            read();
        }).start();
    }

    private void buildCsr(View view) {
        new Thread(() -> {
            save();
            String csrPem = wsaaManager.buildCertificateRequest();
            runOnUiThread(() -> csr.setText(csrPem));
        }).start();
    }
}
