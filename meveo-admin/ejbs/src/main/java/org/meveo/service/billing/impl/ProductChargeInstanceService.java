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
package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.NumberUtil;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.billing.ProductChargeInstance;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.service.base.BusinessService;

@Stateless
public class ProductChargeInstanceService extends BusinessService<ProductChargeInstance> {

	@EJB
	private WalletService walletService;

	@EJB
	private WalletOperationService walletOperationService;
	

	public ProductChargeInstance findByCodeAndSubsription(String code, Long userAccountId) {
		ProductChargeInstance productChargeInstance = null;
		try {
			log.debug("start of find {} by code (code={}, userAccountId={}) ..", new Object[] {
					"ProductChargeInstance", code, userAccountId });
			QueryBuilder qb = new QueryBuilder(ProductChargeInstance.class, "c");
			qb.addCriterion("c.code", "=", code, true);
			qb.addCriterion("c.userAccount.id", "=", userAccountId, true);
			productChargeInstance = (ProductChargeInstance) qb.getQuery(getEntityManager()).getSingleResult();
			log.debug("end of find {} by code (code={}, userAccountId={}). Result found={}.", new Object[] {
					"ProductChargeInstance", code, userAccountId, productChargeInstance != null });
		} catch (NoResultException nre) {
			log.debug("findByCodeAndSubsription : aucune charge ponctuelle n'a ete trouvee");
		} catch (Exception e) {
			log.error("failed to find productChargeInstance by Code and subsription",e);
		}
		return productChargeInstance;
	}

	public List<WalletOperation> applyProductChargeInstance(ProductChargeInstance productChargeInstance,User user,boolean persist) throws BusinessException {
		List<WalletOperation> result=null;
		ChargeTemplate chargeTemplate=productChargeInstance.getProductChargeTemplate();
		List<WalletTemplate> walletTemplates = productChargeInstance.getProductInstance().getProductTemplate().getWalletTemplates();
		productChargeInstance.setPrepaid(false);
		if (walletTemplates != null && walletTemplates.size() > 0) {
			log.debug("found {} wallets",walletTemplates.size());
			for (WalletTemplate walletTemplate : walletTemplates) {
				log.debug("walletTemplate {}",walletTemplate.getCode());
				if(walletTemplate.getWalletType()==BillingWalletTypeEnum.PREPAID){
					log.debug("this wallet is prepaid, we set the charge instance itself as being prepaid");
					productChargeInstance.setPrepaid(true);

				}
				WalletInstance walletInstance=walletService.getWalletInstance(productChargeInstance.getUserAccount(),
						walletTemplate, user);
				log.debug("add the wallet instance {} to the chargeInstance {}",walletInstance.getId(),productChargeInstance.getId());
				productChargeInstance.getWalletInstances().add(walletInstance);
			}
		} else {
			log.debug("as the charge is postpaid, we add the principal wallet");
			productChargeInstance.getWalletInstances().add(
					productChargeInstance.getUserAccount().getWallet());
		}
		BigDecimal inputQuantity = productChargeInstance.getQuantity();
		BigDecimal quantity = NumberUtil.getInChargeUnit(productChargeInstance.getQuantity(), chargeTemplate.getUnitMultiplicator(), chargeTemplate.getUnitNbDecimal(),
				chargeTemplate.getRoundingMode());
		WalletOperation walletOperation =  walletOperationService.rateProductApplication(productChargeInstance, inputQuantity, quantity, user);
		if(persist){
			result=walletOperationService.chargeWalletOperation(walletOperation, user);
		} else {
			result = new ArrayList<>();
			result.add(walletOperation);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<ProductChargeInstance> findBySubscriptionId(Long subscriptionId) {
		QueryBuilder qb = new QueryBuilder(ProductChargeInstance.class, "c", Arrays.asList("chargeTemplate"), null);
		qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
		return qb.getQuery(getEntityManager()).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ProductChargeInstance> findByUserAccountId(Long userAccountId) {
		QueryBuilder qb = new QueryBuilder(ProductChargeInstance.class, "c", Arrays.asList("chargeTemplate"), null);
		qb.addCriterion("c.userAccount.id", "=", userAccountId, true);
		qb.addSql("c.subscription is null");
		return qb.getQuery(getEntityManager()).getResultList();
	}
}
