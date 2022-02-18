package org.meveo.apiv2.securityDeposit;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableSecurityDepositRefundInput.class)
public interface SecurityDepositRefundInput extends Resource {
    
    @Nullable
    String getRefundReason();
    
}