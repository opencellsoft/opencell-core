package org.meveo.apiv2.securityDeposit;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableSecurityDepositSuccessResponse.class)
public interface SecurityDepositSuccessResponse {

    @Nullable
    String getStatus();

    @Nullable
    SecurityDepositInput getNewSecurityDeposit();
}