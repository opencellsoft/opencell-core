package org.meveo.apiv2.accounting;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableAuxiliaryAccount.class)
public interface AuxiliaryAccount extends Resource {

    @Schema(description = "Customer account")
    @Nullable
    Resource getCustomerAccount();

    @Schema(description = "Auxiliary account code")
    @Nullable
    String getAuxiliaryAccountCode();

    @Schema(description = "Auxiliary account label")
    @Nullable
    String getAuxiliaryAccountLabel();
}