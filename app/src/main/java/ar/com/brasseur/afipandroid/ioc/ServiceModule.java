package ar.com.brasseur.afipandroid.ioc;

import org.simpleframework.xml.Serializer;

import ar.com.system.afip.android.common.service.RetrofitBuilderProvider;
import ar.com.system.afip.android.wsaa.business.SerializerProvider;
import ar.com.system.afip.android.wsaa.business.SimpleXmlConverter;
import ar.com.system.afip.android.wsaa.service.LoginCmsImpl;
import ar.com.system.afip.android.wsaa.service.RetrofitLoginCms;
import ar.com.system.afip.android.wsaa.service.RetrofitLoginCmsProvider;
import ar.com.system.afip.android.wsfe.service.RetrofitServiceSoap;
import ar.com.system.afip.android.wsfe.service.RetrofitServiceSoapProvider;
import ar.com.system.afip.android.wsfe.service.ServiceSoapImpl;
import ar.com.system.afip.wsaa.business.api.XmlConverter;
import ar.com.system.afip.wsaa.service.api.LoginCMS;
import ar.com.system.afip.wsfe.service.api.ServiceSoap;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class ServiceModule {
    @Provides
    public LoginCMS provideLoginCms(LoginCmsImpl service) {
        return service;
    }

    @Provides
    public RetrofitLoginCms provideRetrofitLoginCms(RetrofitLoginCmsProvider provider) {
        return provider.get();
    }

    @Provides
    public ServiceSoap provideServiceSoap(ServiceSoapImpl service) {
        return service;
    }

    @Provides
    public RetrofitServiceSoap provideRetrofitServiceSoap(RetrofitServiceSoapProvider provider) {
        return provider.get();
    }

    @Provides
    public Retrofit.Builder provideRetrofitBuilder(RetrofitBuilderProvider provider) {
        return provider.get();
    }

    @Provides
    public XmlConverter provideXmlConverter(SimpleXmlConverter converter) {
        return converter;
    }

    @Provides
    public Serializer provideSerializer(SerializerProvider provider) {
        return provider.get();
    }
}
