package org.meveo.apiv2.mediation.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.mediation.EdrVersioningRule;
import org.meveo.model.mediation.MediationSetting;
import org.meveo.service.mediation.EdrVersioningRuleService;
import org.meveo.service.mediation.MediationsettingService;

@Transactional
public class EdrVersioningRuleApiService implements ApiService<EdrVersioningRule>{

	@Inject
	private EdrVersioningRuleService edrVersioningRuleService;
	@Inject
	private MediationsettingService mediationsettingService;
	
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
		if(StringUtils.isAnyBlank(baseEntity.getCriterialEl(), baseEntity.getKeyEl(), baseEntity.getIsNewVersionEl()) || baseEntity.getPriority() == null)
			throw new MissingParameterException("all parameters of edr version rule is required");
		baseEntity.setMediationSetting(mediationSetting);
		edrVersioningRuleService.create(baseEntity);
		 return baseEntity;
	}

	@Transactional
	@Override
	public Optional<EdrVersioningRule> update(Long id, EdrVersioningRule baseEntity) {
		return Optional.of(baseEntity);
	}

	@Override
	public Optional<EdrVersioningRule> patch(Long id, EdrVersioningRule baseEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<EdrVersioningRule> delete(Long id) {
		return null;
	}

	@Override
	public Optional<EdrVersioningRule> findByCode(String code) {
		throw new BusinessException(MediationSetting.class.getSimpleName() + " doesn't have a code, please search with id" );
	}


}
