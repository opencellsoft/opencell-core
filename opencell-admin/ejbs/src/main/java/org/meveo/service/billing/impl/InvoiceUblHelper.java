package org.meveo.service.billing.impl;

import org.meveo.model.billing.Invoice;

public class InvoiceUblHelper {
	
	private Invoice invoice;
	
	private final static InvoiceUblHelper INSTANCE = new InvoiceUblHelper();
	
	private InvoiceUblHelper(){}
	
	public InvoiceUblHelper getInstance(){ return  INSTANCE; }
	
	
}
