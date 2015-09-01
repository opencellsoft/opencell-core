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
import org.meveo.api.exception.MissingParameterException;
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
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.AutomatedPaymentService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.RecordedInvoiceService;

@Stateless
public class PaymentApi extends BaseApi {

	private static final java.util.logging.Logger log = java.util.logging.Logger
			.getLogger(PaymentApi.class.getName());

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

	public void createPayment(PaymentDto paymentDto, User currentUser)
			throws Exception {
		log.info("create payment for amount:" + paymentDto.getAmount()
				+ " paymentMethodEnum:" + paymentDto.getPaymentMethod()
				+ " isToMatching:" + paymentDto.isToMatching()
				+ "  customerAccount:" + paymentDto.getCustomerAccountCode()
				+ "...");

		Provider provider = currentUser.getProvider();
		CustomerAccount customerAccount = customerAccountService.findByCode(
				paymentDto.getCustomerAccountCode(), provider);
		if (customerAccount == null) {
			throw new BusinessException(
					"Cannot find customer account with code="
							+ paymentDto.getCustomerAccountCode());
		}

		OCCTemplate occTemplate = oCCTemplateService.findByCode(
				paymentDto.getOccTemplateCode(), provider.getCode());
		if (occTemplate == null) {
			throw new BusinessException("Cannot find OCC Template with code="
					+ paymentDto.getOccTemplateCode());
		}

		if (!StringUtils.isBlank(paymentDto.getAmount())
				&& !StringUtils.isBlank(paymentDto.getPaymentMethod())
				&& !StringUtils.isBlank(paymentDto.getCustomerAccountCode())
				&& !StringUtils.isBlank(paymentDto.getOccTemplateCode())
				&& !StringUtils.isBlank(paymentDto.getReference())) {
			AutomatedPayment automatedPayment = new AutomatedPayment();
			automatedPayment.setProvider(provider);
			automatedPayment.setPaymentMethod(PaymentMethodEnum
					.valueOf(paymentDto.getPaymentMethod()));
			automatedPayment.setAmount(paymentDto.getAmount());
			automatedPayment.setUnMatchingAmount(paymentDto.getAmount());
			automatedPayment.setMatchingAmount(BigDecimal.ZERO);
			automatedPayment.setAccountCode(occTemplate.getAccountCode());
			automatedPayment.setOccCode(occTemplate.getCode());
			automatedPayment.setOccDescription(occTemplate.getDescription());
			automatedPayment.setTransactionCategory(occTemplate
					.getOccCategory());
			automatedPayment.setAccountCodeClientSide(occTemplate
					.getAccountCodeClientSide());
			automatedPayment.setCustomerAccount(customerAccount);
			automatedPayment.setReference(paymentDto.getReference());
			automatedPayment.setDueDate(paymentDto.getDueDate());
			automatedPayment
					.setTransactionDate(paymentDto.getTransactionDate());
			automatedPayment.setMatchingStatus(MatchingStatusEnum.O);
			automatedPaymentService.create(automatedPayment, currentUser,
					provider);

			if (paymentDto.isToMatching()) {
				MatchingCode matchingCode = new MatchingCode();
				BigDecimal amountToMatch = BigDecimal.ZERO;
				for (int i = 0; i < paymentDto.getListOCCReferenceforMatching()
						.size(); i++) {
					RecordedInvoice accountOperation = recordedInvoiceService
							.getRecordedInvoice(paymentDto
									.getListOCCReferenceforMatching().get(i),
									provider);
					amountToMatch = accountOperation.getUnMatchingAmount();
					accountOperation.setMatchingAmount(accountOperation
							.getMatchingAmount().add(amountToMatch));
					accountOperation.setUnMatchingAmount(accountOperation
							.getUnMatchingAmount().subtract(amountToMatch));
					accountOperation.setMatchingStatus(MatchingStatusEnum.L);
					recordedInvoiceService
							.update(accountOperation, currentUser);
					MatchingAmount matchingAmount = new MatchingAmount();
					matchingAmount.setProvider(accountOperation.getProvider());
					matchingAmount.setAccountOperation(accountOperation);
					matchingAmount.setMatchingCode(matchingCode);
					matchingAmount.setMatchingAmount(amountToMatch);
					accountOperation.getMatchingAmounts().add(matchingAmount);
					matchingCode.getMatchingAmounts().add(matchingAmount);
				}

				matchingCode.setMatchingAmountDebit(paymentDto.getAmount());
				matchingCode.setMatchingAmountCredit(paymentDto.getAmount());
				matchingCode.setMatchingDate(new Date());
				matchingCode.setMatchingType(MatchingTypeEnum.A);
				matchingCode.setProvider(provider);
				matchingCodeService.create(matchingCode, currentUser, provider);
				log.info("matching created  for 1 automatedPayment and "
						+ (paymentDto.getListOCCReferenceforMatching().size() - 1)
						+ " occ");
			} else {
				log.info("no matching created ");
			}
			log.info("automatedPayment created for amount:"
					+ automatedPayment.getAmount());
		} else {
			if (StringUtils.isBlank(paymentDto.getAmount())) {
				missingParameters.add("amount");
			}
			if (StringUtils.isBlank(paymentDto.getCustomerAccountCode())) {
				missingParameters.add("CustomerAccountCode");
			}
			if (StringUtils.isBlank(paymentDto.getOccTemplateCode())) {
				missingParameters.add("OccTemplateCode");
			}
			if (StringUtils.isBlank(paymentDto.getReference())) {
				missingParameters.add("Reference");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}

	}

	public List<PaymentDto> getPaymentList(String customerAccountCode,
			User currentUser) throws Exception {
		List<PaymentDto> result = new ArrayList<PaymentDto>();

		if (currentUser == null) {
			throw new BusinessException("currentUser is empty");
		}
		Provider provider = currentUser.getProvider();

		CustomerAccount customerAccount = customerAccountService.findByCode(
				customerAccountCode, provider);

		if (customerAccount == null) {
			throw new EntityDoesNotExistsException(CustomerAccount.class,
					customerAccountCode);
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
				paymentDto.setPaymentMethod(p.getPaymentMethod().name());
				paymentDto.setReference(p.getReference());
				paymentDto.setTransactionDate(p.getTransactionDate());
				if (p instanceof AutomatedPayment) {
					AutomatedPayment ap = (AutomatedPayment) p;
					paymentDto
							.setBankCollectionDate(ap.getBankCollectionDate());
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

	public double getBalance(String customerAccountCode, User currentUser)
			throws BusinessException {
		Provider provider = currentUser.getProvider();
		CustomerAccount customerAccount = customerAccountService.findByCode(
				customerAccountCode, provider);

		return customerAccountService.customerAccountBalanceDue(
				customerAccount, new Date()).doubleValue();
	}

}
