/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.billing.impl;

import static org.meveo.commons.utils.NumberUtils.round;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.admin.exception.UnrolledbackBusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.IBillableEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.filter.Filter;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.api.dto.ConsumptionDTO;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.script.billing.TaxScriptService;

/**
 * RatedTransactionService : A class for Rated transaction persistence services.
 * 
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @author Abdelmounaim Akadid
 * 
 * @lastModifiedVersion 5.2
 */
@Stateless
public class RatedTransactionService extends PersistenceService<RatedTransaction> {

    @Inject
    private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private CustomFieldInstanceService customFieldInstanceService;
    
    @Inject
    private TaxScriptService taxScriptService;

    /** constants. */
    private final BigDecimal HUNDRED = new BigDecimal("100");

    /** description map. */
    private Map<String, String> descriptionMap = new HashMap<>();

    /**
     * @param userAccount user account
     * @return list
     */
    public List<RatedTransaction> getRatedTransactionsInvoiced(UserAccount userAccount) {
        if (userAccount == null || userAccount.getWallet() == null) {
            return null;
        }
        return (List<RatedTransaction>) getEntityManager().createNamedQuery("RatedTransaction.listInvoiced", RatedTransaction.class).setParameter("wallet", userAccount.getWallet())
            .getResultList();
    }

