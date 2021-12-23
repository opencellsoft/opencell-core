package org.meveo.apiv2.securityDeposit.securityDepositSettings.impl;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.apiv2.article.resource.AccountingArticleResource;
import org.meveo.apiv2.billing.InvoiceMatchedOperation;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.apiv2.securityDeposit.SecurityDepositSettings;
import org.meveo.apiv2.securityDeposit.securityDepositSettings.SecurityDepositSettingsResource;
import org.meveo.service.securityDeposit.impl.SecurityDepositSettingsService;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SecurityDepositSettingsResourceImpl implements SecurityDepositSettingsResource {

    @Inject SecurityDepositSettingsService securityDepositSettingsService;
    private SecurityDepositSettingsMapper securityDepositSettingsMapper = new SecurityDepositSettingsMapper();


    @Override public Response create(SecurityDepositSettings securityDepositSettings) {

        securityDepositSettingsService.create(securityDepositSettingsMapper.toEntity(securityDepositSettings));
        return Response.ok().entity(buildResponse(securityDepositSettings)).build();



    }

    @Override public Response update(Long id, SecurityDepositSettings securityDepositSettings) {

        org.meveo.model.securityDeposit.SecurityDepositSettings securityDepositSettingsToUpdate = securityDepositSettingsService.findById(id);
        if(securityDepositSettingsToUpdate == null) {
            throw new EntityDoesNotExistsException("security deposit settings with id "+id+" does not exist.");
        }
       securityDepositSettingsToUpdate = securityDepositSettingsMapper.toEntity(securityDepositSettingsToUpdate, securityDepositSettings);
        securityDepositSettingsService.update(securityDepositSettingsToUpdate);
        return Response.ok().entity(buildResponse(securityDepositSettings)).build();
    }


    private Map<String, Object> buildResponse(SecurityDepositSettings securityDepositSettings) {
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status","SUCCESS"));
        response.put("securityDepositSettings", securityDepositSettings);
        return response;
    }


}
