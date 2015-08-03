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
package org.meveo.service.crm.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.UsernameAlreadyExistsException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.security.Role;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.PersistenceService;

/**
 * Provider service implementation.
 */
@Stateless
public class ProviderService extends PersistenceService<Provider> {
	@Inject
	private UserService userService;

	@Inject
	private RoleService roleService;

	private static ParamBean paramBean = ParamBean.getInstance();

	public Provider findByCode(String code) {
		return findByCode(getEntityManager(), code);
	}

	public Provider findByCode(EntityManager em, String code) {
		try {
			return (Provider) em
					.createQuery(
							"from " + Provider.class.getSimpleName()
									+ " where code=:code")
					.setParameter("code", code).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Set<Provider> findUsersProviders(String userName) {
		User user = userService.findByUsername(userName);
		if (user != null) {
			return user.getProviders();
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Provider> getProviders() {
		List<Provider> providers = (List<Provider>) getEntityManager()
				.createQuery("from " + Provider.class.getSimpleName())
				.getResultList();
		return providers;
	}


    public void create(Provider e) throws UsernameAlreadyExistsException, BusinessException {
        log.info("start of create provider");
        super.create(e);
        log.info("created provider id={}. creating default user", e.getId());
        
        User user = new User();
        user.setProvider(e);
        Set<Provider> providers = new HashSet<Provider>();
        providers.add(e);
        user.setProviders(providers);
        user.setPassword(e.getCode() + ".password");
        user.setUserName(e.getCode() + ".ADMIN");
        Role role = roleService.findById(Long.parseLong(paramBean.getProperty("systgetEntityManager().adminRoleid", "1")));
        Set<Role> roles = new HashSet<Role>();
        roles.add(role);
        user.setRoles(roles);
        userService.create(user);
        
        log.info("created default user id={}.", user.getId());
    }

	public Provider findByCodeWithFetch(String code, List<String> fetchFields) {
		QueryBuilder qb = new QueryBuilder(Provider.class, "p", fetchFields,
				null);

		qb.addCriterion("p.code", "=", code, true);

		try {
			return (Provider) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
