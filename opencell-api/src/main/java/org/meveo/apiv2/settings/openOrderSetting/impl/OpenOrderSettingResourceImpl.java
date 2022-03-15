package org.meveo.apiv2.settings.openOrderSetting.impl;

import org.meveo.apiv2.settings.OpenOrderSettingInput;
import org.meveo.apiv2.settings.openOrderSetting.OpenOrderSettingResource;
import org.meveo.model.settings.OpenOrderSetting;
import org.meveo.service.settings.impl.OpenOrderSettingService;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OpenOrderSettingResourceImpl implements OpenOrderSettingResource {

    @Inject
    OpenOrderSettingService openOrderSettingService;

    private OpenOrderSettingMapper openOrderSettingMapper = new OpenOrderSettingMapper();

    @Override
    public Response create(OpenOrderSettingInput input) {

       OpenOrderSetting openOrderSetting = openOrderSettingMapper.toEntity(input);
        openOrderSettingService.create(openOrderSetting);
        return Response.ok().entity(buildResponse(openOrderSettingMapper.toResource(openOrderSetting))).build();

    }


    private Map<String, Object> buildResponse(OpenOrderSettingInput resource) {
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status", "SUCCESS"));
        response.put("openOrderSetting", resource);
        return response;
    }

}
