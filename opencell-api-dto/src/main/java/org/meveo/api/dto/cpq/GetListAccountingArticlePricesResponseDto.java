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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.SearchResponse;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;

/**
 * The Class GetListOfferTemplateResponseDto.
 * 
 * @author Rachid.AIT
 */
@XmlRootElement(name = "GetListAccountingArticlePricesResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListAccountingArticlePricesResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7514761440320813919L;
	
	/** accountingArticle Prices. */
    @XmlElementWrapper(name = "accountingArticlePrices")
    @XmlElement(name = "accountingArticlePrices")
    private List<AccountingArticlePricesDTO> accountingArticlePrices;
    
    /** total Prices. */
    @XmlElementWrapper(name = "totalPrices")
    @XmlElement(name = "totalPrices")
    private List<PriceDTO> totalPrices;
    
    /**
     * Discount item code
     */
    
    private String discountPlanItemCode;
    
    
    /**
     * Discount item type
     */
    
    private DiscountPlanItemTypeEnum discountPlanItemType = DiscountPlanItemTypeEnum.PERCENTAGE;
    
    /**
     * Discount value
     */
    private String discountValue;

    /**
     * Instantiates a new gets the list offer template response dto.
     */
    public GetListAccountingArticlePricesResponseDto() {

    }

	public List<AccountingArticlePricesDTO> getAccountingArticlePrices() {
		return accountingArticlePrices;
	}

	public void setAccountingArticlePrices(List<AccountingArticlePricesDTO> accountingArticlePrices) {
		this.accountingArticlePrices = accountingArticlePrices;
	}

	/**
	 * @return the totalPrices
	 */
	public List<PriceDTO> getTotalPrices() {
		return totalPrices;
	}

	/**
	 * @param totalPrices the totalPrices to set
	 */
	public void setTotalPrices(List<PriceDTO> totalPrices) {
		this.totalPrices = totalPrices;
	}

	/**
	 * @return the discountPlanItemCode
	 */
	public String getDiscountPlanItemCode() {
		return discountPlanItemCode;
	}

	/**
	 * @param discountPlanItemCode the discountPlanItemCode to set
	 */
	public void setDiscountPlanItemCode(String discountPlanItemCode) {
		this.discountPlanItemCode = discountPlanItemCode;
	}

	/**
	 * @return the discountPlanItemType
	 */
	public DiscountPlanItemTypeEnum getDiscountPlanItemType() {
		return discountPlanItemType;
	}

	/**
	 * @param discountPlanItemType the discountPlanItemType to set
	 */
	public void setDiscountPlanItemType(DiscountPlanItemTypeEnum discountPlanItemType) {
		this.discountPlanItemType = discountPlanItemType;
	}

	/**
	 * @return the discountValue
	 */
	public String getDiscountValue() {
		return discountValue;
	}

	/**
	 * @param discountValue the discountValue to set
	 */
	public void setDiscountValue(String discountValue) {
		this.discountValue = discountValue;
	}

   
}