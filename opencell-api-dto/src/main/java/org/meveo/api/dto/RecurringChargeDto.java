package org.meveo.api.dto;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class RecurringChargeDto.
 *
 * @author Edward P. Legaspi
 * @since Nov 4, 2013
 */
@XmlRootElement(name = "RecurringCharge")
@XmlAccessorType(XmlAccessType.FIELD)
public class RecurringChargeDto {

    /** The currency code. */
    private String currencyCode;
    
    /** The start date. */
    private Date startDate;
    
    /** The end date. */
    private Date endDate;
    
    /** The min age. */
    private Integer minAge;
    
    /** The max age. */
    private Integer maxAge;
    
    /** The price. */
    private BigDecimal price;
    
    /** The recommended price. */
    private BigDecimal recommendedPrice;

    /**
     * Gets the currency code.
     *
     * @return the currency code
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Sets the currency code.
     *
     * @param currencyCode the new currency code
     */
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
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
     * @param startDate the new start date
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
     * @param endDate the new end date
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets the min age.
     *
     * @return the min age
     */
    public Integer getMinAge() {
        return minAge;
    }

    /**
     * Sets the min age.
     *
     * @param minAge the new min age
     */
    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    /**
     * Gets the max age.
     *
     * @return the max age
     */
    public Integer getMaxAge() {
        return maxAge;
    }

    /**
     * Sets the max age.
     *
     * @param maxAge the new max age
     */
    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    /**
     * Gets the price.
     *
     * @return the price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the price.
     *
     * @param price the new price
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Gets the recommended price.
     *
     * @return the recommended price
     */
    public BigDecimal getRecommendedPrice() {
        return recommendedPrice;
    }

    /**
     * Sets the recommended price.
     *
     * @param recommendedPrice the new recommended price
     */
    public void setRecommendedPrice(BigDecimal recommendedPrice) {
        this.recommendedPrice = recommendedPrice;
    }

}
