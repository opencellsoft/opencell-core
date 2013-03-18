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
package org.meveo.service.api.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.meveo.admin.exception.AccountAlreadyExistsException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementAlreadyExistsException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.ElementNotResiliatedOrCanceledException;
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.admin.exception.UnknownAccountException;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CreditCategoryEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Title;
import org.meveo.service.api.dto.AddressDTO;
import org.meveo.service.api.dto.BillingAccountDTO;
import org.meveo.service.api.dto.ConsumptionDTO;
import org.meveo.service.api.dto.CustomerAccountDTO;
import org.meveo.service.api.dto.CustomerDTO;
import org.meveo.service.api.dto.ServiceActivationDTO;
import org.meveo.service.api.dto.SubscriptionDTO;
import org.meveo.service.api.dto.UserAccountDTO;
import org.meveo.service.api.remote.ApiServiceRemote;
import org.meveo.service.billing.local.BillingAccountServiceLocal;
import org.meveo.service.billing.local.BillingCycleServiceLocal;
import org.meveo.service.billing.local.RatedTransactionServiceLocal;
import org.meveo.service.billing.local.ServiceInstanceServiceLocal;
import org.meveo.service.billing.local.SubscriptionServiceLocal;
import org.meveo.service.billing.local.UserAccountServiceLocal;
import org.meveo.service.catalog.local.ServiceTemplateServiceLocal;
import org.meveo.service.catalog.local.TitleServiceLocal;
import org.meveo.service.crm.local.CustomerBrandServiceLocal;
import org.meveo.service.crm.local.CustomerCategoryServiceLocal;
import org.meveo.service.crm.local.CustomerServiceLocal;
import org.meveo.service.crm.local.ProviderServiceLocal;
import org.meveo.service.payments.local.CustomerAccountServiceLocal;
import org.slf4j.Logger;

/**
 * API service implementation
 * 
 * @author Andrius Karpavicius
 */
@Stateless
@Named
public class ApiService implements ApiServiceRemote {

	@Inject
	private CustomerServiceLocal customerService;

	@Inject
	private CustomerAccountServiceLocal customerAccountService;

	@Inject
	private BillingAccountServiceLocal billingAccountService;

	@Inject
	private UserAccountServiceLocal userAccountService;

	@Inject
	private SubscriptionServiceLocal subscriptionService;

	@Inject
	private ServiceInstanceServiceLocal serviceInstanceService;

	@Inject
	private ServiceTemplateServiceLocal serviceTemplateService;

	@Inject
	private CustomerBrandServiceLocal customerBrandService;

	@Inject
	private CustomerCategoryServiceLocal customerCategoryService;

	@Inject
	private TitleServiceLocal titleService;

	@Inject
	private BillingCycleServiceLocal billingCycleService;

	@Inject
	private ProviderServiceLocal providerService;

	@Inject
	private Logger log;

