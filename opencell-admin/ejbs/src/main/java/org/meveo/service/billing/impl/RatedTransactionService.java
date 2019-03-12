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
import java.util.Iterator;
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
import org.meveo.model.BaseEntity;
import org.meveo.model.IBillableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Amounts;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionMinAmountTypeEnum;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.api.dto.ConsumptionDTO;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.order.OrderService;
import org.meveo.service.script.billing.TaxScriptService;

/**
 * RatedTransactionService : A class for Rated transaction persistence services.
 * 
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @author Abdelmounaim Akadid
 * @author Abdellatif BARI
 * @author Mounir BAHIJE
 * @lastModifiedVersion 7.0
 */
@Stateless
public class RatedTransactionService extends PersistenceService<RatedTransaction> {
	
	@Inject
	private ServiceInstanceService serviceInstanceService;

	@Inject
	private ChargeInstanceService<ChargeInstance> chargeInstanceService;

	@Inject
	private UserAccountService userAccountService;

    @Inject
    private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private TaxScriptService taxScriptService;

    @Inject
    private TaxService taxService;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private OrderService orderService;

    @Inject
    private SellerService sellerService;

    /** constants. */
    private final BigDecimal HUNDRED = new BigDecimal("100");

    /** description map. */
    private Map<String, String> descriptionMap = new HashMap<>();

