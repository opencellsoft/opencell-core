package org.meveo.apiv2.mediation.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.mediation.EdrVersioningRule;
import org.meveo.model.mediation.MediationSetting;
import org.meveo.service.mediation.EdrVersioningRuleService;
import org.meveo.service.mediation.MediationSettingService;

public class MediationSettingApiService implements ApiService<MediationSetting>{

	@Inject
	private MediationSettingService mediationsettingService;
	@Inject
	private EdrVersioningRuleService edrVersioningRuleService;

	@Inject
	private GenericPagingAndFilteringUtils genericPagingAndFilteringUtils;
	
	@Override
	public List<MediationSetting> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		long apiLimit = genericPagingAndFilteringUtils.getLimit(limit != null ? limit.intValue() : null);
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset.intValue(), (int)apiLimit, null, filter, null, null, null);
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
