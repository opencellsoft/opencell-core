/**
 * 
 */
package org.meveo.admin.async;

import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.service.billing.impl.InvoiceService;
import org.slf4j.Logger;

/**
 * @author anasseh
 *
 */

@Stateless
public class RatedTxInvoicingAsync {
	
	@Inject
	private InvoiceService invoiceService;
	
	@Inject
	protected Logger log;

	@Asynchronous
	public void launchAndForget(List<BillingAccount> billingAccounts,Long billingRunId,User currentUser) {
		
		for (BillingAccount billingAccount : billingAccounts) {
			try {
				invoiceService.createAgregatesAndInvoice(billingAccount, billingRunId, currentUser);
			} catch (Exception e) {
				log.error("Error for BA=" + billingAccount.getCode() + " : " + e.getMessage());
			}
		}
	}
}
