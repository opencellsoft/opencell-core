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

import org.meveo.model.BusinessEntity;
import org.meveo.model.crm.Provider;

public abstract class BusinessService<P extends BusinessEntity> extends PersistenceService<P> {

	@SuppressWarnings("unchecked")
	public P findByCode(String code, Provider provider) {
		log.debug("start of find {} by code (code={}) ..", getEntityClass().getSimpleName(), code);
		final Class<? extends P> productClass = getEntityClass();
		StringBuilder queryString = new StringBuilder("from " + productClass.getName() + " a");
		queryString.append(" where a.code = :code and a.provider=:provider");
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("code", code);
		query.setParameter("provider", provider);
		if (query.getResultList().size() == 0) {
			return null;
		}
		P e = (P) query.getResultList().get(0);
		log.debug("end of find {} by code (code={}). Result found={}.", new Object[] {
				getEntityClass().getSimpleName(), code, e != null });

		return e;
	}

}
