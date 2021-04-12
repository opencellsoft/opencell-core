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
package org.meveo.api.dto;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A Dto class for Rated Transaction informations
 *
 * @author R.AITYAAZZA
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 */

@XmlRootElement(name = "RatedTransaction")
@XmlAccessorType(XmlAccessType.FIELD)
public class RatedTransactionDto extends BaseEntityDto implements IEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7627662294414998797L;

    /**
     * Record identifier
     */
    @XmlTransient
    @JsonIgnore
    private Long id;

    /** The usage date. */
    @XmlElement(required = true)
    @Schema(description = "The usage date", required = true)
    private Date usageDate;

    /** The unit amount without tax. */
    @Schema(description = "The unit amount without tax")
    private BigDecimal unitAmountWithoutTax;

    /** The unit amount with tax. */
    @Schema(description = "The unit amount with tax")
    private BigDecimal unitAmountWithTax;

    /** The unit amount tax. */
    @Schema(description = "The unit amount tax")
    private BigDecimal unitAmountTax;

    /** The quantity. */
    @Schema(description = "The quantity")
    private BigDecimal quantity;

    /** The quantity. */
    @Schema(description = "The quantity")
    private BigDecimal inputQuantity;

    /** The raw amount without tax. */
    @Schema(description = "The raw amount without tax")
    private BigDecimal rawAmountWithoutTax;

    /** The raw amount with tax. */
    @Schema(description = "The raw amount with tax")
    private BigDecimal rawAmountWithTax;

    /** The amount without tax. */
    @XmlElement(required = true)
    @Schema(description = "The amount without tax", required = true)
    private BigDecimal amountWithoutTax;

    /** The amount with tax. */
    @XmlElement(required = true)
    @Schema(description = "The amount with tax", required = true)
    private BigDecimal amountWithTax;

    /** The amount tax. */
    @XmlElement(required = true)
    @Schema(description = "The amount tax", required = true)
    private BigDecimal amountTax;

    /** The code. */
    @XmlElement(required = true)
    @Schema(description = "The code", required = true)
    private String code;

    /** The status. */
    @Schema(description = "The status", example = "possible value are: OPEN, BILLED, REJECTED, RERATED, CANCELED")
    private RatedTransactionStatusEnum status;

    /** The description. */
    @Schema(description = "The description")
    private String description;

    /** The unity description. */
    @Schema(description = "The unity description")
    private String unityDescription;

    /** The priceplan code. */
    @Schema(description = "The price plan code")
    private String priceplanCode;

    /** The do not trigger invoicing. */
    @Schema(description = "indicate if we don't want to trigger invoicing", defaultValue = "false")
    private boolean doNotTriggerInvoicing = false;

    /** The start date. */
    @Schema(description = "The start date")
    private Date startDate;

    /** The end date. */
    @Schema(description = "The end date")
    private Date endDate;

    /** parameter1 : used to set more onformations in case of "DETAILLED" invoice. */
    @Schema(description = "used to set more onformations in case of \"DETAILLED\" invoice")
    private String parameter1;

    /** parameter2 : used to set more onformations in case of "DETAILLED" invoice. */
    @Schema(description = "used to set more onformations in case of \"DETAILLED\" invoice")
    private String parameter2;

    /** parameter2 : used to set more onformations in case of "DETAILLED" invoice. */
    @Schema(description = "used to set more onformations in case of \"DETAILLED\" invoice")
    private String parameter3;

    /** The user account code. */
    @Schema(description = "The user account code")
    private String userAccountCode;

    /**
     * Tax applied - code
     */
    @Schema(description = "Tax applied - code")
    private String taxCode;

    /**
     * Tax percent
     */
    @Schema(description = "Tax percent")
    private BigDecimal taxPercent;

    /**
     * Invoice subcategory code
     */
    @Schema(description = "Invoice sub category code")
    private String invoiceSubCategoryCode;

    /**
     * Seller code
     */
    @Schema(description = "Seller code")
    private String sellerCode;
    /**
     * BillingAccount code
     */
    @Schema(description = "BillingAccount code")
    private String billingAccountCode;

    /**
     * Charge tax class code
     */
    @Schema(description = "Charge tax class code")
    private String taxClassCode;

    /**
     * input_unit_unitOfMeasure
     */
    @Schema(description = "The input unit of measure")
    private String inputUnitOfMeasure;

    /**
     * rating_unit_unitOfMeasure
     */
    @Schema(description = "The rating unit of measure")
    private String ratingUnitOfMeasure;

    /**
     * Sorting index.
     */
    @Schema(description = "Sorting index")
    private Integer sortIndex;

    /**
     * Instantiates a new rated transaction dto.
     */
    public RatedTransactionDto() {

    }

    /**
     * Instantiates a new rated transaction dto.
     *
     * @param ratedTransaction the rated transaction
     */
    public RatedTransactionDto(RatedTransaction ratedTransaction) {
        this.id = ratedTransaction.getId();
        this.setUsageDate(ratedTransaction.getUsageDate());
        this.setUnitAmountWithoutTax(ratedTransaction.getUnitAmountWithoutTax());
        this.setUnitAmountWithTax(ratedTransaction.getUnitAmountWithTax());
        this.setUnitAmountTax(ratedTransaction.getUnitAmountTax());
        this.setQuantity(ratedTransaction.getQuantity());
        this.setAmountWithoutTax(ratedTransaction.getAmountWithoutTax());
        this.setAmountWithTax(ratedTransaction.getAmountWithTax());
        this.setAmountTax(ratedTransaction.getAmountTax());
        this.setCode(ratedTransaction.getCode());
        this.setDescription(ratedTransaction.getDescription());
        this.setStatus(ratedTransaction.getStatus());
        this.setUnityDescription(ratedTransaction.getUnityDescription());
        if (ratedTransaction.getPriceplan() != null) {
            this.setPriceplanCode(ratedTransaction.getPriceplan().getCode());
        }
        this.setDoNotTriggerInvoicing(ratedTransaction.isDoNotTriggerInvoicing());
        this.setStartDate(ratedTransaction.getStartDate());
        this.setEndDate(ratedTransaction.getEndDate());
        if (ratedTransaction.getTax() != null) {
            this.setTaxCode(ratedTransaction.getTax().getCode());
        }
        this.setTaxPercent(ratedTransaction.getTaxPercent());
        if (ratedTransaction.getBillingAccount() != null) {
            this.setBillingAccountCode(ratedTransaction.getBillingAccount().getCode());
        }
        if (ratedTransaction.getSeller() != null) {
            this.setSellerCode(ratedTransaction.getSeller().getCode());
        }

        this.setInputQuantity(ratedTransaction.getInputQuantity());
        this.setRawAmountWithoutTax(ratedTransaction.getRawAmountWithoutTax());
        this.setRawAmountWithTax(ratedTransaction.getRawAmountWithTax());
        taxClassCode = ratedTransaction.getTaxClass() != null ? ratedTransaction.getTaxClass().getCode() : null;
        this.inputUnitOfMeasure = ratedTransaction.getInputUnitOfMeasure()!=null?ratedTransaction.getInputUnitOfMeasure().getCode():null;
        this.ratingUnitOfMeasure = ratedTransaction.getRatingUnitOfMeasure()!=null?ratedTransaction.getRatingUnitOfMeasure().getCode():null;
    }

    /**
     * Instantiates a new rated transaction dto from an entity.
     *
     * @param ratedTransaction Rated transaction to convert
     * @param includeUserAccount Include user account code
     * @param includeSeller Include seller code
     * @param includeInvoiceSubCategory Include Invoice subcategory code
     */
    public RatedTransactionDto(RatedTransaction ratedTransaction, boolean includeUserAccount, boolean includeSeller, boolean includeInvoiceSubCategory) {
        this(ratedTransaction);
        if (includeUserAccount && ratedTransaction.getWallet() != null) {
            this.userAccountCode = ratedTransaction.getWallet().getUserAccount().getCode();
        }
        if (includeInvoiceSubCategory) {
            this.invoiceSubCategoryCode = ratedTransaction.getInvoiceSubCategory().getCode();
        }
        if (includeSeller && ratedTransaction.getSeller() != null) {
            this.sellerCode = ratedTransaction.getSeller().getCode();
        }
    }

    /**
     * Gets the usage date.
     *
     * @return the usage date
     */
    public Date getUsageDate() {
        return usageDate;
    }

    /**
     * Sets the usage date.
     *
     * @param usageDate the new usage date
     */
    public void setUsageDate(Date usageDate) {
        this.usageDate = usageDate;
    }

    /**
     * Gets the unit amount without tax.
     *
     * @return the unit amount without tax
     */
    public BigDecimal getUnitAmountWithoutTax() {
        return unitAmountWithoutTax;
    }

    /**
     * Sets the unit amount without tax.
     *
     * @param unitAmountWithoutTax the new unit amount without tax
     */
    public void setUnitAmountWithoutTax(BigDecimal unitAmountWithoutTax) {
        this.unitAmountWithoutTax = unitAmountWithoutTax;
    }

    /**
     * Gets the unit amount with tax.
     *
     * @return the unit amount with tax
     */
    public BigDecimal getUnitAmountWithTax() {
        return unitAmountWithTax;
    }

    /**
     * Sets the unit amount with tax.
     *
     * @param unitAmountWithTax the new unit amount with tax
     */
    public void setUnitAmountWithTax(BigDecimal unitAmountWithTax) {
        this.unitAmountWithTax = unitAmountWithTax;
    }

    /**
     * Gets the unit amount tax.
     *
     * @return the unit amount tax
     */
    public BigDecimal getUnitAmountTax() {
        return unitAmountTax;
    }

    /**
     * Sets the unit amount tax.
     *
     * @param unitAmountTax the new unit amount tax
     */
    public void setUnitAmountTax(BigDecimal unitAmountTax) {
        this.unitAmountTax = unitAmountTax;
    }

    /**
     * Gets the quantity.
     *
     * @return the quantity
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     *
     * @param quantity the new quantity
     */
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the quantity.
     *
     * @return the quantity
     */
    public BigDecimal getInputQuantity() {
        return inputQuantity;
    }

    /**
     * Sets the inputQuantity.
     *
     * @param inputQuantity the new quantity
     */
    public void setInputQuantity(BigDecimal inputQuantity) {
        this.inputQuantity = inputQuantity;
    }

    /**
     * Gets the raw amount without tax.
     *
     * @return the raw amount without tax
     */
    public BigDecimal getRawAmountWithoutTax() {
        return rawAmountWithoutTax;
    }

    /**
     * Sets the raw amount without tax.
     *
     * @param rawAmountWithoutTax the new raw amount without tax
     */
    public void setRawAmountWithoutTax(BigDecimal rawAmountWithoutTax) {
        this.rawAmountWithoutTax = rawAmountWithoutTax;
    }

    /**
     * Gets the raw amount with tax.
     *
     * @return the raw amount with tax
     */
    public BigDecimal getRawAmountWithTax() {
        return rawAmountWithTax;
    }

    /**
     * Sets the raw amount with tax.
     *
     * @param rawAmountWithTax the new raw amount with tax
     */
    public void setRawAmountWithTax(BigDecimal rawAmountWithTax) {
        this.rawAmountWithTax = rawAmountWithTax;
    }

    /**
     * Gets the amount without tax.
     *
     * @return the amount without tax
     */
    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    /**
     * Sets the amount without tax.
     *
     * @param amountWithoutTax the new amount without tax
     */
    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    /**
     * Gets the amount with tax.
     *
     * @return the amount with tax
     */
    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    /**
     * Sets the amount with tax.
     *
     * @param amountWithTax the new amount with tax
     */
    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    /**
     * Gets the amount tax.
     *
     * @return the amount tax
     */
    public BigDecimal getAmountTax() {
        return amountTax;
    }

    /**
     * Sets the amount tax.
     *
     * @param amountTax the new amount tax
     */
    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    /**
     * Checks if is do not trigger invoicing.
     *
     * @return true, if is do not trigger invoicing
     */
    public boolean isDoNotTriggerInvoicing() {
        return doNotTriggerInvoicing;
    }

    /**
     * Sets the do not trigger invoicing.
     *
     * @param doNotTriggerInvoicing the new do not trigger invoicing
     */
    public void setDoNotTriggerInvoicing(boolean doNotTriggerInvoicing) {
        this.doNotTriggerInvoicing = doNotTriggerInvoicing;
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
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the unity description.
     *
     * @return the unity description
     */
    public String getUnityDescription() {
        return unityDescription;
    }

    /**
     * Sets the unity description.
     *
     * @param unityDescription the new unity description
     */
    public void setUnityDescription(String unityDescription) {
        this.unityDescription = unityDescription;
    }

    /**
     * Gets the priceplan code.
     *
     * @return the priceplan code
     */
    public String getPriceplanCode() {
        return priceplanCode;
    }

    /**
     * Sets the priceplan code.
     *
     * @param priceplanCode the new priceplan code
     */
    public void setPriceplanCode(String priceplanCode) {
        this.priceplanCode = priceplanCode;
    }

    /**
     * Gets the start date.
     *
     * @return the start date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date.
     *
     * @param startDate the start date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date.
     *
     * @return the end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date.
     *
     * @param endDate the end date
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets the parameter 1.
     *
     * @return the parameter 1
     */
    public String getParameter1() {
        return parameter1;
    }

    /**
     * Sets the parameter 1.
     *
     * @param parameter1 the new parameter 1
     */
    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    /**
     * Gets the parameter 2.
     *
     * @return the parameter 2
     */
    public String getParameter2() {
        return parameter2;
    }

    /**
     * Sets the parameter 2.
     *
     * @param parameter2 the new parameter 2
     */
    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    /**
     * Gets the parameter 3.
     *
     * @return the parameter 3
     */
    public String getParameter3() {
        return parameter3;
    }

    /**
     * Sets the parameter 3.
     *
     * @param parameter3 the new parameter 3
     */
    public void setParameter3(String parameter3) {
        this.parameter3 = parameter3;
    }

    /**
     * @return the userAccountCode
     */
    public String getUserAccountCode() {
        return userAccountCode;
    }

    /**
     * @param userAccountCode the userAccountCode to set
     */
    public void setUserAccountCode(String userAccountCode) {
        this.userAccountCode = userAccountCode;
    }

    /**
     * @return Tax applied - code
     */
    public String getTaxCode() {
        return taxCode;
    }

    /**
     * @param taxCode Tax applied - code
     */
    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    /**
     * @return Tax percent applied
     */
    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    /**
     * @param taxPercent Tax percent applied
     */
    public void setTaxPercent(BigDecimal taxPercent) {
        this.taxPercent = taxPercent;
    }

    /**
     * @return Record identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id Record identifier
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Invoice subcategory code
     */
    public String getInvoiceSubCategoryCode() {
        return invoiceSubCategoryCode;
    }

    /**
     * @param invoiceSubCategoryCode Invoice subcategory code
     */
    public void setInvoiceSubCategoryCode(String invoiceSubCategoryCode) {
        this.invoiceSubCategoryCode = invoiceSubCategoryCode;
    }

    /**
     * @return Seller code
     */
    public String getSellerCode() {
        return sellerCode;
    }

    /**
     * @param sellerCode Seller code
     */
    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    /**
     * @return Billing Account code
     */
    public String getBillingAccountCode() {
        return billingAccountCode;
    }

    /**
     * @param billingAccountCode billing account code
     */
    public void setBillingAccountCode(String billingAccountCode) {
        this.billingAccountCode = billingAccountCode;
    }

    /**
     * @return the status
     */
    public RatedTransactionStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(RatedTransactionStatusEnum status) {
        this.status = status;
    }

    /**
     * @return Charge tax class code
     */
    public String getTaxClassCode() {
        return taxClassCode;
    }

    /**
     * @param taxClassCode Charge tax class code
     */
    public void setTaxClassCode(String taxClassCode) {
        this.taxClassCode = taxClassCode;
    }

	/**
	 * @return the ratingUnitOfMeasure
	 */
	public String getRatingUnitOfMeasure() {
		return ratingUnitOfMeasure;
	}

	/**
	 * @param ratingUnitOfMeasure the ratingUnitOfMeasure to set
	 */
	public void setRatingUnitOfMeasure(String ratingUnitOfMeasure) {
		this.ratingUnitOfMeasure = ratingUnitOfMeasure;
	}

	/**
	 * @return the inputUnitOfMeasure
	 */
    public String getInputUnitOfMeasure() {
        return inputUnitOfMeasure;
    }

    /**
     * @param inputUnitOfMeasure the inputUnitOfMeasure to set
     */
    public void setInputUnitOfMeasure(String inputUnitOfMeasure) {
        this.inputUnitOfMeasure = inputUnitOfMeasure;
    }

    /**
     * Gets the sorting index.
     *
     * @return the sorting index
     */
    public Integer getSortIndex() {
        return sortIndex;
    }

    /**
     * Sets the sorting index.
     *
     * @param sortIndex the sorting index.
     */
    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }
}