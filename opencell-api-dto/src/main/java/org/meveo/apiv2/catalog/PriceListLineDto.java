package org.meveo.apiv2.catalog;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutablePriceListLineDto.class)
public interface PriceListLineDto extends Resource {

    @Schema(description = "priceList code")
    @NotNull
    String getPriceListCode();

    @Schema(description = "code of the offer template")
    String getOfferTemplateCode();

    @Schema(description = "code of the offer category")
    String getOfferCategoryCode();

    @Schema(description = "code of the product category")
    @Nullable
    String getProductCategoryCode();

    @Schema(description = "code of the product")
    String getProductCode();

    @Schema(description = "code price plan")
    @NotNull
    String getPricePlanCode();

    @Schema(description = "code of charge template")
    String getChargeTemplateCode();

    @Schema(description = "rate of the priceList")
    Double getRate();

    @Schema(description = "amount")
    BigDecimal getAmount();

    @Schema(description = "Application EL")
    String getApplicationEl();

}
