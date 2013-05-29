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
package org.meveo.service.api.remote;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.meveo.admin.exception.AccountAlreadyExistsException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementAlreadyExistsException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.ElementNotResiliatedOrCanceledException;
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.admin.exception.UnknownAccountException;
import org.meveo.service.api.dto.AddressDTO;
import org.meveo.service.api.dto.BillingAccountDTO;
import org.meveo.service.api.dto.ConsumptionDTO;
import org.meveo.service.api.dto.CustomerAccountDTO;
import org.meveo.service.api.dto.CustomerDTO;
import org.meveo.service.api.dto.ServiceActivationDTO;
import org.meveo.service.api.dto.SubscriptionDTO;
import org.meveo.service.api.dto.UserAccountDTO;

/**
 * Describes remote access to API interface
 * 
 * @author Andrius Karpavicius
 */
@Remote
public interface ApiServiceRemote {

    /**
     * Retrieve customer information
     * 
     * @param code Customer code
     * @return Customer information
     * @throws UnknownAccountException
     */
    public CustomerDTO findCustomer(String code) throws UnknownAccountException;

    /**
     * Create a customer
     * 
     * @param code Customer code
     * @param name Customer name
     * @param brandCode Brand code
     * @param categoryCode Category code
     * @param address Address information
     * @param extRef1 External reference 1
     * @param extRef2 External reference 1
     * @param providerCode Provider code
     * @throws AccountAlreadyExistsException
     * @throws ElementNotFoundException
     */
    public void createCustomer(String code, String name, String brandCode, String categoryCode, AddressDTO address, String extRef1, String extRef2, String providerCode)
            throws AccountAlreadyExistsException, ElementNotFoundException;

    /**
     * Retrieve customer account information
     * 
     * @param code Customer account code
     * @return Customer account information
     * @throws UnknownAccountException
     */
    public CustomerAccountDTO findCustomerAccount(String code) throws UnknownAccountException;

    /**
     * Create a customer account
     * 
     * @param customerCode Code of customer to associate customer account to
     * @param code Customer account code
     * @param description Description
     * @param firstName First name
     * @param lastName Last name
     * @param emailForContact Email for contact
     * @param address Address information
     * @param titleCode Title code
     * @param creditCategoryCode Credit category code
     * @param paymentMethodCode Payment method code
     * @param extRef1 External reference 1
     * @throws AccountAlreadyExistsException
     * @throws UnknownAccountException
     * @throws ElementNotFoundException
     */
    public void createCustomerAccount(String customerCode, String code, String description, String firstName, String lastName, String emailForContact, AddressDTO address,
            String titleCode, Integer creditCategoryCode, Integer paymentMethodCode, String extRef1) throws AccountAlreadyExistsException, UnknownAccountException,
            ElementNotFoundException;

    /**
     * Retrieve billing account information
     * 
     * @param code Billing account code
     * @return Billing account information
     * @throws UnknownAccountException
     */
    public BillingAccountDTO findBillingAccount(String code) throws UnknownAccountException;

    /**
     * Create a billing account
     * 
     * @param customerAccountCode Code of customer account to associate billing account to
     * @param code Billing account code
     * @param description Description
     * @param billingCycleCode Billing cycle code
     * @param firstName First name
     * @param lastName Last name
     * @param address Address information
     * @param paymentMethodCode Payment method code
     * @param elBilling Electronic billing
     * @param elBillingEmail Email for electronic billing notification
     * @param extRef1 External reference 1
     * @param extRef2 External reference 1
     * @param subscriptionDate Subscription date (Now if null)
     * @param nextInvoiceDate Next invoice date (Gets calculated if not provided)
     * @throws AccountAlreadyExistsException
     * @throws UnknownAccountException
     * @throws ElementNotFoundException
     */
    public void createBillingAccount(String customerAccountCode, String code, String description, String billingCycleCode, String firstName, String lastName, AddressDTO address,
            Integer paymentMethodCode, Boolean elBilling, String elBillingEmail, String extRef1, String extRef2, Date subscriptionDate, Date nextInvoiceDate)
            throws AccountAlreadyExistsException, UnknownAccountException, ElementNotFoundException;

