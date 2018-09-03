/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.crm;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.meveo.model.AccountEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.intcrm.AdditionalDetails;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.payments.CustomerAccount;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Entity
@CustomFieldEntity(cftCodePrefix = "CUST")
@ExportIdentifier({ "code" })
@DiscriminatorValue(value = "ACCT_CUST")
@Table(name = "crm_customer")
public class Customer extends AccountEntity {

    public static final String ACCOUNT_TYPE = ((DiscriminatorValue) Customer.class.getAnnotation(DiscriminatorValue.class)).value();

    private static final long serialVersionUID = 1L;


    @OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_book_id")
    private AddressBook addressbook;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "customer_category_id")
    private CustomerCategory customerCategory;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "customer_brand_id")
    private CustomerBrand customerBrand;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<CustomerAccount> customerAccounts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;
    
    @OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "additional_details_id")
    private AdditionalDetails additionalDetails;
    
    
    
    public AddressBook getAddressbook() {
		return addressbook;
	}

	public void setAddressbook(AddressBook addressbook) {
		this.addressbook = addressbook;
	}

	public AdditionalDetails getAdditionalDetails() {
		return additionalDetails;
	}

	public void setAdditionalDetails(AdditionalDetails additionalDetails) {
		this.additionalDetails = additionalDetails;
	}

    public Customer() {
        accountType = ACCOUNT_TYPE;
    }

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

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        return new ICustomFieldEntity[] { seller };
    }

    @Override
    public BusinessEntity getParentEntity() {
        return seller;
    }

    @Override
    public Class<? extends BusinessEntity> getParentEntityType() {
        return Seller.class;
    }
	
	@Override
	public void anonymize(String code) {
		super.anonymize(code);
		getContactInformation().anonymize(code);
		if (getCustomerAccounts() != null) {
			for (CustomerAccount ca : getCustomerAccounts()) {
				ca.anonymize(code);
			}
		}
	}

}
