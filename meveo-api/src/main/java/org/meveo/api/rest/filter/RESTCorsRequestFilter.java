package org.meveo.api.rest.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author Edward P. Legaspi
 **/
@Provider
public class RESTCorsRequestFilter implements ContainerRequestFilter {

    private final static Logger log = LoggerFactory.getLogger(RESTCorsRequestFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext requestCtx) throws IOException {
        // When HttpMethod comes as OPTIONS, just acknowledge that it accepts...
        if (requestCtx.getRequest().getMethod().equals("OPTIONS")) {
            log.debug("HTTP Method (OPTIONS) - Detected!");

            // Just send a OK signal back to the browser
            requestCtx.abortWith(Response.status(Response.Status.OK).build());
        }
    }

}
