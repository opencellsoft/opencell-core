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
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Country;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.base.PersistenceService;

/**
 * PaymentMethod service implementation.
 */
@Stateless
public class PaymentMethodService extends PersistenceService<PaymentMethod> {

    @Inject
    private CountryService countryService;

    @Inject
    private GatewayPaymentFactory gatewayPaymentFactory;

    @Override
    public void create(PaymentMethod paymentMethod) throws BusinessException {

        if (paymentMethod instanceof CardPaymentMethod) {

            CardPaymentMethod cardPayment = (CardPaymentMethod) paymentMethod;
            obtainAndSetCardToken(cardPayment);
        }

        super.create(paymentMethod);

        // Mark other payment methods as not preferred
        if (paymentMethod.isPreferred()) {
            getEntityManager().createNamedQuery("PaymentMethod.updatePreferredPaymentMethod").setParameter("id", paymentMethod.getId())
                .setParameter("ca", paymentMethod.getCustomerAccount()).executeUpdate();
        }
    }

    public void obtainAndSetCardToken(CardPaymentMethod cardPayment) throws BusinessException {
        if (!StringUtils.isBlank(cardPayment.getTokenId())) {
            return;
        }
        cardPayment.setHiddenCardNumber(cardPayment.getCardNumber().substring(cardPayment.getCardNumber().length() - 4));

        String coutryCode = null;
        Country country = countryService.findByName(cardPayment.getCustomerAccount().getAddress() != null ? cardPayment.getCustomerAccount().getAddress().getCountry() : null);
        if (country != null) {
            coutryCode = country.getCountryCode();
        }
        GatewayPaymentInterface gatewayPaymentInterface = gatewayPaymentFactory
            .getInstance(GatewayPaymentNamesEnum.valueOf(ParamBean.getInstance().getProperty("meveo.gatewayPayment", "CUSTOM_API")));

        String cardNumber = cardPayment.getCardNumber();
        cardNumber = cardNumber.replaceAll(" ", "");
        String tockenID = gatewayPaymentInterface.createCardToken(cardPayment.getCustomerAccount(), cardPayment.getAlias(), cardNumber, cardPayment.getOwner(),
            StringUtils.getLongAsNChar(cardPayment.getMonthExpiration(), 2) + StringUtils.getLongAsNChar(cardPayment.getYearExpiration(), 2), cardPayment.getIssueNumber(),
            cardPayment.getCardType().getId(), coutryCode);

        cardPayment.setTokenId(tockenID);
    }
}