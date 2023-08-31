/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.service.accountingscheme;

import static java.util.Collections.emptyMap;
import static java.util.Optional.ofNullable;
import static org.meveo.service.securityDeposit.impl.FinanceSettingsService.AUXILIARY_ACCOUNT_CODE;
import static org.meveo.service.securityDeposit.impl.FinanceSettingsService.AUXILIARY_ACCOUNT_LABEL;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.accountingScheme.JournalEntry;
import org.meveo.model.accountingScheme.JournalEntryDirectionEnum;
import org.meveo.model.admin.Seller;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanTypeEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AccountOperationStatus;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.Journal;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.securityDeposit.AuxiliaryAccounting;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Stateless
public class JournalEntryService extends PersistenceService<JournalEntry> {

    private static final String PARAM_ID_INV = "ID_INV";
    private static final String REVENUE_MANDATORY_ACCOUNTING_CODE_NOT_FOUND = "Not possible to generate journal entries for this invoice," +
            " make sure that all related accounting articles have an accounting code or that the default revenue accounting code" +
            " is set in the account operation type (contra accounting code)";

    private static final String TAX_MANDATORY_ACCOUNTING_CODE_NOT_FOUND = "Not possible to generate journal entries for this invoice," +
            " make sure that all related taxes have an accounting code or that the default tax accounting code" +
            " is set in the account operation type (contra accounting code 2)";
    public static final String NAMED_QUERY_JOURNAL_ENTRY_CHECK_EXISTENCE_WITH_ACCOUNTING_CODE = "JournalEntry.checkExistenceWithAccountingCode";
    public static final String PARAM_ID_AO = "ID_AO";
    public static final String PARAM_ID_ACCOUNTING_CODE = "ID_ACCOUNTING_CODE";
    public static final String PARAM_DIRECTION = "DIRECTION";
    public static final String GET_BY_ACCOUNT_OPERATION_AND_DIRECTION_QUERY = "JournalEntry.getByAccountOperationAndDirection";

    @Inject
    private ProviderService providerService;

    @Inject
    private AccountingArticleService accountingArticleService;

    @Inject
    private FinanceSettingsService financeSettingsService;

    @Transactional
    public List<JournalEntry> createFromAccountOperation(AccountOperation ao, OCCTemplate occT) {
        // INTRD-4702
        // First JournalEntry
        AccountingCode accountingCode = ofNullable(fromCustomerAccount(ao.getCustomerAccount()))
                .orElse(occT.getAccountingCode());
        JournalEntry firstEntry = buildJournalEntry(ao, accountingCode, occT.getOccCategory(),
                ao.getAmount() == null ? BigDecimal.ZERO : ao.getAmount(),
                null, ao.getOperationNumber());

        // Second JournalEntry
        JournalEntry secondEntry = buildJournalEntry(ao, occT.getContraAccountingCode(),
                //if occCategory == DEBIT then direction= CREDIT and vice versa
                occT.getOccCategory() == OperationCategoryEnum.DEBIT ?
                        OperationCategoryEnum.CREDIT : OperationCategoryEnum.DEBIT,
                ao.getAmount() == null ? BigDecimal.ZERO : ao.getAmount(),
                null, ao.getOperationNumber());

        create(firstEntry);
        create(secondEntry);

        return Arrays.asList(firstEntry, secondEntry);

    }

    private AccountingCode fromCustomerAccount(CustomerAccount customerAccount) {
        return ofNullable(customerAccount)
                .map(CustomerAccount::getGeneralClientAccount)
                .orElse(null);
    }

    @Transactional
    public List<JournalEntry> createFromInvoice(RecordedInvoice recordedInvoice, OCCTemplate occT) {
        List<JournalEntry> saved = new ArrayList<>();

        AccountingCode accountingCodeFromRecordedAoOrOcc =
                recordedInvoice.getCustomerAccount().getCustomer().getCustomerCategory().getAccountingCode() != null ?
                recordedInvoice.getCustomerAccount().getCustomer().getCustomerCategory().getAccountingCode() :
                occT.getAccountingCode();
        AccountingCode accountingCode = ofNullable(fromCustomerAccount(recordedInvoice.getCustomerAccount()))
                .orElse(accountingCodeFromRecordedAoOrOcc);
        // 1- produce a Customer account entry line
        JournalEntry customerAccountEntry = buildJournalEntry(recordedInvoice, accountingCode, occT.getOccCategory(),
                recordedInvoice.getAmount() == null ? BigDecimal.ZERO : recordedInvoice.getAmount(), null, recordedInvoice.getOperationNumber());

        saved.add(customerAccountEntry);

        log.info("Customer account entry successfully created for AO={}", recordedInvoice.getId());

        // 2- produce the revenue accounting entries
        buildRevenuesJournalEntries(recordedInvoice, occT, saved);

        // 3- produce the taxes accounting entries
        buildTaxesJournalEntries(recordedInvoice, occT, saved);

        // Persist all
        saved.forEach(this::create);

        log.info("{} JournalEntries created for AO={}", saved.size(), recordedInvoice.getId());

        return saved;
    }

