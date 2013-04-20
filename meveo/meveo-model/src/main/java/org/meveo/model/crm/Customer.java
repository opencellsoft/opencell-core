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
package org.meveo.model.crm;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.meveo.model.AccountEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.ContactInformation;

@Entity
@Table(name = "CRM_CUSTOMER")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CRM_CUSTOMER_SEQ")
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

    public CustomerAccount getDefaultCustomerAccount() {
        for (CustomerAccount customerAccount : getCustomerAccounts()) {
            if (customerAccount.getDefaultLevel()) {
                return customerAccount;
            }
        }
        return null;
    }
}
