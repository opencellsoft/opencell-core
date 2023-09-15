package org.meveo.apiv2.refund;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.PayByCardOrSepaDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.RefundApi;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.OperationActionEnum;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;

@Interceptors({ WsRestApiInterceptor.class })
public class RefundResourceImpl implements RefundResource{

    @Inject
    private RefundApi refundApi;
    @Inject
    private CustomerAccountService customerAccountService;
    @Inject
    private AccountOperationService accountOperationService;


    @Override
    public Response refundByCard(CardRefund cardRefund) {
        PayByCardOrSepaDto payByCardDto = toPayByCardDto(cardRefund);
        try {
            refundApi.refundByCard(payByCardDto);
            return Response.ok(new ActionStatus()).build();
        } catch (Exception e) {
            throw new ClientErrorException(e.getMessage(), Response.Status.PRECONDITION_FAILED);
        }
    }

    @Override
    public Response refundBySCT(SCTRefund sctRefund) {
        if(Objects.isNull(sctRefund.getCustomerAccountCode()) || sctRefund.getCustomerAccountCode().isBlank()){
            throw new BadRequestException("Customer account code is required!");
        }
        CustomerAccount CAByCode = customerAccountService.findByCode(sctRefund.getCustomerAccountCode(), Collections.singletonList("paymentMethods"));
        if(Objects.isNull(CAByCode)){
            throw new EntityDoesNotExistsException(CustomerAccount.class, sctRefund.getCustomerAccountCode());
        }
        if(sctRefund.getAoToRefund() != null && !sctRefund.getAoToRefund().isEmpty()){
        	validateIban(sctRefund.getIBAN());
            HashSet<Long> aoIds = new HashSet<>(sctRefund.getAoToRefund());
            CustomerAccount customerAccount = customerAccountService.findByCode(sctRefund.getCustomerAccountCode(), Collections.singletonList("accountOperations"));
            if(customerAccountService.customerAccountBalanceDue(customerAccount, null).compareTo(BigDecimal.ZERO)>1){
                throw new BusinessApiException("refund is impossible : the customer balance is debit");
            }
            customerAccount
                    .getAccountOperations().stream()
                    .filter(accountOperation -> aoIds.contains(accountOperation.getId()) 
                    		&& OperationCategoryEnum.CREDIT.equals(accountOperation.getTransactionCategory()))
                    .forEach(accountOperation -> {
                        accountOperation.setOperationAction(OperationActionEnum.TO_REFUND);
                        accountOperationService.update(accountOperation);
                    });
        }
        return Response.ok(new ActionStatus()).build();
    }

	private void validateIban(String iban) {
		if(iban==null) {
			return;
		}
		String IBAN_PATTERN = "[A-Z]{2,2}[0-9]{2,2}[a-zA-Z0-9]{1,30}";
		Pattern pattern = Pattern.compile(IBAN_PATTERN);
		Matcher matcher = pattern.matcher(iban);
		if(!matcher.matches()) {
			throw new ValidationException("wrong IBAN value : " + iban);
		}
		
	}

	private PayByCardOrSepaDto toPayByCardDto(CardRefund cardRefund) {
        PayByCardOrSepaDto payByCardDto = new PayByCardOrSepaDto();
        payByCardDto.setCtsAmount(cardRefund.getCtsAmount());
        payByCardDto.setCardNumber(cardRefund.getCardNumber());
        payByCardDto.setCustomerAccountCode(cardRefund.getCustomerAccountCode());
        payByCardDto.setOwnerName(cardRefund.getOwnerName());
        payByCardDto.setCvv(cardRefund.getCvv());
        payByCardDto.setExpiryDate(cardRefund.getExpiryDate());
        if(Objects.nonNull(cardRefund.getCardType())){
            payByCardDto.setCardType(CreditCardTypeEnum.valueOf(cardRefund.getCardType()));
        }
        payByCardDto.setAoToPay(cardRefund.getAoToPay());
        payByCardDto.setCreateAO(cardRefund.createAO());
        payByCardDto.setToMatch(cardRefund.toMatch());
        payByCardDto.setComment(cardRefund.getComment());
        return payByCardDto;
    }
}
