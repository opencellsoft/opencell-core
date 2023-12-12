package org.meveo.apiv2.settings.openOrderSetting.impl;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.settings.OpenOrderSettingInput;
import org.meveo.apiv2.settings.openOrderSetting.OpenOrderSettingResource;
import org.meveo.model.securityDeposit.FinanceSettings;
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
public class OpenOrderSettingResourceImpl implements OpenOrderSettingResource {

    @Inject
    OpenOrderSettingService openOrderSettingService;
    @Inject
    private FinanceSettingsService financeSettingsService;

    private OpenOrderSettingMapper openOrderSettingMapper = new OpenOrderSettingMapper();

    @Override
    public Response create(OpenOrderSettingInput input) {

       OpenOrderSetting openOrderSetting = openOrderSettingMapper.toEntity(input);
       FinanceSettings financeSettings = financeSettingsService.getFinanceSetting();
       if (financeSettings == null) {
           financeSettingsService.create(new FinanceSettings());
       }
       financeSettings = financeSettingsService.getFinanceSetting();

        openOrderSettingService.create(openOrderSetting);
        financeSettings.setOpenOrderSetting(openOrderSetting);
        financeSettingsService.update(financeSettings);

        return Response.ok().entity(buildResponse(openOrderSettingMapper.toResource(openOrderSetting))).build();

    }

    @Override
    public Response update(Long id, OpenOrderSettingInput input) {

        OpenOrderSetting entity = openOrderSettingService.findById(id);
        if (entity == null) {
            throw new EntityDoesNotExistsException("Open order setting with id " + id + " does not exist.");
        }
        OpenOrderSetting entityToUpdate = openOrderSettingMapper.toEntity(entity, input);
        openOrderSettingService.update(entityToUpdate);
        return Response.ok().entity(buildResponse(openOrderSettingMapper.toResource(entityToUpdate))).build();
    }


    private Map<String, Object> buildResponse(OpenOrderSettingInput resource) {
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status", "SUCCESS"));
        response.put("openOrderSetting", resource);
        return response;
    }

}
