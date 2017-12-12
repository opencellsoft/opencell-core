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
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ServiceToUpdate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceToUpdateDto implements Serializable {

    private static final long serialVersionUID = -3815026205495621916L;

    /**
     * Service instance ID
     */
    @XmlAttribute()
    private Long id;

    /**
     * Service instance code. Note: not a unique identifier as service can be activated mnultiple times
     */
    @XmlAttribute()
    private String code;

    /**
     * Description
     */
    @XmlAttribute()
    private String description;

    /**
     * Quantity
     */
    @XmlElement(required = false)
    private BigDecimal quantity;

    /**
     * Service suspension or reactivation date - used in service suspension or reactivation API only
     */
    private Date actionDate;

    /**
     * End agreement date
     */
    private Date endAgreementDate;

    /**
     * Custom fields
     */
    private CustomFieldsDto customFields;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    @Override
    public String toString() {
        return "ServiceToSuspendDto [code=" + code + ", actionDate=" + actionDate + "]";
    }

    public Date getEndAgreementDate() {
        return endAgreementDate;
    }

    public void setEndAgreementDate(Date endAgreementDate) {
        this.endAgreementDate = endAgreementDate;
    }

    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}