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
package org.meveo.service.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.ScriptInstance;
import org.meveo.model.jobs.ScriptTypeEnum;
import org.meveo.script.JavaCompilerManager;
import org.meveo.script.ScriptInterface;
import org.meveo.service.base.PersistenceService;

@Stateless
public class ScriptInstanceService extends PersistenceService<ScriptInstance> {

	@EJB
	private JavaCompilerManager javaCompilerManager;

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
		QueryBuilder qb = new QueryBuilder(ScriptInstance.class, "t", null, provider);
		qb.addCriterionWildcard("t.code", code, true);
		try {
			return (ScriptInstance) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public void executeScriptOnObject(ScriptInstance scriptInstance, Object o) throws BusinessException {
		log.debug("execute script={} on object={}", scriptInstance.getScript(), o);
		ScriptInterface scriptInterface = javaCompilerManager.getAllScriptInterfaces().get(scriptInstance.getCode());
		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("obj", o);
		scriptInterface.execute(userMap);
	}

}