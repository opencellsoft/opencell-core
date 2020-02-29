package org.meveo.service.tax;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectChargeTemplateException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.DatePeriod;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.tax.TaxCategory;
import org.meveo.model.tax.TaxClass;
import org.meveo.model.tax.TaxMapping;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.script.billing.TaxScriptService;

/**
 * Tax mapping service implementation.
 */
@Stateless
public class TaxMappingService extends PersistenceService<TaxMapping> {

    @Inject
    private ResourceBundle resourceBundle;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private TaxService taxService;

    @Inject
    private TaxScriptService taxScriptService;

    @Override
    public void create(TaxMapping entity) throws BusinessException {
        validateValidityDates(entity);
        super.create(entity);
    }

    @Override
    public TaxMapping update(TaxMapping entity) throws BusinessException {
        validateValidityDates(entity);

        return super.update(entity);
    }

    /**
     * Check that validity dates of Tax mapping entity do not overlap existing records
     * 
     * @param taxMapping Tax mapping
     * @return Updated validity dates and priority value in Invoice subcategory country entity
     * @throws BusinessException General business exception
     */
    public void validateValidityDates(TaxMapping taxMapping) throws BusinessException {
        // validate date
        // Check that two dates are one after another
        if (taxMapping.getValid() != null && !taxMapping.getValid().isValid()) {
            throw new BusinessException(resourceBundle.getString("taxMapping.validityDates.intervalIncorrect"));
        }

        // compute priority
        // get all the taxes of an invoice sub category
        List<TaxMapping> taxMappings = listSimilarTaxMappings(taxMapping.getAccountTaxCategory(), taxMapping.getChargeTaxClass(), taxMapping.getSellerCountry(), taxMapping.getBuyerCountry(),
            taxMapping.getValid() != null ? taxMapping.getValid().getFrom() : null, taxMapping.getValid() != null ? taxMapping.getValid().getTo() : null);

        if (taxMappings != null) {
            TaxMapping mappingFound = null;
            // check for overlap
            for (TaxMapping taxMappingTemp : taxMappings) {
                if (!taxMapping.isTransient() && taxMapping.getId().equals(taxMappingTemp.getId())) {
                    continue;
                }
                if (taxMappingTemp.getValid() == null || taxMapping.getValid().isCorrespondsToPeriod(taxMapping.getValid(), false)) {
                    if (mappingFound == null || mappingFound.getPriority() < taxMappingTemp.getPriority()) {
                        mappingFound = taxMappingTemp;
                    }
                }
            }

            if (mappingFound != null) {
                // check if strict
                if (mappingFound.getValid() != null) {
                    if (taxMapping.getValid() == null) {
                        taxMapping.setValid(new DatePeriod());
                    }
                    taxMapping.getValid().setStrictMatch(mappingFound.getValid() != null && mappingFound.getValid().isCorrespondsToPeriod(taxMapping.getValid(), true));
                    taxMapping.getValid().setFromMatch(mappingFound.getValid().getFrom());
                    taxMapping.getValid().setToMatch(mappingFound.getValid().getTo());

                    if (taxMapping.getValid().getStrictMatch()) {
                        throw new BusinessException(resourceBundle.getString("taxMapping.validityDates.matchingFound", mappingFound.getValid().getFrom(), mappingFound.getValid().getTo()));
                    }
                } else if (mappingFound.getValid() == null && taxMapping.getValid() == null) {
                    throw new BusinessException(resourceBundle.getString("taxMapping.validityDates.matchingFound", null, null));
                }
            }

            taxMapping.setPriority(getNextPriority(taxMappings));
        }
    }

    private int getNextPriority(List<TaxMapping> taxMappings) {
        int maxPriority = 0;
        for (TaxMapping taxMapping : taxMappings) {
            maxPriority = (taxMapping.getPriority() > maxPriority ? taxMapping.getPriority() : maxPriority);
        }
        return maxPriority + 1;
    }