    @Transactional
    public List<JournalEntry> createFromPayment(Payment ao, OCCTemplate occT) {
        // INTRD-5613
        List<JournalEntry> saved = new ArrayList<>();

        boolean isOrphan = (Long) getEntityManager().createNamedQuery(NAMED_QUERY_JOURNAL_ENTRY_CHECK_EXISTENCE_WITH_ACCOUNTING_CODE)
                .setParameter(PARAM_ID_AO, ao.getId())
                .setParameter(PARAM_ID_ACCOUNTING_CODE, occT.getContraAccountingCode2().getId())
                .getSingleResult() > 0;

        AccountingCode firstAccountingCode = null;
        OperationCategoryEnum firstCategory = OperationCategoryEnum.CREDIT;

        AccountingCode secondAccountingCode = null;
        OperationCategoryEnum secondCategory = OperationCategoryEnum.DEBIT;

        if (ao.getCustomerAccount() == null && !isOrphan) {
            firstCategory = OperationCategoryEnum.DEBIT;
            firstAccountingCode = occT.getContraAccountingCode();

            secondCategory = OperationCategoryEnum.CREDIT;
            secondAccountingCode = occT.getContraAccountingCode2();

        } else if (ao.getCustomerAccount() != null && isOrphan) {
            firstAccountingCode = ofNullable(fromCustomerAccount(ao.getCustomerAccount()))
                    .orElse(occT.getAccountingCode());
            secondAccountingCode = occT.getContraAccountingCode2();

        } else if (ao.getCustomerAccount() != null && !isOrphan) {
            firstAccountingCode = ofNullable(fromCustomerAccount(ao.getCustomerAccount()))
                    .orElse(occT.getAccountingCode());
            secondAccountingCode = occT.getContraAccountingCode();

        } else if (ao.getCustomerAccount() == null && isOrphan) {
            throw new BusinessException("Not managed case : customerAccount cannot be null, it have already JournalEntries creation (isOrpahn = true)");
        }

        // 1- produce a first accounting entry
        JournalEntry firstAccountingEntry = buildJournalEntry(ao, firstAccountingCode, firstCategory,
                ao.getAmount() == null ? BigDecimal.ZERO : ao.getAmount(), null, ao.getOperationNumber());

        saved.add(firstAccountingEntry);

        log.info("First accounting entry successfully created for AO={} [category={}, accountingCode={}]",
                ao.getId(), firstCategory, firstAccountingCode);

        // 2- produce the second accounting entry : difference with first on (accountingCode and occtCategory)
        JournalEntry secondAccountingEntry = buildJournalEntry(ao, secondAccountingCode, secondCategory,
                ao.getAmount() == null ? BigDecimal.ZERO : ao.getAmount(), null, ao.getOperationNumber());

        saved.add(secondAccountingEntry);

        log.info("Second accounting entry successfully created for AO={} [category={}, accountingCode={}]",
                ao.getId(), secondCategory, secondAccountingCode);

        // Persist all
        saved.forEach(this::create);

        log.info("{} Payments JournalEntries created for AO={}", saved.size(), ao.getId());

        return saved;
    }

    public void validateAOForInvoiceScheme(AccountOperation ao) {
        if (ao == null) {
            log.warn("No AccountOperation passed as CONTEXT_ENTITY");
            throw new BusinessException("No AccountOperation passed as CONTEXT_ENTITY");
        }

        if (!(ao instanceof RecordedInvoice)) {
            log.warn("AccountOperation with id={} is not RecordedInvoice type", ao.getId());
            throw new BusinessException("AccountOperation with id=" + ao.getId() + " is not RecordedInvoice type");
        }
    }

