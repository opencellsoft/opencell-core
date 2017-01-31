package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ServiceToSuspend")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceToSuspendDto implements Serializable {

    private static final long serialVersionUID = -3815026205495621916L;

    @XmlAttribute(required = true)
    private String code;

    private Date suspensionDate;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getSuspensionDate() {
		return suspensionDate;
	}

	public void setSuspensionDate(Date suspensionDate) {
		this.suspensionDate = suspensionDate;
	}

	@Override
	public String toString() {
		return "ServiceToSuspendDto [code=" + code + ", suspensionDate=" + suspensionDate + "]";
	}
    
    }