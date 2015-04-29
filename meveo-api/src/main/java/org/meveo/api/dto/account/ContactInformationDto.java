package org.meveo.api.dto.account;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.meveo.model.shared.ContactInformation;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "ContactInformation")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContactInformationDto implements Serializable {

	private static final long serialVersionUID = -5401291437074205081L;

	protected String email;
	protected String phone;
	protected String mobile;
	protected String fax;

	public ContactInformationDto() {

	}

	public ContactInformationDto(ContactInformation e) {
		email = e.getEmail();
		phone = e.getPhone();
		mobile = e.getMobile();
		fax = e.getFax();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Override
	public String toString() {
		return "ContactInformationDto [email=" + email + ", phone=" + phone + ", mobile=" + mobile + ", fax=" + fax
				+ "]";
	}

}
