package ar.com.brasseur.afipandroid.ioc;

import ar.com.system.afip.android.common.service.RetrosoapExceptionConverter;
import ar.com.system.afip.common.service.ExceptionConverter;
import ar.com.system.afip.wsaa.business.api.WsaaManager;
import ar.com.system.afip.wsaa.business.api.WsaaTemplate;
import ar.com.system.afip.wsaa.business.impl.BouncyCastleWsaaManager;
import ar.com.system.afip.wsaa.business.impl.WsaaTemplateImpl;
import ar.com.system.afip.wsfe.business.api.WsfeManager;
import ar.com.system.afip.wsfe.business.impl.WsfeManagerImpl;
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

    @Provides
    public WsfeManager provideWsfeManager(WsfeManagerImpl manager) {
        return manager;
    }

    @Provides
    public ExceptionConverter provideExceptionConverter(RetrosoapExceptionConverter converter) {
        return converter;
    }
}
