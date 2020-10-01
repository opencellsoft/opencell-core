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

package org.meveo.api.dto.account;

import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.billing.SubscriptionsDto;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.UserAccount;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * The Class UserAccountDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAccountDto extends AccountDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -13552444627686818L;

    /** The billing account. */
    @XmlElement(required = true)
    private String billingAccount;
    
    /** The billing account description. */
    private String billingAccountDescription;
    
    /** The customer account. */
    private String customerAccount;
    
    /** The customer account description. */
    private String customerAccountDescription;
    
    /** The customer. */
    private String customer;
    
    /** The customer description. */
    private String customerDescription;

    /** The subscription date. */
    private Date subscriptionDate;
    
    /** The termination date. */
    private Date terminationDate;
    
    /** The status. */
    private AccountStatusEnum status;
    
    /** The status date. */
    private Date statusDate;
    
    /** The termination reason. */
    private String terminationReason;

    /**
     * Use for GET / LIST only.
     */
    private SubscriptionsDto subscriptions = new SubscriptionsDto();

    /**
     * Instantiates a new user account dto.
     */
    public UserAccountDto() {
        super();
    }
    
    /**
     * Instantiates a new user account dto.
     * 
     * @param e UserAccount entity
     */
	public UserAccountDto(UserAccount e) {
		super(e);
        id = e.getId();
		if (e.getBillingAccount() != null) {
			setBillingAccount(e.getBillingAccount().getCode());
			setBillingAccountDescription(e.getBillingAccount().getDescription());

			if (e.getBillingAccount().getCustomerAccount() != null) {
				setCustomerAccount(e.getBillingAccount().getCustomerAccount().getCode());
				setCustomerAccountDescription(e.getBillingAccount().getCustomerAccount().getDescription());

				if (e.getBillingAccount().getCustomerAccount().getCustomer() != null) {
					setCustomer(e.getBillingAccount().getCustomerAccount().getCustomer().getCode());
					setCustomerDescription(e.getBillingAccount().getCustomerAccount().getCustomer().getDescription());
				}
			}
		}

        setSubscriptionDate(e.getSubscriptionDate());
        setTerminationDate(e.getTerminationDate());
        setStatus(e.getStatus());
        setStatusDate(e.getStatusDate());
        if (e.getMinimumAmountEl() != null) {
            setMinimumAmountEl(e.getMinimumAmountEl());
        }
        if (e.getMinimumLabelEl() != null) {
            setMinimumLabelEl(e.getMinimumLabelEl());
        }
        setRegistrationNo(e.getRegistrationNo());
        setVatNo(e.getVatNo());
        if(e.getSubscriptions() != null && !e.getSubscriptions().isEmpty()) {
        	SubscriptionsDto subDtos = new SubscriptionsDto();
        	subDtos.setSubscription(e.getSubscriptions().stream().map(s -> new SubscriptionDto(s)).collect(Collectors.toList()));
        	setSubscriptions(subDtos);
        }
    }
	

    /**
     * Gets the billing account.
     *
     * @return the billing account
     */
    public String getBillingAccount() {
        return billingAccount;
    }

    /**
     * Sets the billing account.
     *
     * @param billingAccount the new billing account
     */
    public void setBillingAccount(String billingAccount) {
        this.billingAccount = billingAccount;
    }

    /**
     * Gets the subscription date.
     *
     * @return the subscription date
     */
    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    /**
     * Sets the subscription date.
     *
     * @param subscriptionDate the new subscription date
     */
    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    /**
     * Gets the termination date.
     *
     * @return the termination date
     */
    public Date getTerminationDate() {
        return terminationDate;
    }

    /**
     * Sets the termination date.
     *
     * @param terminationDate the new termination date
     */
    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public AccountStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(AccountStatusEnum status) {
        this.status = status;
    }

    /**
     * Gets the status date.
     *
     * @return the status date
     */
    public Date getStatusDate() {
        return statusDate;
    }

    /**
     * Sets the status date.
     *
     * @param statusDate the new status date
     */
    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    /**
     * Gets the termination reason.
     *
     * @return the termination reason
     */
    public String getTerminationReason() {
        return terminationReason;
    }

    /**
     * Sets the termination reason.
     *
     * @param terminationReason the new termination reason
     */
    public void setTerminationReason(String terminationReason) {
        this.terminationReason = terminationReason;
    }

    /**
     * Gets the subscriptions.
     *
     * @return the subscriptions
     */
    public SubscriptionsDto getSubscriptions() {
        return subscriptions;
    }

    /**
     * Sets the subscriptions.
     *
     * @param subscriptions the new subscriptions
     */
    public void setSubscriptions(SubscriptionsDto subscriptions) {
        this.subscriptions = subscriptions;
    }

    /**
     * Gets the billing account description.
     *
     * @return the billing account description
     */
    public String getBillingAccountDescription() {
        return billingAccountDescription;
    }

    /**
     * Sets the billing account description.
     *
     * @param billingAccountDescription the new billing account description
     */
    public void setBillingAccountDescription(String billingAccountDescription) {
        this.billingAccountDescription = billingAccountDescription;
    }

    /**
     * Gets the customer account.
     *
     * @return the customer account
     */
    public String getCustomerAccount() {
        return customerAccount;
    }

    /**
     * Sets the customer account.
     *
     * @param customerAccount the new customer account
     */
    public void setCustomerAccount(String customerAccount) {
        this.customerAccount = customerAccount;
    }

    /**
     * Gets the customer account description.
     *
     * @return the customer account description
     */
    public String getCustomerAccountDescription() {
        return customerAccountDescription;
    }

    /**
     * Sets the customer account description.
     *
     * @param customerAccountDescription the new customer account description
     */
    public void setCustomerAccountDescription(String customerAccountDescription) {
        this.customerAccountDescription = customerAccountDescription;
    }

    /**
     * Gets the customer.
     *
     * @return the customer
     */
    public String getCustomer() {
        return customer;
    }

    /**
     * Sets the customer.
     *
     * @param customer the new customer
     */
    public void setCustomer(String customer) {
        this.customer = customer;
    }

    /**
     * Gets the customer description.
     *
     * @return the customer description
     */
    public String getCustomerDescription() {
        return customerDescription;
    }

    /**
     * Sets the customer description.
     *
     * @param customerDescription the new customer description
     */
    public void setCustomerDescription(String customerDescription) {
        this.customerDescription = customerDescription;
    }


    @Override
    public String toString() {
        return "UserAccountDto [billingAccount=" + billingAccount + ", subscriptionDate=" + subscriptionDate + ", terminationDate=" + terminationDate + ", status=" + status
                + ",statusDate=" + statusDate + ", terminationReason=" + terminationReason + ", subscriptions=" + subscriptions + "]";
    }
}