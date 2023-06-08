package org.meveo.apiv2.catalog;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.pricelist.PriceListTypeEnum;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutablePriceListLineDto.class)
public interface PriceListLineDto extends Resource {

    @Nullable
    @Schema(description = "Description")
    String getDescription();

    @Nullable
    @Schema(description = "priceList code")
    String getPriceListCode();

    @Nullable
    @Schema(description = "code of the offer template")
    String getOfferTemplateCode();

    @Nullable
    @Schema(description = "code of the offer category")
    String getOfferCategoryCode();

    @Nullable
    @Schema(description = "code of the product category")
    String getProductCategoryCode();

    @Nullable
    @Schema(description = "code of the product")
    String getProductCode();

    @Nullable
    @Schema(description = "code price plan")
    String getPricePlanCode();

    @Nullable
    @Schema(description = "code of charge template")
    String getChargeTemplateCode();

    @Nullable
    @Schema(description = "rate of the priceList")
    Double getRate();

    @Nullable
    @Schema(description = "amount")
    BigDecimal getAmount();

    @Nullable
    @Schema(description = "Expression language to condition priceList line application")
    String getApplicationEl();

    @Nullable
    @Value.Default
    @Schema(description = "rate of priceList type", example = "possible value are : PERCENTAGE, FIXED")
    default PriceListTypeEnum getPriceListRateType() { return PriceListTypeEnum.FIXED; }

    @Nullable
    @Schema(description = "list of the custom field if any")
    CustomFieldsDto getCustomFields();

}
