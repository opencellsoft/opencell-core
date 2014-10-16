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

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.Title;
import org.meveo.service.base.PersistenceService;

/**
 * Title service implementation.
 */
@Stateless
@LocalBean
public class TitleService extends PersistenceService<Title> {

	public Title findByCode(Provider provider, String code) {
		Title title = null;
		if (StringUtils.isBlank(code)) {
			return null;
		}
		try {
			title = (Title) getEntityManager()
					.createQuery(
							"from Title t where t.code=:code and t.provider=:provider")
					.setParameter("code", code)
					.setParameter("provider", provider).getSingleResult();
		} catch (Exception e) {
			return null;
		}
		return title;
	}

	public Title findByCode(EntityManager em, Provider provider, String code) {
		Title title = null;
		if (StringUtils.isBlank(code)) {
			return null;
		}
		try {
			title = (Title) em
					.createQuery(
							"from Title t where t.code=:code and t.provider=:provider")
					.setParameter("code", code)
					.setParameter("provider", provider).getSingleResult();
		} catch (Exception e) {
			return null;
		}
		return title;
	}

}
