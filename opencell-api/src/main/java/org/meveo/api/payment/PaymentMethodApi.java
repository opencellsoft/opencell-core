package org.meveo.api.payment;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentMethodService;

@Stateless
public class PaymentMethodApi extends BaseApi {

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private PaymentMethodService paymentMethodService;

	public Long create(PaymentMethodDto paymentMethodDto) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {

		if (paymentMethodDto.getBankCoordinates() != null) {
			if(StringUtils.isBlank(paymentMethodDto.getBankCoordinates().getAccountNumber())){
				missingParameters.add("AccountNumber");	
			}
			if(StringUtils.isBlank(paymentMethodDto.getBankCoordinates().getAccountOwner())){
				missingParameters.add("AccountOwner");	
			}
			if(StringUtils.isBlank(paymentMethodDto.getBankCoordinates().getBankCode())){
				missingParameters.add("BankCode");	
			}   
			if(StringUtils.isBlank(paymentMethodDto.getBankCoordinates().getBankName())){
				missingParameters.add("BankName");	
			}  
			if(StringUtils.isBlank(paymentMethodDto.getBankCoordinates().getIban())){
				missingParameters.add("Iban");	
			}        	
		}else{
			if(StringUtils.isBlank(paymentMethodDto.getMandateIdentification())){
				missingParameters.add("MandateIdentification");	
			}
			if(StringUtils.isBlank(paymentMethodDto.getMandateDate())){
				missingParameters.add("MandateDate");	
			}
		}

		if (StringUtils.isBlank(paymentMethodDto.getCustomerAccountCode())) {
			missingParameters.add("customerAccountCode");
		}
		handleMissingParameters();
		CustomerAccount customerAccount = customerAccountService.findByCode(paymentMethodDto.getCustomerAccountCode());
		if (customerAccount == null) {
			throw new EntityDoesNotExistsException(CustomerAccount.class, paymentMethodDto.getCustomerAccountCode());
		}

		PaymentMethod paymentMethod = paymentMethodDto.fromDto(customerAccount);
		paymentMethodService.create(paymentMethod);   
		return paymentMethod.getId();
	}

	public void update(PaymentMethodDto paymentMethodDto) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
		if(StringUtils.isBlank(paymentMethodDto.getId())){
			missingParameters.add("Id");	
		}
		handleMissingParameters();
		PaymentMethod paymentMethod = null;
		paymentMethod =  paymentMethodService.findById(paymentMethodDto.getId());
		if(paymentMethod == null){
			throw new EntityDoesNotExistsException(PaymentMethod.class, paymentMethodDto.getId());
		}

		paymentMethodService.update(paymentMethodDto.updateFromDto(paymentMethod));




	}

	public void remove(Long id) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
		if (id == null ) {
			missingParameters.add("id");
		}
		handleMissingParameters();
		DDPaymentMethod ddPaymentMethod = null;
		if (id != null) {
			ddPaymentMethod = (DDPaymentMethod) paymentMethodService.findById(id);
		}		
		if (ddPaymentMethod == null) {
			throw new EntityDoesNotExistsException(DDPaymentMethod.class, id);
		}
		paymentMethodService.remove(ddPaymentMethod);
	}

	public List<PaymentMethodDto> list(Long customerAccountId, String customerAccountCode) throws MissingParameterException, EntityDoesNotExistsException {

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

		List<PaymentMethodDto> paymentMethodDtos = new ArrayList<PaymentMethodDto>();

		for (DDPaymentMethod paymentMethod : customerAccount.getDDPaymentMethods()) {
			paymentMethodDtos.add(new PaymentMethodDto(paymentMethod));
		}

		return paymentMethodDtos;
	}

	public PaymentMethodDto find(Long id) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
		if (id == null) {
			missingParameters.add("id");
		}
		handleMissingParameters();
		PaymentMethodDto paymentMethodDto = null;
		try{
			paymentMethodDto =  new PaymentMethodDto(paymentMethodService.findById(id)) ;
			return paymentMethodDto;
		}catch (Exception e) {
			throw new EntityDoesNotExistsException(CardPaymentMethod.class, id);
		}
	}
}