	@Inject
	private EntityManager entityManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.service.api.remote.ApiServiceRemote#activateService(java.lang
	 * .String, java.lang.String, java.util.Date, int)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Long activateService(String subscriptionCode, String serviceCode,
			Date activationDate, int quantity)
			throws IncorrectSusbcriptionException, ElementNotFoundException,
			BusinessException {

		// Upercase all codes, as Meveo stores them upercased
		subscriptionCode = subscriptionCode.toUpperCase();
		serviceCode = serviceCode.toUpperCase();

		log.debug(
				"[Meveo] [API] Activating service: subscriptionCode {0}, serviceCode {1}, activationDate {2}, quantity {3}",
				subscriptionCode, serviceCode, activationDate, quantity);

		Subscription subscription = subscriptionService
				.findByCode(subscriptionCode);
		if (subscription == null) {
			log.error(
					"[Meveo] [API] Activating service: Subscription {0}, service {1} - subscription does not exist",
					subscriptionCode, serviceCode);
			throw new IncorrectSusbcriptionException(
					"Subscription does not exist. code=" + subscriptionCode);
		}

		ServiceTemplate serviceTemplate = null;
		serviceTemplate = serviceTemplateService.findByCode(serviceCode);
		if (serviceTemplate == null) {
			log.error(
					"[Meveo] [API] Activating service: Subscription {0}, service {1} - Service template does not exist",
					subscriptionCode, serviceCode);
			throw new ElementNotFoundException(serviceCode, "ServiceTemplate");
		}

		// Instantiate a service in subscription
		ServiceInstance serviceInstance = new ServiceInstance();
		serviceInstance.setCode(serviceTemplate.getCode());
		serviceInstance.setServiceTemplate(serviceTemplate);
		serviceInstance.setSubscription(subscription);
		serviceInstance.setSubscriptionDate(activationDate == null ? new Date()
				: activationDate);
		serviceInstance.setQuantity(quantity);
		serviceInstance.setProvider(subscription.getProvider());

		serviceInstanceService.serviceInstanciation(serviceInstance, null);

		// Activate a service on subscription
		serviceInstanceService.serviceActivation(serviceInstance, null, null,
				null);

		entityManager.flush();

		log.debug(
				"[Meveo] [API] Activating service: Subscription {0}, service {1} - service activated",
				subscriptionCode, serviceCode);

		return serviceInstance.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.service.api.remote.ApiServiceRemote#activateServices(java.util
	 * .List)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public HashMap<String, String> activateServices(
			List<ServiceActivationDTO> serviceActivationInfo)
			throws IncorrectSusbcriptionException, ElementNotFoundException,
			BusinessException {

		log.debug("[Meveo] [API] Activating services {0}",
				serviceActivationInfo);

		HashMap<String, String> results = new HashMap<String, String>();

		Subscription subscription = null;
		ServiceTemplate serviceTemplate = null;
		for (ServiceActivationDTO serviceActivationDTO : serviceActivationInfo) {

			// Upercase code values, as Meveo stores them upercase
			String subscriptionCode = serviceActivationDTO
					.getSubscriptionCode().toUpperCase();
			String serviceCode = serviceActivationDTO.getServiceCode()
					.toUpperCase();

			subscription = subscriptionService.findByCode(subscriptionCode);
			if (subscription == null) {
				log.error(
						"[Meveo] [API] Activating service: Subscription {0}, service {1} - subscription does not exist",
						subscriptionCode, serviceCode);
				throw new IncorrectSusbcriptionException(
						"Subscription does not exist. code=" + subscriptionCode);
			}

			serviceTemplate = null;
			serviceTemplate = serviceTemplateService.findByCode(serviceCode);
			if (serviceTemplate == null) {
				log.error(
						"[Meveo] [API] Activating service: Subscription {0}, service {1} - Service template does not exist",
						serviceActivationDTO.getSubscriptionCode(), serviceCode);
				throw new ElementNotFoundException(serviceCode,
						"ServiceTemplate");
			}

			// Instantiate a service in subscription
			ServiceInstance serviceInstance = new ServiceInstance();
			serviceInstance.setCode(serviceTemplate.getCode());
			serviceInstance.setServiceTemplate(serviceTemplate);
			serviceInstance.setSubscription(subscription);
			serviceInstance.setSubscriptionDate(serviceActivationDTO
					.getActivationDate());
			serviceInstance.setQuantity(serviceActivationDTO.getQuantity());
			serviceInstance.setProvider(subscription.getProvider());

			serviceInstanceService.serviceInstanciation(serviceInstance, null);

			// Activate a service on subscription
			serviceInstanceService.serviceActivation(serviceInstance, null,
					null, null);

			// Here use original values of subscription code and service code,
			// as calling program might not know of change to uppercase
			results.put(serviceActivationDTO.getSubscriptionCode() + "_"
					+ serviceActivationDTO.getServiceCode(), serviceInstance
					.getId().toString());
		}

		entityManager.flush();

		log.debug("[Meveo] [API] Activating service: Services have been activated");
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.service.api.remote.ApiServiceRemote#closeBillingAccount(java
	 * .lang.String)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void closeBillingAccount(String code)
			throws UnknownAccountException,
			ElementNotResiliatedOrCanceledException {

		// Uppercase code values, as Meveo stores them uppercase
		code = code.toUpperCase();

		log.debug("[Meveo] [API] Close billing account {0}", code);

		billingAccountService.closeBillingAccount(code, null);

		entityManager.flush();

		log.debug("[Meveo] [API] Billing account {0} was closed", code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.service.api.remote.ApiServiceRemote#createBillingAccount(java
	 * .lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, org.meveo.service.api.dto.AddressDTO,
	 * java.lang.Integer, java.lang.Boolean, java.lang.String, java.lang.String,
	 * java.lang.String, java.util.Date, java.util.Date)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createBillingAccount(String customerAccountCode, String code,
			String description, String billingCycleCode, String firstName,
			String lastName, AddressDTO address, Integer paymentMethodCode,
			Boolean elBilling, String elBillingEmail, String extRef1,
			String extRef2, Date subscriptionDate, Date nextInvoiceDate)
			throws AccountAlreadyExistsException, UnknownAccountException,
			ElementNotFoundException {

		// Uppercase code values, as Meveo stores them uppercase
		customerAccountCode = customerAccountCode.toUpperCase();
		code = code.toUpperCase();

		log.debug(
				"[Meveo] [API] Creating billing account: customerAccountCode {0}, code {1}, description {2}, billingCycleCode {3}, firstName {4}, lastName {5}, address {6}, paymentMethodCode {7}, elBilling {8}, elBillingEmail {9}, extRef1 "
						+ extRef1
						+ ",  extRef2 "
						+ extRef2
						+ ",  subscriptionDate "
						+ subscriptionDate
						+ ",  nextInvoiceDate " + nextInvoiceDate,
				customerAccountCode, code, description, billingCycleCode,
				firstName, lastName, address, paymentMethodCode, elBilling,
				elBillingEmail);

		// Find a customer
		CustomerAccount customerAccount = customerAccountService
				.findByCode(customerAccountCode);
		if (customerAccount == null) {
			log.error(
					"[Meveo] [API] Creating billing account: billing account {0} - customer {1} was not found",
					code, customerAccountCode);
			throw new UnknownAccountException(customerAccountCode);
		}

		// Check that record does not exist already
		BillingAccount billingAccount = billingAccountService.findByCode(code);
		if (billingAccount != null) {
			log.error(
					"[Meveo] [API] Creating billing account: billing account {0} - account already exists",
					code);
			throw new AccountAlreadyExistsException(code);
		}

		BillingCycle billingCycle = billingCycleService.findByBillingCycleCode(
				billingCycleCode, customerAccount.getProvider());
		if (billingCycle == null) {
			log.error(
					"[Meveo] [API] Creating billing account: billing account {0} - billing cycle {1} was not found for provider {2}",
					code, billingCycleCode, customerAccount.getProvider());
			throw new ElementNotFoundException(billingCycleCode, "BillingCycle");
		}

		// Create a new account
		billingAccount = new BillingAccount();
		billingAccount.setCustomerAccount(customerAccount);
		billingAccount.setCode(code);
		billingAccount.setBillingCycle(billingCycle);
		billingAccount.setDescription(description);
		billingAccount.setAddress(convertToAddressEntity(address));
		billingAccount.setName(new org.meveo.model.shared.Name());
		billingAccount.getName().setFirstName(firstName);
		billingAccount.getName().setLastName(lastName);
		billingAccount.setElectronicBilling(elBilling);
		billingAccount.setEmail(elBillingEmail);

		billingAccount.setExternalRef1(extRef1);
		billingAccount.setExternalRef2(extRef2);
		billingAccount.setSubscriptionDate(subscriptionDate);
		billingAccount.setNextInvoiceDate(nextInvoiceDate);
		billingAccount.setStatus(AccountStatusEnum.ACTIVE);
		billingAccount.setProvider(customerAccount.getProvider());

		// Check that payment method was matched
		if (paymentMethodCode != null) {
			billingAccount.setPaymentMethod(PaymentMethodEnum
					.getValue(paymentMethodCode));
			if (billingAccount.getPaymentMethod() == null) {
				throw new ElementNotFoundException(
						paymentMethodCode.toString(), "PaymentMethod");
			}
		}

		billingAccountService.createBillingAccount(billingAccount, null);

		entityManager.flush();

		log.debug(
				"[Meveo] [API] Creating billing account: billing account {0} was created",
				code);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.service.api.remote.ApiServiceRemote#createCustomer(java.lang
	 * .String, java.lang.String, java.lang.String, java.lang.String,
	 * org.meveo.service.api.dto.AddressDTO, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createCustomer(String code, String name, String brandCode,
			String categoryCode, AddressDTO address, String extRef1,
			String extRef2, String providerCode)
			throws AccountAlreadyExistsException, ElementNotFoundException {

		// Uppercase code values, as Meveo stores them uppercase
		code = code.toUpperCase();

		log.debug(
				"[Meveo] [API] Creating a customer code {0}, name {1}, brandCode {2}, categoryCode {3}, address {4}, extRef1 {5}, extRef2 {6}, providerCode {7}",
				code, name, brandCode, categoryCode, address, extRef1, extRef2,
				providerCode);

		CustomerBrand customerBrand = customerBrandService
				.findByCode(brandCode);
		if (customerBrand == null) {
			log.error(
					"[Meveo] [API] Creating customer: customer {0} - customer brand {1} was not found",
					code, brandCode);
			throw new ElementNotFoundException(brandCode, "CustomerBrand");
		}
		CustomerCategory customerCategory = customerCategoryService
				.findByCode(categoryCode);
		if (customerCategory == null) {
			log.error(
					"[Meveo] [API] Creating customer: customer {0} - customer category {1} was not found",
					code, categoryCode);
			throw new ElementNotFoundException(categoryCode, "CustomerCategory");
		}

		Provider provider = providerService.findByCode(providerCode);
		if (provider == null) {
			log.error(
					"[Meveo] [API] Creating customer: customer {0} - provider {1} was not found",
					code, providerCode);
			throw new ElementNotFoundException(providerCode, "Provider");
		}

		// Check that record does not exist already
		Customer customer = customerService.findByCode(code);
		if (customer != null) {
			log.error(
					"[Meveo] [API] Creating customer: customer {0} - customer already exists",
					code);
			throw new AccountAlreadyExistsException(code);
		}

		// Create a customer
		customer = new Customer();
		customer.setActive(true);
		customer.setAddress(convertToAddressEntity(address));
		customer.setCode(code);
		customer.setCustomerBrand(customerBrand);
		customer.setCustomerCategory(customerCategory);
		customer.setDescription(name);
		customer.setExternalRef1(extRef1);
		customer.setExternalRef2(extRef2);
		customer.setProvider(provider);

		customerService.create(customer);

		entityManager.flush();

		log.debug(
				"[Meveo] [API] Creating customer: customer {0} - customer was created",
				code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.service.api.remote.ApiServiceRemote#createCustomerAccount(java
	 * .lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, org.meveo.service.api.dto.AddressDTO,
	 * java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.String)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createCustomerAccount(String customerCode, String code,
			String description, String firstName, String lastName,
			String emailForContact, AddressDTO address, String titleCode,
			Integer creditCategoryCode, Integer paymentMethodCode,
			String extRef1) throws AccountAlreadyExistsException,
			UnknownAccountException, ElementNotFoundException {

		// Uppercase code values, as Meveo stores them uppercase
		customerCode = customerCode.toUpperCase();
		code = code.toUpperCase();

		log.debug(
				"[Meveo] [API] Creating a customer account: customerCode {0}, code {1}, description {2}, firstName {3}, lastName {4}, emailForContact {5}, address {6}, titleCode {7},  creditCategoryCode {8},  paymentMethodCode {9},  extRef1 "
						+ extRef1, customerCode, code, description, firstName,
				lastName, emailForContact, address, titleCode,
				creditCategoryCode, paymentMethodCode);

		Customer customer = customerService.findByCode(customerCode);
		if (customer == null) {
			log.error(
					"[Meveo] [API] Creating customer account: customer account {0} - customer not found",
					code);
			throw new UnknownAccountException(customerCode);
		}

		Title title = titleService
				.findByCode(customer.getProvider(), titleCode);
		if (title == null) {
			log.error(
					"[Meveo] [API] Creating customer account: customer account {0} - title does not exist",
					code);
			throw new ElementNotFoundException(titleCode, "Title");
		}

		// Check that record does not exist already
		CustomerAccount customerAccount = customerAccountService
				.findByCode(code);
		if (customerAccount != null) {
			log.error(
					"[Meveo] [API] Creating customer account: customer account {0} - account already exists",
					code);
			throw new AccountAlreadyExistsException(code);
		}

		// Create a customer account
		customerAccount = new CustomerAccount();
		customerAccount.setCustomer(customer);
		customerAccount.setCode(code);
		customerAccount.setDescription(description);
		customerAccount.setName(new org.meveo.model.shared.Name());
		customerAccount.getName().setTitle(title);
		customerAccount.getName().setFirstName(firstName);
		customerAccount.getName().setLastName(lastName);
		customerAccount.setAddress(convertToAddressEntity(address));
		customerAccount.setContactInformation(new ContactInformation());
		customerAccount.getContactInformation().setEmail(emailForContact);
		customerAccount.setStatus(CustomerAccountStatusEnum.ACTIVE);
		customerAccount.setDunningLevel(DunningLevelEnum.R0);
		customerAccount.setDateStatus(new Date());
		customerAccount.setDateDunningLevel(new Date());
		customerAccount.setProvider(customer.getProvider());

		customerAccount.setExternalRef1(extRef1);

		// Check that payment method was matched
		if (paymentMethodCode != null) {
			customerAccount.setPaymentMethod(PaymentMethodEnum
					.getValue(paymentMethodCode));
			if (customerAccount.getPaymentMethod() == null) {
				log.error(
						"[Meveo] [API] Creating customer account: customer account {0} - payment method not found",
						code);
				throw new ElementNotFoundException(
						paymentMethodCode.toString(), "PaymentMethod");
			}
		}

		// Check that credit category was matched
		if (creditCategoryCode != null) {
			customerAccount.setCreditCategory(CreditCategoryEnum
					.getValue(creditCategoryCode));
			if (customerAccount.getCreditCategory() == null) {
				log.error(
						"[Meveo] [API] Creating customer account: customer account {0} - credit category not found",
						code);
				throw new ElementNotFoundException(
						creditCategoryCode.toString(), "CreditCategory");
			}
		}

		customerAccountService.create(customerAccount);

		entityManager.flush();

		log.debug(
				"[Meveo] [API] Creating customer account: customer account {0} - account was created",
				code);
	}

	public void createCustomerAccountOperation(String customerAccountCode,
			String creditChargeType, String creditChargeCode,
			String creditChargeDescription, Double amount, Date operationDate,
			Date efectiveDate) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.service.api.remote.ApiServiceRemote#createSubscription(java
	 * .lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.util.Date, java.util.Date)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Long createSubscription(String userAccountCode, String code,
			String description, String offerCode, Date subscriptionDate,
			Date terminationDate) throws ElementAlreadyExistsException,
			UnknownAccountException, BusinessException {

		// Uppercase code values, as Meveo stores them uppercase
		userAccountCode = userAccountCode.toUpperCase();
		code = code.toUpperCase();
		offerCode = offerCode.toUpperCase();

		log.debug(
				"[Meveo] [API] Creating a subscription userAccountCode {0}, code {1}, description {2}, offerCode {3}, subscriptionDate {4}, terminationDate {5}",
				userAccountCode, code, description, offerCode,
				subscriptionDate, terminationDate);

		Subscription subscription = subscriptionService.findByCode(code);
		if (subscription != null) {
			log.error(
					"[Meveo] [API] Creating subscription: subscription {0} - subscription already exists",
					code);
			throw new ElementAlreadyExistsException(code, "Subscription");
		}

		UserAccount userAccount = userAccountService
				.findByCode(userAccountCode);
		if (userAccount == null) {
			log.error(
					"[Meveo] [API] Creating subscription: subscription {0} - user account not found",
					code);
			throw new UnknownAccountException(userAccountCode);
		}

		// Create a subscription and bind to an offer
		subscription = new Subscription();
		subscription.setCode(code);
		subscription.setDescription(description);
		subscription.setEndAgrementDate(terminationDate);
		subscription.setSubscriptionDate(subscriptionDate);
		subscription.setProvider(userAccount.getProvider());

		subscriptionService.createSubscription(userAccountCode, subscription,
				null);
		subscriptionService.subscriptionOffer(code, offerCode,
				subscriptionDate, null);

		entityManager.flush();

		log.debug(
				"[Meveo] [API] Creating subscription: subscription {0} - subscription was created",
				code);

		return subscription.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.service.api.remote.ApiServiceRemote#createSubscriptions(java
	 * .lang.String, java.util.List)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public HashMap<String, String> createSubscriptions(String userAccountCode,
			List<SubscriptionDTO> subscriptionCreateInfo)
			throws ElementAlreadyExistsException, UnknownAccountException,
			BusinessException {

		// Uppercase code values, as Meveo stores them uppercase
		userAccountCode = userAccountCode.toUpperCase();

		log.debug(
				"[Meveo] [API] Creating subscriptions userAccountCode {0}, subscriptionCreateInfo {1}",
				userAccountCode, subscriptionCreateInfo);

		UserAccount userAccount = userAccountService
				.findByCode(userAccountCode);
		if (userAccount == null) {
			log.error("[Meveo] [API] Creating subscriptions: user account not found");
			throw new UnknownAccountException(userAccountCode);
		}

		HashMap<String, String> results = new HashMap<String, String>();

		Subscription subscription = null;
		for (SubscriptionDTO subscriptionDTO : subscriptionCreateInfo) {

			// Uppercase code values, as Meveo stores them uppercase
			String code = subscriptionDTO.getCode().toUpperCase();
			String offerCode = subscriptionDTO.getOfferCode().toUpperCase();

			// Check if subscription exists already. If its - reuse it
			subscription = subscriptionService.findByCode(code);

			// Check if offer needs to be assigned
			if (subscription != null && subscription.getOffer() == null) {
				subscriptionService.subscriptionOffer(code, offerCode,
						subscriptionDTO.getSubscriptionDate(), null);

				// Create a new one
			} else if (subscription == null) {

				subscription = new Subscription();
				subscription.setCode(code);
				subscription.setDescription(subscriptionDTO.getDescription());
				subscription.setEndAgrementDate(subscriptionDTO
						.getTerminationDate());
				subscription.setSubscriptionDate(subscriptionDTO
						.getSubscriptionDate());
				subscription.setProvider(userAccount.getProvider());

				subscriptionService.createSubscription(userAccountCode,
						subscription, null);
				subscriptionService.subscriptionOffer(code, offerCode,
						subscriptionDTO.getSubscriptionDate(), null);
			}

			// Here use original subscription code, as calling application might
			// not know of the change to uppercase
			results.put(subscriptionDTO.getCode(), subscription.getId()
					.toString());
		}

		entityManager.flush();

		log.debug("[Meveo] [API] Creating subscriptions: subscriptions were created");

		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.service.api.remote.ApiServiceRemote#createUserAccount(java.
	 * lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, org.meveo.service.api.dto.AddressDTO,
	 * java.lang.String, java.lang.String)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createUserAccount(String billingAccountCode, String code,
			String description, String firstName, String lastName,
			String titleCode, AddressDTO address, String extRef1, String extRef2)
			throws AccountAlreadyExistsException, UnknownAccountException,
			ElementNotFoundException {

		// Uppercase code values, as Meveo stores them uppercase
		billingAccountCode = billingAccountCode.toUpperCase();
		code = code.toUpperCase();

		log.debug(
				"[Meveo] [API] Creating user account billingAccountCode {0}, code {1}, description {2}, firstName {3}, lastName {4}, titleCode {5}, address {6}, extRef1 {7}, extRef2 {8}",
				billingAccountCode, code, description, firstName, lastName,
				titleCode, address, extRef1, extRef2);

		BillingAccount billingAccount = billingAccountService
				.findByCode(billingAccountCode);
		if (billingAccount == null) {
			log.error(
					"[Meveo] [API] Creating user account: account {0} - billing account not found",
					code);
			throw new UnknownAccountException(billingAccountCode);
		}

		Title title = titleService.findByCode(billingAccount.getProvider(),
				titleCode);
		if (title == null) {
			log.error(
					"[Meveo] [API] Creating user account: account {0} - title not found",
					code);
			throw new ElementNotFoundException(titleCode, "Title");
		}

		// Check that record does not exist already
		UserAccount userAccount = userAccountService.findByCode(code);
		if (userAccount != null) {
			log.error(
					"[Meveo] [API] Creating user account: account {0} - account exists already",
					code);
			throw new AccountAlreadyExistsException(code);
		}

		// Create an account
		userAccount = new UserAccount();
		userAccount.setBillingAccount(billingAccount);
		userAccount.setAddress(convertToAddressEntity(address));
		userAccount.setCode(code);
		userAccount.setDescription(description);
		userAccount.setExternalRef1(extRef1);
		userAccount.setExternalRef2(extRef2);
		userAccount.setStatus(AccountStatusEnum.ACTIVE);
		userAccount.setName(new org.meveo.model.shared.Name());
		userAccount.getName().setTitle(title);
		userAccount.getName().setFirstName(firstName);
		userAccount.getName().setLastName(lastName);
		userAccount.setProvider(billingAccount.getProvider());

		userAccountService.createUserAccount(billingAccountCode, userAccount,
				null);

		entityManager.flush();

		log.debug(
				"[Meveo] [API] Creating user account: account {0} - account was created",
				code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.service.api.remote.ApiServiceRemote#findBillingAccount(java
	 * .lang.String)
	 */
	public BillingAccountDTO findBillingAccount(String code)
			throws UnknownAccountException {

		// Uppercase code values, as Meveo stores them uppercase
		code = code.toUpperCase();

		log.debug("[Meveo] [API] Find billing account: code {0}", code);

		BillingAccount billingAccount = billingAccountService.findByCode(code);
		if (billingAccount == null) {
			log.error(
					"[Meveo] [API] Find billing account: account {0} - account not found",
					code);
			throw new UnknownAccountException(code);
		}

		BillingAccountDTO billingAccountDTO = new BillingAccountDTO(
				billingAccount.getId(), billingAccount.getCode(),
				billingAccount.getExternalRef1(),
				billingAccount.getExternalRef2(),
				convertToAddressDto(billingAccount.getAddress()),
				billingAccount.getName().getTitle().getCode(), billingAccount
						.getName().getFirstName(), billingAccount.getName()
						.getLastName(), billingAccount.getCustomerAccount()
						.getCode());

		return billingAccountDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.service.api.remote.ApiServiceRemote#findCustomer(java.lang.
	 * String)
	 */
	public CustomerDTO findCustomer(String code) throws UnknownAccountException {

		// Uppercase code values, as Meveo stores them uppercase
		code = code.toUpperCase();

		log.debug("[Meveo] [API] Find customer: code {0}", code);

		Customer customer = customerService.findByCode(code);
		if (customer == null) {
			log.error(
					"[Meveo] [API] Find customer: customer {0} - customer not found",
					code);
			throw new UnknownAccountException(code);
		}

		return new CustomerDTO(customer.getId(), customer.getCode(),
				customer.getDescription(), customer.getExternalRef1(),
				customer.getExternalRef2(),
				convertToAddressDto(customer.getAddress()), customer
						.getCustomerBrand().getCode(), customer
						.getCustomerCategory().getCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.service.api.remote.ApiServiceRemote#findCustomerAccount(java
	 * .lang.String)
	 */
	public CustomerAccountDTO findCustomerAccount(String code)
			throws UnknownAccountException {

		// Uppercase code values, as Meveo stores them uppercase
		code = code.toUpperCase();

		log.debug("[Meveo] [API] Find customer account: code {0}", code);

		CustomerAccount customerAccount = customerAccountService
				.findByCode(code);
		if (customerAccount == null) {
			log.error(
					"[Meveo] [API] Find customer account: account {0} - account not found",
					code);
			throw new UnknownAccountException(code);
		}

		CustomerAccountDTO customerAccountDTO = new CustomerAccountDTO(
				customerAccount.getId(), customerAccount.getCode(),
				customerAccount.getExternalRef1(),
				customerAccount.getExternalRef2(),
				convertToAddressDto(customerAccount.getAddress()),
				customerAccount.getName().getTitle().getCode(), customerAccount
						.getName().getFirstName(), customerAccount.getName()
						.getLastName(), customerAccount.getCustomer().getCode());

		return customerAccountDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.service.api.remote.ApiServiceRemote#findUserAccount(java.lang
	 * .String)
	 */
	public UserAccountDTO findUserAccount(String code)
			throws UnknownAccountException {

		// Uppercase code values, as Meveo stores them uppercase
		code = code.toUpperCase();

		log.debug("[Meveo] [API] Find user account: code {0}", code);

		UserAccount userAccount = userAccountService.findByCode(code);
		if (userAccount == null) {
			log.error(
					"[Meveo] [API] Find user account: account {0} - account not found",
					code);
			throw new UnknownAccountException(code);
		}

		UserAccountDTO userAccountDTO = new UserAccountDTO(userAccount.getId(),
				userAccount.getCode(), userAccount.getExternalRef1(),
				userAccount.getExternalRef2(),
				convertToAddressDto(userAccount.getAddress()), userAccount
						.getName().getTitle().getCode(), userAccount.getName()
						.getFirstName(), userAccount.getName().getLastName(),
				userAccount.getBillingAccount().getCode());

		return userAccountDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.service.api.remote.ApiServiceRemote#findSubscription(java.lang
	 * .String)
	 */
	public SubscriptionDTO findSubscription(String code)
			throws IncorrectSusbcriptionException {

		// Uppercase code values, as Meveo stores them uppercase
		code = code.toUpperCase();

		log.debug("[Meveo] [API] Find subscription: code {0}", code);

		Subscription subscription = subscriptionService.findByCode(code);
		if (subscription == null) {
			log.error(
					"[Meveo] [API] Find subscription: subscription {0} - not found",
					code);
			throw new IncorrectSusbcriptionException(
					"subscription does not exist. code=" + code);
		}

		SubscriptionDTO subscriptionDTO = new SubscriptionDTO(subscription
				.getUserAccount().getCode(), subscription.getCode(),
				subscription.getDescription(), subscription.getOffer()
						.getCode(), subscription.getSubscriptionDate(),
				subscription.getTerminationDate());

		return subscriptionDTO;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.service.api.remote.ApiServiceRemote#reactivateSubscription(
	 * java.lang.String, java.util.Date)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void reactivateSubscription(String code, Date activationDate)
			throws IncorrectSusbcriptionException,
			ElementNotResiliatedOrCanceledException,
			IncorrectServiceInstanceException, BusinessException {

		// Uppercase code values, as Meveo stores them uppercase
		code = code.toUpperCase();

		log.debug(
				"[Meveo] [API] Reactivate subscription: code {0}, activationDate {1}",
				code, activationDate);

		subscriptionService
				.subscriptionReactivation(code, activationDate, null);

		entityManager.flush();

		log.debug(
				"[Meveo] [API] Reactivate subscription: code {0} - subscription reactivated",
				code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.service.api.remote.ApiServiceRemote#suspendSubscription(java
	 * .lang.String, java.util.Date)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void suspendSubscription(String code, Date suspensionDate)
			throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {

		// Uppercase code values, as Meveo stores them uppercase
		code = code.toUpperCase();

		log.debug(
				"[Meveo] [API] Suspend subscription: code {0}, suspensionDate {1}",
				code, suspensionDate);

		subscriptionService.subscriptionSuspension(code, suspensionDate, null);

		entityManager.flush();

		log.debug(
				"[Meveo] [API] Suspend subscription: code {0} - subscription suspended",
				code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.service.api.remote.ApiServiceRemote#terminateService(java.lang
	 * .String, java.lang.String, java.util.Date)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void terminateService(String subscriptionCode, String serviceCode,
			Date terminationDate) throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException {

		// Uppercase code values, as Meveo stores them uppercase
		subscriptionCode = subscriptionCode.toUpperCase();
		serviceCode = serviceCode.toUpperCase();

		log.debug(
				"[Meveo] [API] Terminate service: subscriptionCode {0}, serviceCode {1}, terminationDate {2}",
				subscriptionCode, serviceCode, terminationDate);

		ServiceInstance serviceInstance = serviceInstanceService
				.findByCodeAndSubscription(serviceCode, subscriptionCode);
		if (serviceInstance == null) {
			log.debug(
					"[Meveo] [API] Terminate service: subscriptionCode {0}, serviceCode {1} - service or subscription not found",
					subscriptionCode, serviceCode);
			throw new IncorrectServiceInstanceException(
					"Could not find a service " + serviceCode
							+ " in subscription " + subscriptionCode);
		}

		serviceInstanceService.serviceTermination(serviceInstance,
				terminationDate, null);

		entityManager.flush();

		log.debug(
				"[Meveo] [API] Terminate service: subscriptionCode {0}, serviceCode {1} - service terminated",
				subscriptionCode, serviceCode);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.service.api.remote.ApiServiceRemote#terminateSubscription(java
	 * .lang.String, java.util.Date)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void terminateSubscription(String code, Date terminationDate)
			throws IncorrectSusbcriptionException, BusinessException {

		// Uppercase code values, as Meveo stores them uppercase
		code = code.toUpperCase();

		log.debug(
				"[Meveo] [API] Terminate subscription: code {0}, terminationDate {1}",
				code, terminationDate);

		subscriptionService
				.subscriptionTermination(code, terminationDate, null);

		entityManager.flush();

		log.debug(
				"[Meveo] [API] Terminate subscription: code {0} - subscription terminated",
				code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.service.api.remote.ApiServiceRemote#updateBillingAccount(java
	 * .lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, org.meveo.service.api.dto.AddressDTO,
	 * java.lang.Integer, java.lang.Boolean, java.lang.String, java.lang.String,
	 * java.lang.String, java.util.Date, java.util.Date)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateBillingAccount(String code, String description,
			String billingCycleCode, String firstName, String lastName,
			AddressDTO address, Integer paymentMethodCode, Boolean elBilling,
			String elBillingEmail, String extRef1, String extRef2,
			Date subscriptionDate, Date nextInvoiceDate)
			throws UnknownAccountException, ElementNotFoundException {

		// Uppercase code values, as Meveo stores them uppercase
		code = code.toUpperCase();

		log.debug(
				"[Meveo] [API] Update billing account: code {0}, description {1}, billingCycleCode {2}, firstName {3}, lastName {4}, address {5}, paymentMethodCode {6}, elBilling {7}, elBillingEmail {8}, extRef1 "
						+ extRef1
						+ ",  extRef2 "
						+ extRef2
						+ ",  subscriptionDate "
						+ subscriptionDate
						+ ",  nextInvoiceDate " + nextInvoiceDate, code,
				description, billingCycleCode, firstName, lastName, address,
				paymentMethodCode, elBilling, elBillingEmail);

		BillingAccount billingAccount = billingAccountService.findByCode(code);
		if (billingAccount == null) {
			log.error(
					"[Meveo] [API] Updating billing account: billing account {0} - account was not found",
					code);
			throw new UnknownAccountException(code);
		}

		if (billingCycleCode != null) {
			BillingCycle billingCycle = billingCycleService
					.findByBillingCycleCode(billingCycleCode,
							billingAccount.getProvider());
			if (billingCycle == null) {
				log.error(
						"[Meveo] [API] Update billing account: billing account {0} - billing cycle {1} was not found for provider {2}",
						code, billingCycleCode, billingAccount.getProvider());
				throw new ElementNotFoundException(billingCycleCode,
						"BillingCycle");
			}
			billingAccount.setBillingCycle(billingCycle);
		}
		if (description != null) {
			billingAccount.setDescription(description);
		}
		if (address != null) {
			billingAccount.setAddress(convertToAddressEntity(address));
		}

		if (elBilling != null) {
			billingAccount.setElectronicBilling(elBilling);
		}
		if (elBillingEmail != null) {
			billingAccount.setEmail(elBillingEmail);
		}
		if (extRef1 != null) {
			billingAccount.setExternalRef1(extRef1);
		}
		if (extRef2 != null) {
			billingAccount.setExternalRef2(extRef2);
		}
		if (subscriptionDate != null) {
			billingAccount.setSubscriptionDate(subscriptionDate);
		}
		if (nextInvoiceDate != null) {
			billingAccount.setNextInvoiceDate(nextInvoiceDate);
		}

		if (paymentMethodCode != null) {
			billingAccount.setPaymentMethod(PaymentMethodEnum
					.getValue(paymentMethodCode));
			if (billingAccount.getPaymentMethod() == null) {
				throw new ElementNotFoundException(
						paymentMethodCode.toString(), "PaymentMethod");
			}
		}

		billingAccountService.updateBillingAccount(billingAccount, null);

		entityManager.flush();

		log.debug(
				"[Meveo] [API] Update billing account: billing account {0} - account updated",
				code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.service.api.remote.ApiServiceRemote#updateUserAccount(java.
	 * lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, org.meveo.service.api.dto.AddressDTO, java.lang.String,
	 * java.lang.String)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateUserAccount(String code, String description,
			String firstName, String lastName, String titleCode,
			AddressDTO address, String extRef1, String extRef2)
			throws UnknownAccountException, ElementNotFoundException {

		// Uppercase code values, as Meveo stores them uppercase
		code = code.toUpperCase();

		log.debug(
				"[Meveo] [API] Update user account: code {0}, description {1}, firstName {2}, lastName {3}, titleCode {4}, address {5}, extRef1 {6}, extRef2 {7}",
				code, description, firstName, lastName, titleCode, address,
				extRef1, extRef2);

		UserAccount userAccount = userAccountService.findByCode(code);
		if (userAccount == null) {
			log.error(
					"[Meveo] [API] Update user account: billing account {0} - account was not found",
					code);
			throw new UnknownAccountException(code);
		}

		Title title = titleService.findByCode(userAccount.getProvider(),
				titleCode);
		if (title == null) {
			log.error(
					"[Meveo] [API] Update user account: user account {0} - title not found",
					code);
			throw new ElementNotFoundException(titleCode, "Title");
		}

		if (address != null) {
			userAccount.setAddress(convertToAddressEntity(address));
		}
		if (description != null) {
			userAccount.setDescription(description);
		}
		if (extRef1 != null) {
			userAccount.setExternalRef1(extRef1);
		}
		if (extRef2 != null) {
			userAccount.setExternalRef2(extRef2);
		}

		if (titleCode != null) {
			userAccount.getName().setTitle(title);
		}
		if (firstName != null) {
			userAccount.getName().setFirstName(firstName);
		}
		if (lastName != null) {
			userAccount.getName().setLastName(lastName);
		}

		userAccountService.update(userAccount);

		entityManager.flush();

		log.debug(
				"[Meveo] [API] Update user account: user account {0} - account updated",
				code);
	}

	/**
	 * Convert from DTO to entity object
	 * 
	 * @param addressDTO
	 *            Address DTO object
	 * @return Address entity object
	 */
	private Address convertToAddressEntity(AddressDTO addressDTO) {

		if (addressDTO == null) {
			return null;
		}

		return new Address(addressDTO.getAddress1(), addressDTO.getAddress2(),
				addressDTO.getAddress3(), addressDTO.getZipCode(),
				addressDTO.getCity(), addressDTO.getCountry(),
				addressDTO.getState());
	}

	/**
	 * Convert from entity to DTO object
	 * 
	 * @param address
	 *            Address entity object
	 * @return Address DTO object
	 */
	private AddressDTO convertToAddressDto(Address address) {

		return new AddressDTO(address.getAddress1(), address.getAddress2(),
				address.getAddress3(), address.getZipCode(), address.getCity(),
				address.getCountry(), address.getState());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.service.api.remote.ApiServiceRemote#getConsumption(java.lang
	 * .String, java.lang.String, java.lang.Integer, boolean)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ConsumptionDTO getConsumption(String subscriptionCode,
			String infoType, Integer billingCycle, boolean sumarizeConsumption)
			throws IncorrectSusbcriptionException {

		/*
		 * TODO: Component. RatedTransactionServiceLocal service =
		 * (RatedTransactionServiceLocal) Component
		 * .getInstance("ratedTransactionService");
		 */
		RatedTransactionServiceLocal service = null;
		return service.getConsumption(subscriptionCode, infoType, billingCycle,
				sumarizeConsumption);
	}
}