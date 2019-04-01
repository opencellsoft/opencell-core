package org.meveo.apiv2.models.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly=true)
@XmlType(name="product", propOrder = { "id", "code", "name", "description" })
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
