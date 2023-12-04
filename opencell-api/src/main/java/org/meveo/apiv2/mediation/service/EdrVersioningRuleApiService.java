package org.meveo.apiv2.mediation.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.mediation.EdrVersioningRule;
import org.meveo.model.mediation.MediationSetting;
import org.meveo.service.mediation.EdrVersioningRuleService;
import org.meveo.service.mediation.MediationSettingService;

public class EdrVersioningRuleApiService implements ApiService<EdrVersioningRule>{

	@Inject
	private EdrVersioningRuleService edrVersioningRuleService;
	@Inject
	private MediationSettingService mediationsettingService;


	@Override
	public List<EdrVersioningRule> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset.intValue(), limit.intValue(), null, filter, null, null, null);
		return edrVersioningRuleService.list(paginationConfiguration);
	}

	@Override
	public Long getCount(String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(null, null, null, filter, null, null, null);
        return edrVersioningRuleService.count(paginationConfiguration);
	}

	@Override
	public Optional<EdrVersioningRule> findById(Long id) {
		return Optional.ofNullable(edrVersioningRuleService.findById(id));
	}

	@Transactional
	@Override
	public EdrVersioningRule create(EdrVersioningRule baseEntity) {
		if(baseEntity.getMediationSetting() == null || baseEntity.getMediationSetting().getId() == null)
			throw new BadRequestException("Mediation setting is required");
		MediationSetting mediationSetting = mediationsettingService.findById(baseEntity.getMediationSetting().getId());
		if(mediationSetting == null )
			throw new EntityDoesNotExistsException(MediationSetting.class, baseEntity.getMediationSetting().getId());
		edrVersioningRuleService.checkField(baseEntity);
		baseEntity.setMediationSetting(mediationSetting);
		edrVersioningRuleService.create(baseEntity);
		 return baseEntity;
	}

	@Transactional
	@Override
	public Optional<EdrVersioningRule> update(Long id, EdrVersioningRule baseEntity) {
		var edrVersionRuleUpaded = this.findById(id).orElseThrow(BadRequestException::new);
		if(baseEntity.getMediationSetting() == null || baseEntity.getMediationSetting().getId() == null)
			throw new BadRequestException("Mediation setting is required");
		edrVersioningRuleService.checkField(baseEntity);
		var mediationSetting = mediationsettingService.findById(baseEntity.getMediationSetting().getId());
		if(mediationSetting == null) throw new EntityDoesNotExistsException(MediationSetting.class, baseEntity.getMediationSetting().getId());
		edrVersionRuleUpaded.setMediationSetting(mediationSetting);
		edrVersionRuleUpaded.setCriteriaEL(baseEntity.getCriteriaEL());
		edrVersionRuleUpaded.setKeyEL(baseEntity.getKeyEL());
		edrVersionRuleUpaded.setIsNewVersionEL(baseEntity.getIsNewVersionEL());
		edrVersionRuleUpaded.setPriority(baseEntity.getPriority());
		return Optional.of(baseEntity);
	}
 
	@Transactional
	public void swapPriority(Long edrVersioningRuleOne, Long edrVersioningRuleTwo) {
		if(edrVersioningRuleOne == null || edrVersioningRuleTwo == null)
			throw new BusinessApiException("id for both edr version is required for swaning their priority");
		EdrVersioningRule rule1 = this.findById(edrVersioningRuleOne).orElseThrow(NotFoundException::new);
		EdrVersioningRule rule2 = this.findById(edrVersioningRuleTwo).orElseThrow(NotFoundException::new);

		var priority1 = rule1.getPriority();
//		var priority2 = rule2.getPriority();

		rule1.setPriority(rule2.getPriority());
		rule2.setPriority(priority1);

		edrVersioningRuleService.update(rule1);
		edrVersioningRuleService.update(rule2);
		
	}
	
	@Override
	public Optional<EdrVersioningRule> patch(Long id, EdrVersioningRule baseEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	@Override
	public Optional<EdrVersioningRule> delete(Long id) {
		 var edrVersionRuleRemove = this.findById(id).orElseThrow(BadRequestException::new);
		 PaginationConfiguration config = new PaginationConfiguration("priority", SortOrder.ASCENDING);
		 var rules = edrVersioningRuleService.list(config);
		 edrVersioningRuleService.remove(edrVersionRuleRemove);
		 var currentPriority = edrVersionRuleRemove.getPriority();
		 rules.forEach(edrVersioningRule -> {
			 if(edrVersioningRule.getPriority() > currentPriority) {
					edrVersioningRule.setPriority(edrVersioningRule.getPriority() - 1);
					edrVersioningRuleService.update(edrVersioningRule);
				}
		 });
		 return Optional.of(edrVersionRuleRemove);
	}

	@Override
	public Optional<EdrVersioningRule> findByCode(String code) {
		throw new BusinessException(MediationSetting.class.getSimpleName() + " doesn't have a code, please search with id" );
	}

}
