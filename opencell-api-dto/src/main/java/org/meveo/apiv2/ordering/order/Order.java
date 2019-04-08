package org.meveo.apiv2.ordering.order;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.apiv2.ordering.orderItem.OrderItem;

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
    @Schema(description = "Order status lifecycle", allowableValues = {"Acknowledged","InProgress","Cancelled","Completed","Rejected","Pending","Held","Failed","Partial"})
    String getStatus();
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
