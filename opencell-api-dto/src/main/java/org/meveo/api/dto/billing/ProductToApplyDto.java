package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.catalog.ProductTemplate;

/**
 * The Class ProductToApplyDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "ProductToApply")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductToApplyDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3815026205495621916L;

    /** The code. */
    @XmlAttribute(required = true)
    private String code;

    /** The quantity. */
    @XmlElement(required = true)
    private BigDecimal quantity;

    /** The application date. */
    private Date applicationDate;

    /** The criteria 1. */
    @XmlElement(required = false)
    private String criteria1;

    /** The criteria 2. */
    @XmlElement(required = false)
    private String criteria2;

    /** The criteria 3. */
    @XmlElement(required = false)
    private String criteria3;

    /** The amount without tax. */
    @XmlElement(required = false)
    private BigDecimal amountWithoutTax;

    /** The amount with tax. */
    @XmlElement(required = false)
    private BigDecimal amountWithTax;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /** The product template. */
    @XmlTransient
    // @ApiModelProperty(hidden = true)
    private ProductTemplate productTemplate;

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
     * Gets the quantity.
     *
     * @return the quantity
     */
    public BigDecimal getQuantity() {
        if (quantity == null)
            return new BigDecimal(0);
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
     * Gets the application date.
     *
     * @return the application date
     */
    public Date getApplicationDate() {
        return applicationDate;
    }

    /**
     * Sets the application date.
     *
     * @param applicationDate the new application date
     */
    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
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
     * Gets the product template.
     *
     * @return the product template
     */
    public ProductTemplate getProductTemplate() {
        return productTemplate;
    }

    /**
     * Sets the product template.
     *
     * @param productTemplate the new product template
     */
    public void setProductTemplate(ProductTemplate productTemplate) {
        this.productTemplate = productTemplate;
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("ServiceToInstantiateDto [code=%s, quantity=%s, applicationDate=%s,amountWithoutTax =%s, amountWithTax=%s, customFields=%s]", code, quantity,
            applicationDate, amountWithoutTax, amountWithTax, customFields);
    }

}