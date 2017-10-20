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

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.base.PersistenceService;

/**
 * PaymentMethod service implementation.
 */
@Stateless
public class PaymentMethodService extends PersistenceService<PaymentMethod> {

    @Inject
    private GatewayPaymentFactory gatewayPaymentFactory;

    @Override
    public void create(PaymentMethod paymentMethod) throws BusinessException {

        if (paymentMethod instanceof CardPaymentMethod) {
            CardPaymentMethod cardPayment = (CardPaymentMethod) paymentMethod;
            if (!cardPayment.isValidForDate(new Date())) {
                throw new BusinessException("Cant add expired card");
            }
            obtainAndSetCardToken(cardPayment, cardPayment.getCustomerAccount());
        }

        super.create(paymentMethod);

        // Mark other payment methods as not preferred
        if (paymentMethod.isPreferred()) {
            getEntityManager().createNamedQuery("PaymentMethod.updatePreferredPaymentMethod").setParameter("id", paymentMethod.getId())
            .setParameter("ca", paymentMethod.getCustomerAccount()).executeUpdate();
        }
    }

    @Override
    public PaymentMethod update(PaymentMethod entity) throws BusinessException {
        if (entity.isPreferred()) {
            if (entity instanceof CardPaymentMethod) {
                if (!((CardPaymentMethod) entity).isValidForDate(new Date())) {
                    throw new BusinessException("Cant mark expired card as preferred");
                }
            }
        }
        PaymentMethod paymentMethod = super.update(entity);

        // Mark other payment methods as not preferred
        if (paymentMethod.isPreferred()) {
            getEntityManager().createNamedQuery("PaymentMethod.updatePreferredPaymentMethod").setParameter("id", paymentMethod.getId())
            .setParameter("ca", paymentMethod.getCustomerAccount()).executeUpdate();
        }

        return paymentMethod;
    }

    @Override
    public void remove(PaymentMethod paymentMethod) throws BusinessException {

        boolean wasPreferred = paymentMethod.isPreferred();
        Long caId = paymentMethod.getCustomerAccount().getId();

        long paymentMethodCount = (long) getEntityManager().createNamedQuery("PaymentMethod.getNumberOfPaymentMethods").setParameter("caId", caId).getSingleResult();
        if (paymentMethodCount <= 1) {
            throw new ValidationException("At least one payment method on a customer account is required");
        }

        super.remove(paymentMethod);

        if (wasPreferred) {
            Long minId = (Long) getEntityManager().createNamedQuery("PaymentMethod.updateFirstPaymentMethodToPreferred1").setParameter("caId", caId).getSingleResult();
            getEntityManager().createNamedQuery("PaymentMethod.updateFirstPaymentMethodToPreferred2").setParameter("id", minId).setParameter("caId", caId).executeUpdate();
            getEntityManager().createNamedQuery("PaymentMethod.updateFirstPaymentMethodToPreferred3").setParameter("id", minId).setParameter("caId", caId).executeUpdate();
        }
    }

    /**
     * Store payment information in payment gateway and return token id in a payment gateway.
     * 
     * @param cardPaymentMethod Card payment method
     * @param customerAccount Customer account
     * @throws BusinessException business exception.
     */
    public void obtainAndSetCardToken(CardPaymentMethod cardPaymentMethod, CustomerAccount customerAccount) throws BusinessException {
        if (!StringUtils.isBlank(cardPaymentMethod.getTokenId())) {
            return;
        }
        String cardNumber = cardPaymentMethod.getCardNumber();
        cardPaymentMethod.setHiddenCardNumber(CardPaymentMethod.hideCardNumber(cardNumber));

        String coutryCode = null; // TODO : waiting #2830
        GatewayPaymentInterface gatewayPaymentInterface = null;
        try {

            gatewayPaymentInterface = gatewayPaymentFactory.getInstance(GatewayPaymentNamesEnum.valueOf(ParamBean.getInstance().getProperty("meveo.gatewayPayment", "CUSTOM_API")));
        } catch (Exception e) {
            log.warn("Cant find payment gateway");
        }

        if (gatewayPaymentInterface != null) {
            String tockenID = gatewayPaymentInterface.createCardToken(customerAccount, cardPaymentMethod.getAlias(), cardNumber, cardPaymentMethod.getOwner(),
                    StringUtils.getLongAsNChar(cardPaymentMethod.getMonthExpiration(), 2) + StringUtils.getLongAsNChar(cardPaymentMethod.getYearExpiration(), 2),
                    cardPaymentMethod.getIssueNumber(), cardPaymentMethod.getCardType().getId(), coutryCode);

            cardPaymentMethod.setTokenId(tockenID);
        } else {
            cardPaymentMethod.setTokenId(null);
        }
    }

    /**
     * @param tokenId payment's token id
     * @return card payment method instance.
     */
    public CardPaymentMethod findByTokenId(String tokenId) {
        QueryBuilder queryBuilder = new QueryBuilder(CardPaymentMethod.class, "a", null);
        queryBuilder.addCriterion("tokenId", "=", tokenId, true);
        return (CardPaymentMethod) queryBuilder.getQuery(getEntityManager()).getSingleResult();
    }

    /**
     * 
     * @param customerAccount
     * @param paymentMethodEnum
     * @param isPreferred
     * @param info1 1st information
     * @param info2 2sd  information
     * @param info3 3rd information
     * @param info4 forth information
     * @param info5 fifth information
     * @return list of payment method.
     */
    public List<PaymentMethod> list(CustomerAccount customerAccount, PaymentMethodEnum paymentMethodEnum,
            Boolean isPreferred, String info1, String info2, String info3, String info4, String info5) {
        QueryBuilder queryBuilder = new QueryBuilder(PaymentMethod.class, "pm", null);
        if (customerAccount != null) {
            queryBuilder.addCriterionEntity("pm.customerAccount", customerAccount);
        }
        if (paymentMethodEnum != null) {
            queryBuilder.addCriterionEnum("pm.paymentType", paymentMethodEnum);
        }
        if (isPreferred != null) {
            queryBuilder.addBooleanCriterion("pm.preferred", isPreferred);
        }
        if (!StringUtils.isBlank(info1)) {
            queryBuilder.addCriterion("pm.info1", "=", info1, false);
        }
        if (!StringUtils.isBlank(info2)) {
            queryBuilder.addCriterion("pm.info2", "=", info2, false);
        }
        if (!StringUtils.isBlank(info3)) {
            queryBuilder.addCriterion("pm.info3", "=", info3, false);
        }
        if (!StringUtils.isBlank(info4)) {
            queryBuilder.addCriterion("pm.info4", "=", info4, false);
        }
        if (!StringUtils.isBlank(info5)) {
            queryBuilder.addCriterion("pm.info5", "=", info5, false);
        }
        queryBuilder.addOrderCriterion("pm.auditable.created", false);

        return (List<PaymentMethod>) queryBuilder.getQuery(getEntityManager()).getResultList();
    }
}