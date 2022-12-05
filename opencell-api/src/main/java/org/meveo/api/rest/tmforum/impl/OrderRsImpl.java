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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.meveo.api.billing.OrderApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.ApplicableDueDateDelayDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.tmforum.OrderRs;
import org.meveo.model.order.Order;
import org.tmf.dsmapi.catalog.resource.order.ProductOrder;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class OrderRsImpl extends BaseRs implements OrderRs {

    @Inject
    private OrderApi orderApi;

    @Override
    public Response createProductOrder(ProductOrder productOrder, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;

        try {
            productOrder = orderApi.createProductOrder(productOrder, null);
            responseBuilder = Response.status(Response.Status.CREATED).entity(productOrder);

        } catch (Exception e) {
            processExceptionAndSetBuilder(result, responseBuilder, e);
        }



        return buildResponse(responseBuilder);
    }

    @Override
    public Response getProductOrder(String orderId, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        Response.ResponseBuilder responseBuilder = null;

        try {

            ProductOrder productOrder = orderApi.getProductOrder(orderId);

            responseBuilder = Response.ok().entity(productOrder);

        } catch (Exception e) {
            processExceptionAndSetBuilder(result, responseBuilder, e);
        }



        return buildResponse(responseBuilder);
    }

    @Override
    public Response findProductOrders(UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        Response.ResponseBuilder responseBuilder = null;

        try {

            Map<String, List<String>> filterCriteria = new HashMap<String, List<String>>();
            List<ProductOrder> orders = orderApi.findProductOrders(filterCriteria);

            responseBuilder = Response.ok().entity(orders);

        } catch (Exception e) {
            processExceptionAndSetBuilder(result, responseBuilder, e);
        }



        return buildResponse(responseBuilder);
    }

    @Override
    public Response updateProductOrder(String orderId, ProductOrder productOrder, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        Response.ResponseBuilder responseBuilder = null;

        try {

            productOrder = orderApi.updatePartiallyProductOrder(orderId, productOrder);
            responseBuilder = Response.ok().entity(productOrder);

        } catch (Exception e) {
            processException(e, result);
            if (responseBuilder != null) {
                responseBuilder.entity(result);
            }
        }



        return buildResponse(responseBuilder);
    }

    @SuppressWarnings("hiding")
    @Override
    public Response deleteProductOrder(String orderId, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        Response.ResponseBuilder responseBuilder = null;

        try {
            orderApi.deleteProductOrder(orderId);

            responseBuilder = Response.ok();

        } catch (Exception e) {
            processExceptionAndSetBuilder(result, responseBuilder, e);
        }
        

        return buildResponse(responseBuilder);
    }

	@Override
	public Response applicableDueDateDelay(String orderId, UriInfo info) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		Response.ResponseBuilder responseBuilder = null;

		try {
			responseBuilder = Response.ok().entity(orderApi.applicableDueDateDelay(orderId));

		} catch (Exception e) {
			processExceptionAndSetBuilder(result, responseBuilder, e);
		}

		return buildResponse(responseBuilder);
	}

	@Override
	public Response simpleDueDateDelay(String orderId, ApplicableDueDateDelayDto postData, UriInfo info) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		Response.ResponseBuilder responseBuilder = null;

		try {
			orderApi.simpleDueDateDelay(orderId, postData);

			responseBuilder = Response.ok();

		} catch (Exception e) {
			processExceptionAndSetBuilder(result, responseBuilder, e);
		}

		return buildResponse(responseBuilder);
	}

    @Override
    public Response validateProductOrder(ProductOrder productOrder, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
            orderApi.validateProductOrder(productOrder);
            responseBuilder = Response.ok().entity(productOrder);

        } catch (Exception e) {
            processException(e, result);
            if (responseBuilder != null) {
                responseBuilder.entity(result);
            }
        }
        return buildResponse(responseBuilder);
    }

    /**
     * @param result action result
     * @param responseBuilder builder for response
     * @param e exception
     */
    private void processExceptionAndSetBuilder(ActionStatus result, Response.ResponseBuilder responseBuilder, Exception e) {
        processException(e, result);
        if (responseBuilder != null) {
            responseBuilder.entity(result);
        }
        
    }

    /**
     * @param responseBuilder Response builder.
     * @return response.
     */
    private Response buildResponse(Response.ResponseBuilder responseBuilder) {
        Response response = null;
        if (responseBuilder != null) {
            response = responseBuilder.build();
            log.debug("RESPONSE={}", response.getEntity());
        }
        
		
		return response;
    }
}
