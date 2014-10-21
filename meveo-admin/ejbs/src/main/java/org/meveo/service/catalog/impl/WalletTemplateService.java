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
package org.meveo.service.catalog.impl;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.billing.Tax;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.service.base.PersistenceService;

/**
 * Tax service implementation.
 */
@Stateless
@LocalBean
public class WalletTemplateService extends PersistenceService<WalletTemplate> {

	public Tax findByCode(EntityManager em, String code) {
		try {
			QueryBuilder qb = new QueryBuilder(WalletTemplate.class, "t");
			qb.addCriterion("code", "=", code, false);
			return (Tax) qb.getQuery(em).getSingleResult();
		} catch (NoResultException ne) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<WalletTemplate> findStartsWithCode(EntityManager em, String walletId,BillingWalletTypeEnum walletType) {
		try {
			QueryBuilder qb = new QueryBuilder(WalletTemplate.class, "t");
			qb.like("code", walletId, 1, false);
			if(walletType!=null){
				qb.addCriterionEnum("walletType", walletType);
			}
			return (List<WalletTemplate>) qb.getQuery(em).getResultList();
		} catch (NoResultException ne) {
			return null;
		}
	}
}
