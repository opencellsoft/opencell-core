package org.meveo.api.payment;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.DDPaymentMethodDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentMethodService;

@Stateless
public class DDPaymentMethodApi extends BaseApi {

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private PaymentMethodService paymentMethodService;

	public Long create(DDPaymentMethodDto ddPaymentMethodDto) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {

		if (ddPaymentMethodDto.getBankCoordinates() != null) {
			if(StringUtils.isBlank(ddPaymentMethodDto.getBankCoordinates().getAccountNumber())){
				missingParameters.add("AccountNumber");	
			}
			if(StringUtils.isBlank(ddPaymentMethodDto.getBankCoordinates().getAccountOwner())){
				missingParameters.add("AccountOwner");	
			}
			if(StringUtils.isBlank(ddPaymentMethodDto.getBankCoordinates().getBankCode())){
				missingParameters.add("BankCode");	
			}   
			if(StringUtils.isBlank(ddPaymentMethodDto.getBankCoordinates().getBankName())){
				missingParameters.add("BankName");	
			}  
			if(StringUtils.isBlank(ddPaymentMethodDto.getBankCoordinates().getIban())){
				missingParameters.add("Iban");	
			}        	
		}else{
			if(StringUtils.isBlank(ddPaymentMethodDto.getMandateIdentification())){
				missingParameters.add("MandateIdentification");	
			}
			if(StringUtils.isBlank(ddPaymentMethodDto.getMandateDate())){
				missingParameters.add("MandateDate");	
			}
		}

		if (StringUtils.isBlank(ddPaymentMethodDto.getCustomerAccountCode())) {
			missingParameters.add("customerAccountCode");
		}
		handleMissingParameters();
		CustomerAccount customerAccount = customerAccountService.findByCode(ddPaymentMethodDto.getCustomerAccountCode());
		if (customerAccount == null) {
			throw new EntityDoesNotExistsException(CustomerAccount.class, ddPaymentMethodDto.getCustomerAccountCode());
		}

		DDPaymentMethod paymentMethod = new DDPaymentMethod();
		paymentMethod.setCustomerAccount(customerAccount);
		paymentMethod.setAlias(ddPaymentMethodDto.getAlias());
		paymentMethod.setMandateDate(ddPaymentMethodDto.getMandateDate());
		paymentMethod.setMandateIdentification(ddPaymentMethodDto.getMandateIdentification());       
		if(ddPaymentMethodDto.getBankCoordinates() != null){        	
			paymentMethod.setBankCoordinates(ddPaymentMethodDto.getBankCoordinates().fromDto());
		}
		paymentMethodService.create(paymentMethod);   
		return paymentMethod.getId();
	}

	public void update(DDPaymentMethodDto ddPaymentMethodDto) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
		if(StringUtils.isBlank(ddPaymentMethodDto.getId())){
			missingParameters.add("Id");	
		}
		handleMissingParameters();
		DDPaymentMethod ddPaymentMethod = null;
        ddPaymentMethod = (DDPaymentMethod) paymentMethodService.findById(ddPaymentMethodDto.getId());
        if(ddPaymentMethod == null){
        	throw new EntityDoesNotExistsException(DDPaymentMethod.class, ddPaymentMethodDto.getId());
        }
		
		if (ddPaymentMethodDto.isPreferred()) {
			ddPaymentMethod.setPreferred(true);
		}

