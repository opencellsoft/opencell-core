package org.meveo.apiv2.ordering.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.apiv2.ordering.orderItem.ImmutableOrderItem;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableProduct.class)
public interface Product extends Resource {
    @Nullable
    String getCode();
    @Nullable
    String getName();
    @Nullable
    String getDescription();
    @Nullable
    String getLongDescription();
    @Nullable
    String getLifeCycleStatus();
    @Nullable
    String getValidFrom();
    @Nullable
    String getValidTo();
    @Nullable
    BigDecimal getPrice();
    @Nullable
    List<Resource> getOfferTemplateCategories();
    @Nullable
    List<Resource> getChannels();
    @Nullable
    List<Resource> getSellers();
    @Nullable
    List<Resource> getProductChargeTemplates();
    @Nullable
    Resource getBusinessProductModel();
    @Nullable
    Resource getInvoicingCalendar();
    @Nullable
    List<Resource> getWalletTemplates();
    @Nullable
    @JsonProperty("disabled")
    Boolean isDisabled();
    @Nullable
    String getImageUrl();
    @Nullable
    String getImage64();
}
