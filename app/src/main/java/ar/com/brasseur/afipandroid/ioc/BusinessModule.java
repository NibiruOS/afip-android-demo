package ar.com.brasseur.afipandroid.ioc;

import ar.com.system.afip.wsaa.business.api.WsaaManager;
import ar.com.system.afip.wsaa.business.api.WsaaTemplate;
import ar.com.system.afip.wsaa.business.impl.BouncyCastleWsaaManager;
import ar.com.system.afip.wsaa.business.impl.WsaaTemplateImpl;
import dagger.Module;
import dagger.Provides;

@Module
public class BusinessModule {
    @Provides
    public WsaaManager provideWsaaManager(BouncyCastleWsaaManager manager) {
        return manager;
    }

    @Provides
    public WsaaTemplate.Factory provideWsaaTemplateFactory(WsaaTemplateImpl.FactoryImpl factory) {
        return factory;
    }
}
