package org.meveo.apiv2.securityDeposit.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.securityDeposit.ImmutableSecurityDepositSuccessResponse;
import org.meveo.apiv2.securityDeposit.SecurityDepositInput;
import org.meveo.apiv2.securityDeposit.resource.SecurityDepositResource;
import org.meveo.apiv2.securityDeposit.service.SecurityDepositApiService;
import org.meveo.model.securityDeposit.SecurityDeposit;

public class SecurityDepositResourceImpl implements SecurityDepositResource {

    @Inject
    SecurityDepositApiService securityDepositApiService;

    SecurityDepositMapper securityDepositMapper = new SecurityDepositMapper();

    @Override
    public Response instantiate(SecurityDepositInput securityDepositInput) {

        SecurityDeposit result = securityDepositApiService.instantiate(securityDepositMapper.toEntity(securityDepositInput)).get();

        return Response.ok(ImmutableSecurityDepositSuccessResponse
                .builder()
                .status("SUCCESS")
                .newSecurityDeposit(securityDepositMapper.toResource(result))
                .build()
            ).build();
    }
}
