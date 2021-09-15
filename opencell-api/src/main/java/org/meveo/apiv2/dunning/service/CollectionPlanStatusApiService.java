package org.meveo.apiv2.dunning.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;

import org.apache.logging.log4j.util.Strings;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.dunning.CollectionPlanStatus;
import org.meveo.service.payments.impl.CollectionPlanStatusService;
import org.meveo.service.payments.impl.DunningSettingsService;

public class CollectionPlanStatusApiService implements ApiService<CollectionPlanStatus> {
	
	@Inject
	private CollectionPlanStatusService collectionPlanStatusService;
	@Inject
	private DunningSettingsService dunningSettingsService;

	private static String NO_DUNNING_FOUND = "No Dunning found for %s : %s";
	private static String NO_COLLECTION_PLAN_STATUS_FOUND = "No Collection Plan Status found for id : ";
	private static String NO_COLLECTION_PLAN_STATUS_FOUND_FOR_DUNNING = "No Collection Plan Status found for code : %s, and status : %s";
	
	@Override
	public List<CollectionPlanStatus> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		return null;
	}
	
	@Override
	public Long getCount(String filter) {
		return null;
	}
	@Override
	public Optional<CollectionPlanStatus> findById(Long id) {
		return Optional.ofNullable(collectionPlanStatusService.findById(id));
	}
	@Override
	public CollectionPlanStatus create(CollectionPlanStatus collectionPlan) {
		if(collectionPlan.getDunningSettings() != null && collectionPlan.getDunningSettings().getId() != null) {
			var dunningSettings = dunningSettingsService.findById(collectionPlan.getDunningSettings().getId());
			if(dunningSettings == null)
				throw new BadRequestException(String.format(NO_DUNNING_FOUND , "id", + collectionPlan.getDunningSettings().getId())); 
			var collectionPlanExist = collectionPlanStatusService.findByDunningCodeAndStatus(dunningSettings.getCode(), collectionPlan.getStatus());
			if(collectionPlanExist != null) {
				throw new BadRequestException("Collection plan already exist with id : " + dunningSettings.getId() + " and status : " + collectionPlan.getStatus()); 
			}
			collectionPlan.setDunningSettings(dunningSettings);
		}
		collectionPlanStatusService.create(collectionPlan);
		return collectionPlan;
	}
	@Override
	public Optional<CollectionPlanStatus> update(Long id, CollectionPlanStatus baseEntity) {
		throw new BadRequestException("Please use method that take dunning code and status to update the collection plan");
	}
	
	@Transactional
	public CollectionPlanStatus update(String dunningSettingCode, String status, CollectionPlanStatus baseEntity) {
		var collectionPlan = collectionPlanStatusService.findByDunningCodeAndStatus(dunningSettingCode, status);
		if(collectionPlan == null)
			throw new BadRequestException(String.format(NO_COLLECTION_PLAN_STATUS_FOUND_FOR_DUNNING, dunningSettingCode, status)); 
		if(!Strings.isEmpty(baseEntity.getContext()))
			collectionPlan.setContext(baseEntity.getContext());
		if(baseEntity.getLanguage() != null && !baseEntity.getLanguage().isEmpty()) {
			collectionPlan.getLanguage().clear();
			collectionPlan.getLanguage().putAll(baseEntity.getLanguage());
		}
		if(!Strings.isEmpty(baseEntity.getStatus()))
			collectionPlan.setStatus(baseEntity.getStatus());
		return collectionPlanStatusService.update(collectionPlan);
	}
	@Override
	public Optional<CollectionPlanStatus> patch(Long id, CollectionPlanStatus baseEntity) {
		return null;
	}
	@Override
	public Optional<CollectionPlanStatus> delete(Long id) {
		var collectionPlan = collectionPlanStatusService.findById(id);
		if(collectionPlan == null)
			throw new BadRequestException(NO_COLLECTION_PLAN_STATUS_FOUND + id); 
		collectionPlanStatusService.remove(collectionPlan);
		return Optional.of(collectionPlan);
	}
	
	public CollectionPlanStatus delete(String dunningSettingCode, String status) {
		var collectionPlan = collectionPlanStatusService.findByDunningCodeAndStatus(dunningSettingCode, status);
		if(collectionPlan == null)
			throw new BadRequestException(String.format(NO_COLLECTION_PLAN_STATUS_FOUND_FOR_DUNNING, dunningSettingCode, status)); 
		collectionPlanStatusService.remove(collectionPlan);
		return collectionPlan;
	}
	@Override
	public Optional<CollectionPlanStatus> findByCode(String code) {
		return null;
	}
	
}
