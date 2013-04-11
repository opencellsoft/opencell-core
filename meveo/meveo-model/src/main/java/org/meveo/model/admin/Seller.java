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

import org.meveo.model.BusinessEntity;
import org.meveo.model.billing.BillingAccount; 
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
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
public class Seller extends BusinessEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRADING_CURRENCY_ID")
    private TradingCurrency tradingCurrency ;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRADING_COUNTRY_ID")
    private TradingCountry tradingCountry; 
    

    @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "TRADING_LANGUAGE_ID")
   private TradingLanguage tradingLanguage;
     
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

 

	

	public TradingCurrency getTradingCurrency() {
		return tradingCurrency;
	}





	public void setTradingCurrency(TradingCurrency tradingCurrency) {
		this.tradingCurrency = tradingCurrency;
	}

 
	public TradingCountry getTradingCountry() {
		return tradingCountry;
	}

 
	public void setTradingCountry(TradingCountry tradingCountry) {
		this.tradingCountry = tradingCountry;
	}

 
	public TradingLanguage getTradingLanguage() {
		return tradingLanguage;
	}

 
	public void setTradingLanguage(TradingLanguage tradingLanguage) {
		this.tradingLanguage = tradingLanguage;
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
 
