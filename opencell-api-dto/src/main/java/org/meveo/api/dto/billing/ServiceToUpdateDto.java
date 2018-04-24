package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;

/**
 * The Class ServiceToUpdateDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "ServiceToUpdate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceToUpdateDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3815026205495621916L;

    /** Service instance ID. */
    @XmlAttribute()
    private Long id;

    /**
     * Service instance code. Note: not a unique identifier as service can be activated mnultiple times
     */
    @XmlAttribute()
    private String code;

    /** Description. */
    @XmlAttribute()
    private String description;

    /** Quantity. */
    @XmlElement(required = false)
    private BigDecimal quantity;

    /** Service suspension or reactivation date - used in service suspension or reactivation API only. */
    private Date actionDate;

    /** End agreement date. */
    private Date endAgreementDate;

    /** Custom fields. */
    private CustomFieldsDto customFields;

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(Long id) {
        this.id = id;
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
     * Gets the action date.
     *
     * @return the action date
     */
    public Date getActionDate() {
        return actionDate;
    }

    /**
     * Sets the action date.
     *
     * @param actionDate the new action date
     */
    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    /**
     * Gets the end agreement date.
     *
     * @return the end agreement date
     */
    public Date getEndAgreementDate() {
        return endAgreementDate;
    }

    /**
     * Sets the end agreement date.
     *
     * @param endAgreementDate the new end agreement date
     */
    public void setEndAgreementDate(Date endAgreementDate) {
        this.endAgreementDate = endAgreementDate;
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
    
    @Override
    public String toString() {
        return "ServiceToSuspendDto [code=" + code + ", actionDate=" + actionDate + "]";
    }
}