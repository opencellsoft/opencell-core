/**
 * 
 */
package org.meveo.admin.async;

import java.io.File;
import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.Invoice;
import org.meveo.service.billing.impl.XMLInvoiceCreator;
import org.slf4j.Logger;

/**
 * @author anasseh
 *
 */

@Stateless
public class XmlInvoiceAsync {
	
	@Inject
	private XMLInvoiceCreator xmlInvoiceCreator;
	
	@Inject
	protected Logger log;

	@Asynchronous
	public void launchAndForget(List<Invoice> invoices,File billingRundir) {
		
		for (Invoice invoice : invoices) {
			long startDate = System.currentTimeMillis();
			try {
				xmlInvoiceCreator.createXMLInvoice(invoice.getId(), billingRundir);
			} catch (BusinessException e) {				
				e.printStackTrace();
			}
			log.info("Invoice creation delay :" + (System.currentTimeMillis() - startDate) );
		}
	}
}
