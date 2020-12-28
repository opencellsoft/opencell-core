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
import java.util.Date;
import java.util.List;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.model.catalog.PricePlanMatrix;

/**
 * DTO for {@link PricePlanMatrix}.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "PricePlan")
@XmlAccessorType(XmlAccessType.FIELD)
public class PricePlanMatrixDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -9089693491690592072L;

    /** The event code. */
    @XmlElement(required = true)
    private String eventCode;

    /** The seller. */
    private String seller;

    /** The country. */
    private String country;

    /** The currency. */
    private String currency;

    /** The min quantity. */
    private BigDecimal minQuantity;

    /** The max quantity. */
    private BigDecimal maxQuantity;

    /** The offer template. */
    private String offerTemplate;

    /** The offer template version. */
    private OfferTemplateDto offerTemplateVersion;

    /** The start subscription date. */
    private Date startSubscriptionDate;

    /** The end subscription date. */
    private Date endSubscriptionDate;

    /** The start rating date. */
    private Date startRatingDate;

    /** The end rating date. */
    private Date endRatingDate;

    /** The min subscription age in month. */
    private Long minSubscriptionAgeInMonth;

    /** The max subscription age in month. */
    private Long maxSubscriptionAgeInMonth;

    /** The amount without tax. */
    private BigDecimal amountWithoutTax;

    /** The amount with tax. */
    private BigDecimal amountWithTax;

    /** The amount without tax EL. */
    private String amountWithoutTaxEL;

    /** The amount without tax EL - for Spark */
    private String amountWithoutTaxELSpark;

    /** The amount with tax EL. */
    private String amountWithTaxEL;

    /** The amount with tax EL - for Spark */
    private String amountWithTaxELSpark;

    /** The minimum amount without tax el. */
    private String minimumAmountWithoutTaxEl;

    /** The minimum amount without tax el - for Spark */
    private String minimumAmountWithoutTaxELSpark;

    /** The minimum amount with tax el. */
    private String minimumAmountWithTaxEl;

    /** The minimum amount with tax el - for Spark */
    private String minimumAmountWithTaxELSpark;

    /** The priority. */
    private Integer priority;

    /** The criteria 1. */
    private String criteria1;

    /** The criteria 2. */
    private String criteria2;

    /** The criteria 3. */
    private String criteria3;

    /** The criteria EL. */
    private String criteriaEL;

    /** The criteria EL - for Spark */
    private String criteriaELSpark;

    /** The validity calendar code. */
    private String validityCalendarCode;

    /** The script instance. */
    private String scriptInstance;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /** The language descriptions. */
    private List<LanguageDescriptionDto> languageDescriptions;

    /** The wo description EL. */
    private String woDescriptionEL;

    /** The wo description EL - for Spark */
    private String woDescriptionELSpark;

    /**
     * Expression to calculate price with tax.
     */
    @Size(max = 2000)
    private String ratingWithTaxEL;

    /**
     * Expression to calculate price with tax - for Spark.
     */
    @Size(max = 2000)
    private String ratingWithTaxELSpark;

    /**
     * Expression to calculate price without tax
     */
    @Size(max = 2000)
    private String ratingWithoutTaxEL;

    /**
     * Expression to calculate price without tax - for Spark.
     */
    @Size(max = 2000)
    private String ratingWithoutTaxELSpark;

    /**
     * Expression for getting the InvoiceSubCategory.
     */
    private String invoiceSubCategoryEL;
    
    /**
     * Expression to calculate price with/without tax. It overrides quantity x unitPrice when set.
     */
    private String totalAmountEL;
    
    /**
     * Expression to calculate price with/without tax, It overrides quantity x unitPrice when set - for Spark.
     */
    private String totalAmountELSpark;
    
    /**
     * Minimum allowed amount for a walletOperation. If this amount is less than the
     * walletOperation this amount is save and the old value is save in rawAmount.
     */
    private String minimumAmountEL;

    /**
     * Minimum allowed amount for a walletOperation. If this amount is less than the
     * walletOperation this amount is save and the old value is save in rawAmount - for Spark.
     */
    private String minimumAmountELSpark;

    /**
     * An El expression used to override wallet operation's parameter1El.
     */
    private String parameter1El;

    /**
     * An El expression used to override wallet operation's parameter2El.
     */
    private String parameter2El;

    /**
     * An El expression used to override wallet operation's parameter3El.
     */
    private String parameter3El;

    /**
     * Instantiates a new price plan matrix dto.
     */
    public PricePlanMatrixDto() {

    }

    /**
     * Convert PricePlanMatrix entity to DTO including its custom field values
     * 
     * @param pricePlan Price plan entity
     * @param customFieldInstances Custom field values
     */
    public PricePlanMatrixDto(PricePlanMatrix pricePlan, CustomFieldsDto customFieldInstances) {
        super(pricePlan);

        eventCode = pricePlan.getEventCode();
        if (pricePlan.getSeller() != null) {
            seller = pricePlan.getSeller().getCode();
        }
        if (pricePlan.getTradingCountry() != null) {
            country = pricePlan.getTradingCountry().getCountryCode();
        }
        if (pricePlan.getTradingCurrency() != null) {
            currency = pricePlan.getTradingCurrency().getCurrencyCode();
        }
        if (pricePlan.getOfferTemplate() != null) {
            offerTemplateVersion = new OfferTemplateDto(pricePlan.getOfferTemplate(), null, true);
            offerTemplate = pricePlan.getOfferTemplate().getCode();
        }
        minQuantity = pricePlan.getMinQuantity();
        maxQuantity = pricePlan.getMaxQuantity();
        startSubscriptionDate = pricePlan.getStartRatingDate();
        endSubscriptionDate = pricePlan.getEndSubscriptionDate();
        startRatingDate = pricePlan.getStartRatingDate();
        endRatingDate = pricePlan.getEndRatingDate();
        minSubscriptionAgeInMonth = pricePlan.getMinSubscriptionAgeInMonth();
        maxSubscriptionAgeInMonth = pricePlan.getMaxSubscriptionAgeInMonth();
        amountWithoutTax = pricePlan.getAmountWithoutTax();
        amountWithoutTax = pricePlan.getAmountWithoutTax();
        amountWithTax = pricePlan.getAmountWithTax();
        amountWithTax = pricePlan.getAmountWithTax();
        amountWithoutTaxEL = pricePlan.getAmountWithoutTaxEL();
        amountWithoutTaxELSpark = pricePlan.getAmountWithoutTaxELSpark();
        amountWithTaxEL = pricePlan.getAmountWithTaxEL();
        amountWithTaxELSpark = pricePlan.getAmountWithTaxELSpark();
        priority = pricePlan.getPriority();
        criteria1 = pricePlan.getCriteria1Value();
        criteria2 = pricePlan.getCriteria2Value();
        criteria3 = pricePlan.getCriteria3Value();
        if (pricePlan.getValidityCalendar() != null) {
            validityCalendarCode = pricePlan.getValidityCalendar().getCode();
        }

        criteriaEL = pricePlan.getCriteriaEL();
        criteriaELSpark = pricePlan.getCriteriaELSpark();
        if (pricePlan.getScriptInstance() != null) {
            scriptInstance = pricePlan.getScriptInstance().getCode();
        }
        customFields = customFieldInstances;
        setLanguageDescriptions(LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(pricePlan.getDescriptionI18n()));
        woDescriptionEL = pricePlan.getWoDescriptionEL();
        woDescriptionELSpark = pricePlan.getWoDescriptionELSpark();
        invoiceSubCategoryEL = pricePlan.getInvoiceSubCategoryEL();
        totalAmountEL = pricePlan.getTotalAmountEL();
        totalAmountELSpark = pricePlan.getTotalAmountELSpark();
        minimumAmountEL = pricePlan.getMinimumAmountEL();
        minimumAmountELSpark = pricePlan.getMinimumAmountELSpark();
        parameter1El = pricePlan.getParameter1El();
        parameter2El = pricePlan.getParameter2El();
        parameter3El = pricePlan.getParameter3El();
    }

    /**
     * Gets the event code.
     *
     * @return the event code
     */
    public String getEventCode() {
        return eventCode;
    }

    /**
     * Sets the event code.
     *
     * @param eventCode the new event code
     */
    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    /**
     * Gets the seller.
     *
     * @return the seller
     */
    public String getSeller() {
        return seller;
    }

    /**
     * Sets the seller.
     *
     * @param seller the new seller
     */
    public void setSeller(String seller) {
        this.seller = seller;
    }

    /**
     * Gets the country.
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country.
     *
     * @param country the new country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the currency.
     *
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency.
     *
     * @param currency the new currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Gets the min quantity.
     *
     * @return the min quantity
     */
    public BigDecimal getMinQuantity() {
        return minQuantity;
    }

    /**
     * Sets the min quantity.
     *
     * @param minQuantity the new min quantity
     */
    public void setMinQuantity(BigDecimal minQuantity) {
        this.minQuantity = minQuantity;
    }

    /**
     * Gets the max quantity.
     *
     * @return the max quantity
     */
    public BigDecimal getMaxQuantity() {
        return maxQuantity;
    }

    /**
     * Sets the max quantity.
     *
     * @param maxQuantity the new max quantity
     */
    public void setMaxQuantity(BigDecimal maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    /**
     * Gets the offer template version.
     *
     * @return the offer template version
     */
    public OfferTemplateDto getOfferTemplateVersion() {
        return offerTemplateVersion;
    }

    /**
     * Sets the offer template version.
     *
     * @param offerTemplateVersion the new offer template version
     */
    public void setOfferTemplateVersion(OfferTemplateDto offerTemplateVersion) {
        this.offerTemplateVersion = offerTemplateVersion;
    }

    /**
     * Gets the offer template.
     *
     * @return the offer template
     */
    public String getOfferTemplate() {
        return offerTemplate;
    }

    /**
     * Sets the offer template.
     *
     * @param offerTemplate the new offer template
     */
    public void setOfferTemplate(String offerTemplate) {
        this.offerTemplate = offerTemplate;
    }

    /**
     * Gets the start subscription date.
     *
     * @return the start subscription date
     */
    public Date getStartSubscriptionDate() {
        return startSubscriptionDate;
    }

    /**
     * Sets the start subscription date.
     *
     * @param startSubscriptionDate the new start subscription date
     */
    public void setStartSubscriptionDate(Date startSubscriptionDate) {
        this.startSubscriptionDate = startSubscriptionDate;
    }

    /**
     * Gets the end subscription date.
     *
     * @return the end subscription date
     */
    public Date getEndSubscriptionDate() {
        return endSubscriptionDate;
    }

    /**
     * Sets the end subscription date.
     *
     * @param endSubscriptionDate the new end subscription date
     */
    public void setEndSubscriptionDate(Date endSubscriptionDate) {
        this.endSubscriptionDate = endSubscriptionDate;
    }

    /**
     * Gets the start rating date.
     *
     * @return the start rating date
     */
    public Date getStartRatingDate() {
        return startRatingDate;
    }

    /**
     * Sets the start rating date.
     *
     * @param startRatingDate the new start rating date
     */
    public void setStartRatingDate(Date startRatingDate) {
        this.startRatingDate = startRatingDate;
    }

    /**
     * Gets the end rating date.
     *
     * @return the end rating date
     */
    public Date getEndRatingDate() {
        return endRatingDate;
    }

    /**
     * Sets the end rating date.
     *
     * @param endRatingDate the new end rating date
     */
    public void setEndRatingDate(Date endRatingDate) {
        this.endRatingDate = endRatingDate;
    }

    /**
     * Gets the min subscription age in month.
     *
     * @return the min subscription age in month
     */
    public Long getMinSubscriptionAgeInMonth() {
        return minSubscriptionAgeInMonth;
    }

    /**
     * Sets the min subscription age in month.
     *
     * @param minSubscriptionAgeInMonth the new min subscription age in month
     */
    public void setMinSubscriptionAgeInMonth(Long minSubscriptionAgeInMonth) {
        this.minSubscriptionAgeInMonth = minSubscriptionAgeInMonth;
    }

    /**
     * Gets the max subscription age in month.
     *
     * @return the max subscription age in month
     */
    public Long getMaxSubscriptionAgeInMonth() {
        return maxSubscriptionAgeInMonth;
    }

    /**
     * Sets the max subscription age in month.
     *
     * @param maxSubscriptionAgeInMonth the new max subscription age in month
     */
    public void setMaxSubscriptionAgeInMonth(Long maxSubscriptionAgeInMonth) {
        this.maxSubscriptionAgeInMonth = maxSubscriptionAgeInMonth;
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
     * @return Expression to calculate the amount without tax
     */
    public String getAmountWithoutTaxEL() {
        return amountWithoutTaxEL;
    }

    /**
     * @param amountWithoutTaxEL Expression to calculate the amount without tax
     */
    public void setAmountWithoutTaxEL(String amountWithoutTaxEL) {
        this.amountWithoutTaxEL = amountWithoutTaxEL;
    }

    /**
     * @return Expression to calculate the amount without tax - for Spark
     */
    public String getAmountWithoutTaxELSpark() {
        return amountWithoutTaxELSpark;
    }

    /**
     * @param amountWithoutTaxELSpark Expression to calculate the amount without tax - for Spark
     */
    public void setAmountWithoutTaxELSpark(String amountWithoutTaxELSpark) {
        this.amountWithoutTaxELSpark = amountWithoutTaxELSpark;
    }

    /**
     * @return Expression to calculate the amount with tax
     */
    public String getAmountWithTaxEL() {
        return amountWithTaxEL;
    }

    /**
     * @param amountWithTaxEL Expression to calculate the amount with tax
     */
    public void setAmountWithTaxEL(String amountWithTaxEL) {
        this.amountWithTaxEL = amountWithTaxEL;
    }

    /**
     * @return Expression to calculate the amount with tax - for Spark
     */
    public String getAmountWithTaxELSpark() {
        return amountWithTaxELSpark;
    }

    /**
     * @param amountWithTaxELSpark Expression to calculate the amount with tax - for Spark
     */
    public void setAmountWithTaxELSpark(String amountWithTaxELSpark) {
        this.amountWithTaxELSpark = amountWithTaxELSpark;
    }

    /**
     * Gets the priority.
     *
     * @return the priority
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * Sets the priority.
     *
     * @param priority the new priority
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * Gets the criteria 1.
     *
     * @return the criteria 1
     */
    public String getCriteria1() {
        return criteria1;
    }

    /**
     * Sets the criteria 1.
     *
     * @param criteria1 the new criteria 1
     */
    public void setCriteria1(String criteria1) {
        this.criteria1 = criteria1;
    }

    /**
     * Gets the criteria 2.
     *
     * @return the criteria 2
     */
    public String getCriteria2() {
        return criteria2;
    }

    /**
     * Sets the criteria 2.
     *
     * @param criteria2 the new criteria 2
     */
    public void setCriteria2(String criteria2) {
        this.criteria2 = criteria2;
    }

    /**
     * Gets the criteria 3.
     *
     * @return the criteria 3
     */
    public String getCriteria3() {
        return criteria3;
    }

    /**
     * Sets the criteria 3.
     *
     * @param criteria3 the new criteria 3
     */
    public void setCriteria3(String criteria3) {
        this.criteria3 = criteria3;
    }

    /**
     * Gets the validity calendar code.
     *
     * @return the validity calendar code
     */
    public String getValidityCalendarCode() {
        return validityCalendarCode;
    }

    /**
     * Sets the validity calendar code.
     *
     * @param validityCalendarCode the new validity calendar code
     */
    public void setValidityCalendarCode(String validityCalendarCode) {
        this.validityCalendarCode = validityCalendarCode;
    }

    /**
     * @return Expression to determine if Price plan applies
     */
    public String getCriteriaEL() {
        return criteriaEL;
    }

    /**
     * @param criteriaEL Expression to determine if Price plan applies
     */
    public void setCriteriaEL(String criteriaEL) {
        this.criteriaEL = criteriaEL;
    }

    /**
     * @return Expression to determine if Price plan applies - for Spark
     */
    public String getCriteriaELSpark() {
        return criteriaELSpark;
    }

    /**
     * @param criteriaELSpark Expression to determine if Price plan applies - for Spark
     */
    public void setCriteriaELSpark(String criteriaELSpark) {
        this.criteriaELSpark = criteriaELSpark;
    }

    /**
     * Gets the script instance.
     *
     * @return the script instance
     */
    public String getScriptInstance() {
        return scriptInstance;
    }

    /**
     * Sets the script instance.
     *
     * @param scriptInstance the new script instance
     */
    public void setScriptInstance(String scriptInstance) {
        this.scriptInstance = scriptInstance;
    }

    /**
     * Gets the custom fields.
     *
     * @return the custom fields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the new custom fields
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /**
     * Gets the language descriptions.
     *
     * @return the language descriptions
     */
    public List<LanguageDescriptionDto> getLanguageDescriptions() {
        return languageDescriptions;
    }

    /**
     * Sets the language descriptions.
     *
     * @param languageDescriptions the new language descriptions
     */
    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
        this.languageDescriptions = languageDescriptions;
    }

    /**
     * @return Expression to determine Wallet operation description
     */
    public String getWoDescriptionEL() {
        return woDescriptionEL;
    }

    /**
     * @param woDescriptionEL Expression to determine Wallet operation description
     */
    public void setWoDescriptionEL(String woDescriptionEL) {
        this.woDescriptionEL = woDescriptionEL;
    }

    /**
     * @return Expression to determine Wallet operation description - for Spark
     */
    public String getWoDescriptionELSpark() {
        return woDescriptionELSpark;
    }

    /**
     * @param woDescriptionELSpark Expression to determine Wallet operation description - for Spark
     */
    public void setWoDescriptionELSpark(String woDescriptionELSpark) {
        this.woDescriptionELSpark = woDescriptionELSpark;
    }

    /**
     * @return Expression to calculate price with tax
     */
    public String getRatingWithTaxEL() {
        return ratingWithTaxEL;
    }

    /**
     * @param ratingELWithTax Expression to calculate price with tax
     */
    public void setRatingWithTaxEL(String ratingELWithTax) {
        this.ratingWithTaxEL = ratingELWithTax;
    }

    /**
     * @return Expression to calculate price with tax - for Spark
     */
    public String getRatingWithTaxELSpark() {
        return ratingWithTaxELSpark;
    }

    /**
     * @param ratingWithTaxELSpark Expression to calculate price with tax - for Spark
     */
    public void setRatingWithTaxELSpark(String ratingWithTaxELSpark) {
        this.ratingWithTaxELSpark = ratingWithTaxELSpark;
    }

    /**
     * @return Expression to calculate price without tax
     */
    public String getRatingWithoutTaxEL() {
        return ratingWithoutTaxEL;
    }

    /**
     * @param ratingELWithoutTax Expression to calculate price without tax
     */
    public void setRatingWithoutTaxEL(String ratingELWithoutTax) {
        this.ratingWithoutTaxEL = ratingELWithoutTax;
    }

    /**
     * @return Expression to calculate price without tax - for Spark
     */
    public String getRatingWithoutTaxELSpark() {
        return ratingWithoutTaxELSpark;
    }

    /**
     * @param ratingWithoutTaxELSpark Expression to calculate price without tax - for Spark
     */
    public void setRatingWithoutTaxELSpark(String ratingWithoutTaxELSpark) {
        this.ratingWithoutTaxELSpark = ratingWithoutTaxELSpark;
    }

    /**
     * @return Expression to calculate minimum amount without tax
     */
    public String getMinimumAmountWithoutTaxEl() {
        return minimumAmountWithoutTaxEl;
    }

    /**
     * @param minimumAmountWithoutTaxEl Expression to calculate minimum amount without tax
     */
    public void setMinimumAmountWithoutTaxEl(String minimumAmountWithoutTaxEl) {
        this.minimumAmountWithoutTaxEl = minimumAmountWithoutTaxEl;
    }

    /**
     * @return Expression to calculate minimum amount without tax - for Spark
     */
    public String getMinimumAmountWithoutTaxELSpark() {
        return minimumAmountWithoutTaxELSpark;
    }

    /**
     * @param minimumAmountWithoutTaxELSpark Expression to calculate minimum amount without tax - for Spark
     */
    public void setMinimumAmountWithoutTaxELSpark(String minimumAmountWithoutTaxELSpark) {
        this.minimumAmountWithoutTaxELSpark = minimumAmountWithoutTaxELSpark;
    }

    /**
     * @return Expression to calculate minimum amount with tax
     */
    public String getMinimumAmountWithTaxEl() {
        return minimumAmountWithTaxEl;
    }

    /**
     * @param minimumAmountWithTaxEl Expression to calculate minimum amount with tax
     */
    public void setMinimumAmountWithTaxEl(String minimumAmountWithTaxEl) {
        this.minimumAmountWithTaxEl = minimumAmountWithTaxEl;
    }

    /**
     * @return Expression to calculate minimum amount with tax - for Spark
     */
    public String getMinimumAmountWithTaxELSpark() {
        return minimumAmountWithTaxELSpark;
    }

    /**
     * @param minimumAmountWithTaxELSpark Expression to calculate minimum amount with taxL - for Spark
     */
    public void setMinimumAmountWithTaxELSpark(String minimumAmountWithTaxELSpark) {
        this.minimumAmountWithTaxELSpark = minimumAmountWithTaxELSpark;
    }

    @Override
    public String toString() {
        return "PricePlanMatrixDto [eventCode=" + eventCode + ", seller=" + seller + ", country=" + country + ", currency=" + currency + ", minQuantity=" + minQuantity
                + ", maxQuantity=" + maxQuantity + ", offerTemplate=" + offerTemplate + ", offerTemplateVersion=" + offerTemplateVersion + ", startSubscriptionDate="
                + startSubscriptionDate + ", endSubscriptionDate=" + endSubscriptionDate + ", startRatingDate=" + startRatingDate + ", endRatingDate=" + endRatingDate
                + ", minSubscriptionAgeInMonth=" + minSubscriptionAgeInMonth + ", maxSubscriptionAgeInMonth=" + maxSubscriptionAgeInMonth + ", amountWithoutTax=" + amountWithoutTax
                + ", amountWithTax=" + amountWithTax + ", amountWithoutTaxEL=" + amountWithoutTaxEL + ", amountWithoutTaxELSpark=" + amountWithoutTaxELSpark + ", amountWithTaxEL="
                + amountWithTaxEL + ", amountWithTaxELSpark=" + amountWithTaxELSpark + ", minimumAmountWithoutTaxEl=" + minimumAmountWithoutTaxEl
                + ", minimumAmountWithoutTaxELSpark=" + minimumAmountWithoutTaxELSpark + ", minimumAmountWithTaxEl=" + minimumAmountWithTaxEl + ", minimumAmountWithTaxELSpark="
                + minimumAmountWithTaxELSpark + ", priority=" + priority + ", criteria1=" + criteria1 + ", criteria2=" + criteria2 + ", criteria3=" + criteria3 + ", criteriaEL="
                + criteriaEL + ", criteriaELSpark=" + criteriaELSpark + ", validityCalendarCode=" + validityCalendarCode + ", scriptInstance=" + scriptInstance + ", customFields="
                + customFields + ", languageDescriptions=" + languageDescriptions + ", woDescriptionEL=" + woDescriptionEL + ", woDescriptionELSpark=" + woDescriptionELSpark
                + ", ratingWithTaxEL=" + ratingWithTaxEL + ", ratingWithTaxELSpark=" + ratingWithTaxELSpark + ", ratingWithoutTaxEL=" + ratingWithoutTaxEL
                + ", ratingWithoutTaxELSpark=" + ratingWithoutTaxELSpark + "]";
    }

	public String getInvoiceSubCategoryEL() {
		return invoiceSubCategoryEL;
	}

	public void setInvoiceSubCategoryEL(String invoiceSubCategoryEL) {
		this.invoiceSubCategoryEL = invoiceSubCategoryEL;
	}

	public String getTotalAmountEL() {
		return totalAmountEL;
	}

	public void setTotalAmountEL(String totalAmountEL) {
		this.totalAmountEL = totalAmountEL;
	}

	public String getTotalAmountELSpark() {
		return totalAmountELSpark;
	}

	public void setTotalAmountELSpark(String totalAmountELSpark) {
		this.totalAmountELSpark = totalAmountELSpark;
	}

	public String getMinimumAmountEL() {
		return minimumAmountEL;
	}

	public void setMinimumAmountEL(String minimumAmountEL) {
        this.minimumAmountEL = minimumAmountEL;
    }

    public String getMinimumAmountELSpark() {
        return minimumAmountELSpark;
    }

    public void setMinimumAmountELSpark(String minimumAmountELSpark) {
        this.minimumAmountELSpark = minimumAmountELSpark;
    }

    /**
     * Gets the parameter1El EL expression.
     *
     * @return an El expression
     */
    public String getParameter1El() {
        return parameter1El;
    }

    /**
     * Sets the parameter1El EL expression.
     *
     * @param parameter1El an El expression
     */
    public void setParameter1El(String parameter1El) {
        this.parameter1El = parameter1El;
    }

    /**
     * Gets the parameter2El EL expression.
     *
     * @return an El expression
     */
    public String getParameter2El() {
        return parameter2El;
    }

    /**
     * Sets the parameter2El EL expression.
     *
     * @param parameter2El an El expression
     */
    public void setParameter2El(String parameter2El) {
        this.parameter2El = parameter2El;
    }

    /**
     * Gets the parameter3El EL expression.
     *
     * @return an El expression
     */
    public String getParameter3El() {
        return parameter3El;
    }

    /**
     * Sets the parameter3El EL expression.
     *
     * @param parameter3El an El expression
     */
    public void setParameter3El(String parameter3El) {
        this.parameter3El = parameter3El;
    }
}