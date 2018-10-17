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

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.dto.payment.HostedCheckoutInput;
import org.meveo.api.dto.payment.MandatInfoDto;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.service.base.PersistenceService;

/**
 * PaymentMethod service implementation.
 * 
 * @author anasseh
 * @author Mounir Bahije
 * @lastModifiedVersion 5.2
 */
@Stateless
public class PaymentMethodService extends PersistenceService<PaymentMethod> {

    /** The gateway payment factory. */
    @Inject
    private GatewayPaymentFactory gatewayPaymentFactory;

    /** The payment gateway service. */
    @Inject
    private PaymentGatewayService paymentGatewayService;

    /** The customer account service. */
    @Inject
    private CustomerAccountService customerAccountService;

    /* (non-Javadoc)
     * @see org.meveo.service.base.PersistenceService#create(org.meveo.model.IEntity)
     */
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

    /**
     * Test if the card with a TokenId and aoociated to a customer account Exist.
     *
     * @param paymentMethod Payment Method
     * @return true, if successful
     * @throws BusinessException the business exception
     */
    public boolean cardTokenExist(PaymentMethod paymentMethod) throws BusinessException {

        boolean result = false;
        if (paymentMethod instanceof CardPaymentMethod) {
            CardPaymentMethod cardPayment = (CardPaymentMethod) paymentMethod;
            if ((cardPayment == null) || (cardPayment.getCustomerAccount() == null)) {
                result = true;
            }
            long nbrOfCardCustomerAccount = (long) getEntityManager().createNamedQuery("PaymentMethod.getNumberOfCardCustomerAccount")
                .setParameter("customerAccountId", cardPayment.getCustomerAccount().getId()).setParameter("monthExpiration", cardPayment.getMonthExpiration())
                .setParameter("yearExpiration", cardPayment.getYearExpiration()).setParameter("hiddenCardNumber", cardPayment.getHiddenCardNumber())
                .setParameter("cardType", cardPayment.getCardType()).getSingleResult();

            if (nbrOfCardCustomerAccount > 0)
                result = true;
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.meveo.service.base.PersistenceService#update(org.meveo.model.IEntity)
     */
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

    /* (non-Javadoc)
     * @see org.meveo.service.base.PersistenceService#remove(org.meveo.model.IEntity)
     */
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
        GatewayPaymentInterface gatewayPaymentInterface = null;
        PaymentGateway paymentGateway = paymentGatewayService.getPaymentGateway(customerAccount, cardPaymentMethod, null);
        if (paymentGateway == null) {
            throw new BusinessException("No payment gateway for customerAccount:" + customerAccount.getCode());
        }
        try {
            gatewayPaymentInterface = gatewayPaymentFactory.getInstance(paymentGateway);
        } catch (Exception e) {
            // Create the card even if there no payment gateway
            log.warn("Cant find payment gateway");
        }

        if (gatewayPaymentInterface != null) {
            String tockenID = gatewayPaymentInterface.createCardToken(customerAccount, cardPaymentMethod.getAlias(), cardNumber, cardPaymentMethod.getOwner(),
                StringUtils.getLongAsNChar(cardPaymentMethod.getMonthExpiration(), 2) + StringUtils.getLongAsNChar(cardPaymentMethod.getYearExpiration(), 2),
                cardPaymentMethod.getIssueNumber(), cardPaymentMethod.getCardType());

            cardPaymentMethod.setTokenId(tockenID);
        }
        cardPaymentMethod.setHiddenCardNumber(CardPaymentMethod.hideCardNumber(cardNumber));
    }

    /**
     * Find by token id.
     *
     * @param tokenId payment's token id
     * @return card payment method instance.
     */
    public CardPaymentMethod findByTokenId(String tokenId) {
        QueryBuilder queryBuilder = new QueryBuilder(CardPaymentMethod.class, "a", null);
        queryBuilder.addCriterion("tokenId", "=", tokenId, true);
        return (CardPaymentMethod) queryBuilder.getQuery(getEntityManager()).getSingleResult();
    }

    /**
     * Create a new DDPaymentMethod from the createMandate callBback.
     * 
     * @param customerAccount Customer Account
     * @param mandatInfoDto Mandat info dto
     * @throws BusinessException Business Exception
     */
    public void createMandateCallBack(CustomerAccount customerAccount, MandatInfoDto mandatInfoDto) throws BusinessException {
        log.debug("createMandateCallBack customerAccount:{} mandatInfoDto:{}", customerAccount, mandatInfoDto);
        DDPaymentMethod ddPaymentMethod = new DDPaymentMethod();
        ddPaymentMethod.setCustomerAccount(customerAccount);
        ddPaymentMethod.setMandateIdentification(mandatInfoDto.getReference());
        ddPaymentMethod.setMandateDate(mandatInfoDto.getDateSigned());
        ddPaymentMethod.setPreferred(true);
        ddPaymentMethod.setAlias(mandatInfoDto.getReference());
        BankCoordinates bankCoordinates = new BankCoordinates();
        bankCoordinates.setBankName(mandatInfoDto.getBankName());
        bankCoordinates.setIban(mandatInfoDto.getIban());
        bankCoordinates.setBic(mandatInfoDto.getBic());
        ddPaymentMethod.setBankCoordinates(bankCoordinates);
        create(ddPaymentMethod);
    }

    /**
     * Gets the hosted checkout url.
     *
     * @param hostedCheckoutInput the hosted checkout input
     * @return the hosted checkout url
     * @throws BusinessException the business exception
     */
    public String getHostedCheckoutUrl(HostedCheckoutInput hostedCheckoutInput) throws BusinessException {
        CustomerAccount customerAccount = customerAccountService.findByCode(hostedCheckoutInput.getCustomerAccountCode());
        if (customerAccount == null) {
            throw new BusinessException("Can't found CustomerAccount with code:" + hostedCheckoutInput.getCustomerAccountCode());
        }

        if ( ( customerAccount.getTradingCurrency() != null ) && (!StringUtils.isBlank(customerAccount.getTradingCurrency().getCurrencyCode()))) {
            hostedCheckoutInput.setCurrencyCode(customerAccount.getTradingCurrency().getCurrencyCode());
        }
        if ( ( customerAccount.getAddress() != null ) && ( customerAccount.getAddress().getCountry() != null ) && (!StringUtils.isBlank(customerAccount.getAddress().getCountry().getCountryCode()))) {
            hostedCheckoutInput.setCountryCode(customerAccount.getAddress().getCountry().getCountryCode().toLowerCase());
        }
        GatewayPaymentInterface gatewayPaymentInterface = null;
        PaymentGateway matchedPaymentGatewayForTheCA = paymentGatewayService.getPaymentGateway(customerAccount, null, null);
        if (matchedPaymentGatewayForTheCA == null) {
            throw new BusinessException("No payment gateway for customerAccount:" + customerAccount.getCode());
        }
        try {
            gatewayPaymentInterface = gatewayPaymentFactory.getInstance(matchedPaymentGatewayForTheCA);
        } catch (Exception e1) {
            throw new BusinessException("Can't build gatewayPaymentInterface");
        }
        hostedCheckoutInput.setCustomerAccountId(customerAccount.getId());
        String hostedCheckoutUrl = gatewayPaymentInterface.getHostedCheckoutUrl(hostedCheckoutInput);

        return hostedCheckoutUrl;
    }

    public Object getClient(Long customerAccountId) throws BusinessException {
        CustomerAccount customerAccount = customerAccountService.findById(customerAccountId);
        if (customerAccount == null) {
            throw new BusinessException("Can't found CustomerAccount with Id:" + customerAccountId);
        }
        GatewayPaymentInterface gatewayPaymentInterface = null;
        PaymentGateway matchedPaymentGatewayForTheCA = paymentGatewayService.getPaymentGateway(customerAccount, null, null);
        if (matchedPaymentGatewayForTheCA == null) {
                throw new BusinessException("No payment gateway for customerAccount:" + customerAccount.getCode());
            }
        try {
                gatewayPaymentInterface = gatewayPaymentFactory.getInstance(matchedPaymentGatewayForTheCA);
            } catch (Exception e1) {
                throw new BusinessException("Can't build gatewayPaymentInterface");
            }
        return gatewayPaymentInterface.getClientObject();
    }

}