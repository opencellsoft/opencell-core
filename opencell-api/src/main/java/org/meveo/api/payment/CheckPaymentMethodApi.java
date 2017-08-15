package org.meveo.api.payment;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.CheckPaymentMethodDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CheckPaymentMethod;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentMethodService;

@Stateless
public class CheckPaymentMethodApi extends BaseApi {

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private PaymentMethodService paymentMethodService;

	public Long create(CheckPaymentMethodDto checkPaymentMethodDto) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
		if (StringUtils.isBlank(checkPaymentMethodDto.getCustomerAccountCode())) {
			missingParameters.add("customerAccountCode");
		}
		handleMissingParameters();
		CustomerAccount customerAccount = customerAccountService.findByCode(checkPaymentMethodDto.getCustomerAccountCode());
		if (customerAccount == null) {
			throw new EntityDoesNotExistsException(CustomerAccount.class, checkPaymentMethodDto.getCustomerAccountCode());
		}

		CheckPaymentMethod paymentMethod = new CheckPaymentMethod();
		paymentMethod.setCustomerAccount(customerAccount);
		paymentMethod.setAlias(checkPaymentMethodDto.getAlias());		
		paymentMethodService.create(paymentMethod);   
		return paymentMethod.getId();
	}

	public void update(CheckPaymentMethodDto checkPaymentMethodDto) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
		if(StringUtils.isBlank(checkPaymentMethodDto.getId())){
			missingParameters.add("Id");	
		}
		handleMissingParameters();
		CheckPaymentMethod checkPaymentMethod = null;
        checkPaymentMethod = (CheckPaymentMethod) paymentMethodService.findById(checkPaymentMethodDto.getId());
        if(checkPaymentMethod == null){
        	throw new EntityDoesNotExistsException(CheckPaymentMethod.class, checkPaymentMethodDto.getId());
        }
		
		if (checkPaymentMethodDto.isPreferred()) {
			checkPaymentMethod.setPreferred(true);
		}

		if (!StringUtils.isBlank(checkPaymentMethodDto.getAlias())) {
			checkPaymentMethod.setAlias(checkPaymentMethodDto.getAlias());
		}		
		paymentMethodService.update(checkPaymentMethod);
	}

	public void remove(Long id) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
		if (id == null ) {
			missingParameters.add("id");
		}
		handleMissingParameters();
		CheckPaymentMethod checkPaymentMethod = null;
		if (id != null) {
			checkPaymentMethod = (CheckPaymentMethod) paymentMethodService.findById(id);
		}		
		if (checkPaymentMethod == null) {
			throw new EntityDoesNotExistsException(CheckPaymentMethod.class, id);
		}
		paymentMethodService.remove(checkPaymentMethod);
	}

	public List<CheckPaymentMethodDto> list(Long customerAccountId, String customerAccountCode) throws MissingParameterException, EntityDoesNotExistsException {

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

		List<CheckPaymentMethodDto> checkPaymentMethodDtos = new ArrayList<CheckPaymentMethodDto>();

		for (CheckPaymentMethod paymentMethod : customerAccount.getCheckPaymentMethods()) {
			checkPaymentMethodDtos.add((CheckPaymentMethodDto) PaymentMethodDto.toDto(paymentMethod));
		}

		return checkPaymentMethodDtos;
	}

	public CheckPaymentMethodDto find(Long id) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {

		if (id == null) {
			missingParameters.add("id");
		}

		handleMissingParameters();

		CheckPaymentMethod checkPaymentMethod = null;
		if (id != null) {
			checkPaymentMethod = (CheckPaymentMethod) paymentMethodService.findById(id);
		}
		if (checkPaymentMethod == null) {
			throw new EntityDoesNotExistsException(CardPaymentMethod.class, id);
		}

		CheckPaymentMethodDto checkPaymentMethodDto = (CheckPaymentMethodDto) PaymentMethodDto.toDto(checkPaymentMethod);

		return checkPaymentMethodDto;
	}
}