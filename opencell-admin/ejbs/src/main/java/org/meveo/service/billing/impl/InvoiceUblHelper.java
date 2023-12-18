package org.meveo.service.billing.impl;


import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.BillingReference;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.BranchType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ContactType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CountryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CreditNoteLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CustomerPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.FinancialAccountType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.FinancialInstitution;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ItemType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.MonetaryTotalType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.OrderReference;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyLegalEntity;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyTaxScheme;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentMeans;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentTermsType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PeriodType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PersonType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PriceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxScheme;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotal;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxTotalType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AdditionalStreetName;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AllowanceChargeReason;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AllowanceChargeReasonCode;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.Amount;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.BaseAmount;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.BaseQuantity;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ChargeIndicator;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CityName;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CompanyID;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CountrySubentity;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CreditNoteTypeCode;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CreditedQuantity;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.Description;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DocumentCurrencyCode;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DueDate;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ElectronicMail;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.EndDate;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.FamilyName;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.FirstName;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ID;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IdentificationCode;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.InvoiceTypeCode;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.InvoicedQuantity;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueDate;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.JobTitle;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.LineExtensionAmount;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.Note;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PayableAmount;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PaymentMeansCode;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.Percent;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PostalZone;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PriceAmount;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.RegistrationName;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.SalesOrderID;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.StartDate;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.StreetName;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxAmount;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxCurrencyCode;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxExclusiveAmount;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxExemptionReason;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxExemptionReasonCode;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxInclusiveAmount;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxTypeCode;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxableAmount;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.Telephone;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.UBLVersionID;
import oasis.names.specification.ubl.schema.xsd.creditnote_2.CreditNote;
import oasis.names.specification.ubl.schema.xsd.invoice_2.Invoice;
import oasis.names.specification.ubl.schema.xsd.invoice_2.ObjectFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.storage.StorageFactory;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UntdidAllowanceCode;
import org.meveo.model.billing.UntdidTaxationCategory;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Name;

import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

public class InvoiceUblHelper {
	
	
	private final static InvoiceUblHelper INSTANCE = new InvoiceUblHelper();
	private final static oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ObjectFactory objectFactorycommonBasic;
	private final static oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ObjectFactory objectFactoryCommonAggrement;
	
	private final static UntdidAllowanceCodeService untdidAllowanceCodeService;
	private final static InvoiceAgregateService invoiceAgregateService;
	
	static {
		objectFactorycommonBasic = new oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ObjectFactory();
		objectFactoryCommonAggrement = new oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ObjectFactory();
		invoiceAgregateService = (InvoiceAgregateService) EjbUtils.getServiceInterface(InvoiceAgregateService.class.getSimpleName());
		untdidAllowanceCodeService = (UntdidAllowanceCodeService) EjbUtils.getServiceInterface(UntdidAllowanceCodeService.class.getSimpleName());
	}
	
	private InvoiceUblHelper(){}
	
	public static InvoiceUblHelper getInstance(){ return  INSTANCE; }
	
	public Path createInvoiceUBL(org.meveo.model.billing.Invoice invoice){
		Invoice invoiceXml = new ObjectFactory().createInvoice();
		InvoiceType invoiceType = invoice.getInvoiceType();
		CreditNote creditNote = null;
		final String CREDIT_NOTE_INVOICE_TYPE = "381";
		if(invoiceType != null && invoiceType.getUntdidInvoiceCodeType() != null  && invoiceType.getUntdidInvoiceCodeType().getCode().contentEquals(CREDIT_NOTE_INVOICE_TYPE)) {
			creditNote = new oasis.names.specification.ubl.schema.xsd.creditnote_2.ObjectFactory().createCreditNote();
		}
		
		setUblExtension(invoiceXml, creditNote);
		setAllowanceCharge(invoice, invoiceXml, creditNote);
		
		
		if (CollectionUtils.isNotEmpty(invoice.getInvoiceAgregates())) {
			List<TaxInvoiceAgregate> taxInvoiceAgregates = invoice.getInvoiceAgregates().stream().filter(invAgg -> "T".equals(invAgg.getDescriminatorValue()))
					.map(invAgg -> (TaxInvoiceAgregate) invAgg)
					.collect(Collectors.toList());
			setTaxTotal(taxInvoiceAgregates, invoice.getAmountTax(), invoiceXml, creditNote,  invoice.getTradingCurrency() != null ? invoice.getTradingCurrency().getCurrencyCode() : null);
		}
		setPaymentTerms(invoiceXml, creditNote, invoice.getInvoiceType());
		setAccountingSupplierParty(invoice.getSeller(), invoiceXml, creditNote);
		setAccountingCustomerParty(invoice.getBillingAccount(), invoiceXml, creditNote);
		setPaymentMeans(invoice.getPaymentMethod(), invoiceXml, creditNote);
		String curreny = invoice.getTradingCurrency() != null ? invoice.getTradingCurrency().getCurrencyCode():null;
		BigDecimal amountWithoutTax = invoice.getAmountWithoutTax();
		BigDecimal amountWithTax = invoice.getAmountWithTax();
		if (creditNote != null) {
			setGeneralInfo(invoice, creditNote);
			setBillingReference(invoice, creditNote);
			setOrderReference(invoice, creditNote);
			setInvoiceLine(invoice.getInvoiceLines(), creditNote);
			creditNote.setLegalMonetaryTotal(setTaxExclusiveAmount(curreny, amountWithoutTax , amountWithTax));
		} else {
			setGeneralInfo(invoice, invoiceXml);
			setBillingReference(invoice, invoiceXml);
			setOrderReference(invoice, invoiceXml);
			setInvoiceLine(invoice.getInvoiceLines(), invoiceXml);
			invoiceXml.setLegalMonetaryTotal(setTaxExclusiveAmount(curreny, amountWithoutTax , amountWithTax));
		}
		
		
		// check directory if exist
		ParamBean paramBean = ParamBean.getInstance();
		File ublDirectory = new File(paramBean.getChrootDir("") + File.separator + paramBean.getProperty("meveo.ubl.directory", "/ubl"));
		if (!StorageFactory.existsDirectory(ublDirectory)) {
			StorageFactory.createDirectory(ublDirectory);
		}
		File ublInvoiceFileName = new File(	ublDirectory.getAbsolutePath() + File.separator + "invoice_" + invoice.getInvoiceNumber() + "_"	+ new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".xml");
		Path pathCreatedFile = null;

		try {
			pathCreatedFile = Files.createFile(Paths.get(ublInvoiceFileName.getAbsolutePath()));
			toXml(creditNote != null ? creditNote : invoiceXml, ublInvoiceFileName);
		} catch (Exception e) {
			throw new BusinessException(e);
		}

		return pathCreatedFile;
	}
	
	
	public void toXml(Object invoiceXml, File absoluteFileName) throws JAXBException {
		if(absoluteFileName == null || !absoluteFileName.isFile()) {
			throw new BusinessException("The file doesn't exist");
		}
		JAXBContext context = JAXBContext.newInstance(invoiceXml.getClass());
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		//marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2");
		marshaller.marshal(invoiceXml, absoluteFileName);
	}
	
