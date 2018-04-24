package org.meveo.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class UsageChargeDto.
 *
 * @author Edward P. Legaspi
 * @since Nov 4, 2013
 */
@XmlRootElement(name = "UsageCharge")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsageChargeDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2546246507412182368L;

    /** The min. */
    private Integer min;
    
    /** The max. */
    private Integer max;
    
    /** The currency code. */
    private String currencyCode;
    
    /** The start date. */
    private Date startDate;
    
    /** The end date. */
    private Date endDate;
    
    /** The price. */
    private BigDecimal price;
    
    /** The recommended price. */
    private BigDecimal recommendedPrice;

    /**
     * Gets the min.
     *
     * @return the min
     */
    public Integer getMin() {
        return min;
    }

    /**
     * Sets the min.
     *
     * @param min the new min
     */
    public void setMin(Integer min) {
        this.min = min;
    }

    /**
     * Gets the max.
     *
     * @return the max
     */
    public Integer getMax() {
        return max;
    }

    /**
     * Sets the max.
     *
     * @param max the new max
     */
    public void setMax(Integer max) {
        this.max = max;
    }

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