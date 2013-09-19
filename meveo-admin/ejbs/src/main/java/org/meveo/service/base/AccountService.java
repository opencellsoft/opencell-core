/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
