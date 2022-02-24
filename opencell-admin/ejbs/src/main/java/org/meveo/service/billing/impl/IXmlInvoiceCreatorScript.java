package org.meveo.service.billing.impl;

import java.io.File;
import java.io.IOException;

import javax.ejb.Local;
import javax.xml.parsers.ParserConfigurationException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.Invoice;
import org.meveo.service.script.ScriptInterface;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Interface for XML invoice creation script. Must extend ScriptInterface, so XmlInvoiceCreatorScript can be customized via a script.
 * 
 * @author Andrius Karpavicius
 *
 */
@Local
public interface IXmlInvoiceCreatorScript extends ScriptInterface {

    /**
     * Create XML invoice and store its content in a file. Note: Just creates a file - does not update invoice entity with file information
     * 
     * @param invoice Invoice to convert invoice used to create xml
     * @param isVirtual Is this a virtual invoice. If true, no invoice, invoice aggregate nor RT information is persisted in DB
     * @param fullXmlFilePath Full xml file path
     * @param rtBillingProcess invoicing process : true old process using RT, false : new process using invoiceLines
     * @return DOM element xml file
     * @throws BusinessException business exception
     * @throws ParserConfigurationException parsing exception
     * @throws SAXException sax exception
     * @throws IOException IO exception
     */
    File createDocumentAndFile(Invoice invoice, boolean isVirtual, String fullXmlFilePath, boolean rtBillingProcess) throws BusinessException, ParserConfigurationException, SAXException, IOException;
    /**
     * Create Invoice XML document as DOM
     *
     * @param invoice   Invoice to convert Invoice used to create xml
     * @param isVirtual Is this a virtual invoice. If true, no invoice, invoice aggregate nor RT information is persisted in DB
     * @return DOM element XML DOM document
     * @throws BusinessException            business exception
     * @throws ParserConfigurationException parsing exception
     * @throws SAXException                 sax exception
     * @throws IOException                  IO exception
     */
    public Document createDocument(Invoice invoice, boolean isVirtual, boolean rtBillingProcess) throws BusinessException, ParserConfigurationException, SAXException, IOException;

    /**
     * Store XML DOM into a file.
     *
     * @param doc XML invoice DOM
     * @param invoice Invoice to convert invoice used to build xml
     * @param fullXmlFilePath Full xml file path
     * @return DOM element xml file
     * @throws BusinessException business exception
     */
    public File createFile(Document doc, Invoice invoice, String fullXmlFilePath) throws BusinessException;
}
