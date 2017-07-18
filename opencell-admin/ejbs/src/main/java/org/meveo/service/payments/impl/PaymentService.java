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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.api.dto.payment.DoPaymentResponseDto;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Country;
import org.meveo.model.payments.CardToken;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.base.PersistenceService;

/**
 * Payment service implementation.
 */
@Stateless
public class PaymentService extends PersistenceService<Payment> {

	@Inject
	private CardTokenService cardTokenService;

	@Inject
	private OCCTemplateService oCCTemplateService;

	@Inject
	private MatchingCodeService matchingCodeService;
	
	@Inject
	private CountryService countryService;
	
	@Inject
	private GatewayPaymentFactory gatewayPaymentFactory;
	
	@MeveoAudit
	@Override
	public void create(Payment entity) throws BusinessException {
		super.create(entity);
	}

	public DoPaymentResponseDto doPaymentCardToken(CustomerAccount customerAccount, Long ctsAmount, List<Long> aoIdsToPay,boolean createAO,boolean matchingAO) throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
		if(customerAccount.getPaymentTokens() == null || customerAccount.getPaymentTokens().isEmpty()){
			throw new BusinessException("There no payment token for customerAccount:"+customerAccount.getCode());
		}
		if(!PaymentMethodEnum.CARD.name().equals(customerAccount.getPaymentMethod().name())){
			throw new BusinessException("Unsupported payment method:"+customerAccount.getPaymentMethod());
		}
		GatewayPaymentInterface  gatewayPaymentInterface = gatewayPaymentFactory.getInstance(GatewayPaymentNamesEnum.valueOf(ParamBean.getInstance().getProperty("meveo.gatewayPayment", "CUSTOM_API")));		

		DoPaymentResponseDto doPaymentResponseDto =  gatewayPaymentInterface.doPaymentToken(cardTokenService.getPreferedToken(customerAccount), ctsAmount,null);

		if(PaymentStatusEnum.ACCEPTED.name().equals(doPaymentResponseDto.getPaymentStatus().name())){
			Long aoPaymentId = null;
			if(createAO){
				try{
					aoPaymentId = createPaymentAO(customerAccount, ctsAmount, doPaymentResponseDto);
					doPaymentResponseDto.setAoCreated(true);
				}catch (Exception e) {
					log.warn("Cant create Account operation payment :",e);
				}
				if(matchingAO){
					try{
						 List<Long> aoIdsToMatch = aoIdsToPay;						 
						 aoIdsToMatch.add(aoPaymentId);
						 matchingCodeService.matchOperations(null, customerAccount.getCode(), aoIdsToMatch, null, MatchingTypeEnum.A);			
						doPaymentResponseDto.setMatchingCreated(true);
					}catch (Exception e) {
						log.warn("Cant create matching :",e);
					}
				}
			}
		}

