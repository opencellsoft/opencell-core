package org.meveo.api.dto.account;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.AccountEntity;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.Name;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "BaseAccount")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class BaseAccountDto implements Serializable {

	private static final long serialVersionUID = -8818317499795113026L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute(required = true)
	private String description;

	private String externalRef1;
	private String externalRef2;
	private Name name = new Name();
	private Address address = new Address();

	public BaseAccountDto() {
		super();
	}

	public BaseAccountDto(AccountEntity e) {
		setCode(e.getCode());
		setDescription(e.getDescription());
		setExternalRef1(e.getExternalRef1());
		setExternalRef2(e.getExternalRef2());
		setName(e.getName());
		setAddress(e.getAddress());
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExternalRef1() {
		return externalRef1;
	}

	public void setExternalRef1(String externalRef1) {
		this.externalRef1 = externalRef1;
	}

	public String getExternalRef2() {
		return externalRef2;
	}

	public void setExternalRef2(String externalRef2) {
		this.externalRef2 = externalRef2;
	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

}
