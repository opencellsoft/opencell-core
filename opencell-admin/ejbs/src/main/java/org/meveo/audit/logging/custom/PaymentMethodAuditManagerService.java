/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.audit.logging.custom;

import org.meveo.admin.exception.BusinessException;
import org.meveo.audit.logging.annotations.CustomMeveoAudit;
import org.meveo.audit.logging.core.AuditEventProcessor;
import org.meveo.audit.logging.core.MetadataHandler;
import org.meveo.audit.logging.dto.AuditEvent;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.EntityActionsEnum;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.service.payments.impl.CustomerAccountService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * A custom class to log/audit a payment method management for a customerAccount.
 *
 * @author Khalid HORRI
 * @lastModifiedVersion 7.2
 **/
@Stateless
@CustomMeveoAudit
public class PaymentMethodAuditManagerService implements CustomAuditManagerService {

    @Inject
    private MetadataHandler metadataHandler;

    @Inject
    private AuditEventProcessor auditEventProcessor;

    /**
     * Add the payment method to the audit
     *
     * @param clazz       a Class
     * @param method      a method where the action on payment method is executed.
     * @param paramValues An array of payment methods
     * @throws BusinessException BusinessException
     */
    public void audit(Class<? extends Object> clazz, Method method, Object[] paramValues) throws BusinessException {
        String customerAccountServiceClassName = ReflectionUtils.getCleanClassName(CustomerAccountService.class.getSimpleName());
        if (customerAccountServiceClassName.equals(clazz.getSimpleName()) && (EntityActionsEnum.update.name().equals(method.getName()) || EntityActionsEnum.create
                .name().equals(method.getName())) && paramValues != null && paramValues.length > 0) {
            Map<String, List<PaymentMethod>> auditedPaymentMethods = ((CustomerAccount) paramValues[0]).getAuditedMethodPayments();
            if (auditedPaymentMethods == null || auditedPaymentMethods.isEmpty()) {
                return;
            }
            for (String action : auditedPaymentMethods.keySet()) {
                AuditEvent event = new AuditEvent();
                event.setEntity(PaymentMethod.class.getName());
                event.setAction(action);
                event.addField(PaymentMethod.class.getName(), auditedPaymentMethods.get(action));
                event = metadataHandler.addSignature(event);
                auditEventProcessor.process(event);
            }

        }

    }

}
