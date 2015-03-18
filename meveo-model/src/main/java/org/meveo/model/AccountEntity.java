/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.ProviderContact;
import org.meveo.model.listeners.AccountCodeGenerationListener;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.Name;

@Entity
@Table(name = "ACCOUNT_ENTITY", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ACCOUNT_ENTITY_SEQ")
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners({ AccountCodeGenerationListener.class })
public abstract class AccountEntity extends BusinessEntity implements ICustomFieldEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "EXTERNAL_REF_1", length = 50)
	@Size(max = 50)
	private String externalRef1;

	@Column(name = "EXTERNAL_REF_2", length = 50)
	@Size(max = 50)
	private String externalRef2;

	@Embedded
	private Name name = new Name();

	@Embedded
	private Address address = new Address();

	@Column(name = "DEFAULT_LEVEL")
	private Boolean defaultLevel = true;

	@Column(name = "PROVIDER_CONTACT")
	private String providerContact;

	@ManyToOne
	@JoinColumn(name = "PRIMARY_CONTACT")
	private ProviderContact primaryContact;

	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@MapKeyColumn(name = "code")
	private Map<String, CustomFieldInstance> customFields = new HashMap<String, CustomFieldInstance>();

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
		if (name != null) {
			return name;
		}

		return new Name();
	}

	public void setName(Name name) {
		this.name = name;
	}

	public Address getAddress() {
		if (address != null) {
			return address;
		}

		return new Address();
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public abstract String getAccountType();

	public Boolean getDefaultLevel() {
		return defaultLevel;
	}

	public void setDefaultLevel(Boolean defaultLevel) {
		this.defaultLevel = defaultLevel;
	}

	public String getProviderContact() {
		return providerContact;
	}

	public void setProviderContact(String providerContact) {
		this.providerContact = providerContact;
	}

	public ProviderContact getPrimaryContact() {
		return primaryContact;
	}

	public void setPrimaryContact(ProviderContact primaryContact) {
		this.primaryContact = primaryContact;
	}

	public Map<String, CustomFieldInstance> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Map<String, CustomFieldInstance> customFields) {
		this.customFields = customFields;
	}

	private CustomFieldInstance getOrCreateCustomFieldInstance(String code) {
		CustomFieldInstance cfi = null;

		if (customFields.containsKey(code)) {
			cfi = customFields.get(code);
		} else {
			cfi = new CustomFieldInstance();
			Auditable au = new Auditable();
			au.setCreated(new Date());
			if (this.getAuditable() != null) {
				au.setCreator(this.getAuditable().getCreator());
			}
			cfi.setAuditable(au);
			cfi.setCode(code);
			cfi.setAccount(this);
			cfi.setProvider(this.getProvider());
			customFields.put(code, cfi);
		}

		return cfi;
	}

	public String getStringCustomValue(String code) {
		String result = null;
		if (customFields.containsKey(code)) {
			result = customFields.get(code).getStringValue();
		}

		return result;
	}

	public void setStringCustomValue(String code, String value) {
		getOrCreateCustomFieldInstance(code).setStringValue(value);
	}

	public Date getDateCustomValue(String code) {
		Date result = null;
		if (customFields.containsKey(code)) {
			result = customFields.get(code).getDateValue();
		}

		return result;
	}

	public void setDateCustomValue(String code, Date value) {
		getOrCreateCustomFieldInstance(code).setDateValue(value);
	}

	public Long getLongCustomValue(String code) {
		Long result = null;
		if (customFields.containsKey(code)) {
			result = customFields.get(code).getLongValue();
		}
		return result;
	}

	public void setLongCustomValue(String code, Long value) {
		getOrCreateCustomFieldInstance(code).setLongValue(value);
	}

	public Double getDoubleCustomValue(String code) {
		Double result = null;

		if (customFields.containsKey(code)) {
			result = customFields.get(code).getDoubleValue();
		}

		return result;
	}

	public void setDoubleCustomValue(String code, Double value) {
		getOrCreateCustomFieldInstance(code).setDoubleValue(value);
	}

	public String getCustomFieldsAsJson() {
		String result = "";
		String sep = "";

		for (Entry<String, CustomFieldInstance> cf : customFields.entrySet()) {
			result += sep + cf.getValue().toJson();
			sep = ";";
		}

		return result;
	}

	public String getInheritedCustomStringValue(String code) {
		String result = null;
		if (getCustomFields().containsKey(code) && getCustomFields().get(code).getStringValue() != null) {
			result = getCustomFields().get(code).getStringValue();
		} else {
			if (this instanceof CustomerAccount) {
				CustomerAccount customerAccount = (CustomerAccount) this;
				if (customerAccount.getCustomer() != null) {
					result = ((CustomerAccount) this).getCustomer().getInheritedCustomStringValue(code);
				}
			} else if (this instanceof BillingAccount) {
				BillingAccount billingAccount = (BillingAccount) this;
				if (billingAccount.getCustomerAccount() != null) {
					result = ((BillingAccount) this).getCustomerAccount().getInheritedCustomStringValue(code);
				}
			} else if (this instanceof UserAccount) {
				UserAccount userAccount = (UserAccount) this;
				if (userAccount.getBillingAccount() != null) {
					result = ((UserAccount) this).getBillingAccount().getInheritedCustomStringValue(code);
				}
			}
		}
		return result;
	}

	public Long getInheritedCustomLongValue(String code) {
		Long result = null;
		if (getCustomFields().containsKey(code) && getCustomFields().get(code).getLongValue() != null) {
			result = getCustomFields().get(code).getLongValue();
		} else {
			if (this instanceof CustomerAccount) {
				CustomerAccount customerAccount = (CustomerAccount) this;
				if (customerAccount.getCustomer() != null) {
					result = ((CustomerAccount) this).getCustomer().getInheritedCustomLongValue(code);
				}
			} else if (this instanceof BillingAccount) {
				BillingAccount billigAccount = (BillingAccount) this;
				if (billigAccount.getCustomerAccount() != null) {
					result = ((BillingAccount) this).getCustomerAccount().getInheritedCustomLongValue(code);
				}
			} else if (this instanceof UserAccount) {
				UserAccount userAccount = (UserAccount) this;
				if (userAccount.getBillingAccount() != null) {
					result = ((UserAccount) this).getBillingAccount().getInheritedCustomLongValue(code);
				}
			}
		}
		return result;
	}

	public Date getInheritedCustomDateValue(String code) {
		Date result = null;
		if (getCustomFields().containsKey(code) && getCustomFields().get(code).getDateValue() != null) {
			result = getCustomFields().get(code).getDateValue();
		} else {
			if (this instanceof CustomerAccount) {
				CustomerAccount customerAccount = (CustomerAccount) this;
				if (customerAccount.getCustomer() != null) {
					result = ((CustomerAccount) this).getCustomer().getInheritedCustomDateValue(code);
				}
			} else if (this instanceof BillingAccount) {
				BillingAccount billigAccount = (BillingAccount) this;
				if (billigAccount.getCustomerAccount() != null) {
					result = ((BillingAccount) this).getCustomerAccount().getInheritedCustomDateValue(code);
				}
			} else if (this instanceof UserAccount) {
				UserAccount userAccount = (UserAccount) this;
				if (userAccount.getBillingAccount() != null) {
					result = ((UserAccount) this).getBillingAccount().getInheritedCustomDateValue(code);
				}
			}
		}
		return result;
	}

	public Double getInheritedCustomDoubleValue(String code) {
		Double result = null;
		if (getCustomFields().containsKey(code) && getCustomFields().get(code).getDoubleValue() != null) {
			result = getCustomFields().get(code).getDoubleValue();
			if (this instanceof CustomerAccount) {
				CustomerAccount customerAccount = (CustomerAccount) this;
				if (customerAccount.getCustomer() != null) {
					result = ((CustomerAccount) this).getCustomer().getInheritedCustomDoubleValue(code);
				}
			} else if (this instanceof BillingAccount) {
				BillingAccount billigAccount = (BillingAccount) this;
				if (billigAccount.getCustomerAccount() != null) {
					result = ((BillingAccount) this).getCustomerAccount().getInheritedCustomDoubleValue(code);
				}
			} else if (this instanceof UserAccount) {
				UserAccount userAccount = (UserAccount) this;
				if (userAccount.getBillingAccount() != null) {
					result = ((UserAccount) this).getBillingAccount().getInheritedCustomDoubleValue(code);
				}
			}
		}
		return result;
	}

	public String getICsv(String code) {
		return getInheritedCustomStringValue(code);
	}

	public Long getIClv(String code) {
		return getInheritedCustomLongValue(code);
	}

	public Date getICdav(String code) {
		return getInheritedCustomDateValue(code);
	}

	public Double getICdov(String code) {
		return getInheritedCustomDoubleValue(code);
	}

}
