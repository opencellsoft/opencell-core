package org.meveo.api.payment;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.dto.payment.TipPaymentMethodDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.TipPaymentMethod;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentMethodService;

@Stateless
public class TipPaymentMethodApi extends BaseApi {

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private PaymentMethodService paymentMethodService;

	public Long create(TipPaymentMethodDto tipPaymentMethodDto) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {

		if (tipPaymentMethodDto.getBankCoordinates() != null) {
			if(StringUtils.isBlank(tipPaymentMethodDto.getBankCoordinates().getAccountNumber())){
				missingParameters.add("AccountNumber");	
			}
			if(StringUtils.isBlank(tipPaymentMethodDto.getBankCoordinates().getAccountOwner())){
				missingParameters.add("AccountOwner");	
			}
			if(StringUtils.isBlank(tipPaymentMethodDto.getBankCoordinates().getBankCode())){
				missingParameters.add("BankCode");	
			}   
			if(StringUtils.isBlank(tipPaymentMethodDto.getBankCoordinates().getBankName())){
				missingParameters.add("BankName");	
			}  
			if(StringUtils.isBlank(tipPaymentMethodDto.getBankCoordinates().getIban())){
				missingParameters.add("Iban");	
			}        	
		}

		if (StringUtils.isBlank(tipPaymentMethodDto.getCustomerAccountCode())) {
			missingParameters.add("customerAccountCode");
		}
		handleMissingParameters();
		CustomerAccount customerAccount = customerAccountService.findByCode(tipPaymentMethodDto.getCustomerAccountCode());
		if (customerAccount == null) {
			throw new EntityDoesNotExistsException(CustomerAccount.class, tipPaymentMethodDto.getCustomerAccountCode());
		}

		TipPaymentMethod paymentMethod = new TipPaymentMethod();
		paymentMethod.setCustomerAccount(customerAccount);
		paymentMethod.setAlias(tipPaymentMethodDto.getAlias());      
		if(tipPaymentMethodDto.getBankCoordinates() != null){        	
			paymentMethod.setBankCoordinates(tipPaymentMethodDto.getBankCoordinates().fromDto());
		}
		paymentMethodService.create(paymentMethod);   
		return paymentMethod.getId();
	}

	public void update(TipPaymentMethodDto tipPaymentMethodDto) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
		if(StringUtils.isBlank(tipPaymentMethodDto.getId())){
			missingParameters.add("Id");	
		}
		handleMissingParameters();
		TipPaymentMethod tipPaymentMethod = null;
        tipPaymentMethod = (TipPaymentMethod) paymentMethodService.findById(tipPaymentMethodDto.getId());
        if(tipPaymentMethod == null){
        	throw new EntityDoesNotExistsException(DDPaymentMethod.class, tipPaymentMethodDto.getId());
        }
		
		if (tipPaymentMethodDto.isPreferred()) {
			tipPaymentMethod.setPreferred(true);
		}

