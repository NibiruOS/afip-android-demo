package ar.com.brasseur.afipandroid.ioc;

import android.app.Application;
import android.arch.persistence.room.Room;

import ar.com.brasseur.afipandroid.data.AfipDatabase;
import ar.com.system.afip.android.wsaa.data.CredentialsDaoImpl;
import ar.com.system.afip.android.wsaa.data.WsaaDaoImpl;
import ar.com.system.afip.android.wsaa.data.dao.RoomCompanyDao;
import ar.com.system.afip.android.wsaa.data.dao.RoomCredentialsDao;
import ar.com.system.afip.wsaa.data.api.CredentialsDao;
import ar.com.system.afip.wsaa.data.api.SetupDao;
import ar.com.system.afip.wsaa.data.api.WsaaDao;
import ar.com.system.afip.wsaa.data.impl.HomoSetupDao;
import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {
    @Provides
    public AfipDatabase providesDatabase(Application application) {
        return Room.databaseBuilder(application, AfipDatabase.class,
                AfipDatabase.DB_NAME).build();
    }

    @Provides
    public RoomCompanyDao provideRoomCompanyDao(AfipDatabase database) {
        return database.companyDao();
    }

    @Provides
    public RoomCredentialsDao provideRoomCredentialsDao(AfipDatabase database) {
        return database.credentialsDao();
    }

    @Provides
    public WsaaDao provideWsaaDao(WsaaDaoImpl dao) {
        return dao;
    }

    @Provides
    public CredentialsDao provideCredentialsDao(CredentialsDaoImpl dao) {
        return dao;
    }

    @Provides
    public SetupDao provideSetupDao() {
        return new HomoSetupDao();
    }

}