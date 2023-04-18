package org.meveo.service.securityDeposit.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.model.securityDeposit.SecurityDepositTemplate;
import org.meveo.model.securityDeposit.SecurityTemplateStatusEnum;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.crm.impl.ProviderService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import java.util.Date;
import java.util.Set;

@Stateless
public class SecurityDepositTemplateService extends BusinessService<SecurityDepositTemplate> {

    @Inject
    private CurrencyService currencyService;
    @Inject
    private FinanceSettingsService financeSettingsService;

    @Inject
    private ProviderService providerService;

    @Inject
    private AuditLogService auditLogService;

    @Override public void create(SecurityDepositTemplate entity) throws BusinessException {
        Currency currency = findCurrencyByIdOrCode(entity);
        if(currency == null) {
        	currency = providerService.getProvider().getCurrency();
        }
		entity.setCurrency(currency);
        checkParameters(entity);
        super.create(entity);
        auditLogService.trackOperation("UPDATE", new Date(), entity, entity.getCode());
    }

    @Override
    public SecurityDepositTemplate update(SecurityDepositTemplate entity) throws BusinessException {

    	Currency currency = findCurrencyByIdOrCode(entity);
        if(currency == null) {
        	currency = providerService.getProvider().getCurrency();
        }
		entity.setCurrency(currency);
        checkParameters(entity);
        SecurityDepositTemplate updatedSecurityDepositTemplate =  super.update(entity);
        auditLogService.trackOperation("UPDATE", new Date(), updatedSecurityDepositTemplate, updatedSecurityDepositTemplate.getCode());
        return updatedSecurityDepositTemplate;
    }

    private Currency findCurrencyByIdOrCode(SecurityDepositTemplate entity){

        if(entity.getCurrency() != null && entity.getCurrency().getId() != null) {
            return currencyService.findById(entity.getCurrency().getId());
        } else  if(entity.getCurrency() != null && entity.getCurrency().getCurrencyCode() != null) {
            return currencyService.findByCode(entity.getCurrency().getCurrencyCode());
        }

        return null;
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
            auditLogService.trackOperation("UPDATE STATUS", new Date(), securityDepositTemplateModel, securityDepositTemplateModel.getCode());

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
        FinanceSettings financeSettings = financeSettingsService.getFinanceSetting();
        if(financeSettings.isAutoRefund() && !securityDepositTemplate.isAllowValidityDate() && !securityDepositTemplate.isAllowValidityPeriod())
            throw new InvalidParameterException("At least allowValidityDate or allowValidityPeriod need to be checked");
        if(securityDepositTemplate.getMaxAmount()!=null && securityDepositTemplate.getMinAmount()!=null && securityDepositTemplate.getMaxAmount().compareTo(securityDepositTemplate.getMinAmount()) < 0 )
            throw new InvalidParameterException("The min amount cannot exceed the max amount");
        if(financeSettings.getMaxAmountPerSecurityDeposit() != null && securityDepositTemplate.getMaxAmount() != null
        && financeSettings.getMaxAmountPerSecurityDeposit().compareTo(securityDepositTemplate.getMaxAmount()) < 0 )
            throw new InvalidParameterException("max amount cannot exceed thr max amount per SD configured at FinanceSettings");
    }
    
    public SecurityDepositTemplate findByTemplateName(String templateName) {
        try {
            return (SecurityDepositTemplate) getEntityManager().createNamedQuery("SecurityDepositTemplate.findByTemplateName").setParameter("templateName", templateName).getSingleResult();
        } catch (NoResultException e) {
            log.warn("cannot find SecurityDepositTemplate with TemplateName {} - erreur : {}", templateName, e.getMessage());
            throw new BusinessException("cannot find SecurityDepositTemplate with TemplateName = " + templateName);
        }
    }
    
    public SecurityDepositTemplate getDefaultSDTemplate() {
        return findByTemplateName("DEFAULT_SD_TEMPLATE");
    }
}
