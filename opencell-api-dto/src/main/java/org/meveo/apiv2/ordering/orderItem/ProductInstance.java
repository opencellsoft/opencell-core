package org.meveo.apiv2.ordering.orderItem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlType;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableProductInstance.class)
@XmlType(name="productInstance", propOrder = { "id", "code", "seller", "product" })
public interface ProductInstance extends Resource {
    @Nullable
    String getCode();
    @Nullable
    Resource getProduct();
    @Nullable
    Resource getSeller();
    @Nullable
    Long getQuantity();
    @Nullable
    Double getProductPrice();
}
