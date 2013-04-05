/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.model.admin;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.ProviderBusinessEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.Language;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.Address;

/**
 * @author MBAREK
 * 
 */
@Entity
@Table(name = "CRM_SELLER")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CRM_SELLER_SEQ")
public class Seller extends ProviderBusinessEntity {

    private static final long serialVersionUID = 1L;

     @ManyToOne(fetch = FetchType.LAZY)
	 @JoinColumn(name = "COUNTRY_ID")
	  private Country country; 
    
     @ManyToOne(fetch = FetchType.LAZY)
	 @JoinColumn(name = "CURRENCY_ID")
	  private Currency currency;
     
     @ManyToOne(fetch = FetchType.LAZY)
	 @JoinColumn(name = "LANGUAGE_ID")
	  private Language language;
     
      @Embedded
 	   private Address address = new Address();
      
      @ManyToOne(fetch = FetchType.LAZY)
      @JoinColumn(name = "CUSTOMER_ID")
      private Customer customer;
      
      @ManyToOne(fetch = FetchType.LAZY)
      @JoinColumn(name = "CUSTOMER_ACCOUNT_ID")
      private CustomerAccount customerAccount;
      
      @ManyToOne(fetch = FetchType.LAZY)
      @JoinColumn(name = "BILLING_ACCOUNT_ID")
      private BillingAccount billingAccount;
      
      @ManyToOne(fetch = FetchType.LAZY)
      @JoinColumn(name = "USER_ACCOUNT_ID")
      private UserAccount userAccount;
      
      @ManyToOne(fetch = FetchType.LAZY)
      @JoinColumn(name = "PARENT_SELLER_ID")
      private Seller seller;

	public Seller() {
		super(); 
	}

	public Seller(Country country, Currency currency, Language language,
			Address address, Customer customer,
			CustomerAccount customerAccount, BillingAccount billingAccount,
			UserAccount userAccount, Seller seller) {
		super();
		this.country = country;
		this.currency = currency;
		this.language = language;
		this.address = address;
		this.customer = customer;
		this.customerAccount = customerAccount;
		this.billingAccount = billingAccount;
		this.userAccount = userAccount;
		this.seller = seller;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public CustomerAccount getCustomerAccount() {
		return customerAccount;
	}

	public void setCustomerAccount(CustomerAccount customerAccount) {
		this.customerAccount = customerAccount;
	}

	public BillingAccount getBillingAccount() {
		return billingAccount;
	}

	public void setBillingAccount(BillingAccount billingAccount) {
		this.billingAccount = billingAccount;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}
      
  
      
      
      
    }
 