    /**
     * @param subscription subscription
     * @param infoType info type
     * @param billingCycle billing cycle
     * @param sumarizeConsumption summary consumption
     * @return instance of ConsumptionDTO
     * @throws IncorrectSusbcriptionException exception for incorrect subscription
     */
    @SuppressWarnings("unchecked")
    // FIXME: edward please use Named queries
    public ConsumptionDTO getConsumption(Subscription subscription, String infoType, Integer billingCycle, boolean sumarizeConsumption) throws IncorrectSusbcriptionException {

        Date lastBilledDate = null;
        ConsumptionDTO consumptionDTO = new ConsumptionDTO();

        // If billing has been run already, use last billing date plus a day as
        // filtering FROM value
        // Otherwise leave it null, so it wont be included in a query
        if (subscription.getUserAccount().getBillingAccount().getBillingRun() != null) {
            lastBilledDate = subscription.getUserAccount().getBillingAccount().getBillingRun().getEndDate();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(lastBilledDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            lastBilledDate = calendar.getTime();

        }

        if (sumarizeConsumption) {

            QueryBuilder qb = new QueryBuilder("select sum(amount1WithTax), sum(usageAmount) from " + RatedTransaction.class.getSimpleName());
            qb.addCriterionEntity("subscription", subscription);
            qb.addCriterion("subUsageCode1", "=", infoType, false);
            qb.addCriterionDateRangeFromTruncatedToDay("usageDate", lastBilledDate);
            String baseSql = qb.getSqlString();

            // Summarize invoiced transactions
            String sql = baseSql + " and status='BILLED'";

            Query query = getEntityManager().createQuery(sql);

            for (Entry<String, Object> param : qb.getParams().entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }

            Object[] results = (Object[]) query.getSingleResult();

            consumptionDTO.setAmountCharged((BigDecimal) results[0]);
            consumptionDTO.setConsumptionCharged(((Long) results[1]).intValue());

            // Summarize not invoiced transactions
            sql = baseSql + " and status<>'BILLED'";

            query = getEntityManager().createQuery(sql);

            for (Entry<String, Object> param : qb.getParams().entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }

            results = (Object[]) query.getSingleResult();

            consumptionDTO.setAmountUncharged((BigDecimal) results[0]);
            consumptionDTO.setConsumptionUncharged(((Long) results[1]).intValue());

        } else {

            QueryBuilder qb = new QueryBuilder(
                "select sum(amount1WithTax), sum(usageAmount), groupingId, case when status='BILLED' then 'true' else 'false' end from " + RatedTransaction.class.getSimpleName());
            qb.addCriterionEntity("subscription", subscription);
            qb.addCriterion("subUsageCode1", "=", infoType, false);
            qb.addCriterionDateRangeFromTruncatedToDay("usageDate", lastBilledDate);
            qb.addSql("groupingId is not null");
            String sql = qb.getSqlString() + " group by groupingId, case when status='BILLED' then 'true' else 'false' end";

            Query query = getEntityManager().createQuery(sql);

            for (Entry<String, Object> param : qb.getParams().entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }

            List<Object[]> results = (List<Object[]>) query.getResultList();

            for (Object[] result : results) {

                BigDecimal amount = (BigDecimal) result[0];
                int consumption = ((Long) result[1]).intValue();
                boolean charged = Boolean.parseBoolean((String) result[3]);
                // boolean roaming =
                // RatedTransaction.translateGroupIdToRoaming(groupId);
                // boolean upload =
                // RatedTransaction.translateGroupIdToUpload(groupId);

                if (charged) {

                    // if (!roaming && !upload) {
                    consumptionDTO.setIncomingNationalConsumptionCharged(consumption);
                    // } else if (roaming && !upload) {
                    // consumptionDTO.setIncomingRoamingConsumptionCharged(consumption);
                    // } else if (!roaming && upload) {
                    // consumptionDTO.setOutgoingNationalConsumptionCharged(consumption);
                    // } else {
                    // consumptionDTO.setOutgoingRoamingConsumptionCharged(consumption);
                    // }

                    consumptionDTO.setConsumptionCharged(consumptionDTO.getConsumptionCharged() + consumption);
                    consumptionDTO.setAmountCharged(consumptionDTO.getAmountCharged().add(amount));

                } else {
                    // if (!roaming && !upload) {
                    consumptionDTO.setIncomingNationalConsumptionUncharged(consumption);
                    // } else if (roaming && !upload) {
                    // consumptionDTO.setIncomingRoamingConsumptionUncharged(consumption);
                    // } else if (!roaming && upload) {
                    // consumptionDTO.setOutgoingNationalConsumptionUncharged(consumption);
                    // } else {
                    // consumptionDTO.setOutgoingRoamingConsumptionUncharged(consumption);
                    // }
                    consumptionDTO.setConsumptionUncharged(consumptionDTO.getConsumptionUncharged() + consumption);
                    consumptionDTO.setAmountUncharged(consumptionDTO.getAmountUncharged().add(amount));
                }
            }
        }

        return consumptionDTO;

    }

    /**
     * Append invoice aggregates to an invoice. Only one of these should be provided: ratedTransactionFilter, ratedTransactions, orderNumber
     * 
     * @param billingAccount Billing Account
     * @param invoice Invoice to append invoice aggregates to
     * @param ratedTransactionFilter Filter to use to filter rated transactions.
     * @param firstTransactionDate First transaction date
     * @param lastTransactionDate Last transaction date
     * @throws BusinessException business exception
     */
    public void appendInvoiceAgregates(BillingAccount billingAccount, Invoice invoice, Filter ratedTransactionFilter, Date firstTransactionDate, Date lastTransactionDate)
            throws BusinessException {
        appendInvoiceAgregates(billingAccount, invoice, ratedTransactionFilter, null, firstTransactionDate, lastTransactionDate, false, false);
    }

    /**
     * Append invoice aggregates to an invoice. Only one of these should be provided: ratedTransactionFilter, ratedTransactions, orderNumber
     * 
     * @param billingAccount Billing Account
     * @param invoice Invoice to append invoice aggregates to
     * @param ratedTransactionFilter Filter to use to filter rated transactions.
     * @param ratedTransactions A list of rated transactions - used in conjunction with isVirtual=true
     * @param firstTransactionDate First transaction date
     * @param lastTransactionDate Last transaction date
     * @param isInvoiceAdjustment Is this invoice adjustment
     * @param isVirtual Is this a virtual invoice - invoice is not persisted, rated transactions are not persisted either
     * @throws BusinessException BusinessException
     */
    @SuppressWarnings({ "unchecked", "unused" })
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void appendInvoiceAgregates(BillingAccount billingAccount, Invoice invoice, Filter ratedTransactionFilter, List<RatedTransaction> ratedTransactions,
            Date firstTransactionDate, Date lastTransactionDate, boolean isInvoiceAdjustment, boolean isVirtual) throws BusinessException {

        long startDate = System.currentTimeMillis();
        boolean entreprise = appProvider.isEntreprise();
        BigDecimal nonEnterprisePriceWithTax = BigDecimal.ZERO;
        String languageCode = billingAccount.getTradingLanguage().getLanguageCode();
        boolean isExonerated = billingAccountService.isExonerated(billingAccount);
        int invoiceRounding = appProvider.getInvoiceRounding();
        RoundingModeEnum invoiceRoundingMode = appProvider.getInvoiceRoundingMode();

        if (firstTransactionDate == null) {
            firstTransactionDate = new Date(0);
        }
        
        //TODO[Edward] If !isExonerated then check if there is a tax script inside invoice.invoiceType.
        // Create a new boolean calculateTax=!isExonerated and invoice.invoiceType.taxScript is null.
        // DONE
        String calculateExternalTax = (String) customFieldInstanceService.getCFValue(appProvider, "OPENCELL_ENABLE_TAX_CALCULATION");
        if(StringUtils.isBlank(calculateExternalTax)) {
            calculateExternalTax = "NO";
        }
        boolean calculateTax = !isExonerated && invoice.getInvoiceType().getTaxScript() == null;
        
        if(ratedTransactions == null || ratedTransactions.isEmpty()) {
            ratedTransactions = (List<RatedTransaction>) getEntityManager().createNamedQuery("RatedTransaction.listAllRTByBillingAccount", RatedTransaction.class)
                    .setParameter("billingAccount", billingAccount).getResultList();
        }
        
        Map<InvoiceSubCategory, BigDecimal> minAmountsWithTax = new HashMap<InvoiceSubCategory, BigDecimal>();
        for (RatedTransaction ratedTransaction : ratedTransactions) {
        	if(ratedTransaction.getWallet() == null) {
        		invoice.addAmountWithoutTax((ratedTransaction.getAmountWithoutTax()).setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));
                invoice.addAmountWithTax((ratedTransaction.getAmountWithTax()).setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));
                invoice.addAmountTax((ratedTransaction.getAmountTax()).setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));
                if(minAmountsWithTax.get(ratedTransaction.getInvoiceSubCategory()) == null) {
                	minAmountsWithTax.put(ratedTransaction.getInvoiceSubCategory(), BigDecimal.ZERO);
                }
                minAmountsWithTax.put(ratedTransaction.getInvoiceSubCategory(), minAmountsWithTax.get(ratedTransaction.getInvoiceSubCategory()).add(ratedTransaction.getAmountWithTax()));
        	}
        }

        List<UserAccount> userAccounts = billingAccount.getUsersAccounts();

        for (UserAccount userAccount : userAccounts) {
            WalletInstance wallet = userAccount.getWallet();
            List<Object[]> invoiceSubCats = new ArrayList<>();

            // Filter only valid transactions for a particular UA (compare transaction.wallet.id = ua.wallet.id) and that transaction was not processed yet
            for (RatedTransaction ratedTransaction : ratedTransactions) {
            	
            	if(ratedTransaction.getWallet() == null) {
            		continue;
            	}

                boolean recordValid = (ratedTransaction.getStatus() == RatedTransactionStatusEnum.OPEN) && ratedTransaction.getWallet().getId() == wallet.getId()
                        && (ratedTransaction.getInvoice() == null);
                if (lastTransactionDate != null) { // usageDate
                    recordValid &= ratedTransaction.getUsageDate().before(lastTransactionDate);
                }

                if (!recordValid) {
                    continue;
                }

                Object[] record = new Object[5];
                record[0] = ratedTransaction.getInvoiceSubCategory().getId();
                record[1] = ratedTransaction.getAmountWithoutTax();
                record[2] = ratedTransaction.getAmountWithTax();
                record[3] = ratedTransaction.getAmountTax();
                record[4] = ratedTransaction.getQuantity();

                invoice.getRatedTransactions().add(ratedTransaction);

                boolean foundRecordForSameId = false;
                for (Object[] existingRecord : invoiceSubCats) {
                    if (existingRecord[0].equals(record[0])) {
                        foundRecordForSameId = true;
                        existingRecord[1] = ((BigDecimal) existingRecord[1]).add((BigDecimal) record[1]);
                        existingRecord[2] = ((BigDecimal) existingRecord[2]).add((BigDecimal) record[2]);
                        existingRecord[3] = ((BigDecimal) existingRecord[3]).add((BigDecimal) record[3]);
                        existingRecord[4] = ((BigDecimal) existingRecord[4]).add((BigDecimal) record[4]);
                        break;
                    }
                }
                if (!foundRecordForSameId) {
                    invoiceSubCats.add(record);
                }
            }
            
            // No rated transactions for user account, so continue with a next one
            if (invoiceSubCats == null || invoiceSubCats.isEmpty()) {
                continue;
            }

            List<InvoiceAgregate> invoiceAgregateSubcatList = new ArrayList<>();

            Map<Long, CategoryInvoiceAgregate> catInvoiceAgregateMap = new HashMap<>();
            Map<String, TaxInvoiceAgregate> taxInvoiceAgregateMap = new HashMap<>();

            SubCategoryInvoiceAgregate biggestSubCat = null;
            BigDecimal biggestAmount = new BigDecimal("-100000000");

            for (Object[] invoiceSubCatInfo : invoiceSubCats) {
                Long subcategoryId = (Long) invoiceSubCatInfo[0];
                BigDecimal amountWithoutTax = ((BigDecimal) invoiceSubCatInfo[1]).setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode());
                BigDecimal amountWithTax = ((BigDecimal) invoiceSubCatInfo[2]).setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode());
                BigDecimal amountTax = ((BigDecimal) invoiceSubCatInfo[3]).setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode());
                BigDecimal quantity = ((BigDecimal) invoiceSubCatInfo[4]).setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode());

                log.info("invoice subcategory {}, amountWithoutTax {}, amountWithTax {}, amountTax {}", subcategoryId, amountWithoutTax, amountWithTax, amountTax);

                InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findById(subcategoryId);

                List<Tax> taxes = new ArrayList<>();
                
                // TODO[Edward] If there is a global tax script in the invoiceType, just skip this part until after the next for-loop.
                // DONE
                boolean taxExternal = false;
                if (invoice.getInvoiceType() != null && invoice.getInvoiceType().getTaxScript() == null) {

                    // TODO[Edward] If there is a taxScript in invoiceSubCategory and script.isApplicable(userAccount, invoice, invoiceSubCategory)=true,
                    // use it to compute the taxes (script.computeTaxes(userAccount, invoice, invoiceSubCategory) return List<Tax>).
                    // parameters: userAccount, invoice, invoiceSubCategory
                    // result: List<Tax>
                    // DONE
                    if (invoiceSubCategory.getTaxScript() != null && calculateExternalTax.equals("YES")) {
                        if (taxScriptService.isApplicable(invoiceSubCategory.getTaxScript().getCode(), userAccount, invoice, invoiceSubCategory)) {
                            taxes = taxScriptService.computeTaxes(invoiceSubCategory.getTaxScript().getCode(), userAccount, invoice, invoiceSubCategory);
                            taxExternal = true;
                        }
                        
                    } else {
                        // else use this for loop
                        for (InvoiceSubcategoryCountry invoicesubcatCountry : invoiceSubCategory.getInvoiceSubcategoryCountries()) {
                            if (invoicesubcatCountry.getTradingCountry().getCountryCode().equalsIgnoreCase(billingAccount.getTradingCountry().getCountryCode())
                                    && invoiceSubCategoryService.matchInvoicesubcatCountryExpression(invoicesubcatCountry.getFilterEL(), billingAccount, invoice)) {
                                Tax tax = invoiceSubCategoryCountryService.isInvoiceSubCategoryTaxValid(invoicesubcatCountry, userAccount, billingAccount, invoice, new Date());
                                if (tax != null) {
                                    taxes.add(tax);
                                }
                            }
                        }
                    }                    
                }
                
                // start aggregate F

                SubCategoryInvoiceAgregate invoiceAgregateSubcat = new SubCategoryInvoiceAgregate();
                invoiceAgregateSubcat.updateAudit(currentUser);
                invoiceAgregateSubcat.setInvoiceSubCategory(invoiceSubCategory);
                invoiceAgregateSubcat.setInvoice(invoice);

                String translationSCKey = "SC_" + invoiceSubCategory.getCode() + "_" + languageCode;
                String invSubCatDescTranslated = descriptionMap.get(translationSCKey);
                if (invSubCatDescTranslated == null) {
                    invSubCatDescTranslated = invoiceSubCategory.getDescriptionOrCode();
                    if (invoiceSubCategory.getDescriptionI18n() != null && invoiceSubCategory.getDescriptionI18n().get(languageCode) != null) {
                        invSubCatDescTranslated = invoiceSubCategory.getDescriptionI18n().get(languageCode);
                    }
                    descriptionMap.put(translationSCKey, invSubCatDescTranslated);
                }

                invoiceAgregateSubcat.setDescription(invSubCatDescTranslated);

                if (!isVirtual) {
                    invoiceAgregateSubcat.setBillingRun(billingAccount.getBillingRun());
                }
                invoiceAgregateSubcat.setWallet(wallet);
                invoiceAgregateSubcat.setAccountingCode(invoiceSubCategory.getAccountingCode());
                fillAgregates(invoiceAgregateSubcat, userAccount);

                invoiceAgregateSubcat.setAmountWithoutTax(amountWithoutTax);
                invoiceAgregateSubcat.setAmountWithTax(amountWithTax);
                invoiceAgregateSubcat.setAmountTax(amountTax);
                invoiceAgregateSubcat.setQuantity(quantity);
                invoiceAgregateSubcatList.add(invoiceAgregateSubcat);
                
                // end aggregate F

                if (!entreprise) {
                    nonEnterprisePriceWithTax = nonEnterprisePriceWithTax.add(amountWithTax);
                    if(minAmountsWithTax.get(invoiceSubCategory) != null) {
                    	nonEnterprisePriceWithTax = nonEnterprisePriceWithTax.add(minAmountsWithTax.get(invoiceSubCategory));
                    }
                }

                // start aggregate T
                // if (!isExonerated) {
                if (calculateTax) {

                    for (Tax tax : taxes) {
                        TaxInvoiceAgregate invoiceAgregateTax = null;
                        String taxId = String.valueOf(tax.getId());

                        if(taxExternal) {
                            taxId = tax.getCode();
                            }

                        if (taxInvoiceAgregateMap.containsKey(taxId)) {
                            invoiceAgregateTax = taxInvoiceAgregateMap.get(taxId);
                        } else {
                            invoiceAgregateTax = new TaxInvoiceAgregate();
                            invoiceAgregateTax.updateAudit(currentUser);
                            invoiceAgregateTax.setInvoice(invoice);
                            if (!isVirtual) {
                                invoiceAgregateTax.setBillingRun(billingAccount.getBillingRun());
                            }
                            if (!taxExternal) {
                            invoiceAgregateTax.setTax(tax);
                            }
                            invoiceAgregateTax.setTaxPercent(tax.getPercent());
                            invoiceAgregateTax.setAccountingCode(tax.getAccountingCode());

                            taxInvoiceAgregateMap.put(taxId, invoiceAgregateTax);
                        }

                        if (tax.getPercent().compareTo(BigDecimal.ZERO) == 0) {
                            invoiceAgregateTax.addAmountWithoutTax(invoiceAgregateSubcat.getAmountWithoutTax());
                            invoiceAgregateTax.setAmountTax(BigDecimal.ZERO);
                            invoiceAgregateTax.addAmountWithTax(invoiceAgregateSubcat.getAmountWithoutTax());
                        }
                        fillAgregates(invoiceAgregateTax, userAccount);

                        if(taxExternal) {
                            invoiceAgregateSubcat.addSubCategoryTaxTransient(tax);
                        } else {
                        invoiceAgregateSubcat.addSubCategoryTax(tax);
                    }
                }
                }
                
                // end aggregate T

                // start aggregate R
                
                CategoryInvoiceAgregate invoiceAgregateCat = null;
                Long invoiceCategoryId = invoiceSubCategory.getInvoiceCategory().getId();

                if (catInvoiceAgregateMap.containsKey(invoiceCategoryId)) {
                    invoiceAgregateCat = catInvoiceAgregateMap.get(invoiceCategoryId);
                } else {

                    invoiceAgregateCat = new CategoryInvoiceAgregate();
                    invoiceAgregateCat.updateAudit(currentUser);
                    invoiceAgregateCat.setInvoice(invoice);
                    if (!isVirtual) {
                        invoiceAgregateCat.setBillingRun(billingAccount.getBillingRun());
                    }

                    String translationCKey = "C_" + invoiceSubCategory.getInvoiceCategory().getCode() + "_" + languageCode;
                    String invCatDescTranslated = descriptionMap.get(translationCKey);
                    if (invCatDescTranslated == null) {
                        invCatDescTranslated = invoiceSubCategory.getInvoiceCategory().getDescriptionOrCode();
                        if (invoiceSubCategory.getInvoiceCategory().getDescriptionI18n() != null
                                && invoiceSubCategory.getInvoiceCategory().getDescriptionI18n().get(languageCode) != null) {
                            invCatDescTranslated = invoiceSubCategory.getInvoiceCategory().getDescriptionI18n().get(languageCode);
                        }
                        descriptionMap.put(translationCKey, invCatDescTranslated);
                    }

                    invoiceAgregateCat.setDescription(invCatDescTranslated);
                    invoiceAgregateCat.setInvoiceCategory(invoiceSubCategory.getInvoiceCategory());
                    catInvoiceAgregateMap.put(invoiceCategoryId, invoiceAgregateCat);
                }

                fillAgregates(invoiceAgregateCat, userAccount);
                invoiceAgregateSubcat.setCategoryInvoiceAgregate(invoiceAgregateCat);

                // end agregate R

                // round the amount without Tax
                // compute the largest subcategory

                // first we round the amount without tax

                log.debug("subcat " + invoiceAgregateSubcat.getAccountingCode() + " ht=" + invoiceAgregateSubcat.getAmountWithoutTax() + " ->"
                        + invoiceAgregateSubcat.getAmountWithoutTax().setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));
                invoiceAgregateSubcat.setAmountWithoutTax(invoiceAgregateSubcat.getAmountWithoutTax().setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));
                
                // add it to taxAggregate and CategoryAggregate
                Set<Tax> subCategoryTaxes = invoiceAgregateSubcat.getSubCategoryTaxes();
                if(invoiceAgregateSubcat.getSubCategoryTaxesTransient() != null && !invoiceAgregateSubcat.getSubCategoryTaxesTransient().isEmpty()) {
                    subCategoryTaxes = invoiceAgregateSubcat.getSubCategoryTaxesTransient();
                }

                if(!subCategoryTaxes.isEmpty()) {
	                for (Tax tax : subCategoryTaxes) {
	                	// TODO[Edward] Replace !isExonerated by calculateTax
	                    //if (tax.getPercent().compareTo(BigDecimal.ZERO) != 0 && !isExonerated) {
	                    if (tax.getPercent().compareTo(BigDecimal.ZERO) != 0 && calculateTax) {
	                        TaxInvoiceAgregate taxInvoiceAgregate = taxInvoiceAgregateMap.get(String.valueOf(tax.getIdOrCode()));
                        taxInvoiceAgregate.addAmountWithoutTax(invoiceAgregateSubcat.getAmountWithoutTax());
                        taxInvoiceAgregate.setAmountWithoutTax(taxInvoiceAgregate.getAmountWithoutTax().setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));
                        log.info("  tax " + tax.getPercent() + " ht ->" + taxInvoiceAgregate.getAmountWithoutTax());
                    }
                }
                }

                invoiceAgregateSubcat.getCategoryInvoiceAgregate().addAmountWithoutTax(invoiceAgregateSubcat.getAmountWithoutTax());
                log.debug("  cat {}  ht -> {}", invoiceAgregateSubcat.getCategoryInvoiceAgregate().getId(),
                    invoiceAgregateSubcat.getCategoryInvoiceAgregate().getAmountWithoutTax());

                if (invoiceAgregateSubcat.getAmountWithoutTax().compareTo(biggestAmount) > 0) {
                    biggestAmount = invoiceAgregateSubcat.getAmountWithoutTax();
                    biggestSubCat = invoiceAgregateSubcat;
                }
            }

            // compute the tax
            // TODO[Edward] Replace !isExonerated by calculateTax
            // if (!isExonerated) {
            if (calculateTax) {
                for (Map.Entry<String, TaxInvoiceAgregate> taxCatMap : taxInvoiceAgregateMap.entrySet()) {
                    TaxInvoiceAgregate taxInvoiceAggregate = taxCatMap.getValue();
                    BigDecimal taxPercent = taxInvoiceAggregate.getTaxPercent() != null ? taxInvoiceAggregate.getTaxPercent() : taxInvoiceAggregate.getTax().getPercent();
                    if (taxPercent.compareTo(BigDecimal.ZERO) != 0) {
                        // then compute the tax
                        taxInvoiceAggregate.setAmountTax(taxInvoiceAggregate.getAmountWithoutTax().multiply(taxInvoiceAggregate.getTaxPercent()).divide(new BigDecimal("100")));
                        // then round the tax
                        taxInvoiceAggregate.setAmountTax(taxInvoiceAggregate.getAmountTax().setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));

                        // and compute amount with tax
                        /*
                         * taxCat.setAmountWithTax(taxCat.getAmountWithoutTax().add(taxCat.getAmountTax()) .setScale(rounding, RoundingMode.HALF_UP));
                         */
                        log.debug("  tax2 ht ->" + taxInvoiceAggregate.getAmountWithoutTax());
                    }

                }
            }

            for (Map.Entry<Long, CategoryInvoiceAgregate> cat : catInvoiceAgregateMap.entrySet()) {
                CategoryInvoiceAgregate categoryInvoiceAgregate = cat.getValue();
                invoice.addAmountWithoutTax(categoryInvoiceAgregate.getAmountWithoutTax().setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));
            }
            
            // TODO [Edward] if(!isExonerated && !calculateTax) then call the global
            // taxInvoiceAgregateMap = invoice.invoiceType.taxScript.computeTaxAggregateMap(invoice, catInvoiceAgregateMap)
            if (!isExonerated && !calculateTax) {
                if (invoice.getInvoiceType() != null && invoice.getInvoiceType().getTaxScript() != null) {
                    taxInvoiceAgregateMap = taxScriptService.computeTaxAggregateMap(invoice.getInvoiceType().getTaxScript().getCode(), invoice, catInvoiceAgregateMap);
                }
            }
            
            for (Map.Entry<String, TaxInvoiceAgregate> tax : taxInvoiceAgregateMap.entrySet()) {
                TaxInvoiceAgregate taxInvoiceAgregate = tax.getValue();
                invoice.addAmountTax(taxInvoiceAgregate.getAmountTax().setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));
            }
            
            if (invoice.getAmountWithoutTax() != null) {
                invoice.setAmountWithTax(invoice.getAmountWithoutTax().add(invoice.getAmountTax() == null ? BigDecimal.ZERO : invoice.getAmountTax()));
            }
            
            BigDecimal balance = BigDecimal.ZERO;
            if (!entreprise && biggestSubCat != null && !isExonerated) {
                BigDecimal delta = nonEnterprisePriceWithTax.subtract(invoice.getAmountWithTax());
                log.debug("delta= " + nonEnterprisePriceWithTax + " - " + invoice.getAmountWithTax() + "=" + delta);
                biggestSubCat.setAmountWithoutTax(biggestSubCat.getAmountWithoutTax().add(delta).setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));
                
                Set<Tax> subCategoryTaxes = biggestSubCat.getSubCategoryTaxes();
                if(biggestSubCat.getSubCategoryTaxesTransient() != null && !biggestSubCat.getSubCategoryTaxesTransient().isEmpty()) {
                    subCategoryTaxes = biggestSubCat.getSubCategoryTaxesTransient();
                }
                
                for (Tax tax : biggestSubCat.getSubCategoryTaxes()) {

                    TaxInvoiceAgregate invoiceAgregateT = taxInvoiceAgregateMap.get(tax.getId());
                    if (invoiceAgregateT!=null){ // AKK removed for testing
                    log.debug("  tax3 ht ->" + invoiceAgregateT.getAmountWithoutTax());
                    invoiceAgregateT.setAmountWithoutTax(invoiceAgregateT.getAmountWithoutTax().add(delta).setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));
                    log.debug("  tax4 ht ->" + invoiceAgregateT.getAmountWithoutTax());
                    }

                }
                CategoryInvoiceAgregate invoiceAgregateR = biggestSubCat.getCategoryInvoiceAgregate();
                invoiceAgregateR.setAmountWithoutTax(invoiceAgregateR.getAmountWithoutTax().add(delta).setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));

                invoice.setAmountWithoutTax(invoice.getAmountWithoutTax().add(delta).setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));
                invoice.setAmountWithTax(nonEnterprisePriceWithTax.setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode()));

            }

            appendInvoiceDiscountAggregates(userAccount, isExonerated, invoice, taxInvoiceAgregateMap, isVirtual);
        }

        BigDecimal invoicingThreshold = billingAccount.getInvoicingThreshold() == null ? billingAccount.getBillingCycle().getInvoicingThreshold()
                : billingAccount.getInvoicingThreshold();
        if (invoicingThreshold != null && invoice.getAmountWithoutTax() != null) {
            if (invoicingThreshold.compareTo(invoice.getAmountWithoutTax()) > 0) {
                throw new BusinessException("Invoice amount below the threshold");
            }
        }

        BigDecimal discountAmountWithoutTax = BigDecimal.ZERO;
        BigDecimal discountAmountTax = BigDecimal.ZERO;
        BigDecimal discountAmountWithTax = BigDecimal.ZERO;

        Object[] object = new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO };
        if (!isVirtual) {
            // invoiceAgregateService.findTotalAmountsForDiscountAggregates(invoice);
            discountAmountWithoutTax = (BigDecimal) object[0];
            discountAmountTax = (BigDecimal) object[1];
            discountAmountWithTax = (BigDecimal) object[2];

        } else {
            for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {
                if (invoiceAgregate instanceof SubCategoryInvoiceAgregate && ((SubCategoryInvoiceAgregate)invoiceAgregate).isDiscountAggregate()) {
                    discountAmountWithoutTax.add(invoiceAgregate.getAmountWithoutTax());
                    discountAmountTax.add(invoiceAgregate.getAmountTax());
                    discountAmountWithTax.add(invoiceAgregate.getAmountWithTax());
                }
            }
        }

        log.info("discountAmountWithoutTax= {},discountAmountTax={},discountAmountWithTax={}", discountAmountWithoutTax, discountAmountTax, discountAmountWithTax);

        invoice.addAmountWithoutTax(discountAmountWithoutTax);
        invoice.addAmountTax(discountAmountTax);
        invoice.addAmountWithTax(discountAmountWithTax);

        invoice.setAmountWithoutTax(round(invoice.getAmountWithoutTax(), invoiceRounding, invoiceRoundingMode));
        invoice.setAmountTax(round(invoice.getAmountTax(), invoiceRounding, invoiceRoundingMode));
        invoice.setAmountWithTax(round(invoice.getAmountWithTax(), invoiceRounding, invoiceRoundingMode));

        BigDecimal netToPay = BigDecimal.ZERO;
        if (entreprise) {
            netToPay = invoice.getAmountWithTax();
        } else {
            BigDecimal balance = BigDecimal.ZERO;
            if (!isVirtual) {
                // FIXME : what is the need of this line code ? may its return should be set to balance !?
                customerAccountService.customerAccountBalanceDue(invoice.getBillingAccount().getCustomerAccount(), invoice.getDueDate());

                if (balance == null) {
                    throw new BusinessException("account balance calculation failed");
                }
            }
            netToPay = invoice.getAmountWithTax().add(round(balance, invoiceRounding, invoiceRoundingMode));
        }
        invoice.setNetToPay(netToPay);
        
        for (RatedTransaction ratedTransaction : ratedTransactions) {
        	if(ratedTransaction.getWallet() == null) {
        		invoice.getRatedTransactions().add(ratedTransaction);
        	}
        }
    }

    /**
     * @param invoiceAgregate invoice agregate
     * @param userAccount user account
     */
    private void fillAgregates(InvoiceAgregate invoiceAgregate, UserAccount userAccount) {
        invoiceAgregate.setBillingAccount(userAccount.getBillingAccount());
        invoiceAgregate.setUserAccount(userAccount);
        int itemNumber = invoiceAgregate.getItemNumber() != null ? invoiceAgregate.getItemNumber() + 1 : 1;
        invoiceAgregate.setItemNumber(itemNumber);
    }

    /**
     * @param billingAccount billing acount
     * @param firstTransactionDate date of first transaction
     * @param lastTransactionDate date of last transaction
     * @return
     */

    /**
     * @param billingAccount billing account
     * @param firstTransactionDate date of first transaction
     * @param lastTransactionDate date of last transaction
     * @return true/false
     */
    public Boolean isBillingAccountBillable(BillingAccount billingAccount, Date firstTransactionDate, Date lastTransactionDate) {
        long count = 0;
        if (firstTransactionDate == null) {
            firstTransactionDate = new Date(0);
        }
        TypedQuery<Long> q = getEntityManager().createNamedQuery("RatedTransaction.countNotInvoinced", Long.class);
        count = q.setParameter("billingAccount", billingAccount).setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate)
            .getSingleResult();
        log.debug("isBillingAccountBillable code={},lastTransactionDate={}) : {}", billingAccount.getCode(), lastTransactionDate, count);
        return count > 0 ? true : false;
    }

    /**
     * @param billingAccount billing account
     * @param orderNumber order number.
     * @param firstTransactionDate firstTransactionDate.
     * @param lastTransactionDate lastTransactionDate.
     * @return true/false
     */
    public Boolean isBillingAccountBillable(BillingAccount billingAccount, String orderNumber, Date firstTransactionDate, Date lastTransactionDate) {
        long count = 0;
        TypedQuery<Long> q = getEntityManager().createNamedQuery("RatedTransaction.countListToInvoiceByOrderNumber", Long.class);
        count = q.setParameter("orderNumber", orderNumber).setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate)
            .getSingleResult();
        log.debug("isBillingAccountBillable code={},orderNumber={}) : {}", billingAccount.getCode(), orderNumber, count);
        return count > 0 ? true : false;
    }

    /**
     * This method is only for generating Xml invoice {@link org.meveo.service.billing.impl.XMLInvoiceCreator #createXMLInvoice(Long, java.io.File, boolean, boolean)
     * createXMLInvoice}
     * <p>
     * If the provider's displayFreeTransacInInvoice of the current invoice is <tt>false</tt>, RatedTransaction with amount=0 don't show up in the XML.
     * </p>
     * 
     * @param wallet wallet instance
     * @param invoice invoice
     * @param invoiceSubCategory invoice sub category
     * @return list of rated transaction.
     */
    public List<RatedTransaction> getRatedTransactionsForXmlInvoice(WalletInstance wallet, Invoice invoice, InvoiceSubCategory invoiceSubCategory) {

        QueryBuilder qb = new QueryBuilder(RatedTransaction.class, "c", Arrays.asList("priceplan"));
        qb.addCriterionEntity("c.wallet", wallet);
        qb.addCriterionEntity("c.invoiceSubCategory", invoiceSubCategory);
        qb.addCriterionEnum("c.status", RatedTransactionStatusEnum.BILLED);
        qb.addCriterionEntity("c.invoice", invoice);

        if (!appProvider.isDisplayFreeTransacInInvoice()) {
            qb.addCriterion("c.amountWithoutTax", "<>", BigDecimal.ZERO, false);
        }

        qb.addOrderCriterionAsIs("c.usageDate", true);

        @SuppressWarnings("unchecked")
        List<RatedTransaction> ratedTransactions = qb.getQuery(getEntityManager()).getResultList();

        return ratedTransactions;

    }

    /**
     * @param invoice invoice to get rated transaction.
     * @return list of rated transactions
     */
    public List<RatedTransaction> getRatedTransactionsForXmlInvoice(Invoice invoice) {

        QueryBuilder qb = new QueryBuilder(RatedTransaction.class, "c", Arrays.asList("priceplan"));
        qb.addCriterionEnum("c.status", RatedTransactionStatusEnum.BILLED);
        qb.addCriterionEntity("c.invoice", invoice);

        if (!appProvider.isDisplayFreeTransacInInvoice()) {
            qb.addCriterion("c.amountWithoutTax", "<>", BigDecimal.ZERO, false);
        }

        qb.addOrderCriterionAsIs("c.usageDate", true);
        @SuppressWarnings("unchecked")
        List<RatedTransaction> ratedTransactions = qb.getQuery(getEntityManager()).getResultList();

        return ratedTransactions;

    }

    /**
     * @param wallet wallet contains the wallet operation.
     * @param invoice invoice to get from
     * @param invoiceSubCategory invoice sub category
     * @return list of rated transactions.
     */
    public List<RatedTransaction> getRatedTransactions(WalletInstance wallet, Invoice invoice, InvoiceSubCategory invoiceSubCategory) {

        QueryBuilder qb = new QueryBuilder(RatedTransaction.class, "c", Arrays.asList("priceplan"));
        qb.addCriterionEnum("c.status", RatedTransactionStatusEnum.BILLED);
        qb.addCriterionEntity("c.wallet", wallet);
        qb.addCriterionEntity("c.invoice", invoice);
        qb.addCriterionEntity("c.invoiceSubCategory", invoiceSubCategory);
        qb.addOrderCriterionAsIs("c.usageDate", true);

        @SuppressWarnings("unchecked")
        List<RatedTransaction> ratedTransactions = qb.getQuery(getEntityManager()).getResultList();

        return ratedTransactions;

    }

    /**
     * @param id wallet operation id
     * @return re-rated transaction id
     * @throws UnrolledbackBusinessException un rolledback business exception
     */
    public int reratedByWalletOperationId(Long id) throws UnrolledbackBusinessException {
        int result = 0;
        List<RatedTransaction> ratedTransactions = (List<RatedTransaction>) getEntityManager().createNamedQuery("RatedTransaction.listByWalletOperationId", RatedTransaction.class)
            .setParameter("walletOperationId", id).getResultList();
        for (RatedTransaction ratedTransaction : ratedTransactions) {
            BillingRun billingRun = ratedTransaction.getBillingRun();
            if (billingRun != null && billingRun.getStatus() != BillingRunStatusEnum.CANCELED) {
                throw new UnrolledbackBusinessException("A rated transaction " + ratedTransaction.getId() + " forbid rerating of wallet operation " + id);
            }
            ratedTransaction.setStatus(RatedTransactionStatusEnum.RERATED);
            result++;
        }
        return result;
    }

    /**
     * @param walletOperationId wallet operation i
     * @return list of rated transactions
     */
    @SuppressWarnings("unchecked")
    public List<RatedTransaction> getNotBilledRatedTransactions(Long walletOperationId) {
        QueryBuilder qb = new QueryBuilder(RatedTransaction.class, "c");
        qb.addCriterionEntity("c.walletOperationId", walletOperationId);
        qb.addCriterion("c.status", "!=", RatedTransactionStatusEnum.BILLED, false);
        try {
            return (List<RatedTransaction>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            log.warn("error on get not billed rated transactions ", e);
            return null;
        }

    }

    /**
     * @param BillingRun billing run
     * @return list of rated transactions for given billing run.
     */
    @SuppressWarnings("unchecked")
    public List<RatedTransaction> getRatedTransactionsByBillingRun(BillingRun BillingRun) {
        QueryBuilder qb = new QueryBuilder(RatedTransaction.class, "c");
        qb.addCriterionEntity("c.billingRun", BillingRun);
        try {
            return (List<RatedTransaction>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            log.warn("failed to get ratedTransactions ny nillingRun", e);
            return null;
        }

    }

    /**
     * @param userAccount user account
     * @param invoice invoice
     * @param taxInvoiceAgregateMap map of tax invoice agregate
     * @param isVirtual true/false
     */
    private void appendInvoiceDiscountAggregates(UserAccount userAccount, boolean isExonerated, Invoice invoice, Map<String, TaxInvoiceAgregate> taxInvoiceAgregateMap,
            boolean isVirtual) {
        try {

            BillingAccount billingAccount = userAccount.getBillingAccount();
            DiscountPlan discountPlan = billingAccount.getDiscountPlan();
            if (discountPlan != null && discountPlan.isActive()) {
                CustomerAccount customerAccount = billingAccount.getCustomerAccount();
                List<DiscountPlanItem> discountPlanItems = discountPlan.getDiscountPlanItems();

                for (DiscountPlanItem discountPlanItem : discountPlanItems) {
                    if (discountPlanItem.isActive() && matchDiscountPlanItemExpression(discountPlanItem.getExpressionEl(), customerAccount, billingAccount, invoice)) {

                        // Apply discount to a particular invoice subcategory
                        if (discountPlanItem.getInvoiceSubCategory() != null) {
                            appendDiscountAggregate(userAccount, isExonerated, userAccount.getWallet(), invoice, discountPlanItem.getInvoiceSubCategory(), discountPlanItem,
                                taxInvoiceAgregateMap, isVirtual);

                            // Apply discount to all subcategories of a particular invoice category
                        } else if (discountPlanItem.getInvoiceCategory() != null) {
                            InvoiceCategory invoiceCat = discountPlanItem.getInvoiceCategory();
                            for (InvoiceSubCategory invoiceSubCat : invoiceCat.getInvoiceSubCategories()) {
                                appendDiscountAggregate(userAccount, isExonerated, userAccount.getWallet(), invoice, invoiceSubCat, discountPlanItem, taxInvoiceAgregateMap,
                                    isVirtual);
                            }

                            // Apply discount to all subcategories in the invoice
                        } else {

                            List<InvoiceSubCategory> allSubcategories = new ArrayList<>();
                            for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {

                                if (invoiceAgregate instanceof SubCategoryInvoiceAgregate
                                        && ((SubCategoryInvoiceAgregate) invoiceAgregate).getWallet().equals(userAccount.getWallet()) && !((SubCategoryInvoiceAgregate) invoiceAgregate).isDiscountAggregate()) {
                                    allSubcategories.add(((SubCategoryInvoiceAgregate) invoiceAgregate).getInvoiceSubCategory());
                                }
                            }
                            for (InvoiceSubCategory invoiceSubCategory : allSubcategories) {
                                appendDiscountAggregate(userAccount, isExonerated, userAccount.getWallet(), invoice, invoiceSubCategory, discountPlanItem, taxInvoiceAgregateMap,
                                    isVirtual);
                            }
                        }
                    }
                }
            }

        } catch (BusinessException e) {
            log.error("Error when trying to create discount aggregates", e);
        }

    }

    /**
     * @param userAccount user account
     * @param wallet wallet
     * @param invoice invoice from to create
     * @param invoiceSubCat sub category of invoice
     * @param discountPlanItem item of discount plan
     * @param taxInvoiceAgregateMap map of tax invoice agregate
     * @param isVirtual true/false
     * @throws BusinessException business exception
     */
    private void appendDiscountAggregate(UserAccount userAccount, boolean isExonerated, WalletInstance wallet, Invoice invoice, InvoiceSubCategory invoiceSubCat,
            DiscountPlanItem discountPlanItem, Map<String, TaxInvoiceAgregate> taxInvoiceAgregateMap, boolean isVirtual) throws BusinessException {

        BillingAccount billingAccount = userAccount.getBillingAccount();
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal discountPercent = discountPlanItem.getPercent();

        // AKK commented out, as why to call service if can be done locally in a simple loop
        // if (!isVirtual) {
        // amount = invoiceAgregateService.findTotalAmountByWalletSubCat(wallet, invoiceSubCat, invoice);
        // } else {
        for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {
            if (invoiceAgregate instanceof SubCategoryInvoiceAgregate && ((SubCategoryInvoiceAgregate) invoiceAgregate).getWallet().equals(wallet)
                    && ((SubCategoryInvoiceAgregate) invoiceAgregate).getInvoiceSubCategory().equals(invoiceSubCat) && !((SubCategoryInvoiceAgregate) invoiceAgregate).isDiscountAggregate()) {
                amount = amount.add(invoiceAgregate.getAmountWithoutTax());
            }
        }
        // }

        if (amount != null && !BigDecimal.ZERO.equals(amount)) {
            if (discountPlanItem.getDiscountPercentEl() != null) {
                discountPercent = evaluateDiscountPercentExpression(discountPlanItem.getDiscountPercentEl(), userAccount, wallet, invoice, amount);
                log.debug("for discountPlan " + discountPlanItem.getCode() + " percentEL ->" + discountPercent + " on amount=" + amount);
            }
            BigDecimal discountAmountWithoutTax = amount.multiply(discountPercent.divide(HUNDRED)).negate();
            // BigDecimal discountAmountWithoutTax=amount.multiply(discountPlanItem.getPercent().divide(HUNDRED)).negate();
            List<Tax> taxes = new ArrayList<Tax>();
            for (InvoiceSubcategoryCountry invoicesubcatCountry : invoiceSubCat.getInvoiceSubcategoryCountries()) {
                if ((invoicesubcatCountry.getSellingCountry() == null
                        || (billingAccount.getCustomerAccount().getCustomer().getSeller().getTradingCountry() != null && invoicesubcatCountry.getSellingCountry().getCountryCode()
                            .equalsIgnoreCase(billingAccount.getCustomerAccount().getCustomer().getSeller().getTradingCountry().getCountryCode())))
                        && (invoicesubcatCountry.getTradingCountry() == null
                                || invoicesubcatCountry.getTradingCountry().getCountryCode().equalsIgnoreCase(invoice.getBillingAccount().getTradingCountry().getCountryCode()))
                        && invoiceSubCategoryService.matchInvoicesubcatCountryExpression(invoicesubcatCountry.getFilterEL(), billingAccount, invoice)) {
                    if (StringUtils.isBlank(invoicesubcatCountry.getTaxCodeEL())) {
                        taxes.add(invoicesubcatCountry.getTax());
                    } else {
                        taxes.add(invoiceSubCategoryService.evaluateTaxCodeEL(invoicesubcatCountry.getTaxCodeEL(), userAccount, billingAccount, invoice));
                    }
                }
            }

            SubCategoryInvoiceAgregate invoiceAgregateSubcat = new SubCategoryInvoiceAgregate();
            BigDecimal discountAmountTax = BigDecimal.ZERO;
            if (!isExonerated) {
                for (Tax tax : taxes) {
                    BigDecimal amountTax = discountAmountWithoutTax.multiply(tax.getPercent().divide(HUNDRED));
                    discountAmountTax = discountAmountTax.add(amountTax);
                    invoiceAgregateSubcat.addSubCategoryTax(tax);
                    TaxInvoiceAgregate taxInvoiceAgregate = taxInvoiceAgregateMap.get(tax.getId());
                    if (taxInvoiceAgregate != null) {
                        taxInvoiceAgregate.addAmountTax(amountTax);
                        taxInvoiceAgregate.addAmountWithoutTax(discountAmountWithoutTax);
                    }
                }
            }

            BigDecimal discountAmountWithTax = discountAmountWithoutTax.add(discountAmountTax);

            invoiceAgregateSubcat.updateAudit(currentUser);
            invoiceAgregateSubcat.setInvoice(invoice);
            if (!isVirtual) {
                invoiceAgregateSubcat.setBillingRun(billingAccount.getBillingRun());
            }
            invoiceAgregateSubcat.setWallet(userAccount.getWallet());
            invoiceAgregateSubcat.setAccountingCode(invoiceSubCat.getAccountingCode());
            fillAgregates(invoiceAgregateSubcat, userAccount);
            invoiceAgregateSubcat.setAmountWithoutTax(discountAmountWithoutTax);
            invoiceAgregateSubcat.setAmountWithTax(discountAmountWithTax);
            invoiceAgregateSubcat.setAmountTax(discountAmountTax);
            invoiceAgregateSubcat.setInvoiceSubCategory(invoiceSubCat);

            invoiceAgregateSubcat.setDiscountAggregate(true);
            invoiceAgregateSubcat.setDiscountPercent(discountPercent);
            // invoiceAgregateSubcat.setDiscountPercent(discountPlanItem.getPercent());
            invoiceAgregateSubcat.setDiscountPlanItem(discountPlanItem);
        }
    }

    /**
     * @param expression el expression
     * @param userAccount user account
     * @param wallet wallet
     * @param invoice invoice
     * @param subCatTotal total of sub category
     * @return amount
     * @throws BusinessException business exception
     */
    private BigDecimal evaluateDiscountPercentExpression(String expression, UserAccount userAccount, WalletInstance wallet, Invoice invoice, BigDecimal subCatTotal)
            throws BusinessException {

        if (StringUtils.isBlank(expression)) {
            return null;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("ca", userAccount.getBillingAccount().getCustomerAccount());
        userMap.put("ba", userAccount.getBillingAccount());
        userMap.put("iv", invoice);
        userMap.put("invoice", invoice);
        userMap.put("wa", wallet);
        userMap.put("amount", subCatTotal);
       
        BigDecimal result = ValueExpressionWrapper.evaluateExpression(expression, userMap, BigDecimal.class);
        return result;
    }

    /**
     * @param expression EL exprestion
     * @param customerAccount customer account
     * @param billingAccount billing account
     * @param invoice invoice
     * @return true/false
     * @throws BusinessException business exception.
     */
    private boolean matchDiscountPlanItemExpression(String expression, CustomerAccount customerAccount, BillingAccount billingAccount, Invoice invoice) throws BusinessException {
        Boolean result = true;

        if (StringUtils.isBlank(expression)) {
            return result;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();

        if (expression.indexOf("ca") >= 0) {
            userMap.put("ca", customerAccount);
        }
        if (expression.indexOf("ba") >= 0) {
            userMap.put("ba", billingAccount);
        }
        if (expression.indexOf("iv") >= 0) {
            userMap.put("iv", invoice);

        }
        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
        try {
            result = (Boolean) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
        }
        return result;
    }

    /**
     * @param invoice invoice
     * @param invoiceSubCategory sub category invoice
     * @return list of rated transaction
     */
    public List<RatedTransaction> getListByInvoiceAndSubCategory(Invoice invoice, InvoiceSubCategory invoiceSubCategory) {
        if (invoice == null || invoiceSubCategory == null) {
            return null;
        }
        return (List<RatedTransaction>) getEntityManager().createNamedQuery("RatedTransaction.getListByInvoiceAndSubCategory", RatedTransaction.class)
            .setParameter("invoice", invoice).setParameter("invoiceSubCategory", invoiceSubCategory).getResultList();
    }

    /**
     * @param walletOperationId wallet operation i
     * @throws BusinessException business exception
     */
    public void createRatedTransaction(Long walletOperationId) throws BusinessException {
        WalletOperation walletOperation = walletOperationService.findById(walletOperationId);

        createRatedTransaction(walletOperation, false);
    }

    /**
     * Create Rated transaction from wallet operation.
     * 
     * @param walletOperation Wallet operation
     * @param isVirtual Is charge event a virtual operation? If so, no entities should be created/updated/persisted in DB
     * @return Rated transaction
     * @throws BusinessException business exception
     */
    public RatedTransaction createRatedTransaction(WalletOperation walletOperation, boolean isVirtual) throws BusinessException {
        RatedTransaction ratedTransaction = new RatedTransaction(walletOperation);

        walletOperation.setStatus(WalletOperationStatusEnum.TREATED);

        if (!isVirtual) {
            create(ratedTransaction);
            walletOperationService.updateNoCheck(walletOperation);
        }
        return ratedTransaction;
    }

    /**
     * @param entity entity to bill
     * @param invoicingDate invoicing date
     * @throws BusinessException business exception.
     */
    public void createRatedTransaction(IBillableEntity entity, Date invoicingDate) throws BusinessException {
        List<WalletOperation> walletOps = new ArrayList<WalletOperation>();
        if(entity instanceof BillingAccount) {
            BillingAccount billingAccount = billingAccountService.findById(((BillingAccount)entity).getId());
            List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
            for (UserAccount ua : userAccounts) {
                walletOps.addAll(walletOperationService.listToInvoiceByUserAccount(invoicingDate, ua));
            }
        } else if(entity instanceof Subscription) {
            walletOps.addAll(walletOperationService.listToInvoiceBySubscription(invoicingDate, (Subscription) entity));
        } else if(entity instanceof Order) {
            walletOps.addAll(walletOperationService.listToInvoiceByOrder(invoicingDate, (Order) entity));
        }
        
        for (WalletOperation walletOp : walletOps) {
            createRatedTransaction(walletOp, false);
        }
        }

    /**
     * @param invoice invoice
     * @return list of rated transactions for given invoice
     */
    @SuppressWarnings("unchecked")
    public List<RatedTransaction> listByInvoice(Invoice invoice) {
        QueryBuilder qb = new QueryBuilder(RatedTransaction.class, "r");
        qb.addCriterionEntity("invoice", invoice);

        try {
            return (List<RatedTransaction>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param walletInstance wallet's instance
     * @param invoiceSubCategory invoice sub category
     * @return list of open rated transaction for a given invoice sub category.
     */
    public List<RatedTransaction> openRTbySubCat(WalletInstance walletInstance, InvoiceSubCategory invoiceSubCategory) {
        return openRTbySubCat(walletInstance, invoiceSubCategory, null, null);
    }

    /**
     * @param walletInstance wallet instance
     * @param invoiceSubCategory invoice sub category
     * @param from checking date
     * @param to checking date
     * @return list of rated transaction
     */
    @SuppressWarnings("unchecked")
    public List<RatedTransaction> openRTbySubCat(WalletInstance walletInstance, InvoiceSubCategory invoiceSubCategory, Date from, Date to) {
        QueryBuilder qb = new QueryBuilder(RatedTransaction.class, "rt");
        if (invoiceSubCategory != null) {
            qb.addCriterionEntity("rt.invoiceSubCategory", invoiceSubCategory);
        }
        qb.addCriterionEntity("rt.wallet", walletInstance);
        qb.addSql("rt.invoice is null");
        qb.addCriterionEnum("rt.status", RatedTransactionStatusEnum.OPEN);
        if (from != null) {
            qb.addCriterion("usageDate", ">=", from, false);
        }
        if (to != null) {
            qb.addCriterion("usageDate", "<=", to, false);
        }

        try {
            return (List<RatedTransaction>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Long countNotInvoicedRTByBA(BillingAccount billingAccount) {
        try {
            return (Long) getEntityManager().createNamedQuery("RatedTransaction.countNotInvoicedByBA").setParameter("billingAccount", billingAccount).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countNotInvoiced RT by BA", e);
            return null;
        }
    }

    public Long countNotInvoicedRTByUA(UserAccount userAccount) {
        try {
            return (Long) getEntityManager().createNamedQuery("RatedTransaction.countNotInvoicedByUA").setParameter("userAccount", userAccount).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countNotInvoiced RT by UA", e);
            return null;
        }
    }

    public Long countNotInvoicedRTByCA(CustomerAccount customerAccount) {
        try {
            return (Long) getEntityManager().createNamedQuery("RatedTransaction.countNotInvoicedByCA").setParameter("customerAccount", customerAccount).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countNotInvoiced RT by CA", e);
            return null;
        }
    }

    /**
     * Find the rated transaction by wallet operation id.
     *
     * @param walletOperationId the wallet operation id
     * @return the rated transaction
     */
    public RatedTransaction findByWalletOperationId(Long walletOperationId) {
        try {
            QueryBuilder qb = new QueryBuilder(getEntityClass(), "rt");
            qb.addCriterion("rt.walletOperationId", "=", walletOperationId, false);
            return (RatedTransaction) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            log.warn("No Rated transaction foud for this walletOperationId {} ", walletOperationId);
            return null;
        }
    }

    /**
     * Call RatedTransaction.setStatusToCanceledByRsCodes Named query to cancel just opened RatedTransaction of all passed RatedTransaction ids.
     * 
     * @param rsToCancelIds rated transactions to cancel
     */
    public void cancelRatedTransactions(List<Long> rsToCancelIds) {
        if (rsToCancelIds.size() > 0 && !rsToCancelIds.isEmpty()) {
            getEntityManager().createNamedQuery("RatedTransaction.setStatusToCanceledByRsCodes").setParameter("rsToCancelCodes", rsToCancelIds).executeUpdate();
        }
    }

}