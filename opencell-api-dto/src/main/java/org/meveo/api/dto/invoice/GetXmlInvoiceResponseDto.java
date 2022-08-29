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

package org.meveo.api.dto.invoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetXmlInvoiceResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetXmlInvoiceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetXmlInvoiceResponseDto extends BaseResponse {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The xml content. */
    private String xmlContent;
    
    /** The xml FileName. */
    private String xmlFileName;

    /**
     * Gets the xml FileName.
     *
     * @return the xmlFileName
     */
    public String getXmlFileName() {
        return xmlFileName;
    }

    /**
     * Sets the xml FileName.
     *
     * @param xmlFileName the xmlFileName to set
     */
    public void setXmlFileName(String xmlFileName) {
        this.xmlFileName = xmlFileName;
    }

    /**
     * Instantiates a new gets the xml invoice response dto.
     */
    public GetXmlInvoiceResponseDto() {

    }

    /**
     * Gets the xml content.
     *
     * @return the xmlContent
     */
    public String getXmlContent() {
        return xmlContent;
    }

    /**
     * Sets the xml content.
     *
     * @param xmlContent the xmlContent to set
     */
    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    @Override
    public String toString() {
        return "GetXmlInvoiceResponseDto [xmlContent=" + xmlContent + ", xmlFileName=" + xmlFileName + "]";
    }

}