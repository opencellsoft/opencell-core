package org.meveo.api.dto.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.billing.ProductChargeInstance;

/**
 * The Class ProductDto.
 * 
 * @author anasseh
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4084004747483067153L;

    /** The code. */
    @XmlAttribute(required = true)
    private String code;

    /** The description. */
    @XmlAttribute()
    private String description;

    /** The charge date. */
    private Date chargeDate;

    /** The quantity. */
    private BigDecimal quantity;

    /** The amount without tax. */
    private BigDecimal amountWithoutTax;

    /** The amount with tax. */
    private BigDecimal amountWithTax;

    /** The criteria 1. */
    private String criteria1;

    /** The criteria 2. */
    private String criteria2;

    /** The criteria 3. */
    private String criteria3;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /**
     * Instantiates a new product dto.
     */
    public ProductDto() {

    }

    /**
     * Instantiates a new product dto.
     *
     * @param productChargeInstance the ProductChargeInstance entity
     * @param customFieldInstances the custom field instances
     */
    public ProductDto(ProductChargeInstance productChargeInstance, CustomFieldsDto customFieldInstances) {
        code = productChargeInstance.getCode();
        description = productChargeInstance.getDescription();
        chargeDate = productChargeInstance.getChargeDate();
        quantity = productChargeInstance.getQuantity();
        customFields = customFieldInstances;
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
     * Gets the charge date.
     *
     * @return the charge date
     */
    public Date getChargeDate() {
        return chargeDate;
    }

    /**
     * Sets the charge date.
     *
     * @param chargeDate the new charge date
     */
    public void setChargeDate(Date chargeDate) {
        this.chargeDate = chargeDate;
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
    
    @Override
    public String toString() {
        return "ServiceInstanceDto [code=" + code + ", description=" + description + ", chargeDate=" + chargeDate + ", quantity=" + quantity + "]";
    }    
}