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
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.AccountEntity;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.ContactInformation;

/**
 * Customer
 * 
 * @author Gediminas Ubartas
 * @created 2010.11.15
 * 
 */
@Entity
@Table(name = "CRM_CUSTOMER")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CRM_CUSTOMER_SEQ")
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

    
    @Column(name = "BUSINESS_INTERMEDIARY_ID")
    private Integer businessIntermediaryId;
    
    
    @Column(name = "COUNTRY_CODE", length = 2)
    private String countryCode;
    
    
    @Column(name = "CURRENCY_CODE", length = 3)
    private String currencyCode;
    
    
    @Column(name = "LANGUAGE_CODE", length = 3)
    private String languageCode;

    
    
    public Integer getBusinessIntermediaryId() {
		return businessIntermediaryId;
	}

	public void setBusinessIntermediaryId(Integer businessIntermediaryId) {
		this.businessIntermediaryId = businessIntermediaryId;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
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
