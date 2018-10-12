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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.IBillableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionMinAmountTypeEnum;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.api.dto.ConsumptionDTO;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.order.OrderService;
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

    @Inject
    private TaxService taxService;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private OrderService orderService;

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
     * Append invoice aggregates to an invoice. Retrieves all to-invoice Rated transactions for a given billing account
     * 
     * @param billingAccount Billing Account
     * @param invoice Invoice to append invoice aggregates to
     * @param firstTransactionDate First transaction date
     * @param lastTransactionDate Last transaction date
     * @throws BusinessException business exception
     */
    public void appendInvoiceAgregates(BillingAccount billingAccount, Invoice invoice, Date firstTransactionDate, Date lastTransactionDate) throws BusinessException {

        if (firstTransactionDate == null) {
            firstTransactionDate = new Date(0);
        }

        if (lastTransactionDate == null) {
            lastTransactionDate = new Date();
        }

        List<RatedTransaction> ratedTransactions = (List<RatedTransaction>) getEntityManager()
            .createNamedQuery("RatedTransaction.listToInvoiceByBillingAccount", RatedTransaction.class).setParameter("billingAccount", billingAccount)
            .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).getResultList();

        appendInvoiceAgregates(billingAccount, invoice, ratedTransactions, false, false);
    }

    /**
     * Creates Invoice aggregates from given Rated transactions and appends them to an invoice
     * 
     * @param billingAccount Billing Account
     * @param invoice Invoice to append invoice aggregates to
     * @param ratedTransactions A list of rated transactions
     * @param isInvoiceAdjustment Is this invoice adjustment
     * @param isVirtual Is this a virtual invoice - invoice is not persisted, rated transactions are not persisted either
     * @throws BusinessException BusinessException
     */
    public void appendInvoiceAgregates(BillingAccount billingAccount, Invoice invoice, List<RatedTransaction> ratedTransactions, boolean isInvoiceAdjustment, boolean isVirtual)
            throws BusinessException {

        boolean isEnterprise = appProvider.isEntreprise();
        String languageCode = billingAccount.getTradingLanguage().getLanguageCode();
        boolean isExonerated = billingAccountService.isExonerated(billingAccount);
        int rtRounding = appProvider.getRounding();
        int invoiceRounding = appProvider.getInvoiceRounding();
        RoundingModeEnum rtRoundingMode = appProvider.getRoundingMode();
        RoundingModeEnum invoiceRoundingMode = appProvider.getInvoiceRoundingMode();
        Seller seller = invoice.getSeller();
        Tax taxZero = taxService.getZeroTax();

        // InvoiceType.taxScript will calculate all tax aggregates at once.
        boolean calculateTaxOnSubCategoryLevel = !isExonerated && invoice.getInvoiceType().getTaxScript() == null;

        // Should tax calculation on subcategory level be done externally
        boolean calculateExternalTax = "YES".equalsIgnoreCase((String) appProvider.getCfValue("OPENCELL_ENABLE_TAX_CALCULATION"));

        Map<String, SubCategoryInvoiceAgregate> subCategoryAggregates = new HashMap<>();
        Map<String, CategoryInvoiceAgregate> categoryAggregates = new HashMap<>();
        List<SubCategoryInvoiceAgregate> discountAggregates = new ArrayList<>();
        Map<String, TaxInvoiceAgregate> taxAggregates = new HashMap<>();

        Map<InvoiceSubCategory, BigDecimal> minAmountsWithTax = new HashMap<InvoiceSubCategory, BigDecimal>();
        String scaKey = null;
        for (RatedTransaction ratedTransaction : ratedTransactions) {

            InvoiceSubCategory invoiceSubCategory = ratedTransaction.getInvoiceSubCategory();

            scaKey = (ratedTransaction.getUserAccount() != null ? ratedTransaction.getUserAccount().getId() : "") + "_"
                    + (ratedTransaction.getWallet() != null ? ratedTransaction.getWallet().getId() : "") + "_" + invoiceSubCategory.getId() + "_"
                    + (ratedTransaction.getTax() != null ? ratedTransaction.getTax().getId() : "");

            SubCategoryInvoiceAgregate scAggregate = subCategoryAggregates.get(scaKey);
            if (scAggregate == null) {
                scAggregate = new SubCategoryInvoiceAgregate(invoiceSubCategory, billingAccount, ratedTransaction.getUserAccount(), ratedTransaction.getWallet(),
                    ratedTransaction.getTax(), ratedTransaction.getTaxPercent(), invoice, invoiceSubCategory.getAccountingCode());
                scAggregate.updateAudit(currentUser);

                String translationSCKey = "SC_" + invoiceSubCategory.getId() + "_" + languageCode;
                String descTranslated = descriptionMap.get(translationSCKey);
                if (descTranslated == null) {
                    descTranslated = invoiceSubCategory.getDescriptionOrCode();
                    if (invoiceSubCategory.getDescriptionI18n() != null && invoiceSubCategory.getDescriptionI18n().get(languageCode) != null) {
                        descTranslated = invoiceSubCategory.getDescriptionI18n().get(languageCode);
                    }
                    descriptionMap.put(translationSCKey, descTranslated);
                }
                scAggregate.setDescription(descTranslated);

                subCategoryAggregates.put(scaKey, scAggregate);
                invoice.addInvoiceAggregate(scAggregate);
            }

            if (isEnterprise) {
                scAggregate.addAmountWithoutTax(ratedTransaction.getAmountWithoutTax());
            } else {
                scAggregate.addAmountWithTax(ratedTransaction.getAmountWithTax());
            }
            scAggregate.addRatedTransaction(ratedTransaction);
            ratedTransaction.setInvoice(invoice);
            ratedTransaction.setStatus(RatedTransactionStatusEnum.BILLED);
        }

        // Determine which discount plan items apply to this invoice
        List<DiscountPlanItem> applicableDiscountPlanItems = new ArrayList<>();
        DiscountPlan discountPlan = billingAccount.getDiscountPlan();
        if (discountPlan != null && discountPlan.isActive()) {
            CustomerAccount customerAccount = billingAccount.getCustomerAccount();
            List<DiscountPlanItem> discountPlanItems = discountPlan.getDiscountPlanItems();
            for (DiscountPlanItem discountPlanItem : discountPlanItems) {
                if (discountPlanItem.isActive() && matchDiscountPlanItemExpression(discountPlanItem.getExpressionEl(), customerAccount, billingAccount, invoice)) {
                    applicableDiscountPlanItems.add(discountPlanItem);
                }
            }
        }

        log.error("AKK subcat aggregates {}", subCategoryAggregates.size());
        // Calculate derived aggregate amounts for subcategory aggregate, create category aggregates, discount aggregates and tax aggregates
        BigDecimal[] amounts = null;
        for (SubCategoryInvoiceAgregate scAggregate : subCategoryAggregates.values()) {

            InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.retrieveIfNotManaged(scAggregate.getInvoiceSubCategory());

            // If tax calculation is done on subcategory level, evaluate tax again in case it was changed
            if (calculateTaxOnSubCategoryLevel) {
                Tax tax = null;

                // If there is a taxScript in invoiceSubCategory and script is applicable, use it to compute external taxes
                if (calculateExternalTax && invoiceSubCategory.getTaxScript() != null) {
                    if (taxScriptService.isApplicable(invoiceSubCategory.getTaxScript().getCode(), scAggregate.getUserAccount(), invoice, invoiceSubCategory)) {
                        List<Tax> taxes = taxScriptService.computeTaxes(invoiceSubCategory.getTaxScript().getCode(), scAggregate.getUserAccount(), invoice, invoiceSubCategory);
                        if (!taxes.isEmpty()) {
                            tax = taxes.get(0);
                        }
                    }
                }

                if (tax == null) {
                    tax = invoiceSubCategoryCountryService.determineTax(invoiceSubCategory, seller, billingAccount, invoice.getInvoiceDate(), false);
                }

                // If tax has changed, need to update RTs with new tax value
                if (scAggregate.getTaxPercent() == null || scAggregate.getTaxPercent().compareTo(tax.getPercent()) != 0) {
                    log.trace("Will update {} rated transactions in subcategory {} with new tax from {} to {}", scAggregate.getItemNumber(),
                        scAggregate.getInvoiceSubCategory().getCode(), scAggregate.getTaxPercent(), tax.getPercent());
                    for (RatedTransaction ratedTransaction : scAggregate.getRatedtransactions()) {
                        ratedTransaction.setTax(tax);
                        ratedTransaction.setTaxPercent(tax.getPercent());
                        ratedTransaction.computeDerivedAmounts(isEnterprise, rtRounding, rtRoundingMode);
                    }
                }
                scAggregate.setTax(tax);
                scAggregate.setTaxPercent(tax.getPercent());
            } else if (isExonerated) {
                scAggregate.setTax(taxZero);
                scAggregate.setTaxPercent(BigDecimal.ZERO);
            }

            amounts = NumberUtils.computeDerivedAmounts(scAggregate.getAmountWithoutTax(), scAggregate.getAmountWithTax(), scAggregate.getTaxPercent(), isEnterprise,
                invoiceRounding, invoiceRoundingMode.getRoundingMode());
            scAggregate.setAmountWithoutTax(amounts[0]);
            scAggregate.setAmountWithTax(amounts[1]);
            scAggregate.setAmountTax(amounts[2]);

            BigDecimal amount = isEnterprise ? scAggregate.getAmountWithoutTax() : scAggregate.getAmountWithTax();
            BigDecimal amountCumulativeForTax = amount;

            // Create category aggregates or update their amounts

            String caKey = (scAggregate.getUserAccount() != null ? scAggregate.getUserAccount().getId() : "") + "_" + invoiceSubCategory.getInvoiceCategory().getId();

            CategoryInvoiceAgregate cAggregate = categoryAggregates.get(caKey);
            if (cAggregate == null) {
                cAggregate = new CategoryInvoiceAgregate(invoiceSubCategory.getInvoiceCategory(), billingAccount, scAggregate.getUserAccount(), invoice);
                cAggregate.updateAudit(currentUser);

                String translationCKey = "C_" + invoiceSubCategory.getInvoiceCategory().getId() + "_" + languageCode;
                String descTranslated = descriptionMap.get(translationCKey);
                if (descTranslated == null) {
                    descTranslated = invoiceSubCategory.getInvoiceCategory().getDescriptionOrCode();
                    if (invoiceSubCategory.getInvoiceCategory().getDescriptionI18n() != null
                            && invoiceSubCategory.getInvoiceCategory().getDescriptionI18n().get(languageCode) != null) {
                        descTranslated = invoiceSubCategory.getInvoiceCategory().getDescriptionI18n().get(languageCode);
                    }
                    descriptionMap.put(translationCKey, descTranslated);
                }

                cAggregate.setDescription(descTranslated);

                categoryAggregates.put(caKey, cAggregate);

                invoice.addInvoiceAggregate(cAggregate);
            }

            cAggregate.addSubCategoryInvoiceAggregate(scAggregate);

            if (amount != null && !BigDecimal.ZERO.equals(amount)) {

                // Add discount aggregates
                for (DiscountPlanItem discountPlanItem : applicableDiscountPlanItems) {

                    // Apply discount if matches the category, subcategory, or applies to any category
                    if ((discountPlanItem.getInvoiceCategory() == null && discountPlanItem.getInvoiceSubCategory() == null)
                            || (discountPlanItem.getInvoiceSubCategory() != null
                                    && discountPlanItem.getInvoiceSubCategory().getId().equals(scAggregate.getInvoiceSubCategory().getId()))
                            || (discountPlanItem.getInvoiceCategory() != null && discountPlanItem.getInvoiceSubCategory() == null
                                    && discountPlanItem.getInvoiceCategory().getId().equals(scAggregate.getInvoiceSubCategory().getInvoiceCategory().getId()))) {
                        BigDecimal discountPercent = discountPlanItem.getPercent();

                        if (discountPlanItem.getDiscountPercentEl() != null) {
                            discountPercent = evaluateDiscountPercentExpression(discountPlanItem.getDiscountPercentEl(), scAggregate.getUserAccount(), scAggregate.getWallet(),
                                invoice, amount);
                            log.debug("for discountPlan " + discountPlanItem.getCode() + " percentEL ->" + discountPercent + " on amount=" + amount);
                        }

                        BigDecimal discountAmount = amount.multiply(discountPercent.divide(HUNDRED)).negate().setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode());

                        log.error("AKK discountAmount {}, {}", discountAmount, discountAmount.compareTo(BigDecimal.ZERO) < 0);

                        if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
                            SubCategoryInvoiceAgregate discountAggregate = new SubCategoryInvoiceAgregate(scAggregate.getInvoiceSubCategory(), billingAccount,
                                scAggregate.getUserAccount(), scAggregate.getWallet(), scAggregate.getTax(), scAggregate.getTaxPercent(), invoice, null);

                            discountAggregate.updateAudit(currentUser);
                            discountAggregate.setItemNumber(scAggregate.getItemNumber());
                            discountAggregate.setCategoryInvoiceAgregate(cAggregate);

                            discountAggregate.setDiscountAggregate(true);
                            discountAggregate.setDiscountPercent(discountPercent);
                            discountAggregate.setDiscountPlanItem(discountPlanItem);
                            discountAggregate.setDescription(discountPlanItem.getCode());

                            amounts = NumberUtils.computeDerivedAmounts(discountAmount, discountAmount, scAggregate.getTaxPercent(), isEnterprise, invoiceRounding,
                                invoiceRoundingMode.getRoundingMode());

                            discountAggregate.setAmountWithoutTax(amounts[0]);
                            discountAggregate.setAmountWithTax(amounts[1]);
                            discountAggregate.setAmountTax(amounts[2]);

                            invoice.addInvoiceAggregate(discountAggregate);
                            discountAggregates.add(discountAggregate);

                            amountCumulativeForTax = amountCumulativeForTax.add(amounts[isEnterprise ? 0 : 1]);
                        } else {
                            log.error("AKK discount amount is not {} <0 ", discountAmount);
                        }
                    }
                }

                // Add tax aggregate or update its amounts

                if (calculateTaxOnSubCategoryLevel && amountCumulativeForTax.compareTo(BigDecimal.ZERO) > 0) {

                    TaxInvoiceAgregate taxAggregate = taxAggregates.get(scAggregate.getTax().getCode());
                    if (taxAggregate == null) {
                        taxAggregate = new TaxInvoiceAgregate(billingAccount, scAggregate.getTax(), scAggregate.getTaxPercent(), invoice);
                        taxAggregate.updateAudit(currentUser);
                        taxAggregates.put(scAggregate.getTax().getCode(), taxAggregate);

                        String translationCKey = "T_" + scAggregate.getTax().getId() + "_" + languageCode;
                        String descTranslated = descriptionMap.get(translationCKey);
                        if (descTranslated == null) {
                            descTranslated = scAggregate.getTax().getDescriptionOrCode();
                            if (scAggregate.getTax().getDescriptionI18n() != null && scAggregate.getTax().getDescriptionI18n().get(languageCode) != null) {
                                descTranslated = scAggregate.getTax().getDescriptionI18n().get(languageCode);
                            }
                            descriptionMap.put(translationCKey, descTranslated);
                        }

                        taxAggregate.setDescription(descTranslated);

                        invoice.addInvoiceAggregate(taxAggregate);
                    }

                    if (isEnterprise) {
                        taxAggregate.addAmountWithoutTax(amountCumulativeForTax);
                    } else {
                        taxAggregate.addAmountWithTax(amountCumulativeForTax);
                    }
                }
            }
        }

        // Calculate derived tax aggregate amounts
        if (calculateTaxOnSubCategoryLevel) {
            for (TaxInvoiceAgregate taxAggregate : taxAggregates.values()) {

                amounts = NumberUtils.computeDerivedAmounts(taxAggregate.getAmountWithoutTax(), taxAggregate.getAmountWithTax(), taxAggregate.getTaxPercent(), isEnterprise,
                    invoiceRounding, invoiceRoundingMode.getRoundingMode());
                taxAggregate.setAmountWithoutTax(amounts[0]);
                taxAggregate.setAmountWithTax(amounts[1]);
                taxAggregate.setAmountTax(amounts[2]);

            }
        }

        // If tax calculation is not done at subcategory level, then call a global script to do calculation for the whole invoice
        if (!isExonerated && !calculateTaxOnSubCategoryLevel) {
            if (invoice.getInvoiceType() != null && invoice.getInvoiceType().getTaxScript() != null) {
                taxAggregates = taxScriptService.createTaxAggregates(invoice.getInvoiceType().getTaxScript().getCode(), invoice);
                if (taxAggregates != null) {
                    for (TaxInvoiceAgregate taxAggregate : taxAggregates.values()) {
                        taxAggregate.setInvoice(invoice);
                        invoice.addInvoiceAggregate(taxAggregate);
                    }
                }
            }
        }

        // Calculate invoice total amounts by the sum of tax aggregates or category aggregates minus discount aggregates
        // Left here in case tax script modifies something
        if (!isExonerated && taxAggregates != null) {
            for (TaxInvoiceAgregate taxAggregate : taxAggregates.values()) {
                invoice.addAmountWithoutTax(taxAggregate.getAmountWithoutTax());
                invoice.addAmountWithTax(taxAggregate.getAmountWithTax());
                invoice.addAmountTax(taxAggregate.getAmountTax());
            }
        } else {
            invoice.setAmountTax(BigDecimal.ZERO);

            for (CategoryInvoiceAgregate cAggregate : categoryAggregates.values()) {
                invoice.addAmountWithoutTax(cAggregate.getAmountWithoutTax());
                invoice.addAmountWithTax(cAggregate.getAmountWithTax());
            }

            for (SubCategoryInvoiceAgregate discountAggregate : discountAggregates) {
                invoice.addAmountWithoutTax(discountAggregate.getAmountWithoutTax());
                invoice.addAmountWithTax(discountAggregate.getAmountWithTax());
            }
        }

        BigDecimal invoicingThreshold = billingAccount.getInvoicingThreshold() == null ? billingAccount.getBillingCycle().getInvoicingThreshold()
                : billingAccount.getInvoicingThreshold();
        if (invoicingThreshold != null && invoicingThreshold.compareTo(isEnterprise ? invoice.getAmountWithoutTax() : invoice.getAmountWithTax()) > 0) {
            throw new BusinessException("Invoice amount below the threshold");
        }
    }

    /**
     * Check if Billing account has any not yet billed Rated transactions
     * 
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
     * Convert Wallet operations to Rated transactions for a given billable entity up to a given date
     * 
     * @param entity entity to bill
     * @param invoicingDate invoicing date
     * @throws BusinessException business exception.
     */
    public void createRatedTransaction(IBillableEntity entity, Date invoicingDate) throws BusinessException {
        List<WalletOperation> walletOps = new ArrayList<WalletOperation>();
        if (entity instanceof BillingAccount) {
            BillingAccount billingAccount = billingAccountService.findById(((BillingAccount) entity).getId());
            List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
            for (UserAccount ua : userAccounts) {
                walletOps.addAll(walletOperationService.listToInvoiceByUserAccount(invoicingDate, ua));
            }
        } else if (entity instanceof Subscription) {
            walletOps.addAll(walletOperationService.listToInvoiceBySubscription(invoicingDate, (Subscription) entity));
        } else if (entity instanceof Order) {
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

    /**
     * Update billing account total amounts.
     *
     * @param entity entity
     * @param billingRun the billing run
     * @return Updated entity
     * @throws BusinessException the business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IBillableEntity updateEntityTotalAmounts(IBillableEntity entity, BillingRun billingRun) throws BusinessException {
        log.debug("updateEntityTotalAmounts  entity:" + entity.getId());

        if (entity instanceof BillingAccount) {
            entity = billingAccountService.findById((Long) entity.getId());
        }

        if (entity instanceof Subscription) {
            entity = subscriptionService.findById((Long) entity.getId());
        }

        if (entity instanceof Order) {
            entity = orderService.findById((Long) entity.getId());
        }

        calculateAmountsAndCreateMinAmountTransactions(entity, null, billingRun.getLastTransactionDate());

        BigDecimal invoiceAmount = entity.getTotalInvoicingAmountWithoutTax();
        if (invoiceAmount != null) {
            BillingCycle billingCycle = billingRun.getBillingCycle();
            BigDecimal invoicingThreshold = billingCycle == null ? null : billingCycle.getInvoicingThreshold();

            if (invoicingThreshold != null) {
                if (invoicingThreshold.compareTo(invoiceAmount) > 0) {
                    log.debug("updateEntityTotalAmounts  invoicingThreshold( stop invoicing)  baCode:{}, amountWithoutTax:{} ,invoicingThreshold:{}", entity.getCode(),
                        invoiceAmount, invoicingThreshold);
                    return null;
                } else {
                    log.debug("updateEntityTotalAmounts  invoicingThreshold(out continue invoicing)  baCode:{}, amountWithoutTax:{} ,invoicingThreshold:{}", entity.getCode(),
                        invoiceAmount, invoicingThreshold);
                }
            } else {
                log.debug("updateBillingAccountTotalAmounts no invoicingThreshold to apply");
            }

            log.debug("{}/{} will be updated with BR amount {}", entity.getClass().getSimpleName(), entity.getId(), invoiceAmount);
        }

        entity.setBillingRun(getEntityManager().getReference(BillingRun.class, billingRun.getId()));

        if (entity instanceof BillingAccount) {
            ((BillingAccount) entity).setBrAmountWithoutTax(invoiceAmount);
            billingAccountService.updateNoCheck((BillingAccount) entity);
        }
        if (entity instanceof Order) {
            orderService.updateNoCheck((Order) entity);
        }
        if (entity instanceof Subscription) {
            subscriptionService.updateNoCheck((Subscription) entity);
        }

        return entity;
    }

    /**
     * Create min amounts rated transactions and set invoiceable amounts to the billable entity
     *
     * @param billableEntity The billable entity
     * @param lastTransactionDate Last transaction date
     * @throws BusinessException General business exception
     */
    public void calculateAmountsAndCreateMinAmountTransactions(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate) throws BusinessException {

        List<RatedTransaction> minAmountTransactions = new ArrayList<RatedTransaction>();

        Date minRatingDate = DateUtils.addDaysToDate(lastTransactionDate, -1);

        BillingAccount billingAccount = null;
        List<Subscription> subscriptionsToProcess = new ArrayList<Subscription>();
        if (billableEntity instanceof Subscription) {
            subscriptionsToProcess.add((Subscription) billableEntity);
            billingAccount = ((Subscription) billableEntity).getUserAccount().getBillingAccount();
        }

        if (billableEntity instanceof BillingAccount) {
            billingAccount = (BillingAccount) billableEntity;
            for (UserAccount userAccount : ((BillingAccount) billableEntity).getUsersAccounts()) {
                if (userAccount.getStatus().equals(AccountStatusEnum.ACTIVE)) {
                    for (Subscription subscription : userAccount.getSubscriptions()) {
                        if (subscription.getStatus().equals(SubscriptionStatusEnum.ACTIVE)) {
                            subscriptionsToProcess.add(subscription);
                        }
                    }
                }
            }
        }

        Map<InvoiceSubCategory, Map<String, BigDecimal>> billingAccountAmountMap = new HashMap<InvoiceSubCategory, Map<String, BigDecimal>>();

        for (Subscription subscription : subscriptionsToProcess) {

            UserAccount userAccount = subscription.getUserAccount();

            Map<InvoiceSubCategory, Map<String, BigDecimal>> subscriptionAmountMap = new HashMap<InvoiceSubCategory, Map<String, BigDecimal>>();

            for (ServiceInstance serviceInstance : subscription.getServiceInstances()) {
                if (serviceInstance.getStatus().equals(InstanceStatusEnum.ACTIVE)) {

                    Map<InvoiceSubCategory, Map<String, BigDecimal>> serviceAmountMap = new HashMap<InvoiceSubCategory, Map<String, BigDecimal>>();

                    List<RecurringChargeInstance> recurringChargeInstanceList = serviceInstance.getRecurringChargeInstances();
                    for (RecurringChargeInstance recurringChargeInstance : recurringChargeInstanceList) {
                        List<Object[]> amountsList = computeChargeInvoiceAmount(recurringChargeInstance, new Date(0), lastTransactionDate, billingAccount);

                        for (Object[] amounts : amountsList) {
                            BigDecimal chargeAmountWithoutTax = (BigDecimal) amounts[0];
                            BigDecimal chargeAmountWithTax = (BigDecimal) amounts[1];
                            InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findById((Long) amounts[3]);

                            if (chargeAmountWithoutTax != null) {
                                if (serviceAmountMap.get(invoiceSubCategory) != null) {
                                    Map<String, BigDecimal> serviceAmount = serviceAmountMap.get(invoiceSubCategory);
                                    serviceAmount.put("serviceAmountWithoutTax", serviceAmount.get("serviceAmountWithoutTax").add(chargeAmountWithoutTax));
                                    serviceAmount.put("serviceAmountWithTax", serviceAmount.get("serviceAmountWithTax").add(chargeAmountWithTax));
                                    serviceAmountMap.put(invoiceSubCategory, serviceAmount);
                                } else {
                                    Map<String, BigDecimal> serviceAmount = new HashMap<String, BigDecimal>();
                                    serviceAmount.put("serviceAmountWithoutTax", chargeAmountWithoutTax);
                                    serviceAmount.put("serviceAmountWithTax", chargeAmountWithTax);
                                    serviceAmountMap.put(invoiceSubCategory, serviceAmount);
                                }
                            }
                        }
                    }

                    List<UsageChargeInstance> usageChargeInstanceList = serviceInstance.getUsageChargeInstances();
                    for (UsageChargeInstance usageChargeInstance : usageChargeInstanceList) {
                        List<Object[]> amountsList = computeChargeInvoiceAmount(usageChargeInstance, new Date(0), lastTransactionDate, billingAccount);

                        for (Object[] amounts : amountsList) {
                            BigDecimal chargeAmountWithoutTax = (BigDecimal) amounts[0];
                            BigDecimal chargeAmountWithTax = (BigDecimal) amounts[1];
                            InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findById((Long) amounts[3]);

                            if (chargeAmountWithoutTax != null) {
                                if (serviceAmountMap.get(invoiceSubCategory) != null) {
                                    Map<String, BigDecimal> serviceAmount = serviceAmountMap.get(invoiceSubCategory);
                                    serviceAmount.put("serviceAmountWithoutTax", serviceAmount.get("serviceAmountWithoutTax").add(chargeAmountWithoutTax));
                                    serviceAmount.put("serviceAmountWithTax", serviceAmount.get("serviceAmountWithTax").add(chargeAmountWithTax));
                                    serviceAmountMap.put(invoiceSubCategory, serviceAmount);
                                } else {
                                    Map<String, BigDecimal> serviceAmount = new HashMap<String, BigDecimal>();
                                    serviceAmount.put("serviceAmountWithoutTax", chargeAmountWithoutTax);
                                    serviceAmount.put("serviceAmountWithTax", chargeAmountWithTax);
                                    serviceAmountMap.put(invoiceSubCategory, serviceAmount);
                                }
                            }
                        }
                    }

                    List<OneShotChargeInstance> subscriptionChargeInstanceList = serviceInstance.getSubscriptionChargeInstances();
                    for (OneShotChargeInstance subscriptionChargeInstance : subscriptionChargeInstanceList) {
                        List<Object[]> amountsList = computeChargeInvoiceAmount(subscriptionChargeInstance, new Date(0), lastTransactionDate, billingAccount);

                        for (Object[] amounts : amountsList) {
                            BigDecimal chargeAmountWithoutTax = (BigDecimal) amounts[0];
                            BigDecimal chargeAmountWithTax = (BigDecimal) amounts[1];
                            InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findById((Long) amounts[3]);

                            if (chargeAmountWithoutTax != null) {
                                if (serviceAmountMap.get(invoiceSubCategory) != null) {
                                    Map<String, BigDecimal> serviceAmount = serviceAmountMap.get(invoiceSubCategory);
                                    serviceAmount.put("serviceAmountWithoutTax", serviceAmount.get("serviceAmountWithoutTax").add(chargeAmountWithoutTax));
                                    serviceAmount.put("serviceAmountWithTax", serviceAmount.get("serviceAmountWithTax").add(chargeAmountWithTax));
                                    serviceAmountMap.put(invoiceSubCategory, serviceAmount);
                                } else {
                                    Map<String, BigDecimal> serviceAmount = new HashMap<String, BigDecimal>();
                                    serviceAmount.put("serviceAmountWithoutTax", chargeAmountWithoutTax);
                                    serviceAmount.put("serviceAmountWithTax", chargeAmountWithTax);
                                    serviceAmountMap.put(invoiceSubCategory, serviceAmount);
                                }
                            }
                        }
                    }

                    List<OneShotChargeInstance> terminationChargeInstanceList = serviceInstance.getTerminationChargeInstances();
                    for (OneShotChargeInstance terminationChargeInstance : terminationChargeInstanceList) {
                        List<Object[]> amountsList = computeChargeInvoiceAmount(terminationChargeInstance, new Date(0), lastTransactionDate, billingAccount);

                        for (Object[] amounts : amountsList) {
                            BigDecimal chargeAmountWithoutTax = (BigDecimal) amounts[0];
                            BigDecimal chargeAmountWithTax = (BigDecimal) amounts[1];
                            InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findById((Long) amounts[3]);

                            if (chargeAmountWithoutTax != null) {
                                if (serviceAmountMap.get(invoiceSubCategory) != null) {
                                    Map<String, BigDecimal> serviceAmount = serviceAmountMap.get(invoiceSubCategory);
                                    serviceAmount.put("serviceAmountWithoutTax", serviceAmount.get("serviceAmountWithoutTax").add(chargeAmountWithoutTax));
                                    serviceAmount.put("serviceAmountWithTax", serviceAmount.get("serviceAmountWithTax").add(chargeAmountWithTax));
                                    serviceAmountMap.put(invoiceSubCategory, serviceAmount);
                                } else {
                                    Map<String, BigDecimal> serviceAmount = new HashMap<String, BigDecimal>();
                                    serviceAmount.put("serviceAmountWithoutTax", chargeAmountWithoutTax);
                                    serviceAmount.put("serviceAmountWithTax", chargeAmountWithTax);
                                    serviceAmountMap.put(invoiceSubCategory, serviceAmount);
                                }
                            }
                        }
                    }

                    BigDecimal totalServiceAmountWithoutTax = BigDecimal.ZERO;
                    BigDecimal totalServiceAmountWithTax = BigDecimal.ZERO;

                    for (Map.Entry<InvoiceSubCategory, Map<String, BigDecimal>> entry : serviceAmountMap.entrySet()) {
                        totalServiceAmountWithoutTax = totalServiceAmountWithoutTax.add(entry.getValue().get("serviceAmountWithoutTax"));
                        totalServiceAmountWithTax = totalServiceAmountWithTax.add(entry.getValue().get("serviceAmountWithTax"));
                    }

                    for (Map.Entry<InvoiceSubCategory, Map<String, BigDecimal>> entry : serviceAmountMap.entrySet()) {

                        BigDecimal serviceAmountWithoutTax = entry.getValue().get("serviceAmountWithoutTax");
                        BigDecimal serviceAmountWithTax = entry.getValue().get("serviceAmountWithTax");
                        InvoiceSubCategory invoiceSubCategory = entry.getKey();

                        String serviceMinAmountEL = StringUtils.isBlank(serviceInstance.getMinimumAmountEl()) ? serviceInstance.getServiceTemplate().getMinimumAmountEl()
                                : serviceInstance.getMinimumAmountEl();
                        String serviceMinLabelEL = StringUtils.isBlank(serviceInstance.getMinimumLabelEl()) ? serviceInstance.getServiceTemplate().getMinimumLabelEl()
                                : serviceInstance.getMinimumLabelEl();
                        if (!StringUtils.isBlank(serviceMinAmountEL)) {

                            BigDecimal serviceMinAmount = evaluateMinAmountExpression(serviceMinAmountEL, null, null, serviceInstance);
                            String serviceMinLabel = evaluateMinAmountLabelExpression(serviceMinLabelEL, null, null, serviceInstance);

                            BigDecimal ratio = BigDecimal.ZERO;
                            BigDecimal diff = null;
                            if (appProvider.isEntreprise()) {
                                diff = serviceMinAmount.subtract(totalServiceAmountWithoutTax);
                                if (totalServiceAmountWithoutTax.compareTo(BigDecimal.ZERO) != 0) {
                                    ratio = serviceAmountWithoutTax.divide(totalServiceAmountWithoutTax, 2, RoundingMode.HALF_UP);
                                } else {
                                    ratio = BigDecimal.ONE;
                                }
                            } else {
                                diff = serviceMinAmount.subtract(totalServiceAmountWithTax);
                                if (totalServiceAmountWithTax.compareTo(BigDecimal.ZERO) != 0) {
                                    ratio = serviceAmountWithTax.divide(totalServiceAmountWithTax, 2, RoundingMode.HALF_UP);
                                } else {
                                    ratio = BigDecimal.ONE;
                                }
                            }

                            if (diff.doubleValue() > 0) {
                                BigDecimal taxPercent = BigDecimal.ZERO;
                                Tax tax = null;
                                BigDecimal rtMinAmount = diff.multiply(ratio);
                                for (InvoiceSubcategoryCountry invoiceSubcategoryCountry : invoiceSubCategory.getInvoiceSubcategoryCountries()) {
                                    if (invoiceSubcategoryCountry.getTradingCountry() == billingAccount.getTradingCountry()) {
                                        taxPercent = invoiceSubcategoryCountry.getTax().getPercent();
                                        tax = invoiceSubcategoryCountry.getTax();
                                        break;
                                    }
                                }
                                BigDecimal unitAmountWithoutTax = appProvider.isEntreprise() ? rtMinAmount
                                        : rtMinAmount.subtract(rtMinAmount.multiply(taxPercent).divide(new BigDecimal(100), appProvider.getRounding(),
                                            appProvider.getRoundingMode().getRoundingMode()));
                                BigDecimal unitAmountWithTax = appProvider.isEntreprise() ? rtMinAmount
                                    .add(rtMinAmount.multiply(taxPercent).divide(new BigDecimal(100), appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode()))
                                        : rtMinAmount;
                                BigDecimal unitAmountTax = unitAmountWithTax.subtract(unitAmountWithoutTax);
                                BigDecimal amountWithoutTax = unitAmountWithoutTax;
                                BigDecimal amountWithTax = unitAmountWithTax;
                                BigDecimal amountTax = unitAmountTax;

                                RatedTransaction ratedTransaction = new RatedTransaction(minRatingDate, unitAmountWithoutTax, unitAmountWithTax, unitAmountTax, BigDecimal.ONE,
                                    amountWithoutTax, amountWithTax, amountTax, RatedTransactionStatusEnum.OPEN, null, billingAccount, userAccount, invoiceSubCategory, "", "", "",
                                    "", null, null, "", "", null, "NO_OFFER", null, RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_SE.getCode() + "_" + serviceInstance.getCode(),
                                    serviceMinLabel, null, null, subscription.getSeller(), tax, taxPercent);

                                minAmountTransactions.add(ratedTransaction);

                                serviceAmountWithoutTax = serviceAmountWithoutTax.add(amountWithoutTax);
                                serviceAmountWithTax = serviceAmountWithTax.add(amountWithTax);

                            }
                        }

                        if (subscriptionAmountMap.get(invoiceSubCategory) != null) {
                            Map<String, BigDecimal> subscriptionAmount = subscriptionAmountMap.get(invoiceSubCategory);
                            subscriptionAmount.put("subscriptionAmountWithoutTax", subscriptionAmount.get("subscriptionAmountWithoutTax").add(serviceAmountWithoutTax));
                            subscriptionAmount.put("subscriptionAmountWithTax", subscriptionAmount.get("subscriptionAmountWithTax").add(serviceAmountWithTax));
                            subscriptionAmountMap.put(invoiceSubCategory, subscriptionAmount);
                        } else {
                            Map<String, BigDecimal> subscriptionAmount = new HashMap<String, BigDecimal>();
                            subscriptionAmount.put("subscriptionAmountWithoutTax", serviceAmountWithoutTax);
                            subscriptionAmount.put("subscriptionAmountWithTax", serviceAmountWithTax);
                            subscriptionAmountMap.put(invoiceSubCategory, subscriptionAmount);
                        }

                    }
                }
            }

            BigDecimal totalSubscriptionAmountWithoutTax = BigDecimal.ZERO;
            BigDecimal totalSubscriptionAmountWithTax = BigDecimal.ZERO;

            for (Map.Entry<InvoiceSubCategory, Map<String, BigDecimal>> entry : subscriptionAmountMap.entrySet()) {
                totalSubscriptionAmountWithoutTax = totalSubscriptionAmountWithoutTax.add(entry.getValue().get("subscriptionAmountWithoutTax"));
                totalSubscriptionAmountWithTax = totalSubscriptionAmountWithTax.add(entry.getValue().get("subscriptionAmountWithTax"));
            }

            for (Map.Entry<InvoiceSubCategory, Map<String, BigDecimal>> entry : subscriptionAmountMap.entrySet()) {

                BigDecimal subscriptionAmountWithoutTax = entry.getValue().get("subscriptionAmountWithoutTax");
                BigDecimal subscriptionAmountWithTax = entry.getValue().get("subscriptionAmountWithTax");
                InvoiceSubCategory invoiceSubCategory = entry.getKey();

                String subscriptionMinAmountEL = StringUtils.isBlank(subscription.getMinimumAmountEl()) ? subscription.getOffer().getMinimumAmountEl()
                        : subscription.getMinimumAmountEl();
                String subscriptionMinLabelEL = StringUtils.isBlank(subscription.getMinimumLabelEl()) ? subscription.getOffer().getMinimumLabelEl()
                        : subscription.getMinimumLabelEl();

                if (!StringUtils.isBlank(subscriptionMinAmountEL)) {
                    BigDecimal subscriptionMinAmount = evaluateMinAmountExpression(subscriptionMinAmountEL, null, subscription, null);
                    String subscriptionMinLabel = evaluateMinAmountLabelExpression(subscriptionMinLabelEL, null, subscription, null);

                    BigDecimal ratio = BigDecimal.ZERO;
                    BigDecimal diff = null;
                    if (appProvider.isEntreprise()) {
                        diff = subscriptionMinAmount.subtract(totalSubscriptionAmountWithoutTax);
                        if (totalSubscriptionAmountWithoutTax.compareTo(BigDecimal.ZERO) != 0) {
                            ratio = subscriptionAmountWithoutTax.divide(totalSubscriptionAmountWithoutTax, 2, RoundingMode.HALF_UP);
                        } else {
                            ratio = BigDecimal.ONE;
                        }
                    } else {
                        diff = subscriptionMinAmount.subtract(totalSubscriptionAmountWithTax);
                        if (totalSubscriptionAmountWithTax.compareTo(BigDecimal.ZERO) != 0) {
                            ratio = subscriptionAmountWithTax.divide(totalSubscriptionAmountWithTax, 2, RoundingMode.HALF_UP);
                        } else {
                            ratio = BigDecimal.ONE;
                        }
                    }

                    if (diff.doubleValue() > 0) {

                        BigDecimal taxPercent = BigDecimal.ZERO;
                        Tax tax = null;
                        BigDecimal rtMinAmount = diff.multiply(ratio);
                        for (InvoiceSubcategoryCountry invoiceSubcategoryCountry : invoiceSubCategory.getInvoiceSubcategoryCountries()) {
                            if (invoiceSubcategoryCountry.getTradingCountry() == billingAccount.getTradingCountry()) {
                                taxPercent = invoiceSubcategoryCountry.getTax().getPercent();
                                tax = invoiceSubcategoryCountry.getTax();
                                break;
                            }
                        }

                        BigDecimal unitAmountWithoutTax = appProvider.isEntreprise() ? rtMinAmount
                                : rtMinAmount.subtract(
                                    rtMinAmount.multiply(taxPercent).divide(new BigDecimal(100), appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode()));
                        BigDecimal unitAmountWithTax = appProvider.isEntreprise()
                                ? rtMinAmount
                                    .add(rtMinAmount.multiply(taxPercent).divide(new BigDecimal(100), appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode()))
                                : rtMinAmount;
                        BigDecimal unitAmountTax = unitAmountWithTax.subtract(unitAmountWithoutTax);
                        BigDecimal amountWithoutTax = unitAmountWithoutTax;
                        BigDecimal amountWithTax = unitAmountWithTax;
                        BigDecimal amountTax = unitAmountTax;

                        RatedTransaction ratedTransaction = new RatedTransaction(minRatingDate, unitAmountWithoutTax, unitAmountWithTax, unitAmountTax, BigDecimal.ONE,
                            amountWithoutTax, amountWithTax, amountTax, RatedTransactionStatusEnum.OPEN, null, billingAccount, userAccount, invoiceSubCategory, "", "", "", "",
                            null, subscription, "", "", null, "NO_OFFER", null, RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_SU.getCode() + "_" + subscription.getCode(),
                            subscriptionMinLabel, null, null, subscription.getSeller(), tax, taxPercent);

                        minAmountTransactions.add(ratedTransaction);

                        subscriptionAmountWithoutTax = subscriptionAmountWithoutTax.add(amountWithoutTax);
                        subscriptionAmountWithTax = subscriptionAmountWithTax.add(amountWithTax);

                    }
                }

                if (billingAccountAmountMap.get(invoiceSubCategory) != null) {
                    Map<String, BigDecimal> billingAccountAmount = billingAccountAmountMap.get(invoiceSubCategory);
                    billingAccountAmount.put("billingAccountAmountWithoutTax", billingAccountAmount.get("billingAccountAmountWithoutTax").add(subscriptionAmountWithoutTax));
                    billingAccountAmount.put("billingAccountAmountWithTax", billingAccountAmount.get("billingAccountAmountWithTax").add(subscriptionAmountWithTax));
                    billingAccountAmountMap.put(invoiceSubCategory, billingAccountAmount);
                } else {
                    Map<String, BigDecimal> billingAccountAmount = new HashMap<String, BigDecimal>();
                    billingAccountAmount.put("billingAccountAmountWithoutTax", subscriptionAmountWithoutTax);
                    billingAccountAmount.put("billingAccountAmountWithTax", subscriptionAmountWithTax);
                    billingAccountAmountMap.put(invoiceSubCategory, billingAccountAmount);
                }

            }
        }

        BigDecimal totalInvoiceAmountWithoutTax = BigDecimal.ZERO;
        BigDecimal totalInvoiceAmountWithTax = BigDecimal.ZERO;
        BigDecimal totalInvoiceAmountTax = BigDecimal.ZERO;
        BigDecimal totalBillingAccountAmountWithoutTax = BigDecimal.ZERO;
        BigDecimal totalBillingAccountAmountWithTax = BigDecimal.ZERO;

        for (Map.Entry<InvoiceSubCategory, Map<String, BigDecimal>> entry : billingAccountAmountMap.entrySet()) {
            totalBillingAccountAmountWithoutTax = totalBillingAccountAmountWithoutTax.add(entry.getValue().get("billingAccountAmountWithoutTax"));
            totalBillingAccountAmountWithTax = totalBillingAccountAmountWithTax.add(entry.getValue().get("billingAccountAmountWithTax"));
        }

        if (billableEntity instanceof Subscription) {
            totalInvoiceAmountWithoutTax = totalBillingAccountAmountWithoutTax;
            totalInvoiceAmountWithTax = totalBillingAccountAmountWithTax;
            totalInvoiceAmountTax = totalBillingAccountAmountWithTax.subtract(totalBillingAccountAmountWithoutTax);
        }

        if (billableEntity instanceof Order) {
            Object[] amounts = computeOrderInvoiceAmount((Order) billableEntity, new Date(0), lastTransactionDate);
            totalInvoiceAmountWithoutTax = (BigDecimal) amounts[0];
            totalInvoiceAmountWithTax = (BigDecimal) amounts[1];
            totalInvoiceAmountTax = (BigDecimal) amounts[2];
        }

        if (billableEntity instanceof BillingAccount) {

            Seller seller = billingAccount.getCustomerAccount().getCustomer().getSeller();

            for (Map.Entry<InvoiceSubCategory, Map<String, BigDecimal>> entry : billingAccountAmountMap.entrySet()) {

                BigDecimal billingAccountAmountWithoutTax = entry.getValue().get("billingAccountAmountWithoutTax");
                BigDecimal billingAccountAmountWithTax = entry.getValue().get("billingAccountAmountWithTax");
                InvoiceSubCategory invoiceSubCategory = entry.getKey();

                if (!StringUtils.isBlank(billingAccount.getMinimumAmountEl()) && billingAccountAmountWithoutTax != null
                        && billingAccountAmountWithoutTax.compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal billingAccountMinAmount = evaluateMinAmountExpression(billingAccount.getMinimumAmountEl(), billingAccount, null, null);
                    String billingAccountMinLabel = evaluateMinAmountLabelExpression(billingAccount.getMinimumLabelEl(), billingAccount, null, null);

                    BigDecimal ratio = BigDecimal.ZERO;
                    BigDecimal diff = null;
                    if (appProvider.isEntreprise()) {
                        diff = billingAccountMinAmount.subtract(totalBillingAccountAmountWithoutTax);
                        if (totalBillingAccountAmountWithoutTax.compareTo(BigDecimal.ZERO) != 0) {
                            ratio = billingAccountAmountWithoutTax.divide(totalBillingAccountAmountWithoutTax, 2, RoundingMode.HALF_UP);
                        } else {
                            ratio = BigDecimal.ONE;
                        }
                    } else {
                        diff = billingAccountMinAmount.subtract(totalBillingAccountAmountWithTax);
                        if (totalBillingAccountAmountWithTax.compareTo(BigDecimal.ZERO) != 0) {
                            ratio = billingAccountAmountWithTax.divide(totalBillingAccountAmountWithTax, 2, RoundingMode.HALF_UP);
                        } else {
                            ratio = BigDecimal.ONE;
                        }
                    }

                    if (diff.doubleValue() > 0) {

                        BigDecimal taxPercent = BigDecimal.ZERO;
                        Tax tax = null;
                        BigDecimal rtMinAmount = diff.multiply(ratio);
                        for (InvoiceSubcategoryCountry invoiceSubcategoryCountry : invoiceSubCategory.getInvoiceSubcategoryCountries()) {
                            if (invoiceSubcategoryCountry.getTradingCountry() == billingAccount.getTradingCountry()) {
                                taxPercent = invoiceSubcategoryCountry.getTax().getPercent();
                                tax = invoiceSubcategoryCountry.getTax();
                                break;
                            }
                        }

                        BigDecimal unitAmountWithoutTax = appProvider.isEntreprise() ? rtMinAmount
                                : rtMinAmount.subtract(
                                    rtMinAmount.multiply(taxPercent).divide(new BigDecimal(100), appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode()));
                        BigDecimal unitAmountWithTax = appProvider.isEntreprise()
                                ? rtMinAmount
                                    .add(rtMinAmount.multiply(taxPercent).divide(new BigDecimal(100), appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode()))
                                : rtMinAmount;
                        BigDecimal unitAmountTax = unitAmountWithTax.subtract(unitAmountWithoutTax);
                        BigDecimal amountWithoutTax = unitAmountWithoutTax;
                        BigDecimal amountWithTax = unitAmountWithTax;
                        BigDecimal amountTax = unitAmountTax;

                        RatedTransaction ratedTransaction = new RatedTransaction(minRatingDate, unitAmountWithoutTax, unitAmountWithTax, unitAmountTax, BigDecimal.ONE,
                            amountWithoutTax, amountWithTax, amountTax, RatedTransactionStatusEnum.OPEN, null, billingAccount, null, invoiceSubCategory, "", "", "", "", null, null,
                            "", "", null, "NO_OFFER", null, RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_BA.getCode() + "_" + billingAccount.getCode(), billingAccountMinLabel,
                            null, null, seller, tax, taxPercent);

                        minAmountTransactions.add(ratedTransaction);

                        billingAccountAmountWithoutTax = billingAccountAmountWithoutTax.add(amountWithoutTax);
                        billingAccountAmountWithTax = billingAccountAmountWithTax.add(amountWithTax);

                    }
                }
                totalInvoiceAmountWithoutTax = totalInvoiceAmountWithoutTax.add(billingAccountAmountWithoutTax);
                totalInvoiceAmountWithTax = totalInvoiceAmountWithTax.add(billingAccountAmountWithTax);
            }

            totalInvoiceAmountTax = totalInvoiceAmountWithTax.subtract(totalInvoiceAmountWithoutTax);
        }

        billableEntity.setMinRatedTransactions(minAmountTransactions);
        billableEntity.setTotalInvoicingAmountWithoutTax(totalInvoiceAmountWithoutTax);
        billableEntity.setTotalInvoicingAmountWithTax(totalInvoiceAmountWithTax);
        billableEntity.setTotalInvoicingAmountTax(totalInvoiceAmountTax);

    }

    /**
     * Compute the invoice amount by charge.
     * 
     * @param chargeInstance Charge instance
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @param billingAccount Billing account
     * @return Computed invoice amount by charge.
     */
    private List<Object[]> computeChargeInvoiceAmount(ChargeInstance chargeInstance, Date firstTransactionDate, Date lastTransactionDate, BillingAccount billingAccount) {
        Query q = getEntityManager().createNamedQuery("RatedTransaction.sumByCharge").setParameter("chargeInstance", chargeInstance)
            .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).setParameter("billingAccount", billingAccount);
        return (List<Object[]>) q.getResultList();
    }

    /**
     * Evaluate double expression. Either ba, subscription or service instance must be specified.
     *
     * @param expression EL expression
     * @param ba Billing account
     * @param subscription Subscription
     * @param serviceInstance serviceInstance
     * @return evaluated expression
     * @throws BusinessException business exception
     */
    private BigDecimal evaluateMinAmountExpression(String expression, BillingAccount ba, Subscription subscription, ServiceInstance serviceInstance) throws BusinessException {
        if (StringUtils.isBlank(expression)) {
            return null;
        }

        Map<Object, Object> userMap = constructElContext(expression, ba, subscription, serviceInstance);

        return ValueExpressionWrapper.evaluateExpression(expression, userMap, BigDecimal.class);
    }

    /**
     * Evaluate string expression.
     *
     * @param expression EL expression
     * @param ba billing account
     * @return evaluated expression
     * @throws BusinessException business exception
     */
    private String evaluateMinAmountLabelExpression(String expression, BillingAccount ba, Subscription subscription, ServiceInstance serviceInstance) throws BusinessException {
        if (StringUtils.isBlank(expression)) {
            return null;
        }

        Map<Object, Object> userMap = constructElContext(expression, ba, subscription, serviceInstance);

        return ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
    }

    /**
     * Construct EL context of variables
     *
     * @param expression EL expression
     * @param ba Billing account
     * @param subscription Subscription
     * @param serviceInstance Service instance
     * @return Context of variable
     */
    private Map<Object, Object> constructElContext(String expression, BillingAccount ba, Subscription subscription, ServiceInstance serviceInstance) {

        Map<Object, Object> contextMap = new HashMap<Object, Object>();

        if (expression.indexOf("serviceInstance") >= 0) {
            contextMap.put("serviceInstance", serviceInstance);
        }

        if (expression.indexOf("sub") >= 0) {
            if (subscription == null) {
                subscription = serviceInstance.getSubscription();
            }
            contextMap.put("sub", subscription);
        }
        if (expression.indexOf("offer") >= 0) {
            if (subscription == null) {
                subscription = serviceInstance.getSubscription();
            }
            contextMap.put("offer", subscription.getOffer());
        }

        if (expression.indexOf("ba") >= 0) {
            if (ba == null) {
                ba = subscription != null ? subscription.getUserAccount().getBillingAccount() : serviceInstance.getSubscription().getUserAccount().getBillingAccount();
            }

            contextMap.put("ba", ba);
        }

        if (expression.indexOf("ca") >= 0) {

            if (ba == null) {
                ba = subscription != null ? subscription.getUserAccount().getBillingAccount() : serviceInstance.getSubscription().getUserAccount().getBillingAccount();
            }
            contextMap.put("ca", ba.getCustomerAccount());
        }

        if (expression.indexOf("c") >= 0) {
            if (ba == null) {
                ba = subscription != null ? subscription.getUserAccount().getBillingAccount() : serviceInstance.getSubscription().getUserAccount().getBillingAccount();
            }
            contextMap.put("c", ba.getCustomerAccount().getCustomer());
        }

        if (expression.indexOf("prov") >= 0) {
            contextMap.put("prov", appProvider);
        }

        return contextMap;
    }

    /**
     * Compute the invoice amount for order.
     * 
     * @param order order
     * @param firstTransactionDate first transaction date.
     * @param lastTransactionDate last transaction date
     * @return computed order's invoice amount.
     */
    public Object[] computeOrderInvoiceAmount(Order order, Date firstTransactionDate, Date lastTransactionDate) {
        Query q = getEntityManager().createNamedQuery("RatedTransaction.sumByOrderNumber").setParameter("orderNumber", order.getOrderNumber())
            .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
        Object[] amounts = (Object[]) q.getSingleResult();
        return amounts;
    }
}