		return doPaymentResponseDto;
	}

	/**
	 * 
	 * @param customerAccount
	 * @param ctsAmount
	 * @param invoice
	 * @param cardNumber
	 * @param ownerName
	 * @param cvv
	 * @param expirayDate format :MMyy
	 * @return
	 * @throws BusinessException
	 * @throws UnbalanceAmountException 
	 * @throws NoAllOperationUnmatchedException 
	 */
	public DoPaymentResponseDto doPaymentCard(CustomerAccount customerAccount, Long ctsAmount,String cardNumber,String ownerName, String cvv,String expirayDate,
			CreditCardTypeEnum cardType, List<Long> aoIdsToPay,boolean createAO,boolean matchingAO ) throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
		GatewayPaymentInterface  gatewayPaymentInterface = gatewayPaymentFactory.getInstance(GatewayPaymentNamesEnum.valueOf(ParamBean.getInstance().getProperty("meveo.gatewayPayment", "INGENICO_GC")));		
		if(!PaymentMethodEnum.CARD.name().equals(customerAccount.getPaymentMethod().name())){
			throw new BusinessException("Unsupported payment method:"+customerAccount.getPaymentMethod());
		}				
		String coutryCode = null;
		Country country = countryService.findByName(customerAccount.getAddress() != null ? customerAccount.getAddress().getCountry() : null);
		if(country != null){
			coutryCode = country.getCountryCode();
		}	
		DoPaymentResponseDto doPaymentResponseDto =  gatewayPaymentInterface.doPaymentCard(customerAccount, ctsAmount, cardNumber, ownerName,  cvv, expirayDate,cardType,coutryCode,null);		
		
		if(PaymentStatusEnum.ACCEPTED.name().equals(doPaymentResponseDto.getPaymentStatus().name())){
			CardToken cardToken = new CardToken(); 
			cardToken.setAlias("Card_"+cardNumber.substring(12, 16));
			cardToken.setCardNumber(cardNumber);
			cardToken.setCardType(cardType);
			cardToken.setCustomerAccount(customerAccount);
			cardToken.setDefault(true);		
			cardToken.setHiddenCardNumber(StringUtils.hideCardNumber(cardNumber));
			cardToken.setMonthExpiration(new Integer(expirayDate.substring(0, 2)));
			cardToken.setYearExpiration(new Integer(expirayDate.substring(2, 4)));
			cardToken.setOwner(ownerName);
			cardToken.setTokenId(doPaymentResponseDto.getTokenId());
			cardTokenService.create(cardToken);	
			Long aoPaymentId = null;
			if(createAO){
				try{
					aoPaymentId = createPaymentAO(customerAccount, ctsAmount, doPaymentResponseDto);
					doPaymentResponseDto.setAoCreated(true);
				}catch (Exception e) {
					log.warn("Cant create Account operation payment :"+e.getMessage());
				}
				if(matchingAO){
					try{
						 List<Long> aoIdsToMatch = aoIdsToPay;						 
						 aoIdsToMatch.add(aoPaymentId);
						matchingCodeService.matchOperations(null, customerAccount.getCode(), aoIdsToMatch, null, MatchingTypeEnum.A);							
						doPaymentResponseDto.setMatchingCreated(true);
					}catch (Exception e) {
						log.warn("Cant create matching :"+e.getMessage());
					}
				}
			}			
		}
		return doPaymentResponseDto;
	}

/**
 * 
 * @param customerAccount
 * @param ctsAmount
 * @param paymentID
 * @return the AO id created
 * @throws BusinessException
 */
	public Long createPaymentAO(CustomerAccount customerAccount,Long ctsAmount,DoPaymentResponseDto doPaymentResponseDto) throws BusinessException {
		OCCTemplate occTemplate = oCCTemplateService.findByCode(ParamBean.getInstance().getProperty("occ.payment.card", "RG_CARD"));
		if (occTemplate == null) {
			throw new BusinessException("Cannot find OCC Template with code=" + (ParamBean.getInstance().getProperty("occ.payment.card", "RG_CARD")));
		}
		Payment payment = new Payment();
		payment.setPaymentMethod(customerAccount.getPaymentMethod());
		payment.setAmount((new BigDecimal(ctsAmount).divide(new BigDecimal(100))));
		payment.setUnMatchingAmount(payment.getAmount());
		payment.setMatchingAmount(BigDecimal.ZERO);
		payment.setAccountCode(occTemplate.getAccountCode());
		payment.setOccCode(occTemplate.getCode());
		payment.setOccDescription(occTemplate.getDescription());
		payment.setType(doPaymentResponseDto.getPaymentBrand());
		payment.setTransactionCategory(occTemplate.getOccCategory());
		payment.setAccountCodeClientSide(doPaymentResponseDto.getCodeClientSide());
		payment.setCustomerAccount(customerAccount);
		payment.setReference(doPaymentResponseDto.getPaymentID());				
		payment.setTransactionDate(new Date());
		payment.setMatchingStatus(MatchingStatusEnum.O);	
		payment.setBankReference(doPaymentResponseDto.getBankRefenrence());		
		create(payment);
		return payment.getId();

	}
	
}
