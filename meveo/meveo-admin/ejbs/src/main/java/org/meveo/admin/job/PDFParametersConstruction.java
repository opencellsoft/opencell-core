/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.admin.job;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import net.sf.jasperreports.engine.JRParameter;

import org.meveo.commons.utils.ParamBean;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.TIP;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;

@Stateless @LocalBean
public class PDFParametersConstruction{

    private static final String TIP_PAYMENT_METHOD = "TIP";
    private static final String PDF_DIR_NAME = "pdf";
    private static NumberFormat currencyFormat =  NumberFormat.getInstance(new Locale("FR"));
    static {
	    currencyFormat.setMinimumFractionDigits(2);
    }

    private ClassLoader cl = new URLClassLoader(new URL[] { PDFParametersConstruction.class.getClassLoader().getResource(
            "reports/fonts.jar") });

  
    public Map<String, Object> constructParameters(Invoice invoice) {
        try {
        	 ParamBean paramBean = ParamBean.getInstance("meveo-admin.properties");
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put(JRParameter.REPORT_CLASS_LOADER, cl);
            
            BillingCycle billingCycle = invoice.getBillingRun().getBillingCycle();
            BillingAccount billingAccount=invoice.getBillingAccount();
            Provider provider=invoice.getProvider();
            String billingTemplateName =billingCycle!=null && billingCycle.getBillingTemplateName()!=null?
                    billingCycle.getBillingTemplateName():"default";
                    
            String resourcesFilesDirectory = paramBean.getProperty("pdfInvoiceGenrationJob.resourcesFilesDirectory");
            String messagePathKey = new StringBuilder(resourcesFilesDirectory).append(File.separator).append(
                    billingTemplateName).append(File.separator).append(PDF_DIR_NAME).append(File.separator).toString();
            
            parameters.put(PdfGenratorConstants.MESSAGE_PATH_KEY, messagePathKey);
            parameters.put(PdfGenratorConstants.LOGO_PATH_KEY, messagePathKey);
            parameters.put(PdfGenratorConstants.CUSTOMER_ADDRESS_KEY, getCustomerAddress(invoice));
            String resDir = paramBean.getProperty("pdfInvoiceGenrationJob.resourcesFilesDirectory");
            String pdfDirName = new StringBuilder(resDir).append(File.separator).append(billingTemplateName).append(File.separator).append(PDF_DIR_NAME).toString();
            parameters.put(PdfGenratorConstants.SUBREPORT_DIR, pdfDirName);
            if (TIP_PAYMENT_METHOD.equals(billingAccount.getPaymentMethod().toString())) {
            	BigDecimal netToPay = invoice.getNetToPay();
            	if(netToPay.signum() != 1){
            		parameters.put(PdfGenratorConstants.HIGH_OPTICAL_LINE_KEY," ");
                	parameters.put(PdfGenratorConstants.LOW_OPTICAL_LINE_KEY, " ");
            	}
            	else {
            	BankCoordinates bankCoordinates = billingAccount.getBankCoordinates();
            	if(bankCoordinates==null || bankCoordinates.getBankCode()==null) {
            		BankCoordinates bankCoordinatesEmpty = new BankCoordinates();
            		bankCoordinatesEmpty.setAccountNumber("           ");
            		bankCoordinatesEmpty.setBankCode("     ");
            		bankCoordinatesEmpty.setBranchCode("     ");
            		bankCoordinatesEmpty.setKey("  ");
                    TIP tip = new TIP(provider.getInterBankTitle().getCodeCreancier(), provider.getInterBankTitle().getCodeEtablissementCreancier(), 
                            provider.getInterBankTitle().getCodeCentre(), bankCoordinatesEmpty, billingAccount.getCustomerAccount().getCode(), invoice.getId(),
                            invoice.getInvoiceDate(), invoice.getDueDate(), netToPay);
                    parameters.put(PdfGenratorConstants.HIGH_OPTICAL_LINE_KEY, tip.getLigneOptiqueHaute());
                    parameters.put(PdfGenratorConstants.LOW_OPTICAL_LINE_KEY, tip.getLigneOptiqueBasse());
            	} else {
            		TIP tip = new TIP(provider.getInterBankTitle().getCodeCreancier(), provider.getInterBankTitle().getCodeEtablissementCreancier(), 
                        provider.getInterBankTitle().getCodeCentre(), bankCoordinates, billingAccount.getCustomerAccount().getCode(), invoice.getId(),
                        invoice.getInvoiceDate(), invoice.getDueDate(), netToPay);
            		parameters.put(PdfGenratorConstants.HIGH_OPTICAL_LINE_KEY, tip.getLigneOptiqueHaute());
            		parameters.put(PdfGenratorConstants.LOW_OPTICAL_LINE_KEY, tip.getLigneOptiqueBasse());
            	}
            }
            }
            
            parameters.put(PdfGenratorConstants.INVOICE_NUMBER_KEY, invoice.getInvoiceNumber());
            parameters.put(PdfGenratorConstants.BILLING_TEMPLATE, billingTemplateName);
            parameters.put(PdfGenratorConstants.BILLING_ACCOUNT, billingAccount);
            parameters.put(PdfGenratorConstants.CUSTOMER_ACCOUNT, billingAccount.getCustomerAccount());
            parameters.put(PdfGenratorConstants.INVOICE, invoice);
            
        
            return parameters;
        
        } catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
    }
    
    public String getCustomerAddress(Invoice invoice){
    	CustomerAccount customerAccount=invoice.getBillingAccount().getCustomerAccount();
    	   String name = "";
           if (customerAccount.getName() != null) {
               name = customerAccount.getName().getTitle() == null ? "" : (customerAccount.getName().getTitle()
                       .getCode() + " ");
               name += customerAccount.getName().getFirstName() == null ? "" : (customerAccount.getName()
                       .getFirstName() + " ");
               name += customerAccount.getName().getLastName() == null ? "" : customerAccount.getName().getLastName();
           }
           String address = "";
           if (customerAccount.getAddress() != null) {
               address = customerAccount.getAddress().getAddress1() == null ? "" : (customerAccount.getAddress()
                       .getAddress1() + "\n");
               address += customerAccount.getAddress().getAddress2() == null ? "" : (customerAccount.getAddress()
                       .getAddress2() + "\n");
               address += customerAccount.getAddress().getAddress3() == null ? "" : (customerAccount.getAddress()
                       .getAddress3() + "\n");
               address += customerAccount.getAddress().getZipCode() == null ? "" : (customerAccount.getAddress()
                       .getZipCode() + " ");
               address += customerAccount.getAddress().getCity() == null ? "" : (customerAccount.getAddress()
                       .getCity());
           }
           return  (name + "\n" + address);
    }

}