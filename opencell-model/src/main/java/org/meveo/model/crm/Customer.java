/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.model.crm;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.meveo.model.AccountEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICounterEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IWFEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.ThresholdOptionsEnum;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.intcrm.AdditionalDetails;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.payments.CustomerAccount;

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
        @NamedQuery(name = "Customer.getMinimumAmountUsed", query = "select c.minimumAmountEl from Customer c where c.minimumAmountEl is not null"),
        @NamedQuery(name = "Customer.getByAddressBook", query = "select c from Customer c where c.addressbook.id =:addressBookId"),
        @NamedQuery(name = "Customer.getCustomersWithMinAmountELNotNullByBA", query = "select c from Customer c where c.minimumAmountEl is not null  AND c=:customer"),
        @NamedQuery(name = "Customer.getProspects", query = "select c from Customer c left join c.customerAccounts as ca left join ca.billingAccounts as ba "
                + "left join ba.invoices as inv left join ba.usersAccounts as ua left join ua.subscriptions as sub "
                + "where sub.id is null and inv.id is null and c.auditable.created < :creationDate")})
public class Customer extends AccountEntity implements IInvoicingMinimumApplicable, IWFEntity, ICounterEntity {

    private static final long serialVersionUID = 1L;

    /**
     * anonymization date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "anonymization_date")
    private Date anonymizationDate;

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

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
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
     * Accumulator Counters instantiated on the customer with Counter template code as a key.
     */
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    @MapKey(name = "code")
    private Map<String, CounterInstance> counters = new HashMap<>();

    /**
     * The billable Entity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "minimum_target_account_id")
    private BillingAccount minimumTargetAccount;


    /**
     * Invoicing threshold - do not invoice for a lesser amount.
     */
    @Column(name = "invoicing_threshold")
    private BigDecimal invoicingThreshold;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Contract> contracts = new ArrayList<>();
    public List<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(List<Contract> contracts) {
        this.contracts = contracts;
    }

    /**
     * The option on how to check the threshold.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "check_threshold")
    private ThresholdOptionsEnum checkThreshold;

    /**
     * check threshold per entity?
     */
    @Type(type = "numeric_boolean")
    @Column(name = "threshold_per_entity")
    private boolean thresholdPerEntity;

    /**
     * Parent customer 
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_customer_id")
    private Customer parentCustomer;
    
    /**
     * Children customers 
     */
    @OneToMany(mappedBy = "parentCustomer", fetch = FetchType.LAZY)
    private List<Customer> childrenCustomers = new ArrayList<>();
    
    public Date getAnonymizationDate() {
        return anonymizationDate;
    }

    public void setAnonymizationDate(Date anonymizationDate) {
        this.anonymizationDate = anonymizationDate;
    }

    public boolean isAnonymized(){
        return anonymizationDate != null;
    }

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

    public boolean isThresholdPerEntity() {
    	return thresholdPerEntity;
	}

	public void setThresholdPerEntity(boolean thresholdPerEntity) {
		this.thresholdPerEntity = thresholdPerEntity;
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
        setAnonymizationDate(new Date());
    }

    /**
     * Gets a counters map.
     *
     * @return a counters map
     */
    @Override
    public Map<String, CounterInstance> getCounters() {
        return counters;
    }

    /**
     * Sets counters.
     *
     * @param counters a counters map
     */
    public void setCounters(Map<String, CounterInstance> counters) {
        this.counters = counters;
    }
    
    public BillingAccount getMinimumTargetAccount() {
        return minimumTargetAccount;
    }

    public void setMinimumTargetAccount(BillingAccount minimumTargetAccount) {
        this.minimumTargetAccount = minimumTargetAccount;
    }

    /**
     * @return the invoicingThreshold
     */
    public BigDecimal getInvoicingThreshold() {
        return invoicingThreshold;
    }

    /**
     * @param invoicingThreshold the invoicingThreshold to set
     */
    public void setInvoicingThreshold(BigDecimal invoicingThreshold) {
        this.invoicingThreshold = invoicingThreshold;
    }

    /**
     * Gets the threshold option.
     *
     * @return the threshold option
     */
    public ThresholdOptionsEnum getCheckThreshold() {
        return checkThreshold;
    }

    /**
     * Sets the threshold option.
     *
     * @param checkThreshold the threshold option
     */
    public void setCheckThreshold(ThresholdOptionsEnum checkThreshold) {
        this.checkThreshold = checkThreshold;
    }

	public Customer getParentCustomer() {
		return parentCustomer;
	}

	public void setParentCustomer(Customer parentCustomer) {
		this.parentCustomer = parentCustomer;
	}

	public List<Customer> getChildrenCustomers() {
		return childrenCustomers;
	}

	public void setChildrenCustomers(List<Customer> childrenCustomers) {
		this.childrenCustomers = childrenCustomers;
	}
    
}