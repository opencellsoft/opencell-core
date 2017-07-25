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
package org.meveo.admin.job;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.commons.utils.ParamBean;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.TIP;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.TipPaymentMethod;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.JRParameter;

@Stateless
public class PDFParametersConstruction {

	private Logger log = LoggerFactory
			.getLogger(PDFParametersConstruction.class);
    @Inject
    private CatMessagesService catMessagesService;
        
	@Inject
	protected CustomFieldTemplateService customFieldTemplateService;
    
    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    @Inject
    @ApplicationProvider
    private Provider appProvider;
    
	private  String PDF_DIR_NAME = "pdf";
	private NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("FR"));
	

	private ClassLoader cl = new URLClassLoader(
			new URL[] { PDFParametersConstruction.class.getClassLoader()
					.getResource("reports/fonts.jar") });

	public Map<String, Object> constructParameters(Invoice invoice) {
		
		try {
			currencyFormat.setMinimumFractionDigits(2);
			
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(JRParameter.REPORT_CLASS_LOADER, cl);
			
			BillingAccount billingAccount = invoice.getBillingAccount();
			BillingCycle billingCycle = null;
			if (billingAccount!= null && billingAccount.getBillingCycle()!= null) {
				billingCycle=billingAccount.getBillingCycle();
			}	
			
            String billingTemplateName = InvoiceService.getInvoiceTemplateName(billingCycle, invoice.getInvoiceType());

			ParamBean paramBean = ParamBean.getInstance();
			String meveoDir = paramBean.getProperty("providers.rootDir",
					"./opencelldata");
			String resDir = meveoDir + File.separator + appProvider.getCode()
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
			
			PaymentMethod preferedPaymentMethod =  billingAccount.getCustomerAccount().getPreferredPaymentMethod();

            if (preferedPaymentMethod != null && preferedPaymentMethod instanceof TipPaymentMethod) {
				BigDecimal netToPay = invoice.getNetToPay();
				if (netToPay.signum() != 1) {
					parameters.put(PdfGeneratorConstants.HIGH_OPTICAL_LINE_KEY,
							" ");
					parameters.put(PdfGeneratorConstants.LOW_OPTICAL_LINE_KEY,
							" ");
				} else {
					BankCoordinates bankCoordinates = ((TipPaymentMethod)preferedPaymentMethod).getBankCoordinates();
					if (bankCoordinates == null
							|| bankCoordinates.getBankCode() == null) {
						BankCoordinates bankCoordinatesEmpty = new BankCoordinates();
						bankCoordinatesEmpty.setAccountNumber("           ");
						bankCoordinatesEmpty.setBankCode("     ");
						bankCoordinatesEmpty.setBranchCode("     ");
						bankCoordinatesEmpty.setKey("  ");
						TIP tip = new TIP(appProvider.getInterBankTitle()
								.getCodeCreancier(), appProvider
								.getInterBankTitle()
								.getCodeEtablissementCreancier(), appProvider
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
						TIP tip = new TIP(appProvider.getInterBankTitle()
								.getCodeCreancier(), appProvider
								.getInterBankTitle()
								.getCodeEtablissementCreancier(), appProvider
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
			log.error("failed to construct parameters ",e);
			return null;
		}
	}
	
    private Map<String, String> getBACustomFields(BillingAccount billingAccount) {
        Map<String, CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAppliesTo(billingAccount);
		Map<String, String>  customFields = new HashMap<String, String> ();
		if (customFieldTemplates != null && customFieldTemplates.size() > 0) {
			for (String cfCode : customFieldTemplates.keySet()) {

                Object cfValue = customFieldInstanceService.getInheritedCFValue(billingAccount, cfCode);
                if (cfValue != null && cfValue instanceof Date) {
                    customFields.put(cfCode, DateUtils.formatDateWithPattern((Date) cfValue, "MM-dd-yyyy"));
                } else if (cfValue != null) {
                    customFields.put(cfCode, String.valueOf(cfValue));
                }
            }
        }
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