    /**
     * Update billing account
     * 
     * @param code Billing account code
     * @param description Description
     * @param billingCycleCode Billing cycle code
     * @param firstName First name
     * @param lastName Last name
     * @param address Address information
     * @param paymentMethodCode Payment method code
     * @param elBilling Electronic billing
     * @param elBillingEmail Email for electronic billing notification
     * @param extRef1 External reference 1
     * @param extRef2 External reference 1
     * @param subscriptionDate Subscription date (Now if null)
     * @param nextInvoiceDate Next invoice date (Gets calculated if not provided)
     * @throws UnknownAccountException
     * @throws ElementNotFoundException
     */
    public void updateBillingAccount(String code, String description, String billingCycleCode, String firstName, String lastName, AddressDTO address, Integer paymentMethodCode,
            Boolean elBilling, String elBillingEmail, String extRef1, String extRef2, Date subscriptionDate, Date nextInvoiceDate) throws UnknownAccountException,
            ElementNotFoundException;

    /**
     * Retrieve user account information
     * 
     * @param code User account code
     * @return User account information
     * @throws UnknownAccountException
     */
    public UserAccountDTO findUserAccount(String code) throws UnknownAccountException;

    /**
     * Create a user account
     * 
     * @param billingAccountCode Code of billing account to associate user account to
     * @param code User account code
     * @param description Description
     * @param firstName First name
     * @param lastName Last name
     * @param titleCode Title code
     * @param address Address information
     * @throws AccountAlreadyExistsException
     * @throws UnknownAccountException
     * @throws ElementNotFoundException
     */
    public void createUserAccount(String billingAccountCode, String code, String description, String firstName, String lastName, String titleCode, AddressDTO address,
            String extRef1, String extRef2) throws AccountAlreadyExistsException, UnknownAccountException, ElementNotFoundException;

    /**
     * Update user account
     * 
     * @param code User account code
     * @param description Description
     * @param firstName First name
     * @param lastName Last name
     * @param titleCode Title code
     * @param address Address information
     * @throws UnknownAccountException
     * @throws ElementNotFoundException
     */
    public void updateUserAccount(String code, String description, String firstName, String lastName, String titleCode, AddressDTO address, String extRef1, String extRef2)
            throws UnknownAccountException, ElementNotFoundException;

    /**
     * Close billing account
     * 
     * @param code Code of a billing account
     * @throws UnknownAccountException
     * @throws ElementNotResiliatedOrCanceledException
     */
    public void closeBillingAccount(String code) throws UnknownAccountException, ElementNotResiliatedOrCanceledException;

    /**
     * Create a subscription
     * 
     * @param userAccountCode Code of user account to associate subscription to
     * @param code Subscription code
     * @param description Description
     * @param offerCode Code of an offer related to subscription
     * @param subscriptionDate Subscription date
     * @param terminationDate Subscription end date (optional)
     * @return Subscription record identifier
     * @throws ElementAlreadyExistsException
     * @throws UnknownAccountException
     * @throws BusinessException
     */
    public Long createSubscription(String userAccountCode, String code, String description, String offerCode, Date subscriptionDate, Date terminationDate)
            throws ElementAlreadyExistsException, UnknownAccountException, BusinessException;

    /**
     * Create subscriptions
     * 
     * @param userAccountCode Code of user account to associate subscription to
     * @param subscriptionCreateInfo A list of subscription information
     * @return A HashMap where key = subscription code and value = subscription identifier
     * @throws ElementAlreadyExistsException
     * @throws UnknownAccountException
     * @throws BusinessException
     */
    public HashMap<String, String> createSubscriptions(String userAccountCode, List<SubscriptionDTO> subscriptionCreateInfo) throws ElementAlreadyExistsException,
            UnknownAccountException, BusinessException;

