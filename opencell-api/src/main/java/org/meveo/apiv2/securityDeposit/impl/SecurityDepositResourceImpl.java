package org.meveo.apiv2.securityDeposit.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.securityDeposit.ImmutableSecurityDepositSuccessResponse;
import org.meveo.apiv2.securityDeposit.SecurityDepositInput;
import org.meveo.apiv2.securityDeposit.impl.SecurityDepositMapper;
import org.meveo.apiv2.securityDeposit.SecurityDepositTemplate;
import org.meveo.apiv2.securityDeposit.resource.SecurityDepositResource;
import org.meveo.apiv2.securityDeposit.service.SecurityDepositApiService;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.securityDeposit.impl.SecurityDepositService;

public class SecurityDepositResourceImpl implements SecurityDepositResource {

    @Inject
    SecurityDepositApiService securityDepositApiService;

    @Inject
    SecurityDepositService securityDepositService;
    
    @Inject
    private AuditLogService auditLogService;
    
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
    
    @Override
    public Response update(Long id, SecurityDepositInput securityDepositInput) {
        SecurityDeposit securityDepositToUpdate = securityDepositService.findById(id);
        BigDecimal oldAmountSD = securityDepositToUpdate.getAmount();
        if(securityDepositToUpdate == null) {
            throw new EntityDoesNotExistsException("security deposit template with id "+id+" does not exist.");
        }
        securityDepositToUpdate = securityDepositMapper.toEntity(securityDepositToUpdate, securityDepositInput);
        securityDepositService.checkParameters(securityDepositToUpdate, securityDepositInput, oldAmountSD);
        securityDepositApiService.linkRealEntities(securityDepositToUpdate);        
        securityDepositService.update(securityDepositToUpdate);
        auditLogService.trackOperation("UPDATE", new Date(), securityDepositToUpdate, securityDepositToUpdate.getCode());
        return Response.ok().entity(buildResponse(securityDepositMapper.toResource(securityDepositToUpdate))).build();

    }

    private Map<String, Object> buildResponse(SecurityDepositInput resource) {
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status","SUCCESS"));
        response.put("securityDeposit", resource);
        return response;
    }

}
