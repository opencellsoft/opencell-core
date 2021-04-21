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
package org.meveo.admin.job;

import java.io.File;
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
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.Invoice;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.JRParameter;

/**
 * @author Wassim Drira
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 *
 */
@Stateless
public class PDFParametersConstruction {

    private Logger log = LoggerFactory.getLogger(PDFParametersConstruction.class);

    @Inject
    protected CustomFieldTemplateService customFieldTemplateService;

    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    private String PDF_DIR_NAME = "pdf";
    private NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("FR"));

    public Map<String, Object> constructParameters(Invoice invoice, String provider) {

        try {
            Map<String, Object> parameters = new HashMap<>();
            URL resource = PDFParametersConstruction.class.getClassLoader().getResource("reports/fonts.jar");
            if (resource != null) {
                ClassLoader cl = new URLClassLoader(new URL[] { resource });
                parameters.put(JRParameter.REPORT_CLASS_LOADER, cl);
            } else {
                log.error("the reports/fonts.jar not found");
            }
            currencyFormat.setMinimumFractionDigits(2);

            BillingAccount billingAccount = invoice.getBillingAccount();
            BillingCycle billingCycle = null;
            if (billingAccount != null && billingAccount.getBillingCycle() != null) {
                billingCycle = billingAccount.getBillingCycle();
            }

            String billingTemplateName = invoiceService.getInvoiceTemplateName(invoice, billingCycle, invoice.getInvoiceType());

            ParamBean paramBean = paramBeanFactory.getInstance();
            String resDir = paramBean.getChrootDir(provider) + File.separator + "jasper";
            String templateDir = new StringBuilder(resDir).append(File.separator).append(billingTemplateName).append(File.separator).append(PDF_DIR_NAME).toString();
            parameters.put(PdfGeneratorConstants.MESSAGE_PATH_KEY, templateDir + File.separator);
            parameters.put(PdfGeneratorConstants.LOGO_PATH_KEY, templateDir + File.separator);
            parameters.put(PdfGeneratorConstants.CUSTOMER_ADDRESS_KEY, getCustomerAddress(invoice));
            parameters.put(PdfGeneratorConstants.SUBREPORT_DIR, templateDir);

            parameters.put(PdfGeneratorConstants.INVOICE_NUMBER_KEY, invoice.getInvoiceNumber());
            parameters.put(PdfGeneratorConstants.BILLING_TEMPLATE, billingTemplateName);
            parameters.put(PdfGeneratorConstants.BILLING_ACCOUNT, billingAccount);
            if (billingAccount != null) {
                parameters.put(PdfGeneratorConstants.CUSTOMER_ACCOUNT, billingAccount.getCustomerAccount());
            }
            parameters.put(PdfGeneratorConstants.INVOICE, invoice);
            Map<String, String> baCustomFields = getBACustomFields(billingAccount);
            for (String key : baCustomFields.keySet()) {
                parameters.put(key, baCustomFields.get(key));
            }

            parameters.put(JRParameter.REPORT_LOCALE, getLocal(invoice));

            return parameters;
        } catch (Exception e) {
            log.error("failed to construct parameters ", e);
            return null;
        }
    }

    private Map<String, String> getBACustomFields(BillingAccount billingAccount) {
        Map<String, CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAppliesTo(billingAccount);
        Map<String, String> customFields = new HashMap<String, String>();
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
        CustomerAccount customerAccount = invoice.getBillingAccount().getCustomerAccount();
        String name = "";
        if (customerAccount.getName() != null) {
            name = "";

            if (customerAccount.getName().getTitle() != null) {
                String descTranslated = customerAccount.getName().getTitle().getDescriptionOrCode();
                if (customerAccount.getName().getTitle().getDescriptionI18n() != null
                        && customerAccount.getName().getTitle().getDescriptionI18n().containsKey(billingAccountLanguage)) {
                    descTranslated = customerAccount.getName().getTitle().getDescriptionI18n().get(billingAccountLanguage);
                }
                name = descTranslated + " ";
            }

            name += customerAccount.getName().getFirstName() == null ? "" : (customerAccount.getName().getFirstName() + " ");
            name += customerAccount.getName().getLastName() == null ? "" : customerAccount.getName().getLastName();
        }
        String address = "";
        if (customerAccount.getAddress() != null) {
            address = customerAccount.getAddress().getAddress1() == null ? "" : (customerAccount.getAddress().getAddress1() + "\n");
            address += customerAccount.getAddress().getAddress2() == null ? "" : (customerAccount.getAddress().getAddress2() + "\n");
            address += customerAccount.getAddress().getAddress3() == null ? "" : (customerAccount.getAddress().getAddress3() + "\n");
            address += customerAccount.getAddress().getZipCode() == null ? "" : (customerAccount.getAddress().getZipCode() + " ");
            address += customerAccount.getAddress().getCity() == null ? "" : (customerAccount.getAddress().getCity());
        }
        return (name + "\n" + address);
    }

    /**
     * Gets the locale value of the billing account
     *
     * @param invoice the invoice
     * @return the locale value of the billing account
     */
    private Locale getLocal(Invoice invoice) {

        if (invoice != null && invoice.getBillingAccount() != null) {
            BillingAccount billingAccount = invoice.getBillingAccount();
            String languageCode = billingAccount.getTradingLanguage() != null ? billingAccount.getTradingLanguage().getLanguageCode() : null;
            if (languageCode == null) {
                return Locale.getDefault();
            }

            String country = billingAccount.getTradingCountry() != null ? billingAccount.getTradingCountry().getCountryCode() : "";

            String[] languages = Locale.getISOLanguages();
            for (String language : languages) {
                Locale locale = new Locale(language, country);
                if (locale.getISO3Language().toUpperCase().equals(languageCode)) {
                    return locale;
                }
            }
        }
        return Locale.getDefault();
    }
}
