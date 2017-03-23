package org.meveo.api.rest.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author Edward P. Legaspi
 **/
@Provider
public class RESTCorsResponseFilter implements ContainerResponseFilter {
    private final static Logger log = LoggerFactory.getLogger(RESTCorsResponseFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext requestCtx, ContainerResponseContext responseCtx) throws IOException {

        MultivaluedMap<String, Object> headers = responseCtx.getHeaders();
        if (!headers.containsKey("Access-Control-Allow-Headers")) {
            log.debug("Adding CORS to the response.");
            responseCtx.getHeaders().add("Access-Control-Allow-Origin", "*");
            responseCtx.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
            responseCtx.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
            responseCtx.getHeaders().add("Access-Control-Allow-Credentials", true);
        }

    }

}
