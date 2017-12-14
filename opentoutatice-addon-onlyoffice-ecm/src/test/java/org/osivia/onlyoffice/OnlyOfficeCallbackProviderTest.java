package org.osivia.onlyoffice;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.plexus.util.StringInputStream;
import org.osivia.onlyoffice.rest.OnlyofficeCallback;
import org.osivia.onlyoffice.rest.OnlyofficeCallbackProvider;

import com.sun.jersey.core.util.MultivaluedMapImpl;

public class OnlyOfficeCallbackProviderTest {

    private static final String json = "{\"key\":\"article-de-presse\",\"status\":1,\"users\":[\"dlicois@exemple.com\"],\"actions\":[{\"type\":1,\"userid\":\"dlicois@exemple.com\"}]}";

    public static void main(String[] args) {

        OnlyofficeCallbackProvider provider = new OnlyofficeCallbackProvider();

        Annotation[] annotations = {};
        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        MultivaluedMap<String, String> httpHeaders = new MultivaluedMapImpl();
        InputStream entityStream = new StringInputStream(json);
        OnlyofficeCallback oc = null;
        try {
            oc = provider.readFrom(OnlyofficeCallback.class, OnlyofficeCallback.class, annotations, mediaType, httpHeaders, entityStream);
        } catch (WebApplicationException | IOException e) {
            e.printStackTrace();
        }

        System.out.println(oc.getStatus());
    }

}
