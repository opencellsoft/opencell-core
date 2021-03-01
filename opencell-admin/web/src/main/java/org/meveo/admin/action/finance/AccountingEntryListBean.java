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
package org.meveo.admin.action.finance;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

import org.meveo.model.billing.Invoice;
import org.meveo.model.finance.AccountingEntry;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AutomatedPayment;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.RecordedInvoice;

/**
 * Standard backing bean for {@link AccountingEntry}
 *
 * @author mboukayoua
 */
@Named
@ConversationScoped
public class AccountingEntryListBean extends AccountingEntryBean {
	
	private static final long serialVersionUID = 1L;

	/**
	 * selected Accounting writing id
	 */
	private AccountingEntry selectedAccountingEntry;

    /**
     * get selectedAccountingEntry
     * @return selected Accounting Writing
     */
    public AccountingEntry getSelectedAccountingEntry() {
        return selectedAccountingEntry;
    }

    /**
	 * select Accounting writing by it's ID
	 * @param accountingWritingId selected Accounting writing id
	 */
	public void selectAccountingWriting(Long accountingWritingId) {
		this.selectedAccountingEntry = getPersistenceService().findById(accountingWritingId);
	}
	
	/**
	 * get AOs linked to the selected Accounting writing
	 * @return list of {@code AccountOperation}
	 */
	public List<AccountOperation> getAccountOperations(){
	    if (selectedAccountingEntry != null) {
            return selectedAccountingEntry.getAccountOperations();
        } else {
	        return null;
        }
	}
	
	/**
	 * check if an AO is a recorded invoice
	 * 
	 * @param accountOperation AO
	 * @return True or False
	 */
	public boolean isRecordedInvoice(AccountOperation accountOperation) {
		return accountOperation instanceof RecordedInvoice;
	}
	
	/**
     * Get invoices linked to the AO
     * 
     * @param accountOperation AO
     * @return invoices
     */
    public List<Invoice> getInvoices(AccountOperation accountOperation) {
        if (accountOperation instanceof RecordedInvoice) {
            RecordedInvoice recordedInvoice = (RecordedInvoice) accountOperation;
            return recordedInvoice.getInvoices();
        } else if (accountOperation instanceof Payment || accountOperation instanceof OtherCreditAndCharge) {
            List<MatchingAmount> listMatchingAmount = accountOperation.getMatchingAmounts();
            for(MatchingAmount ma : listMatchingAmount) {
                MatchingCode mc = ma.getMatchingCode();
                for(MatchingAmount ma2 : mc.getMatchingAmounts()) {
                    AccountOperation ao = ma2.getAccountOperation();
                    if(ao instanceof RecordedInvoice) {
                        return ao.getInvoices();
                    }
                }
            }
        }
        return new ArrayList<Invoice>();
    }
	
	/**
	 * display invoice page linked to an AO if it's a recorded invoice
	 * 
	 * @param accountOperation AO
	 * @return page's URL
	 */
	public String displayInvoice(AccountOperation accountOperation) {
        if (isRecordedInvoice(accountOperation)) {
        	return "/pages/payments/accountOperations/showInvoice.xhtml";
        } else {
        	return null;
        }
    }
	
	/**
	 * display AO view page
	 * 
	 * @param accountOperation AO to display
	 * @return AO page link
	 */
	public String displayOperation(AccountOperation accountOperation) {
        String page = "/pages/payments/accountOperations/showOcc.xhtml";
        if (accountOperation instanceof RecordedInvoice) {
            page = "/pages/payments/accountOperations/showInvoice.xhtml";
        }
        if (accountOperation instanceof AutomatedPayment) {
            page = "/pages/payments/accountOperations/showAutomatedPayment.xhtml";
        }
        if (accountOperation instanceof Payment) {
            page = "/pages/payments/accountOperations/showPayment.xhtml";
        }
        return page;
    }

}
