package org.meveo.apiv2.generic.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GenericApiLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private final Logger logger = LoggerFactory.getLogger(GenericApiLoggingFilter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        requestContext.getDate();
        StringBuilder requestLogBuilder = new StringBuilder();
        requestLogBuilder.append("Request Method : ").append(requestContext.getMethod()).append("\n");
        requestLogBuilder.append("Request URL : ").append(requestContext.getUriInfo().getRequestUri().getPath()).append("\n");
        requestLogBuilder.append("Request Headers : ").append(objectMapper.writeValueAsString(requestContext.getHeaders())).append("\n");
        if(requestContext.hasEntity()){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(requestContext.getEntityStream(), baos);
            requestLogBuilder.append("Request Payload : ").append(baos).append("\n");
            requestContext.setEntityStream(new ByteArrayInputStream(baos.toByteArray()));
        }
        logger.info(requestLogBuilder.toString());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        StringBuilder requestLogBuilder = new StringBuilder();
        requestLogBuilder.append("Response Code : ").append(responseContext.getStatus()).append("\n");
        requestLogBuilder.append("Request Headers : ").append(objectMapper.writeValueAsString(requestContext.getHeaders())).append("\n");
        requestLogBuilder.append("Request Payload : ").append(requestContext.hasEntity() ? responseContext.getEntity() : "{}").append("\n");
        logger.info(requestLogBuilder.toString());
    }
}