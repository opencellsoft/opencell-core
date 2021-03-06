package org.meveo.apiv2.accountreceivable.deferralPayments;

import static java.util.Arrays.asList;
import static javax.ws.rs.core.Response.ok;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.apiv2.AcountReceivable.DeferralPayments;
import org.meveo.model.audit.logging.AuditLog;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.payments.impl.AccountOperationService;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Date;

public class AccountReceivableDeferralPaymentsResourceImpl implements AccountReceivableDeferralPaymentsResource {

    @Inject
    private AccountOperationService accountOperationService;

    @Inject
    private AuditLogService auditLogService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Override
    public Response create(DeferralPayments deferralPayments) {
        AccountOperation accountOperation = null;
        if (deferralPayments.getAccountOperationId() == null) {
            throw new InvalidParameterException("AccountOperationId is required");
        } else {
            accountOperation = accountOperationService.findById(deferralPayments.getAccountOperationId(), asList("customerAccount"));
            if (accountOperation == null) {
                throw new EntityDoesNotExistsException("Account operation with id {id}, does not exist.");
            }
        }
        if (deferralPayments.getPaymentDate() == null) {
            throw new InvalidParameterException("PaymentDate is required");
        }
        PaymentMethodEnum paymentMethod = deferralPayments.getPaymentMethod() == null ? null : PaymentMethodEnum.valueOf(deferralPayments.getPaymentMethod());
        AccountOperation savedDeferralPayments = accountOperationService.createDeferralPayments(accountOperation, paymentMethod, deferralPayments.getPaymentDate());
        createAuditLog(currentUser.getUserName(), savedDeferralPayments.getCode());
        return ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the deferral payments successfully created\"}}")
                .build();
    }

    private AuditLog createAuditLog(String actor, String origin) {
        AuditLog auditLog = new AuditLog();
        auditLog.setOrigin(origin);
        auditLog.setCreated(new Date());
        auditLog.setEntity("PaymentDeferral");
        auditLog.setActor(actor);
        auditLog.setAction("CREATION");
        auditLogService.create(auditLog);
        return auditLog;
    }
}