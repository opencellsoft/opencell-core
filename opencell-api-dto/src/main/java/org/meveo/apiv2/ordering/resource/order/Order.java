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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.apiv2.ordering.resource.orderItem.OrderItem;
import org.meveo.model.order.OrderStatusEnum;

import javax.annotation.Nullable;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableOrder.class)
public interface Order extends Resource {
    @Nullable
    String getCode();
    @Nullable
    String getDescription();
    @Nullable
    Long getPriority();
    @Nullable
    String getCategory();
    @Nullable
    @Schema(description = "Order status lifecycle")
    OrderStatusEnum getStatus();
    @Nullable
    String getStatusMessage();
    @Nullable
    String getOrderDate();
    @Nullable
    String getRequestedProcessingStartDate();
    @Nullable
    String getRequestedCompletionDate();
    @Nullable
    String getExpectedCompletionDate();
    @Nullable
    Resource getBillingCycle();
    @Nullable
    PaymentMethod getPaymentMethod();
    @Nullable
    List<OrderItem> getOrderItems();
    @Nullable
    String getDeliveryInstructions();
}
