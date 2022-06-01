package org.meveo.api.dto;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableExchangeRateDto.class)
public interface ExchangeRateDto extends Resource {

    @Nullable
    Boolean getCurrentRate();
    
    @Nullable
    Date getFromDate();

    @Nullable
    BigDecimal getExchangeRate();

    @NotNull
    Resource getTradingCurrency();

}