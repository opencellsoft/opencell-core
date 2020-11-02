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
package org.meveo.service.billing.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.Invoice;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Handles the logic of XML invoice generation
 * 
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @author Wassim Drira
 * @author Mounir Bahije
 * @author Said Ramli
 * @author Abdellatif BARI
 * @author Mounir Bahije
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 **/
@Stateless
public class XMLInvoiceCreator extends PersistenceService<Invoice> {

    @Inject
    @DefaultXmlInvoiceCreatorScript
    private IXmlInvoiceCreatorScript defaultXmlInvoiceCreatorScript;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private InvoiceService invoiceService;

    /**
     * @param invoice invoice used to create xml
     * @param isVirtual true/false (true for Quote/order)
     * @return generated xml file
     * @throws BusinessException business exception
     */
    public File createXMLInvoice(Invoice invoice, boolean isVirtual) throws BusinessException {

        invoice = this.retrieveIfNotManaged(invoice);

        String xmlFileName = invoiceService.getOrGenerateXmlFilename(invoice);
        String fullXmlFilePath = invoiceService.getFullXmlFilePath(invoice, true);

        log.debug("Creating xml for invoice id={} number={}.", invoice.getId(), invoice.getInvoiceNumberOrTemporaryNumber());

        try {

            File file = null;
            ScriptInstance scriptInstance = invoice.getInvoiceType().getCustomInvoiceXmlScriptInstance();
            if (scriptInstance != null) {
                String invoiceXmlScript = scriptInstance.getCode();
                ScriptInterface script = scriptInstanceService.getScriptInstance(invoiceXmlScript);

                if (script instanceof IXmlInvoiceCreatorScript) {

                    file = ((IXmlInvoiceCreatorScript) script).createDocumentAndFile(invoice, isVirtual, fullXmlFilePath);

                } else {

                    Map<String, Object> methodContext = new HashMap<String, Object>();
                    methodContext.put(Script.CONTEXT_ENTITY, invoice);
                    methodContext.put(Script.CONTEXT_CURRENT_USER, currentUser);
                    methodContext.put(Script.CONTEXT_APP_PROVIDER, appProvider);
                    methodContext.put("isVirtual", Boolean.valueOf(isVirtual));
                    methodContext.put("XMLInvoiceCreator", this);
                    if (script != null) {
                        script.execute(methodContext);
                    }
                    file = (File) methodContext.get(Script.RESULT_VALUE);

                }

            } else {
                file = defaultXmlInvoiceCreatorScript.createDocumentAndFile(invoice, isVirtual, fullXmlFilePath);
            }

            if (file != null) {
                invoice.setXmlFilename(xmlFileName);
            }
            return file;

        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new BusinessException("Failed to create xml file for invoice id=" + invoice.getId() + " number=" + invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : invoice.getTemporaryInvoiceNumber(),
                e);
        }

    }

    /**
     * Store invoice XML DOM into a file.
     * 
     * Deprecated in v11. Use XmlInvoiceCreatorScript.createAndSetFileOnInvoice() instead
     * 
     * @param doc DOM invoice
     * @param invoice invoice used to build xml
     * @return xml file
     * @throws BusinessException business exception
     */
    @Deprecated
    public File createFile(Document doc, Invoice invoice) throws BusinessException {

        String xmlFileName = invoiceService.getOrGenerateXmlFilename(invoice);
        String fullXmlFilePath = invoiceService.getFullXmlFilePath(invoice, true);

        File file = ((XmlInvoiceCreatorScript) defaultXmlInvoiceCreatorScript).createFile(doc, invoice, fullXmlFilePath);
        if (file != null) {
            invoice.setXmlFilename(xmlFileName);
        }

        return file;
    }

    /**
     * Create Invoice XML document
     * 
     * Deprecated in v11. Use XmlInvoiceCreatorScript.createDocument() instead
     *
     * @param invoice invoice used to create xml
     * @param isVirtual true/false
     * @return xml DOM document
     * @throws BusinessException business exception
     * @throws ParserConfigurationException parsing exception
     * @throws SAXException sax exception
     * @throws IOException IO exception
     *
     * @author akadid abdelmounaim
     */
    @Deprecated
    public Document createDocument(Invoice invoice, boolean isVirtual) throws BusinessException, ParserConfigurationException, SAXException, IOException {

        return ((XmlInvoiceCreatorScript) defaultXmlInvoiceCreatorScript).createDocument(invoice, isVirtual);
    }
}