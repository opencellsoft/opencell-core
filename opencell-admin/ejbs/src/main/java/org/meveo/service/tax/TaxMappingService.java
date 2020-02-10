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
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.tax.TaxCategory;
import org.meveo.model.tax.TaxClass;
import org.meveo.model.tax.TaxMapping;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;

/**
 * Tax mapping service implementation.
 */
@Stateless
public class TaxMappingService extends PersistenceService<TaxMapping> {

    @Inject
    private ResourceBundle resourceBundle;

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
        checkValidityDateFromList(taxMapping, taxMappings);
    }

    private void checkValidityDateFromList(TaxMapping taxMapping, List<TaxMapping> taxMappings) throws BusinessException {
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
                }

                if (taxMapping.getValid().getStrictMatch()) {
                    throw new BusinessException(resourceBundle.getString("invoiceSubCategoryCountry.validityDates.matchingFound"));
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
     * Find a list of matching Tax mappings with a given range of validity dates.
     * 
     * @param taxCategory Tax category
     * @param taxClass Tax class
     * @param sellersCountry Seller's country
     * @param buyersCountry Buyer's country
     * @param validFrom Validity range - from date
     * @param validTo Validity range - to date
     * @return A list of matching tax mappings corresponding to validity dates with an overlap allowed
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
            qb.addSql("((tm.valid.from is null or m.valid.from<=:endDate) AND (:startDate<m.valid.to or tm.valid.to is null))");
        } else if (validFrom != null) {
            qb.addSql("(:startDate<m.valid.to or tm.valid.to is null)");
        } else if (validTo != null) {
            qb.addSql("(tm.valid.from is null or tm.valid.from<=:endDate)");
        }

        qb.addOrderMultiCriterion("tm.taxClass", false, "tm.sellerCountry", false, "tm.buyerCountry", false, "tm.priority", false);

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
    public Tax determineTax(ChargeInstance chargeInstance, Date date) throws BusinessException {

        TaxClass taxClass = chargeInstance.getChargeTemplate().getTaxClass();

        String taxClassEl = chargeInstance.getChargeTemplate().getTaxClassEl();

        if (taxClassEl != null) {

        }

        TaxCategory taxCategory = null;

        TradingCountry sellersCountry = chargeInstance.getSeller().getTradingCountry();
        TradingCountry buyersCountry = chargeInstance.getCountry();

        TaxMapping taxMapping = findBestTaxMappingMatch(taxCategory, taxClass, sellersCountry, buyersCountry, date);
        if (taxMapping == null) {
            throw new IncorrectChargeTemplateException("No Tax mapping matched for " + taxCategory.getId() + "/" + taxClass.getId() + "/" + sellersCountry.getId() + "/" + buyersCountry.getId() + "/" + date);
        }

        Tax tax = null;

        if (StringUtils.isBlank(taxMapping.getTaxEL())) {
            tax = taxMapping.getTax();
        } else {
            tax = evaluateTaxCodeEL(taxMapping.getTaxEL(), chargeInstance.getUserAccount(), chargeInstance.getUserAccount().getBillingAccount(), null); // TODO check parameters
        }

        if (tax == null) {
            throw new IncorrectChargeTemplateException("No tax exists for Tax mapping id=" + taxMapping.getId());
        }

        return tax;
    }

    /**
     * Determine applicable tax for a given seller/buyer and tax category and class combination
     * 
     * @param taxCategory Tax category
     * @param taxClass Tax class
     * @param seller Seller
     * @param billingAccount Billing account
     * @param date Date to determine tax validity
     * @param ignoreNoTax Should exception be thrown if no tax was matched
     * @return Tax to apply
     * @throws BusinessException General business exception
     */
    public Tax determineTax(TaxCategory taxCategory, TaxClass taxClass, Seller seller, BillingAccount billingAccount, Date date, boolean ignoreNoTax) throws BusinessException {

        TradingCountry sellersCountry = seller.getTradingCountry();
        TradingCountry buyersCountry = billingAccount.getTradingCountry();

        TaxMapping taxMapping = findBestTaxMappingMatch(taxCategory, taxClass, sellersCountry, buyersCountry, date);
        if (taxMapping == null) {
            throw new IncorrectChargeTemplateException("No Tax mapping matched for " + taxCategory.getId() + "/" + taxClass.getId() + "/" + sellersCountry.getId() + "/" + buyersCountry.getId() + "/" + date);
        }

        Tax tax = null;

        if (StringUtils.isBlank(taxMapping.getTaxEL())) {
            tax = taxMapping.getTax();
        } else {
            tax = evaluateTaxCodeEL(taxMapping.getTaxEL(), null, billingAccount, null); // TODO check parameters
        }

        if (tax == null) {
            throw new IncorrectChargeTemplateException("No tax exists for Tax mapping id=" + taxMapping.getId());
        }

        return tax;
    }

    /**
     * Determine applicable tax for a given seller/buyer and invoice subcategory combination
     * 
     * @param taxCategory Tax category
     * @param taxClass Tax class
     * @param seller Seller
     * @param buyersCountry Buyer's country
     * @param date Date to determine tax validity
     * @param ignoreNoTax Should exception be thrown if no tax was matched
     * @return Tax to apply
     * @throws BusinessException General business exception
     */
    public Tax determineTax(TaxCategory taxCategory, TaxClass taxClass, Seller seller, TradingCountry buyersCountry, Date date, boolean ignoreNoTax) throws BusinessException {

        TradingCountry sellersCountry = seller.getTradingCountry();

        TaxMapping taxMapping = findBestTaxMappingMatch(taxCategory, taxClass, sellersCountry, buyersCountry, date);
        if (taxMapping == null) {
            throw new IncorrectChargeTemplateException("No Tax mapping matched for " + taxCategory.getId() + "/" + taxClass.getId() + "/" + sellersCountry.getId() + "/" + buyersCountry.getId() + "/" + date);
        }

        Tax tax = null;

        if (StringUtils.isBlank(taxMapping.getTaxEL())) {
            tax = taxMapping.getTax();
        } else {
            tax = evaluateTaxCodeEL(taxMapping.getTaxEL(), null, null, null); // TODO check parameters
        }

        if (tax == null) {
            throw new IncorrectChargeTemplateException("No tax exists for Tax mapping id=" + taxMapping.getId());
        }

        return tax;
    }

    /**
     * Find Tax mapping with the highest priority (highest number)
     * 
     * @param taxCategory Tax category
     * @param taxClass Tax class
     * @param sellersCountry Seller's country
     * @param buyersCountry Buyer's country
     * @param applicationDate Date to consider for match
     * @return A best matched Tax mapping
     */
    public TaxMapping findBestTaxMappingMatch(TaxCategory taxCategory, TaxClass taxClass, TradingCountry sellersCountry, TradingCountry buyersCountry, Date applicationDate) {

        try {
            return getEntityManager().createNamedQuery("TaxMapping.findApplicableTax", TaxMapping.class).setParameter("taxCategory", taxCategory).setParameter("taxClass", taxClass)
                .setParameter("sellerCountry", sellersCountry).setParameter("buyerCountry", buyersCountry).setParameter("applicationDate", applicationDate).setMaxResults(1).getSingleResult();

        } catch (NoResultException ex) {
            log.warn("Failed to find Tax mapping with parameters {}/{}/{}/{}/{}", taxCategory.getId(), taxClass.getId(), sellersCountry.getId(), buyersCountry.getId(), applicationDate);
        }

        return null;
    }

    /**
     * Evaluate tax EL expression
     * 
     * @param taxCodeEL
     * @param userAccount
     * @param billingAccount
     * @param invoice
     * @return
     */
    @SuppressWarnings("deprecation")
    public Tax evaluateTaxCodeEL(String expression, UserAccount userAccount, BillingAccount billingAccount, Invoice invoice) throws BusinessException {
        Tax result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
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
        if (expression.indexOf("ua") >= 0) {
            userMap.put("ua", userAccount);
        }
        if (expression.indexOf("iv") >= 0 || expression.indexOf("invoice") >= 0) {
            userMap.put("iv", invoice);
            userMap.put("invoice", invoice);
        }
        if (expression.indexOf("date") >= 0) {
            userMap.put("date", invoice == null ? new Date() : invoice.getInvoiceDate());
        }
        String taxCode = null;
        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
        try {
            taxCode = (String) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to String but " + res);
        }
        if (taxCode == null) {
            throw new BusinessException("Expression " + expression + " evaluates to null  ");
        } else {
            try {
                result = getEntityManager().createNamedQuery("Tax.getTaxByCode", Tax.class).setParameter("code", taxCode).setMaxResults(1).getSingleResult();
            } catch (NoResultException e) {
                log.debug("No Tax of code {} found", taxCode);
            }
        }

        return result;
    }

}