    /**
     * Terminate subscription
     * 
     * @param code Code of subscription to terminate
     * @param terminationDate Termination date (now if null)
     * @throws BusinessException
     */
    public void terminateSubscription(String code, Date terminationDate, boolean applyAgreement,boolean applyReimbursment,boolean applyTerminationCharges) throws IncorrectSusbcriptionException, BusinessException;

    /**
     * Reactivate subscription
     * 
     * @param code Code of subscription to reactivate
     * @param activationDate Activation date (now if null)
     * @throws IncorrectSusbcriptionException
     * @throws ElementNotResiliatedOrCanceledException
     * @throws BusinessException
     * @throws IncorrectServiceInstanceException
     */
    public void reactivateSubscription(String code, Date activationDate) throws IncorrectSusbcriptionException, ElementNotResiliatedOrCanceledException,
            IncorrectServiceInstanceException, BusinessException;

    /**
     * Suspend subscription
     * 
     * @param code Code of subscription to suspend
     * @param suspensionDate Suspension date (now if null)
     * @throws IncorrectSusbcriptionException
     * @throws BusinessException
     * @throws IncorrectServiceInstanceException
     */
    public void suspendSubscription(String code, Date suspensionDate) throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException;

    /**
     * Retrieve information about subscription
     * 
     * @param code Subscription code
     * @return Subscription information
     * @throws IncorrectSusbcriptionException
     */
    public SubscriptionDTO findSubscription(String code) throws IncorrectSusbcriptionException;

    /**
     * Activate a service under subscription
     * 
     * @param subscriptionCode Code of subscription that service relates to
     * @param serviceCode Code of service to activate
     * @param activationDate Activation date (now if null)
     * @param quantity Number of service instance to activate
     * @return Service record identifier
     * @throws IncorrectSusbcriptionException
     * @throws BusinessException
     */
    public Long activateService(String subscriptionCode, String serviceCode, Date activationDate, int quantity) throws IncorrectSusbcriptionException, BusinessException;

    /**
     * Activate services under subscription
     * 
     * @param serviceActivationInfo
     * @return A hashmap with key = subscriptionCode_serviceCode and value - a service record identifier
     * @throws IncorrectSusbcriptionException
     * @throws BusinessException
     */
    public HashMap<String, String> activateServices(List<ServiceActivationDTO> serviceActivationInfo) throws IncorrectSusbcriptionException, BusinessException;

    /**
     * Terminate a service
     * 
     * @param subscriptioncode Code of subscription, that service relates to
     * @param serviceCode Code of service to terminate
     * @param terminationDate Termination date (now if null)
     * @throws IncorrectSusbcriptionException
     * @throws IncorrectServiceInstanceException
     * @throws BusinessException
     */
    public void terminateService(String subscriptionCode, String serviceCode, Date terminationDate) throws IncorrectSusbcriptionException, IncorrectServiceInstanceException,
            BusinessException;

    /**
     * Register a customer account operation
     * 
     * @param customerAccountCode Customer account code
     * @param creditChargeType Operation type
     * @param creditChargeCode Operation code
     * @param creditChargeDescription Operation description
     * @param amount Amount to credit/debit
     * @param operationDate Operation registration date (Now if null)
     * @param efectiveDate Date when operation is effective (Now if null)
     */
    public void createCustomerAccountOperation(String customerAccountCode, String creditChargeType, String creditChargeCode, String creditChargeDescription, Double amount,
            Date operationDate, Date efectiveDate);

    /**
     * Obtain consumption and charge information for a given billing period and given subscription
     * 
     * @param subscriptionCode Subscription code
     * @param infoType Information type (optional)
     * @param billingCycle Billing cycle 0 - current, -1 - last, -2 - before last, -3 ... (optional) Current cycle is assumed, if not specified
     * @param sumarizeConsumption If true, a summarized consumption information will be provided. If false, charges will be split into national/roaming and incoming/outgoing
     * @return Usage and charge information
     */
    public ConsumptionDTO getConsumption(String subscriptionCode, String infoType, Integer billingCycle, boolean sumarizeConsumption) throws IncorrectSusbcriptionException;
}