package org.meveo.apiv2.securityDeposit.financeSettings.impl;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.securityDeposit.FinanceSettings;
import org.meveo.apiv2.securityDeposit.financeSettings.FinanceSettingsResource;
import org.meveo.apiv2.settings.openOrderSetting.impl.OpenOrderSettingMapper;
import org.meveo.model.settings.OpenOrderSetting;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;
import org.meveo.service.settings.impl.OpenOrderSettingService;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FinanceSettingsResourceImpl implements FinanceSettingsResource {

    @Inject
    FinanceSettingsService financeSettingsService;
    @Inject
    OpenOrderSettingService openOrderSettingService;

    private FinanceSettingsMapper financeSettingsMapper = new FinanceSettingsMapper();
    private OpenOrderSettingMapper openOrderSettingMapper = new OpenOrderSettingMapper();

    @Override
    public Response create(FinanceSettings financeSettings) {

        org.meveo.model.securityDeposit.FinanceSettings financeSettingsModel = financeSettingsMapper.toEntity(financeSettings);
        financeSettingsService.create(financeSettingsModel);
        if(financeSettings.getOpenOrderSetting() != null)
        {
            OpenOrderSetting openOrderSetting = openOrderSettingMapper.toEntity(financeSettings.getOpenOrderSetting());
            openOrderSettingService.create(openOrderSetting);
            financeSettingsModel.setOpenOrderSetting(openOrderSetting);
            financeSettingsService.update(financeSettingsModel);
        }

        return Response.ok().entity(buildResponse(financeSettingsMapper.toResource(financeSettingsModel))).build();

    }

    @Override
    public Response update(Long id, FinanceSettings financeSettings) {

        org.meveo.model.securityDeposit.FinanceSettings financeSettingsToUpdate = financeSettingsService.findById(id);

        if (financeSettingsToUpdate == null) {
            throw new EntityDoesNotExistsException("security deposit settings with id " + id + " does not exist.");
        }

        if(financeSettings.getOpenOrderSetting() != null)
        {
            OpenOrderSetting openOrderSettingToUpdate = openOrderSettingService.findById(financeSettings.getOpenOrderSetting().getId());

            if (openOrderSettingToUpdate == null) {
            throw new EntityDoesNotExistsException("security deposit settings with id " + id + " does not exist.");
        }

            openOrderSettingToUpdate = openOrderSettingMapper.toEntity(openOrderSettingToUpdate,
                    financeSettings.getOpenOrderSetting());
            financeSettingsToUpdate.setOpenOrderSetting(openOrderSettingToUpdate);
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
