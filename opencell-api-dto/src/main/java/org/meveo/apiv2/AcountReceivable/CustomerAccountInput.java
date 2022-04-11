package org.meveo.apiv2.AcountReceivable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableCustomerAccountInput.class)
public interface CustomerAccountInput {

    @Schema(description = "Customer account")
    @Nullable
    CustomerAccount getCustomerAccount();
}