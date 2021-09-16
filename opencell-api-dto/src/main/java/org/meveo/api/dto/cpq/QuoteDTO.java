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

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO to create or update a quote
 * 
 * @author Rachid.AIT
 * @lastModiedVersion 11.0 
 */
@XmlRootElement(name = "QuoteDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class QuoteDTO extends BaseQuoteDTO{

    /**
	 * 
	 */
	private static final long serialVersionUID = 8115890992793236496L;
	
	
    private QuoteVersionDto quoteVersion;

    @Schema(description = "The name of the pdf file")
    private String pdfFilename;

	
	/** Discount plan code */
    @Schema(description = "The code of the discount plan")
	private String discountPlanCode;
	/**
	 * @return the quoteVersion
	 */
	public QuoteVersionDto getQuoteVersion() {
		return quoteVersion;
	}


	/**
	 * @param quoteVersion the quoteVersion to set
	 */
	public void setQuoteVersion(QuoteVersionDto quoteVersion) {
		this.quoteVersion = quoteVersion;
	}


	/**
	 * @return the discountPlanCode
	 */
	public String getDiscountPlanCode() {
		return discountPlanCode;
	}


	/**
	 * @param discountPlanCode the discountPlanCode to set
	 */
	public void setDiscountPlanCode(String discountPlanCode) {
		this.discountPlanCode = discountPlanCode;
	}


	/**
	 * @return the pdfFilename
	 */
	public String getPdfFilename() {
		return pdfFilename;
	}


	/**
	 * @param pdfFilename the pdfFilename to set
	 */
	public void setPdfFilename(String pdfFilename) {
		this.pdfFilename = pdfFilename;
	}
	
	

   
	
    
   
}