package org.meveo.apiv2.billing;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableTaxDetails.class)
public interface TaxDetails extends Resource {

    @Schema(description = "Main tax")
    Tax getTax();

    @Schema(description = "Tax amount")
    BigDecimal getTaxAmount();

    @Schema(description = "Converted tax amount")
    @Nullable
    BigDecimal getConvertedTaxAmount();

    @Schema(description = "Sub taxes")
    @Nullable
    List<TaxDetails> getSubTaxes();
}