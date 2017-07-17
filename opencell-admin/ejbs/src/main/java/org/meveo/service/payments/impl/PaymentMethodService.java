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
import org.meveo.model.payments.CustomerAccount;
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
            obtainAndSetCardToken(cardPayment, cardPayment.getCustomerAccount());
        }

        super.create(paymentMethod);

        // Mark other payment methods as not preferred
        if (paymentMethod.isPreferred()) {
            getEntityManager().createNamedQuery("PaymentMethod.updatePreferredPaymentMethod").setParameter("id", paymentMethod.getId())
                .setParameter("ca", paymentMethod.getCustomerAccount()).executeUpdate();
        }
    }

    /**
     * Store payment information in payment gateway and return token id in a payment gateway
     * 
     * @param cardPaymentMethod Card payment method
     * @param customerAccount Customer account
     * @throws BusinessException
     */
    public void obtainAndSetCardToken(CardPaymentMethod cardPaymentMethod, CustomerAccount customerAccount) throws BusinessException {
        if (!StringUtils.isBlank(cardPaymentMethod.getTokenId())) {
            return;
        }

        cardPaymentMethod.setHiddenCardNumber(cardPaymentMethod.getCardNumber().substring(cardPaymentMethod.getCardNumber().length() - 4));

        String coutryCode = null;
        Country country = countryService.findByName(customerAccount.getAddress() != null ? customerAccount.getAddress().getCountry() : null);
        if (country != null) {
            coutryCode = country.getCountryCode();
        }
        GatewayPaymentInterface gatewayPaymentInterface = gatewayPaymentFactory
            .getInstance(GatewayPaymentNamesEnum.valueOf(ParamBean.getInstance().getProperty("meveo.gatewayPayment", "CUSTOM_API")));

        String cardNumber = cardPaymentMethod.getCardNumber();
        cardNumber = cardNumber.replaceAll(" ", "");
        String tockenID = gatewayPaymentInterface.createCardToken(customerAccount, cardPaymentMethod.getAlias(), cardNumber, cardPaymentMethod.getOwner(),
            StringUtils.getLongAsNChar(cardPaymentMethod.getMonthExpiration(), 2) + StringUtils.getLongAsNChar(cardPaymentMethod.getYearExpiration(), 2),
            cardPaymentMethod.getIssueNumber(), cardPaymentMethod.getCardType().getId(), coutryCode);

        cardPaymentMethod.setTokenId(tockenID);
    }
}