	private void setPaymentTerms(Invoice target, CreditNote creditNote, InvoiceType invoiceType) {
		if(invoiceType != null && invoiceType.getUntdidInvoiceCodeType() != null && invoiceType.getUntdidInvoiceCodeType().getCode().equals("380")) {
			PaymentTermsType paymentTermsType = objectFactoryCommonAggrement.createPaymentTermsType();
			Note note = objectFactorycommonBasic.createNote();
			note.setValue("No early payment discount. Any amounts owned that are not paid will due shall bear interest, from the time the payment was due until the time paid, " +
					"at a rate of 10% per annum compounded annually");
			paymentTermsType.getNotes().add(note);
			if(creditNote == null)
				target.getPaymentTerms().add(paymentTermsType);
			else{
				creditNote.getPaymentTerms().add(paymentTermsType);
			}
		}
	}
	
	private void setUblExtension(Invoice target, CreditNote creditNote){
		UBLVersionID ublVersionID = objectFactorycommonBasic.createUBLVersionID();
		ublVersionID.setValue("2.1");
		if(creditNote != null){
			creditNote.setUBLVersionID(ublVersionID);
		}else{
			target.setUBLVersionID(ublVersionID);
		}
		
	}
	
	private static void setGeneralInfo(org.meveo.model.billing.Invoice source, Invoice target){
		if(source.getInvoiceType() != null && source.getInvoiceType().getUntdidInvoiceCodeType() != null) {
			InvoiceType invoiceType = source.getInvoiceType();
			InvoiceTypeCode invoiceTypeCode = objectFactorycommonBasic.createInvoiceTypeCode();
			invoiceTypeCode.setListID("UNCL 1001");
			invoiceTypeCode.setValue(invoiceType.getUntdidInvoiceCodeType().getCode());
			target.setInvoiceTypeCode(invoiceTypeCode);
		}
		ID id = objectFactorycommonBasic.createID();
		id.setValue(source.getInvoiceNumber());
		target.setID(id);
		
		IssueDate issueDate = getIssueDate(source.getInvoiceDate());
		target.setIssueDate(issueDate);
		
		if(source.getStartDate() != null){
			PeriodType periodType = objectFactoryCommonAggrement.createPeriodType();
			StartDate startDate = objectFactorycommonBasic.createStartDate();
			EndDate endDate = objectFactorycommonBasic.createEndDate();
			
			startDate.setValue(toXmlDate(source.getStartDate()));
			endDate.setValue(toXmlDate(source.getEndDate()));
			periodType.setStartDate(startDate);
			target.getInvoicePeriods().add(periodType);
		}
		
		Note note = objectFactorycommonBasic.createNote();
		note.setValue(source.getDescription());
		target.getNotes().add(note);
		
		setTaxCurrencyCodeAndDocumentCurrencyCode(objectFactorycommonBasic, source, target);
		
		DueDate dueDate = objectFactorycommonBasic.createDueDate();
		dueDate.setValue(toXmlDate(source.getDueDate()));
		target.setDueDate(dueDate);
		
		var monetaryTotalType = objectFactoryCommonAggrement.createMonetaryTotalType();
		var taxInclusiveAmount = objectFactorycommonBasic.createTaxInclusiveAmount();
		final String currencyId = source.getTradingCurrency() != null ? source.getTradingCurrency().getCurrencyCode() : null;
		taxInclusiveAmount.setCurrencyID(currencyId);
		taxInclusiveAmount.setValue(source.getAmountWithTax());
		monetaryTotalType.setTaxInclusiveAmount(taxInclusiveAmount);
		var allowanceTotalAmount = objectFactorycommonBasic.createAllowanceTotalAmount();
		allowanceTotalAmount.setCurrencyID(currencyId);
		allowanceTotalAmount.setValue(source.getDiscountAmount());
		monetaryTotalType.setAllowanceTotalAmount(allowanceTotalAmount);
		PayableAmount payableAmount = objectFactorycommonBasic.createPayableAmount();
		payableAmount.setValue(source.getAmountWithTax());
		payableAmount.setCurrencyID(currencyId);
		monetaryTotalType.setPayableAmount(payableAmount);
		target.setLegalMonetaryTotal(monetaryTotalType);
	}
	private static void setGeneralInfo(org.meveo.model.billing.Invoice source, CreditNote target){
		if(source.getInvoiceType() != null && source.getInvoiceType().getUntdidInvoiceCodeType() != null) {
			InvoiceType invoiceType = source.getInvoiceType();
			CreditNoteTypeCode invoiceTypeCode = objectFactorycommonBasic.createCreditNoteTypeCode();
			invoiceTypeCode.setListID("UNCL 1001");
			invoiceTypeCode.setValue(invoiceType.getUntdidInvoiceCodeType().getCode());
			target.setCreditNoteTypeCode(invoiceTypeCode);
		}
		ID id = objectFactorycommonBasic.createID();
		id.setValue(source.getInvoiceNumber());
		target.setID(id);
		
		IssueDate issueDate = getIssueDate(source.getInvoiceDate());
		target.setIssueDate(issueDate);
		
		if(source.getStartDate() != null){
			PeriodType periodType = objectFactoryCommonAggrement.createPeriodType();
			StartDate startDate = objectFactorycommonBasic.createStartDate();
			EndDate endDate = objectFactorycommonBasic.createEndDate();
			
			startDate.setValue(toXmlDate(source.getStartDate()));
			endDate.setValue(toXmlDate(source.getEndDate()));
			periodType.setStartDate(startDate);
			target.getInvoicePeriods().add(periodType);
		}
		
		Note note = objectFactorycommonBasic.createNote();
		note.setValue(source.getDescription());
		target.getNotes().add(note);
		
		setTaxCurrencyCodeAndDocumentCurrencyCode(objectFactorycommonBasic, source, target);
		
		
		var monetaryTotalType = objectFactoryCommonAggrement.createMonetaryTotalType();
		var taxInclusiveAmount = objectFactorycommonBasic.createTaxInclusiveAmount();
		final String currencyId = source.getTradingCurrency() != null ? source.getTradingCurrency().getCurrencyCode() : null;
		taxInclusiveAmount.setCurrencyID(currencyId);
		taxInclusiveAmount.setValue(source.getAmountWithTax());
		monetaryTotalType.setTaxInclusiveAmount(taxInclusiveAmount);
		var allowanceTotalAmount = objectFactorycommonBasic.createAllowanceTotalAmount();
		allowanceTotalAmount.setCurrencyID(currencyId);
		allowanceTotalAmount.setValue(source.getDiscountAmount());
		monetaryTotalType.setAllowanceTotalAmount(allowanceTotalAmount);
		PayableAmount payableAmount = objectFactorycommonBasic.createPayableAmount();
		payableAmount.setValue(source.getAmountWithTax());
		payableAmount.setCurrencyID(currencyId);
		monetaryTotalType.setPayableAmount(payableAmount);
		target.setLegalMonetaryTotal(monetaryTotalType);
	}
	
