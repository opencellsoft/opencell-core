package org.meveo.apiv2.accountreceivable.deferralPayments;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.apiv2.AcountReceivable.DeferralPayments;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.payments.impl.AccountOperationService;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Arrays;

public class AccountReceivableDeferralPaymentsResourceImpl implements AccountReceivableDeferralPaymentsResource{
    @Inject
    private AccountOperationService accountOperationService;
    @Override
    public Response create(DeferralPayments deferralPayments) {
        AccountOperation accountOperation = null;
        if(deferralPayments.getAccountOperationId() == null){
            throw new InvalidParameterException("AccountOperationId is required");
        }else {
            accountOperation = accountOperationService.findById(deferralPayments.getAccountOperationId(), Arrays.asList("customerAccount"));
            if(accountOperation == null){
               throw new EntityDoesNotExistsException("Account operation with id {id}, does not exist.");
            }
        }
        if(deferralPayments.getPaymentDate() == null){
            throw new InvalidParameterException("PaymentDate is required");
        }
        PaymentMethodEnum paymentMethod = deferralPayments.getPaymentMethod() == null ? null : PaymentMethodEnum.valueOf(deferralPayments.getPaymentMethod());
        AccountOperation deferralPayments1 = accountOperationService.createDeferralPayments(accountOperation, paymentMethod, deferralPayments.getPaymentDate());
        return Response.ok().entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the deferral payments successfully created\"}}").build();
    }
}
