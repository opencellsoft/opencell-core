package org.meveo.apiv2.ordering.resource.ooq;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.ordering.resource.order.ThresholdInput;
import org.meveo.model.ordering.OpenOrderTypeEnum;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableOpenOrderQuoteDto.class)
public interface OpenOrderQuoteDto {

    @NotNull
    String getCode();

    @NotNull
    String getBillingAccountCode();

    @Nullable
    String getDescription();

    @Nullable
    String getExternalReference();

    @NotNull
    OpenOrderTypeEnum getOpenOrderType();

    @NotNull
    String getOpenOrderTemplate();

    @NotNull
    BigDecimal getMaxAmount();

    @Nullable
    Date getEndOfValidityDate();

    @NotNull
    Date getActivationDate();

    @Nullable
    List<ThresholdInput> getThresholds();

    @Nullable
    List<String> getTags();

    @Nullable
    List<String> getArticles();

    @Nullable
    List<String> getProducts();


}