		if (!StringUtils.isBlank(ddPaymentMethodDto.getAlias())) {
			ddPaymentMethod.setAlias(ddPaymentMethodDto.getAlias());
		}
		if (!StringUtils.isBlank(ddPaymentMethodDto.getMandateIdentification())) {
			ddPaymentMethod.setMandateIdentification(ddPaymentMethodDto.getMandateIdentification());
		}
		if (!StringUtils.isBlank(ddPaymentMethodDto.getMandateDate())) {
			ddPaymentMethod.setMandateDate(ddPaymentMethodDto.getMandateDate());
		}
		if(ddPaymentMethodDto.getBankCoordinates() != null){
			if (!StringUtils.isBlank(ddPaymentMethodDto.getBankCoordinates().getAccountNumber())) {
				ddPaymentMethod.getBankCoordinates().setAccountNumber(ddPaymentMethodDto.getBankCoordinates().getAccountNumber());
			}
			if (!StringUtils.isBlank(ddPaymentMethodDto.getBankCoordinates().getAccountOwner())) {
				ddPaymentMethod.getBankCoordinates().setAccountNumber(ddPaymentMethodDto.getBankCoordinates().getAccountOwner());
			}	
			if (!StringUtils.isBlank(ddPaymentMethodDto.getBankCoordinates().getBankCode())) {
				ddPaymentMethod.getBankCoordinates().setAccountNumber(ddPaymentMethodDto.getBankCoordinates().getBankCode());
			}
			if (!StringUtils.isBlank(ddPaymentMethodDto.getBankCoordinates().getBankId())) {
				ddPaymentMethod.getBankCoordinates().setAccountNumber(ddPaymentMethodDto.getBankCoordinates().getBankId());
			}
			if (!StringUtils.isBlank(ddPaymentMethodDto.getBankCoordinates().getBankName())) {
				ddPaymentMethod.getBankCoordinates().setAccountNumber(ddPaymentMethodDto.getBankCoordinates().getBankName());
			}
			if (!StringUtils.isBlank(ddPaymentMethodDto.getBankCoordinates().getBic())) {
				ddPaymentMethod.getBankCoordinates().setAccountNumber(ddPaymentMethodDto.getBankCoordinates().getBic());
			}			
			if (!StringUtils.isBlank(ddPaymentMethodDto.getBankCoordinates().getBranchCode())) {
				ddPaymentMethod.getBankCoordinates().setAccountNumber(ddPaymentMethodDto.getBankCoordinates().getBranchCode());
			}	
			if (!StringUtils.isBlank(ddPaymentMethodDto.getBankCoordinates().getIban())) {
				ddPaymentMethod.getBankCoordinates().setAccountNumber(ddPaymentMethodDto.getBankCoordinates().getIban());
			}	
			if (!StringUtils.isBlank(ddPaymentMethodDto.getBankCoordinates().getIcs())) {
				ddPaymentMethod.getBankCoordinates().setAccountNumber(ddPaymentMethodDto.getBankCoordinates().getIcs());
			}	
			if (!StringUtils.isBlank(ddPaymentMethodDto.getBankCoordinates().getIssuerName())) {
				ddPaymentMethod.getBankCoordinates().setAccountNumber(ddPaymentMethodDto.getBankCoordinates().getIssuerName());
			}	
			if (!StringUtils.isBlank(ddPaymentMethodDto.getBankCoordinates().getIssuerNumber())) {
				ddPaymentMethod.getBankCoordinates().setAccountNumber(ddPaymentMethodDto.getBankCoordinates().getIssuerNumber());
			}
			if (!StringUtils.isBlank(ddPaymentMethodDto.getBankCoordinates().getKey())) {
				ddPaymentMethod.getBankCoordinates().setAccountNumber(ddPaymentMethodDto.getBankCoordinates().getKey());
			}		
		}
		paymentMethodService.update(ddPaymentMethod);
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

	public List<DDPaymentMethodDto> list(Long customerAccountId, String customerAccountCode) throws MissingParameterException, EntityDoesNotExistsException {

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

		List<DDPaymentMethodDto> ddPaymentMethodDtos = new ArrayList<DDPaymentMethodDto>();

		for (DDPaymentMethod paymentMethod : customerAccount.getDDPaymentMethods()) {
			ddPaymentMethodDtos.add((DDPaymentMethodDto) PaymentMethodDto.toDto(paymentMethod));
		}

		return ddPaymentMethodDtos;
	}

	public DDPaymentMethodDto find(Long id) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {

		if (id == null) {
			missingParameters.add("id");
		}

		handleMissingParameters();

		DDPaymentMethod ddPaymentMethod = null;
		if (id != null) {
			ddPaymentMethod = (DDPaymentMethod) paymentMethodService.findById(id);
		}
		if (ddPaymentMethod == null) {
			throw new EntityDoesNotExistsException(CardPaymentMethod.class, id);
		}

		DDPaymentMethodDto ddPaymentMethodDto = (DDPaymentMethodDto) PaymentMethodDto.toDto(ddPaymentMethod);

		return ddPaymentMethodDto;
	}
}