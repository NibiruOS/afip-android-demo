package ar.com.brasseur.afipandroid;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Inject;

import ar.com.system.afip.wsaa.business.api.WsaaManager;
import ar.com.system.afip.wsaa.data.api.CompanyInfo;
import ar.com.system.afip.wsaa.data.api.TaxCategory;
import ar.com.system.afip.wsaa.data.api.WsaaDao;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;

public class SettingsActivity extends AppCompatActivity {
    private Serializable id;

    @BindView(R.id.name)
    EditText name;

    @BindView(R.id.unit)
    EditText unit;

    @BindView(R.id.cuit)
    EditText cuit;

    @BindView(R.id.publicKey)
    EditText publicKey;

    @BindView(R.id.privateKey)
    EditText privateKey;

    @BindView(R.id.csr)
    EditText csr;

    @BindView(R.id.certificate)
    EditText certificate;

    @BindView(R.id.grossIncome)
    EditText grossIncome;

    @BindView(R.id.activityStartDate)
    EditDate activityStartDate;

    @BindView(R.id.taxCategory)
    Spinner taxCategory;

    @BindView(R.id.address)
    EditText address;

    @BindView(R.id.location)
    EditText location;

    @BindView(R.id.alias)
    EditText alias;

    @Inject
    WsaaDao wsaaDao;

    UiHelper uiHelper;

    @Inject
    WsaaManager wsaaManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiHelper(this);
        uiHelper.run(() -> AndroidInjection.inject(this));

        setContentView(R.layout.activity_settings);

        TabLayout tab = findViewById(R.id.tab);
        FrameLayout content = findViewById(R.id.content);
        addTab(tab, content, R.string.company, R.layout.settings_company, View.VISIBLE);
        addTab(tab, content, R.string.public_key, R.layout.settings_public_key);
        addTab(tab, content, R.string.private_key, R.layout.settings_private_key);
        addTab(tab, content, R.string.certificate, R.layout.settings_certificate);
        addTab(tab, content, R.string.csr, R.layout.settings_csr);
        addTab(tab, content, R.string.actions, R.layout.settings_actions);

        ButterKnife.bind(this);

        taxCategory.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                TaxCategory.values()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        read();
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiHelper.run(this::save);
    }

    private void read() {
        uiHelper.run(() -> wsaaDao != null
                        ? loadCompanyInfo()
                        : null,
                (companyInfo) -> {
                    id = companyInfo.getId();
                    name.setText(companyInfo.getName());
                    unit.setText(companyInfo.getUnit());
                    cuit.setText(companyInfo.getCuit());
                    publicKey.setText(companyInfo.getPublicKey());
                    privateKey.setText(companyInfo.getPrivateKey());
                    certificate.setText(companyInfo.getCertificate());
                    grossIncome.setText(companyInfo.getGrossIncome());
                    activityStartDate.setDate(companyInfo.getActivityStartDate());
                    taxCategory.setSelection(companyInfo.getTaxCategory()
                            .ordinal());
                    address.setText(companyInfo.getAddress());
                    location.setText(companyInfo.getLocation());
                    alias.setText(companyInfo.getAlias());
                });
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
        return new CompanyInfo(id,
                name.getText().toString(),
                true,
                unit.getText().toString(),
                cuit.getText().toString(),
                publicKey.getText().toString(),
                privateKey.getText().toString(),
                certificate.getText().toString(),
                grossIncome.getText().toString(),
                activityStartDate.getDate(),
                TaxCategory.values()[taxCategory.getSelectedItemPosition()],
                address.getText().toString(),
                location.getText().toString(),
                alias.getText().toString());
    }

    @OnClick(R.id.buildKeys)
    void buildKeys() {
        uiHelper.run(() -> {
            save();
            wsaaManager.initializeKeys();
            read();
        });
    }

    @OnClick(R.id.buildCsr)
    void buildCsr() {
        uiHelper.run(() -> {
            save();
            return wsaaManager.buildCertificateRequest();
        }, (csrPem) -> csr.setText(csrPem));
    }


    private void addTab(TabLayout tabLayout,
                        FrameLayout detailContainer,
                        int title,
                        int panel) {
        addTab(tabLayout, detailContainer, title, panel, View.GONE);
    }

    private void addTab(TabLayout tabLayout,
                        FrameLayout detailContainer,
                        int title,
                        int panel,
                        int visibility) {
        View panelView = getLayoutInflater().inflate(panel, null);
        panelView.setVisibility(visibility);
        detailContainer.addView(panelView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        TabLayout.Tab tab = tabLayout.newTab()
                .setText(title);
        tabLayout.addTab(tab);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab aTab) {
                if (aTab == tab) {
                    panelView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab aTab) {
                if (aTab == tab) {
                    panelView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
}
