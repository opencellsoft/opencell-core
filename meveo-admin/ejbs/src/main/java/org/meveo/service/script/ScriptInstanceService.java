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
package org.meveo.service.script;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.scripts.ScriptInstanceError;
import org.meveo.model.scripts.ScriptTypeEnum;
import org.meveo.service.base.PersistenceService;

@Stateless
public class ScriptInstanceService extends PersistenceService<ScriptInstance> {

	@Inject
	ScriptInstanceErrorService scriptInstanceErrorService;

	@SuppressWarnings("unchecked")
	public List<ScriptInstance> findByType(ScriptTypeEnum type) {
		List<ScriptInstance> result = new ArrayList<ScriptInstance>();
		QueryBuilder qb = new QueryBuilder(ScriptInstance.class, "t");
		qb.addCriterionEnum("t.scriptTypeEnum", type);
		try {
			result = (List<ScriptInstance>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {

		}
		return result;
	}

	public ScriptInstance findByCode(String code, Provider provider) {
		log.debug("find ScriptInstance by code {}",code);
		QueryBuilder qb = new QueryBuilder(ScriptInstance.class, "t", null, provider);
		qb.addCriterionWildcard("t.code", code, true);
		try {
			return (ScriptInstance) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public void removeErrors(ScriptInstance scriptInstance) {
	 getEntityManager().createQuery("delete from ScriptInstanceError o where o.scriptInstance=:scriptInstance")
				.setParameter("scriptInstance", scriptInstance)
				.executeUpdate();
	}

	public List<ScriptInstance> getScriptInstancesWithError(Provider provider) {
		return ((List<ScriptInstance>) getEntityManager().createNamedQuery("ScriptInstance.getScriptInstanceOnError", ScriptInstance.class)
				.setParameter("isError", Boolean.TRUE)
				.setParameter("provider", provider)
				.getResultList());
	}

	public long countScriptInstancesWithError(Provider provider) {
		return ((Long) getEntityManager().createNamedQuery("ScriptInstance.countScriptInstanceOnError", Long.class)
				.setParameter("isError", Boolean.TRUE)
				.setParameter("provider", provider)
				.getSingleResult());
	}

}