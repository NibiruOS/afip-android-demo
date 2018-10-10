package ar.com.brasseur.afipandroid.data;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.net.Credentials;

import ar.com.system.afip.android.wsaa.data.converter.DateTypeConverter;
import ar.com.system.afip.android.wsaa.data.converter.ServiceConverter;
import ar.com.system.afip.android.wsaa.data.converter.TaxCategoryConverter;
import ar.com.system.afip.android.wsaa.data.dao.RoomCompanyDao;
import ar.com.system.afip.android.wsaa.data.dao.RoomCredentialsDao;
import ar.com.system.afip.android.wsaa.data.model.Company;
import ar.com.system.afip.android.wsaa.data.model.ServiceCredentials;

@Database(entities = {Company.class,
        ServiceCredentials.class},
        version = 1)
@TypeConverters({DateTypeConverter.class,
        ServiceConverter.class,
        TaxCategoryConverter.class})
public abstract class AfipDatabase extends RoomDatabase {
    public static final String DB_NAME = "afip.db";

    public abstract RoomCompanyDao companyDao();

    public abstract RoomCredentialsDao credentialsDao();
}