    /**
     * @param userAccount user account
     * @return list
     */
    public List<RatedTransaction> getRatedTransactionsInvoiced(UserAccount userAccount) {
        if ((userAccount == null) || (userAccount.getWallet() == null)) {
            return null;
        }
        return getEntityManager().createNamedQuery("RatedTransaction.listInvoiced", RatedTransaction.class).setParameter("wallet", userAccount.getWallet())
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

            List<Object[]> results = query.getResultList();

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

        List<RatedTransaction> ratedTransactions = getEntityManager()
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
        boolean calculateTaxOnSubCategoryLevel = !isExonerated && (invoice.getInvoiceType().getTaxScript() == null);

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
                    if ((invoiceSubCategory.getDescriptionI18n() != null) && (invoiceSubCategory.getDescriptionI18n().get(languageCode) != null)) {
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
            if (isVirtual == true) {
                ratedTransaction.setStatus(RatedTransactionStatusEnum.VIRTUAL);
            } else {
                ratedTransaction.setStatus(RatedTransactionStatusEnum.BILLED);
            }
        }

        // Determine which discount plan items apply to this invoice
        List<DiscountPlanItem> subscriptionApplicableDiscountPlanItems = new ArrayList<>();
        List<DiscountPlanItem> billingAccountApplicableDiscountPlanItems = new ArrayList<>();

        Subscription subscription = invoice.getSubscription();
        CustomerAccount customerAccount = billingAccount.getCustomerAccount();

        if (subscription != null && subscription.getDiscountPlanInstances() != null && !subscription.getDiscountPlanInstances().isEmpty()) {
            subscriptionApplicableDiscountPlanItems.addAll(getApplicableDiscountPlanItems(billingAccount, subscription.getDiscountPlanInstances(), invoice, customerAccount));
        }
        if (billingAccount.getDiscountPlanInstances() != null && !billingAccount.getDiscountPlanInstances().isEmpty()) {
            billingAccountApplicableDiscountPlanItems.addAll(getApplicableDiscountPlanItems(billingAccount, billingAccount.getDiscountPlanInstances(), invoice, customerAccount));
        }

        // Calculate derived aggregate amounts for subcategory aggregate, create category aggregates, discount aggregates and tax aggregates
        BigDecimal[] amounts = null;
        for (SubCategoryInvoiceAgregate scAggregate : subCategoryAggregates.values()) {

            InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.retrieveIfNotManaged(scAggregate.getInvoiceSubCategory());

            // If tax calculation is done on subcategory level, evaluate tax again in case it was changed
            if (calculateTaxOnSubCategoryLevel) {
                Tax tax = null;

                // If there is a taxScript in invoiceSubCategory and script is applicable, use it to compute external taxes
                if (calculateExternalTax && (invoiceSubCategory.getTaxScript() != null)) {
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
                if ((scAggregate.getTaxPercent() == null) || (scAggregate.getTaxPercent().compareTo(tax.getPercent()) != 0)) {
                    log.trace("Will update {} rated transactions in subcategory {} with new tax from {} to {}", scAggregate.getItemNumber(),
                        scAggregate.getInvoiceSubCategory().getCode(), scAggregate.getTaxPercent(), tax.getPercent());
                    for (RatedTransaction ratedTransaction : scAggregate.getRatedtransactions()) {
                        if ("NO_OFFER".equals(ratedTransaction.getOfferCode())) {
                            int i = 5;
                        }

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
                    if ((invoiceSubCategory.getInvoiceCategory().getDescriptionI18n() != null)
                            && (invoiceSubCategory.getInvoiceCategory().getDescriptionI18n().get(languageCode) != null)) {
                        descTranslated = invoiceSubCategory.getInvoiceCategory().getDescriptionI18n().get(languageCode);
                    }
                    descriptionMap.put(translationCKey, descTranslated);
                }

                cAggregate.setDescription(descTranslated);

                categoryAggregates.put(caKey, cAggregate);

                invoice.addInvoiceAggregate(cAggregate);
            }

            cAggregate.addSubCategoryInvoiceAggregate(scAggregate);

            if ((amount != null) && !BigDecimal.ZERO.equals(amount)) {

                // Add discount aggregates for subscription
                for (DiscountPlanItem discountPlanItem : subscriptionApplicableDiscountPlanItems) {
                    SubCategoryInvoiceAgregate discountAggregate = getDiscountAggregates(billingAccount, invoice, isEnterprise, invoiceRounding, invoiceRoundingMode, scAggregate,
                            amount, cAggregate, discountPlanItem);
                    amountCumulativeForTax = amount.add(isEnterprise ? discountAggregate.getAmountWithoutTax() : discountAggregate.getAmountWithTax());
                }

                // Add discount aggregates for billingAccount
                for (DiscountPlanItem discountPlanItem : billingAccountApplicableDiscountPlanItems) {
                    SubCategoryInvoiceAgregate discountAggregate = getDiscountAggregates(billingAccount, invoice, isEnterprise, invoiceRounding, invoiceRoundingMode,
                            scAggregate, amountCumulativeForTax, cAggregate, discountPlanItem);
                    amountCumulativeForTax = amountCumulativeForTax.add(isEnterprise ? discountAggregate.getAmountWithoutTax() : discountAggregate.getAmountWithTax());
                }

                // Add tax aggregate or update its amounts

                if (calculateTaxOnSubCategoryLevel && (amountCumulativeForTax.compareTo(BigDecimal.ZERO) > 0)) {

                    TaxInvoiceAgregate taxAggregate = taxAggregates.get(scAggregate.getTax().getCode());
                    if (taxAggregate == null) {
                        taxAggregate = new TaxInvoiceAgregate(billingAccount, scAggregate.getTax(), scAggregate.getTaxPercent(), invoice);
                        taxAggregate.updateAudit(currentUser);
                        taxAggregates.put(scAggregate.getTax().getCode(), taxAggregate);

                        String translationCKey = "T_" + scAggregate.getTax().getId() + "_" + languageCode;
                        String descTranslated = descriptionMap.get(translationCKey);
                        if (descTranslated == null) {
                            descTranslated = scAggregate.getTax().getDescriptionOrCode();
                            if ((scAggregate.getTax().getDescriptionI18n() != null) && (scAggregate.getTax().getDescriptionI18n().get(languageCode) != null)) {
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
            if ((invoice.getInvoiceType() != null) && (invoice.getInvoiceType().getTaxScript() != null)) {
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
		if (!isExonerated && (taxAggregates != null) && !taxAggregates.isEmpty()) {
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
        if ((invoicingThreshold != null) && (invoicingThreshold.compareTo(isEnterprise ? invoice.getAmountWithoutTax() : invoice.getAmountWithTax()) > 0)) {
            throw new BusinessException("Invoice amount below the threshold");
        }
    }

    private SubCategoryInvoiceAgregate getDiscountAggregates(BillingAccount billingAccount, Invoice invoice, boolean isEnterprise,
            int invoiceRounding, RoundingModeEnum invoiceRoundingMode, SubCategoryInvoiceAgregate scAggregate, BigDecimal amount,
            CategoryInvoiceAgregate cAggregate, DiscountPlanItem discountPlanItem) throws BusinessException {
        BigDecimal[] amounts;// Apply discount if matches the category, subcategory, or applies to any category
        SubCategoryInvoiceAgregate discountAggregate;
        if ((discountPlanItem.getInvoiceCategory() == null && discountPlanItem.getInvoiceSubCategory() == null)
                || (discountPlanItem.getInvoiceSubCategory() != null
                        && discountPlanItem.getInvoiceSubCategory().getId().equals(scAggregate.getInvoiceSubCategory().getId()))
                || (discountPlanItem.getInvoiceCategory() != null && discountPlanItem.getInvoiceSubCategory() == null
                        && discountPlanItem.getInvoiceCategory().getId().equals(scAggregate.getInvoiceSubCategory().getInvoiceCategory().getId()))) {
            BigDecimal discountValue = discountPlanItem.getDiscountValue();

            if (discountPlanItem.getDiscountValueEL() != null) {
                discountValue = evaluateDiscountPercentExpression(discountPlanItem.getDiscountValueEL(), scAggregate.getUserAccount(), scAggregate.getWallet(),
                    invoice, amount);
                log.debug("for discountPlan {} percentEL -> {}  on amount={}", discountPlanItem.getCode(),
                        discountValue, amount);
            }

            BigDecimal discountAmount;
            if (discountPlanItem.getDiscountPlanItemType().equals(DiscountPlanItemTypeEnum.PERCENTAGE)) {
                discountAmount = amount.multiply(discountValue.divide(HUNDRED)).negate().setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode());
            } else {
                discountAmount = discountValue.negate().setScale(invoiceRounding, invoiceRoundingMode.getRoundingMode());;
            }

            if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
                discountAggregate = new SubCategoryInvoiceAgregate(scAggregate.getInvoiceSubCategory(), billingAccount,
                    scAggregate.getUserAccount(), scAggregate.getWallet(), scAggregate.getTax(), scAggregate.getTaxPercent(), invoice, null);

                discountAggregate.updateAudit(currentUser);
                discountAggregate.setItemNumber(scAggregate.getItemNumber());
                discountAggregate.setCategoryInvoiceAgregate(cAggregate);

                discountAggregate.setDiscountAggregate(true);
                if (discountPlanItem.getDiscountPlanItemType()
                        .equals(DiscountPlanItemTypeEnum.PERCENTAGE)) {
                    discountAggregate.setDiscountPercent(discountValue);
                }
                discountAggregate.setDiscountPlanItem(discountPlanItem);
                discountAggregate.setDescription(discountPlanItem.getCode());

                amounts = NumberUtils.computeDerivedAmounts(discountAmount, discountAmount, scAggregate.getTaxPercent(), isEnterprise, invoiceRounding,
                    invoiceRoundingMode.getRoundingMode());

                discountAggregate.setAmountWithoutTax(amounts[0]);
                discountAggregate.setAmountWithTax(amounts[1]);
                discountAggregate.setAmountTax(amounts[2]);

                invoice.addInvoiceAggregate(discountAggregate);
                return discountAggregate;
            }
        }
        return null;
    }

    private List<DiscountPlanItem> getApplicableDiscountPlanItems(BillingAccount billingAccount,List<DiscountPlanInstance> discountPlanInstances, Invoice invoice, CustomerAccount customerAccount)
            throws BusinessException {
        List<DiscountPlanItem> applicableDiscountPlanItems = new ArrayList<>();
        for (DiscountPlanInstance dpi : discountPlanInstances) {
            if (!dpi.isEffective(invoice.getInvoiceDate())) {
                continue;
            }
            if (dpi.getDiscountPlan().isActive()) {
                List<DiscountPlanItem> discountPlanItems = dpi.getDiscountPlan().getDiscountPlanItems();
                for (DiscountPlanItem discountPlanItem : discountPlanItems) {
                    if (discountPlanItem.isActive() && matchDiscountPlanItemExpression(
                            discountPlanItem.getExpressionEl(), customerAccount, billingAccount, invoice, dpi)) {
                        applicableDiscountPlanItems.add(discountPlanItem);
                    }
                }
            }
        }
        return applicableDiscountPlanItems;
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
        List<RatedTransaction> ratedTransactions = getEntityManager().createNamedQuery("RatedTransaction.listByWalletOperationId", RatedTransaction.class)
            .setParameter("walletOperationId", id).getResultList();
        for (RatedTransaction ratedTransaction : ratedTransactions) {
            BillingRun billingRun = ratedTransaction.getBillingRun();
            if ((billingRun != null) && (billingRun.getStatus() != BillingRunStatusEnum.CANCELLED)) {
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
            return qb.getQuery(getEntityManager()).getResultList();
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
            return qb.getQuery(getEntityManager()).getResultList();
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
     * @param dpi the discount plan instance
     * @return true/false
     * @throws BusinessException business exception.
     */
	private boolean matchDiscountPlanItemExpression(String expression, CustomerAccount customerAccount,
			BillingAccount billingAccount, Invoice invoice, DiscountPlanInstance dpi) throws BusinessException {
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
        if (expression.indexOf("dpi") >= 0) {
            userMap.put("dpi", dpi);
        }
        if (expression.indexOf("su") >= 0) {
            userMap.put("su", invoice.getSubscription());
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
        if ((invoice == null) || (invoiceSubCategory == null)) {
            return null;
        }
        return getEntityManager().createNamedQuery("RatedTransaction.getListByInvoiceAndSubCategory", RatedTransaction.class)
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
        } else {
            create(ratedTransaction);
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
	 * Create a {@link RatedTransaction} from a group of wallet operations.
	 * 
	 * @param aggregatedWo
	 *            aggregated wallet operations
	 * @param aggregatedSettings
	 *            aggregation settings of wallet operations
	 * @param invoicingDate
	 *            the invoicing date
	 * @return created {@link RatedTransaction}
	 * @throws BusinessException
	 *             Exception when RT is not create successfully
	 * @see WalletOperation
	 */
	public RatedTransaction createRatedTransaction(AggregatedWalletOperation aggregatedWo,
			RatedTransactionsJobAggregationSetting aggregatedSettings, Date invoicingDate) throws BusinessException {
		return createRatedTransaction(aggregatedWo, aggregatedSettings, invoicingDate, false);
	}

	/**
	 * 
	 * @param aggregatedWo
	 *            aggregated wallet operations
	 * @param aggregationSettings
	 *            aggregation settings of wallet operations
	 * @param isVirtual
	 *            is virtual
	 * @param invoicingDate
	 *            the invoicing date
	 * @return {@link RatedTransaction}
	 * @throws BusinessException
	 *             Exception when RT is not create successfully
	 */
	public RatedTransaction createRatedTransaction(AggregatedWalletOperation aggregatedWo,
			RatedTransactionsJobAggregationSetting aggregationSettings, Date invoicingDate, boolean isVirtual)
			throws BusinessException {
		RatedTransaction ratedTransaction = new RatedTransaction();

		Seller seller = null;
		BillingAccount ba = null;
		UserAccount ua = null;
		Subscription sub = null;
		ServiceInstance si = null;
		ChargeInstance ci = null;
		String code = null;
		String description = null;
		InvoiceSubCategory isc = null;

		Calendar cal = Calendar.getInstance();
		if (aggregationSettings.isAggregateByDay()) {
			cal.set(Calendar.YEAR, aggregatedWo.getYear(), aggregatedWo.getMonth(), aggregatedWo.getDay(), 0, 0);
			ratedTransaction.setUsageDate(cal.getTime());
		} else {
			cal.set(Calendar.YEAR, aggregatedWo.getYear(), aggregatedWo.getMonth(), 1, 0, 0);
			ratedTransaction.setUsageDate(cal.getTime());

		}

		isc = invoiceSubCategoryService.refreshOrRetrieve(aggregatedWo.getInvoiceSubCategory());

		switch (aggregationSettings.getAggregationLevel()) {
		case BA:
			ba = billingAccountService.findById(aggregatedWo.getIdAsLong());
			seller = ba.getCustomerAccount().getCustomer().getSeller();
			code = isc.getCode();
			description = isc.getDescription();
			break;

		case UA:
			ua = userAccountService.findById(aggregatedWo.getIdAsLong());
			ba = ua.getBillingAccount();
			seller = ba.getCustomerAccount().getCustomer().getSeller();
			code = isc.getCode();
			description = isc.getDescription();
			break;

		case SUB:
			sub = subscriptionService.findById(aggregatedWo.getIdAsLong());
			ua = sub.getUserAccount();
			ba = ua.getBillingAccount();
			seller = sub.getSeller();
			code = isc.getCode();
			description = isc.getDescription();
			break;

		case SI:
			si = serviceInstanceService.findById(aggregatedWo.getIdAsLong());
			sub = si.getSubscription();
			ua = sub.getUserAccount();
			ba = ua.getBillingAccount();
			seller = sub.getSeller();
			code = si.getCode();
			description = si.getDescription();
			break;

		case CI:
			ci = (ChargeInstance) chargeInstanceService.findById(aggregatedWo.getIdAsLong());
			sub = ci.getSubscription();
			ua = sub.getUserAccount();
			ba = ua.getBillingAccount();
			seller = sub.getSeller();
			code = ci.getCode();
			description = ci.getDescription();
			break;

		case DESC:
			ci = (ChargeInstance) chargeInstanceService.findById(aggregatedWo.getIdAsLong());
			sub = ci.getSubscription();
			ua = sub.getUserAccount();
			ba = ua.getBillingAccount();
			seller = sub.getSeller();
			code = ci.getCode();
			description = aggregatedWo.getComputedDescription();
			break;

		default:
			ba = billingAccountService.findById(aggregatedWo.getIdAsLong());
			seller = ba.getCustomerAccount().getCustomer().getSeller();
		}

		if(aggregationSettings.isAggregateByOrder()) {
			ratedTransaction.setOrderNumber(aggregatedWo.getOrderNumber());
		}
		if (aggregationSettings.isAggregateByParam1()) {
			ratedTransaction.setParameter1(aggregatedWo.getParameter1());
		}
		if (aggregationSettings.isAggregateByParam2()) {
			ratedTransaction.setParameter2(aggregatedWo.getParameter2());
		}
		if (aggregationSettings.isAggregateByParam3()) {
			ratedTransaction.setParameter3(aggregatedWo.getParameter3());
		}
		if (aggregationSettings.isAggregateByExtraParam()) {
			ratedTransaction.setParameterExtra(aggregatedWo.getParameterExtra());
		}

		Tax tax = taxService.refreshOrRetrieve(aggregatedWo.getTax());

		ratedTransaction.setCode(code);
		ratedTransaction.setDescription(description);
		ratedTransaction.setTax(tax);
		ratedTransaction.setTaxPercent(tax.getPercent());
		ratedTransaction.setInvoiceSubCategory(isc);
		ratedTransaction.setSeller(seller);
		ratedTransaction.setBillingAccount(ba);
		ratedTransaction.setUserAccount(ua);
		ratedTransaction.setSubscription(sub);
		ratedTransaction.setChargeInstance(ci);
		ratedTransaction.setAmountWithTax(aggregatedWo.getAmountWithTax());
		ratedTransaction.setAmountTax(aggregatedWo.getAmountTax());
		ratedTransaction.setAmountWithoutTax(aggregatedWo.getAmountWithoutTax());
		ratedTransaction.setUnitAmountWithTax(aggregatedWo.getUnitAmountWithTax());
		ratedTransaction.setUnitAmountTax(aggregatedWo.getUnitAmountTax());
		ratedTransaction.setUnitAmountWithoutTax(aggregatedWo.getUnitAmountWithoutTax());
		ratedTransaction.setQuantity(aggregatedWo.getQuantity());

		if (!isVirtual) {
			create(ratedTransaction);

			WalletOperationAggregatorQueryBuilder woa = new WalletOperationAggregatorQueryBuilder(aggregationSettings);
			String strQuery = woa.listWoQuery(aggregatedWo.getIdAsLong());
			Query query = getEntityManager().createQuery(strQuery);
			query.setParameter("invoicingDate", invoicingDate);
			List<WalletOperation> walletOps = (List<WalletOperation>) query.getResultList();
			for (WalletOperation tempWo : walletOps) {
				tempWo.setRatedTransaction(ratedTransaction);
			}
		}

		return ratedTransaction;
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
            return qb.getQuery(getEntityManager()).getResultList();
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
            return qb.getQuery(getEntityManager()).getResultList();
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
        if ((rsToCancelIds.size() > 0) && !rsToCancelIds.isEmpty()) {
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

        BillingAccount billingAccount = null;
        if (entity instanceof BillingAccount) {
            entity = billingAccountService.findById((Long) entity.getId());
            billingAccount = (BillingAccount) entity;
        }

        if (entity instanceof Subscription) {
            entity = subscriptionService.findById((Long) entity.getId());
            billingAccount = ((Subscription) entity).getUserAccount() != null ? ((Subscription) entity).getUserAccount().getBillingAccount() : null;
        }

        if (entity instanceof Order) {
            entity = orderService.findById((Long) entity.getId());
            if ((((Order) entity).getUserAccounts() != null) && !((Order) entity).getUserAccounts().isEmpty()) {
                billingAccount = ((Order) entity).getUserAccounts().stream().findFirst().get() != null ?
                        (((Order) entity).getUserAccounts().stream().findFirst().get()).getBillingAccount() : null;
            }
        }


        calculateAmountsAndCreateMinAmountTransactions(entity, null, billingRun.getLastTransactionDate());

        BigDecimal invoiceAmount = entity.getTotalInvoicingAmountWithoutTax();
        if (invoiceAmount != null) {
            BigDecimal invoicingThreshold = null;
            if (billingAccount != null) {
                invoicingThreshold = billingAccount.getInvoicingThreshold();
            }
            if ((invoicingThreshold == null) && (billingRun.getBillingCycle() != null)) {
                invoicingThreshold = billingRun.getBillingCycle().getInvoicingThreshold();
            }


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

        BigDecimal totalInvoiceAmountWithoutTax = BigDecimal.ZERO;
        BigDecimal totalInvoiceAmountWithTax = BigDecimal.ZERO;
        BigDecimal totalInvoiceAmountTax = BigDecimal.ZERO;

        if (billableEntity instanceof Order) {
            Object[] amounts = computeOrderInvoiceAmount((Order) billableEntity, new Date(0), lastTransactionDate);
            totalInvoiceAmountWithoutTax = (BigDecimal) amounts[0];
            totalInvoiceAmountWithTax = (BigDecimal) amounts[1];
            totalInvoiceAmountTax = (BigDecimal) amounts[2];

        } else {

            // Select subscriptions to be analyzed
            BillingAccount billingAccount = null;
            List<Subscription> subscriptionsToProcess = new ArrayList<Subscription>();
            if (billableEntity instanceof Subscription) {
                subscriptionsToProcess.add((Subscription) billableEntity);
                billingAccount = ((Subscription) billableEntity).getUserAccount().getBillingAccount();
            }

            if (billableEntity instanceof BillingAccount) {
                billingAccount = (BillingAccount) billableEntity;
                for (UserAccount userAccount : ((BillingAccount) billableEntity).getUsersAccounts()) {
                    for (Subscription subscription : userAccount.getSubscriptions()) {
                        subscriptionsToProcess.add(subscription);
                    }
                }
            }

            // Map key is <seller.id>_<invoiceSubCategory.id>
            Map<String, Amounts> baLevelAmounts = new HashMap<>();
            Amounts totalAmounts = new Amounts();

            List<RatedTransaction> minAmountTransactions = new ArrayList<RatedTransaction>();

            Date minRatingDate = DateUtils.addDaysToDate(lastTransactionDate, -1);

            // Analyze total amounts per service and subscription and create Rated transactions to reach a minimum amount per service and/or subscription if applicable
            for (Subscription subscription : subscriptionsToProcess) {

                String mapKeyPrefix = subscription.getSeller().getId().toString() + "_";

                UserAccount userAccount = subscription.getUserAccount();

                // Map key is <seller.id>_<invoiceSubCategory.id>
                Map<String, Amounts> subscriptionLevelExtraAmounts = new HashMap<>();

                // Create RTs to reach min amounts for services that require it
                for (ServiceInstance serviceInstance : subscription.getServiceInstances()) {

                    if (serviceInstance.getStatus().equals(InstanceStatusEnum.ACTIVE)) {
                        Map<String, Amounts> createdAmounts = createMinRTForService(serviceInstance, lastTransactionDate, billingAccount, minRatingDate, minAmountTransactions);

                        // Update subscription level amounts with extra amounts
                        if (createdAmounts != null) {
                            for (Entry<String, Amounts> createdAmount : createdAmounts.entrySet()) {
                                if (subscriptionLevelExtraAmounts.containsKey(createdAmount.getKey())) {
                                    subscriptionLevelExtraAmounts.get(createdAmount.getKey()).addAmounts(createdAmount.getValue());
                                } else {
                                    subscriptionLevelExtraAmounts.put(createdAmount.getKey(), createdAmount.getValue());
                                }
                            }
                        }
                    }
                }

                // Create RTs to reach min amounts for subscriptions that require it and calculate/update total amounts and amounts per Billing account level
                createMinRTForSubscription(subscription, lastTransactionDate, billingAccount, minRatingDate, minAmountTransactions, subscriptionLevelExtraAmounts, totalAmounts,
                    baLevelAmounts);
            }

            if ((billableEntity instanceof BillingAccount) && (totalAmounts.getAmountWithoutTax().compareTo(BigDecimal.ZERO) != 0)) {
                createMinRTForBillingAccount(billingAccount, lastTransactionDate, minRatingDate, minAmountTransactions, totalAmounts, baLevelAmounts);
            }

            if ((billableEntity instanceof Subscription) || (billableEntity instanceof BillingAccount)) {
                totalInvoiceAmountWithoutTax = totalAmounts.getAmountWithoutTax();
                totalInvoiceAmountWithTax = totalAmounts.getAmountWithTax();
                totalInvoiceAmountTax = totalInvoiceAmountWithTax.subtract(totalInvoiceAmountWithoutTax);
            }

            billableEntity.setMinRatedTransactions(minAmountTransactions);
        }

        billableEntity.setTotalInvoicingAmountWithoutTax(totalInvoiceAmountWithoutTax);
        billableEntity.setTotalInvoicingAmountWithTax(totalInvoiceAmountWithTax);
        billableEntity.setTotalInvoicingAmountTax(totalInvoiceAmountTax);

    }

    /**
     * Create Rated transactions to reach minimum invoiced amount per service. Updates minAmountTransactions parameter.
     * 
     * @param serviceInstance Service instance
     * @param lastTransactionDate Last transaction date
     * @param billingAccount Billing account
     * @param minRatingDate Date to assign to newly created minimum amount Rated transactions
     * @param minAmountTransactions Newly created minimum amount Rated transactions. ARE UPDATED by this method. Rated trancastions created in this method are appended.
     * @return A map of amounts created. With <seller.id>_<invoiceSubCategory.id> as a key a and amounts as values
     * @throws BusinessException General business exception
     */
    private Map<String, Amounts> createMinRTForService(ServiceInstance serviceInstance, Date lastTransactionDate, BillingAccount billingAccount, Date minRatingDate,
            List<RatedTransaction> minAmountTransactions) throws BusinessException {

        // Only interested in services with minAmount condition
        BigDecimal minAmount = null;
        String minAmountLabel = null;

        String minAmountEL = StringUtils.isBlank(serviceInstance.getMinimumAmountEl()) ? serviceInstance.getServiceTemplate().getMinimumAmountEl()
                : serviceInstance.getMinimumAmountEl();
        String minAmountLabelEL = StringUtils.isBlank(serviceInstance.getMinimumLabelEl()) ? serviceInstance.getServiceTemplate().getMinimumLabelEl()
                : serviceInstance.getMinimumLabelEl();

        if (!StringUtils.isBlank(minAmountEL)) {
            minAmount = evaluateMinAmountExpression(minAmountEL, null, null, serviceInstance);
            minAmountLabel = evaluateMinAmountLabelExpression(minAmountLabelEL, null, null, serviceInstance);
        }
        if (minAmount == null) {
            return null;
        }

        // Calculate amounts on service level grouped by invoice category.
        // Calculate a total sum of amounts on service level
        List<ChargeInstance> chargeInstances = new ArrayList<>();
        chargeInstances.addAll(serviceInstance.getSubscriptionChargeInstances());
        chargeInstances.addAll(serviceInstance.getRecurringChargeInstances());
        chargeInstances.addAll(serviceInstance.getUsageChargeInstances());
        chargeInstances.addAll(serviceInstance.getTerminationChargeInstances());

        BigDecimal totalServiceAmountWithoutTax = BigDecimal.ZERO;
        BigDecimal totalServiceAmountWithTax = BigDecimal.ZERO;
        BigDecimal totalServiceAmount = BigDecimal.ZERO;

        List<Object[]> amountsList = computeInvoiceableAmountForService(chargeInstances, new Date(0), lastTransactionDate);

        Subscription subscription = serviceInstance.getSubscription();
        Seller seller = subscription.getSeller();
        String mapKeyPrefix = seller.getId().toString() + "_";

        Map<String, Amounts> serviceAmountMap = new HashMap<String, Amounts>();
        for (Object[] amounts : amountsList) {
            BigDecimal chargeAmountWithoutTax = (BigDecimal) amounts[0];
            BigDecimal chargeAmountWithTax = (BigDecimal) amounts[1];

            String mapKey = mapKeyPrefix + amounts[2];

            if (chargeAmountWithoutTax.compareTo(BigDecimal.ZERO) != 0) {
                if (serviceAmountMap.containsKey(mapKey)) {
                    serviceAmountMap.get(mapKey).addAmounts(chargeAmountWithoutTax, chargeAmountWithTax);
                } else {
                    serviceAmountMap.put(mapKey, new Amounts(chargeAmountWithoutTax, chargeAmountWithTax));
                }
            }

            totalServiceAmountWithoutTax = totalServiceAmountWithoutTax.add(chargeAmountWithoutTax);
            totalServiceAmountWithTax = totalServiceAmountWithTax.add(chargeAmountWithTax);
        }

        totalServiceAmount = appProvider.isEntreprise() ? totalServiceAmountWithoutTax : totalServiceAmountWithTax;

        // Service amount exceed the minimum amount per service
        if (totalServiceAmount.compareTo(minAmount) >= 0) {
            return null;
        }

        // Create Rated transactions to reach a minimum amount per service
        UserAccount userAccount = serviceInstance.getSubscription().getUserAccount();
        Map<String, Amounts> minRTAmountMap = new HashMap<String, Amounts>();

        BigDecimal totalRatio = BigDecimal.ZERO;
        Iterator<Entry<String, Amounts>> amountIterator = serviceAmountMap.entrySet().iterator();

        while (amountIterator.hasNext()) {
            Entry<String, Amounts> amountsEntry = amountIterator.next();

            String mapKey = amountsEntry.getKey();

            BigDecimal serviceAmount = appProvider.isEntreprise() ? amountsEntry.getValue().getAmountWithoutTax() : amountsEntry.getValue().getAmountWithTax();

            BigDecimal diff = minAmount.subtract(totalServiceAmount);
            BigDecimal ratio = totalServiceAmount.compareTo(serviceAmount) == 0 ? BigDecimal.ONE : serviceAmount.divide(totalServiceAmount, 4, RoundingMode.HALF_UP);

            // Ensure that all ratios sum up to 1
            if (!amountIterator.hasNext()) {
                ratio = BigDecimal.ONE.subtract(totalRatio);
            }

            String[] ids = mapKey.split("_");

            InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findById(Long.parseLong(ids[1]));
            Tax tax = invoiceSubCategoryCountryService.determineTax(invoiceSubCategory, seller, billingAccount, minRatingDate, false);

            BigDecimal rtMinAmount = diff.multiply(ratio);

            BigDecimal[] unitAmounts = NumberUtils.computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), BaseEntity.NB_DECIMALS,
                RoundingMode.HALF_UP);
            BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), appProvider.getRounding(),
                appProvider.getRoundingMode().getRoundingMode());

            RatedTransaction ratedTransaction = new RatedTransaction(minRatingDate, unitAmounts[0], unitAmounts[1], unitAmounts[2], BigDecimal.ONE, amounts[0], amounts[1],
                amounts[2], RatedTransactionStatusEnum.OPEN, null, billingAccount, null, invoiceSubCategory, null, null, null, null, null, null, null, null, null, "NO_OFFER", null,
                RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_SE.getCode() + "_" + serviceInstance.getCode(), minAmountLabel, null, null, seller, tax, tax.getPercent());

            minAmountTransactions.add(ratedTransaction);

            // Remember newly "created" transaction amounts, as they are not persisted yet to DB
            minRTAmountMap.put(mapKey, new Amounts(amounts[0], amounts[1]));

            totalRatio = totalRatio.add(ratio);
        }

        return minRTAmountMap;
    }

    /**
     * Create Rated transactions to reach minimum invoiced amount per subscription and update total amount sum. Updates minAmountTransactions, baLeveltotalAmounts and
     * baLevelAmounts parameters.
     * 
     * @param subscription Subscription
     * @param lastTransactionDate Last transaction date
     * @param billingAccount Billing account
     * @param minRatingDate Date to assign to newly created minimum amount Rated transactions
     * @param minAmountTransactions Newly created minimum amount Rated transactions. ARE UPDATED by this method. Rated trancastions created in this method are appended.
     * @param newMinAmountsFromServices Additional Rated transaction amounts created in services level analysis with <seller.id>_<invoiceSubCategory.id> as key and amounts as
     *        value.
     * @param baLeveltotalAmounts A sum of amounts irrelevant of seller or invoice subcategory. ARE UPDATED by this method. Amounts calculated on service and subscription level are
     *        appended to the total amounts.
     * @param baLevelAmounts Billing account level amounts. ARE UPDATED by this method. Amounts calculated on service and subscription level are appended to the total amounts.
     * @throws BusinessException General Business exception
     */
    private void createMinRTForSubscription(Subscription subscription, Date lastTransactionDate, BillingAccount billingAccount, Date minRatingDate,
            List<RatedTransaction> minAmountTransactions, Map<String, Amounts> newMinAmountsFromServices, Amounts baLeveltotalAmounts, Map<String, Amounts> baLevelAmounts)
            throws BusinessException {

        // Initialize subscription level amounts with values of newly created RTS from services
        BigDecimal totalSubscriptionAmountWithoutTax = BigDecimal.ZERO;
        BigDecimal totalSubscriptionAmountWithTax = BigDecimal.ZERO;
        Map<String, Amounts> subscriptionLevelAmounts = new HashMap<String, Amounts>(newMinAmountsFromServices);

        for (Amounts amount : newMinAmountsFromServices.values()) {
            totalSubscriptionAmountWithoutTax = totalSubscriptionAmountWithoutTax.add(amount.getAmountWithoutTax());
            totalSubscriptionAmountWithTax = totalSubscriptionAmountWithTax.add(amount.getAmountWithTax());
        }

        // Calculate amounts on subscription level grouped by invoice category
        // Calculate total amounts on subscription level
        List<Object[]> amountsList = computeInvoiceableAmountForSubscription(subscription, new Date(0), lastTransactionDate);

        Seller seller = subscription.getSeller();
        String mapKeyPrefix = seller.getId().toString() + "_";

        for (Object[] amounts : amountsList) {
            BigDecimal chargeAmountWithoutTax = (BigDecimal) amounts[0];
            BigDecimal chargeAmountWithTax = (BigDecimal) amounts[1];

            String amountsKey = mapKeyPrefix + amounts[2];

            if (chargeAmountWithoutTax.compareTo(BigDecimal.ZERO) != 0) {
                if (subscriptionLevelAmounts.containsKey(amountsKey)) {
                    subscriptionLevelAmounts.get(amountsKey).addAmounts(chargeAmountWithoutTax, chargeAmountWithTax);
                } else {
                    subscriptionLevelAmounts.put(amountsKey, new Amounts(chargeAmountWithoutTax, chargeAmountWithTax));
                }
            }

            totalSubscriptionAmountWithoutTax = totalSubscriptionAmountWithoutTax.add(chargeAmountWithoutTax);
            totalSubscriptionAmountWithTax = totalSubscriptionAmountWithTax.add(chargeAmountWithTax);
        }

        // Update the total/ba level amounts with subscription level amounts
        baLeveltotalAmounts.addAmounts(totalSubscriptionAmountWithoutTax, totalSubscriptionAmountWithTax);

        for (Entry<String, Amounts> amount : subscriptionLevelAmounts.entrySet()) {
            if (baLevelAmounts.containsKey(amount.getKey())) {
                baLevelAmounts.get(amount.getKey()).addAmounts(amount.getValue());
            } else {
                baLevelAmounts.put(amount.getKey(), amount.getValue());
            }
        }

        BigDecimal minAmount = null;
        String minAmountLabel = null;

        String minAmountEL = subscription.getMinimumAmountEl();
        String minAmountLabelEL = subscription.getMinimumLabelEl();

        if (!StringUtils.isBlank(minAmountEL)) {
            minAmount = evaluateMinAmountExpression(minAmountEL, null, subscription, null);
            minAmountLabel = evaluateMinAmountLabelExpression(minAmountLabelEL, null, subscription, null);
        }
        // No min amount criteria. DO NOT MOVE this above the total amount calculation/update.
        if (minAmount == null) {
            return;
        }

        BigDecimal totalSubscriptionAmount = appProvider.isEntreprise() ? totalSubscriptionAmountWithoutTax : totalSubscriptionAmountWithTax;

        // Subscription level amount exceeds the minimum amount required per subscription
        if (totalSubscriptionAmount.compareTo(minAmount) >= 0) {
            return;
        }

        // Create Rated transactions to reach minimum amount per Subscription
        UserAccount userAccount = subscription.getUserAccount();

        BigDecimal totalRatio = BigDecimal.ZERO;
        Iterator<Entry<String, Amounts>> amountIterator = subscriptionLevelAmounts.entrySet().iterator();

        while (amountIterator.hasNext()) {
            Entry<String, Amounts> amountsEntry = amountIterator.next();

            String amountsKey = amountsEntry.getKey();

            BigDecimal subscriptionAmount = appProvider.isEntreprise() ? amountsEntry.getValue().getAmountWithoutTax() : amountsEntry.getValue().getAmountWithTax();

            BigDecimal diff = minAmount.subtract(totalSubscriptionAmount);
            BigDecimal ratio = totalSubscriptionAmount.compareTo(subscriptionAmount) == 0 ? BigDecimal.ONE
                    : subscriptionAmount.divide(totalSubscriptionAmount, 4, RoundingMode.HALF_UP);

            // Ensure that all ratios sum up to 1
            if (!amountIterator.hasNext()) {
                ratio = BigDecimal.ONE.subtract(totalRatio);
            }

            String[] ids = amountsKey.split("_");
            InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findById(Long.parseLong(ids[1]));
            Tax tax = invoiceSubCategoryCountryService.determineTax(invoiceSubCategory, seller, billingAccount, minRatingDate, false);

            BigDecimal rtMinAmount = diff.multiply(ratio);

            BigDecimal[] unitAmounts = NumberUtils.computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), BaseEntity.NB_DECIMALS,
                RoundingMode.HALF_UP);
            BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), appProvider.getRounding(),
                appProvider.getRoundingMode().getRoundingMode());

            RatedTransaction ratedTransaction = new RatedTransaction(minRatingDate, unitAmounts[0], unitAmounts[1], unitAmounts[2], BigDecimal.ONE, amounts[0], amounts[1],
                amounts[2], RatedTransactionStatusEnum.OPEN, null, billingAccount, null, invoiceSubCategory, null, null, null, null, null, subscription, null, null, null,
                "NO_OFFER", null, RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_SU.getCode() + "_" + subscription.getCode(), minAmountLabel, null, null, seller, tax,
                tax.getPercent());

            minAmountTransactions.add(ratedTransaction);

            // Update the total/ba level amounts
            baLeveltotalAmounts.addAmounts(amounts[0], amounts[1]);
            baLevelAmounts.get(amountsKey).addAmounts(amounts[0], amounts[1]);

            totalRatio = totalRatio.add(ratio);
        }
    }

    /**
     * Create Rated transactions to reach minimum invoiced amount per Billing account and update total amount sum. Updates minAmountTransactions, baLeveltotalAmounts and
     * baLevelAmounts parameters.
     * 
     * @param billingAccount Billing account
     * @param lastTransactionDate Last transaction date
     * @param minRatingDate Date to assign to newly created minimum amount Rated transactions
     * @param minAmountTransactions Newly created minimum amount Rated transactions. ARE UPDATED by this method. Rated trancastions created in this method are appended.
     * @param baLeveltotalAmounts A sum of amounts irrelevant of seller or invoice subcategory. ARE UPDATED by this method. Amounts calculated on service and subscription level are
     *        appended to the total amounts.
     * @param baLevelAmounts Billing account level amounts. ARE UPDATED by this method. Amounts calculated on service and subscription level are appended to the total amounts.
     * @throws BusinessException General business exception
     */
    private void createMinRTForBillingAccount(BillingAccount billingAccount, Date lastTransactionDate, Date minRatingDate, List<RatedTransaction> minAmountTransactions,
            Amounts baLeveltotalAmounts, Map<String, Amounts> baLevelAmounts) throws BusinessException {

        // Calculate amounts on Billing account level grouped by invoice category. Take into account only those transactions that are not linked to Subscription. The Subscription
        // linked transactions are already present in baLevelAmounts.
        List<Object[]> amountsList = computeInvoiceableAmountForBANotSubscriptionRelated(billingAccount, new Date(0), lastTransactionDate);

        for (Object[] amounts : amountsList) {
            BigDecimal chargeAmountWithoutTax = (BigDecimal) amounts[0];
            BigDecimal chargeAmountWithTax = (BigDecimal) amounts[1];

            String amountsKey = amounts[3] + "_" + amounts[2];

            if (chargeAmountWithoutTax.compareTo(BigDecimal.ZERO) != 0) {
                if (baLevelAmounts.containsKey(amountsKey)) {
                    baLevelAmounts.get(amountsKey).addAmounts(chargeAmountWithoutTax, chargeAmountWithTax);
                } else {
                    baLevelAmounts.put(amountsKey, new Amounts(chargeAmountWithoutTax, chargeAmountWithTax));
                }
            }
            baLeveltotalAmounts.addAmounts(chargeAmountWithoutTax, chargeAmountWithTax);
        }

        // Interested in Billing accounts with minimum amount criteria
        BigDecimal minAmount = null;
        String minAmountLabel = null;

        String minAmountEL = billingAccount.getMinimumAmountEl();
        String minAmountLabelEL = billingAccount.getMinimumLabelEl();

        if (!StringUtils.isBlank(minAmountEL)) {
            minAmount = evaluateMinAmountExpression(minAmountEL, billingAccount, null, null);
            minAmountLabel = evaluateMinAmountLabelExpression(minAmountLabelEL, billingAccount, null, null);
        }

        if (minAmount == null) {
            return;
        }

        BigDecimal totalBaAmount = appProvider.isEntreprise() ? baLeveltotalAmounts.getAmountWithoutTax() : baLeveltotalAmounts.getAmountWithTax();

        // Billing account level amount exceeds the minimum amount required per Billing account
        if (totalBaAmount.compareTo(minAmount) >= 0) {
            return;
        }

        // Create Rated transactions to reach minimum amount per Billing account
        BigDecimal totalRatio = BigDecimal.ZERO;
        Iterator<Entry<String, Amounts>> amountIterator = baLevelAmounts.entrySet().iterator();

        while (amountIterator.hasNext()) {
            Entry<String, Amounts> amountsEntry = amountIterator.next();

            String amountsKey = amountsEntry.getKey();

            BigDecimal baAmount = appProvider.isEntreprise() ? amountsEntry.getValue().getAmountWithoutTax() : amountsEntry.getValue().getAmountWithTax();

            BigDecimal diff = minAmount.subtract(totalBaAmount);
            BigDecimal ratio = totalBaAmount.compareTo(baAmount) == 0 ? BigDecimal.ONE : baAmount.divide(totalBaAmount, 4, RoundingMode.HALF_UP);

            // Ensure that all ratios sum up to 1
            if (!amountIterator.hasNext()) {
                ratio = BigDecimal.ONE.subtract(totalRatio);
            }

            String[] ids = amountsKey.split("_");
            Long sellerId = Long.parseLong(amountsEntry.getKey().substring(0, amountsEntry.getKey().indexOf("_")));
            Seller seller = sellerService.findById(sellerId);
            InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findById(Long.parseLong(ids[1]));

            Tax tax = invoiceSubCategoryCountryService.determineTax(invoiceSubCategory, seller, billingAccount, minRatingDate, false);

            BigDecimal rtMinAmount = diff.multiply(ratio);

            BigDecimal[] unitAmounts = NumberUtils.computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), BaseEntity.NB_DECIMALS,
                RoundingMode.HALF_UP);
            BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), appProvider.getRounding(),
                appProvider.getRoundingMode().getRoundingMode());

            RatedTransaction ratedTransaction = new RatedTransaction(minRatingDate, unitAmounts[0], unitAmounts[1], unitAmounts[2], BigDecimal.ONE, amounts[0], amounts[1],
                amounts[2], RatedTransactionStatusEnum.OPEN, null, billingAccount, null, invoiceSubCategory, null, null, null, null, null, null, null, null, null, "NO_OFFER", null,
                RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_BA.getCode() + "_" + billingAccount.getCode(), minAmountLabel, null, null, seller, tax, tax.getPercent());

            minAmountTransactions.add(ratedTransaction);

            // Update the total/ba level amounts
            baLeveltotalAmounts.addAmounts(amounts[0], amounts[1]);
            baLevelAmounts.get(amountsKey).addAmounts(amounts[0], amounts[1]);

            totalRatio = totalRatio.add(ratio);
        }

    }

    /**
     * Summed rated transaction amounts grouped by invoice subcategory for a given list of charge instances
     * 
     * @param chargeInstances Charge instances
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, invoice subcategory id
     */
    @SuppressWarnings("unchecked")
    private List<Object[]> computeInvoiceableAmountForService(List<ChargeInstance> chargeInstances, Date firstTransactionDate, Date lastTransactionDate) {
        Query q = getEntityManager().createNamedQuery("RatedTransaction.sumByCharge").setParameter("chargeInstances", chargeInstances)
            .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
        return q.getResultList();
    }

    /**
     * Summed rated transaction amounts grouped by invoice subcategory for a given subscription
     * 
     * @param subscription Subscription
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, invoice subcategory id
     */
    @SuppressWarnings("unchecked")
    private List<Object[]> computeInvoiceableAmountForSubscription(Subscription subscription, Date firstTransactionDate, Date lastTransactionDate) {
        Query q = getEntityManager().createNamedQuery("RatedTransaction.sumBySubscription").setParameter("subscription", subscription)
            .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
        return q.getResultList();
    }

    /**
     * Summed rated transaction amounts grouped by invoice subcategory for a given billing account. ONLY those that are not tied to subscription. e.g. product purchases by User
     * account instead of subscribed products.
     * 
     * @param billingAccount Billing account
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, invoice subcategory id
     */
    @SuppressWarnings("unchecked")
    private List<Object[]> computeInvoiceableAmountForBANotSubscriptionRelated(BillingAccount billingAccount, Date firstTransactionDate, Date lastTransactionDate) {
        Query q = getEntityManager().createNamedQuery("RatedTransaction.sumByBillingAccountNoSubscription").setParameter("billingAccount", billingAccount)
            .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
        return q.getResultList();
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