package org.meveo.apiv2.mediation.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import jakarta.ejb.TransactionAttribute;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.mediation.EdrVersioningRule;
import org.meveo.model.mediation.MediationSetting;
import org.meveo.service.mediation.EdrVersioningRuleService;
import org.meveo.service.mediation.MediationsettingService;

import software.amazon.awssdk.utils.StringUtils;

public class MediationSettingApiService implements ApiService<MediationSetting>{

	@Inject
	private MediationsettingService mediationsettingService;
	@Inject
	private EdrVersioningRuleService edrVersioningRuleService;
	
	@Override
	public List<MediationSetting> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset.intValue(), limit.intValue(), null, filter, null, null, null);
        return mediationsettingService.list(paginationConfiguration);
	}

	@Override
	public Long getCount(String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(null, null, null, filter, null, null, null);
        return mediationsettingService.count(paginationConfiguration);
	}

	@Override
	public Optional<MediationSetting> findById(Long id) {
		return Optional.ofNullable(mediationsettingService.findById(id));
	}

	@Transactional
	@Override
	public MediationSetting create(MediationSetting baseEntity) {
		if(mediationsettingService.count() > 0) throw new EntityAlreadyExistsException("Entity Already exist");
		 var rules = new HashSet<EdrVersioningRule>(baseEntity.getRules());
		 baseEntity.getRules().clear();
		 mediationsettingService.create(baseEntity);
		 rules.forEach(edrV -> {
			 edrVersioningRuleService.checkField(edrV);
			 edrV.setMediationSetting(baseEntity);
			 baseEntity.getRules().add(edrV);
			 edrVersioningRuleService.create(edrV);
		 });
		 return baseEntity;
	}

	@Transactional
	@Override
	public Optional<MediationSetting> update(Long id, MediationSetting baseEntity) {
		var mediationSetting = mediationsettingService.findById(id, true);
		if(mediationSetting == null) throw new EntityDoesNotExistsException(MediationSetting.class, id);
		mediationSetting.getRules().clear();
		if(CollectionUtils.isNotEmpty(baseEntity.getRules())) {
			baseEntity.getRules().forEach(edrV -> {
				edrVersioningRuleService.checkField(edrV);
				 edrV.setMediationSetting(mediationSetting);
				 mediationSetting.getRules().add(edrV);
			});
		}
		mediationSetting.setEnableEdrVersioning(baseEntity.isEnableEdrVersioning());
		mediationsettingService.update(mediationSetting);
		return Optional.of(baseEntity);
	}

	@Override
	public Optional<MediationSetting> patch(Long id, MediationSetting baseEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<MediationSetting> delete(Long id) {
		var deletedMediationSetting = this.findById(id).orElseThrow(NotFoundException::new);
		mediationsettingService.remove(id);
		return Optional.of(deletedMediationSetting);
	}

	@Override
	public Optional<MediationSetting> findByCode(String code) {
		throw new BusinessException(MediationSetting.class.getSimpleName() + " doesn't have a code, please search with id" );
	}

}
