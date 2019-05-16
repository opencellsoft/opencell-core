package org.meveo.apiv2.ordering.orderItem;

import org.immutables.value.Value;
import org.meveo.apiv2.models.PaginatedResource;

@Value.Immutable
public interface OrderItems extends PaginatedResource<OrderItem> {
}
