package org.meveo.api.rest.tmforum.impl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.billing.QuoteApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.LoginException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.tmforum.QuoteRs;
import org.tmf.dsmapi.catalog.resource.order.ProductOrder;
import org.tmf.dsmapi.quote.ProductQuote;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class QuoteRsImpl extends BaseRs implements QuoteRs {

    @Inject
    private QuoteApi quoteApi;

    @Override
    public Response createProductQuote(ProductQuote productQuote, UriInfo info) {
    	long l = Calendar.getInstance().getTimeInMillis();
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;

        try {
        	System.out.println("> QuoteRsImpl > createProductQuote > <1> " + (Calendar.getInstance().getTimeInMillis() - l));
            productQuote = quoteApi.createQuote(productQuote);
            System.out.println("> QuoteRsImpl > createProductQuote > <2> " + (Calendar.getInstance().getTimeInMillis() - l));
            responseBuilder = Response.status(Response.Status.CREATED).entity(productQuote);
            System.out.println("> QuoteRsImpl > createProductQuote > <3> " + (Calendar.getInstance().getTimeInMillis() - l));

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }

        System.out.println("> QuoteRsImpl > createProductQuote > <4> " + (Calendar.getInstance().getTimeInMillis() - l));
        Response response = responseBuilder.build();
        System.out.println("> QuoteRsImpl > createProductQuote > <5> " + (Calendar.getInstance().getTimeInMillis() - l));
        log.debug("RESPONSE={}", response.getEntity());
        System.out.println("> QuoteRsImpl > createProductQuote > <6> " + (Calendar.getInstance().getTimeInMillis() - l));
        return response;
    }

    @Override
    public Response getProductQuote(String quoteId, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        Response.ResponseBuilder responseBuilder = null;

        try {

            ProductQuote productQuote = quoteApi.getQuote(quoteId);

            responseBuilder = Response.ok().entity(productQuote);

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }


        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
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
            processException(e, result);
            responseBuilder.entity(result);
        }


        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response updateProductQuote(String quoteId, ProductQuote productQuote, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        Response.ResponseBuilder responseBuilder = null;

        try {

            productQuote = quoteApi.updatePartiallyQuote(quoteId, productQuote);
            responseBuilder = Response.ok().entity(productQuote);

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }


        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
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
            processException(e, result);
            responseBuilder.entity(result);
        }


        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response placeOrder(String quoteId, UriInfo info) {
    	long l = Calendar.getInstance().getTimeInMillis();
    	
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        System.out.println("> QuoteRsImpl > placeOrder > <1> " + (Calendar.getInstance().getTimeInMillis() - l));
        try {
        	
            ProductOrder productOrder = quoteApi.placeOrder(quoteId);
            System.out.println("> QuoteRsImpl > placeOrder > <2> " + (Calendar.getInstance().getTimeInMillis() - l));
            responseBuilder = Response.ok().entity(productOrder);
            System.out.println("> QuoteRsImpl > placeOrder > <3> " + (Calendar.getInstance().getTimeInMillis() - l));

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }


        Response response = responseBuilder.build();
        System.out.println("> QuoteRsImpl > placeOrder > <4> " + (Calendar.getInstance().getTimeInMillis() - l));
        log.debug("RESPONSE={}", response.getEntity());
        System.out.println("> QuoteRsImpl > placeOrder > <5> " + (Calendar.getInstance().getTimeInMillis() - l));
        return response;
    }
}