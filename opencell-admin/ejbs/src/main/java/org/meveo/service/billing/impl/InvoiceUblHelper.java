package org.meveo.service.billing.impl;


import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PeriodType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DocumentCurrencyCode;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.EndDate;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ID;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.InvoiceTypeCode;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueDate;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.Note;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.StartDate;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxCurrencyCode;
import oasis.names.specification.ubl.schema.xsd.invoice_2.Invoice;
import oasis.names.specification.ubl.schema.xsd.invoice_2.ObjectFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class InvoiceUblHelper {
	
	
	private final static InvoiceUblHelper INSTANCE = new InvoiceUblHelper();
	
	private InvoiceUblHelper(){}
	
	public InvoiceUblHelper getInstance(){ return  INSTANCE; }
	
	public String transformToXml(org.meveo.model.billing.Invoice invoice){
		Invoice invoiceXml = new ObjectFactory().createInvoice();
		setGeneralInfo(invoice, invoiceXml);
		return null;
	}
	
	private static void setGeneralInfo(org.meveo.model.billing.Invoice source, Invoice target){
		var objectFactorycommonBasic = new oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ObjectFactory();
		var objectFactoryCommonAggrement = new oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ObjectFactory();
		if(source.getInvoiceType() != null) {
			InvoiceTypeCode invoiceTypeCode = objectFactorycommonBasic.createInvoiceTypeCode();
			invoiceTypeCode.setListID("UN/ECE 1001 Subset");
			invoiceTypeCode.setListAgencyID("6");
			invoiceTypeCode.setValue(source.getInvoiceType().getCode());
			target.setInvoiceTypeCode(invoiceTypeCode);
		}
		ID id = objectFactorycommonBasic.createID();
		id.setValue(source.getInvoiceNumber());
		target.setID(id);
		
		IssueDate issueDate = objectFactorycommonBasic.createIssueDate();
		issueDate.setValue(toXmlDate(source.getInvoiceDate()));
		target.setIssueDate(issueDate);
		
		PeriodType periodType = objectFactoryCommonAggrement.createPeriodType();
		StartDate startDate = objectFactorycommonBasic.createStartDate();
		EndDate endDate = objectFactorycommonBasic.createEndDate();
		
		startDate.setValue(toXmlDate(source.getStartDate()));
		endDate.setValue(toXmlDate(source.getEndDate()));
		periodType.setStartDate(startDate);
		target.getInvoicePeriods().add(periodType);
		
		Note note = objectFactorycommonBasic.createNote();
		note.setValue(source.getDescription());
		target.getNotes().add(note);
		
		TaxCurrencyCode taxCurrencyCode = objectFactorycommonBasic.createTaxCurrencyCode();
		DocumentCurrencyCode documentCurrencyCode = objectFactorycommonBasic.createDocumentCurrencyCode();
		if(source.getTradingCurrency() != null && source.getTradingCurrency().getCurrencyCode() != null){
			taxCurrencyCode.setValue(source.getTradingCurrency().getCurrencyCode());
			documentCurrencyCode.setValue(source.getTradingCurrency().getCurrencyCode());
		}else if(source.getBillingAccount() != null && source.getBillingAccount().getTradingCurrency() != null && source.getBillingAccount().getTradingCurrency().getCurrencyCode() != null){
			taxCurrencyCode.setValue(source.getBillingAccount().getTradingCurrency().getCurrencyCode());
			documentCurrencyCode.setValue(source.getBillingAccount().getTradingCurrency().getCurrencyCode());
		}
		target.setDocumentCurrencyCode(documentCurrencyCode);
		target.setTaxCurrencyCode(taxCurrencyCode);
	}
	
	private static XMLGregorianCalendar toXmlDate(Date date){
		if(date == null) return null;
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	
}
