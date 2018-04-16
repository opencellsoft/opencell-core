package org.meveo.api.dto.catalog;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.api.dto.CustomFieldsDto;
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
public class PricePlanMatrixDto extends BusinessDto {

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

    /** The amount with tax EL. */
    private String amountWithTaxEL;

    /** The minimum amount without tax el. */
    private String minimumAmountWithoutTaxEl;

    /** The minimum amount with tax el. */
    private String minimumAmountWithTaxEl;

    /** The priority. */
    private int priority;

    /** The criteria 1. */
    private String criteria1;

    /** The criteria 2. */
    private String criteria2;

    /** The criteria 3. */
    private String criteria3;

    /** The criteria EL. */
    private String criteriaEL;

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

    /**
     * If this EL is not null, evaluate and set in WalletOperation amounts during amount calculation in RatingService.
     */
    private String ratingEL;

    /**
     * Instantiates a new price plan matrix dto.
     */
    public PricePlanMatrixDto() {

    }

    /**
     * Instantiates a new price plan matrix dto.
     *
     * @param pricePlan the price plan
     * @param customFieldInstances the custom field instances
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
        amountWithTax = pricePlan.getAmountWithTax();
        amountWithoutTaxEL = pricePlan.getAmountWithoutTaxEL();
        amountWithTaxEL = pricePlan.getAmountWithTaxEL();
        priority = pricePlan.getPriority();
        criteria1 = pricePlan.getCriteria1Value();
        criteria2 = pricePlan.getCriteria2Value();
        criteria3 = pricePlan.getCriteria3Value();
        if (pricePlan.getValidityCalendar() != null) {
            validityCalendarCode = pricePlan.getValidityCalendar().getCode();
        }

        criteriaEL = pricePlan.getCriteriaEL();
        if (pricePlan.getScriptInstance() != null) {
            scriptInstance = pricePlan.getScriptInstance().getCode();
        }
        customFields = customFieldInstances;
        setLanguageDescriptions(LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(pricePlan.getDescriptionI18n()));
        woDescriptionEL = pricePlan.getWoDescriptionEL();
        ratingEL = pricePlan.getRatingEL();
        minimumAmountWithoutTaxEl = pricePlan.getMinimumAmountWithoutTaxEl();
        minimumAmountWithTaxEl = pricePlan.getMinimumAmountWithTaxEl();
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
     * Gets the amount without tax EL.
     *
     * @return the amount without tax EL
     */
    public String getAmountWithoutTaxEL() {
        return amountWithoutTaxEL;
    }

    /**
     * Sets the amount without tax EL.
     *
     * @param amountWithoutTaxEL the new amount without tax EL
     */
    public void setAmountWithoutTaxEL(String amountWithoutTaxEL) {
        this.amountWithoutTaxEL = amountWithoutTaxEL;
    }

    /**
     * Gets the amount with tax EL.
     *
     * @return the amount with tax EL
     */
    public String getAmountWithTaxEL() {
        return amountWithTaxEL;
    }

    /**
     * Sets the amount with tax EL.
     *
     * @param amountWithTaxEL the new amount with tax EL
     */
    public void setAmountWithTaxEL(String amountWithTaxEL) {
        this.amountWithTaxEL = amountWithTaxEL;
    }

    /**
     * Gets the priority.
     *
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the priority.
     *
     * @param priority the new priority
     */
    public void setPriority(int priority) {
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
     * Gets the criteria EL.
     *
     * @return the criteria EL
     */
    public String getCriteriaEL() {
        return criteriaEL;
    }

    /**
     * Sets the criteria EL.
     *
     * @param criteriaEL the new criteria EL
     */
    public void setCriteriaEL(String criteriaEL) {
        this.criteriaEL = criteriaEL;
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
     * Gets the wo description EL.
     *
     * @return the wo description EL
     */
    public String getWoDescriptionEL() {
        return woDescriptionEL;
    }

    /**
     * Sets the wo description EL.
     *
     * @param woDescriptionEL the new wo description EL
     */
    public void setWoDescriptionEL(String woDescriptionEL) {
        this.woDescriptionEL = woDescriptionEL;
    }

    /**
     * Gets the rating EL.
     *
     * @return the rating EL
     */
    public String getRatingEL() {
        return ratingEL;
    }

    /**
     * Sets the rating EL.
     *
     * @param ratingEL the new rating EL
     */
    public void setRatingEL(String ratingEL) {
        this.ratingEL = ratingEL;
    }

    /**
     * Gets the minimum amount without tax el.
     *
     * @return the minimum amount without tax el
     */
    public String getMinimumAmountWithoutTaxEl() {
        return minimumAmountWithoutTaxEl;
    }

    /**
     * Sets the minimum amount without tax el.
     *
     * @param minimumAmountWithoutTaxEl the new minimum amount without tax el
     */
    public void setMinimumAmountWithoutTaxEl(String minimumAmountWithoutTaxEl) {
        this.minimumAmountWithoutTaxEl = minimumAmountWithoutTaxEl;
    }

    /**
     * Gets the minimum amount with tax el.
     *
     * @return the minimum amount with tax el
     */
    public String getMinimumAmountWithTaxEl() {
        return minimumAmountWithTaxEl;
    }

    /**
     * Sets the minimum amount with tax el.
     *
     * @param minimumAmountWithTaxEl the new minimum amount with tax el
     */
    public void setMinimumAmountWithTaxEl(String minimumAmountWithTaxEl) {
        this.minimumAmountWithTaxEl = minimumAmountWithTaxEl;
    }
    
    @Override
    public String toString() {
        return "PricePlanDto [code=" + code + ", eventCode=" + eventCode + ", description=" + description + ", seller=" + seller + ", country=" + country + ", currency=" + currency
                + ", minQuantity=" + minQuantity + ", maxQuantity=" + maxQuantity + ", offerTemplate=" + offerTemplateVersion + ", startSubscriptionDate=" + startSubscriptionDate
                + ", endSubscriptionDate=" + endSubscriptionDate + ", startRatingDate=" + startRatingDate + ", endRatingDate=" + endRatingDate + ", minSubscriptionAgeInMonth="
                + minSubscriptionAgeInMonth + ", maxSubscriptionAgeInMonth=" + maxSubscriptionAgeInMonth + ", amountWithoutTax=" + amountWithoutTax + ", amountWithTax="
                + amountWithTax + ", amountWithoutTaxEL=" + amountWithoutTaxEL + ", amountWithTaxEL=" + amountWithTaxEL + ", priority=" + priority + ", criteria1=" + criteria1
                + ", criteria2=" + criteria2 + ", criteria3=" + criteria3 + ", validityCalendarCode=" + validityCalendarCode + ", scriptInstance=" + scriptInstance + "]";
    }
}