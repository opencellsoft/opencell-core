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
package org.meveo.admin.action.finance;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

import org.meveo.model.billing.Invoice;
import org.meveo.model.finance.AccountingWriting;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AutomatedPayment;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.RecordedInvoice;

/**
 * Standard backing bean for {@link AccountingWriting}
 *
 * @author mboukayoua
 */
@Named
@ConversationScoped
public class AccountingWritingListBean extends AccountingWritingBean {
	
	private static final long serialVersionUID = 1L;

	/**
	 * selected Accounting writing id
	 */
	private AccountingWriting selectedAccountingWriting;

    /**
     * get selectedAccountingWriting
     * @return selected Accounting Writing
     */
    public AccountingWriting getSelectedAccountingWriting() {
        return selectedAccountingWriting;
    }

    /**
	 * select Accounting writing by it's ID
	 * @param accountingWritingId selected Accounting writing id
	 */
	public void selectAccountingWriting(Long accountingWritingId) {
		this.selectedAccountingWriting = getPersistenceService().findById(accountingWritingId);
	}
	
	/**
	 * get AOs linked to the selected Accounting writing
	 * @return list of {@code AccountOperation}
	 */
	public List<AccountOperation> getAccountOperations(){
	    if (selectedAccountingWriting != null) {
            return selectedAccountingWriting.getAccountOperations();
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
