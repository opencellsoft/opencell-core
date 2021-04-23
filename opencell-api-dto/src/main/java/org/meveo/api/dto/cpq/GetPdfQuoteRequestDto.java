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

package org.meveo.api.dto.cpq;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

/**
 * The Class GetPdfInvoiceRequestDto.
 * 
 * @author Mbarek-Ay
 */
@XmlRootElement(name = "GetPdfQuoteRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetPdfQuoteRequestDto extends BaseEntityDto {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The quote number. */
    private String quoteNumber;
     
    
    /** The quote code. */
    private String code;
    
    /** The generate pdf. */
    private Boolean generatePdf = Boolean.FALSE;

    /**
     * Instantiates a new gets the pdf quote request dto.
     */
    public GetPdfQuoteRequestDto() {

    }
  
    /**
	 * @return the quoteNumber
	 */
	public String getQuoteNumber() {
		return quoteNumber;
	}



	/**
	 * @param quoteNumber the quoteNumber to set
	 */
	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}



	/**
	 * @return the generatePdf
	 */
	public Boolean getGeneratePdf() {
		return generatePdf;
	}



	/**
	 * @param generatePdf the generatePdf to set
	 */
	public void setGeneratePdf(Boolean generatePdf) {
		this.generatePdf = generatePdf;
	}
 
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	@Override
    public String toString() {
        return "GetPdfQuoteRequestDto [invoiceNumber=" + quoteNumber +", code=" + code +"+generatePdf=" + generatePdf + "]";
    }
}