		if (!StringUtils.isBlank(tipPaymentMethodDto.getAlias())) {
			tipPaymentMethod.setAlias(tipPaymentMethodDto.getAlias());
		}
		if(tipPaymentMethodDto.getBankCoordinates() != null){
			if (!StringUtils.isBlank(tipPaymentMethodDto.getBankCoordinates().getAccountNumber())) {
				tipPaymentMethod.getBankCoordinates().setAccountNumber(tipPaymentMethodDto.getBankCoordinates().getAccountNumber());
			}
			if (!StringUtils.isBlank(tipPaymentMethodDto.getBankCoordinates().getAccountOwner())) {
				tipPaymentMethod.getBankCoordinates().setAccountNumber(tipPaymentMethodDto.getBankCoordinates().getAccountOwner());
			}	
			if (!StringUtils.isBlank(tipPaymentMethodDto.getBankCoordinates().getBankCode())) {
				tipPaymentMethod.getBankCoordinates().setAccountNumber(tipPaymentMethodDto.getBankCoordinates().getBankCode());
			}
			if (!StringUtils.isBlank(tipPaymentMethodDto.getBankCoordinates().getBankId())) {
				tipPaymentMethod.getBankCoordinates().setAccountNumber(tipPaymentMethodDto.getBankCoordinates().getBankId());
			}
			if (!StringUtils.isBlank(tipPaymentMethodDto.getBankCoordinates().getBankName())) {
				tipPaymentMethod.getBankCoordinates().setAccountNumber(tipPaymentMethodDto.getBankCoordinates().getBankName());
			}
			if (!StringUtils.isBlank(tipPaymentMethodDto.getBankCoordinates().getBic())) {
				tipPaymentMethod.getBankCoordinates().setAccountNumber(tipPaymentMethodDto.getBankCoordinates().getBic());
			}			
			if (!StringUtils.isBlank(tipPaymentMethodDto.getBankCoordinates().getBranchCode())) {
				tipPaymentMethod.getBankCoordinates().setAccountNumber(tipPaymentMethodDto.getBankCoordinates().getBranchCode());
			}	
			if (!StringUtils.isBlank(tipPaymentMethodDto.getBankCoordinates().getIban())) {
				tipPaymentMethod.getBankCoordinates().setAccountNumber(tipPaymentMethodDto.getBankCoordinates().getIban());
			}	
			if (!StringUtils.isBlank(tipPaymentMethodDto.getBankCoordinates().getIcs())) {
				tipPaymentMethod.getBankCoordinates().setAccountNumber(tipPaymentMethodDto.getBankCoordinates().getIcs());
			}	
			if (!StringUtils.isBlank(tipPaymentMethodDto.getBankCoordinates().getIssuerName())) {
				tipPaymentMethod.getBankCoordinates().setAccountNumber(tipPaymentMethodDto.getBankCoordinates().getIssuerName());
			}	
			if (!StringUtils.isBlank(tipPaymentMethodDto.getBankCoordinates().getIssuerNumber())) {
				tipPaymentMethod.getBankCoordinates().setAccountNumber(tipPaymentMethodDto.getBankCoordinates().getIssuerNumber());
			}
			if (!StringUtils.isBlank(tipPaymentMethodDto.getBankCoordinates().getKey())) {
				tipPaymentMethod.getBankCoordinates().setAccountNumber(tipPaymentMethodDto.getBankCoordinates().getKey());
			}		
		}
		paymentMethodService.update(tipPaymentMethod);
	}

	public void remove(Long id) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
		if (id == null ) {
			missingParameters.add("id");
		}
		handleMissingParameters();
		TipPaymentMethod tipPaymentMethod = null;
		if (id != null) {
			tipPaymentMethod = (TipPaymentMethod) paymentMethodService.findById(id);
		}		
		if (tipPaymentMethod == null) {
			throw new EntityDoesNotExistsException(DDPaymentMethod.class, id);
		}
		paymentMethodService.remove(tipPaymentMethod);
	}

	public List<TipPaymentMethodDto> list(Long customerAccountId, String customerAccountCode) throws MissingParameterException, EntityDoesNotExistsException {

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

		List<TipPaymentMethodDto> tipPaymentMethodDtos = new ArrayList<TipPaymentMethodDto>();

		for (TipPaymentMethod paymentMethod : customerAccount.getTipPaymentMethods()) {
			tipPaymentMethodDtos.add((TipPaymentMethodDto) PaymentMethodDto.toDto(paymentMethod));
		}

		return tipPaymentMethodDtos;
	}

	public TipPaymentMethodDto find(Long id) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {

		if (id == null) {
			missingParameters.add("id");
		}

		handleMissingParameters();

		TipPaymentMethod tipPaymentMethod = null;
		if (id != null) {
			tipPaymentMethod = (TipPaymentMethod) paymentMethodService.findById(id);
		}
		if (tipPaymentMethod == null) {
			throw new EntityDoesNotExistsException(CardPaymentMethod.class, id);
		}

		TipPaymentMethodDto tipPaymentMethodDto = (TipPaymentMethodDto) PaymentMethodDto.toDto(tipPaymentMethod);

		return tipPaymentMethodDto;
	}
}