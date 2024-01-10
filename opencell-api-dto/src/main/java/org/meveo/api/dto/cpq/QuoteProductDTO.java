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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.cpq.AttributeCategoryEnum;
import org.meveo.model.cpq.ProductVersionAttribute;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.commercial.ProductActionTypeEnum;
import org.meveo.model.quote.QuoteArticleLine;
import org.meveo.model.quote.QuoteProduct;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO to create or update a quoteProduct
 * 
 * @author Rachid.AIT
 * @lastModiedVersion 11.0 
 */
@XmlRootElement(name = "QuoteProductDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class QuoteProductDTO extends BaseEntityDto{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7231556751341912018L;

	private Long quoteProductId;
	
    @NotNull
	private String quoteCode;
    @NotNull
    private String productCode;

    @NotNull
    private int quoteVersion;

    @NotNull
    private Integer productVersion;

    @NotNull
    private BigDecimal quantity;
    
    /** Discount plan code */
    @Schema(description = "The code of the discount plan")
	private String discountPlanCode;
    
    private List<QuoteAttributeDTO> productAttributes=new ArrayList<QuoteAttributeDTO>();
    
    private List<AccountingArticlePricesDTO> accountingArticlePrices = new ArrayList<AccountingArticlePricesDTO>();

	private CustomFieldsDto customFields;
	
	private Date deliveryDate;
	
	@Schema(description = "The termination date")
    private Date terminationDate;
	
	@Schema(description = "The termination reason code")
    private String terminationReasonCode;
	
	@Schema(description = "The action type")
    private ProductActionTypeEnum actionType;
    
    public QuoteProductDTO() {
    	super();
    }
    

	public QuoteProductDTO(QuoteProduct quoteProduct, CustomFieldsDto customFields) {
		super();
		init(quoteProduct);
		this.customFields=customFields;
		
	}
	
	public void init(QuoteProduct quoteProduct) {
    	quoteProductId = quoteProduct.getId();
		quoteCode=quoteProduct.getQuote()!=null?quoteProduct.getQuote().getCode():null;
		productCode=quoteProduct.getProductVersion().getProduct().getCode();
		productVersion=quoteProduct.getProductVersion().getCurrentVersion();
		quantity=quoteProduct.getQuantity();
		discountPlanCode=quoteProduct.getDiscountPlan() != null ?quoteProduct.getDiscountPlan().getCode() : null;
		deliveryDate=quoteProduct.getDeliveryDate();
	   }
	
	public QuoteProductDTO(QuoteProduct quoteProduct, boolean loadAttributes,
						   Map<String, TaxDTO> mapTaxIndexes) {
		super();
		init(quoteProduct);
		if(loadAttributes) {
			productAttributes=new ArrayList<QuoteAttributeDTO>();
			for(QuoteAttribute quoteAttribute:quoteProduct.getQuoteAttributes()) {
				ProductVersionAttribute productVersionAttribute = quoteProduct.getProductVersion().getAttributes().stream().filter(pva -> pva.getAttribute().getCode().equals(quoteAttribute.getAttribute().getCode())).findFirst().orElse(null);
				Integer sequence = 0;
				boolean display = false;
				boolean mandatory = false;
				Boolean readOnly = null;
				List<String> allowedValues = new ArrayList<>();
				AttributeCategoryEnum attributeCategoryEnum = null;
				if(productVersionAttribute != null) {
					sequence = productVersionAttribute.getSequence();
					display = productVersionAttribute.isDisplay();
					mandatory = productVersionAttribute.isMandatory();
					readOnly = productVersionAttribute.getReadOnly();
					allowedValues = new ArrayList<>(productVersionAttribute.getAttribute().getAllowedValues());
					attributeCategoryEnum = productVersionAttribute.getAttribute().getAttributeCategory();
				}
				productAttributes.add(new QuoteAttributeDTO(quoteAttribute, sequence, mandatory, display, readOnly, allowedValues, attributeCategoryEnum));
			}
		}
		accountingArticlePrices=new ArrayList<AccountingArticlePricesDTO>();
		for(QuoteArticleLine quoteArticleLine:quoteProduct.getQuoteArticleLines()) {
			accountingArticlePrices.add(new AccountingArticlePricesDTO(quoteArticleLine, mapTaxIndexes));
		}
		
	}

	/**
	 * @return the productCode
	 */
	public String getProductCode() {
		return productCode;
	}

	/**
	 * @param productCode the productCode to set
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	/**
	 * @return the productVersion
	 */
	public Integer getProductVersion() {
		return productVersion;
	}

	/**
	 * @param productVersion the productVersion to set
	 */
	public void setProductVersion(Integer productVersion) {
		this.productVersion = productVersion;
	}





	/**
	 * @return the quantity
	 */
	public BigDecimal getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	

	/**
	 * @return the productAttributes
	 */
	public List<QuoteAttributeDTO> getProductAttributes() {
		return productAttributes;
	}


	/**
	 * @param productAttributes the productAttributes to set
	 */
	public void setProductAttributes(List<QuoteAttributeDTO> productAttributes) {
		this.productAttributes = productAttributes;
	}


	/**
	 * @return the cpqQuoteCode
	 */
	public String getQuoteCode() {
		return quoteCode;
	}

	/**
	 * @param cpqQuoteCode the cpqQuoteCode to set
	 */
	public void setQuoteCode(String cpqQuoteCode) {
		this.quoteCode = cpqQuoteCode;
	}

	/**
	 * @return the quoteVersion
	 */
	public int getQuoteVersion() {
		return quoteVersion;
	}

	/**
	 * @param quoteVersion the quoteVersion to set
	 */
	public void setQuoteVersion(int quoteVersion) {
		this.quoteVersion = quoteVersion;
	}

	/**
	 * @return the accountingArticlePrices
	 */
	public List<AccountingArticlePricesDTO> getAccountingArticlePrices() {
		return accountingArticlePrices;
	}

	/**
	 * @param accountingArticlePrices the accountingArticlePrices to set
	 */
	public void setAccountingArticlePrices(List<AccountingArticlePricesDTO> accountingArticlePrices) {
		this.accountingArticlePrices = accountingArticlePrices;
	}


	/**
	 * @return the quoteProductId
	 */
	public Long getQuoteProductId() {
		return quoteProductId;
	}


	/**
	 * @param quoteProductId the quoteProductId to set
	 */
	public void setQuoteProductId(Long quoteProductId) {
		this.quoteProductId = quoteProductId;
	}


	/**
	 * @return the customFields
	 */
	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	

	public String getDiscountPlanCode() {
		return discountPlanCode;
	}


	public void setDiscountPlanCode(String discountPlanCode) {
		this.discountPlanCode = discountPlanCode;
	}


	/**
	 * @param customFields the customFields to set
	 */
	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}


	public Date getDeliveryDate() {
		return deliveryDate;
	}


	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}


	public Date getTerminationDate() {
		return terminationDate;
	}


	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}


	public String getTerminationReasonCode() {
		return terminationReasonCode;
	}


	public void setTerminationReasonCode(String terminationReasonCode) {
		this.terminationReasonCode = terminationReasonCode;
	}


	public ProductActionTypeEnum getActionType() {
		return actionType;
	}


	public void setActionType(ProductActionTypeEnum actionType) {
		this.actionType = actionType;
	}
	
	
}