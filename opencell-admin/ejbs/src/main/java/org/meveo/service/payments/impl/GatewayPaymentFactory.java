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

package org.meveo.service.payments.impl;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentGatewayTypeEnum;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.payment.PaymentScriptInterface;

@Stateless
public class GatewayPaymentFactory implements Serializable {

    private static final long serialVersionUID = -8729566002684225810L;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    public GatewayPaymentInterface getInstance(PaymentGateway paymentGateway) throws Exception {
        GatewayPaymentInterface gatewayPaymentInterface = null;

        PaymentGatewayTypeEnum paymentType = paymentGateway.getType();
        if (paymentType == PaymentGatewayTypeEnum.CUSTOM) { 
            gatewayPaymentInterface = new CustomApiGatewayPayment((PaymentScriptInterface) scriptInstanceService.getScriptInstance(paymentGateway.getScriptInstance().getCode()));           
        }
        if (paymentType == PaymentGatewayTypeEnum.NATIF) {
            Class<?> clazz = Class.forName(paymentGateway.getImplementationClassName());
            gatewayPaymentInterface = (GatewayPaymentInterface) clazz.newInstance();
        }
        gatewayPaymentInterface.setPaymentGateway(paymentGateway);
        return gatewayPaymentInterface;
    }
}
