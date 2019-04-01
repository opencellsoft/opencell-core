package org.meveo.apiv2.models.orderItem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableOrderItem.class)
@XmlType(name="orderItem", propOrder = { "id", "itemId", "action", "status", "userAccount", "order" , "productInstance", "subscription" })
public interface OrderItem extends Resource {
    @Nullable
    String getItemId();
    @Nullable
    @Schema(description = "Action requested on a product or product offer", allowableValues = {"add","modify","delete"})
    String getAction();
    @Nullable
    @Schema(description = "Order status lifecycle", allowableValues = {"Acknowledged","InProgress","Cancelled","Completed","Rejected","Pending","Held","Failed","Partial"})
    String getStatus();
    @Nullable
    Resource getUserAccount();
    @Nullable
    List<ProductInstance> getProductInstance();
    @Nullable
    Resource getOrder();
    @Nullable
    Subscription getSubscription();
}
