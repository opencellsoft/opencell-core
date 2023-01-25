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

package org.meveo.service.payments.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.payment.HostedCheckoutInput;
import org.meveo.api.dto.payment.HostedCheckoutStatusResponseDto;
import org.meveo.api.dto.payment.MandatInfoDto;
import org.meveo.api.dto.payment.PaymentHostedCheckoutResponseDto;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Invoice;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.util.PaymentGatewayClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerCreateParams.Address;

/**
 * The Class StripeGatewayPayment, Stripe SDK 22.4.0, and Stripe API Version
 * [2022-11-15] ].
 *
 * @author anasseh
 * @lastModifiedVersion 13.X
 */
@PaymentGatewayClass
public class StripeGatewayPayment implements GatewayPaymentInterface {

	/** The log. */
	protected Logger log = LoggerFactory.getLogger(StripeGatewayPayment.class);

	/** The payment gateway. */
	private PaymentGateway paymentGateway = null;

	private CustomerAccountService customerAccountService = null;

	private ParamBean paramBean() {
		ParamBeanFactory paramBeanFactory = (ParamBeanFactory) EjbUtils
				.getServiceInterface(ParamBeanFactory.class.getSimpleName());
		return paramBeanFactory.getInstance();		
	}

	private CustomerAccountService getCustomerAccountService() {
		if (customerAccountService != null) {
			return customerAccountService;
		}
		customerAccountService = (CustomerAccountService) EjbUtils
				.getServiceInterface(CustomerAccountService.class.getSimpleName());
		return customerAccountService;
	}

	/**
	 * Gets the client object
	 *
	 * @return the client object
	 */
	@Override
	public Object getClientObject() {

		return paymentGateway.getSecretKey();
	}

	@Override
	public String createCardToken(CustomerAccount customerAccount, String alias, String cardNumber,
			String cardHolderName, String expirayDate, String issueNumber, CreditCardTypeEnum cardType)
			throws BusinessException {

		throw new UnsupportedOperationException();
	}

	@Override
	public String createSepaDirectDebitToken(CustomerAccount customerAccount, String alias, String accountHolderName,
			String iban) throws BusinessException {

		throw new UnsupportedOperationException();
	}

	@Override
	public void createMandate(CustomerAccount customerAccount, String iban, String mandateReference)
			throws BusinessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void approveSepaDDMandate(String token, Date signatureDate) throws BusinessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public PaymentResponseDto doPaymentToken(CardPaymentMethod paymentCardToken, Long ctsAmount,
			Map<String, Object> additionalParams) throws BusinessException {

		return doPayment(paymentCardToken, null, ctsAmount,
				paymentCardToken.getCustomerAccount().getTradingCurrency().getCurrencyCode().toLowerCase(),
				paymentCardToken.getTokenId(), paymentCardToken.getCustomerAccount().getExternalRef2());

	}

	private PaymentResponseDto doPayment(CardPaymentMethod paymentCardToken, DDPaymentMethod ddPaymentMethod,
			Long ctsAmount, String currencyCode, String tokenId, String stripeCustomerId) throws BusinessException {
		PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
		paymentResponseDto.setPaymentStatus(PaymentStatusEnum.NOT_PROCESSED);
		try {
			setKey(paymentGateway.getSecretKey());

			List<Object> paymentMethodTypes = new ArrayList<>();
			if (paymentCardToken != null) {
				paymentMethodTypes.add("card");
			}
			if (ddPaymentMethod != null) {
				paymentMethodTypes.add("sepa_debit");
			}
			Map<String, Object> params = new HashMap<>();
			params.put("amount", ctsAmount);
			params.put("currency", currencyCode);
			params.put("payment_method_types", paymentMethodTypes);
			params.put("payment_method", tokenId);
			params.put("customer", stripeCustomerId);

			params.put("confirm", paramBean().getProperty("stripe.paymentToken.confirm", "true"));
			params.put("off_session", paramBean().getProperty("stripe.paymentToken.offSession", "true"));

			PaymentIntent paymentIntent = PaymentIntent.create(params);
			if(paymentIntent == null) {
				throw new BusinessException("paymentIntent created is null");
			}
			String paymentIntentResponseJson  = paymentIntent.toJson();
			
		    log.info("PaymentIntent  created :{}",paymentIntentResponseJson );
						
			paymentResponseDto.setPaymentID(paymentIntent.getId());
			paymentResponseDto.setPaymentStatus(mappingStaus(paymentIntent.getStatus()));
			return paymentResponseDto;
		} catch (Exception e) {
			paymentResponseDto.setErrorMessage(e.getMessage());
			paymentResponseDto.setErrorCode(e.getMessage());
			paymentResponseDto.setPaymentStatus(PaymentStatusEnum.ERROR);
			log.error("Error during  paymentIntent creation", e);
		}
		return paymentResponseDto;
	}

