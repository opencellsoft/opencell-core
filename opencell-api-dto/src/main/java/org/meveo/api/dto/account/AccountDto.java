package org.meveo.api.dto.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.api.dto.BusinessDto;
import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.AccountEntity;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "Account")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AccountDto extends BusinessDto {

	private static final long serialVersionUID = -8818317499795113026L;	
	
	private String externalRef1;
	private String externalRef2;
	private NameDto name;
	private AddressDto address;

	@XmlElement(name = "businessAccountModel")
	private BusinessEntityDto businessAccountModel;
	private CustomFieldsDto customFields;
	
	@XmlTransient
	protected boolean loaded = false;
	
	public AccountDto() {
		super();
	}

	public AccountDto(AccountEntity e, CustomFieldsDto customFieldInstances) {
		initFromEntity(e, customFieldInstances);
	}

	public void initFromEntity(AccountEntity account, CustomFieldsDto customFieldInstances) {
		setCode(account.getCode());
		setDescription(account.getDescription());
		setExternalRef1(account.getExternalRef1());
		setExternalRef2(account.getExternalRef2());
        if (account.getName() != null) {
            setName(new NameDto(account.getName()));
        }
        if (account.getAddress() != null) {
            setAddress(new AddressDto(account.getAddress()));
        }

        customFields = customFieldInstances;

		loaded = true;
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

	public BusinessEntityDto getBusinessAccountModel() {
		return businessAccountModel;
	}

	public void setBusinessAccountModel(BusinessEntityDto businessAccountModel) {
		this.businessAccountModel = businessAccountModel;
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
		result = prime * result + ((getCode() == null) ? 0 : getCode().hashCode());
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
		if (getCode() == null) {
			if (other.getCode() != null)
				return false;
		} else if (!getCode().equals(other.getCode()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AccountDto [code=" + getCode() + ", description=" + getDescription()
				+ ", externalRef1=" + externalRef1 + ", externalRef2="
				+ externalRef2 + ", name=" + name + ", address=" + address
				+ ", customFields=" + customFields + ", loaded=" + loaded
				+ ", businessAccountModel=" + businessAccountModel +  "]";
	}

}
