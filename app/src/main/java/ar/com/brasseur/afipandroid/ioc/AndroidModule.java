package ar.com.brasseur.afipandroid.ioc;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import dagger.Module;
import dagger.Provides;

@Module
public class AndroidModule {
    @Provides
    public XmlSerializer getXmlSerializer() {
        return Xml.newSerializer();
    }

    @Provides
    public XmlPullParser getXmlPullParser() {
        return Xml.newPullParser();
    }
}
