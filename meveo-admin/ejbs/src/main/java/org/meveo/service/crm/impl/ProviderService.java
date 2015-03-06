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

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.admin.util.security.Sha1Encrypt;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.Auditable;
import org.meveo.model.AuditableEntity;
import org.meveo.model.BaseEntity;
import org.meveo.model.IEntity;
import org.meveo.model.IdentifiableEnum;
import org.meveo.model.UniqueEntity;
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

	@SuppressWarnings("unchecked")
	@Override
	public List<Provider> list() {
		Query query = getEntityManager().createQuery(
				"FROM " + Provider.class.getName());
		return (List<Provider>) query.getResultList();
	}

	public long count(PaginationConfiguration config) {
		List<String> fetchFields = config.getFetchFields();
		config.setFetchFields(null);
		QueryBuilder queryBuilder = getQuery(config);
		config.setFetchFields(fetchFields);
		return queryBuilder.count(getEntityManager());
	}

	@SuppressWarnings({ "unchecked" })
	public List<Provider> list(PaginationConfiguration config) {
		QueryBuilder queryBuilder = getQuery(config);
		Query query = queryBuilder.getQuery(getEntityManager());
		return query.getResultList();
	}

	public Provider update(Provider e) {
		((AuditableEntity) e).updateAudit(getCurrentUser());
		e = getEntityManager().merge(e);
		log.info("updated provider");
		return e;
	}

	public void create(Provider e) {
		log.info("start of create provider");
		if (e instanceof AuditableEntity) {
			((AuditableEntity) e).updateAudit(getCurrentUser());
		}
		e.setProvider(null);
		getEntityManager().persist(e);
		log.info("created provider id={}. creating default user", e.getId());
		User user = new User();
		user.setProvider(e);
		Set<Provider> providers = new HashSet<Provider>();
		providers.add(e);
		user.setProviders(providers);
		user.setActive(true);
		Auditable au = new Auditable();
		au.setCreated(new Date());
		au.setCreator(getCurrentUser());
		user.setAuditable(au);
		user.setDisabled(false);
		user.setLastPasswordModification(new Date());
		user.setPassword(Sha1Encrypt.encodePassword(e.getCode() + ".password"));
		Role role = roleService.findById(Long.parseLong(paramBean.getProperty(
				"systgetEntityManager().adminRoleid", "1")));
		Set<Role> roles = new HashSet<Role>();
		roles.add(role);
		user.setRoles(roles);
		user.setUserName(e.getCode() + ".ADMIN");
		getEntityManager().persist(user);
		log.info("created default user id={}.", user.getId());
	}

	@SuppressWarnings("rawtypes")
	public QueryBuilder getQuery(PaginationConfiguration config) {

		QueryBuilder queryBuilder = new QueryBuilder(Provider.class, "a",
				config.getFetchFields(), null);

		Map<String, Object> filters = config.getFilters();
		if (filters != null) {
			if (!filters.isEmpty()) {
				for (String key : filters.keySet()) {
					Object filter = filters.get(key);
					if (filter != null) {
						// if ranged search (from - to fields)
						if (key.contains("fromRange-")) {
							String parsedKey = key.substring(10);
							if (filter instanceof Double) {
								BigDecimal rationalNumber = new BigDecimal(
										(Double) filter);
								queryBuilder.addCriterion("a." + parsedKey,
										" >= ", rationalNumber, true);
							} else if (filter instanceof Number) {
								queryBuilder.addCriterion("a." + parsedKey,
										" >= ", filter, true);
							} else if (filter instanceof Date) {
								queryBuilder
										.addCriterionDateRangeFromTruncatedToDay(
												"a." + parsedKey, (Date) filter);
							}
						} else if (key.contains("toRange-")) {
							String parsedKey = key.substring(8);
							if (filter instanceof Double) {
								BigDecimal rationalNumber = new BigDecimal(
										(Double) filter);
								queryBuilder.addCriterion("a." + parsedKey,
										" <= ", rationalNumber, true);
							} else if (filter instanceof Number) {
								queryBuilder.addCriterion("a." + parsedKey,
										" <= ", filter, true);
							} else if (filter instanceof Date) {
								queryBuilder
										.addCriterionDateRangeToTruncatedToDay(
												"a." + parsedKey, (Date) filter);
							}
						} else if (key.contains("list-")) {
							// if searching elements from list
							String parsedKey = key.substring(5);
							queryBuilder.addSqlCriterion(":" + parsedKey
									+ " in elements(a." + parsedKey + ")",
									parsedKey, filter);
						}
						// if not ranged search
						else {
							if (filter instanceof String) {
								// if contains dot, that means join is needed
								String filterString = (String) filter;
								queryBuilder.addCriterionWildcard("a." + key,
										filterString, true);
							} else if (filter instanceof Date) {
								queryBuilder.addCriterionDateTruncatedToDay(
										"a." + key, (Date) filter);
							} else if (filter instanceof Number) {
								queryBuilder.addCriterion("a." + key, " = ",
										filter, true);
							} else if (filter instanceof Boolean) {
								queryBuilder.addCriterion("a." + key, " is ",
										filter, true);
							} else if (filter instanceof Enum) {
								if (filter instanceof IdentifiableEnum) {
									String enumIdKey = new StringBuilder(key)
											.append("Id").toString();
									queryBuilder
											.addCriterion("a." + enumIdKey,
													" = ",
													((IdentifiableEnum) filter)
															.getId(), true);
								} else {
									queryBuilder.addCriterionEnum("a." + key,
											(Enum) filter);
								}
							} else if (BaseEntity.class.isAssignableFrom(filter
									.getClass())) {
								queryBuilder.addCriterionEntity("a." + key,
										filter);
							} else if (filter instanceof UniqueEntity
									|| filter instanceof IEntity) {
								queryBuilder.addCriterionEntity("a." + key,
										filter);
							}
						}
					}
				}
			}
		}
		queryBuilder.addPaginationConfiguration(config, "a");

		return queryBuilder;
	}

	public Provider findByCodeWithFetch(String code, List<String> fetchFields) {
		QueryBuilder qb = new QueryBuilder(Provider.class, "p", fetchFields,
				null);

		qb.addCriterion("code", "=", code, true);

		try {
			return (Provider) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
