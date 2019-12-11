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

import org.meveo.model.AccountEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IWFEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.intcrm.AdditionalDetails;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.payments.CustomerAccount;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * Customer
 *
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @author Khalid HORRI
 * @lastModifiedVersion 10.0
 */
@Entity
@WorkflowedEntity
@CustomFieldEntity(cftCodePrefix = "Customer", inheritCFValuesFrom = "seller")
@ExportIdentifier({ "code" })
@DiscriminatorValue(value = "ACCT_CUST")
@Table(name = "crm_customer")
@NamedQueries({
        @NamedQuery(name = "Customer.getMimimumRTUsed", query = "select c.minimumAmountEl from Customer c where c.minimumAmountEl is not null"),
        @NamedQuery(name = "Customer.getCustomersWithMinAmountELNotNullByBA", query = "select c from Customer c where c.minimumAmountEl is not null  AND c=:customer")})

public class Customer extends AccountEntity implements IWFEntity {

    public static final String ACCOUNT_TYPE = ((DiscriminatorValue) Customer.class.getAnnotation(DiscriminatorValue.class)).value();

    private static final long serialVersionUID = 1L;

    /**
     * Address book
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_book_id")
    private AddressBook addressbook;

    /**
     * Customer category
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_category_id")
    private CustomerCategory customerCategory;

    /**
     * Customer brand
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_brand_id")
    private CustomerBrand customerBrand;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<CustomerAccount> customerAccounts = new ArrayList<>();

    /**
     * Seller. used as a default Seller for minimum RTs
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    /**
     * Additional details
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "additional_details_id")
    private AdditionalDetails additionalDetails;

    /**
     * Expression to determine minimum amount value
     */
    @Column(name = "minimum_amount_el", length = 2000)
    @Size(max = 2000)
    private String minimumAmountEl;

    /**
     * The billable Entity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "minimum_target_account_id")
    private BillingAccount minimumTargetAccount;

    /**
     * Expression to determine rated transaction description to reach minimum amount value
     */
    @Column(name = "minimum_label_el", length = 2000)
    @Size(max = 2000)
    private String minimumLabelEl;


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
        if (seller != null) {
            return new ICustomFieldEntity[] { seller };
        }
        return null;
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
        if (isNotEmpty(this.customerAccounts)) {
            this.customerAccounts.forEach(ca -> ca.anonymize(code));
        }
    }

    public String getMinimumAmountEl() {
        return minimumAmountEl;
    }

    public void setMinimumAmountEl(String minimumAmountEl) {
        this.minimumAmountEl = minimumAmountEl;
    }

    public BillingAccount getMinimumTargetAccount() {
        return minimumTargetAccount;
    }

    public void setMinimumTargetAccount(BillingAccount minimumTargetAccount) {
        this.minimumTargetAccount = minimumTargetAccount;
    }

    public String getMinimumLabelEl() {
        return minimumLabelEl;
    }

    public void setMinimumLabelEl(String minimumLabelEl) {
        this.minimumLabelEl = minimumLabelEl;
    }
}