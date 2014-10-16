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
package org.meveo.service.base;

import javax.persistence.Query;

import org.meveo.model.AccountEntity;

public abstract class AccountService<P extends AccountEntity> extends BusinessService<P> {

	@SuppressWarnings("unchecked")
	public P findByExternalRef1(String externalRef1) {
		log.debug("start of find {} by externalRef1 (externalRef1={}) ..", getEntityClass()
				.getSimpleName(), externalRef1);
		final Class<? extends P> productClass = getEntityClass();
		StringBuilder queryString = new StringBuilder("from " + productClass.getName() + " a");
		queryString.append(" where a.externalRef1 = :externalRef1");
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("externalRef1", externalRef1);
		if (query.getResultList().size() == 0) {
			return null;
		}
		P e = (P) query.getResultList().get(0);
		log.debug("end of find {} by externalRef1 (externalRef1={}). Result found={}.",
				new Object[] { getEntityClass().getSimpleName(), externalRef1, e != null });
		return e;
	}

}
