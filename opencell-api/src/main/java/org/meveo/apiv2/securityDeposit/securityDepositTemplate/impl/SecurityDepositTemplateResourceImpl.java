package org.meveo.apiv2.securityDeposit.securityDepositTemplate.impl;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.securityDeposit.SecurityDepositTemplate;
import org.meveo.apiv2.securityDeposit.securityDepositTemplate.SecurityDepositTemplateResource;
import org.meveo.service.securityDeposit.impl.SecurityDepositTemplateService;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SecurityDepositTemplateResourceImpl implements SecurityDepositTemplateResource {


    @Inject
    SecurityDepositTemplateService securityDepositTemplateService;
    private SecurityDepositTemplateMapper securityDepositTemplateMapper = new SecurityDepositTemplateMapper();

    @Override
    public Response create(SecurityDepositTemplate securityDepositTemplate) {
        org.meveo.model.securityDeposit.SecurityDepositTemplate securityDepositTemplateModel = securityDepositTemplateMapper.toEntity(securityDepositTemplate);
        securityDepositTemplateService.create(securityDepositTemplateModel);

        return Response.ok().entity(buildResponse(securityDepositTemplateMapper.toResource(securityDepositTemplateModel))).build();

    }

    @Override
    public Response update(Long id, SecurityDepositTemplate securityDepositTemplate) {
         org.meveo.model.securityDeposit.SecurityDepositTemplate securityDepositTemplateToUpdate = securityDepositTemplateService.findById(id);
        if(securityDepositTemplateToUpdate == null) {
            throw new EntityDoesNotExistsException("security deposit template with id "+id+" does not exist.");
        }
       securityDepositTemplateToUpdate = securityDepositTemplateMapper.toEntity(securityDepositTemplateToUpdate, securityDepositTemplate);
        securityDepositTemplateService.update(securityDepositTemplateToUpdate);
        return Response.ok().entity(buildResponse(securityDepositTemplateMapper.toResource(securityDepositTemplateToUpdate))).build();

    }

        private Map<String, Object> buildResponse(SecurityDepositTemplate securityDepositTemplate) {
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status","SUCCESS"));
        response.put("securityDepositTemplate", securityDepositTemplate);
        return response;
    }
}
