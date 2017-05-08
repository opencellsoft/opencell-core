/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.payments.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.payment.DoPaymentResponseDto;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.billing.Invoice;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.base.PersistenceService;

/**
 * Payment service implementation.
 */
@Stateless
public class PaymentService extends PersistenceService<Payment> {
	
	@Inject
	private CardTokenService cardTokenService;

	public DoPaymentResponseDto doPayment(CustomerAccount customerAccount, Long ctsAmount, Invoice invoice) throws BusinessException {
		if(customerAccount.getPaymentTokens() == null || customerAccount.getPaymentTokens().isEmpty()){
			throw new BusinessException("There no payment token for customerAccount:"+customerAccount.getCode());
		}
		GatewayPaymentInterface  gatewayPaymentInterface = GatewayPaymentFactory.getInstance(GatewayPaymentNamesEnum.valueOf(ParamBean.getInstance().getProperty("meveo.gatewayPayment", "INGENICO")));		
		if(PaymentMethodEnum.CARD.name().equals(customerAccount.getPaymentMethod().name())){
			return gatewayPaymentInterface.doPayment(cardTokenService.getPreferedToken(customerAccount), ctsAmount);
		}		
		throw new BusinessException("Unsupported payment method:"+customerAccount.getPaymentMethod());
	}
}