	private void setPaymentMeans(PaymentMethod paymentMethod, Invoice target, CreditNote creditNote){
		PaymentMeans paymentMeans = objectFactoryCommonAggrement.createPaymentMeans();
		if(paymentMethod != null && paymentMethod.getPaymentMeans() != null) {
			PaymentMeansCode paymentMeansCode = objectFactorycommonBasic.createPaymentMeansCode();
			paymentMeansCode.setListID("UN/ECE 4461");
			paymentMeansCode.setListAgencyID("NES");
			paymentMeansCode.setListAgencyName("Northern European Subset");
			paymentMeansCode.setListName("Payment Means");
			paymentMeansCode.setValue(paymentMethod.getPaymentMeans().getCode());
			paymentMeans.setPaymentMeansCode(paymentMeansCode);
			
		}
		
		if(paymentMethod instanceof DDPaymentMethod) {
			FinancialAccountType financialAccountType = objectFactoryCommonAggrement.createFinancialAccountType();
			DDPaymentMethod bank = (DDPaymentMethod)  paymentMethod;
			ID id = null;
			// PaymentMeans/PayeeFinancialAccount/ID
			if(StringUtils.isNotBlank(bank.getBankCoordinates().getIban())){
				id = objectFactorycommonBasic.createID();
				id.setValue(bank.getBankCoordinates().getIban());
				financialAccountType.setID(id);
			}
			// PaymentMeans/PayeeFinancialAccount/FinancialInstitutionBranch/FinancialInstitution/ID
			if(StringUtils.isNotBlank(bank.getBankCoordinates().getBankCode())){
				id = objectFactorycommonBasic.createID();
				id.setValue(bank.getBankCoordinates().getBankCode());
				FinancialInstitution financialInstitution = objectFactoryCommonAggrement.createFinancialInstitution();
				financialInstitution.setID(id);
				BranchType branchType = objectFactoryCommonAggrement.createBranchType();
				branchType.setFinancialInstitution(financialInstitution);
				financialAccountType.setFinancialInstitutionBranch(branchType);
			}
			paymentMeans.setPayeeFinancialAccount(financialAccountType);
		}
		if(paymentMeans.getPaymentMeansCode() != null || paymentMeans.getPayeeFinancialAccount() != null){
			if(creditNote == null)
				target.getPaymentMeans().add(paymentMeans);
			else
				creditNote.getPaymentMeans().add(paymentMeans);
		}
	}
	private void setInvoiceLine(List<InvoiceLine> invoiceLines, Invoice target){
		invoiceLines.forEach(invoiceLine -> {
			// InvoiceLine/ Item/ ClassifiedTaxCategory/ Percent
			InvoiceLineType invoiceLineType = objectFactoryCommonAggrement.createInvoiceLineType();
			ItemType itemType = getItemTyp(invoiceLine);
			// InvoiceLine/ InvoicedQuantity
			InvoicedQuantity invoicedQuantity = objectFactorycommonBasic.createInvoicedQuantity();
			invoicedQuantity.setValue(invoiceLine.getQuantity());
			invoiceLineType.setInvoicedQuantity(invoicedQuantity);
			setPriceAndCreditLine(invoiceLine, invoiceLineType, itemType);
			target.getInvoiceLines().add(invoiceLineType);
		});
	}
	private void setInvoiceLine(List<InvoiceLine> invoiceLines, CreditNote target){
		invoiceLines.forEach(invoiceLine -> {
			// InvoiceLine/ Item/ ClassifiedTaxCategory/ Percent
			CreditNoteLineType invoiceLineType = objectFactoryCommonAggrement.createCreditNoteLineType();
			ItemType itemType = getItemTyp(invoiceLine);
			// InvoiceLine/ InvoicedQuantity
			CreditedQuantity invoicedQuantity = objectFactorycommonBasic.createCreditedQuantity();
			invoicedQuantity.setValue(invoiceLine.getQuantity());
			invoiceLineType.setCreditedQuantity(invoicedQuantity);
			setPriceAndCreditLine(invoiceLine, invoiceLineType, itemType);
			target.getCreditNoteLines().add(invoiceLineType);
		});
	}
	
