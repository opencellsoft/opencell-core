package org.meveo.service.securityDeposit.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.model.securityDeposit.SecurityDepositTemplate;
import org.meveo.model.securityDeposit.SecurityTemplateStatusEnum;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.base.BusinessService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Set;

@Stateless
public class SecurityDepositTemplateService extends BusinessService<SecurityDepositTemplate> {

    @Inject
    private CurrencyService currencyService;
    @Override public void create(SecurityDepositTemplate entity) throws BusinessException {
        entity.setCurrency(currencyService.findById(entity.getCurrency().getId()));
        checkParameters(entity);
        super.create(entity);
    }

    @Override
    public SecurityDepositTemplate update(SecurityDepositTemplate entity) throws BusinessException {

        entity.setCurrency(currencyService.findById(entity.getCurrency().getId()));
        checkParameters(entity);
        return super.update(entity);
    }

    public void updateStatus(Set<Long> ids, String status)
    {
        for(Long id: ids)
        {
            var securityDepositTemplateModel = findById(id);
            if(securityDepositTemplateModel == null) {
            throw new EntityDoesNotExistsException("security deposit template with id "+id+" does not exist.");
        }
            checkStatusTransition(securityDepositTemplateModel, SecurityTemplateStatusEnum.valueOf(status));
            securityDepositTemplateModel.setStatus(SecurityTemplateStatusEnum.valueOf(status));
            update(securityDepositTemplateModel);
        }

    }

    public void checkStatusTransition(SecurityDepositTemplate securityDepositTemplate, SecurityTemplateStatusEnum status)
    {
        if(SecurityTemplateStatusEnum.ARCHIVED == securityDepositTemplate.getStatus()
                && status == SecurityTemplateStatusEnum.ACTIVE)
            throw new BusinessException("cannot activate an archived security deposit template");

    }

    public void checkParameters(SecurityDepositTemplate securityDepositTemplate)
    {
        if(securityDepositTemplate.getCurrency() == null)
            throw new EntityDoesNotExistsException("currency does not exist.");

        if(!securityDepositTemplate.isAllowValidityDate() && !securityDepositTemplate.isAllowValidityPeriod())
            throw new InvalidParameterException("At least allowValidityDate or allowValidityPeriod need to be checked");
        if(securityDepositTemplate.getMaxAmount().compareTo(securityDepositTemplate.getMinAmount()) < 0 )
            throw new InvalidParameterException("The min amount cannot exceed the max amount");

    }
}
