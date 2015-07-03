package org.meveo.api.payment;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.payment.MatchingAmountDto;
import org.meveo.api.dto.payment.MatchingCodeDto;
import org.meveo.api.dto.response.payment.AccountOperationsResponseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.payments.RejectedPayment;
import org.meveo.model.payments.RejectedType;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.MatchingAmountService;
import org.meveo.service.payments.impl.MatchingCodeService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class AccountOperationApi extends BaseApi {

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private AccountOperationService accountOperationService;

	@Inject
	private MatchingCodeService matchingCodeService;

	@Inject
	private MatchingAmountService matchingAmountService;

	public void create(AccountOperationDto postData, User currentUser) throws MeveoApiException {
		AccountOperation accountOperation = null;

		CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCustomerAccount(), currentUser.getProvider());
		if (customerAccount == null) {
			throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCustomerAccount());
		}

		if (postData.getType().equals("OCC") && postData.getOtherCreditAndCharge() != null) {
			// otherCreditAndCharge
			OtherCreditAndCharge otherCreditAndCharge = new OtherCreditAndCharge();
			otherCreditAndCharge.setOperationDate(postData.getOtherCreditAndCharge().getOperationDate());
			accountOperation = (AccountOperation) otherCreditAndCharge;
		} else if (postData.getType().equals("I") && postData.getRecordedInvoice() != null) {
			// recordedInvoice
			RecordedInvoice recordedInvoice = new RecordedInvoice();
			recordedInvoice.setProductionDate(postData.getRecordedInvoice().getProductionDate());
			recordedInvoice.setInvoiceDate(postData.getRecordedInvoice().getInvoiceDate());
			recordedInvoice.setAmountWithoutTax(postData.getRecordedInvoice().getAmountWithoutTax());
			recordedInvoice.setTaxAmount(postData.getRecordedInvoice().getTaxAmount());
			recordedInvoice.setNetToPay(postData.getRecordedInvoice().getNetToPay());

			try {
				recordedInvoice.setPaymentMethod(PaymentMethodEnum.valueOf(postData.getRecordedInvoice().getPaymentMethod()));
			} catch (IllegalStateException e) {
				log.warn("error occurred while setting payment methode",e);
			} catch (NullPointerException e) {
				log.warn("error generated while setting payment methode",e);
			}

			recordedInvoice.setPaymentInfo(postData.getRecordedInvoice().getPaymentInfo());
			recordedInvoice.setPaymentInfo1(postData.getRecordedInvoice().getPaymentInfo1()); 
			recordedInvoice.setPaymentInfo5(postData.getRecordedInvoice().getPaymentInfo5());
			recordedInvoice.setPaymentInfo6(postData.getRecordedInvoice().getPaymentInfo6());

			// recordedInvoice.setDdRequestItem(postData.getRecordedInvoice().getDdRequestItem());
			// recordedInvoice.setDdRequestLOT(postData.getRecordedInvoice().getDdRequestItem());
			recordedInvoice.setBillingAccountName(postData.getRecordedInvoice().getBillingAccountName());

			accountOperation = (AccountOperation) recordedInvoice;
		} else if (postData.getType().equals("R") && postData.getRejectedPayment() != null) {
			// rejectedPayment
			RejectedPayment rejectedPayment = new RejectedPayment();

			try {
				rejectedPayment.setRejectedType(RejectedType.valueOf(postData.getRejectedPayment().getRejectedType()));
			} catch (IllegalStateException e) {
				log.warn("error occurred while setting rejected type", e);
			} catch (NullPointerException e) {
				log.warn("error generated while setting rejected type", e);
			}

			rejectedPayment.setBankLot(postData.getRejectedPayment().getBankLot());
			rejectedPayment.setBankReference(postData.getRejectedPayment().getBankReference());
			rejectedPayment.setRejectedDate(postData.getRejectedPayment().getRejectedDate());
			rejectedPayment.setRejectedDescription(postData.getRejectedPayment().getRejectedDescription());
			rejectedPayment.setRejectedCode(postData.getRejectedPayment().getRejectedCode());

			accountOperation = (AccountOperation) rejectedPayment;
		}

		if (accountOperation == null) {
			throw new MeveoApiException("Type and data mismatch OCC=otherCreditAndCharge, I=recordedInvoice, R=rejectedPayment.");
		}

		accountOperation.setDueDate(postData.getDueDate());
		accountOperation.setType(postData.getType());
		accountOperation.setTransactionDate(postData.getTransactionDate());
		try {
			accountOperation.setTransactionCategory(OperationCategoryEnum.valueOf(postData.getTransactionCategory()));
		} catch (IllegalStateException e) {
			log.warn("error occurred while setting transaction category", e);
		} catch (NullPointerException e) {
			log.warn("error generated while setting transaction category", e);
		}
		accountOperation.setReference(postData.getReference());
		accountOperation.setAccountCode(postData.getAccountCode());
		accountOperation.setAccountCodeClientSide(postData.getAccountCodeClientSide());
		accountOperation.setAmount(postData.getAmount());
		accountOperation.setMatchingAmount(postData.getMatchingAmount());
		accountOperation.setUnMatchingAmount(postData.getUnMatchingAmount());
		accountOperation.setCustomerAccount(customerAccount);

		try {
			accountOperation.setMatchingStatus(MatchingStatusEnum.valueOf(postData.getMatchingStatus()));
		} catch (IllegalStateException e) {
			log.warn("error occurred while setting matching status", e);
		} catch (NullPointerException e) {
			log.warn("error generated while setting matching status", e);
		}

		accountOperation.setOccCode(postData.getOccCode());
		accountOperation.setOccDescription(postData.getOccDescription());
		if(!StringUtils.isBlank(postData.getExcludedFromDunning())){
			accountOperation.setExcludedFromDunning(postData.getExcludedFromDunning());	
		}else{
			accountOperation.setExcludedFromDunning(false);	
		}
		
		accountOperationService.create(accountOperation, currentUser, currentUser.getProvider());

		if (postData.getMatchingAmounts() != null) {
			for (MatchingAmountDto matchingAmountDto : postData.getMatchingAmounts().getMatchingAmount()) {
				MatchingAmount matchingAmount = new MatchingAmount();
				matchingAmount.setMatchingAmount(matchingAmountDto.getMatchingAmount());
				matchingAmount.setAccountOperation(accountOperation);
				if (matchingAmountDto.getMatchingCodes() != null) {
					for (MatchingCodeDto matchingCodeDto : matchingAmountDto.getMatchingCodes().getMatchingCode()) {
						MatchingCode matchingCode = matchingCodeService.findByCode(matchingCodeDto.getCode(), currentUser.getProvider());
						if (matchingCode == null) {
							matchingCode = new MatchingCode();
							matchingCode.setCode(matchingCodeDto.getCode());
						}

						try {
							matchingCode.setMatchingType(MatchingTypeEnum.valueOf(matchingCodeDto.getMatchingType()));
						} catch (IllegalStateException e) {
							log.warn("error occurred while setting matching type",e);
						} catch (NullPointerException e) {
							log.warn("error generated while setting matching type",e);
						}

						matchingCode.setMatchingDate(matchingCodeDto.getMatchingDate());
						matchingCode.setMatchingAmountCredit(matchingCodeDto.getMatchingAmountCredit());
						matchingCode.setMatchingAmountDebit(matchingCodeDto.getMatchingAmountDebit());

						if (matchingCode.isTransient()) {
							matchingCodeService.create(matchingCode, currentUser, currentUser.getProvider());
						} else {
							matchingCodeService.update(matchingCode, currentUser);
						}

						matchingAmount.setMatchingCode(matchingCode);
					}
				}

				if (matchingAmount.isTransient()) {
					matchingAmountService.create(matchingAmount, currentUser, currentUser.getProvider());
				} else {
					matchingAmountService.update(matchingAmount, currentUser);
				}

				accountOperation.getMatchingAmounts().add(matchingAmount);
			}
		}
	}

	public AccountOperationsResponseDto list(String customerAccountCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(customerAccountCode)) {
			AccountOperationsResponseDto result = new AccountOperationsResponseDto();

			CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode, provider);
			if (customerAccount == null) {
				throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCode);
			}

			List<AccountOperation> accountOperations = accountOperationService.listAccountOperationByCustomerAccount(customerAccount, provider);

			for (AccountOperation accountOp : accountOperations) {
				AccountOperationDto accountOperationDto = new AccountOperationDto();
				accountOperationDto.setDueDate(accountOp.getDueDate());
				accountOperationDto.setType(accountOp.getType());
				accountOperationDto.setTransactionDate(accountOp.getTransactionDate());
				accountOperationDto.setTransactionCategory(accountOp.getTransactionCategory().toString() != null ? accountOp.getTransactionCategory().toString() : null);
				accountOperationDto.setReference(accountOp.getReference());
				accountOperationDto.setAccountCode(accountOp.getAccountCode());
				accountOperationDto.setAccountCodeClientSide(accountOp.getAccountCodeClientSide());
				accountOperationDto.setAmount(accountOp.getAmount());
				accountOperationDto.setMatchingAmount(accountOp.getMatchingAmount());
				accountOperationDto.setUnMatchingAmount(accountOp.getUnMatchingAmount());
				accountOperationDto.setMatchingStatus(accountOp.getMatchingStatus().toString() != null ? accountOp.getMatchingStatus().toString() : null);
				accountOperationDto.setOccCode(accountOp.getOccCode());
				accountOperationDto.setOccDescription(accountOp.getOccDescription());

				List<MatchingAmount> matchingAmounts = accountOp.getMatchingAmounts();
				MatchingAmountDto matchingAmountDto = new MatchingAmountDto();
				for (MatchingAmount matchingAmount : matchingAmounts) {
					matchingAmountDto.setMatchingCode(matchingAmount.getMatchingCode().getCode());
					matchingAmountDto.setMatchingAmount(matchingAmount.getMatchingAmount());
					accountOperationDto.addMatchingAmounts(matchingAmountDto);
				}

				result.getAccountOperations().getAccountOperation().add(accountOperationDto);
			}

			return result;
		} else {
			missingParameters.add("customerAccountCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

}
