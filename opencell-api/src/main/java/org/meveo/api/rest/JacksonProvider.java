package org.meveo.api.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

@Provider
public class JacksonProvider extends ResteasyJackson2Provider {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Override
    public ObjectMapper locateMapper(Class<?> arg0, MediaType arg1) {
        ObjectMapper mapper = super.locateMapper(arg0, arg1);
        try {
             mapper.setDateFormat(new StdDefaultDateFormat());
        } catch (Exception e) {
            log.error(" error setting ObjectMapper DateFormat ", e);
        }
        return mapper;
    }
}

