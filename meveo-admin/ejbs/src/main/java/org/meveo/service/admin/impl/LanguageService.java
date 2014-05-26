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
package org.meveo.service.admin.impl;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.Language;
import org.meveo.service.base.PersistenceService;

@Stateless
@LocalBean
@Named
public class LanguageService extends PersistenceService<Language> {

	public Language findByCode(String code) {
		if (code == null) {
			return null;
		}
		QueryBuilder qb = new QueryBuilder(Language.class, "c");
		qb.addCriterion("languageCode", "=", code, false);

		try {
			return (Language) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Language findByCode(EntityManager em, String code) {
		if (code == null) {
			return null;
		}
		QueryBuilder qb = new QueryBuilder(Language.class, "c");
		qb.addCriterion("languageCode", "=", code, false);

		try {
			return (Language) qb.getQuery(em).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