    /**
     * Check OCCTemplate fields
     *
     * @param ao             account operation
     * @param occT           occt.code = ao.code
     * @param isDefaultCheck for Default Script we must check accountingCode and contraAccountingCode,
     *                       for Invoice one for example, we must on check accountingCode
     */
    public void validateOccTForAccountingScheme(AccountOperation ao, OCCTemplate occT, boolean isDefaultCheck, boolean isPaymentCheck) {
        if (occT == null) {
            log.warn("No OCCTemplate found for AccountOperation [id={}]", ao.getId());
            throw new BusinessException("No OCCTemplate found for AccountOperation id=" + ao.getId());
        }
        if (occT.getAccountingCode() == null) {
            log.warn("AccountOperation with id={} Mandatory AccountingCode not found for OCCTemplate id={}", ao.getId(), occT.getId());
            throw new BusinessException("AccountOperation with id=" + ao.getId() + " : Mandatory AccountingCode not found for OCCTemplate id=" + occT.getId());
        }
        if ((isDefaultCheck || isPaymentCheck) && occT.getContraAccountingCode() == null) {
            log.warn("AccountOperation with id={} : Mandatory ContraAccountingCode not found for OCCTemplate id={}", ao.getId(), occT.getId());
            throw new BusinessException("AccountOperation with id=" + ao.getId() + " : Mandatory ContraAccountingCode not found for OCCTemplate id=" + occT.getId());
        }        
        if (isPaymentCheck && occT.getContraAccountingCode2() == null) {
            log.warn("AccountOperation with id={} : Mandatory ContraAccountingCode2 not found for OCCTemplate id={}", ao.getId(), occT.getId());
            throw new BusinessException("AccountOperation with id=" + ao.getId() + " : Mandatory ContraAccountingCode2 not found for OCCTemplate id=" + occT.getId());
        }
    }

    private JournalEntry buildJournalEntry(AccountOperation ao, AccountingCode code, OperationCategoryEnum categoryEnum, BigDecimal amount, Tax tax, Long operationNumber) {
        JournalEntry firstEntry = new JournalEntry();
        firstEntry.setAccountOperation(ao);
        firstEntry.setAccountingCode(code);
        firstEntry.setAmount(amount);
        CustomerAccount customerAccount = ao.getCustomerAccount();
        firstEntry.setCustomerAccount(customerAccount);
        firstEntry.setDirection(JournalEntryDirectionEnum.getValue(categoryEnum.getId()));
        firstEntry.setTax(tax);

        Seller seller = getSeller(ao);
        firstEntry.setSeller(seller);
        firstEntry.setOperationNumber(operationNumber);
        firstEntry.setSellerCode(seller != null ? seller.getCode() : "");
        firstEntry.setClientUniqueId(ao.getCustomerAccount() != null ? ao.getCustomerAccount().getRegistrationNo() : "");

        Provider provider = providerService.getProvider();
        firstEntry.setCurrency(provider.getCurrency() != null ? provider.getCurrency().getCurrencyCode() : "");
        
        firstEntry.setTradingCurrency((ao.getTransactionalCurrency() != null) ? ao.getTransactionalCurrency().getCurrencyCode() : null);
		firstEntry.setTransactionalAmount((ao.getTransactionalAmount() != null) ? ao.getTransactionalAmount() : ((ao.getAppliedRate() != null) ? amount.multiply(ao.getAppliedRate()) : null));

        if (ao instanceof RecordedInvoice) {
            Invoice invoice = ((RecordedInvoice) ao).getInvoice();
			firstEntry.setSupportingDocumentRef(invoice);
            firstEntry.setSupportingDocumentType(invoice != null && invoice.getInvoiceType() != null ? invoice.getInvoiceType().getCode() : null);
            
			if (firstEntry.getTradingCurrency() == null) {
				firstEntry.setTradingCurrency(invoice != null && invoice.getTradingCurrency() != null ? invoice.getTradingCurrency().getCurrencyCode() : null);
			}

			if (firstEntry.getTransactionalAmount() == null) {
				firstEntry.setTransactionalAmount(invoice != null ? invoice.getTransactionalAmountWithTax() : null);
			}

        }
        Map<String, String> accountingInfo = addAccountingInfo(customerAccount);
        if (accountingInfo != null && !accountingInfo.isEmpty()) {
            firstEntry.setAuxiliaryAccountCode(accountingInfo.get(AUXILIARY_ACCOUNT_CODE));
            firstEntry.setAuxiliaryAccountLabel(accountingInfo.get(AUXILIARY_ACCOUNT_LABEL));
        }

        if (ao != null) {
        	firstEntry.setJournalCode(ofNullable(ao.getJournal()).map(Journal::getCode).orElse(null));
        	firstEntry.setReference(ao.getReference());
        	firstEntry.setDocumentType(ao.getType());
        }
        
        if (code != null) {
        	firstEntry.setCategory(code.getChartOfAccountTypeEnum());
        	firstEntry.setAccount(code.getCode());
        	firstEntry.setLabel(code.getDescription());
        }
        
        if (customerAccount != null) {
        	firstEntry.setCustomerCode(customerAccount.getCode());
        	firstEntry.setCustomerName(customerAccount.getDescription());
        }
        
        firstEntry.setSellerName(ofNullable(seller).map(Seller::getDescription).orElse(null));

        return firstEntry;
    }

