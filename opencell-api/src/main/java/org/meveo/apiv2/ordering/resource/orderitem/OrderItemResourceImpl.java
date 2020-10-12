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

package org.meveo.apiv2.ordering.resource.orderitem;

import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.apiv2.ordering.resource.orderItem.ImmutableOrderItem;
import org.meveo.apiv2.ordering.resource.orderItem.ImmutableOrderItems;
import org.meveo.apiv2.ordering.resource.orderItem.OrderItems;
import org.meveo.apiv2.ordering.services.OrderItemApiService;
import org.meveo.model.order.OrderItem;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.List;

public class OrderItemResourceImpl implements OrderItemResource {

    private final OrderItemMapper orderItemMapper = new OrderItemMapper();
    @Inject
    private OrderItemApiService orderItemService;

    @Override
    public Response getOrderItems(Long offset, Long limit, String sort, String orderBy, String filter, Request request) {
        List<OrderItem> orderItemsEntity = orderItemService.list(offset, limit, sort, orderBy, filter);

        EntityTag etag = new EntityTag(Integer.toString(orderItemsEntity.hashCode()));
        CacheControl cc = new CacheControl();
        cc.setMaxAge(1000);
        Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
        if (builder != null) {
            builder.cacheControl(cc);
            return builder.build();
        }

        ImmutableOrderItem[] orderItemList = orderItemsEntity
                .stream()
                .map(orderItem -> toResourceOrderItemWithLink(orderItemMapper.toResource(orderItem)))
                .toArray(ImmutableOrderItem[]::new);

        Long productCount = orderItemService.getCount(filter);

        OrderItems immutableOrderItems = ImmutableOrderItems.builder().addData(orderItemList).offset(offset).limit(limit).total(productCount)
                .build().withLinks(new LinkGenerator.PaginationLinkGenerator(OrderItemResource.class)
                        .offset(offset).limit(limit).total(productCount).build());
        return Response.ok().cacheControl(cc).tag(etag).entity(immutableOrderItems).build();
    }

    @Override
    public Response getOrderItem(Long id, Request request) {
        return orderItemService.findById(id)
                .map(orderItem ->  {
                    EntityTag etag = new EntityTag(Integer.toString(orderItem.hashCode()));
                    CacheControl cc = new CacheControl();
                    cc.setMaxAge(1000);
                    Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
                    if (builder != null) {
                        builder.cacheControl(cc);
                        return builder.build();
                    }
                    return Response.ok().cacheControl(cc).tag(etag)
                            .entity(toResourceOrderItemWithLink(orderItemMapper.toResource(orderItem))).build();
                })
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Response createOrderItem(org.meveo.apiv2.ordering.resource.orderItem.OrderItem orderItem) {
        OrderItem orderItemEntity = orderItemService.create(orderItemMapper.toEntity(orderItem));
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(OrderItemResource.class, orderItemEntity.getId()).build())
                .entity(toResourceOrderItemWithLink(orderItemMapper.toResource(orderItemEntity)))
                .build();
    }

    @Override
    public Response updateOrderItem(Long id, org.meveo.apiv2.ordering.resource.orderItem.OrderItem orderItem) {
        return orderItemService.update(id, orderItemMapper.toEntity(orderItem))
                .map(orderItemEntity -> Response.ok().entity(toResourceOrderItemWithLink(orderItemMapper.toResource(orderItemEntity))).build())
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Response patchOrderItem(Long id, org.meveo.apiv2.ordering.resource.orderItem.OrderItem orderItem) {
        return orderItemService.patch(id, orderItemMapper.toEntity(orderItem))
                .map(orderItemEntity -> Response.ok().entity(toResourceOrderItemWithLink(orderItemMapper.toResource(orderItemEntity))).build())
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Response deleteOrderItem(Long id) {
        return orderItemService.delete(id)
                .map(orderItemEntity -> Response.ok().entity(toResourceOrderItemWithLink(orderItemMapper.toResource(orderItemEntity))).build())
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Response deleteOrderItem(List<Long> ids) {
        ids.forEach(id -> orderItemService.delete(id)
                        .orElseThrow(() -> new NotFoundException("order-Item with id "+id+" does not exist !")));
        return Response.ok().entity("Successfully deleted all order-Item").build();
    }

    // TODO : move to mapper
    private org.meveo.apiv2.ordering.resource.orderItem.OrderItem toResourceOrderItemWithLink(org.meveo.apiv2.ordering.resource.orderItem.OrderItem orderItem) {
        return ImmutableOrderItem.copyOf(orderItem).withLinks(new LinkGenerator.SelfLinkGenerator(OrderItemResource.class)
                .withId(orderItem.getId()).withGetAction().withPostAction().withPutAction().withPatchAction()
                .withDeleteAction().build());
    }
}