	private void setPriceAndCreditLine(InvoiceLine invoiceLine, Object invoiceLineType, ItemType itemType) {
		// InvoiceLine/ Price/ BaseQuantity
		PriceType priceType = getPriceType(invoiceLine);
		// InvoiceLine/ LineExtensionAmount
		LineExtensionAmount lineExtensionAmount = getLineExtensionAmount(invoiceLine);
		// InvoiceLine/ Note
		ID id = objectFactorycommonBasic.createID();
		id.setValue(invoiceLine.getId().toString());
		Note note = objectFactorycommonBasic.createNote();
		note.setValue(invoiceLine.getLabel());
		if(invoiceLineType instanceof  CreditNoteLineType){
			CreditNoteLineType CreditlineType = (CreditNoteLineType) invoiceLineType;
			CreditlineType.setID(id);
			CreditlineType.setPrice(priceType);
			CreditlineType.setLineExtensionAmount(lineExtensionAmount);
			CreditlineType.getNotes().add(note);
			CreditlineType.setItem(itemType);
		}else{
			InvoiceLineType lineType = (InvoiceLineType) invoiceLineType;
			lineType.setID(id);
			lineType.setPrice(priceType);
			lineType.setLineExtensionAmount(lineExtensionAmount);
			lineType.getNotes().add(note);
			lineType.setItem(itemType);
		}
	}
	
	private static LineExtensionAmount getLineExtensionAmount(InvoiceLine invoiceLine) {
		LineExtensionAmount lineExtensionAmount = objectFactorycommonBasic.createLineExtensionAmount();
		lineExtensionAmount.setCurrencyID(invoiceLine.getInvoice().getTradingCurrency().getCurrencyCode());
		lineExtensionAmount.setValue(invoiceLine.getAmountWithTax());
		return lineExtensionAmount;
	}
	
	private ItemType getItemTyp(InvoiceLine invoiceLine){
		ItemType itemType = objectFactoryCommonAggrement.createItemType();
		TaxCategoryType taxCategoryType = objectFactoryCommonAggrement.createTaxCategoryType();
		Percent percent = objectFactorycommonBasic.createPercent();
		percent.setValue(invoiceLine.getTaxRate());
		taxCategoryType.setPercent(percent);
		// InvoiceLine/ Item/ ClassifiedTaxCategory/TaxScheme/TaxTypeCode
		TaxScheme taxScheme = objectFactoryCommonAggrement.createTaxScheme();
		TaxTypeCode taxTypeCode = objectFactorycommonBasic.createTaxTypeCode();
		taxTypeCode.setValue(invoiceLine.getTax().getCode());
		taxScheme.setTaxTypeCode(taxTypeCode);
		taxCategoryType.setTaxScheme(taxScheme);
		itemType.getClassifiedTaxCategories().add(taxCategoryType);
		//InvoiceLine/ Item/ Description
		Description description = objectFactorycommonBasic.createDescription();
		description.setValue(invoiceLine.getLabel());
		itemType.getDescriptions().add(description);
		// InvoiceLine/ Item/ Name
		oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.Name name = objectFactorycommonBasic.createName();
		name.setValue(invoiceLine.getLabel());
		itemType.setName(name);
		return itemType;
	}
	