    private Seller getSeller(AccountOperation ao) {
        Seller customerSeller = ao.getCustomerAccount() != null && ao.getCustomerAccount().getCustomer() != null
                ? ao.getCustomerAccount().getCustomer().getSeller() : null;
        return ao.getSeller() != null ? ao.getSeller() : customerSeller;

    }

    private Map<String, String> addAccountingInfo(CustomerAccount customerAccount) {
        FinanceSettings financeSettings = financeSettingsService.getFinanceSetting();
        if(financeSettings != null) {
            AuxiliaryAccounting auxiliaryAccounting = financeSettings.getAuxiliaryAccounting();
            if(auxiliaryAccounting != null && auxiliaryAccounting.isUseAuxiliaryAccounting()) {
                try {
                    return financeSettingsService.generateAuxiliaryAccountInfo(customerAccount, auxiliaryAccounting);
                } catch (Exception exception) {
                    log.error(exception.getMessage());
                }
            }
        }
        return emptyMap();
    }

    private void buildTaxesJournalEntries(RecordedInvoice recordedInvoice, OCCTemplate occT, List<JournalEntry> saved) {
        Query queryTax = getEntityManager().createQuery(
                        "SELECT taxAg" + // amountTax
                                " FROM TaxInvoiceAgregate taxAg LEFT JOIN AccountingCode ac ON taxAg.accountingCode = ac" +
                                " WHERE taxAg.invoice.id = :" + PARAM_ID_INV)
                .setParameter(PARAM_ID_INV, recordedInvoice.getInvoice().getId());

        List<TaxInvoiceAgregate> taxResult = queryTax.getResultList();

        if (taxResult != null && !taxResult.isEmpty()) {
            log.info("Start creating taxes accounting entries for AO={} | INV_ID={} : {} invoice line to process",
                    recordedInvoice.getId(), recordedInvoice.getInvoice().getId(), taxResult.size());

            // INTRD-6292 : if the acounting code related to an article is null then use occT.contraAccountingCode before grouping,
            // otherwise the default accounting code should be assigned before grouping
            Map<String, JournalEntry> accountingCodeJournal = new HashMap<>();
            taxResult.forEach(taxAgr -> {
                AccountingCode taxACC = taxAgr.getAccountingCode() != null ? taxAgr.getAccountingCode() : occT.getContraAccountingCode2();
                if (taxACC == null) {
                    throw new BusinessException("AccountOperation with id=" + recordedInvoice.getId() + " : " +
                            TAX_MANDATORY_ACCOUNTING_CODE_NOT_FOUND);
                }

                String groupKey = taxACC.getCode() + (taxAgr.getTax() == null ? "" : taxAgr.getTax().getCode());
                BigDecimal amoutTax = taxAgr.getAmountTax() == null ? BigDecimal.ZERO : taxAgr.getAmountTax();
                BigDecimal transactionAmoutTax = taxAgr.getTransactionalAmountTax() == null ? BigDecimal.ZERO : taxAgr.getTransactionalAmountTax();

                if (accountingCodeJournal.get(groupKey) == null) {
                    JournalEntry taxEntry = buildJournalEntry(recordedInvoice, taxACC,
                            occT.getOccCategory() == OperationCategoryEnum.DEBIT ? OperationCategoryEnum.CREDIT : OperationCategoryEnum.DEBIT,
                            amoutTax,
                            taxAgr.getTax(), recordedInvoice.getOperationNumber());
                    taxEntry.setTransactionalAmount(transactionAmoutTax);
                    accountingCodeJournal.put(groupKey, taxEntry);
                } else {
                    JournalEntry entry = accountingCodeJournal.get(groupKey);
                    entry.setAmount(entry.getAmount().add(amoutTax));
                    entry.setTransactionalAmount(entry.getTransactionalAmount().add(transactionAmoutTax));
                }
            });

            saved.addAll(accountingCodeJournal.values());

        } else {
            log.info("No taxes accounting entries to create for AO={} | INV_ID={}",
                    recordedInvoice.getId(), recordedInvoice.getInvoice().getId());
        }
    }

