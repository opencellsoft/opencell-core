package org.meveo.api.rest.filter.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.filter.FilteredListApi;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.filter.FilteredListRs;
import org.meveo.api.rest.impl.BaseRs;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
public class FilteredListRsImpl extends BaseRs implements FilteredListRs {

	@Inject
	private Logger log;

	@Inject
	private FilteredListApi filteredListApi;

	@Override
	public Response list(String filter, Integer firstRow, Integer numberOfRows) {
		Response.ResponseBuilder responseBuilder = null;

		try {
			String result = filteredListApi.list(filter, firstRow, numberOfRows, getCurrentUser().getProvider());
			responseBuilder = Response.ok();
			responseBuilder.entity(result);
		} catch (MeveoApiException e) {
			log.debug("RESPONSE={}", e);
			responseBuilder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
		} catch (Exception e) {
			log.debug("RESPONSE={}", e);
			responseBuilder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
		}

		return responseBuilder.build();
	}
}
