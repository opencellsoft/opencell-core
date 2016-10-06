package org.meveo.api.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AutomatedPayment;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.AutomatedPaymentService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.MatchingAmountService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.RecordedInvoiceService;

@Stateless
public class PaymentApi extends BaseApi {

	@Inject
	AutomatedPaymentService automatedPaymentService;

	@Inject
	RecordedInvoiceService recordedInvoiceService;

	@Inject
	MatchingCodeService matchingCodeService;

	@Inject
	ProviderService providerService;

	@Inject
	CustomerAccountService customerAccountService;

	@Inject
	OCCTemplateService oCCTemplateService;
	
	@Inject
	MatchingAmountService matchingAmountService;
	
	

	public void createPayment(PaymentDto paymentDto, User currentUser) throws  MeveoApiException, BusinessException {
		log.info("create payment for amount:" + paymentDto.getAmount() + " paymentMethodEnum:" + paymentDto.getPaymentMethod() + " isToMatching:" + paymentDto.isToMatching() + "  customerAccount:" + paymentDto.getCustomerAccountCode() + "...");

		if (StringUtils.isBlank(paymentDto.getAmount())) {
			missingParameters.add("amount");
		}
		if (StringUtils.isBlank(paymentDto.getCustomerAccountCode())) {
			missingParameters.add("customerAccountCode");
		}
		if (StringUtils.isBlank(paymentDto.getOccTemplateCode())) {
			missingParameters.add("occTemplateCode");
		}
		if (StringUtils.isBlank(paymentDto.getReference())) {
			missingParameters.add("reference");
		}
		if (StringUtils.isBlank(paymentDto.getPaymentMethod())) {
			missingParameters.add("paymentMethod");
		}
		
		handleMissingParameters();
		
		Provider provider = currentUser.getProvider();
		CustomerAccount customerAccount = customerAccountService.findByCode(paymentDto.getCustomerAccountCode(), provider);
		if (customerAccount == null) {
			throw new BusinessException("Cannot find customer account with code=" + paymentDto.getCustomerAccountCode());
		}

		OCCTemplate occTemplate = oCCTemplateService.findByCode(paymentDto.getOccTemplateCode(), provider.getCode());
		if (occTemplate == null) {
			throw new BusinessException("Cannot find OCC Template with code=" + paymentDto.getOccTemplateCode());
		}

		AutomatedPayment automatedPayment = new AutomatedPayment();
		automatedPayment.setProvider(provider);
		automatedPayment.setPaymentMethod(paymentDto.getPaymentMethod());
		automatedPayment.setAmount(paymentDto.getAmount());
		automatedPayment.setUnMatchingAmount(paymentDto.getAmount());
		automatedPayment.setMatchingAmount(BigDecimal.ZERO);
		automatedPayment.setAccountCode(occTemplate.getAccountCode());
		automatedPayment.setOccCode(occTemplate.getCode());
		automatedPayment.setOccDescription(occTemplate.getDescription());
		automatedPayment.setTransactionCategory(occTemplate.getOccCategory());
		automatedPayment.setAccountCodeClientSide(occTemplate.getAccountCodeClientSide());
		automatedPayment.setCustomerAccount(customerAccount);
		automatedPayment.setReference(paymentDto.getReference());
		automatedPayment.setDueDate(paymentDto.getDueDate());
		automatedPayment.setTransactionDate(paymentDto.getTransactionDate());
		automatedPayment.setMatchingStatus(MatchingStatusEnum.O);
		automatedPaymentService.create(automatedPayment, currentUser);
		// populate customFields
        try {
            populateCustomFields(paymentDto.getCustomFields(), automatedPayment, true, currentUser); 
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
        
		int nbOccMatched = 0;
		if (paymentDto.isToMatching()) {
			MatchingCode matchingCode = new MatchingCode();
			BigDecimal amountToMatch = BigDecimal.ZERO;
			if(paymentDto.getListOCCReferenceforMatching() !=null){
				nbOccMatched = paymentDto.getListOCCReferenceforMatching().size();
				for (int i = 0; i < nbOccMatched; i++) {
					RecordedInvoice accountOperation = recordedInvoiceService.getRecordedInvoice(paymentDto.getListOCCReferenceforMatching().get(i), provider);
					amountToMatch = accountOperation.getUnMatchingAmount();
					accountOperation.setMatchingAmount(accountOperation.getMatchingAmount().add(amountToMatch));
					accountOperation.setUnMatchingAmount(accountOperation.getUnMatchingAmount().subtract(amountToMatch));
					accountOperation.setMatchingStatus(MatchingStatusEnum.L);
					recordedInvoiceService.update(accountOperation, currentUser);
					MatchingAmount matchingAmount = new MatchingAmount();
					matchingAmount.setProvider(accountOperation.getProvider());
					matchingAmount.setAccountOperation(accountOperation);
					matchingAmount.setMatchingCode(matchingCode);
					matchingAmount.setMatchingAmount(amountToMatch);
					matchingAmountService.create(matchingAmount, currentUser);
					accountOperation.getMatchingAmounts().add(matchingAmount);
					matchingCode.getMatchingAmounts().add(matchingAmount);
					
				}
			}

			matchingCode.setMatchingAmountDebit(paymentDto.getAmount());
			matchingCode.setMatchingAmountCredit(paymentDto.getAmount());
			matchingCode.setMatchingDate(new Date());
			matchingCode.setMatchingType(MatchingTypeEnum.A);
			matchingCode.setProvider(provider);
			matchingCodeService.create(matchingCode, currentUser);
			log.info("matching created  for 1 automatedPayment and " + (nbOccMatched - 1) + " occ");
		} else {
			log.info("no matching created ");
		}
		log.info("automatedPayment created for amount:" + automatedPayment.getAmount());
	}

	public List<PaymentDto> getPaymentList(String customerAccountCode, User currentUser) throws Exception {
		List<PaymentDto> result = new ArrayList<PaymentDto>();

		if (currentUser == null) {
			throw new BusinessException("currentUser is empty");
		}
		Provider provider = currentUser.getProvider();

		CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode, provider);

		if (customerAccount == null) {
			throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCode);
		}

