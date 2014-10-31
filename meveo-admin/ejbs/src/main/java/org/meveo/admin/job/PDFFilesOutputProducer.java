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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import org.meveo.commons.exceptions.ConfigurationException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.Invoice;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.billing.impl.InvoiceService;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

@Stateless @LocalBean
public class PDFFilesOutputProducer{

    private static final String PDF_DIR_NAME = "pdf";

    private static final String INVOICE_TEMPLATE_FILENAME = "invoice.jasper";

    private Logger logger = Logger.getLogger(PDFFilesOutputProducer.class.getName());
    
    private static String DATE_PATERN = "yyyy.MM.dd";
    
    @EJB
	private InvoiceService invoiceService;


    /**
     * @see org.meveo.core.outputproducer.OutputProducer#produceOutput(java.util.List)
     */
     public void producePdf(Map<String, Object> parameters) throws Exception{

		    	ParamBean paramBean = ParamBean.getInstance();
		    	logger.info("PDFInvoiceGenerationJob is invoice key exists="+parameters!=null?parameters.containsKey(PdfGenratorConstants.INVOICE)+"":"parameters is null");
		    	Invoice invoice=(Invoice)parameters.get(PdfGenratorConstants.INVOICE);
		    	String meveoDir= paramBean.getProperty("providers.rootDir", "/tmp/meveo/")+ File.separator + invoice.getProvider().getCode()
						+ File.separator;
		        String pdfDirectory =meveoDir+"invoices"+ File.separator+"pdf" + File.separator ;

		        (new File(pdfDirectory)).mkdirs();
		        String INVOICE_TAG_NAME = "invoice";

			    File billingRundir = new File(meveoDir +"invoices"+File.separator+"xml"+File.separator+invoice.getBillingRun().getId());
		        String invoiceXmlFileName=billingRundir + File.separator + invoice.getInvoiceNumber()+ ".xml";
		        File invoiceXmlFile=new File(invoiceXmlFileName);
		        if(!invoiceXmlFile.exists()){
		        	throw new ConfigurationException("The xml invoice file doesn't exist");
		        }
          	    BillingCycle billingCycle = invoice.getBillingRun().getBillingCycle();
                BillingAccount billingAccount=invoice.getBillingAccount();
                String billingTemplate =billingCycle!=null && billingCycle.getBillingTemplateName()!=null?
                      billingCycle.getBillingTemplateName():"default";
                String resDir = meveoDir +"jasper";
      		    File jasperFile = getJasperTemplateFile(resDir, billingTemplate, billingAccount.getPaymentMethod());
                logger.info(String.format("Jasper template used: %s", jasperFile.getCanonicalPath()));
                InputStream reportTemplate = new FileInputStream(jasperFile);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document xmlDocument = db.parse(invoiceXmlFile);
                xmlDocument.getDocumentElement().normalize(); // TODO check this out
                Node invoiceNode =xmlDocument.getElementsByTagName(INVOICE_TAG_NAME).item(0);
                JRXmlDataSource dataSource = new JRXmlDataSource(new ByteArrayInputStream(getNodeXmlString(invoiceNode).getBytes()),
                        "/invoice/detail/userAccounts/userAccount/categories/category/subCategories/subCategory/line");
                JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportTemplate);
                JasperPrint jasperPrint = JasperFillManager
                        .fillReport(jasperReport, parameters, dataSource);
                String pdfFileName = getNameWoutSequence(pdfDirectory, invoice.getInvoiceDate(), invoice.getInvoiceNumber())+".pdf";
                JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFileName);
                logger.info(String.format("PDF file '%s' produced", pdfFileName));
                FileInputStream fileInputStream=null;
                try {
                    File file = new File(pdfFileName);
                    long fileSize = file.length();
                    if (fileSize > Integer.MAX_VALUE) {
                        throw new IllegalArgumentException("File is too big to put it to buffer in memory");
                    }
                    byte[] fileBytes = new byte[(int)file.length()];
                    fileInputStream = new FileInputStream(file);
                    fileInputStream.read(fileBytes);
                    invoice.setPdf(fileBytes);
                    invoiceService.update(invoice);
                } catch (Exception e) {
                    logger.severe("Error handling file.");
                    throw new ConfigurationException("Error saving file to DB as blob.");
                } finally {
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {
                            logger.severe("Error closing file input stream.");
                        }
                    }
                }
                
    }
    
    private File getJasperTemplateFile(String resDir, String billingTemplate, PaymentMethodEnum paymentMethod) {
        String pdfDirName = new StringBuilder(resDir).append(File.separator).append(billingTemplate).append(File.separator).append(PDF_DIR_NAME).toString();
        File pdfDir = new File(pdfDirName);
        String paymentMethodFileName = new StringBuilder("invoice_").append(paymentMethod).append(".jasper").toString();
        File paymentMethodFile = new File(pdfDir, paymentMethodFileName);
        if (paymentMethodFile.exists()) {
            return paymentMethodFile;
        } else {
            File defaultTemplate = new File(pdfDir, INVOICE_TEMPLATE_FILENAME);
            return defaultTemplate;
        }
        
    }
    
    protected String getNodeXmlString(Node node) {
        try {
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            StringWriter buffer = new StringWriter();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(node),
                  new StreamResult(buffer));
            return buffer.toString();
        } catch (Exception e) {
            logger.severe("Error converting xml node to its string representation");
            e.printStackTrace();
            throw new ConfigurationException();
        }
    }
    
    public static String getNameWoutSequence(String tempDir, Date invoiceDate, String invoiceNumber) {
        return new StringBuilder(tempDir).append(File.separator).append(
                formatInvoiceDate(invoiceDate)).append("_").append(invoiceNumber).toString();
    }
    public static String formatInvoiceDate(Date invoiceDate) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_PATERN);
        return dateFormat.format(invoiceDate);
    }


}