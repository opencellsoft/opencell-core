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

  
    @SuppressWarnings("deprecation")
	public Map<String, Object> constructParameters(Invoice invoice) {
        try {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put(JRParameter.REPORT_CLASS_LOADER, cl);
            
            BillingCycle billingCycle = invoice.getBillingRun().getBillingCycle();
            BillingAccount billingAccount=invoice.getBillingAccount();
            Provider provider=invoice.getProvider();
            String billingTemplateName =billingCycle!=null && billingCycle.getBillingTemplateName()!=null?
                    billingCycle.getBillingTemplateName():"default";

    		ParamBean paramBean = ParamBean.getInstance();
	        String meveoDir = paramBean.getProperty("meveo.dir","/tmp/meveo");
            String resDir = meveoDir + File.separator +provider.getCode()+ File.separator +"jasper";
            String templateDir = new StringBuilder(resDir).append(File.separator).append(
                    billingTemplateName).append(File.separator).append(PDF_DIR_NAME).toString();
            parameters.put(PdfGenratorConstants.MESSAGE_PATH_KEY, templateDir+File.separator);
            parameters.put(PdfGenratorConstants.LOGO_PATH_KEY, templateDir+File.separator);
            parameters.put(PdfGenratorConstants.CUSTOMER_ADDRESS_KEY, getCustomerAddress(invoice));
            parameters.put(PdfGenratorConstants.SUBREPORT_DIR, templateDir);
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