		customerAccountService.getEntityManager().refresh(customerAccount);

		List<AccountOperation> ops = customerAccount.getAccountOperations();
		for (AccountOperation op : ops) {
			if (op instanceof Payment) {
				Payment p = (Payment) op;
				PaymentDto paymentDto = new PaymentDto();
				paymentDto.setType(p.getType());
				paymentDto.setAmount(p.getAmount());
				paymentDto.setDueDate(p.getDueDate());
				paymentDto.setOccTemplateCode(p.getOccCode());
				paymentDto.setPaymentMethod(p.getPaymentMethod());
				paymentDto.setReference(p.getReference());
				paymentDto.setTransactionDate(p.getTransactionDate()); 
				paymentDto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(op));
				if (p instanceof AutomatedPayment) {
					AutomatedPayment ap = (AutomatedPayment) p;
					paymentDto.setBankCollectionDate(ap.getBankCollectionDate());
					paymentDto.setBankLot(ap.getBankLot());
					paymentDto.setDepositDate(ap.getDepositDate());
				}
				result.add(paymentDto);
			} else if (op instanceof OtherCreditAndCharge) {
				OtherCreditAndCharge occ = (OtherCreditAndCharge) op;
				PaymentDto paymentDto = new PaymentDto();
				paymentDto.setType(occ.getType());
				paymentDto.setDescription(op.getOccDescription());
				paymentDto.setAmount(occ.getAmount());
				paymentDto.setDueDate(occ.getDueDate());
				paymentDto.setOccTemplateCode(occ.getOccCode());
				paymentDto.setReference(occ.getReference());
				paymentDto.setTransactionDate(occ.getTransactionDate());
				result.add(paymentDto);
			}
		}
		return result;
	}

	public double getBalance(String customerAccountCode, User currentUser) throws BusinessException {
		Provider provider = currentUser.getProvider();
		CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode, provider);

		return customerAccountService.customerAccountBalanceDue(customerAccount, new Date()).doubleValue();
	}

}