	private PriceType getPriceType(InvoiceLine invoiceLine){
		PriceType priceType = objectFactoryCommonAggrement.createPriceType();
		BaseQuantity baseQuantity = objectFactorycommonBasic.createBaseQuantity();
		baseQuantity.setValue(new BigDecimal(1));
		priceType.setBaseQuantity(baseQuantity);
		
		final String currencyCode = invoiceLine.getInvoice().getTradingCurrency().getCurrencyCode();
		// InvoiceLine/ Price/ PriceAmount
		PriceAmount priceAmount = objectFactorycommonBasic.createPriceAmount();
		priceAmount.setCurrencyID(currencyCode);
		priceAmount.setValue(invoiceLine.getUnitPrice());
		priceType.setPriceAmount(priceAmount);
		return priceType;
	}
	private void setAccountingCustomerParty(BillingAccount billingAccount, Invoice target, CreditNote creditNote){
		// AccountingCustomerParty/Party
		CustomerPartyType customerPartyType = objectFactoryCommonAggrement.createCustomerPartyType();
		PartyType partyType = objectFactoryCommonAggrement.createPartyType();
		
		Address address = billingAccount.getAddress();
		if(billingAccount.getAddress() != null) {
			AddressType postalAddress = objectFactoryCommonAggrement.createAddressType();
			// AccountingCustomerParty/Party/PostalAddress/CityName
			if(StringUtils.isNotBlank(address.getCity())){
				CityName cityName = objectFactorycommonBasic.createCityName();
				cityName.setValue(address.getCity());
				postalAddress.setCityName(cityName);
			}
			//AccountingCustomerParty/Party/PostalAddress/PostalZone
			if(StringUtils.isNotBlank(address.getZipCode())){
				PostalZone postalZone = objectFactorycommonBasic.createPostalZone();
				postalZone.setValue(address.getZipCode());
				postalAddress.setPostalZone(postalZone);
			}
			//AccountingCustomerParty/Party/PostalAddress/Country
			if (address.getCountry() != null) {
				CountryType countryType = objectFactoryCommonAggrement.createCountryType();
				IdentificationCode identificationCode = objectFactorycommonBasic.createIdentificationCode();
				identificationCode.setValue(address.getCountry().getCode());
				countryType.setIdentificationCode(identificationCode);
				postalAddress.setCountry(countryType);
			}
			//AccountingCustomerParty/Party/PostalAddress/PostalAddress
			if(StringUtils.isNotBlank(address.getAddress1())){
				StreetName streetName = objectFactorycommonBasic.createStreetName();
				streetName.setValue(address.getAddress1());
				postalAddress.setStreetName(streetName);
			}
			partyType.setPostalAddress(postalAddress);
		}
		// AccountingCustomerParty/Party/PartyTaxScheme/CompanyID
		if(StringUtils.isNotBlank(billingAccount.getVatNo()) || ( billingAccount.getSeller() != null && billingAccount.getVatNo() != null)){
			// AccountingSupplierParty/Party/PartyTaxScheme/CompanyID
			PartyTaxScheme partyTaxScheme = objectFactoryCommonAggrement.createPartyTaxScheme();
			CompanyID companyID = objectFactorycommonBasic.createCompanyID();
			companyID.setSchemeAgencyID("ZZZ");
			companyID.setSchemeID(address.getCountry() != null ? address.getCountry().getCountryCode() : null );
			companyID.setValue(billingAccount.getVatNo());
			partyTaxScheme.setCompanyID(companyID);
			partyTaxScheme.setTaxScheme(getTaxSheme());
			partyType.getPartyTaxSchemes().add(partyTaxScheme);
			//TODO : AccountingCustomerParty/Party/PartyTaxScheme/TaxScheme/ID ask @Emmanuel for this field INTRD-12578
		}
		// AccountingCustomerParty/Party/PartyLegalEntity
		PartyLegalEntity partyLegalEntity = objectFactoryCommonAggrement.createPartyLegalEntity();
		
		// AccountingCustomerParty/Party/PartyLegalEntity/RegistrationName
		if(StringUtils.isNotBlank(billingAccount.getDescription())){
			RegistrationName registrationName = objectFactorycommonBasic.createRegistrationName();
			registrationName.setValue(billingAccount.getDescription());
			partyLegalEntity.setRegistrationName(registrationName);
		}
		if(billingAccount.getAddress() != null){
			// AccountingCustomerParty/Party/PartyLegalEntity/RegistrationAddress
			AddressType addressType = getRegistrationAddress(billingAccount.getAddress());
			partyLegalEntity.setRegistrationAddress(addressType);
		}
		partyType.getPartyLegalEntities().add(partyLegalEntity);
		
		// AccountingCustomerParty/Party/PartyLegalEntity/Contact
		// todo : Check this contact namespace is correct
		if(billingAccount.getContactInformation() != null){
			ContactType contactType = getContactInformation(billingAccount.getContactInformation());
			partyType.setContact(contactType);
		}
		// AccountingCustomerParty/Party/PartyLegalEntity/Person
		
		if(billingAccount.getName() != null) {
			// AccountingSupplierParty/Party/Person/FirstName
			PersonType personType = getPersonType(billingAccount.getName());
			partyType.getPersons().add(personType);
		}
		customerPartyType.setParty(partyType);
		if(creditNote == null)
			target.setAccountingCustomerParty(customerPartyType);
		else
			creditNote.setAccountingCustomerParty(customerPartyType);
	}
	private TaxScheme getTaxSheme(){
		TaxScheme taxScheme = objectFactoryCommonAggrement.createTaxScheme();
		TaxTypeCode taxTypeCode = objectFactorycommonBasic.createTaxTypeCode();
		taxTypeCode.setValue("TVA_SUR_ENCAISSEMENT");
		ID id = objectFactorycommonBasic.createID();
		id.setSchemeID("UN/ECE 5153");
		id.setSchemeAgencyID("6");
		id.setValue("VAT");
		taxScheme.setTaxTypeCode(taxTypeCode);
		taxScheme.setID(id);
		return taxScheme;
	}
	private PersonType getPersonType(Name name){
		PersonType personType = objectFactoryCommonAggrement.createPersonType();
		if(StringUtils.isNotBlank(name.getFirstName())){
			FirstName firstName = objectFactorycommonBasic.createFirstName();
			firstName.setValue(name.getFirstName());
			personType.setFirstName(firstName);
		}
		//AccountingSupplierParty/Party/Person/FamilyName
		if(StringUtils.isNotBlank(name.getLastName())){
			FamilyName familyName = objectFactorycommonBasic.createFamilyName();
			familyName.setValue(name.getLastName());
			personType.setFamilyName(familyName);
		}
		return personType;
	}
	
