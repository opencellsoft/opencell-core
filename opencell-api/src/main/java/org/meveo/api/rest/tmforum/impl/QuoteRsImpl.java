/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.rest.tmforum.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.meveo.api.billing.QuoteApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.QuoteVersionDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.tmforum.QuoteRs;
import org.tmf.dsmapi.catalog.resource.order.ProductOrder;
import org.tmf.dsmapi.quote.ProductQuote;
import org.tmf.dsmapi.quote.ProductQuoteItem;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class QuoteRsImpl extends BaseRs implements QuoteRs {

    @Inject
    private QuoteApi quoteApi;

    @Override
    public Response createProductQuote(ProductQuote productQuote, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;

        try {
            productQuote = quoteApi.createQuote(productQuote);
            responseBuilder = Response.status(Response.Status.CREATED).entity(productQuote);

        } catch (Exception e) {
            processExceptionAndSetBuilder(result, responseBuilder, e);
        }

        return buildResponse(responseBuilder);
    }

    @Override
    public Response getProductQuote(String quoteId, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        Response.ResponseBuilder responseBuilder = null;

        try {

            ProductQuote productQuote = quoteApi.getQuote(quoteId);

            responseBuilder = Response.ok().entity(productQuote);

        } catch (Exception e) {
            processExceptionAndSetBuilder(result, responseBuilder, e);
        }


        return buildResponse(responseBuilder);
    }

    @Override
    public Response findProductQuotes(UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        Response.ResponseBuilder responseBuilder = null;

        try {

            Map<String, List<String>> filterCriteria = new HashMap<String, List<String>>();
            List<ProductQuote> quotes = quoteApi.findQuotes(filterCriteria);

            responseBuilder = Response.ok().entity(quotes);

//        } catch (MeveoApiException e) {
//            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
//            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            processExceptionAndSetBuilder(result, responseBuilder, e);
        }


        return buildResponse(responseBuilder);
    }

    @Override
    public Response updateProductQuote(String quoteId, ProductQuote productQuote, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        Response.ResponseBuilder responseBuilder = null;

        try {

            productQuote = quoteApi.updatePartiallyQuote(quoteId, productQuote);
            responseBuilder = Response.ok().entity(productQuote);

        } catch (Exception e) {
            processExceptionAndSetBuilder(result, responseBuilder, e);
        }


        return buildResponse(responseBuilder);
    }

    @SuppressWarnings("hiding")
    @Override
    public Response deleteProductQuote(String quoteId, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        Response.ResponseBuilder responseBuilder = null;

        try {
            quoteApi.deleteQuote(quoteId);

            responseBuilder = Response.ok();

        } catch (Exception e) {
            processExceptionAndSetBuilder(result, responseBuilder, e);
        }


        return buildResponse(responseBuilder);
    }

    @Override
    public Response placeOrder(String quoteId, UriInfo info) {
    	
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
        	
            ProductOrder productOrder = quoteApi.placeOrder(quoteId);
            responseBuilder = Response.ok().entity(productOrder);

        } catch (Exception e) {
            processExceptionAndSetBuilder(result, responseBuilder, e);
        }


        return buildResponse(responseBuilder);
    }

    /**
     * @param result action result
     * @param responseBuilder builder response
     * @param e exception happened
     */
    private void processExceptionAndSetBuilder(ActionStatus result, Response.ResponseBuilder responseBuilder, Exception e) {
        processException(e, result);
        if (responseBuilder != null) {
            responseBuilder.entity(result);
        }
    }

    /**
     * @param responseBuilder response builder
     * @return instance of Response.
     */
    private Response buildResponse(Response.ResponseBuilder responseBuilder) {
        Response response = null;
        if (responseBuilder != null) {
            response = responseBuilder.build();
            log.debug("RESPONSE={}", response.getEntity());
        }
        
        
        return response;
    }

    
	

	@Override
	public Response createQuoteItem(ProductQuoteItem productQuoteItem, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
        	Long id = quoteApi.createQuoteItem(productQuoteItem);
            responseBuilder = Response.ok(Collections.singletonMap("id", id));
            return responseBuilder.build();
        } catch (Exception e) {
            processExceptionAndSetBuilder(result, responseBuilder, e);
            responseBuilder = Response.status(Status.INTERNAL_SERVER_ERROR);
        }
		return null;
	}

	@Override
	public Response deleteQuoteItem(String id, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
        	quoteApi.deleteQuoteItem(id);
            responseBuilder = Response.ok();
        } catch (Exception e) {
            processExceptionAndSetBuilder(result, responseBuilder, e);
            responseBuilder = Response.status(Status.INTERNAL_SERVER_ERROR);
        }
		return responseBuilder.build();
	}

	@Override
	public Response createQuoteVersion(QuoteVersionDto quoteVersion, UriInfo info) {
        try {
        	Long id = quoteApi.createQuoteVersion(quoteVersion);
            return  Response.ok().entity(Collections.singletonMap("id", id))
                    .build();
        } catch (MeveoApiException e) {
        	return errorResponse(e, null);
        }
	
	}

	@Override
	public Response updateQuoteItem(String code, ProductQuoteItem productQuoteitem, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
        	quoteApi.updateQuoteItem(code, productQuoteitem);
            responseBuilder = Response.ok();
        } catch (Exception e) {
            processExceptionAndSetBuilder(result, responseBuilder, e);
            responseBuilder = Response.status(Status.INTERNAL_SERVER_ERROR);
        }
		return responseBuilder.build();
	
	}

	@Override
	public Response updateQuoteVersion(QuoteVersionDto quoteVersion, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
        	quoteApi.updateQuoteVersion(quoteVersion);
            responseBuilder = Response.ok();
        } catch (Exception e) {
            processExceptionAndSetBuilder(result, responseBuilder, e);
            responseBuilder = Response.status(Status.INTERNAL_SERVER_ERROR);
        }
		return responseBuilder.build();
	}

	@Override
	public Response deleteQuoteVersion(String quoteCode, int quoteVersion, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
        	quoteApi.deleteQuoteVersion(quoteCode, quoteVersion);
            responseBuilder = Response.ok(result);
        } catch (Exception e) {
            processExceptionAndSetBuilder(result, responseBuilder, e);
            responseBuilder = Response.status(Status.INTERNAL_SERVER_ERROR);
        }
		return responseBuilder.build();
	}

	/*private Response errorResponse(MeveoApiException e, ActionStatus result) {
		if(result==null) {
			result = new ActionStatus();
		}
		result.setStatus(ActionStatusEnum.FAIL);
		result.setMessage(e.getMessage());
		 return createResponseFromMeveoApiException(e, result).build();
	}*/
   
}