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
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;

/**
 * The Class AccountingArticlePrices.
 *
 * @author Rachid.AIT
 * @lastModifiedVersion 11.0.0
 */
@XmlRootElement(name = "AccountingArticlePricesDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountingArticlePricesDTO extends BaseEntityDto {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

    /**
     * The accounting article Code
     */
    private String accountingArticleCode;
    
    /**
     * The accounting article label
     */
    private String accountingArticleLabel;
    
    /**
     * List of accouting article prices
     */
    private List<PriceDTO> accountingArticlePrices;
    
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
    
    
  

    
	public String getAccountingArticleCode() {
		return accountingArticleCode;
	}

	public void setAccountingArticleCode(String accountingArticleCode) {
		this.accountingArticleCode = accountingArticleCode;
	}

	public List<PriceDTO> getAccountingArticlePrices() {
		return accountingArticlePrices;
	}

	public void setAccountingArticlePrices(List<PriceDTO> accountingArticlePrices) {
		this.accountingArticlePrices = accountingArticlePrices;
	}


	public String getAccountingArticleLabel() {
		return accountingArticleLabel;
	}

	public void setAccountingArticleLabel(String accountingArticleLabel) {
		this.accountingArticleLabel = accountingArticleLabel;
	}

	public String getDiscountPlanItemCode() {
		return discountPlanItemCode;
	}

	public void setDiscountPlanItemCode(String discountPlanItemCode) {
		this.discountPlanItemCode = discountPlanItemCode;
	}

	public DiscountPlanItemTypeEnum getDiscountPlanItemType() {
		return discountPlanItemType;
	}

	public void setDiscountPlanItemType(DiscountPlanItemTypeEnum discountPlanItemType) {
		this.discountPlanItemType = discountPlanItemType;
	}

	public String getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(String discountValue) {
		this.discountValue = discountValue;
	}
    
	
  
    
    
    
}