	private AddressType getRegistrationAddress(Address address){
		if(address == null) return null;
		AddressType addressType = objectFactoryCommonAggrement.createAddressType();
		// AccountingCustomerParty/Party/PartyLegalEntity/RegistrationAddress/CityName
		if(StringUtils.isNotBlank(address.getCity())){
			CityName cityName = objectFactorycommonBasic.createCityName();
			cityName.setValue(address.getCity());
			addressType.setCityName(cityName);
		}
		// AccountingCustomerParty/Party/PartyLegalEntity/RegistrationAddress/PostalZone
		if(StringUtils.isNotBlank(address.getZipCode())){
			PostalZone postalZone = objectFactorycommonBasic.createPostalZone();
			postalZone.setValue(address.getZipCode());
			addressType.setPostalZone(postalZone);
		}
		// AccountingCustomerParty/Party/PartyLegalEntity/RegistrationAddress/CountrySubentity
		if(StringUtils.isNotBlank(address.getState())){
			CountrySubentity countrySubentity = objectFactorycommonBasic.createCountrySubentity();
			countrySubentity.setValue(address.getState());
			addressType.setCountrySubentity(countrySubentity);
		}
		// AccountingCustomerParty/Party/PartyLegalEntity/RegistrationAddress/Country
		if (address.getCountry() != null) {
			CountryType countryType = objectFactoryCommonAggrement.createCountryType();
			IdentificationCode identificationCode = objectFactorycommonBasic.createIdentificationCode();
			identificationCode.setValue(address.getCountry().getCode());
			countryType.setIdentificationCode(identificationCode);
			addressType.setCountry(countryType);
		}
		// AccountingCustomerParty/Party/PartyLegalEntity/RegistrationAddress/RegistrationAddress
		if(StringUtils.isNotBlank(address.getAddress1())){
			StreetName streetName = objectFactorycommonBasic.createStreetName();
			streetName.setValue(address.getAddress1());
			addressType.setStreetName(streetName);
		}
		return addressType;
	}
	private void setAccountingSupplierParty(Seller seller, Invoice target, CreditNote creditNote){
		SupplierPartyType supplierPartyType = objectFactoryCommonAggrement.createSupplierPartyType();
		//AccountingSupplierParty/Party
		PartyType partyType = objectFactoryCommonAggrement.createPartyType();
		// AccountingSupplierParty/Party/PartyLegalEntity
		PartyLegalEntity partyLegalEntity = objectFactoryCommonAggrement.createPartyLegalEntity();
		// AccountingSupplierParty/Party/PartyLegalEntity/RegistrationName
		RegistrationName registrationName = objectFactorycommonBasic.createRegistrationName();
		registrationName.setValue(seller.getDescription());
		partyLegalEntity.setRegistrationName(registrationName);
		// AccountingSupplierParty/Party/PartyLegalEntity/RegistrationAddress/StreetName
		if(seller.getAddress() != null) {
			Address address = seller.getAddress();
			AddressType addressType = objectFactoryCommonAggrement.createAddressType();
			StreetName streetName = objectFactorycommonBasic.createStreetName();
			streetName.setValue(address.getAddress1());
			addressType.setStreetName(streetName);
			// AccountingSupplierParty/Party/PartyLegalEntity/RegistrationAddress/AdditionalStreetName
			if (StringUtils.isNotBlank(address.getAddress2())) {
				AdditionalStreetName additionalStreetName = objectFactorycommonBasic.createAdditionalStreetName();
				additionalStreetName.setValue(address.getAddress2());
				addressType.setAdditionalStreetName(additionalStreetName);
			}
			// AccountingSupplierParty/Party/PartyLegalEntity/RegistrationAddress/PostalZone
			PostalZone postalZone = objectFactorycommonBasic.createPostalZone();
			postalZone.setValue(address.getZipCode());
			addressType.setPostalZone(postalZone);
			// AccountingSupplierParty/Party/PartyLegalEntity/RegistrationAddress/Country/IdentificationCode
			if (address.getCountry() != null) {
				CountryType countryType = objectFactoryCommonAggrement.createCountryType();
				IdentificationCode identificationCode = objectFactorycommonBasic.createIdentificationCode();
				identificationCode.setValue(address.getCountry().getCode());
				countryType.setIdentificationCode(identificationCode);
				addressType.setCountry(countryType);
			}
			partyLegalEntity.setRegistrationAddress(addressType);
			partyType.getPartyLegalEntities().add(partyLegalEntity);
		}
		if(StringUtils.isNotBlank(seller.getVatNo())){
			// AccountingSupplierParty/Party/PartyTaxScheme/CompanyID
			PartyTaxScheme taxScheme = objectFactoryCommonAggrement.createPartyTaxScheme();
			CompanyID companyID = objectFactorycommonBasic.createCompanyID();
			String countryCode = seller.getAddress() != null && seller.getAddress().getCountry() != null ? seller.getAddress().getCountry().getCountryCode() : null;
			companyID.setSchemeID(countryCode);
			companyID.setSchemeAgencyID("ZZZ");
			companyID.setValue(countryCode + seller.getVatNo());
			taxScheme.setCompanyID(companyID);
			partyType.getPartyTaxSchemes().add(taxScheme);
		}
		
		if(seller.getContactInformation() != null) {
			//AccountingSupplierParty/Party/Contact/Telephone
			ContactType contactType = getContactInformation(seller.getContactInformation());
			partyType.setContact(contactType);
		}
		// AccountingSupplierParty/Party/Person
		if(seller.getName() != null) {
			// AccountingSupplierParty/Party/Person/FirstName
			Name name = seller.getName();
			PersonType personType = objectFactoryCommonAggrement.createPersonType();
			if(StringUtils.isNotBlank(name.getFirstName())){
				FirstName firstName = objectFactorycommonBasic.createFirstName();
				firstName.setValue(name.getFirstName());
				personType.setFirstName(firstName);
			}
			//AccountingSupplierParty/Party/Person/FamilyName
			if(StringUtils.isNotBlank(name.getLastName())){
				FamilyName familyName = objectFactorycommonBasic.createFamilyName();
				familyName.setValue(name.getLastName());
				personType.setFamilyName(familyName);
			}
			// AccountingSupplierParty/Party/Person/JobTitle
			if(name.getTitle() != null && StringUtils.isNotBlank(name.getTitle().getCode())){
				JobTitle jobTitle = objectFactorycommonBasic.createJobTitle();
				jobTitle.setValue(name.getTitle().getCode());
				personType.setJobTitle(jobTitle);
			}
			partyType.getPersons().add(personType);
		}
		supplierPartyType.setParty(partyType);
		if(creditNote == null)
			target.setAccountingSupplierParty(supplierPartyType);
		else
			creditNote.setAccountingSupplierParty(supplierPartyType);
		
	}
	
	private ContactType getContactInformation(ContactInformation contactInformation) {
		ContactType contactType = objectFactoryCommonAggrement.createContactType();
		if(StringUtils.isNotBlank(contactInformation.getPhone())) {
			Telephone telephone = objectFactorycommonBasic.createTelephone();
			telephone.setValue(contactInformation.getPhone());
			contactType.setTelephone(telephone);
		}
		// AccountingSupplierParty/Party/Contact/ElectronicMail
		if(StringUtils.isNotBlank(contactInformation.getEmail())) {
			ElectronicMail electronicMail = objectFactorycommonBasic.createElectronicMail();
			electronicMail.setValue(contactInformation.getEmail());
			contactType.setElectronicMail(electronicMail);
		}
		return contactType;
	}
	
