package org.meveo.api.payment;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.CardPaymentMethodDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentMethodService;

@Stateless
public class CardPaymentMethodApi extends BaseApi {

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private PaymentMethodService paymentMethodService;

    public String create(CardPaymentMethodDto cardPaymentMethodDto) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {

        if (StringUtils.isBlank(cardPaymentMethodDto.getCardNumber())) {
            missingParameters.add("cardNumber");
        }
        if (StringUtils.isBlank(cardPaymentMethodDto.getOwner())) {
            missingParameters.add("owner");
        }
        if (StringUtils.isBlank(cardPaymentMethodDto.getMonthExpiration()) || StringUtils.isBlank(cardPaymentMethodDto.getYearExpiration())) {
            missingParameters.add("expiryDate");
        }

        if (StringUtils.isBlank(cardPaymentMethodDto.getCustomerAccountCode())) {
            missingParameters.add("customerAccountCode");
        }
        handleMissingParameters();
        CustomerAccount customerAccount = customerAccountService.findByCode(cardPaymentMethodDto.getCustomerAccountCode());
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, cardPaymentMethodDto.getCustomerAccountCode());
        }

        CardPaymentMethod paymentMethod = new CardPaymentMethod();
        paymentMethod.setCustomerAccount(customerAccount);
        paymentMethod.setAlias(cardPaymentMethodDto.getAlias());
        paymentMethod.setCardNumber(cardPaymentMethodDto.getCardNumber());
        paymentMethod.setOwner(cardPaymentMethodDto.getOwner());
        paymentMethod.setCardType(cardPaymentMethodDto.getCardType());
        paymentMethod.setPreferred(cardPaymentMethodDto.isPreferred());
        paymentMethod.setIssueNumber(cardPaymentMethodDto.getIssueNumber());
        paymentMethod.setYearExpiration(cardPaymentMethodDto.getYearExpiration());
        paymentMethod.setMonthExpiration(cardPaymentMethodDto.getMonthExpiration());
        paymentMethodService.create(paymentMethod);

        return paymentMethod.getTokenId();
    }

    public void update(CardPaymentMethodDto cardPaymentMethodDto) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {

        if (StringUtils.isBlank(cardPaymentMethodDto.getTokenId()) && StringUtils.isBlank(cardPaymentMethodDto.getId())) {
            missingParameters.add("tokenId or id");
        }

        handleMissingParameters();

        CardPaymentMethod cardPaymentMethod = null;
        if (!StringUtils.isBlank(cardPaymentMethodDto.getId())) {
            cardPaymentMethod = (CardPaymentMethod) paymentMethodService.findById(cardPaymentMethodDto.getId());
        }
        if (!StringUtils.isBlank(cardPaymentMethodDto.getTokenId())) {
            cardPaymentMethod = paymentMethodService.findByTokenId(cardPaymentMethodDto.getTokenId());
        }

        if (cardPaymentMethod == null) {
            throw new EntityDoesNotExistsException(CardPaymentMethod.class,
                cardPaymentMethodDto.getTokenId() == null ? "" + cardPaymentMethodDto.getId() : cardPaymentMethodDto.getTokenId());
        }

        if (cardPaymentMethodDto.isPreferred()) {
            cardPaymentMethod.setPreferred(true);
        }

        if (!StringUtils.isBlank(cardPaymentMethodDto.getAlias())) {
            cardPaymentMethod.setAlias(cardPaymentMethodDto.getAlias());
        }
        paymentMethodService.update(cardPaymentMethod);
    }

    public void remove(Long id, String tokenId) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {

        if (id == null && StringUtils.isBlank(tokenId)) {
            missingParameters.add("tokenId or id");
        }

        handleMissingParameters();

        CardPaymentMethod cardPaymentMethod = null;
        if (id != null) {
            cardPaymentMethod = (CardPaymentMethod) paymentMethodService.findById(id);
        }
        if (!StringUtils.isBlank(tokenId)) {
            cardPaymentMethod = paymentMethodService.findByTokenId(tokenId);
        }

        if (cardPaymentMethod == null) {
            throw new EntityDoesNotExistsException(CardPaymentMethod.class, id != null ? "" + id : tokenId);
        }

        paymentMethodService.remove(cardPaymentMethod);
    }

    public List<CardPaymentMethodDto> list(Long customerAccountId, String customerAccountCode) throws MissingParameterException, EntityDoesNotExistsException {

        if (StringUtils.isBlank(customerAccountId) && StringUtils.isBlank(customerAccountCode)) {
            missingParameters.add("customerAccountId or customerAccountCode");
        }

        handleMissingParameters();

        CustomerAccount customerAccount = null;

        if (!StringUtils.isBlank(customerAccountId)) {
            customerAccount = customerAccountService.findById(customerAccountId);
        }
        if (!StringUtils.isBlank(customerAccountCode)) {
            customerAccount = customerAccountService.findByCode(customerAccountCode);
        }

        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountId == null ? customerAccountCode : "" + customerAccountId);
        }

        List<CardPaymentMethodDto> cardPaymentMethodDtos = new ArrayList<CardPaymentMethodDto>();

        for (CardPaymentMethod paymentMethod : customerAccount.getCardPaymentMethods(false)) {
            cardPaymentMethodDtos.add((CardPaymentMethodDto) PaymentMethodDto.toDto(paymentMethod));
        }

        return cardPaymentMethodDtos;
    }

    public CardPaymentMethodDto find(Long id, String tokenId) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {

        if (id == null && StringUtils.isBlank(tokenId)) {
            missingParameters.add("tokenId or id");
        }

        handleMissingParameters();

        CardPaymentMethod cardPaymentMethod = null;
        if (id != null) {
            cardPaymentMethod = (CardPaymentMethod) paymentMethodService.findById(id);
        }
        if (!StringUtils.isBlank(tokenId)) {
            cardPaymentMethod = paymentMethodService.findByTokenId(tokenId);
        }

        if (cardPaymentMethod == null) {
            throw new EntityDoesNotExistsException(CardPaymentMethod.class, id != null ? "" + id : tokenId);
        }

        CardPaymentMethodDto cardTokenDto = (CardPaymentMethodDto) PaymentMethodDto.toDto(cardPaymentMethod);

        return cardTokenDto;
    }
}