    private void buildRevenuesJournalEntries(RecordedInvoice recordedInvoice, OCCTemplate occT, List<JournalEntry> saved) {
        Query query = getEntityManager().createQuery(
                        "SELECT ivl" + // amountWithoutTax
                                " FROM InvoiceLine ivL LEFT JOIN AccountingCode ac ON ivL.accountingArticle.accountingCode = ac" +
                                " WHERE ivL.invoice.id = :" + PARAM_ID_INV)
                .setParameter(PARAM_ID_INV, recordedInvoice.getInvoice().getId());

        List<InvoiceLine> ivlResults = query.getResultList();

        if (ivlResults != null && !ivlResults.isEmpty()) {
            log.info("Start creating revenue accounting entries for AO={} | INV_ID={} : {} invoice line to process",
                    recordedInvoice.getId(), recordedInvoice.getInvoice().getId(), ivlResults.size());

            // INTRD-6292 : if the acounting code related to an article is null then use occT.contraAccountingCode before grouping,
            // otherwise the default accounting code should be assigned before grouping
            Map<String, JournalEntry> accountingCodeJournal = new HashMap<>();
            ivlResults.forEach(invoiceLine -> {
                // find default accounting code
                AccountingCode revenuACC = accountingArticleService.getArticleAccountingCode(invoiceLine.getInvoice(), invoiceLine.getAccountingArticle());

                if (revenuACC == null &&  occT != null) {
                    revenuACC = occT.getContraAccountingCode();
                    if (revenuACC == null) {
                        throw new BusinessException("AccountOperation with id=" + recordedInvoice.getId() + " : " +
                                REVENUE_MANDATORY_ACCOUNTING_CODE_NOT_FOUND);
                    }
                }

                String groupKey = revenuACC.getCode() +
                        (invoiceLine.getAccountingArticle().getAnalyticCode1() == null ? "" : invoiceLine.getAccountingArticle().getAnalyticCode1())
                        + (invoiceLine.getAccountingArticle().getAnalyticCode2() == null ? "" : invoiceLine.getAccountingArticle().getAnalyticCode2())
                        + (invoiceLine.getAccountingArticle().getAnalyticCode3() == null ? "" : invoiceLine.getAccountingArticle().getAnalyticCode3());

                if (accountingCodeJournal.get(groupKey) == null) {
                    accountingCodeJournal.put(groupKey,
                            buildJournalEntryForRevenu(recordedInvoice, revenuACC, occT, invoiceLine));
                } else {
                    JournalEntry entry = accountingCodeJournal.get(groupKey);
                    entry.setAmount(entry.getAmount().add(invoiceLine.getAmountWithoutTax()));
                    entry.setTransactionalAmount(entry.getTransactionalAmount().add(invoiceLine.getTransactionalAmountWithoutTax()));
                }

            });

            if (recordedInvoice.getInvoice().getDiscountPlan() != null &&
                    recordedInvoice.getInvoice().getDiscountPlan().getDiscountPlanType() == DiscountPlanTypeEnum.INVOICE) {

                AccountingArticle accountingArticle = null;
                if (recordedInvoice.getInvoice().getDiscountPlan().getDiscountPlanItems() != null &&
                        !recordedInvoice.getInvoice().getDiscountPlan().getDiscountPlanItems().isEmpty()) {
                    DiscountPlanItem discountPlanItem = recordedInvoice.getInvoice().getDiscountPlan().getDiscountPlanItems().get(0);
                    accountingArticle = discountPlanItem.getAccountingArticle();
                }

                // find default accounting code
                AccountingCode revenuACC = accountingArticleService.getArticleAccountingCode(recordedInvoice.getInvoice(), accountingArticle);

                if (revenuACC == null && occT != null) {
                    revenuACC = occT.getContraAccountingCode();
                    if (revenuACC == null) {
                        throw new BusinessException("AccountOperation with id=" + recordedInvoice.getId() + " : " +
                                REVENUE_MANDATORY_ACCOUNTING_CODE_NOT_FOUND);
                    }
                }

                JournalEntry revenuEntry = buildJournalEntry(recordedInvoice, revenuACC, OperationCategoryEnum.CREDIT,
                        recordedInvoice.getInvoice().getDiscountAmount() == null ? BigDecimal.ZERO : recordedInvoice.getInvoice().getDiscountAmount().negate(),
                        null, recordedInvoice.getOperationNumber());
                if (accountingArticle != null) {
                    revenuEntry.setAnalyticCode1(accountingArticle.getAnalyticCode1());
                    revenuEntry.setAnalyticCode2(accountingArticle.getAnalyticCode2());
                    revenuEntry.setAnalyticCode3(accountingArticle.getAnalyticCode3());
                }
                revenuEntry.setTransactionalAmount(recordedInvoice.getInvoice().getDiscountAmount().negate());
                saved.add(revenuEntry);
            }

            saved.addAll(accountingCodeJournal.values());

        } else {
            log.info("No revenue accounting entries to create for AO={} | INV_ID={}",
                    recordedInvoice.getId(), recordedInvoice.getInvoice().getId());
        }
    }

