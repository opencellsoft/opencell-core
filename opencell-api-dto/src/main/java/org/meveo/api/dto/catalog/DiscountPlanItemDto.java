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

package org.meveo.api.dto.catalog;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.IEnableDto;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Discount plan item
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.3
 */
@XmlRootElement(name = "DiscountPlanItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiscountPlanItemDto extends BaseEntityDto implements IEnableDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4512584223794507921L;

    /**
     * Code
     */
    @NotNull
    @XmlAttribute(required = true)
    @Schema(description = "The code", required = true)
    private String code;

    /**
     * Discount plan code
     */
    @NotNull
    @XmlElement(required = true)
    @Schema(description = "Discount plan code", required = true)
    private String discountPlanCode;

    /**
     * Invoice category code
     */
    @Schema(description = "Invoice category code")
    private String invoiceCategoryCode;

    /**
     * Invoice sub category code
     */
    @Schema(description = "Invoice sub category code")
    private String invoiceSubCategoryCode;

    /**
     * Accounting code
     */
    @Deprecated // until further analysis
    @Schema(description = "Accounting code", deprecated = true)
    private String accountingCode;

    /**


    /**
     * Expression to determine if discount applies
     */
    @Schema(description = "Expression to determine if discount applies")
    private String expressionEl;

    /**
     * Is entity disabled. Value is ignored in Update action - use enable/disable API instead.
     */
    @Schema(description = "Is entity disabled. Value is ignored in Update action - use enable/disable API instead")
    private Boolean disabled;

    /**
     * Type of discount, whether absolute or percentage.
     */
    @Schema(description = "Type of discount, whether absolute or percentage", defaultValue = "PERCENTAGE")
    private DiscountPlanItemTypeEnum discountPlanItemType = DiscountPlanItemTypeEnum.PERCENTAGE;

    /**
     * The absolute or percentage discount amount.
     */
    @Schema(description = "The absolute or percentage discount amount")
    private BigDecimal discountValue;

    /**
     * The absolute or percentage discount amount EL.
     */
    @Schema(description = "The absolute or percentage discount amount EL")
    private String discountValueEL;

    /** The accountingArticle */
    @XmlElementWrapper(name = "targetAccountingArticleCodes")
    @XmlElement(name = "targetAccountingArticleCodes")
    @Schema(description = "The target accounting article codes")
    protected Set<String> targetAccountingArticleCodes = new HashSet<String>();

	/**
     * pricePlanMatrix code
     */
    @Schema(description = "Price plan matrix code")
	 private String pricePlanMatrixCode;

	/** The custom fields. */
    @XmlElement(required = false)
    @Schema(description = "The custom fields")
    private CustomFieldsDto customFields;

    /**
     * If true, then allows to negate the amount of affected invoice lines.
     * If fase, then amount for the discount line produce by the discount plan item cannot exceed the amount of discounted lines.
     * Default: false
     */
    @Schema(description = "<ul><li>If true, then allows to negate the amount of affected invoice lines</li><li>If fase, then amount for the discount line produce by the discount plan item cannot exceed the amount of discounted lines</li></ul>", defaultValue = "false")
    private Boolean allowToNegate;

    @Schema(description = "description of discount plan item")
	private String description;
    
    
    /**
     * The lower number, the higher the priority is
     */
    @Schema(description = "The lower number, the higher the priority is")
    protected Long priority ;
    
    
    /**
     * accounting article code
     */
    @Schema(description = "accounting article code")
    private String accountingArticleCode;
    
    
    @Schema(description = "Apply by article")
    private Boolean applyByArticle;
    
    /**
	 defines the order in which discount plans are applied
	 */
	@Schema(description = "defines the order in which discount plans are applied")
	private Integer sequence;
	
	 @Schema(description = "last discount")
	 private Boolean lastDiscount;
    
    /**
     * Instantiates a new discount plan item dto.
     */
    public DiscountPlanItemDto() {
    }

    /**
     * Convert discount plan item entity to DTO
     *
     * @param discountPlanItem Entity to convert
     * @param customFieldInstances the custom fields
     */
    public DiscountPlanItemDto(DiscountPlanItem discountPlanItem, CustomFieldsDto customFieldInstances) {
        this.code = discountPlanItem.getCode();
        this.discountPlanCode = discountPlanItem.getDiscountPlan().getCode();
        this.invoiceCategoryCode = discountPlanItem.getInvoiceCategory() != null ? discountPlanItem.getInvoiceCategory().getCode() : null;
        this.invoiceSubCategoryCode = discountPlanItem.getInvoiceSubCategory() != null ? discountPlanItem.getInvoiceSubCategory().getCode() : null;
        this.expressionEl = discountPlanItem.getExpressionEl();
        this.disabled = discountPlanItem.isDisabled();
		this.discountPlanItemType = discountPlanItem.getDiscountPlanItemType();
		this.discountValue = discountPlanItem.getDiscountValue();
		this.discountValueEL = discountPlanItem.getDiscountValueEL();
		this.pricePlanMatrixCode=discountPlanItem.getPricePlanMatrix()!=null?discountPlanItem.getPricePlanMatrix().getCode():null;
		 this.targetAccountingArticleCodes = discountPlanItem.getTargetAccountingArticle()
                 .stream()
                 .map(accountingArticle -> accountingArticle.getCode())
                 .collect(Collectors.toSet());
        this.allowToNegate = discountPlanItem.isAllowToNegate();
        customFields = customFieldInstances;
        this.description = discountPlanItem.getDescription();
        this.priority=discountPlanItem.getPriority();
        this.accountingArticleCode= discountPlanItem.getAccountingArticle() != null ? discountPlanItem.getAccountingArticle().getCode() : null;
        this.applyByArticle=discountPlanItem.isApplyByArticle();
        this.lastDiscount=discountPlanItem.getLastDiscount();
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the discount plan code.
     *
     * @return the discount plan code
     */
    public String getDiscountPlanCode() {
        return discountPlanCode;
    }

    /**
     * Sets the discount plan code.
     *
     * @param discountPlanCode the new discount plan code
     */
    public void setDiscountPlanCode(String discountPlanCode) {
        this.discountPlanCode = discountPlanCode;
    }

    /**
     * Gets the invoice category code.
     *
     * @return the invoice category code
     */
    public String getInvoiceCategoryCode() {
        return invoiceCategoryCode;
    }

    /**
     * Sets the invoice category code.
     *
     * @param invoiceCategoryCode the new invoice category code
     */
    public void setInvoiceCategoryCode(String invoiceCategoryCode) {
        this.invoiceCategoryCode = invoiceCategoryCode;
    }

    /**
     * Gets the invoice sub category code.
     *
     * @return the invoice sub category code
     */
    public String getInvoiceSubCategoryCode() {
        return invoiceSubCategoryCode;
    }

    /**
     * Sets the invoice sub category code.
     *
     * @param invoiceSubCategoryCode the new invoice sub category code
     */
    public void setInvoiceSubCategoryCode(String invoiceSubCategoryCode) {
        this.invoiceSubCategoryCode = invoiceSubCategoryCode;
    }
    /**
     * Gets the accounting code.
     *
     * @return the accounting code
     */
    public String getAccountingCode() {
        return accountingCode;
    }

    /**
     * Sets the accounting code.
     *
     * @param accountingCode the new accounting code
     */
    public void setAccountingCode(String accountingCode) {
        this.accountingCode = accountingCode;
    }

    /**
    public String getExpressionEl() {
        return expressionEl;
    }

    /**
     * @param expressionEl Expression to determine if discount applies
     */
    public void setExpressionEl(String expressionEl) {
        this.expressionEl = expressionEl;
    }

    /**
     * Sets whether this entity is disabled or not.
     */
    @Override
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Whether this entity is disabled.
     */
    @Override
    public Boolean isDisabled() {
        return disabled;
    }

    /**
     * Sets the discount plan item type.
     * @return item type
     */
	public DiscountPlanItemTypeEnum getDiscountPlanItemType() {
		return discountPlanItemType;
	}


    /**
     * Gets the discount plan item type.
     */
	public void setDiscountPlanItemType(DiscountPlanItemTypeEnum discountPlanItemType) {
		this.discountPlanItemType = discountPlanItemType;
	}

	/**
	 * Gets the discount value. Can be either percentage or fixed. Depending on the item type.
	 * @return the discount value
	 */
	public BigDecimal getDiscountValue() {
		return discountValue;
	}

	/**
	 * Sets the discount value. Can be either percentage or fixed. Depending on the item type.
	 * @param discountValue the discount value
	 */
	public void setDiscountValue(BigDecimal discountValue) {
		this.discountValue = discountValue;
	}

    @Override
    public String toString() {
        return "DiscountPlanItemDto [code=" + code + ", discountPlanCode=" + discountPlanCode + ", invoiceCategoryCode=" + invoiceCategoryCode + ", invoiceSubCategoryCode="
                + invoiceSubCategoryCode + ", accountingCode=" + accountingCode + ", expressionEl=" + expressionEl + ", disabled="
                + disabled + ", discountPlanItemType=" + discountPlanItemType + ", discountValue=" + discountValue + ", discountValueEL=" + discountValueEL +"]";
    }

    /**
     * Sets the discount value el.
     *
     * @param discountValueEL el expression
     */
    public void setDiscountValueEL(String discountValueEL) {
        this.discountValueEL = discountValueEL;
    }

    /**
     * Gets the discount value el.
     *
     * @return el expression
     */
    public String getDiscountValueEL() {
        return discountValueEL;
    }

    /**
     * Gets the custom fields.
     *
     * @return custom fields associated with this entity
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

	/**
	 * Sets the custom fields.
	 * @param customFields custom fields to be associated with this entity
	 */
	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}

	public String getPricePlanMatrixCode() {
		return pricePlanMatrixCode;
	}

	public void setPricePlanMatrixCode(String pricePlanMatrixCode) {
		this.pricePlanMatrixCode = pricePlanMatrixCode;
	}



	public Set<String> getTargetAccountingArticleCodes() {
		return targetAccountingArticleCodes;
	}

	public void setTargetAccountingArticleCodes(Set<String> targetAccountingArticleCodes) {
		this.targetAccountingArticleCodes = targetAccountingArticleCodes;
	}

	public String getExpressionEl() {
		return expressionEl;
	}
    public Boolean isAllowToNegate() {
        return allowToNegate;
    }

    public void setAllowToNegate(Boolean allowToNegate) {
        this.allowToNegate = allowToNegate;
    }

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public Long getPriority() {
		return priority;
	}

	public void setPriority(Long priority) {
		this.priority = priority;
	}

	public String getAccountingArticleCode() {
		return accountingArticleCode;
	}

	public void setAccountingArticleCode(String accountingArticleCode) {
		this.accountingArticleCode = accountingArticleCode;
	}

	public Boolean getApplyByArticle() {
		return applyByArticle;
	}

	public void setApplyByArticle(Boolean applyByArticle) {
		this.applyByArticle = applyByArticle;
	}

	public Boolean getAllowToNegate() {
		return allowToNegate;
	}
	
	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Boolean getLastDiscount() {
		return lastDiscount;
	}

	public void setLastDiscount(Boolean lastDiscount) {
		this.lastDiscount = lastDiscount;
	}

 
	
	
	



}
