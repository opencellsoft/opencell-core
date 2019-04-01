package org.meveo.apiv2.models.orderItem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.PaginatedResource;

@Value.Immutable
@JsonSerialize(as = org.meveo.apiv2.models.orderItem.ImmutableOrderItems.class)
@JsonDeserialize(as = org.meveo.apiv2.models.orderItem.ImmutableOrderItems.class)
public interface OrderItems extends PaginatedResource<OrderItem> {
}
