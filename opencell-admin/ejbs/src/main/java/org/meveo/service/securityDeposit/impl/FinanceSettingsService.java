package org.meveo.service.securityDeposit.impl;

import static java.util.Arrays.asList;
import static java.util.Map.of;
import static java.util.Optional.ofNullable;
import static org.meveo.commons.utils.ParamBeanFactory.getAppScopeInstance;
import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.pricelist.PriceList;
import org.meveo.model.securityDeposit.AuxiliaryAccounting;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.PriceListService;
import org.meveo.service.payments.impl.OCCTemplateService;

@Stateless
public class FinanceSettingsService extends BusinessService<FinanceSettings> {

    public static final String AUXILIARY_ACCOUNT_CODE = "auxiliaryAccountCode";
    public static final String AUXILIARY_ACCOUNT_LABEL = "auxiliaryAccountLabel";
    @Inject
    private OCCTemplateService occTemplateService;

    @Inject
    private PriceListService priceListService;

    private static final String OCC_CODE_KEY = "accountOperationsGenerationJob.occCode";
    private static final String OCC_DEFAULT_CODE = "INV_STD";

    // Use FINANCE_SETTING_ID to remember the finance setting and look it up by ID next time. A value of -1 indicate that there is no finance setting in the database.
    private static Long FINANCE_SETTING_ID = null;
    
    @Override
    public void create(FinanceSettings entity) throws BusinessException {
        checkParameters(entity);
        super.create(entity);
        FINANCE_SETTING_ID = entity.getId();
    }

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
     * Return a functional error when we want to deactivate a PriceList and we have in the same time a list of Active Price List
     * @param financeSettingsEntity {@link FinanceSettings}
     * @param financeSettings {@link org.meveo.apiv2.securityDeposit.FinanceSettings}
     */
    public void checkPriceList(FinanceSettings financeSettingsEntity, org.meveo.apiv2.securityDeposit.FinanceSettings financeSettings) {
        if(financeSettingsEntity.isEnablePriceList() && !financeSettings.getEnablePriceList()) {
            List<PriceList> priceLists = priceListService.getActivePriceList();
            if (priceLists != null && priceLists.size() > 0)
                throw new InvalidParameterException("You can deactivate this feature only once no price lists are in ACTIVE status.");
        }
    }

    /**
     * Get an active Finance Setting - there should be only one in the system. Use FINANCE_SETTING_ID to remember the finance setting and look it up by ID next time.
     * 
     * @return Mediation setting
     */
    public FinanceSettings getFinanceSetting() {
        FinanceSettings financeSetting = null;

        // No finance setting was looked up yet
        if (FINANCE_SETTING_ID == null) {
            List<FinanceSettings> financeSettings = getEntityManager().createQuery("from FinanceSettings f order by f.id asc", FinanceSettings.class).setMaxResults(1).getResultList();
            if (financeSettings.isEmpty()) {
                FINANCE_SETTING_ID = -1L;
                return null;
            }
            financeSetting = financeSettings.get(0);
            FINANCE_SETTING_ID = financeSetting.getId();

            // Finance setting was looked up and does not exist
        } else if (FINANCE_SETTING_ID < 0) {
            return null;

            // Finance setting was looked up and exists - find by ID
        } else {
            financeSetting = findById(FINANCE_SETTING_ID);
        }
        return financeSetting;
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
        FinanceSettings financeSetting = getFinanceSetting();
        return financeSetting != null && financeSetting.isEnableBillingRedirectionRules();
    }

    public boolean isEntityWithHugeVolume(String entityName){
        FinanceSettings financeSetting = getFinanceSetting();
        return Optional.ofNullable(financeSetting)
                .map(FinanceSettings::getEntitiesWithHugeVolume)
                .map(Map::keySet)
                .orElse(new HashSet<>())
                .stream()
                .anyMatch(e -> e.equalsIgnoreCase(entityName));
    }
}