    /**
     * Find a list of matching Tax mappings within a given range of validity dates.
     * 
     * @param taxCategory Tax category
     * @param taxClass Tax class
     * @param sellersCountry Seller's country
     * @param buyersCountry Buyer's country
     * @param validFrom Validity range - from date
     * @param validTo Validity range - to date
     * @return A list of matching tax mappings corresponding to validity dates - dates can overlap
     */
    @SuppressWarnings("unchecked")
    private List<TaxMapping> listSimilarTaxMappings(TaxCategory taxCategory, TaxClass taxClass, TradingCountry sellersCountry, TradingCountry buyersCountry, Date validFrom, Date validTo) {

        QueryBuilder qb = new QueryBuilder(TaxMapping.class, "tm");

        qb.addCriterionEntity("tm.accountTaxCategory", taxCategory);

        if (taxClass == null) {
            qb.addSql("tm.chargeTaxClass is null");
        } else {
            qb.addCriterionEntity("tm.chargeTaxClass", taxClass);
        }
        if (sellersCountry == null) {
            qb.addSql("tm.sellerCountry is null");
        } else {
            qb.addCriterionEntity("tm.sellerCountry", sellersCountry);
        }

        if (buyersCountry == null) {
            qb.addSql("tm.buyerCountry is null");
        } else {
            qb.addCriterionEntity("tm.buyerCountry", buyersCountry);
        }

        if (validFrom != null && validTo != null) {
            qb.addSqlCriterionMultiple("((tm.valid.from is null or tm.valid.from<=:endDate) AND (:startDate<tm.valid.to or tm.valid.to is null))", "startDate", validFrom, "endDate", validTo);
        } else if (validFrom != null) {
            qb.addSqlCriterion("(:startDate<tm.valid.to or tm.valid.to is null)", "startDate", validFrom);
        } else if (validTo != null) {
            qb.addSqlCriterion("(tm.valid.from is null or tm.valid.from<=:endDate)", "endDate", validTo);
        }

        qb.addOrderMultiCriterion("tm.chargeTaxClass", false, "tm.sellerCountry", false, "tm.buyerCountry", false, "tm.priority", false);

        try {
            return (List<TaxMapping>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Determine applicable tax for a given charge instance
     * 
     * @param chargeInstance Charge instance
     * @param date Date to determine tax validity
     * @return Tax to apply
     * @throws BusinessException General business exception
     */
    public TaxInfo determineTax(ChargeInstance chargeInstance, Date date) throws BusinessException {

        TaxClass taxClass = chargeInstance.getTaxClassResolved();
        if (taxClass == null) {
            if (chargeInstance.getChargeTemplate().getTaxClassEl() != null) {
                taxClass = evaluateTaxClassExpression(chargeInstance.getChargeTemplate().getTaxClassEl(), chargeInstance);
            }
            if (taxClass == null) {
                taxClass = chargeInstance.getChargeTemplate().getTaxClass();
            }
            chargeInstance.setTaxClassResolved(taxClass);
        }

        return determineTax(taxClass, chargeInstance.getSeller(), chargeInstance.getUserAccount().getBillingAccount(), chargeInstance.getUserAccount(), date, true, false);
    }

    /**
     * Determine applicable tax for a given charge template
     * 
     * @param chargeTemplate Charge template
     * @param seller Seller
     * @param userAccount userAccount;
     * @param date Date to determine tax validity
     * @return Tax to apply
     * @throws BusinessException General business exception
     */
    public TaxInfo determineTax(ChargeTemplate chargeTemplate, Seller seller, UserAccount userAccount, Date date) throws BusinessException {

        TaxClass taxClass = chargeTemplate.getTaxClass();

        return determineTax(taxClass, seller, userAccount.getBillingAccount(), userAccount, date, true, false);
    }

    /**
     * Determine applicable tax for a given seller/buyer and tax category and class combination
     * 
     * @param taxClass Tax class
     * @param seller Seller
     * @param billingAccount Billing account
     * @param userAccount User account
     * @param date Date to determine tax validity
     * @param checkExoneration Check if billing account is exonerated
     * @param ignoreNoTax Should exception be thrown if no tax was matched
     * @return Tax to apply
     * @throws BusinessException General business exception
     */
    public TaxInfo determineTax(TaxClass taxClass, Seller seller, BillingAccount billingAccount, UserAccount userAccount, Date date, boolean checkExoneration, boolean ignoreNoTax) throws BusinessException {

        try {
            TaxInfo taxInfo = new TaxInfo();
            taxInfo.taxClass = taxClass;

            Tax tax = null;

            if (checkExoneration && billingAccountService.isExonerated(billingAccount)) {
                tax = taxService.getZeroTax();

            } else {

                TaxCategory taxCategory = getTaxCategory(billingAccount);
                taxInfo.taxCategory = taxCategory;

                TaxMapping taxMapping = findBestTaxMappingMatch(taxCategory, taxClass, seller, billingAccount, date);

                if (taxMapping.getTaxEL() != null) {
                    tax = evaluateTaxExpression(taxMapping.getTaxEL(), seller, billingAccount, date); // TODO AKK check parameters
                }

                if (taxMapping.getTaxScript() != null) {

                    if (taxScriptService.isApplicable(taxMapping.getTaxScript().getCode(), userAccount, seller, taxClass, date)) {
                        List<Tax> taxes = taxScriptService.computeTaxes(taxMapping.getTaxScript().getCode(), userAccount, seller, taxClass, date);
                        if (!taxes.isEmpty()) {
                            tax = taxes.get(0);
                        }
                    }
                }

                if (tax == null) {
                    tax = taxMapping.getTax();
                }
            }

            taxInfo.tax = tax;

            return taxInfo;

        } catch (BusinessException e) {
            if (ignoreNoTax) {
                return null;
            }
            throw e;
        }
    }

    /**
     * Determine tax category from a billing account
     * 
     * @param billingAccount Billing account
     * @return Tax category
     */
    private TaxCategory getTaxCategory(BillingAccount billingAccount) {
        TaxCategory taxCategory = billingAccount.getTaxCategoryResolved();
        if (taxCategory == null) {
            taxCategory = billingAccount.getTaxCategory();
            if (taxCategory == null) {
                String taxCategoryEl = billingAccount.getCustomerAccount().getCustomer().getCustomerCategory().getTaxCategoryEl();
                if (taxCategoryEl != null) {
                    taxCategory = evaluateTaxCategoryExpression(taxCategoryEl, billingAccount);
                }
                if (taxCategory == null) {
                    taxCategory = billingAccount.getCustomerAccount().getCustomer().getCustomerCategory().getTaxCategory();
                }
            }
            billingAccount.setTaxCategoryResolved(taxCategory);
        }
        return taxCategory;
    }

    /**
     * Find Tax mapping with the highest priority (highest number)
     * 
     * @param taxCategory Tax category
     * @param taxClass Tax class
     * @param seller Seller
     * @param billingAccount Billing account
     * @param applicationDate Date to consider for match
     * @return A best matched Tax mapping
     */
    public TaxMapping findBestTaxMappingMatch(TaxCategory taxCategory, TaxClass taxClass, Seller seller, BillingAccount billingAccount, Date applicationDate) {

        TradingCountry sellersCountry = seller.getTradingCountry();
        TradingCountry buyersCountry = billingAccount.getTradingCountry();

        List<TaxMapping> taxMappings = getEntityManager().createNamedQuery("TaxMapping.findApplicableTax", TaxMapping.class).setParameter("taxCategory", taxCategory).setParameter("taxClass", taxClass)
            .setParameter("sellerCountry", sellersCountry).setParameter("buyerCountry", buyersCountry).setParameter("applicationDate", applicationDate).getResultList();

        for (TaxMapping taxMapping : taxMappings) {

            if (taxMapping.getFilterEL() == null || evaluateBooleanExpression(taxMapping.getFilterEL(), seller, billingAccount, applicationDate)) {
                return taxMapping;
            }
        }

        log.warn("Failed to find Tax mapping with parameters {}/{}/{}/{}/{}", taxCategory.getId(), taxClass.getId(), sellersCountry.getId(), buyersCountry.getId(), applicationDate);

        throw new IncorrectChargeTemplateException("No Tax mapping matched for " + taxCategory.getId() + "/" + taxClass.getId() + "/" + sellersCountry.getId() + "/" + buyersCountry.getId() + "/" + applicationDate);
    }

    /**
     * Evaluate tax EL expression
     * 
     * @param expression Expression to evaluate
     * @param seller Seller
     * @param billingAccount Billing account
     * @param date Date
     * @return Tax
     */
    private Tax evaluateTaxExpression(String expression, Seller seller, BillingAccount billingAccount, Date date) throws BusinessException {

        if (StringUtils.isBlank(expression)) {
            return null;
        }

        Map<Object, Object> userMap = constructElContext(expression, seller, billingAccount, date);

        String taxCode = ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);

        if (taxCode == null) {
            throw new BusinessException("Expression " + expression + " evaluates to null  ");
        } else {
            try {
                return getEntityManager().createNamedQuery("Tax.getTaxByCode", Tax.class).setParameter("code", taxCode).setMaxResults(1).getSingleResult();
            } catch (NoResultException e) {
                log.debug("No Tax of code {} found", taxCode);
            }
        }

        return null;

    }

    /**
     * Evaluate EL expression with boolean as result
     * 
     * @param expression Expression to evaluate
     * @param seller Seller
     * @param billingAccount Billing account
     * @param date Date
     * @return true/false True if expression is matched
     * @throws BusinessException Business exception
     */
    private boolean evaluateBooleanExpression(String expression, Seller seller, BillingAccount billingAccount, Date date) throws BusinessException {

        if (StringUtils.isBlank(expression)) {
            return true;
        }

        Map<Object, Object> userMap = constructElContext(expression, seller, billingAccount, date);

        return ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);

    }

    /**
     * Construct variable context for EL expression evaluation
     * 
     * @param expression EL expression
     * @param seller Seller
     * @param billingAccount Billing account
     * @param date Date
     * @return A map of variables
     */
    private Map<Object, Object> constructElContext(String expression, Seller seller, BillingAccount billingAccount, Date date) {

        Map<Object, Object> userMap = new HashMap<Object, Object>();

        if (expression.indexOf("seller") >= 0) {
            userMap.put("seller", seller);
        }
        if (expression.indexOf("cust") >= 0) {
            userMap.put("cust", billingAccount.getCustomerAccount().getCustomer());
            userMap.put("c", billingAccount.getCustomerAccount().getCustomer());
        }
        if (expression.indexOf("ca") >= 0) {
            userMap.put("ca", billingAccount.getCustomerAccount());
        }
        if (expression.indexOf("ba") >= 0) {
            userMap.put("ba", billingAccount);
        }
        if (expression.indexOf("date") >= 0) {
            userMap.put("date", date);
        }

        return userMap;
    }

    /**
     * Evaluate tax class EL expression
     * 
     * @param expression Expression to evaluate
     * @param chargeInstance
     * @return Tax class
     */
    private TaxClass evaluateTaxClassExpression(String expression, ChargeInstance chargeInstance) throws BusinessException {

        if (StringUtils.isBlank(expression)) {
            return null;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();

        if (expression.indexOf("ci") >= 0) {
            userMap.put("ci", chargeInstance);
        }

        String code = null;
        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
        try {
            code = (String) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to String but " + res);
        }
        if (code == null) {
            throw new BusinessException("Expression " + expression + " evaluates to null ");

        } else {
            try {
                return getEntityManager().createNamedQuery("TaxClass.getByCode", TaxClass.class).setParameter("code", code).setMaxResults(1).getSingleResult();

            } catch (NoResultException e) {
                log.debug("No Tax of code {} found", code);
            }
        }

        return null;
    }

    /**
     * Evaluate tax category EL expression
     * 
     * @param expression Expression to evaluate
     * @param billingAccount Billing account
     * @return Tax category
     */
    @SuppressWarnings("deprecation")
    private TaxCategory evaluateTaxCategoryExpression(String expression, BillingAccount billingAccount) throws BusinessException {

        if (StringUtils.isBlank(expression)) {
            return null;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();

        if (expression.indexOf("seller") >= 0) {
            userMap.put("seller", billingAccount.getCustomerAccount().getCustomer().getSeller());
        }
        if (expression.indexOf("cust") >= 0) {
            userMap.put("cust", billingAccount.getCustomerAccount().getCustomer());
            userMap.put("c", billingAccount.getCustomerAccount().getCustomer());
        }
        if (expression.indexOf("ca") >= 0) {
            userMap.put("ca", billingAccount.getCustomerAccount());
        }
        if (expression.indexOf("ba") >= 0) {
            userMap.put("ba", billingAccount);
        }

        String code = null;
        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
        try {
            code = (String) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to String but " + res);
        }
        if (code == null) {
            throw new BusinessException("Expression " + expression + " evaluates to null  ");

        } else {
            try {
                return getEntityManager().createNamedQuery("TaxCategory.getByCode", TaxCategory.class).setParameter("code", code).setMaxResults(1).getSingleResult();
            } catch (NoResultException e) {
                log.debug("No TaxCategory of code {} found", code);
            }
        }

        return null;
    }

    /**
     * Tax information
     */
    public class TaxInfo {

        /**
         * Tax category
         */
        public TaxCategory taxCategory;

        /**
         * Tax class
         */
        public TaxClass taxClass;

        /**
         * Tax
         */
        public Tax tax;
    }
}