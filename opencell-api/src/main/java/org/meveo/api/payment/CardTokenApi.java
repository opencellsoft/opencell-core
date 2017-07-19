package org.meveo.api.payment;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.CardTokenDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CardToken;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentToken;
import org.meveo.service.payments.impl.CardTokenService;
import org.meveo.service.payments.impl.CustomerAccountService;

@Stateless
public class CardTokenApi extends BaseApi {
	
	@Inject
	private CustomerAccountService customerAccountService;
	
	@Inject
	private CardTokenService cardTokenService;
	
	public String create(CardTokenDto cardTokenRequestDto) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException{
		if(cardTokenRequestDto == null){
			throw new InvalidParameterException("CardTokenRequestDto","cardTokenRequestDto");
		}
		if(StringUtils.isBlank(cardTokenRequestDto.getCardNumber())){
			missingParameters.add("CardNumber");
		}
		if(StringUtils.isBlank(cardTokenRequestDto.getOwner())){
			missingParameters.add("Owner");
		}
		if(StringUtils.isBlank(cardTokenRequestDto.getMonthExpiration()) || StringUtils.isBlank(cardTokenRequestDto.getYearExpiration())){
			missingParameters.add("ExpiryDate");
		}

		if(StringUtils.isBlank(cardTokenRequestDto.getCustomerAccountCode())){
			missingParameters.add("CustomerAccountCode");
		}
		handleMissingParameters();
		CustomerAccount customerAccount = customerAccountService.findByCode(cardTokenRequestDto.getCustomerAccountCode());
		if(customerAccount == null){
			throw new EntityDoesNotExistsException(CustomerAccount.class, cardTokenRequestDto.getCustomerAccountCode());
		}

		CardToken cardToken = new CardToken();
		cardToken.setCustomerAccount(customerAccount);
		cardToken.setAlias(cardTokenRequestDto.getAlias());
		cardToken.setCardNumber(cardTokenRequestDto.getCardNumber());
		cardToken.setOwner(cardTokenRequestDto.getOwner());
		cardToken.setCardType(cardTokenRequestDto.getCardType());
		cardToken.setIsDefault(cardTokenRequestDto.getIsDefault());
		cardToken.setIssueNumber(cardTokenRequestDto.getIssueNumber());
		cardToken.setYearExpiration(cardTokenRequestDto.getYearExpiration());
		cardToken.setMonthExpiration(cardTokenRequestDto.getMonthExpiration());		
		cardToken.setHiddenCardNumber(StringUtils.hideCardNumber(cardTokenRequestDto.getCardNumber()) );
		cardToken.setTokenId(cardTokenRequestDto.getTokenId());
		cardTokenService.create(cardToken);
		return cardToken.getTokenId();
	}
	
	public void update(CardTokenDto cardTokenRequestDto) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException{
		if(cardTokenRequestDto == null){
			throw new InvalidParameterException("CardTokenRequestDto","cardTokenRequestDto");
		}
		if(StringUtils.isBlank(cardTokenRequestDto.getTokenId()) && StringUtils.isBlank(cardTokenRequestDto.getId())){
			missingParameters.add("TokenId or ID");
		}
		
		handleMissingParameters();
		CardToken cardToken = null;
		if(!StringUtils.isBlank(cardTokenRequestDto.getId())){
			cardToken = cardTokenService.findById(cardTokenRequestDto.getId());
		}
		if(!StringUtils.isBlank(cardTokenRequestDto.getTokenId())){
			cardTokenService.findByTokenId(cardTokenRequestDto.getTokenId());
		}
		
		if(cardToken == null){
			throw new EntityDoesNotExistsException(CardToken.class, cardTokenRequestDto.getTokenId() == null ? ""+cardTokenRequestDto.getId() : cardTokenRequestDto.getTokenId());
		}
		if(!StringUtils.isBlank(cardTokenRequestDto.getIsDefault())){
			cardToken.setIsDefault(cardTokenRequestDto.getIsDefault());
		}
		if(!StringUtils.isBlank(cardTokenRequestDto.getAlias())){
			cardToken.setAlias(cardTokenRequestDto.getAlias());
		}
		if(!StringUtils.isBlank(cardTokenRequestDto.getIsDisabled())){
			cardToken.setDisabled(cardTokenRequestDto.getIsDisabled());
		}
		cardTokenService.update(cardToken);		
	}
	
	public void remove(Long cardTokenId) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException{
		if(StringUtils.isBlank(cardTokenId)){
			missingParameters.add("TokenId");
		}
		
		handleMissingParameters();
		CardToken cardToken = cardTokenService.findById(cardTokenId);
		if(cardToken == null){
			throw new EntityDoesNotExistsException(CardToken.class, cardTokenId);
		}
		
		cardTokenService.remove(cardToken);		
	}

	public List<CardTokenDto> list(Long customerAccountId,String customerAccountCode) throws MissingParameterException, EntityDoesNotExistsException {
		if(StringUtils.isBlank(customerAccountId) && StringUtils.isBlank(customerAccountCode)){
			missingParameters.add("customerAccountId or customerAccountCode");
		}
		handleMissingParameters();
		CustomerAccount customerAccount = null;
			if(!StringUtils.isBlank(customerAccountId)){
				customerAccount = customerAccountService.findById(customerAccountId);	
			}
			if(!StringUtils.isBlank(customerAccountCode)){
				customerAccount = customerAccountService.findByCode(customerAccountCode);	
			}
				
				
		if(customerAccount == null){
			throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountId == null ? customerAccountCode : ""+customerAccountId );
		}		
		List<CardTokenDto> listCardToken = new ArrayList<CardTokenDto>();
		
		for(PaymentToken token : customerAccount.getPaymentTokens()){			
			CardTokenDto cardTokenDto = new CardTokenDto(token);
			listCardToken.add(cardTokenDto);
		}		
		return listCardToken;
	}
	
	public CardTokenDto find(Long cardTokenId) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException{
		if(StringUtils.isBlank(cardTokenId)){
			missingParameters.add("TokenId");
		}
		
		handleMissingParameters();
		CardToken cardToken = cardTokenService.findById(cardTokenId);
		if(cardToken == null){
			throw new EntityDoesNotExistsException(CardToken.class, cardTokenId);
		}
		CardTokenDto cardTokenDto = new CardTokenDto(cardToken);
		return cardTokenDto;
		
	}
}
