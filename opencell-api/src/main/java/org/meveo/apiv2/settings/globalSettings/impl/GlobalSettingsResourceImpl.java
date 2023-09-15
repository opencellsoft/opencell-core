package org.meveo.apiv2.settings.globalSettings.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.settings.GlobalSettingsInput;
import org.meveo.apiv2.settings.globalSettings.GlobalSettingsResource;
import org.meveo.apiv2.settings.globalSettings.service.GlobalSettingsApiService;
import org.meveo.model.settings.GlobalSettings;

@Interceptors({ WsRestApiInterceptor.class })
public class GlobalSettingsResourceImpl implements GlobalSettingsResource {

    @Inject
    private GlobalSettingsApiService globalSettingsApiService;

    private GlobalSettingsMapper globalSettingsMapper = new GlobalSettingsMapper();

    @Override
    public Response create(GlobalSettingsInput input) {
        GlobalSettings globalSetting = globalSettingsMapper.toEntity(input);
        globalSettingsApiService.create(globalSetting);
        return Response.ok().entity(buildResponse(globalSettingsMapper.toResource(globalSetting))).build();
    }

    @Override
    public Response update(Long id, GlobalSettingsInput input) {
        GlobalSettings entityToUpdate = globalSettingsApiService.update(id, globalSettingsMapper.toEntity(input)).get();
        return Response.ok().entity(buildResponse(globalSettingsMapper.toResource(entityToUpdate))).build();
    }

    private Map<String, Object> buildResponse(GlobalSettingsInput resource) {
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status", "SUCCESS"));
        response.put("globalSettings", resource);
        return response;
    }
}
