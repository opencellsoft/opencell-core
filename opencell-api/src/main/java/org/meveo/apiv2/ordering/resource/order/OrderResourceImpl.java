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

package org.meveo.apiv2.ordering.resource.order;

import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.apiv2.ordering.services.OrderApiService;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.List;

public class OrderResourceImpl implements OrderResource {
    @Inject
    private OrderApiService orderService;
    private final OrderMapper orderMapper = new OrderMapper();

    @Override
    public Response getOrders(Long offset, Long limit, String sort, String orderBy, String filter, Request request) {
        List<org.meveo.model.order.Order> ordersEntity = orderService.list(offset, limit, sort, orderBy, filter);
        EntityTag etag = new EntityTag(Integer.toString(ordersEntity.hashCode()));
        CacheControl cc = new CacheControl();
        cc.setMaxAge(1000);
        Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
        if (builder != null) {
            builder.cacheControl(cc);
            return builder.build();
        }
        ImmutableOrder[] OrderList = ordersEntity
                .stream()
                .map(order -> toResourceOrderWithLink(orderMapper.toResource(order)))
                .toArray(ImmutableOrder[]::new);
        Long orderCount = orderService.getCount(filter);
        Orders orders = ImmutableOrders.builder().addData(OrderList).offset(offset).limit(limit).total(orderCount)
                .build().withLinks(new LinkGenerator.PaginationLinkGenerator(OrderResource.class)
                        .offset(offset).limit(limit).total(orderCount).build());
        return Response.ok().cacheControl(cc).tag(etag).entity(orders).build();
    }

    @Override
    public Response getOrder(Long id, Request request) {
        return orderService.findById(id)
                .map(order ->  {
                    EntityTag etag = new EntityTag(Integer.toString(order.hashCode()));
                    CacheControl cc = new CacheControl();
                    cc.setMaxAge(1000);
                    Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
                    if (builder != null) {
                        builder.cacheControl(cc);
                        return builder.build();
                    }
                    return Response.ok().cacheControl(cc).tag(etag)
                            .entity(toResourceOrderWithLink(orderMapper.toResource(order))).build();
                })
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Response createOrder(Order order) {
        org.meveo.model.order.Order orderEntity = orderService.create(orderMapper.toEntity(order));
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(OrderResource.class, orderEntity.getId()).build())
                .entity(toResourceOrderWithLink(orderMapper.toResource(orderEntity)))
                .build();
    }

    @Override
    public Response updateOrder(Long id, Order order) {
        return orderService.update(id, orderMapper.toEntity(order))
                .map(orderEntity -> Response.ok().entity(toResourceOrderWithLink(orderMapper.toResource(orderEntity))).build())
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Response patchOrder(Long id, Order order) {
        return orderService.patch(id, orderMapper.toEntity(order))
                .map(orderEntity -> Response.ok().entity(toResourceOrderWithLink(orderMapper.toResource(orderEntity))).build())
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Response deleteOrder(Long id) {
        return orderService.delete(id)
                .map(orderEntity -> Response.ok().entity(toResourceOrderWithLink(orderMapper.toResource(orderEntity))).build())
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Response deleteOrder(List<Long> ids) {
        ids.forEach(id -> orderService.delete(id)
                        .orElseThrow(() -> new NotFoundException("order with id "+id+" does not exist !")));
        return Response.ok().entity("Successfully deleted all order").build();
    }

    private org.meveo.apiv2.ordering.resource.order.Order toResourceOrderWithLink(org.meveo.apiv2.ordering.resource.order.Order order) {
        return ImmutableOrder.copyOf(order).withLinks(new LinkGenerator.SelfLinkGenerator(OrderResource.class)
                .withId(order.getId()).withGetAction().withPostAction().withPutAction().withPatchAction()
                .withDeleteAction().build());
    }
}
