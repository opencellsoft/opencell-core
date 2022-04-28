package org.meveo.apiv2.dunning.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import org.elasticsearch.common.Strings;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.dunning.DunningAgent;
import org.meveo.model.dunning.DunningSettings;
import org.meveo.service.payments.impl.DunningAgentService;
import org.meveo.service.payments.impl.DunningSettingsService;

public class DunningAgentApiService implements ApiService<DunningAgent> {
	@Inject
	private GlobalSettingsVerifier globalSettingsVerifier;

	@Inject
	private DunningSettingsService dunningSettingsService;

	@Inject
	private DunningAgentService dunningAgentService;

	private static final String  NO_DUNNING_AGENT_FOUND = "Dunning Agent doesn't exist with code : %s and agent email : %s";

	@Override
	public List<DunningAgent> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		return Collections.emptyList();
	}

	@Override
	public Long getCount(String filter) {
		return null;
	}

	@Override
	public Optional<DunningAgent> findById(Long id) {
		return Optional.empty();
	}

	@Override
	public DunningAgent create(DunningAgent baseEntity) {
		globalSettingsVerifier.checkActivateDunning();
		if(baseEntity.getDunningSettings() != null) {
			DunningSettings dunningSetting = null;
			if(!Strings.isEmpty(baseEntity.getDunningSettings().getCode()))
				dunningSetting = dunningSettingsService.findByCode(baseEntity.getDunningSettings().getCode());
			if(dunningSetting == null && baseEntity.getDunningSettings().getId() != null)
				dunningSetting = dunningSettingsService.findById(baseEntity.getDunningSettings().getId());
			if(dunningSetting == null)
				throw new BadRequestException("No Dunning found for Dunning agent");
			
			var existingDunningAgent = dunningAgentService.findByDunningCodeAndAgentEmailItem(baseEntity.getDunningSettings().getCode(), baseEntity.getAgentEmailItem());
			if(existingDunningAgent != null)
				throw new BadRequestException("Dunning Agent already exist with code : " + baseEntity.getDunningSettings().getCode() + " and agent email : " + baseEntity.getAgentEmailItem());
			baseEntity.setDunningSettings(dunningSetting);
		}	
		dunningAgentService.create(baseEntity);
		return baseEntity;
	}

	@Override
	public Optional<DunningAgent> update(Long id, DunningAgent baseEntity) {
		globalSettingsVerifier.checkActivateDunning();
		return Optional.empty();
	}

	@Override
	public Optional<DunningAgent> patch(Long id, DunningAgent baseEntity) {
		return Optional.empty();
	}

	@Override
	public Optional<DunningAgent> delete(Long id) {
		globalSettingsVerifier.checkActivateDunning();
		return Optional.empty();
	}

	@Override
	public Optional<DunningAgent> findByCode(String code) {
		return Optional.empty();
	}

	public DunningAgent update(String dunningCode, String agentEmailItem, DunningAgent baseEntity) {
		globalSettingsVerifier.checkActivateDunning();
		var existingDunningAgent = dunningAgentService.findByDunningCodeAndAgentEmailItem(dunningCode, agentEmailItem);
		if(existingDunningAgent == null)
			throw new BadRequestException(String.format(NO_DUNNING_AGENT_FOUND, dunningCode, agentEmailItem));
		if(!Strings.isEmpty(baseEntity.getCollectionAgency()))
			existingDunningAgent.setCollectionAgency(baseEntity.getCollectionAgency());
		if(!Strings.isEmpty(baseEntity.getAgentFirstNameItem()))
			existingDunningAgent.setAgentFirstNameItem(baseEntity.getAgentFirstNameItem());
		if(!Strings.isEmpty(baseEntity.getAgentLastNameItem()))
			existingDunningAgent.setAgentLastNameItem(baseEntity.getAgentLastNameItem());
		
		existingDunningAgent.setExternal(baseEntity.isExternal());
		dunningAgentService.update(existingDunningAgent);
		return existingDunningAgent;
		
	}

	public DunningAgent delete(String dunningCode, String agentEmailItem) {
		globalSettingsVerifier.checkActivateDunning();
		var deletedgDunningAgent = dunningAgentService.findByDunningCodeAndAgentEmailItem(dunningCode, agentEmailItem);
		if(deletedgDunningAgent == null)
			throw new BadRequestException(String.format(NO_DUNNING_AGENT_FOUND, dunningCode, agentEmailItem));
		dunningAgentService.remove(deletedgDunningAgent);
		return deletedgDunningAgent;
	}
}
