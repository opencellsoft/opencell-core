package org.meveo.api.rest.tmforum.impl;

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
import org.meveo.api.billing.OrderApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.tmforum.OrderRs;
import org.tmf.dsmapi.catalog.resource.order.ProductOrder;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class OrderRsImpl extends BaseRs implements OrderRs {

    @Inject
    private OrderApi orderApi;

    @Override
    public Response createProductOrder(ProductOrder productOrder, UriInfo info) {
        Response.ResponseBuilder responseBuilder = null;

        try {
            productOrder = orderApi.createProductOrder(productOrder);
            responseBuilder = Response.status(Response.Status.CREATED).entity(productOrder);

        } catch (MeveoApiException e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response getProductOrder(String orderId, UriInfo info) {

        Response.ResponseBuilder responseBuilder = null;

        try {

            ProductOrder productOrder = orderApi.getProductOrder(orderId);

            responseBuilder = Response.ok().entity(productOrder);

        } catch (EntityDoesNotExistsException e) {
            responseBuilder = Response.status(Response.Status.NOT_FOUND);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e2) {
            log.error("Failed to execute API", e2);
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e2 instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e2.getMessage()));
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response findProductOrders(UriInfo info) {

        Response.ResponseBuilder responseBuilder = null;

        try {

            Map<String, List<String>> filterCriteria = new HashMap<String, List<String>>();
            List<ProductOrder> orders = orderApi.findProductOrders(filterCriteria);

            responseBuilder = Response.ok().entity(orders);

//        } catch (MeveoApiException e) {
//            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
//            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response updateProductOrder(String orderId, ProductOrder productOrder, UriInfo info) {

        Response.ResponseBuilder responseBuilder = null;

        try {

            productOrder = orderApi.updatePartiallyProductOrder(orderId, productOrder);
            responseBuilder = Response.ok().entity(productOrder);

        } catch (EntityDoesNotExistsException e) {
            responseBuilder = Response.status(Response.Status.NOT_FOUND);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (MeveoApiException e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @SuppressWarnings("hiding")
    @Override
    public Response deleteProductOrder(String orderId, UriInfo info) {

        Response.ResponseBuilder responseBuilder = null;

        try {
            orderApi.deleteProductOrder(orderId);

            responseBuilder = Response.ok();

        } catch (EntityDoesNotExistsException e) {
            responseBuilder = Response.status(Response.Status.NOT_FOUND);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (ActionForbiddenException e) {
            responseBuilder = Response.status(Response.Status.FORBIDDEN);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (MeveoApiException e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }
}