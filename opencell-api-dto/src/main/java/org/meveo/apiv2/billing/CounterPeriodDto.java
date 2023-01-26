package org.meveo.apiv2.billing;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.model.catalog.CounterTypeEnum;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableCounterPeriodDto.class)
public interface CounterPeriodDto {

    @Nullable
    String getCode();

    @Nullable
    CounterTypeEnum getCounterType();

    @Nullable
    BigDecimal getLevel();

    @Nullable
    Date getStartDate();

    @Nullable
    Date getEndDate();

    @Nullable
    BigDecimal getValue();

    @Nullable
    Map<String, BigDecimal> getAccumulatedValues();


}