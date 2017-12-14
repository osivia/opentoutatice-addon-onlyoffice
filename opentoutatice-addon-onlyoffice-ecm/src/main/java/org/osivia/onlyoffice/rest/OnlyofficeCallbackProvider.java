package org.osivia.onlyoffice.rest;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

@Provider
@Consumes("application/json")
public class OnlyofficeCallbackProvider implements MessageBodyReader<OnlyofficeCallback> {

    public OnlyofficeCallbackProvider() {
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.isAnnotationPresent(XmlRootElement.class);
    }

    @Override
    public OnlyofficeCallback readFrom(Class<OnlyofficeCallback> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationConfig.Feature.AUTO_DETECT_FIELDS, true);
        return mapper.readValue(entityStream, OnlyofficeCallback.class);
    }

}
