package org.meveo.apiv2.ordering.orderItem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.order.OrderItemActionEnum;
import org.meveo.model.order.OrderStatusEnum;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableOrderItem.class)
public interface OrderItem extends Resource {
    @Nullable
    String getItemId();
    @Nullable
    @Schema(description = "Action requested on a product or product offer")
    OrderItemActionEnum getAction();
    @Nullable
    @Schema(description = "Order status lifecycle")
    OrderStatusEnum getStatus();
    @Nullable
    Resource getUserAccount();
    @Nullable
    List<ProductInstance> getProductInstance();
    @Nullable
    Resource getOrder();
    @Nullable
    Subscription getSubscription();
}
