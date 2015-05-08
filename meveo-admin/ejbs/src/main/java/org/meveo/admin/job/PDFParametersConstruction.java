/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.job;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRParameter;

import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.TIP;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class PDFParametersConstruction {

	private Logger log = LoggerFactory
			.getLogger(PDFParametersConstruction.class);
    @Inject
    private CatMessagesService catMessagesService;
    
    @Inject
    private InvoiceService invoiceService;
    
	@Inject
	protected CustomFieldTemplateService customFieldTemplateService;
    
	private static final String TIP_PAYMENT_METHOD = "TIP";
	private static final String PDF_DIR_NAME = "pdf";
	private static NumberFormat currencyFormat = NumberFormat
			.getInstance(new Locale("FR"));
	static {
		currencyFormat.setMinimumFractionDigits(2);
	}

	private ClassLoader cl = new URLClassLoader(
			new URL[] { PDFParametersConstruction.class.getClassLoader()
					.getResource("reports/fonts.jar") });

	public Map<String, Object> constructParameters(Invoice invoice) {
		return constructParameters(invoice.getId());
	}
	
	@SuppressWarnings("deprecation")
	public Map<String, Object> constructParameters(Long invoiceId) {
		try {
			Invoice invoice = invoiceService.findById(invoiceId);
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(JRParameter.REPORT_CLASS_LOADER, cl);

			BillingCycle billingCycle = invoice.getBillingRun()
					.getBillingCycle();
			BillingAccount billingAccount = invoice.getBillingAccount();
			Provider provider = invoice.getProvider();
			String billingTemplateName = billingCycle != null
					&& billingCycle.getBillingTemplateName() != null ? billingCycle
					.getBillingTemplateName() : "default";

			ParamBean paramBean = ParamBean.getInstance();
			String meveoDir = paramBean.getProperty("providers.rootDir",
					"/tmp/meveo");
			String resDir = meveoDir + File.separator + provider.getCode()
					+ File.separator + "jasper";
			String templateDir = new StringBuilder(resDir)
					.append(File.separator).append(billingTemplateName)
					.append(File.separator).append(PDF_DIR_NAME).toString();
			parameters.put(PdfGeneratorConstants.MESSAGE_PATH_KEY, templateDir
					+ File.separator);
			parameters.put(PdfGeneratorConstants.LOGO_PATH_KEY, templateDir
					+ File.separator);
			parameters.put(PdfGeneratorConstants.CUSTOMER_ADDRESS_KEY,
					getCustomerAddress(invoice));
			parameters.put(PdfGeneratorConstants.SUBREPORT_DIR, templateDir);
			if (TIP_PAYMENT_METHOD.equals(billingAccount.getPaymentMethod()
					.toString())) {
				BigDecimal netToPay = invoice.getNetToPay();
				if (netToPay.signum() != 1) {
					parameters.put(PdfGeneratorConstants.HIGH_OPTICAL_LINE_KEY,
							" ");
					parameters.put(PdfGeneratorConstants.LOW_OPTICAL_LINE_KEY,
							" ");
				} else {
					BankCoordinates bankCoordinates = billingAccount
							.getBankCoordinates();
					if (bankCoordinates == null
							|| bankCoordinates.getBankCode() == null) {
						BankCoordinates bankCoordinatesEmpty = new BankCoordinates();
						bankCoordinatesEmpty.setAccountNumber("           ");
						bankCoordinatesEmpty.setBankCode("     ");
						bankCoordinatesEmpty.setBranchCode("     ");
						bankCoordinatesEmpty.setKey("  ");
						TIP tip = new TIP(provider.getInterBankTitle()
								.getCodeCreancier(), provider
								.getInterBankTitle()
								.getCodeEtablissementCreancier(), provider
								.getInterBankTitle().getCodeCentre(),
								bankCoordinatesEmpty, billingAccount
										.getCustomerAccount().getCode(),
								invoice.getId(), invoice.getInvoiceDate(),
								invoice.getDueDate(), netToPay);
						parameters.put(
								PdfGeneratorConstants.HIGH_OPTICAL_LINE_KEY,
								tip.getLigneOptiqueHaute());
						parameters.put(
								PdfGeneratorConstants.LOW_OPTICAL_LINE_KEY,
								tip.getLigneOptiqueBasse());
					} else {
						TIP tip = new TIP(provider.getInterBankTitle()
								.getCodeCreancier(), provider
								.getInterBankTitle()
								.getCodeEtablissementCreancier(), provider
								.getInterBankTitle().getCodeCentre(),
								bankCoordinates, billingAccount
										.getCustomerAccount().getCode(),
								invoice.getId(), invoice.getInvoiceDate(),
								invoice.getDueDate(), netToPay);
						parameters.put(
								PdfGeneratorConstants.HIGH_OPTICAL_LINE_KEY,
								tip.getLigneOptiqueHaute());
						parameters.put(
								PdfGeneratorConstants.LOW_OPTICAL_LINE_KEY,
								tip.getLigneOptiqueBasse());
					}
				}
			}

			parameters.put(PdfGeneratorConstants.INVOICE_NUMBER_KEY,
					invoice.getInvoiceNumber());
			parameters.put(PdfGeneratorConstants.BILLING_TEMPLATE,
					billingTemplateName);
			parameters
					.put(PdfGeneratorConstants.BILLING_ACCOUNT, billingAccount);
			parameters.put(PdfGeneratorConstants.CUSTOMER_ACCOUNT,
					billingAccount.getCustomerAccount());
			parameters.put(PdfGeneratorConstants.INVOICE, invoice);
			Map<String, String> baCustomFields=getBACustomFields(billingAccount);
			for(String key:baCustomFields.keySet()){
				parameters.put(key,baCustomFields.get(key));
			}
			

			return parameters;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}
	
	private Map<String, String> getBACustomFields(
			BillingAccount billingAccount) {
		List<CustomFieldTemplate> customFieldTemplates = customFieldTemplateService
				.findByAccountLevel(AccountLevelEnum.BA,billingAccount.getProvider());
		Map<String, String>  customFields = new HashMap<String, String> ();
		if (customFieldTemplates != null && customFieldTemplates.size() > 0) {
			for (CustomFieldTemplate cf : customFieldTemplates) {

				CustomFieldInstance cfi = billingAccount.getCustomFields().get(
						cf.getCode());
				if (cfi != null) {
					if (cf.getFieldType() == CustomFieldTypeEnum.DATE) {
						Date dateField = billingAccount
								.getInheritedCustomDateValue(cf.getCode());
						if (dateField != null) {
							customFields.put(cf.getCode(), DateUtils.formatDateWithPattern(dateField,
									"MM-dd-yyyy"));
						}
					} else if (cf.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
						Double doubleField = billingAccount
								.getInheritedCustomDoubleValue(cf.getCode());
						if (doubleField != null) {
							customFields.put(cf.getCode(), String.valueOf(doubleField));
						}
					} else if (cf.getFieldType() == CustomFieldTypeEnum.LONG) {
						Long longField = billingAccount
								.getInheritedCustomLongValue(cf.getCode());
						if (longField != null) {
							customFields.put(cf.getCode(), 	String.valueOf(longField));
						}
					} else if (cf.getFieldType() == CustomFieldTypeEnum.STRING
							|| cf.getFieldType() == CustomFieldTypeEnum.LIST) {
						String stringField = billingAccount
								.getInheritedCustomStringValue(cf.getCode());
						if (!StringUtils.isBlank(stringField)) {
							customFields.put(cf.getCode(), 	stringField);
						}}
				}}}
		return customFields;
	}

	public String getCustomerAddress(Invoice invoice) {

	    String billingAccountLanguage = invoice.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode();
		CustomerAccount customerAccount = invoice.getBillingAccount()
				.getCustomerAccount();
		String name = "";
		if (customerAccount.getName() != null) {
		    name="";
		    if (customerAccount.getName().getTitle() != null){
		        name=catMessagesService.getMessageDescription(customerAccount.getName().getTitle(), billingAccountLanguage)+" ";
		    }
		    
			name += customerAccount.getName().getFirstName() == null ? ""
					: (customerAccount.getName().getFirstName() + " ");
			name += customerAccount.getName().getLastName() == null ? ""
					: customerAccount.getName().getLastName();
		}
		String address = "";
		if (customerAccount.getAddress() != null) {
			address = customerAccount.getAddress().getAddress1() == null ? ""
					: (customerAccount.getAddress().getAddress1() + "\n");
			address += customerAccount.getAddress().getAddress2() == null ? ""
					: (customerAccount.getAddress().getAddress2() + "\n");
			address += customerAccount.getAddress().getAddress3() == null ? ""
					: (customerAccount.getAddress().getAddress3() + "\n");
			address += customerAccount.getAddress().getZipCode() == null ? ""
					: (customerAccount.getAddress().getZipCode() + " ");
			address += customerAccount.getAddress().getCity() == null ? ""
					: (customerAccount.getAddress().getCity());
		}
		return (name + "\n" + address);
	}

}