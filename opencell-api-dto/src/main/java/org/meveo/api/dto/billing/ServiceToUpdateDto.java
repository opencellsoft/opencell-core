package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ServiceToUpdate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceToUpdateDto implements Serializable {

    private static final long serialVersionUID = -3815026205495621916L;

    @XmlAttribute(required = true)
    private String code;
    private Date actionDate;    
    private Date endAgreementDate;
    private CustomFieldsDto customFields;

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
    
    }