	private void setTaxTotal(List<TaxInvoiceAgregate> taxInvoiceAgregates, BigDecimal amountTax,  Invoice target, CreditNote creditNote,  String currency) {
		if(CollectionUtils.isNotEmpty(taxInvoiceAgregates)) {
			TaxTotalType taxTotalType = objectFactoryCommonAggrement.createTaxTotalType();
			TaxAmount taxAmount = objectFactorycommonBasic.createTaxAmount();
			taxAmount.setCurrencyID(currency);
			taxAmount.setValue(amountTax);
			taxTotalType.setTaxAmount(taxAmount);
			taxInvoiceAgregates.forEach(taxInvoiceAgregate -> {
				TaxSubtotal taxSubtotal = objectFactoryCommonAggrement.createTaxSubtotal();
				if(taxInvoiceAgregate.getTax() != null) {
					//TaxTotal/ TaxSubtotal / TaxCategory/ TaxScheme/ TaxTypeCode
					TaxCategoryType taxCategoryType = setTaxCategory(taxInvoiceAgregate);
					taxSubtotal.setTaxCategory(taxCategoryType);
					
				}
				// TaxTotal/ TaxSubtotal / TaxableAmount
				TaxableAmount taxableAmount = objectFactorycommonBasic.createTaxableAmount();
				final String currencyCode = taxInvoiceAgregate.getTradingCurrency() != null ? taxInvoiceAgregate.getTradingCurrency().getCurrencyCode() : currency;
				taxableAmount.setCurrencyID(currencyCode);
				taxableAmount.setValue(taxInvoiceAgregate.getAmountWithoutTax());
				taxSubtotal.setTaxableAmount(taxableAmount);
				// TaxTotal/ TaxSubtotal / Percent
				Percent percent = objectFactorycommonBasic.createPercent();
				percent.setValue(taxInvoiceAgregate.getTaxPercent());
				taxSubtotal.setPercent(percent);
				// TaxTotal/ TaxSubtotal / TaxAmount
				TaxAmount taxAmountSubTotal = objectFactorycommonBasic.createTaxAmount();
				taxAmountSubTotal.setValue(taxInvoiceAgregate.getAmountTax());
				taxAmountSubTotal.setCurrencyID(currencyCode);
				taxSubtotal.setTaxAmount(taxAmountSubTotal);
				taxTotalType.getTaxSubtotals().add(taxSubtotal);
			});
			if(creditNote == null)
				target.getTaxTotals().add(taxTotalType);
			else{
				creditNote.getTaxTotals().add(taxTotalType);
			}
		}
	}
	private void setAllowanceCharge(org.meveo.model.billing.Invoice invoice, Invoice target, CreditNote creditNote){
		List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = (List<SubCategoryInvoiceAgregate>) invoiceAgregateService.listByInvoiceAndType(invoice, "F");
		if(CollectionUtils.isNotEmpty(subCategoryInvoiceAgregates)){
			subCategoryInvoiceAgregates.forEach(subCategoryInvoiceAgregate -> {
				AllowanceChargeType allowanceCharge = objectFactoryCommonAggrement.createAllowanceChargeType();
				ChargeIndicator chargeIndicator = objectFactorycommonBasic.createChargeIndicator();
				chargeIndicator.setValue(false);
				allowanceCharge.setChargeIndicator(chargeIndicator);
				AllowanceChargeReasonCode allowanceChargeReasonCode = objectFactorycommonBasic.createAllowanceChargeReasonCode();
				AllowanceChargeReason allowanceChargeReason = objectFactorycommonBasic.createAllowanceChargeReason();
				if(subCategoryInvoiceAgregate.getDiscountPlanItem() != null) {
					allowanceChargeReasonCode.setValue(subCategoryInvoiceAgregate.getDiscountPlanItem().getCode());
					allowanceCharge.setAllowanceChargeReasonCode(allowanceChargeReasonCode);
					
					allowanceChargeReason.setValue(subCategoryInvoiceAgregate.getDiscountPlanItem().getDescription());
					allowanceCharge.getAllowanceChargeReasons().add(allowanceChargeReason);
				}else{
					UntdidAllowanceCode allowanceCode = untdidAllowanceCodeService.getByCode("104");
					if(allowanceCode == null) {
						throw new EntityDoesNotExistsException(UntdidAllowanceCode.class, "104");
					}
					allowanceChargeReasonCode.setValue(allowanceCode.getCode());
					allowanceCharge.setAllowanceChargeReasonCode(allowanceChargeReasonCode);
					
					allowanceChargeReason.setValue(allowanceCode.getDescription());
					allowanceCharge.getAllowanceChargeReasons().add(allowanceChargeReason);
				}
				Amount amount = objectFactorycommonBasic.createAmount();
				amount.setValue(subCategoryInvoiceAgregate.getAmountWithTax().abs());
				allowanceCharge.setAmount(amount);
				
				BaseAmount baseAmount = objectFactorycommonBasic.createBaseAmount();
				baseAmount.setCurrencyID(invoice.getTradingCurrency() != null ? invoice.getTradingCurrency().getCurrencyCode() : null);
				baseAmount.setValue(subCategoryInvoiceAgregate.getAmountWithoutTax().abs());
				allowanceCharge.setBaseAmount(baseAmount);
				if(creditNote != null)
					creditNote.getAllowanceCharges().add(allowanceCharge);
				else
					target.getAllowanceCharges().add(allowanceCharge);
				
			});
		}
		
	}
	
	private static IssueDate getIssueDate(Date date){
		IssueDate issueDate = objectFactorycommonBasic.createIssueDate();
		issueDate.setValue(toXmlDate(date));
		return issueDate;
	}
	
	private OrderReference getOrderReference(CommercialOrder commercialOrder, Date invoiceDate) {
		if(commercialOrder == null) return null;
		OrderReference orderReference = objectFactoryCommonAggrement.createOrderReference();
		SalesOrderID salesOrderID = orderReference.getSalesOrderID();
		salesOrderID.setValue(commercialOrder != null ? commercialOrder.getOrderNumber() : StringUtils.EMPTY);
		orderReference.setSalesOrderID(salesOrderID);
		orderReference.setIssueDate(getIssueDate(invoiceDate));
		return orderReference;
	}
	private void setOrderReference(org.meveo.model.billing.Invoice source, Invoice target){
		target.setOrderReference(getOrderReference(source.getCommercialOrder(), source.getInvoiceDate()));
	}
	private void setOrderReference(org.meveo.model.billing.Invoice source, CreditNote target){
		target.setOrderReference(getOrderReference(source.getCommercialOrder(), source.getInvoiceDate()));
	}
	private void setBillingReference(org.meveo.model.billing.Invoice source, Invoice target){
		source.getLinkedInvoices().forEach(linInv -> {
			BillingReference billingReference = setBillingReference(linInv.getLinkedInvoiceValue());
			target.getBillingReferences().add(billingReference);
		});
	}
	private void setBillingReference(org.meveo.model.billing.Invoice source, CreditNote target){
		source.getLinkedInvoices().forEach(linInv -> {
			BillingReference billingReference = setBillingReference(linInv.getLinkedInvoiceValue());
			target.getBillingReferences().add(billingReference);
		});
	}
	private BillingReference setBillingReference(org.meveo.model.billing.Invoice source){
		BillingReference billingReference = objectFactoryCommonAggrement.createBillingReference();
		DocumentReferenceType documentReferenceType = objectFactoryCommonAggrement.createDocumentReferenceType();
		ID id = objectFactorycommonBasic.createID();
		id.setValue(source.getInvoiceNumber());
		documentReferenceType.setID(id);
		billingReference.setInvoiceDocumentReference(documentReferenceType);
		return billingReference;
	}
	
