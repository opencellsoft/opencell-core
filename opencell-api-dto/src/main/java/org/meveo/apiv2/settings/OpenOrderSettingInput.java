package org.meveo.apiv2.settings;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.settings.MaximumValidityUnitEnum;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableOpenOrderSettingInput.class)
public interface OpenOrderSettingInput extends Resource {

    @NotNull
    Boolean getUseOpenOrders();


    @Nullable
    Boolean getApplyMaximumValidity();


    @Nullable
    Integer getApplyMaximumValidityValue();


    @Nullable
    MaximumValidityUnitEnum getApplyMaximumValidityUnit();


    @Nullable
    Boolean getDefineMaximumValidity();


    @Nullable
    Integer getDefineMaximumValidityValue();


    @Nullable
    Boolean getUseManagmentValidationForOOQuotation();

}
