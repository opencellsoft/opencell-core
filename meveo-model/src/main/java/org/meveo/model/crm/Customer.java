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
package org.meveo.model.crm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.AccountEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.ContactInformation;

@Entity
@Table(name = "CRM_CUSTOMER")
public class Customer extends AccountEntity {

	public static final String ACCOUNT_TYPE = "customer.type";

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "CUSTOMER_CATEGORY_ID")
	private CustomerCategory customerCategory;

	@ManyToOne
	@JoinColumn(name = "CUSTOMER_BRAND_ID")
	private CustomerBrand customerBrand;

	@OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<CustomerAccount> customerAccounts = new ArrayList<CustomerAccount>();

	@Embedded
	private ContactInformation contactInformation = new ContactInformation();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SELLER_ID")
	private Seller seller;

	@Column(name = "MANDATE_IDENTIFICATION", length = 35)
	private String mandateIdentification = "";

	@Column(name = "MANDATE_DATE")
	@Temporal(TemporalType.DATE)
	private Date mandateDate;

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public CustomerCategory getCustomerCategory() {
		return customerCategory;
	}

	public void setCustomerCategory(CustomerCategory customerCategory) {
		this.customerCategory = customerCategory;
	}

	public CustomerBrand getCustomerBrand() {
		return customerBrand;
	}

	public void setCustomerBrand(CustomerBrand customerBrand) {
		this.customerBrand = customerBrand;
	}

	public List<CustomerAccount> getCustomerAccounts() {
		return customerAccounts;
	}

	public void setCustomerAccounts(List<CustomerAccount> customerAccounts) {
		this.customerAccounts = customerAccounts;
	}

	public ContactInformation getContactInformation() {
		if (contactInformation == null) {
			contactInformation = new ContactInformation();
		}
		return contactInformation;
	}

	public void setContactInformation(ContactInformation contactInformation) {
		this.contactInformation = contactInformation;
	}

	@Override
	public String getAccountType() {
		return ACCOUNT_TYPE;
	}

	public String getMandateIdentification() {
		return mandateIdentification;
	}

	public void setMandateIdentification(String mandateIdentification) {
		this.mandateIdentification = mandateIdentification;
	}

	public Date getMandateDate() {
		return mandateDate;
	}

	public void setMandateDate(Date mandateDate) {
		this.mandateDate = mandateDate;
	}

	public CustomerAccount getDefaultCustomerAccount() {
		for (CustomerAccount customerAccount : getCustomerAccounts()) {
			if (customerAccount.getDefaultLevel()) {
				return customerAccount;
			}
		}
		return null;
	}
}
