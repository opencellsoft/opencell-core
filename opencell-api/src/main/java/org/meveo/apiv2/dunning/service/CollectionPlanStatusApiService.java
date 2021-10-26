package org.meveo.apiv2.dunning.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;

import org.apache.logging.log4j.util.Strings;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.dunning.DunningCollectionPlanStatuses;
import org.meveo.service.payments.impl.DunningCollectionPlanStatusService;
import org.meveo.service.payments.impl.DunningSettingsService;

public class CollectionPlanStatusApiService implements ApiService<DunningCollectionPlanStatuses> {
	
	@Inject
	private DunningCollectionPlanStatusService dunningCollectionPlanStatusService;
	@Inject
	private DunningSettingsService dunningSettingsService;

	private static final String NO_DUNNING_FOUND = "No Dunning found for %s : %s";
	private static final String NO_COLLECTION_PLAN_STATUS_FOUND = "No Collection Plan Status found for id : ";
	private static final String NO_COLLECTION_PLAN_STATUS_FOUND_FOR_DUNNING = "No Collection Plan Status found for code : %s, and status : %s";
	
	@Override
	public List<DunningCollectionPlanStatuses> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		return Collections.emptyList();
	}
	
	@Override
	public Long getCount(String filter) {
		return null;
	}
	@Override
	public Optional<DunningCollectionPlanStatuses> findById(Long id) {
		return Optional.ofNullable(dunningCollectionPlanStatusService.findById(id));
	}
	@Override
	public DunningCollectionPlanStatuses create(DunningCollectionPlanStatuses collectionPlan) {
		if(collectionPlan.getDunningSettings() != null && collectionPlan.getDunningSettings().getId() != null) {
			var dunningSettings = dunningSettingsService.findById(collectionPlan.getDunningSettings().getId());
			if(dunningSettings == null)
				throw new BadRequestException(String.format(NO_DUNNING_FOUND , "id", + collectionPlan.getDunningSettings().getId())); 
			var collectionPlanExist = dunningCollectionPlanStatusService.findByDunningCodeAndStatus(dunningSettings.getCode(), collectionPlan.getStatus());
			if(collectionPlanExist != null) {
				throw new BadRequestException("Collection plan already exist with id : " + dunningSettings.getId() + " and status : " + collectionPlan.getStatus()); 
			}
			collectionPlan.setDunningSettings(dunningSettings);
		}
		dunningCollectionPlanStatusService.create(collectionPlan);
		return collectionPlan;
	}
	@Override
	public Optional<DunningCollectionPlanStatuses> update(Long id, DunningCollectionPlanStatuses baseEntity) {
		throw new BadRequestException("Please use method that take dunning code and status to update the collection plan");
	}
	
	@Transactional
	public DunningCollectionPlanStatuses update(String dunningSettingCode, String status, DunningCollectionPlanStatuses baseEntity) {
		var collectionPlan = dunningCollectionPlanStatusService.findByDunningCodeAndStatus(dunningSettingCode, status);
		if(collectionPlan == null)
			throw new BadRequestException(String.format(NO_COLLECTION_PLAN_STATUS_FOUND_FOR_DUNNING, dunningSettingCode, status)); 
		if(baseEntity.getContext() != null)
			collectionPlan.setContext(baseEntity.getContext());
		if(baseEntity.getLanguage() != null ) {
			collectionPlan.setLanguage(baseEntity.getLanguage());

		}
		if(!Strings.isEmpty(baseEntity.getStatus()))
			collectionPlan.setStatus(baseEntity.getStatus());
		return dunningCollectionPlanStatusService.update(collectionPlan);
	}
	@Override
	public Optional<DunningCollectionPlanStatuses> patch(Long id, DunningCollectionPlanStatuses baseEntity) {
		return Optional.empty();
	}
	@Override
	public Optional<DunningCollectionPlanStatuses> delete(Long id) {
		var collectionPlan = dunningCollectionPlanStatusService.findById(id);
		if(collectionPlan == null)
			throw new BadRequestException(NO_COLLECTION_PLAN_STATUS_FOUND + id); 
		dunningCollectionPlanStatusService.remove(collectionPlan);
		return Optional.of(collectionPlan);
	}
	
	public DunningCollectionPlanStatuses delete(String dunningSettingCode, String status) {
		var collectionPlan = dunningCollectionPlanStatusService.findByDunningCodeAndStatus(dunningSettingCode, status);
		if(collectionPlan == null)
			throw new BadRequestException(String.format(NO_COLLECTION_PLAN_STATUS_FOUND_FOR_DUNNING, dunningSettingCode, status)); 
		dunningCollectionPlanStatusService.remove(collectionPlan);
		return collectionPlan;
	}
	@Override
	public Optional<DunningCollectionPlanStatuses> findByCode(String code) {
		return Optional.empty();
	}
	
}
