package org.meveo.apiv2.securityDeposit.securityDepositTemplate.impl;

import org.meveo.admin.exception.ValidationException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.models.Resource;
import org.meveo.apiv2.securityDeposit.SDTemplateListStatus;
import org.meveo.apiv2.securityDeposit.SecurityDepositTemplate;
import org.meveo.apiv2.securityDeposit.securityDepositTemplate.SecurityDepositTemplateResource;
import org.meveo.model.securityDeposit.SecurityTemplateStatusEnum;
import org.meveo.service.securityDeposit.impl.SecurityDepositTemplateService;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Interceptors({ WsRestApiInterceptor.class })
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
        if (securityDepositTemplateToUpdate == null) {
            throw new EntityDoesNotExistsException("security deposit template with id " + id + " does not exist.");
        } else if (SecurityTemplateStatusEnum.ACTIVE.equals(securityDepositTemplateToUpdate.getStatus())) {
            throw new ValidationException("Cannot update an active security deposit template");
        }
        securityDepositTemplateToUpdate = securityDepositTemplateMapper.toEntity(securityDepositTemplateToUpdate, securityDepositTemplate);
        securityDepositTemplateService.update(securityDepositTemplateToUpdate);
        return Response.ok().entity(buildResponse(securityDepositTemplateMapper.toResource(securityDepositTemplateToUpdate))).build();
    }

    @Override
    public Response updateStatus(SDTemplateListStatus sdTemplateListStatus) {
        securityDepositTemplateService.updateStatus(
                sdTemplateListStatus.getSecurityDepositTemplates().stream().map(Resource::getId).collect(Collectors.toSet())
                , sdTemplateListStatus.getStatus());
        return Response.ok().entity(Collections.singletonMap("actionStatus", Collections.singletonMap("status","SUCCESS"))).build();

    }

    private Map<String, Object> buildResponse(SecurityDepositTemplate securityDepositTemplate) {
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status","SUCCESS"));
        response.put("securityDepositTemplate", securityDepositTemplate);
        return response;
    }
}
