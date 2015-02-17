/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.billing.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

/**
 * Wallet service implementation.
 * 
 */
@Stateless
public class WalletService extends PersistenceService<WalletInstance> {

	public WalletInstance findByUserAccount(UserAccount userAccount) {
		return findByUserAccount(getEntityManager(), userAccount);
	}

	public WalletInstance findByUserAccount(EntityManager em, UserAccount userAccount) {
		QueryBuilder qb = new QueryBuilder(WalletInstance.class, "w");
		try {
			qb.addCriterionEntity("userAccount", userAccount);

			return (WalletInstance) qb.getQuery(em).getSingleResult();
		} catch (NoResultException e) {
			log.warn(e.getMessage());
			return null;
		}
	}

	public WalletInstance getWalletInstance(UserAccount userAccount, WalletTemplate walletTemplate, User creator,
			Provider provider) {
		String walletCode = walletTemplate.getCode();
		log.debug("get wallet instance for userAccount {} and wallet template {}",userAccount.getCode(),walletCode);
		if (!WalletTemplate.PRINCIPAL.equals(walletCode)) {
			if (!userAccount.getPrepaidWallets().containsKey(walletCode)) {
				WalletInstance wallet = new WalletInstance();
				wallet.setCode(walletCode);
				wallet.setWalletTemplate(walletTemplate);
				wallet.setUserAccount(userAccount);
				create(wallet, creator, provider);
				log.debug("add prepaid wallet {} to useraccount {}",walletCode,userAccount.getCode());
				userAccount.getPrepaidWallets().put(walletCode, wallet);
				getEntityManager().merge(userAccount);
			}
			
			return userAccount.getPrepaidWallets().get(walletCode);
		} else {
			log.debug("return the Principal wallet instance {}",userAccount.getWallet().getId());
			return userAccount.getWallet();
		}
	}

	public List<WalletInstance> getWalletsToMatch(Date date){
		return getEntityManager().createNamedQuery(
				"WalletInstance.listPrepaidWalletsToMatch", WalletInstance.class).setParameter("matchingDate", date).getResultList();
	}

}