	@Override
	public PaymentResponseDto doPaymentCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber,
			String ownerName, String cvv, String expirayDate, CreditCardTypeEnum cardType, String countryCode,
			Map<String, Object> additionalParams) throws BusinessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public PaymentResponseDto doPaymentSepa(DDPaymentMethod ddPaymentMethod, Long ctsAmount,
			Map<String, Object> additionalParams) throws BusinessException {

		return doPayment(null, ddPaymentMethod, ctsAmount,
				ddPaymentMethod.getCustomerAccount().getTradingCurrency().getCurrencyCode().toLowerCase(),
				ddPaymentMethod.getTokenId(), ddPaymentMethod.getCustomerAccount().getExternalRef2());
	}

	@Override
	public void cancelPayment(String paymentID) throws BusinessException {
		try {
			setKey(paymentGateway.getSecretKey());

			PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentID);
			if (paymentIntent == null) {
				throw new BusinessException("Cant find payment by id:" + paymentID);
			}
			PaymentIntent updatedPaymentIntent = paymentIntent.cancel();
			if(updatedPaymentIntent == null) {
				throw new BusinessException("updatedPaymentIntent is null");
			}
			String updatedPaymentIntentJson = updatedPaymentIntent.toJson();
			log.info("updatedPaymentIntent:{}", updatedPaymentIntentJson );

		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
	}

	@Override
	public PaymentResponseDto checkPayment(String paymentID, PaymentMethodEnum paymentMethodType)
			throws BusinessException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the billing address.
	 *
	 * @param customerAccount the customer account
	 * @return the billing address
	 */
	private Address getBillingAddress(CustomerAccount customerAccount) throws BusinessException {
		if (customerAccount.getAddress() == null || customerAccount.getAddress().getCountry() == null) {
			throw new BusinessException("Invalid Address");
		}
		return Address.builder().setLine1(customerAccount.getAddress().getAddress1())
				.setLine2(customerAccount.getAddress().getAddress2()).setCity(customerAccount.getAddress().getCity())
				.setPostalCode(customerAccount.getAddress().getZipCode())
				.setCountry(customerAccount.getAddress().getCountry().getCountryCode()).build();
	}

	/**
	 * Mapping status.
	 *
	 * @param stripePaymentStatus the Stripe payment status
	 * @return the payment status enum
	 */
	private PaymentStatusEnum mappingStaus(String stripePaymentStatus) {
		if (stripePaymentStatus == null) {
			return PaymentStatusEnum.ERROR;
		}
		if ("succeeded".equalsIgnoreCase(stripePaymentStatus)) {
			return PaymentStatusEnum.ACCEPTED;
		}
		if (stripePaymentStatus.equals("processing")) {
			return PaymentStatusEnum.PENDING;
		}
		if (stripePaymentStatus.equals("requires_capture")) {
			return PaymentStatusEnum.PENDING;
		}
		if (stripePaymentStatus.equals("requires_payment_method")) {
			return PaymentStatusEnum.REJECTED;
		}

		return PaymentStatusEnum.REJECTED;
	}

	@Override
	public PaymentResponseDto doRefundToken(CardPaymentMethod paymentToken, Long ctsAmount,
			Map<String, Object> additionalParams) throws BusinessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public PaymentResponseDto doRefundCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber,
			String ownerName, String cvv, String expirayDate, CreditCardTypeEnum cardType, String countryCode,
			Map<String, Object> additionalParams) throws BusinessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void doBulkPaymentAsService(DDRequestLOT ddRequestLot) throws BusinessException {
		throw new UnsupportedOperationException();

	}

	@Override
	public MandatInfoDto checkMandat(String mandatReference, String mandateId) throws BusinessException {
		throw new UnsupportedOperationException();

	}

	@Override
	public PaymentResponseDto doRefundSepa(DDPaymentMethod paymentToken, Long ctsAmount,
			Map<String, Object> additionalParams) throws BusinessException {
		throw new UnsupportedOperationException();
	}

	private Customer createCustomer(CustomerAccount customerAccount) throws BusinessException {
		try {
			if (customerAccount.getName() == null) {
				throw new BusinessException("Invalid customer name");
			}
			if (StringUtils.isBlank(customerAccount.getContactInformationNullSafe().getEmail())) {
				throw new BusinessException("Invalid customer email");
			}
			setKey(paymentGateway.getSecretKey());
			Customer stripeCustomer = null;
			
			String phone = customerAccount.getContactInformationNullSafe().getMobile();
			if(StringUtils.isBlank(phone)) {
				phone = customerAccount.getContactInformationNullSafe().getPhone();
			}
			
			CustomerCreateParams params = CustomerCreateParams.builder().setAddress(getBillingAddress(customerAccount))
					.setName(customerAccount.getName().getFullName()).setDescription(customerAccount.getCode())
					.setPhone(phone).build();

			stripeCustomer = Customer.create(params);
			if(stripeCustomer == null) {
				throw new BusinessException("stripeCustomer reponse is null");
			}
			String stripeCustomerJson  = stripeCustomer.toJson();
			log.info("Customer stripe created :{}",stripeCustomerJson );
			return stripeCustomer;
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}

	}

	private static synchronized void setKey(String key) {
		Stripe.apiKey = key;
	}
	@Override
	public PaymentHostedCheckoutResponseDto getHostedCheckoutUrl(HostedCheckoutInput hostedCheckoutInput)
			throws BusinessException {
		try {
			setKey(paymentGateway.getSecretKey());
			CustomerAccount customerAccount = getCustomerAccountService()
					.findByCode(hostedCheckoutInput.getCustomerAccountCode());

			if (customerAccount == null) {
				throw new BusinessException(
						"Cant find customerAccount by code:" + hostedCheckoutInput.getCustomerAccountCode());
			}
			
			Customer stripeCustomer = null;
			if (!StringUtils.isBlank(customerAccount.getExternalRef2())) {
				stripeCustomer = Customer.retrieve(customerAccount.getExternalRef2());
			}

			if (stripeCustomer == null) {
				stripeCustomer = createCustomer(customerAccount);				
				customerAccount.setExternalRef2(stripeCustomer.getId());
				customerAccountService.update(customerAccount);
			}
			String[] pmTypes = { "card" };
			if (PaymentMethodEnum.DIRECTDEBIT == hostedCheckoutInput.getPaymentMethodType()) {
				pmTypes[0] = "sepa_debit";
			}

			Map<String, Object> params = new HashMap<>();
			params.put("success_url", hostedCheckoutInput.getReturnUrl());
			params.put("cancel_url", hostedCheckoutInput.getCancelUrl());
			params.put("customer", stripeCustomer.getId());
			params.put("mode", "setup");
			params.put("payment_method_types", pmTypes);
			params.put("currency", hostedCheckoutInput.getCurrencyCode());

			Session session = Session.create(params);
			
			if(session == null) {
				throw new BusinessException("session created is null");
			} 
			String sessionJson = session.toJson();
			log.info("session:{}",sessionJson);
			
			return new PaymentHostedCheckoutResponseDto(session.getUrl(), null, null, session.getId());
		} catch (Exception e) {
			throw new BusinessException("Error on getHostedCheckoutUrl:"+e.getMessage());
		}
	}

	@Override
	public void setPaymentGateway(PaymentGateway paymentGateway) {
		this.paymentGateway = paymentGateway;
	}

	@Override
	public String createInvoice(Invoice invoice) throws BusinessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public HostedCheckoutStatusResponseDto getHostedCheckoutStatus(String id) throws BusinessException {
		try {

			setKey(paymentGateway.getSecretKey());
			Session session = Session.retrieve(id);
			HostedCheckoutStatusResponseDto hostedCheckoutStatusResponseDto = new HostedCheckoutStatusResponseDto();
			hostedCheckoutStatusResponseDto.setHostedCheckoutStatus(session.getStatus());

			hostedCheckoutStatusResponseDto.setPaymentId(session.getPaymentIntent());
			hostedCheckoutStatusResponseDto.setPaymentStatus(mappingStaus(session.getPaymentStatus()));

			return hostedCheckoutStatusResponseDto;
		} catch (Exception e) {
			throw new BusinessException("Error on getHostedCheckoutStatus:"+e.getMessage());
		}
	}
}