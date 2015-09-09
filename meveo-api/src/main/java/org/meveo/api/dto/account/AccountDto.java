package org.meveo.api.dto.account;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.AccountEntity;
import org.meveo.model.crm.CustomFieldInstance;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "Account")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AccountDto implements Serializable {

	private static final long serialVersionUID = -8818317499795113026L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute(required = true)
	private String description;

	private String externalRef1;
	private String externalRef2;
	private NameDto name = new NameDto();
	private AddressDto address = new AddressDto();
	private CustomFieldsDto customFields = new CustomFieldsDto();
	protected boolean loaded = false;

	public AccountDto() {
		super();
	}

	public AccountDto(AccountEntity e) {
		initFromEntity(e);
	}

	public void initFromEntity(AccountEntity e) {
		setCode(e.getCode());
		setDescription(e.getDescription());
		setExternalRef1(e.getExternalRef1());
		setExternalRef2(e.getExternalRef2());
		setName(new NameDto(e.getName()));
		setAddress(new AddressDto(e.getAddress()));

		if (e.getCustomFields() != null) {
		    for (CustomFieldInstance cfi : e.getCustomFields().values()) {
                customFields.getCustomField().addAll(CustomFieldDto.toDTO(cfi));
            }
		}
		
		if (e.getCustomFields() != null) {
			for (CustomFieldInstance cfi : e.getCustomFields().values()) {
				customFields.getCustomField().addAll(CustomFieldDto.toDTO(cfi));
			}
		}

		loaded = true;
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

	public NameDto getName() {
		return name;
	}

	public void setName(NameDto name) {
		this.name = name;
	}

	public AddressDto getAddress() {
		return address;
	}

	public void setAddress(AddressDto address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "BaseAccountDto [code=" + code + ", description=" + description + ", externalRef1=" + externalRef1
				+ ", externalRef2=" + externalRef2 + ", name=" + name + ", address=" + address + ", customFields="
				+ customFields + "]";
	}

	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccountDto other = (AccountDto) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

}
