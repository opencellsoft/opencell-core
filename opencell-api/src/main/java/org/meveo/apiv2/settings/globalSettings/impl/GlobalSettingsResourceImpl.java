package org.meveo.apiv2.settings.globalSettings.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.settings.GlobalSettingsInput;
import org.meveo.apiv2.settings.globalSettings.GlobalSettingsResource;
import org.meveo.apiv2.settings.globalSettings.service.GlobalSettingsApiService;
import org.meveo.model.settings.GlobalSettings;

public class GlobalSettingsResourceImpl implements GlobalSettingsResource {

    @Inject
    private GlobalSettingsApiService globalSettingsApiService;

    private GlobalSettingsMapper globalSettingsMapper = new GlobalSettingsMapper();

    @Override
    public Response create(GlobalSettingsInput input) {
        GlobalSettings openOrderSetting = globalSettingsMapper.toEntity(input);
        globalSettingsApiService.create(openOrderSetting);
        return Response.ok().entity(buildResponse(globalSettingsMapper.toResource(openOrderSetting))).build();

    }

    private Map<String, Object> buildResponse(GlobalSettingsInput resource) {
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status", "SUCCESS"));
        response.put("globalSettings", resource);
        return response;
    }
}
