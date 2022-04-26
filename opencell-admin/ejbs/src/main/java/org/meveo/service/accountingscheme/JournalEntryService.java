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

import org.meveo.model.accountingScheme.JournalEntry;
import org.meveo.model.accountingScheme.JournalEntryDirectionEnum;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Stateless
public class JournalEntryService extends PersistenceService<JournalEntry> {

    @Transactional
    public List<JournalEntry> createFromAccountOperation(AccountOperation ao, OCCTemplate occT) {
        // INTRD-4702
        // First JournalEntry
        JournalEntry firstEntry = buildJournalEntry(
                ao, occT.getAccountingCode(), occT.getOccCategory());

        // Second JournalEntry
        JournalEntry secondEntry = buildJournalEntry(
                ao, occT.getContraAccountingCode(),
                //if occCategory == DEBIT then direction= CREDIT and vice versa
                occT.getOccCategory() == OperationCategoryEnum.DEBIT ?
                        OperationCategoryEnum.CREDIT : OperationCategoryEnum.DEBIT);

        create(firstEntry);
        create(secondEntry);

        return Arrays.asList(firstEntry, secondEntry);

    }

    private JournalEntry buildJournalEntry(AccountOperation ao, AccountingCode code, OperationCategoryEnum categoryEnum) {
        JournalEntry firstEntry = new JournalEntry();
        firstEntry.setAccountOperation(ao);
        firstEntry.setAccountingCode(code);
        firstEntry.setAmount(ao.getAmount() == null ? BigDecimal.ZERO : ao.getAmount());
        firstEntry.setCustomerAccount(ao.getCustomerAccount());
        firstEntry.setDirection(JournalEntryDirectionEnum.getValue(categoryEnum.getId()));
        // when AccountOperation doesnot have a seller, get it from ao.customerAccount.customer.seller
        firstEntry.setSeller(ao.getSeller() != null ? ao.getSeller() : ao.getCustomerAccount().getCustomer().getSeller());
        firstEntry.setTax(null);
        firstEntry.setAnalyticCode1(null);
        firstEntry.setAnalyticCode2(null);
        firstEntry.setAnalyticCode3(null);

        return firstEntry;
    }

}