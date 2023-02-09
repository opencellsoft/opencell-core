package org.meveo.service.securityDeposit.impl;

import static java.util.Arrays.asList;
import static java.util.Map.of;
import static java.util.Optional.ofNullable;
import static org.meveo.commons.utils.ParamBeanFactory.getAppScopeInstance;
import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.securityDeposit.AuxiliaryAccounting;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.service.base.BusinessService;
import org.meveo.service.payments.impl.OCCTemplateService;

import java.util.HashMap;
import java.util.Map;

@Stateless
public class FinanceSettingsService extends BusinessService<FinanceSettings> {

    public static final String AUXILIARY_ACCOUNT_CODE = "auxiliaryAccountCode";
    public static final String AUXILIARY_ACCOUNT_LABEL = "auxiliaryAccountLabel";
    @Inject
    private OCCTemplateService occTemplateService;

    private static final String OCC_CODE_KEY = "accountOperationsGenerationJob.occCode";
    private static final String OCC_DEFAULT_CODE = "INV_STD";

    @Override
    public void create(FinanceSettings entity) throws BusinessException {
        checkParameters(entity);
        super.create(entity);
    }

    @Override
    public FinanceSettings update(FinanceSettings financeSettings) throws BusinessException {
        checkParameters(financeSettings);
        return super.update(financeSettings);
    }

    public void checkParameters(FinanceSettings financeSettings) {
        if (financeSettings.getMaxAmountPerSecurityDeposit() != null
                && financeSettings.getMaxAmountPerSecurityDeposit().longValue() < 1)
            throw new InvalidParameterException("max amount per security Deposit should be greater or equals 1");
        if (financeSettings.getMaxAmountPerCustomer() != null
                && financeSettings.getMaxAmountPerCustomer().longValue() < 1)
            throw new InvalidParameterException("max amount per customer should be greater or equals 1");
    }

    /**
     * Returns active finance settings
     *
     * @return FinanceSettings
     */
    public FinanceSettings findLastOne() {
        try {
            TypedQuery<FinanceSettings> query =
                    getEntityManager()
                            .createQuery("from FinanceSettings f order by f.id desc", entityClass)
                            .setMaxResults(1);
            return query.getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} found", getEntityClass().getSimpleName());
            return null;
        }
    }

    /**
     * Generate auxiliary accounting info
     *
     * @param customerAccount customer account
     * @param auxiliaryAccounting auxiliaryA accounting settings
     * @return Map of generated auxiliary accounting code and auxiliary accounting label
     */
    public Map<String, String> generateAuxiliaryAccountInfo(CustomerAccount customerAccount,
                                                          AuxiliaryAccounting auxiliaryAccounting) {
        Map<Object, Object> context = new HashMap<>();
        context.put("ca", customerAccount);
        context.put("gca", getGeneralClientAccounting(customerAccount));
        String auxiliaryAccountCodeEl = auxiliaryAccounting.getAuxiliaryAccountCodeEl() != null
                && !auxiliaryAccounting.getAuxiliaryAccountCodeEl().isBlank()
                ? auxiliaryAccounting.getAuxiliaryAccountCodeEl() : buildDefaultAuxiliaryCodeEL(customerAccount.getIsCompany());
        String auxiliaryAccountLabelEl = auxiliaryAccounting.getAuxiliaryAccountLabelEl() != null
                && !auxiliaryAccounting.getAuxiliaryAccountLabelEl().isBlank()
                ? auxiliaryAccounting.getAuxiliaryAccountLabelEl() : buildDefaultAuxiliaryLabelEL(customerAccount.getIsCompany());
        String auxiliaryAccountCode =
                evaluateExpression(auxiliaryAccountCodeEl, context, String.class);
        String auxiliaryAccountLabel =
                evaluateExpression(auxiliaryAccountLabelEl, context, String.class);
        return of(AUXILIARY_ACCOUNT_CODE, auxiliaryAccountCode, AUXILIARY_ACCOUNT_LABEL, auxiliaryAccountLabel);
    }

    private AccountingCode getGeneralClientAccounting(CustomerAccount customerAccount) {
        if (customerAccount != null && customerAccount.getGeneralClientAccount() != null) {
            return customerAccount.getGeneralClientAccount();
        } else {
            String occTemplateCode =
                    getAppScopeInstance().getProperty(OCC_CODE_KEY, OCC_DEFAULT_CODE);
            OCCTemplate occTemplate = occTemplateService.findByCode(occTemplateCode, asList("accountingCode"));
            return ofNullable(occTemplate)
                    .map(OCCTemplate::getAccountingCode)
                    .orElse(null);
        }
    }

    private String buildDefaultAuxiliaryCodeEL(boolean isCompany) {
        if(isCompany) {
            return "#{gca.code.substring(0, 3)}#{ca.description}";
        } else {
            return "#{gca.code.substring(0, 3)}#{ca.name.lastName}";
        }
    }

    private String buildDefaultAuxiliaryLabelEL(boolean isCompany) {
        if(isCompany) {
            return "#{ca.description}";
        } else {
            return "#{ca.name.firstName} #{ca.name.lastName}";
        }
    }

    public boolean isBillingRedirectionRulesEnabled() {
        return findLastOne() != null && findLastOne().isEnableBillingRedirectionRules();
    }
}