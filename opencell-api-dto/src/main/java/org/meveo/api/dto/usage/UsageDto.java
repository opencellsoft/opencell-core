package org.meveo.api.dto.usage;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

@XmlRootElement(name = "Usage")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsageDto extends BaseDto {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Date dateEvent;
    private String code;
    private String description;
    private String unityDescription;
    private BigDecimal unitAmountWithoutTax;
    private BigDecimal quantity;
    private BigDecimal amountWithoutTax;
    private String parameter1;
    private String parameter2;
    private String parameter3;
    private String parameterExtra;
    private String offerCode;
    private String priceplanCode;

    public UsageDto() {

    }

    /**
     * @return the dateEvent
     */
    public Date getDateEvent() {
        return dateEvent;
    }

    /**
     * @param dateEvent the dateEvent to set
     */
    public void setDateEvent(Date dateEvent) {
        this.dateEvent = dateEvent;
    }

    /**
     * @return the unityDescription
     */
    public String getUnityDescription() {
        return unityDescription;
    }

    /**
     * @param unityDescription the unityDescription to set
     */
    public void setUnityDescription(String unityDescription) {
        this.unityDescription = unityDescription;
    }

    /**
     * @return the unitAmountWithoutTax
     */
    public BigDecimal getUnitAmountWithoutTax() {
        return unitAmountWithoutTax;
    }

    /**
     * @param unitAmountWithoutTax the unitAmountWithoutTax to set
     */
    public void setUnitAmountWithoutTax(BigDecimal unitAmountWithoutTax) {
        this.unitAmountWithoutTax = unitAmountWithoutTax;
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
     * @return the amountWithoutTax
     */
    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    /**
     * @param amountWithoutTax the amountWithoutTax to set
     */
    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    /**
     * @return the parameter1
     */
    public String getParameter1() {
        return parameter1;
    }

    /**
     * @param parameter1 the parameter1 to set
     */
    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    /**
     * @return the parameter2
     */
    public String getParameter2() {
        return parameter2;
    }

    /**
     * @param parameter2 the parameter2 to set
     */
    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    /**
     * @return the parameter3
     */
    public String getParameter3() {
        return parameter3;
    }

    /**
     * @param parameter3 the parameter3 to set
     */
    public void setParameter3(String parameter3) {
        this.parameter3 = parameter3;
    }

    public String getParameterExtra() {
        return parameterExtra;
    }

    public void setParameterExtra(String parameterExtra) {
        this.parameterExtra = parameterExtra;
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
     * @return the priceplanCode
     */
    public String getPriceplanCode() {
        return priceplanCode;
    }

    /**
     * @param priceplanCode the priceplanCode to set
     */
    public void setPriceplanCode(String priceplanCode) {
        this.priceplanCode = priceplanCode;
    }

    @Override
    public String toString() {
        return "UsageDto [dateEvent=" + dateEvent + ", code=" + code + ", description=" + description + ", unityDescription=" + unityDescription + ", unitAmountWithoutTax="
                + unitAmountWithoutTax + ", quantity=" + quantity + ", amountWithoutTax=" + amountWithoutTax + ", parameter1=" + parameter1 + ", parameter2=" + parameter2
                + ", parameter3=" + parameter3 + ", offerCode=" + offerCode + ", priceplanCode=" + priceplanCode + "]";
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
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

}
