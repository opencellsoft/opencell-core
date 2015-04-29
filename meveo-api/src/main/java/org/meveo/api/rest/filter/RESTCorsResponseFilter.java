package org.meveo.api.rest.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 **/
@Provider
@PreMatching
public class RESTCorsResponseFilter implements ContainerResponseFilter {
	private final static Logger log = LoggerFactory
			.getLogger(RESTCorsResponseFilter.class.getName());

	@Override
	public void filter(ContainerRequestContext requestCtx,
			ContainerResponseContext responseCtx) throws IOException {
		log.debug("Adding CORS to the response.");

		responseCtx.getHeaders().add("Access-Control-Allow-Origin", "*");
		responseCtx.getHeaders()
				.add("Access-Control-Allow-Credentials", "true");
		responseCtx.getHeaders().add("Access-Control-Allow-Methods",
				"GET, POST, DELETE, PUT");
	}

}
