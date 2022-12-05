package org.meveo.apiv2.dunning.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import org.apache.logging.log4j.util.Strings;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.dunning.DunningCollectionPlanStatus;
import org.meveo.model.payments.DunningCollectionPlanStatusEnum;
import org.meveo.service.payments.impl.DunningCollectionPlanStatusService;
import org.meveo.service.payments.impl.DunningSettingsService;

public class CollectionPlanStatusApiService implements ApiService<DunningCollectionPlanStatus> {

	@Inject
	private DunningCollectionPlanStatusService dunningCollectionPlanStatusService;

	@Inject
	private DunningSettingsService dunningSettingsService;

	private static final String NO_DUNNING_FOUND = "No Dunning found for %s : %s";
	private static final String NO_COLLECTION_PLAN_STATUS_FOUND = "No Collection Plan Status found for id : ";
	private static final String NO_COLLECTION_PLAN_STATUS_FOUND_FOR_DUNNING = "No Collection Plan Status found for code : %s, and status : %s";

	@Override
	public List<DunningCollectionPlanStatus> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		return Collections.emptyList();
	}

	@Override
	public Long getCount(String filter) {
		return null;
	}

	@Override
	public Optional<DunningCollectionPlanStatus> findById(Long id) {
		return Optional.ofNullable(dunningCollectionPlanStatusService.findById(id));
	}

	@Override
	public DunningCollectionPlanStatus create(DunningCollectionPlanStatus collectionPlan) {
		if (collectionPlan.getDunningSettings() != null && collectionPlan.getDunningSettings().getId() != null) {
			var dunningSettings = dunningSettingsService.findById(collectionPlan.getDunningSettings().getId());
			if (dunningSettings == null) {
				throw new BadRequestException(String.format(NO_DUNNING_FOUND, "id", +collectionPlan.getDunningSettings().getId()));
			}
			var collectionPlanExist = dunningCollectionPlanStatusService.findByDunningCodeAndStatus(dunningSettings.getCode(), collectionPlan.getStatus());
			if (collectionPlanExist != null) {
				throw new BadRequestException("Collection plan already exist with id : " + dunningSettings.getId() + " and status : " + collectionPlan.getStatus());
			}
			collectionPlan.setDunningSettings(dunningSettings);
		}
		dunningCollectionPlanStatusService.create(collectionPlan);
		return collectionPlan;
	}

	@Override
	public Optional<DunningCollectionPlanStatus> update(Long id, DunningCollectionPlanStatus baseEntity) {
		var collectionPlan = dunningCollectionPlanStatusService.findById(id);
		if (collectionPlan == null) {
			throw new BadRequestException(NO_COLLECTION_PLAN_STATUS_FOUND + id);
		}
		if (Strings.isNotEmpty(baseEntity.getDescription())) {
			collectionPlan.setDescription(baseEntity.getDescription());
		}
		if (baseEntity.getStatus() != null) {
			collectionPlan.setStatus(baseEntity.getStatus());
		}
		if (Strings.isNotEmpty(baseEntity.getColorCode())) {
		    collectionPlan.setColorCode(baseEntity.getColorCode());
		}
		dunningCollectionPlanStatusService.update(collectionPlan);
		return Optional.of(collectionPlan);
	}

	@Override
	public Optional<DunningCollectionPlanStatus> patch(Long id, DunningCollectionPlanStatus baseEntity) {
		return Optional.empty();
	}

	@Override
	public Optional<DunningCollectionPlanStatus> delete(Long id) {
		var collectionPlan = dunningCollectionPlanStatusService.findById(id);
		if (collectionPlan == null) {
			throw new BadRequestException(NO_COLLECTION_PLAN_STATUS_FOUND + id);
		}
		dunningCollectionPlanStatusService.remove(collectionPlan);
		return Optional.of(collectionPlan);
	}

	public DunningCollectionPlanStatus delete(String dunningSettingCode, DunningCollectionPlanStatusEnum status) {
		var collectionPlan = dunningCollectionPlanStatusService.findByDunningCodeAndStatus(dunningSettingCode, status);
		if (collectionPlan == null) {
			throw new BadRequestException(String.format(NO_COLLECTION_PLAN_STATUS_FOUND_FOR_DUNNING, dunningSettingCode, status));
		}
		dunningCollectionPlanStatusService.remove(collectionPlan);
		return collectionPlan;
	}

	@Override
	public Optional<DunningCollectionPlanStatus> findByCode(String code) {
		return Optional.empty();
	}
	
}
