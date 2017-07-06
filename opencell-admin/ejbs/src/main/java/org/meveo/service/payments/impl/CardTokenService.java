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
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Country;
import org.meveo.model.payments.CardToken;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.base.PersistenceService;

/**
 * CardToken service implementation.
 */
@Stateless
public class CardTokenService extends PersistenceService<CardToken> {

	@Inject
	private CountryService countryService;
	
	@Inject
	private GatewayPaymentFactory gatewayPaymentFactory;

	@Override
	public void create(CardToken cardToken) throws BusinessException{

		if(StringUtils.isBlank(cardToken.getTokenId())){	
			String coutryCode = null;
			Country country = countryService.findByName(cardToken.getCustomerAccount().getAddress() != null ? cardToken.getCustomerAccount().getAddress().getCountry() : null);
			if(country != null){
				coutryCode = country.getCountryCode();
			}	
			GatewayPaymentInterface  gatewayPaymentInterface = gatewayPaymentFactory.getInstance(GatewayPaymentNamesEnum.valueOf(ParamBean.getInstance().getProperty("meveo.gatewayPayment", "CUSTOM_API")));
			String tockenID = gatewayPaymentInterface.createCardToken(cardToken.getCustomerAccount(), cardToken.getAlias(), cardToken.getCardNumber(), cardToken.getOwner(),
					StringUtils.getLongAsNChar(cardToken.getMonthExpiration(), 2)+StringUtils.getLongAsNChar(cardToken.getYearExpiration(),2), cardToken.getIssueNumber(),cardToken.getCardType().getId(),coutryCode);			
			cardToken.setTokenId(tockenID);
		}
		super.create(cardToken);
		if(cardToken.getIsDefault()){			
			getEntityManager().createNamedQuery("CardToken.updateDefaultToken").setParameter("defaultOne", cardToken.getTokenId()).executeUpdate();
		}
	}

	/**
	 * Return the default token, if expired return other valid token
	 * @param customerAccount
	 * @return
	 * @throws BusinessException
	 */
	public CardToken getPreferedToken(CustomerAccount customerAccount) throws BusinessException{
		CardToken cardToken = null;
		try{
			cardToken = (CardToken) getEntityManager().createNamedQuery("CardToken.getDefaultToken")
					.setParameter("monthExpiration", DateUtils.getMonthFromDate(new Date()))
					.setParameter("yearExpiration", new Integer((""+DateUtils.getYearFromDate(new Date())).substring(2, 3)))
					.getSingleResult();
		}catch (Exception e) {
		}

		if(cardToken == null){
			try{
				cardToken = (CardToken) getEntityManager().createNamedQuery("CardToken.getAvailableToken")
						.setParameter("monthExpiration", DateUtils.getMonthFromDate(new Date()))
						.setParameter("yearExpiration", new Integer((""+DateUtils.getYearFromDate(new Date())).substring(2, 3)))
						.getSingleResult();
			}catch (Exception e) {
			}
		}
		if(cardToken == null){
			throw new BusinessException("There no valid token for customerAccount:"+customerAccount.getCode());
		}
		return cardToken;
	}
}
