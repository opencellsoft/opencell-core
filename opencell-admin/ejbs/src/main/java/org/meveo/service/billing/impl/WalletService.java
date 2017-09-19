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
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.WalletCacheContainerProvider;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.service.base.PersistenceService;

/**
 * Wallet service implementation.
 * 
 */
@Stateless
public class WalletService extends PersistenceService<WalletInstance> {

	@Inject
	private WalletCacheContainerProvider walletCacheContainerProvider;

	@Override
	public void create(WalletInstance walletInstance) throws BusinessException {
		super.create(walletInstance);
		walletCacheContainerProvider.updateBalanceCache(walletInstance);
	}

	public WalletInstance findByUserAccount(UserAccount userAccount) {
	
		QueryBuilder qb = new QueryBuilder(WalletInstance.class, "w");
		try {
			qb.addCriterionEntity("userAccount", userAccount);

			return (WalletInstance) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			log.warn("failed to find walletInstance by user account",e);
			return null;
		}
	}

	public WalletInstance findByUserAccountAndCode(UserAccount userAccount, String code) {
		QueryBuilder qb = new QueryBuilder(WalletInstance.class, "w");
		try {
			qb.addCriterionEntity("userAccount", userAccount);
			qb.addCriterion("w.code", "=", code, true);

			return (WalletInstance) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			log.warn("failed to find walletInstance by user account And code",e);
			return null;
		}
	}

	public WalletInstance getWalletInstance(UserAccount userAccount, WalletTemplate walletTemplate, boolean isVirtual) throws BusinessException {
		String walletCode = walletTemplate.getCode();
		log.debug("get wallet instance for userAccount {} and wallet template {}", userAccount.getCode(), walletCode);
		if (!WalletTemplate.PRINCIPAL.equals(walletCode)) {
			if (!userAccount.getPrepaidWallets().containsKey(walletCode)) {
				WalletInstance wallet = new WalletInstance();
				wallet.setCode(walletCode);
				wallet.setWalletTemplate(walletTemplate);
				wallet.setUserAccount(userAccount);
                
				if (!isVirtual) {
                    create(wallet);
                }
				
				log.debug("add prepaid wallet {} to useraccount {}", walletCode, userAccount.getCode());
				userAccount.getPrepaidWallets().put(walletCode, wallet);

                if (!isVirtual) {
                    getEntityManager().merge(userAccount);
                }
			}

			return userAccount.getPrepaidWallets().get(walletCode);
		} else {
			log.debug("return the Principal wallet instance {}", userAccount.getWallet().getId());
			return userAccount.getWallet();
		}
	}

	public List<WalletInstance> getWalletsToMatch(Date date) {
		return getEntityManager().createNamedQuery("WalletInstance.listPrepaidWalletsToMatch", WalletInstance.class).setParameter("matchingDate", date).getResultList();
	}

	/**
	 * Get a list of prepaid and active wallet ids (user account is active) to populate a cache
	 * 
	 * @return A list of prepaid and active wallet ids
	 */
	public List<Long> getWalletsIdsForCache() {
		return getEntityManager().createNamedQuery("WalletInstance.listPrepaidActiveWalletIds", Long.class).getResultList();
	}

	/**
	 * Get balance amount for a give wallet instance
	 * 
	 * @param walletInstanceId
	 *            Wallet instance id
	 * @return Wallet balance amount
	 */
	public BigDecimal getWalletBalance(Long walletInstanceId) {
		return getEntityManager().createNamedQuery("WalletOperation.getBalance", BigDecimal.class).setParameter("walletId", walletInstanceId).getSingleResult();
	}

	/**
	 * Get reserved balance amount for a give wallet instance
	 * 
	 * @param walletInstanceId
	 *            Wallet instance id
	 * @return Wallet's reserved balance amount
	 */
	public BigDecimal getWalletReservedBalance(Long walletInstanceId) {
		return getEntityManager().createNamedQuery("WalletOperation.getReservedBalance", BigDecimal.class).setParameter("walletId", walletInstanceId).getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<WalletInstance> findByWalletTemplate(WalletTemplate walletTemplate){
		QueryBuilder qb=new QueryBuilder(WalletInstance.class,"w");
		qb.addCriterionEntity("walletTemplate", walletTemplate);
		return qb.find(getEntityManager());
	}
}