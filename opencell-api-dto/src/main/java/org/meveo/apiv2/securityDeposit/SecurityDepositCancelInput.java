package org.meveo.apiv2.securityDeposit;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableSecurityDepositCancelInput.class)
public interface SecurityDepositCancelInput extends Resource {
    
    @Nullable
    String getCancelReason();
    
}