    private JournalEntry buildJournalEntryForRevenu(RecordedInvoice recordedInvoice, AccountingCode revenuACC,
                                                    OCCTemplate occT, InvoiceLine invoiceLine) {
        JournalEntry revenuEntry = buildJournalEntry(recordedInvoice, revenuACC,
                occT.getOccCategory() == OperationCategoryEnum.DEBIT ? OperationCategoryEnum.CREDIT : OperationCategoryEnum.DEBIT,
                invoiceLine.getAmountWithoutTax() == null ? BigDecimal.ZERO : invoiceLine.getAmountWithoutTax(),
                null, recordedInvoice.getOperationNumber());
        revenuEntry.setAnalyticCode1(invoiceLine.getAccountingArticle().getAnalyticCode1());
        revenuEntry.setAnalyticCode2(invoiceLine.getAccountingArticle().getAnalyticCode2());
        revenuEntry.setAnalyticCode3(invoiceLine.getAccountingArticle().getAnalyticCode3());
        revenuEntry.setTransactionalAmount(invoiceLine.getTransactionalAmountWithoutTax());
        return revenuEntry;
    }

    @Override
    public void create(JournalEntry journalEntry) {
        super.create(journalEntry);
        if(checkAuxiliaryCodeUniqniess(journalEntry.getAuxiliaryAccountCode(), journalEntry.getCustomerAccount()) != 0) {
            journalEntry.setAuxiliaryAccountCode(journalEntry.getAuxiliaryAccountCode()
                    + journalEntry.getCustomerAccount().getId());
        }
    }

    /**
     * Check auxiliary account code uniqniess
     * @param auxiliaryAccountCode auxiliary account code
     * @param customerAccount      customer account
     * @return number of occurrence
     */
    public long checkAuxiliaryCodeUniqniess(String auxiliaryAccountCode, CustomerAccount customerAccount) {
        return (long) getEntityManager().createNamedQuery("JournalEntry.checkAuxiliaryCodeUniqniess")
                                .setParameter("auxiliaryAccountCode", auxiliaryAccountCode)
                                .setParameter("customerAccount", customerAccount)
                                .getSingleResult();
    }

