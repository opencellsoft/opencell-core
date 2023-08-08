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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.contract.ContractItem;
import org.meveo.model.cpq.contract.ContractRateTypeEnum;
import org.meveo.model.cpq.enums.PriceTypeEnum;
import org.meveo.model.quote.QuotePrice;
import org.meveo.model.tax.TaxCategory;
import org.meveo.model.tax.TaxClass;

/**
 * The Class AccountingArticlePrices.
 *
 * @author Rachid.AIT
 * @lastModifiedVersion 11.0.0
 */
@XmlRootElement(name = "PriceDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class PriceDTO extends BaseEntityDto {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1054495149414405858L;

	
	@XmlAttribute
	private PriceTypeEnum priceType;

    private BigDecimal amountWithTax;
    
    private BigDecimal unitPriceWithoutTax;

    private BigDecimal amountWithoutTax;
    
    private BigDecimal amountWithoutTaxWithoutDiscount;

    private BigDecimal taxAmount;
    
    private BigDecimal taxRate;
    
    private Boolean priceOverCharged;

    private String currencyCode;

	private String currencySymbol;
    
    private Long recurrenceDuration;
    private String recurrencePeriodicity;
    private String chargeCode;
    private String chargeLabel;
    
    private String taxCategory;
    private String taxCode;
    private BigDecimal quantity;
    private PriceDTO discountedQuotePrice;
    
    private BigDecimal unitMultiplicator;
    private BigDecimal discountValue;
    private DiscountPlanItemTypeEnum discountPlanType;
    private String discountPlanItemCode;
    private String discountPlanCode;
    private String discountPlanItemDesc;
    private String discountPlanDesc;
    private Boolean applyDiscountsOnOverridenPrice;
    private BigDecimal overchargedUnitAmountWithoutTax;
    private BigDecimal discountedAmount;
    private Integer sequence;
    private Long id;
    private String contractCode;
    private String contractDescription;
    private String contractItemCode;
    private String contractItemDescription;
    private String contractType;
    private String pricePlanMatrixCode;
    private String pricePlanMatrixLabel;
    private Long pricePlanMatrixVersionId;
    private Long pricePlanMatrixLineId;
    private ContractRateTypeEnum contractItemRateType;
    
    private CustomFieldsDto customFields;
    
    
	public PriceDTO(QuotePrice quotePrice) {
		super();
		id=quotePrice.getId();
		priceType=quotePrice.getPriceTypeEnum();
	    unitPriceWithoutTax=quotePrice.getUnitPriceWithoutTax();
	    taxAmount=quotePrice.getTaxAmount();
	    taxRate=quotePrice.getTaxRate();
	    priceOverCharged=quotePrice.getPriceOverCharged();
	    currencyCode=quotePrice.getCurrencyCode();
	    recurrenceDuration=quotePrice.getRecurrenceDuration();
	    recurrencePeriodicity=quotePrice.getRecurrencePeriodicity();
		amountWithTax=quotePrice.getAmountWithTax();
		amountWithoutTax=quotePrice.getAmountWithoutTax();
		amountWithoutTaxWithoutDiscount=quotePrice.getAmountWithoutTaxWithoutDiscount();
		

	    chargeCode=quotePrice.getChargeTemplate()!=null?quotePrice.getChargeTemplate().getCode():null;
	    chargeLabel=quotePrice.getChargeTemplate()!=null?quotePrice.getChargeTemplate().getDescription():null;
	   TaxCategory taxCategoryEntity = quotePrice.getQuoteArticleLine() != null ? quotePrice.getQuoteArticleLine().getBillableAccount().getTaxCategory()!=null ? quotePrice.getQuoteArticleLine().getBillableAccount().getTaxCategory(): 
	    	quotePrice.getQuoteArticleLine().getBillableAccount().getCustomerAccount().getCustomer().getCustomerCategory().getTaxCategory() : null;
	   taxCategory=taxCategoryEntity!=null?taxCategoryEntity.getCode():null;
	   TaxClass taxClass=quotePrice.getQuoteArticleLine() != null ? quotePrice.getQuoteArticleLine().getAccountingArticle().getTaxClass() : null;
	   taxCode=taxClass!=null?taxClass.getCode():null;
	   quantity = quotePrice.getQuantity();
	   unitMultiplicator=quotePrice.getChargeTemplate()!=null?quotePrice.getChargeTemplate().getUnitMultiplicator():null;
	   if(quotePrice.getDiscountedQuotePrice() != null) {
		   discountedQuotePrice = new PriceDTO(quotePrice.getDiscountedQuotePrice());
	   }
	   discountPlanCode=quotePrice.getDiscountPlan()!=null?quotePrice.getDiscountPlan().getCode():null;
	   discountPlanDesc=quotePrice.getDiscountPlan()!=null?quotePrice.getDiscountPlan().getDescription():null;
	   discountPlanItemCode=quotePrice.getDiscountPlanItem()!=null?quotePrice.getDiscountPlanItem().getCode():null;
	   discountPlanItemDesc=quotePrice.getDiscountPlanItem()!=null?quotePrice.getDiscountPlanItem().getDescription():null;
	   discountPlanType=quotePrice.getDiscountPlanType();
	   discountValue=quotePrice.getDiscountValue();
	   applyDiscountsOnOverridenPrice=quotePrice.getApplyDiscountsOnOverridenPrice();
	   overchargedUnitAmountWithoutTax=quotePrice.getOverchargedUnitAmountWithoutTax();
	   discountedAmount=quotePrice.getDiscountedAmount();
	   sequence=quotePrice.getSequence();
	   if(quotePrice.getContractItem()!=null) {
		   ContractItem contractItem=quotePrice.getContractItem();
		   contractCode=contractItem.getContract().getCode();
		   contractDescription=contractItem.getContract().getDescription();
		   contractItemCode=contractItem.getCode();
		   contractItemDescription=contractItem.getDescription();
		   contractItemRateType=contractItem.getContractRateType();
	   }
	   if(quotePrice.getPricePlanMatrixVersion()!=null) {
		   PricePlanMatrixVersion ppmv=quotePrice.getPricePlanMatrixVersion();
		   pricePlanMatrixVersionId=ppmv.getId();
		   pricePlanMatrixCode=ppmv.getPricePlanMatrix().getCode();
		   pricePlanMatrixLabel=ppmv.getPricePlanMatrix().getDescription();
	   }
	   pricePlanMatrixLineId=quotePrice.getPricePlanMatrixLine()!=null?quotePrice.getPricePlanMatrixLine().getId():null;

	}

	public PriceDTO(QuotePrice quotePrice, TradingCurrency currency) {
		this(quotePrice);
		this.setCurrencySymbol(currency.getSymbol());
	}

	public PriceDTO(QuotePrice quotePrice,CustomFieldsDto customFields) {
		this(quotePrice);
		this.customFields = customFields;
	}
	
	
	
	public PriceDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PriceTypeEnum getPriceType() {
		return priceType;
	}
	public void setPriceType(PriceTypeEnum priceType) {
		this.priceType = priceType;
	}
	public BigDecimal getAmountWithTax() { return amountWithTax; }
	public void setAmountWithTax(BigDecimal amountWithTax) { this.amountWithTax = amountWithTax; }
	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}
	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}
	public BigDecimal getTaxAmount() {
		return taxAmount;
	}
	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}
	public BigDecimal getTaxRate() {
		return taxRate;
	}
	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public Long getRecurrenceDuration() {
		return recurrenceDuration;
	}
	public void setRecurrenceDuration(Long recurrenceDuration) {
		this.recurrenceDuration = recurrenceDuration;
	}
	public String getRecurrencePeriodicity() {
		return recurrencePeriodicity;
	}
	public void setRecurrencePeriodicity(String recurrencePeriodicity) {
		this.recurrencePeriodicity = recurrencePeriodicity;
	}
	public BigDecimal getUnitPriceWithoutTax() {
		return unitPriceWithoutTax;
	}
	public void setUnitPriceWithoutTax(BigDecimal unitPriceWithoutTax) {
		this.unitPriceWithoutTax = unitPriceWithoutTax;
	}
	public BigDecimal getAmountWithoutTaxWithoutDiscount() {
		return amountWithoutTaxWithoutDiscount;
	}
	public void setAmountWithoutTaxWithoutDiscount(BigDecimal amountWithoutTaxWithDiscount) {
		this.amountWithoutTaxWithoutDiscount = amountWithoutTaxWithDiscount;
	}
	/**
	 * @return the priceOverCharged
	 */
	public Boolean getPriceOverCharged() {
		return priceOverCharged;
	}
	/**
	 * @param priceOverCharged the priceOverCharged to set
	 */
	public void setPriceOverCharged(Boolean priceOverCharged) {
		this.priceOverCharged = priceOverCharged;
	}

	/**
	 * @return the chargeCode
	 */
	public String getChargeCode() {
		return chargeCode;
	}

	/**
	 * @param chargeCode the chargeCode to set
	 */
	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}

	/**
	 * @return the chargeLabel
	 */
	public String getChargeLabel() {
		return chargeLabel;
	}

	/**
	 * @param chargeLabel the chargeLabel to set
	 */
	public void setChargeLabel(String chargeLabel) {
		this.chargeLabel = chargeLabel;
	}

	public String getTaxCategory() {
		return taxCategory;
	}

	public void setTaxCategory(String taxCategory) {
		this.taxCategory = taxCategory;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}
	public CustomFieldsDto getCustomFields() {
		return customFields;
	}
	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}
	public BigDecimal getQuantity() {
		return quantity;
	}
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}
	public PriceDTO getDiscountedQuotePrice() {
		return discountedQuotePrice;
	}
	public void setDiscountedQuotePrice(PriceDTO discountedQuotePrice) {
		this.discountedQuotePrice = discountedQuotePrice;
	}
	public BigDecimal getUnitMultiplicator() {
		return unitMultiplicator;
	}
	public void setUnitMultiplicator(BigDecimal unitMultiplicator) {
		this.unitMultiplicator = unitMultiplicator;
	}
	public BigDecimal getDiscountValue() {
		return discountValue;
	}
	public void setDiscountValue(BigDecimal discountValue) {
		this.discountValue = discountValue;
	}
	public DiscountPlanItemTypeEnum getDiscountPlanType() {
		return discountPlanType;
	}
	public void setDiscountPlanType(DiscountPlanItemTypeEnum discountPlanType) {
		this.discountPlanType = discountPlanType;
	}
	public String getDiscountPlanItemCode() {
		return discountPlanItemCode;
	}
	public void setDiscountPlanItemCode(String discountPlanItemCode) {
		this.discountPlanItemCode = discountPlanItemCode;
	}
	public Boolean getApplyDiscountsOnOverridenPrice() {
		return applyDiscountsOnOverridenPrice;
	}
	public void setApplyDiscountsOnOverridenPrice(Boolean applyDiscountsOnOverridenPrice) {
		this.applyDiscountsOnOverridenPrice = applyDiscountsOnOverridenPrice;
	}
	public BigDecimal getOverchargedUnitAmountWithoutTax() {
		return overchargedUnitAmountWithoutTax;
	}
	public void setOverchargedUnitAmountWithoutTax(BigDecimal overchargedUnitAmountWithoutTax) {
		this.overchargedUnitAmountWithoutTax = overchargedUnitAmountWithoutTax;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public BigDecimal getDiscountedAmount() {
		return discountedAmount;
	}
	public void setDiscountedAmount(BigDecimal discountedAmount) {
		this.discountedAmount = discountedAmount;
	}
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	public String getContractItemCode() {
		return contractItemCode;
	}

	public void setContractItemCode(String contractItemCode) {
		this.contractItemCode = contractItemCode;
	}

	public Long getPricePlanMatrixVersionId() {
		return pricePlanMatrixVersionId;
	}

	public void setPricePlanMatrixVersionId(Long pricePlanMatrixVersionId) {
		this.pricePlanMatrixVersionId = pricePlanMatrixVersionId;
	}

	public Long getPricePlanMatrixLineId() {
		return pricePlanMatrixLineId;
	}

	public void setPricePlanMatrixLineId(Long pricePlanMatrixLineId) {
		this.pricePlanMatrixLineId = pricePlanMatrixLineId;
	}

	public String getContractCode() {
		return contractCode;
	}

	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}

	public String getContractDescription() {
		return contractDescription;
	}

	public void setContractDescription(String contractDescription) {
		this.contractDescription = contractDescription;
	}

	public String getContractItemDescription() {
		return contractItemDescription;
	}

	public void setContractItemDescription(String contractItemDescription) {
		this.contractItemDescription = contractItemDescription;
	}

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public String getPricePlanMatrixCode() {
		return pricePlanMatrixCode;
	}

	public void setPricePlanMatrixCode(String pricePlanMatrixCode) {
		this.pricePlanMatrixCode = pricePlanMatrixCode;
	}

	public String getPricePlanMatrixLabel() {
		return pricePlanMatrixLabel;
	}

	public void setPricePlanMatrixLabel(String pricePlanMatrixLabel) {
		this.pricePlanMatrixLabel = pricePlanMatrixLabel;
	}

	public ContractRateTypeEnum getContractItemRateType() {
		return contractItemRateType;
	}

	public void setContractItemRateType(ContractRateTypeEnum contractItemRateType) {
		this.contractItemRateType = contractItemRateType;
	}

	public String getDiscountPlanCode() {
		return discountPlanCode;
	}

	public void setDiscountPlanCode(String discountPlanCode) {
		this.discountPlanCode = discountPlanCode;
	}

	public String getDiscountPlanItemDesc() {
		return discountPlanItemDesc;
	}

	public void setDiscountPlanItemDesc(String discountPlanItemDesc) {
		this.discountPlanItemDesc = discountPlanItemDesc;
	}

	public String getDiscountPlanDesc() {
		return discountPlanDesc;
	}

	public void setDiscountPlanDesc(String discountPlanDesc) {
		this.discountPlanDesc = discountPlanDesc;
	}

	
	
	
	 
}