	private static void setTaxCurrencyCodeAndDocumentCurrencyCode(oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ObjectFactory objectFactorycommonBasic,
	                                                                        org.meveo.model.billing.Invoice source,
	                                                                  Invoice target){
		TaxCurrencyCode taxCurrencyCode = objectFactorycommonBasic.createTaxCurrencyCode();
		DocumentCurrencyCode documentCurrencyCode = objectFactorycommonBasic.createDocumentCurrencyCode();
		getTaxCurrencyCodeAndDocumentCurrencyCode(source, taxCurrencyCode, documentCurrencyCode);
		target.setDocumentCurrencyCode(documentCurrencyCode);
		target.setTaxCurrencyCode(taxCurrencyCode);
	}
	private static void setTaxCurrencyCodeAndDocumentCurrencyCode(oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ObjectFactory objectFactorycommonBasic,
	                                                              org.meveo.model.billing.Invoice source,
	                                                              CreditNote target){
		TaxCurrencyCode taxCurrencyCode = objectFactorycommonBasic.createTaxCurrencyCode();
		DocumentCurrencyCode documentCurrencyCode = objectFactorycommonBasic.createDocumentCurrencyCode();
		getTaxCurrencyCodeAndDocumentCurrencyCode(source, taxCurrencyCode, documentCurrencyCode);
		target.setDocumentCurrencyCode(documentCurrencyCode);
		target.setTaxCurrencyCode(taxCurrencyCode);
	}
	
	private static void getTaxCurrencyCodeAndDocumentCurrencyCode(org.meveo.model.billing.Invoice source,  TaxCurrencyCode taxCurrencyCode, DocumentCurrencyCode documentCurrencyCode){
		if(source.getTradingCurrency() != null && source.getTradingCurrency().getCurrencyCode() != null){
			taxCurrencyCode.setValue(source.getTradingCurrency().getCurrencyCode());
			documentCurrencyCode.setValue(source.getTradingCurrency().getCurrencyCode());
		}else if(source.getBillingAccount() != null && source.getBillingAccount().getTradingCurrency() != null && source.getBillingAccount().getTradingCurrency().getCurrencyCode() != null){
			taxCurrencyCode.setValue(source.getBillingAccount().getTradingCurrency().getCurrencyCode());
			documentCurrencyCode.setValue(source.getBillingAccount().getTradingCurrency().getCurrencyCode());
		}
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
	
	
	private TaxCategoryType setTaxCategory(TaxInvoiceAgregate taxInvoiceAgregate) {
		TaxCategoryType taxCategoryType = objectFactoryCommonAggrement.createTaxCategoryType();
		ID id = objectFactorycommonBasic.createID();
		id.setSchemeID("UN/ECE 5305");
		id.setSchemeAgencyID("6");
		id.setValue("E");
		taxCategoryType.setID(id);
		
		Percent percent = objectFactorycommonBasic.createPercent();
		percent.setValue(taxInvoiceAgregate.getTaxPercent());
		taxCategoryType.setPercent(percent);
		
		Tax tax = taxInvoiceAgregate.getTax();
		if(tax.getUntdidTaxationCategory() != null) {
			UntdidTaxationCategory untdidTaxationCategory = tax.getUntdidTaxationCategory();
			TaxExemptionReason taxExemptionReason = objectFactorycommonBasic.createTaxExemptionReason();
			taxExemptionReason.setValue(untdidTaxationCategory.getSemanticModel());
			taxCategoryType.getTaxExemptionReasons().add(taxExemptionReason);
			if(!untdidTaxationCategory.getCode().equalsIgnoreCase("S")) {
				TaxExemptionReasonCode taxExemptionReasonCode = objectFactorycommonBasic.createTaxExemptionReasonCode();
				taxExemptionReasonCode.setListID("CEF VATEX");
				taxExemptionReasonCode.setListAgencyID("ZZZ");
				taxExemptionReasonCode.setValue("VATEX-EU-O");
				taxCategoryType.setTaxExemptionReasonCode(taxExemptionReasonCode);
			}
		}
		
		TaxScheme taxScheme = objectFactoryCommonAggrement.createTaxScheme();
		TaxTypeCode taxTypeCode = objectFactorycommonBasic.createTaxTypeCode();
		taxTypeCode.setValue(taxInvoiceAgregate.getTax().getCode());
		taxScheme.setTaxTypeCode(taxTypeCode);
		taxCategoryType.setTaxScheme(taxScheme);
		return taxCategoryType;
	}
	
	private static MonetaryTotalType setTaxExclusiveAmount(String currency, BigDecimal amountWithoutTax, BigDecimal amountWithTax) {
		MonetaryTotalType moneyTotalType = objectFactoryCommonAggrement.createMonetaryTotalType();
		TaxInclusiveAmount taxInclusiveAmount = objectFactorycommonBasic.createTaxInclusiveAmount();
		taxInclusiveAmount.setCurrencyID(currency);
		taxInclusiveAmount.setValue(amountWithTax);
		moneyTotalType.setTaxInclusiveAmount(taxInclusiveAmount);
		
		TaxExclusiveAmount taxExclusiveAmount = objectFactorycommonBasic.createTaxExclusiveAmount();
		taxExclusiveAmount.setCurrencyID(currency);
		taxExclusiveAmount.setValue(amountWithoutTax);
		moneyTotalType.setTaxExclusiveAmount(taxExclusiveAmount);
		
		return moneyTotalType;
		
	}
}
