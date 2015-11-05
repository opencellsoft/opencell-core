package org.meveo.api.dto.account;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "Address")
@XmlAccessorType(XmlAccessType.FIELD)
public class AddressDto implements Serializable {

	private static final long serialVersionUID = 3064994876758578132L;

	protected String address1;
	protected String address2;
	protected String address3;
	protected String zipCode;
	protected String city;
	protected String country;
	protected String state;

	public AddressDto() {

	}

	public AddressDto(org.meveo.model.shared.Address e) {
		if (e != null) {
			address1 = e.getAddress1();
			address2 = e.getAddress2();
			address3 = e.getAddress3();
			zipCode = e.getZipCode();
			city = e.getCity();
			country = e.getCountry();
			state = e.getState();
		}
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "Address [address1=" + address1 + ", address2=" + address2 + ", address3=" + address3 + ", zipCode="
				+ zipCode + ", city=" + city + ", country=" + country + ", state=" + state + "]";
	}

}