    public void assignMatchingCodeToJournalEntries(AccountOperation ao, List<JournalEntry> createdEntries) {
        if (!(ao instanceof RecordedInvoice || ao instanceof Payment)) {
            log.warn("AccountOperation id={}-type={} is not managed by Assigning matching code processing, Expected type : RecordedInvoice and Payment.", ao.getId(), ao.getType());
            return;
        }

        if (ao.getStatus() != AccountOperationStatus.EXPORTED || ao.getMatchingStatus() != MatchingStatusEnum.L) {
            log.warn("RecordedInvoice id={} does not have the expected status to assign it a 'Matching Code' for its JournalEntry [given={}-{}, expected={}-{}]",
                    ao.getId(), ao.getStatus(), ao.getMatchingStatus(), AccountOperationStatus.EXPORTED, MatchingStatusEnum.L);
            return;
        }

        // 1- Build map of AOS
        //  1.1- First of all, get RecordedInvoice AOs with it JournalEntries
        //  1.2- Get all Payment AOS, using RecordedInoice.matchingAmount.matchingCode relationship, with it JournalEntries
        //  1.3- Add content of param createdEntries (this param is used before flushing transaction, it group all created JE with current AOJoob executed instance)

        final List<JournalEntry> aoJEs = new ArrayList<>();
        Map<Long, Integer> aoIdWithTransactionCategory = new HashMap<>();
        Set<Long> processedMatchingAmounts = new HashSet<>();
        AtomicBoolean isValidAo = new AtomicBoolean(true);

        lookupMatchedAO(ao.getMatchingAmounts(), aoIdWithTransactionCategory, processedMatchingAmounts, isValidAo);

        if (isValidAo.get()) {
            aoIdWithTransactionCategory.forEach((aoId, transactionCategory) -> aoJEs.addAll(getEntityManager().createNamedQuery(GET_BY_ACCOUNT_OPERATION_AND_DIRECTION_QUERY)
                    .setParameter(PARAM_ID_AO, aoId)
                    .setParameter(PARAM_DIRECTION, JournalEntryDirectionEnum.getValue(transactionCategory))
                    .getResultList()));

            // add passed journalEntries
            aoJEs.addAll(getJournalEntries(ao, createdEntries));

            if (CollectionUtils.isNotEmpty(aoJEs) && aoJEs.size() >= aoIdWithTransactionCategory.size()
                    && CollectionUtils.isEmpty(aoJEs.stream().filter(journalEntry -> StringUtils.isNotBlank(journalEntry.getMatchingCode())).collect(Collectors.toList()))) {
                String matchingCode = providerService.getNextMatchingCode();
                aoJEs.forEach(je -> {
                            if (StringUtils.isBlank(je.getMatchingCode())) {
                                // Peut on si on lance un exception ca sera plus claire en cas de matchingCode deja existant...a valider = ce cas ne doit pas etre possible : des JE avec code et d'autre sans liés au meme group de matching (Recorded invoice et Payment)
                                je.setMatchingCode(matchingCode);
                                update(je);
                            }
                        }
                );
            }
        }

    }

    private List<JournalEntry> getJournalEntries(AccountOperation ao, List<JournalEntry> createdEntries) {
        if (CollectionUtils.isEmpty(createdEntries)) {
            return Collections.emptyList();
        }
        return createdEntries.stream()
                .filter(journalEntry -> journalEntry.getDirection() == JournalEntryDirectionEnum.getValue(ao.getTransactionCategory().getId()))
                .collect(Collectors.toList());
    }

    private void lookupMatchedAO(List<MatchingAmount> matchingAmounts, Map<Long, Integer> aoIdWithTransactionCategory, Set<Long> processedMatchingAmounts, AtomicBoolean isValidAo) {
        Optional.ofNullable(matchingAmounts).orElse(Collections.emptyList())
                .forEach(matchingAmount -> {
                            if (processedMatchingAmounts.contains(matchingAmount.getId())) {
                                return;
                            }
                            // get nested matching amount
                            Optional.ofNullable(matchingAmount.getMatchingCode().getMatchingAmounts()).orElse(Collections.emptyList())
                                    .forEach(ma -> {
                                        processedMatchingAmounts.add(ma.getId());
                                        AccountOperation aoFromMatching = ma.getAccountOperation();
                                        if (aoFromMatching.getStatus() != AccountOperationStatus.EXPORTED || aoFromMatching.getMatchingStatus() != MatchingStatusEnum.L) {
                                            log.warn("AccountOperation id={}-type={} does not have the expected status to assign it a 'Matching Code' for its JournalEntry [given={}-{}, expected={}-{}]",
                                                    aoFromMatching.getId(), aoFromMatching.getType(), aoFromMatching.getStatus(), aoFromMatching.getMatchingStatus(), AccountOperationStatus.EXPORTED, MatchingStatusEnum.L);
                                            isValidAo.set(false);
                                            return; // skip process if related Recorded invoice AO (payment in our case) does not have a JournalEntry (export status still in POSTED or FAILED)
                                        }
                                        aoIdWithTransactionCategory.put(aoFromMatching.getId(), aoFromMatching.getTransactionCategory().getId());

                                        lookupMatchedAO(aoFromMatching.getMatchingAmounts(), aoIdWithTransactionCategory, processedMatchingAmounts, isValidAo);
                                    });
                        }
                );
    }

}