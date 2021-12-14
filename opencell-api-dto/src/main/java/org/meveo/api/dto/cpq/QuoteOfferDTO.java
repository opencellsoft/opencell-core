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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.cpq.xml.TaxPricesDto;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.commercial.PriceLevelEnum;
import org.meveo.model.cpq.enums.PriceTypeEnum;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuotePrice;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteVersion;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO to create or update a quoteOffer
 * 
 * @author Rachid.AIT
 * @lastModiedVersion 11.0 
 */
@XmlRootElement(name = "QuoteOfferDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class QuoteOfferDTO extends BusinessEntityDto{

    public QuoteOfferDTO() {
		super();
	}
    

	/**
	 * 
	 */
	private static final long serialVersionUID = 8115890992793236496L;

	@Schema(description = "quote offer id, used for updating an existing quote offer")
	private Long quoteOfferId;
	

	@Schema(description = "the code of the quote")
    private String quoteCode;

	@Schema(description = "the version of the quote, associated to the quote code to retrieve a quote")
    @NotNull
    private Integer quoteVersion;

	@Schema(description = "the code of the quote lot")
    private String quoteLotCode;

    @NotNull
	@Schema(description = "id of offer template")
    private Long offerId;

	@Schema(description = "the code of offer template")
    private String offerCode;

	@Schema(description = "code of billing account")
    private String billableAccountCode;

	@Schema(description = "the code of contract ")
    private String contractCode;

	@Schema(description = "list of quote product", example = "\"products\": [[<br/>" + 
			"        {[<br/>" + 
			"            \"productCode\": \"product_Code\",[<br/>" + 
			"            \"quoteCode\": \"quote_Code\",[<br/>" + 
			"            \"productVersion\": product_Version,[<br/>" + 
			"            \"quoteVersion\": Quote_Version,[<br/>" + 
			"            \"quantity\": 1,[<br/>" + 
			"            \"productAttributes\": [[<br/>" + 
			"                {[<br/>" + 
			"                    \"quoteAttributeCode\": \"billing_Cycle_Attribute\",[<br/>" + 
			"                    \"stringValue\": \"Monthly\"[<br/>" + 
			"                },[<br/>" + 
			"                {[<br/>" + 
			"                    \"quoteAttributeCode\": \"engagement_Attribute_Code\",[<br/>" + 
			"                    \"doubleValue\": 24[<br/>" + 
			"                }[<br/>" + 
			"            ][<br/>" + 
			"        }[<br/>" + 
			"    ]")
    private List<QuoteProductDTO> products = new ArrayList<QuoteProductDTO>();

	@Schema(description = "list of quote attribute", example = " \"offerAttributes\": [[<br/>" + 
			"        {[<br/>" + 
			"            \"quoteAttributeCode\": \"Attribute_code}}\",[<br/>" + 
			"            \"stringValue\": \"Monthly\",[<br/>" + 
			"            \"doubleValue\": 24,[<br/>" + 
			"            \"dateValue\": \"2021-02-22\"[<br/>" + 
			"        }[<br/>" + 
			"    ]")
    private List<QuoteAttributeDTO> offerAttributes =new ArrayList<QuoteAttributeDTO>();

	@Schema(description = "custom fields for quote offer")
    private CustomFieldsDto customFields;
    
    /** Discount plan code */
	@Schema(description = "the code of the discount plan")
	private String discountPlanCode;
	
	
	/**
	 * List of quote prices
	 */
	@Schema(description = "total amounts")
	private List<TaxPricesDto> prices;
    
    
	/** Discount plan code */
	@Schema(description = "the position of the quote item in GUI")
	private Integer sequence;
	
	/** Delivery date */
	@Schema(description = "the delivery date")
	private Date deliveryDate;
	
	
	/** User account */
	@Schema(description = "The code of the user account")
	private String userAccountCode;
   

	public QuoteOfferDTO(QuoteOffer quoteOffer) {
		super();
		init(quoteOffer);
	}
	private void init(QuoteOffer quoteOffer) {
		quoteOfferId=quoteOffer.getId();
		quoteCode=quoteOffer.getQuoteVersion().getQuote().getCode();
		quoteVersion=quoteOffer.getQuoteVersion().getQuoteVersion();
		quoteLotCode=quoteOffer.getQuoteLot()!=null?quoteOffer.getQuoteLot().getCode():null;
		offerCode=quoteOffer.getOfferTemplate().getCode();
		billableAccountCode=quoteOffer.getBillableAccount()!=null?quoteOffer.getBillableAccount().getCode():null;
		contractCode=quoteOffer.getContractCode();
		discountPlanCode=quoteOffer.getDiscountPlan()!=null?quoteOffer.getDiscountPlan().getCode():null;
		offerId = quoteOffer.getOfferTemplate().getId();
		sequence=quoteOffer.getSequence();
		code = quoteOffer.getCode();
		description = quoteOffer.getDescription();
		deliveryDate = quoteOffer.getDeliveryDate();
		userAccountCode=quoteOffer.getUserAccount()!=null?quoteOffer.getUserAccount().getCode():null;
		
	}
	public QuoteOfferDTO(QuoteOffer quoteOffer, boolean loadQuoteProduct, boolean loadQuoteAttributes,boolean loadOfferAttributes) {
		init(quoteOffer);
		prices=calculateTotalsPerOffer(quoteOffer);
		if(loadQuoteProduct) {
			products=new ArrayList<QuoteProductDTO>();
				for(QuoteProduct quoteProduct:quoteOffer.getQuoteProduct()) {
					products.add(new QuoteProductDTO(quoteProduct,loadQuoteAttributes));
				}
		}
		
		if(loadOfferAttributes) {
			offerAttributes=new ArrayList<QuoteAttributeDTO>();
			for(QuoteAttribute offerAttribute:quoteOffer.getQuoteAttributes()) {
				offerAttributes.add(new QuoteAttributeDTO(offerAttribute));
			}
	}
	}

	private List<TaxPricesDto> calculateTotalsPerOffer(QuoteOffer quoteOffer) {
		List<QuotePrice> quotePrices = quoteOffer.getQuotePrices();
		List<TaxPricesDto> taxPricesDtos =new ArrayList<>();
		Map<BigDecimal, List<QuotePrice>> pricesPerTax = quotePrices.stream()
				.filter(price -> PriceLevelEnum.OFFER.equals(price.getPriceLevelEnum()))
				.collect(Collectors.groupingBy(QuotePrice::getTaxRate));


		for (BigDecimal taxRate : pricesPerTax.keySet() ) {

			Map<PriceTypeEnum, List<QuotePrice>> pricesPerType = quotePrices.stream()
					.filter(price -> PriceLevelEnum.OFFER.equals(price.getPriceLevelEnum()))
					.collect(Collectors.groupingBy(QuotePrice::getPriceTypeEnum));

			List<PriceDTO> taxPrices = pricesPerType
					.keySet()
					.stream()
					.map(key -> reducePrices(key, pricesPerType, null, quoteOffer, PriceLevelEnum.OFFER))
					.filter(Optional::isPresent)
					.map(price -> new PriceDTO(price.get()))
					.collect(Collectors.toList());

			taxPricesDtos.add(new TaxPricesDto(taxRate, taxPrices));
		}
		return taxPricesDtos;
	}

	private Optional<QuotePrice> reducePrices(PriceTypeEnum key, Map<PriceTypeEnum, List<QuotePrice>> pricesPerType, QuoteVersion quoteVersion, QuoteOffer quoteOffer, PriceLevelEnum level) {
		if(pricesPerType.get(key).size()==1){
			QuotePrice accountingArticlePrice =pricesPerType.get(key).get(0);
			QuotePrice quotePrice = new QuotePrice();
			quotePrice.setPriceTypeEnum(key);
			quotePrice.setPriceLevelEnum(level);
			quotePrice.setQuoteVersion(quoteVersion!=null?quoteVersion:quoteOffer.getQuoteVersion());
			quotePrice.setQuoteOffer(quoteOffer);
			quotePrice.setTaxAmount(accountingArticlePrice.getTaxAmount());
			quotePrice.setAmountWithTax(accountingArticlePrice.getAmountWithTax());
			quotePrice.setAmountWithoutTax(accountingArticlePrice.getAmountWithoutTax());
			quotePrice.setUnitPriceWithoutTax(accountingArticlePrice.getUnitPriceWithoutTax());
			quotePrice.setTaxRate(accountingArticlePrice.getTaxRate());
			quotePrice.setRecurrenceDuration(accountingArticlePrice.getRecurrenceDuration());
			quotePrice.setRecurrencePeriodicity(accountingArticlePrice.getRecurrencePeriodicity());
			return Optional.of(quotePrice);
		}
		return pricesPerType.get(key).stream().reduce((a, b) -> {
			QuotePrice quotePrice = new QuotePrice();
			quotePrice.setPriceTypeEnum(key);
			quotePrice.setPriceLevelEnum(level);
			quotePrice.setQuoteVersion(quoteVersion!=null?quoteVersion:quoteOffer.getQuoteVersion());
			quotePrice.setQuoteOffer(quoteOffer);
			quotePrice.setTaxAmount(a.getTaxAmount().add(b.getTaxAmount()));
			quotePrice.setAmountWithTax(a.getAmountWithTax().add(b.getAmountWithTax()));
			quotePrice.setAmountWithoutTax(a.getAmountWithoutTax().add(b.getAmountWithoutTax()));
			quotePrice.setUnitPriceWithoutTax(a.getUnitPriceWithoutTax().add(b.getUnitPriceWithoutTax()));
			quotePrice.setTaxRate(a.getTaxRate());
			if(a.getRecurrenceDuration()!=null) {
				quotePrice.setRecurrenceDuration(a.getRecurrenceDuration());
			}
			if(a.getRecurrencePeriodicity()!=null) {
				quotePrice.setRecurrencePeriodicity(a.getRecurrencePeriodicity());
			}
			return quotePrice;
		});
	}

	/**
	 * @return the quoteCode
	 */
	public String getQuoteCode() {
		return quoteCode;
	}

	/**
	 * @param quoteCode the quoteCode to set
	 */
	public void setQuoteCode(String quoteCode) {
		this.quoteCode = quoteCode;
	}

	/**
	 * @return the quoteVersion
	 */
	public Integer getQuoteVersion() {
		return quoteVersion;
	}

	/**
	 * @param quoteVersion the quoteVersion to set
	 */
	public void setQuoteVersion(Integer quoteVersion) {
		this.quoteVersion = quoteVersion;
	}

	/**
	 * @return the customerServiceCode
	 */
	public String getQuoteLotCode() {
		return quoteLotCode;
	}

	/**
	 * @param quoteLotCode the quoteLotCode to set
	 */
	public void setQuoteLotCode(String quoteLotCode) {
		this.quoteLotCode = quoteLotCode;
	}

	/**
	 * @return the offerCode
	 */
	public String getOfferCode() {
		return offerCode;
	}

	/**
	 * @param offerCode the offerCode to set
	 */
	public void setOfferCode(String offerCode) {
		this.offerCode = offerCode;
	}

	/**
	 * @return the billableAccountCode
	 */
	public String getBillableAccountCode() {
		return billableAccountCode;
	}

	/**
	 * @param billableAccountCode the billableAccountCode to set
	 */
	public void setBillableAccountCode(String billableAccountCode) {
		this.billableAccountCode = billableAccountCode;
	}

 

	/**
	 * @return the products
	 */
	public List<QuoteProductDTO> getProducts() {
		return products;
	}
	/**
	 * @param products the products to set
	 */
	public void setProducts(List<QuoteProductDTO> products) {
		this.products = products;
	}
	/**
	 * @return the quoteOfferId
	 */
	public Long getQuoteOfferId() {
		return quoteOfferId;
	}

	/**
	 * @param quoteOfferId the quoteOfferId to set
	 */
	public void setQuoteOfferId(Long quoteOfferId) {
		this.quoteOfferId = quoteOfferId;
	}



	/**
	 * @return the contractCode
	 */
	public String getContractCode() {
		return contractCode;
	}

	/**
	 * @param contractCode the contractCode to set
	 */
	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}
	/**
	 * @return the offerAttributes
	 */
	public List<QuoteAttributeDTO> getOfferAttributes() {
		return offerAttributes;
	}
	/**
	 * @param offerAttributes the offerAttributes to set
	 */
	public void setOfferAttributes(List<QuoteAttributeDTO> offerAttributes) {
		this.offerAttributes = offerAttributes;
	}
	/**
	 * @return the customFields
	 */
	public CustomFieldsDto getCustomFields() {
		return customFields;
	}
	/**
	 * @param customFields the customFields to set
	 */
	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
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
	 * @return the offerId
	 */
	public Long getOfferId() {
		return offerId;
	}
	/**
	 * @param offerId the offerId to set
	 */
	public void setOfferId(Long offerId) {
		this.offerId = offerId;
	}
	public List<TaxPricesDto> getPrices() {
		return prices;
	}
	public void setPrices(List<TaxPricesDto> prices) {
		this.prices = prices;
	}
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public String getUserAccountCode() {
		return userAccountCode;
	}
	public void setUserAccountCode(String userAccountCode) {
		this.userAccountCode = userAccountCode;
	}
	
	
	
   
}