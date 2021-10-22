package org.meveo.apiv2.dunning.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import org.elasticsearch.common.Strings;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.dunning.DunningCollectionManagement;
import org.meveo.model.dunning.DunningSettings;
import org.meveo.service.payments.impl.DunningCollectionManagementService;
import org.meveo.service.payments.impl.DunningSettingsService;

public class DunningCollectionManagementApiService implements ApiService<DunningCollectionManagement> {
	
	@Inject
	private DunningSettingsService dunningSettingsService;
	@Inject
	private DunningCollectionManagementService dunningCollectionManagementService;

	private static final String  NO_DUNNING_COLLECTION_FOUND = "Dunning Collection doesn't exist with code : %s and agent email : %s";

	@Override
	public List<DunningCollectionManagement> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		return Collections.emptyList();
	}

	@Override
	public Long getCount(String filter) {
		return null;
	}

	@Override
	public Optional<DunningCollectionManagement> findById(Long id) {
		return Optional.empty();
	}

	@Override
	public DunningCollectionManagement create(DunningCollectionManagement baseEntity) {
		if(baseEntity.getDunningSettings() != null) {
			DunningSettings dunningSetting = null;
			if(!Strings.isEmpty(baseEntity.getDunningSettings().getCode()))
				dunningSetting = dunningSettingsService.findByCode(baseEntity.getDunningSettings().getCode());
			if(dunningSetting == null && baseEntity.getDunningSettings().getId() != null)
				dunningSetting = dunningSettingsService.findById(baseEntity.getDunningSettings().getId());
			if(dunningSetting == null)
				throw new BadRequestException("No Dunning found for Dunning collection management");
			
			var existingDunningCollection = dunningCollectionManagementService.findByDunningCodeAndAgentEmailItem(baseEntity.getDunningSettings().getCode(), baseEntity.getAgentEmailItem());
			if(existingDunningCollection != null)
				throw new BadRequestException("Dunning Collection already exist with code : " + baseEntity.getDunningSettings().getCode() + " and agent email : " + baseEntity.getAgentEmailItem());
			baseEntity.setDunningSettings(dunningSetting);
		}	
		dunningCollectionManagementService.create(baseEntity);
		return baseEntity;
	}

	@Override
	public Optional<DunningCollectionManagement> update(Long id, DunningCollectionManagement baseEntity) {
		return Optional.empty();
	}

	@Override
	public Optional<DunningCollectionManagement> patch(Long id, DunningCollectionManagement baseEntity) {
		return Optional.empty();
	}

	@Override
	public Optional<DunningCollectionManagement> delete(Long id) {
		return Optional.empty();
	}

	@Override
	public Optional<DunningCollectionManagement> findByCode(String code) {
		return Optional.empty();
	}

	public DunningCollectionManagement update(String dunningCode, String agentEmailItem, DunningCollectionManagement baseEntity) {
		var existingDunningCollection = dunningCollectionManagementService.findByDunningCodeAndAgentEmailItem(dunningCode, agentEmailItem);
		if(existingDunningCollection == null)
			throw new BadRequestException(String.format(NO_DUNNING_COLLECTION_FOUND, dunningCode, agentEmailItem));
		if(!Strings.isEmpty(baseEntity.getCollectionAgency()))
			existingDunningCollection.setCollectionAgency(baseEntity.getCollectionAgency());
		if(!Strings.isEmpty(baseEntity.getAgentFirstNameItem()))
			existingDunningCollection.setAgentFirstNameItem(baseEntity.getAgentFirstNameItem());
		if(!Strings.isEmpty(baseEntity.getAgentLastNameItem()))
			existingDunningCollection.setAgentLastNameItem(baseEntity.getAgentLastNameItem());
		
		existingDunningCollection.setExternal(baseEntity.isExternal());
		dunningCollectionManagementService.update(existingDunningCollection);
		return existingDunningCollection;
		
	}

	public DunningCollectionManagement delete(String dunningCode, String agentEmailItem) {
		var deletedgDunningCollection = dunningCollectionManagementService.findByDunningCodeAndAgentEmailItem(dunningCode, agentEmailItem);
		if(deletedgDunningCollection == null)
			throw new BadRequestException(String.format(NO_DUNNING_COLLECTION_FOUND, dunningCode, agentEmailItem));
		dunningCollectionManagementService.remove(deletedgDunningCollection);
		return deletedgDunningCollection;
	}
}
