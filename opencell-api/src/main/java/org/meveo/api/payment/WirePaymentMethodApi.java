package org.meveo.api.payment;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.WirePaymentMethodDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.WirePaymentMethod;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentMethodService;

@Stateless
public class WirePaymentMethodApi extends BaseApi {

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private PaymentMethodService paymentMethodService;

	public Long create(WirePaymentMethodDto wirePaymentMethodDto) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
		if (StringUtils.isBlank(wirePaymentMethodDto.getCustomerAccountCode())) {
			missingParameters.add("customerAccountCode");
		}
		handleMissingParameters();
		CustomerAccount customerAccount = customerAccountService.findByCode(wirePaymentMethodDto.getCustomerAccountCode());
		if (customerAccount == null) {
			throw new EntityDoesNotExistsException(CustomerAccount.class, wirePaymentMethodDto.getCustomerAccountCode());
		}

		WirePaymentMethod paymentMethod = new WirePaymentMethod();
		paymentMethod.setCustomerAccount(customerAccount);
		paymentMethod.setAlias(wirePaymentMethodDto.getAlias());		
		paymentMethodService.create(paymentMethod);   
		return paymentMethod.getId();
	}

	public void update(WirePaymentMethodDto wirePaymentMethodDto) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
		if(StringUtils.isBlank(wirePaymentMethodDto.getId())){
			missingParameters.add("Id");	
		}
		handleMissingParameters();
		WirePaymentMethod wirePaymentMethod = null;
        wirePaymentMethod = (WirePaymentMethod) paymentMethodService.findById(wirePaymentMethodDto.getId());
        if(wirePaymentMethod == null){
        	throw new EntityDoesNotExistsException(WirePaymentMethod.class, wirePaymentMethodDto.getId());
        }
		
		if (wirePaymentMethodDto.isPreferred()) {
			wirePaymentMethod.setPreferred(true);
		}

		if (!StringUtils.isBlank(wirePaymentMethodDto.getAlias())) {
			wirePaymentMethod.setAlias(wirePaymentMethodDto.getAlias());
		}		
		paymentMethodService.update(wirePaymentMethod);
	}

	public void remove(Long id) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
		if (id == null ) {
			missingParameters.add("id");
		}
		handleMissingParameters();
		WirePaymentMethod wirePaymentMethod = null;
		if (id != null) {
			wirePaymentMethod = (WirePaymentMethod) paymentMethodService.findById(id);
		}		
		if (wirePaymentMethod == null) {
			throw new EntityDoesNotExistsException(WirePaymentMethod.class, id);
		}
		paymentMethodService.remove(wirePaymentMethod);
	}

	public List<WirePaymentMethodDto> list(Long customerAccountId, String customerAccountCode) throws MissingParameterException, EntityDoesNotExistsException {

		if (StringUtils.isBlank(customerAccountId) && StringUtils.isBlank(customerAccountCode)) {
			missingParameters.add("customerAccountId or customerAccountCode");
		}

		handleMissingParameters();

		CustomerAccount customerAccount = null;

		if (!StringUtils.isBlank(customerAccountId)) {
			customerAccount = customerAccountService.findById(customerAccountId);
		}
		if (!StringUtils.isBlank(customerAccountCode)) {
			customerAccount = customerAccountService.findByCode(customerAccountCode);
		}

		if (customerAccount == null) {
			throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountId == null ? customerAccountCode : "" + customerAccountId);
		}

		List<WirePaymentMethodDto> wirePaymentMethodDtos = new ArrayList<WirePaymentMethodDto>();

		for (WirePaymentMethod paymentMethod : customerAccount.getWirePaymentMethods()) {
			wirePaymentMethodDtos.add((WirePaymentMethodDto) PaymentMethodDto.toDto(paymentMethod));
		}

		return wirePaymentMethodDtos;
	}

	public WirePaymentMethodDto find(Long id) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {

		if (id == null) {
			missingParameters.add("id");
		}

		handleMissingParameters();

		WirePaymentMethod wirePaymentMethod = null;
		if (id != null) {
			wirePaymentMethod = (WirePaymentMethod) paymentMethodService.findById(id);
		}
		if (wirePaymentMethod == null) {
			throw new EntityDoesNotExistsException(CardPaymentMethod.class, id);
		}

		WirePaymentMethodDto wirePaymentMethodDto = (WirePaymentMethodDto) PaymentMethodDto.toDto(wirePaymentMethod);

		return wirePaymentMethodDto;
	}
}