package org.keycloak.quickstarts.devconf2019.app.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Component
public class ObjectMapperProvider {

    private static final Logger log = Logger.getLogger(ObjectMapperProvider.class);

    private final ObjectMapper mapper = new ObjectMapper();

    public ObjectMapperProvider() {
        log.infof("Constructing object mapper");
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


    public ObjectMapper getMapper() {
        return mapper;
    }
}
