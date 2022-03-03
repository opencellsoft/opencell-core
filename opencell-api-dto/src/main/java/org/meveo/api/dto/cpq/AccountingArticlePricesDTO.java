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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.cpq.xml.TaxPricesDto;
import org.meveo.common.UtilsDto;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;
import org.meveo.model.cpq.commercial.PriceLevelEnum;
import org.meveo.model.cpq.enums.PriceTypeEnum;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuoteArticleLine;
import org.meveo.model.quote.QuotePrice;
import org.meveo.model.quote.QuoteVersion;

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
    
    
  

    
	public AccountingArticlePricesDTO(QuoteArticleLine quoteArticleline) {
		super();
		accountingArticleCode=quoteArticleline.getAccountingArticle().getCode();
		accountingArticleLabel=quoteArticleline.getAccountingArticle().getDescription();
		accountingArticlePrices=new ArrayList<PriceDTO>();
		Map<BigDecimal, List<QuotePrice>> pricesPerTaux = quoteArticleline.getQuotePrices().stream()
                .collect(Collectors.groupingBy(QuotePrice::getTaxRate));
		 BigDecimal quoteTotalAmount = BigDecimal.ZERO;
	        for (BigDecimal taux: pricesPerTaux.keySet()) {

	            Map<PriceTypeEnum, List<QuotePrice>> pricesPerType = pricesPerTaux.get(taux).stream()
	                    .collect(Collectors.groupingBy(QuotePrice::getPriceTypeEnum));

	            List<PriceDTO> prices = pricesPerType
	                    .keySet()
	                    .stream()
	                    .map(key -> reducePrices(key, pricesPerType, quoteArticleline.getQuoteVersion(), quoteArticleline.getQuoteProduct()!=null?quoteArticleline.getQuoteProduct().getQuoteOffer():null, PriceLevelEnum.PRODUCT))
	                    .filter(Optional::isPresent)
	                    .map(price -> new PriceDTO(price.get())).collect(Collectors.toList());

	            quoteTotalAmount.add(prices.stream().map(o->o.getAmountWithoutTax()).reduce(BigDecimal.ZERO, BigDecimal::add));
	        }
	}
	
	 private Optional<QuotePrice> reducePrices(PriceTypeEnum key, Map<PriceTypeEnum, List<QuotePrice>> pricesPerType, QuoteVersion quoteVersion,QuoteOffer quoteOffer, PriceLevelEnum level) {
	    	if(pricesPerType.get(key) != null && pricesPerType.get(key).size()==1){
	    		return Optional.of(pricesPerType.get(key).get(0));
	    	}
	    	return UtilsDto.reducePrices(key, pricesPerType, PriceLevelEnum.PRODUCT, quoteVersion, quoteOffer);
	    }

	public AccountingArticlePricesDTO() {
		super();
		// TODO Auto-generated constructor stub
	}



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