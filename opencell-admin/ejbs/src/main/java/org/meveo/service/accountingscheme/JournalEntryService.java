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

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.accountingScheme.JournalEntry;
import org.meveo.model.accountingScheme.JournalEntryDirectionEnum;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.cpq.commercial.InvoiceLine;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Stateless
public class JournalEntryService extends PersistenceService<JournalEntry> {

    private static final String PARAM_ID_INV = "ID_INV";

    @Transactional
    public List<JournalEntry> createFromAccountOperation(AccountOperation ao, OCCTemplate occT) {
        // INTRD-4702
        // First JournalEntry
        JournalEntry firstEntry = buildJournalEntry(ao, occT.getAccountingCode(), occT.getOccCategory(),
                ao.getAmount() == null ? BigDecimal.ZERO : ao.getAmount(),
                null);

        // Second JournalEntry
        JournalEntry secondEntry = buildJournalEntry(ao, occT.getContraAccountingCode(),
                //if occCategory == DEBIT then direction= CREDIT and vice versa
                occT.getOccCategory() == OperationCategoryEnum.DEBIT ?
                        OperationCategoryEnum.CREDIT : OperationCategoryEnum.DEBIT,
                ao.getAmount() == null ? BigDecimal.ZERO : ao.getAmount(),
                null);

        create(firstEntry);
        create(secondEntry);

        return Arrays.asList(firstEntry, secondEntry);

    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<JournalEntry> createFromInvoice(RecordedInvoice recordedInvoice, OCCTemplate occT) {
        List<JournalEntry> saved = new ArrayList<>();

        // 1- produce a Customer account entry line
        JournalEntry customerAccountEntry = buildJournalEntry(recordedInvoice,
                recordedInvoice.getCustomerAccount().getCustomer().getCustomerCategory().getAccountingCode() != null ?
                        recordedInvoice.getCustomerAccount().getCustomer().getCustomerCategory().getAccountingCode() :
                        occT.getAccountingCode(),
                occT.getOccCategory(),
                recordedInvoice.getAmount() == null ? BigDecimal.ZERO : recordedInvoice.getAmount(),
                null);

        saved.add(customerAccountEntry);

        log.info("Customer account entry successfully created for AO={}", recordedInvoice.getId());

        // 2- produce the revenue accounting entries
        Query query = getEntityManager().createQuery(
                        "SELECT sum(ivL.amountWithoutTax), ivl" +
                                " FROM InvoiceLine ivL LEFT JOIN AccountingCode ac ON ivL.accountingArticle.accountingCode = ac" +
                                " WHERE ivL.invoice.id = :" + PARAM_ID_INV +
                                " GROUP BY ivl, ac.code," +
                                " ivl.accountingArticle.analyticCode1, ivl.accountingArticle.analyticCode2, ivl.accountingArticle.analyticCode3")
                .setParameter(PARAM_ID_INV, recordedInvoice.getInvoice().getId());

        List<Object[]> revenuResult = query.getResultList();

        if (revenuResult != null && !revenuResult.isEmpty()) {
            log.info("Start creating revenue accounting entries for AO={} | INV_ID={} : {} invoice line to process",
                    recordedInvoice.getId(), recordedInvoice.getInvoice().getId(), revenuResult.size());

            revenuResult.forEach(objects -> {
                InvoiceLine invoiceLine = (InvoiceLine) objects[1];

                JournalEntry revenuEntry = buildJournalEntry(recordedInvoice,
                        invoiceLine.getAccountingArticle().getAccountingCode() != null ? invoiceLine.getAccountingArticle().getAccountingCode() : recordedInvoice.getAccountingCode(),
                        occT.getOccCategory() == OperationCategoryEnum.DEBIT ? OperationCategoryEnum.CREDIT : OperationCategoryEnum.DEBIT,
                        objects[0] == null ? BigDecimal.ZERO : (BigDecimal) objects[0],
                        null);
                revenuEntry.setAnalyticCode1(invoiceLine.getAccountingArticle().getAnalyticCode1());
                revenuEntry.setAnalyticCode2(invoiceLine.getAccountingArticle().getAnalyticCode2());
                revenuEntry.setAnalyticCode3(invoiceLine.getAccountingArticle().getAnalyticCode3());

                saved.add(revenuEntry);

            });
        } else {
            log.info("No revenue accounting entries to create for AO={} | INV_ID={}",
                    recordedInvoice.getId(), recordedInvoice.getInvoice().getId());
        }

        // 3- produce the taxes accounting entries
        Query queryTax = getEntityManager().createQuery(
                        "SELECT sum(taxAg.amountTax), taxAg" +
                                " FROM TaxInvoiceAgregate taxAg LEFT JOIN AccountingCode ac ON taxAg.accountingCode = ac" +
                                " WHERE taxAg.invoice.id = :" + PARAM_ID_INV +
                                " GROUP BY taxAg, ac.code, taxAg.tax.code")
                .setParameter(PARAM_ID_INV, recordedInvoice.getInvoice().getId());

        List<Object[]> taxResult = queryTax.getResultList();

        if (taxResult != null && !taxResult.isEmpty()) {
            log.info("Start creating taxes accounting entries for AO={} | INV_ID={} : {} invoice line to process",
                    recordedInvoice.getId(), recordedInvoice.getInvoice().getId(), taxResult.size());

            taxResult.forEach(objects -> {
                TaxInvoiceAgregate taxAgr = (TaxInvoiceAgregate) objects[1];

                JournalEntry taxEntry = buildJournalEntry(recordedInvoice,
                        taxAgr.getAccountingCode() != null ? taxAgr.getAccountingCode() : recordedInvoice.getAccountingCode(),
                        occT.getOccCategory() == OperationCategoryEnum.DEBIT ? OperationCategoryEnum.CREDIT : OperationCategoryEnum.DEBIT,
                        objects[0] == null ? BigDecimal.ZERO : (BigDecimal) objects[0],
                        taxAgr.getTax());

                saved.add(taxEntry);

            });
        } else {
            log.info("No taxes accounting entries to create for AO={} | INV_ID={}",
                    recordedInvoice.getId(), recordedInvoice.getInvoice().getId());
        }

        // Persist all
        saved.forEach(this::create);

        log.info("{} JournalEntries created for AO={}", saved.size(), recordedInvoice.getId());

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
     * @param isDefaultCheck for Default Script we must check accountinfCode and contraAccountingCode,
     *                       for Invoice one for exemple, we must on check accountinfode
     */
    public void validateOccTForAccountingScheme(AccountOperation ao, OCCTemplate occT, boolean isDefaultCheck) {
        if (occT == null) {
            log.warn("No OCCTemplate found for AccountOperation [id={}]", ao.getId());
            throw new BusinessException("No OCCTemplate found for AccountOperation id=" + ao.getId());
        }

        if (occT.getAccountingCode() == null) {
            log.warn("Mandatory AccountingCode not found for OCCTemplate id={}", occT.getId());
            throw new BusinessException("Mandatory AccountingCode not found for OCCTemplate id=" + occT.getId());
        }

        if (isDefaultCheck && occT.getContraAccountingCode() == null) {
            log.warn("Mandatory ContraAccountingCode not found for OCCTemplate id={}", occT.getId());
            throw new BusinessException("Mandatory AccountingCode not found for OCCTemplate id=" + occT.getId());
        }

    }

    private JournalEntry buildJournalEntry(AccountOperation ao, AccountingCode code,
                                           OperationCategoryEnum categoryEnum, BigDecimal amount,
                                           Tax tax) {
        JournalEntry firstEntry = new JournalEntry();
        firstEntry.setAccountOperation(ao);
        firstEntry.setAccountingCode(code);
        firstEntry.setAmount(amount);
        firstEntry.setCustomerAccount(ao.getCustomerAccount());
        firstEntry.setDirection(JournalEntryDirectionEnum.getValue(categoryEnum.getId()));
        firstEntry.setSeller(getSeller(ao));
        firstEntry.setTax(tax);

        return firstEntry;
    }

    private Seller getSeller(AccountOperation ao) {
        return ao.getSeller() != null ? ao.getSeller() :
                ao.getCustomerAccount() != null && ao.getCustomerAccount().getCustomer() != null ?
                        ao.getCustomerAccount().getCustomer().getSeller() : null;
    }

}