package org.meveo.api;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class InvoiceApi extends BaseApi {

	public void registerInvoice(String invoiceNo, Double amount, Date date,
			Date dueDate) {
		// TODO Seb
	}

}
