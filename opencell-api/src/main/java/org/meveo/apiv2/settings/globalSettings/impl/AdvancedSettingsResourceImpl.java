package org.meveo.apiv2.settings.globalSettings.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.apiv2.settings.globalSettings.AdvancedSettingsResource;
import org.meveo.apiv2.settings.globalSettings.service.AdvancedSettingsApiService;
import org.meveo.model.settings.AdvancedSettings;

public class AdvancedSettingsResourceImpl implements AdvancedSettingsResource {

	@Inject
	private AdvancedSettingsApiService advancedSettingsApiService;

	@Override
	public Response create(org.meveo.apiv2.settings.AdvancedSettings input) {
		if (advancedSettingsApiService.findByCode(input.getCode()) != null) {
			throw new EntityAlreadyExistsException(AdvancedSettings.class, input.getCode());
		}
		AdvancedSettings advancedSetting = mapToEntity(input);
		advancedSettingsApiService.create(advancedSetting);
		return Response.ok().entity(buildResponse(advancedSetting)).build();
	}

	@Override
	public Response update(Long id, org.meveo.apiv2.settings.AdvancedSettings input) {
		AdvancedSettings entityToUpdate = advancedSettingsApiService.update(id, mapToEntity(input)).get();
		return Response.ok().entity(entityToUpdate).build();
	}

	private Map<String, Object> buildResponse(AdvancedSettings resource) {
		Map<String, Object> response = new HashMap<>();
		response.put("actionStatus", Collections.singletonMap("status", "SUCCESS"));
		response.put("advancedSettings", resource);
		return response;
	}

	private AdvancedSettings mapToEntity(org.meveo.apiv2.settings.AdvancedSettings source) {
		AdvancedSettings target = new AdvancedSettings();
		target.setId(source.getId());
		target.setCode(source.getCode());
		target.setDescription(source.getDescription());
		target.setOrigin(source.getOrigin());
		target.setCategory(source.getCategory());
		target.setGroup(source.getGroup());
		target.setValue(source.getValue());
		target.setType(source.getType());
		return target;
	}

}