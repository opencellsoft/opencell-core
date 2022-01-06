package org.meveo.apiv2.securityDeposit.financeSettings.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.securityDeposit.FinanceSettings;
import org.meveo.apiv2.securityDeposit.financeSettings.FinanceSettingsResource;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;

public class FinanceSettingsResourceImpl implements FinanceSettingsResource {

    @Inject
    FinanceSettingsService financeSettingsService;

    private FinanceSettingsMapper financeSettingsMapper = new FinanceSettingsMapper();

    @Override
    public Response create(FinanceSettings financeSettings) {

        org.meveo.model.securityDeposit.FinanceSettings financeSettingsModel = financeSettingsMapper.toEntity(financeSettings);
        financeSettingsService.create(financeSettingsModel);

        return Response.ok().entity(buildResponse(financeSettingsMapper.toResource(financeSettingsModel))).build();

    }

    @Override
    public Response update(Long id, FinanceSettings financeSettings) {

        org.meveo.model.securityDeposit.FinanceSettings financeSettingsToUpdate = financeSettingsService.findById(id);
        if (financeSettingsToUpdate == null) {
            throw new EntityDoesNotExistsException("security deposit settings with id " + id + " does not exist.");
        }
        financeSettingsToUpdate = financeSettingsMapper.toEntity(financeSettingsToUpdate, financeSettings);
        financeSettingsService.update(financeSettingsToUpdate);
        return Response.ok().entity(buildResponse(financeSettingsMapper.toResource(financeSettingsToUpdate))).build();
    }

    private Map<String, Object> buildResponse(FinanceSettings financeSettings) {
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status", "SUCCESS"));
        response.put("financeSettings", financeSettings);
        return response;
    }

}
