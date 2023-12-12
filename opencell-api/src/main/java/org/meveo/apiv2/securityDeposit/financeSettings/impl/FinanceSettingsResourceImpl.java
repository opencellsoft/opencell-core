package org.meveo.apiv2.securityDeposit.financeSettings.impl;

import static java.util.Optional.ofNullable;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.securityDeposit.FinanceSettings;
import org.meveo.apiv2.securityDeposit.financeSettings.FinanceSettingsResource;
import org.meveo.apiv2.settings.OpenOrderSettingInput;
import org.meveo.apiv2.settings.openOrderSetting.impl.OpenOrderSettingMapper;
import org.meveo.model.settings.OpenOrderSetting;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;
import org.meveo.service.settings.impl.OpenOrderSettingService;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Interceptors({ WsRestApiInterceptor.class })
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
        if(financeSettings.getOpenOrderSetting() != null) {
            financeSettingsModel.setOpenOrderSetting(createOpenOrderSetting(financeSettings.getOpenOrderSetting()));
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
        if(financeSettings.getOpenOrderSetting() != null && financeSettings.getOpenOrderSetting().getId() != null) {
            OpenOrderSetting openOrderSettingToUpdate = ofNullable(openOrderSettingService.findById(financeSettings.getOpenOrderSetting().getId()))
                            .orElseThrow(() -> new EntityDoesNotExistsException("security deposit settings with id " + id + " does not exist."));
            openOrderSettingToUpdate = openOrderSettingMapper.toEntity(openOrderSettingToUpdate, financeSettings.getOpenOrderSetting());
            openOrderSettingService.checkParameters(openOrderSettingToUpdate);
            financeSettingsToUpdate.setOpenOrderSetting(openOrderSettingToUpdate);
        } else if(financeSettings.getOpenOrderSetting() != null) {
            financeSettingsToUpdate.setOpenOrderSetting(createOpenOrderSetting(financeSettings.getOpenOrderSetting()));
        }

        //Check Active Price List before disabling the price list feature
        financeSettingsService.checkPriceList(financeSettingsToUpdate, financeSettings);
        financeSettingsToUpdate = financeSettingsMapper.toEntity(financeSettingsToUpdate, financeSettings);
        financeSettingsService.update(financeSettingsToUpdate);
        return Response.ok().entity(buildResponse(financeSettingsMapper.toResource(financeSettingsToUpdate))).build();
    }

    private OpenOrderSetting createOpenOrderSetting(OpenOrderSettingInput openOrderSettingResource) {
        OpenOrderSetting openOrderSetting = openOrderSettingMapper.toEntity(openOrderSettingResource);
        openOrderSettingService.create(openOrderSetting);
        return openOrderSetting;
    }

    private Map<String, Object> buildResponse(FinanceSettings financeSettings) {
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status", "SUCCESS"));
        response.put("financeSettings", financeSettings);
        return response;
    }
}