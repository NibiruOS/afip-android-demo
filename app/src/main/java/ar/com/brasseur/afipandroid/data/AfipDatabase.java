package ar.com.brasseur.afipandroid.data;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import ar.com.system.afip.android.wsaa.data.CompanyDao;
import ar.com.system.afip.android.wsaa.data.DateTypeConverter;
import ar.com.system.afip.android.wsaa.data.TaxCategoryConverter;
import ar.com.system.afip.android.wsaa.data.model.Company;

@Database(entities = {Company.class},
        version = 1)
@TypeConverters({DateTypeConverter.class,
        TaxCategoryConverter.class})
public abstract class AfipDatabase extends RoomDatabase {
    public static final String DB_NAME = "afip.db";

    public abstract CompanyDao companyDao();
}
