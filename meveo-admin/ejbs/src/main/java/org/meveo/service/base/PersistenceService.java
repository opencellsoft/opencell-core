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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.Conversation;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ProviderNotAllowedException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.AuditableEntity;
import org.meveo.model.BaseEntity;
import org.meveo.model.EnableEntity;
import org.meveo.model.IEntity;
import org.meveo.model.IdentifiableEnum;
import org.meveo.model.UniqueEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.util.MeveoJpa;
import org.meveo.util.MeveoJpaForJobs;

/**
 * Generic implementation that provides the default implementation for
 * persistence methods declared in the {@link IPersistenceService} interface.
 */
public abstract class PersistenceService<E extends IEntity> extends BaseService
		implements IPersistenceService<E> {
	protected final Class<E> entityClass;

	@Inject
	@MeveoJpa
	protected EntityManager em;

	@Inject
	@MeveoJpaForJobs
	private EntityManager emfForJobs;

	@Inject
	private Conversation conversation;

	private Provider provider;

	/**
	 * Constructor.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PersistenceService() {
		Class clazz = getClass();
		while (!(clazz.getGenericSuperclass() instanceof ParameterizedType)) {
			clazz = clazz.getSuperclass();
		}
		Object o = ((ParameterizedType) clazz.getGenericSuperclass())
				.getActualTypeArguments()[0];

		if (o instanceof TypeVariable) {
			this.entityClass = (Class<E>) ((TypeVariable) o).getBounds()[0];
		} else {
			this.entityClass = (Class<E>) o;
		}
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#getEntityClass()
	 */
	public Class<E> getEntityClass() {
		return entityClass;
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#create(org.manaty.model.BaseEntity)
	 */
	public void create(E e) throws BusinessException {
		create(e, getCurrentUser());
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#update(org.manaty.model.BaseEntity)
	 */
	public void update(E e) {
		update(e, getCurrentUser());
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#update(org.manaty.model.BaseEntity)
	 */
	public void update(EntityManager em, E e) {
		update(em, e, getCurrentUser());
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#remove(java.lang.Long)
	 */
	public void remove(Long id) {
		E e = findById(id);
		if (e != null) {
			remove(e);
		}
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#findById(java.lang.Long)
	 */
	public E findById(Long id) {
		return findById(id, false);
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#findById(java.lang.Long,
	 *      java.util.List)
	 */
	public E findById(Long id, List<String> fetchFields) {
		return findById(id, fetchFields, false);
	}

	public E findById(Long id, boolean refresh) {
		return findById(getEntityManager(), id, refresh);
	}

	public E findById(EntityManager em, Long id) {
		return findById(em, id, false);
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#findById(java.lang.Long,
	 *      boolean)
	 */
	public E findById(EntityManager em, Long id, boolean refresh) {
		log.debug("start of find {} by id (id={}) ..", getEntityClass()
				.getSimpleName(), id);
		final Class<? extends E> productClass = getEntityClass();
		E e = em.find(productClass, id);
		if (refresh) {
			log.debug("refreshing loaded entity");
			em.refresh(e);
		}
		log.debug("end of find {} by id (id={}). Result found={}.",
				getEntityClass().getSimpleName(), id, e != null);
		return e;
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#findById(java.lang.Long,
	 *      java.util.List, boolean)
	 */
	@SuppressWarnings("unchecked")
	public E findById(Long id, List<String> fetchFields, boolean refresh) {
		log.debug("start of find {} by id (id={}) ..", getEntityClass()
				.getSimpleName(), id);
		final Class<? extends E> productClass = getEntityClass();
		StringBuilder queryString = new StringBuilder("from "
				+ productClass.getName() + " a");
		if (fetchFields != null && !fetchFields.isEmpty()) {
			for (String fetchField : fetchFields) {
				queryString.append(" left join fetch a." + fetchField);
			}
		}
		queryString.append(" where a.id = :id");
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("id", id);

		E e = (E) query.getResultList().get(0);

		if (refresh) {
			log.debug("refreshing loaded entity");
			getEntityManager().refresh(e);
		}
		log.debug("end of find {} by id (id={}). Result found={}.",
				getEntityClass().getSimpleName(), id, e != null);
		return e;
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#disable(java.lang.Long)
	 */
	public void disable(Long id) {
		E e = findById(id);
		if (e instanceof EnableEntity) {
			((EnableEntity) e).setDisabled(true);
			update(e);
		}
	}

	public void remove(E e) {
		remove(getEntityManager(), e);
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#remove(org.manaty.model.BaseEntity)
	 */
	public void remove(EntityManager em, E e) {
		checkProvider(e);
		log.debug("start of remove {} entity (id={}) ..", getEntityClass()
				.getSimpleName(), e.getId());
		em.remove(e);
		em.flush();
		log.debug("end of remove {} entity (id={}).", getEntityClass()
				.getSimpleName(), e.getId());
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#remove(java.util.Set)
	 */
	public void remove(Set<Long> ids) {
		Query query = getEntityManager().createQuery(
				"delete from " + getEntityClass().getName()
						+ " where id in (:ids) and provider.id = :providerId");
		query.setParameter("ids", ids);
		query.setParameter("providerId",
				getCurrentProvider() != null ? getCurrentProvider().getId()
						: null);
		query.executeUpdate();
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#update(org.manaty.model.BaseEntity,
	 *      org.manaty.model.user.User)
	 */
	public void update(E e, User updater) {
		update(getEntityManager(), e, updater);
	}

	public void update(EntityManager em, E e, User updater) {
		log.debug("start of update {} entity (id={}) ..", e.getClass()
				.getSimpleName(), e.getId());
		if (e instanceof AuditableEntity) {
			if (updater != null) {
				((AuditableEntity) e).updateAudit(updater);
			} else {
				((AuditableEntity) e).updateAudit(getCurrentUser());
			}
		}
		checkProvider(e);
		em.merge(e);
		log.debug("end of update {} entity (id={}).", e.getClass()
				.getSimpleName(), e.getId());
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#create(org.manaty.model.BaseEntity,
	 *      org.manaty.model.user.User)
	 */
	public void create(E e, User creator) {
		create(e, creator, getCurrentProvider());
	}

	public void create(EntityManager em, E e) {
		create(em, e, getCurrentUser(), getCurrentProvider());
	}

	public void create(E e, User creator, Provider provider) {
		create(getEntityManager(), e, creator, provider);
	}

	public void create(EntityManager em, E e, User creator, Provider provider) {
		log.debug("start of create {} entity ..", e.getClass().getSimpleName());

		if (e instanceof AuditableEntity) {
			if (creator != null) {
				((AuditableEntity) e).updateAudit(creator);
			} else {
				((AuditableEntity) e).updateAudit(getCurrentUser());
			}
		}

		if (e instanceof BaseEntity && (((BaseEntity) e).getProvider() == null)) {
			((BaseEntity) e).setProvider(provider);
		}

		em.persist(e);

		log.debug("end of create {}. entity id={}.", e.getClass()
				.getSimpleName(), e.getId());

	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#list()
	 */
	@SuppressWarnings("unchecked")
	public List<E> list() {
		final Class<? extends E> entityClass = getEntityClass();
		QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null,
				getCurrentProvider());
		Query query = queryBuilder.getQuery(getEntityManager());
		return query.getResultList();
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#list(org.meveo.admin.util.pagination.PaginationConfiguration)
	 */

	@SuppressWarnings({ "unchecked" })
	public List<E> list(PaginationConfiguration config) {
		QueryBuilder queryBuilder = getQuery(config);
		Query query = queryBuilder.getQuery(getEntityManager());
		return query.getResultList();
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#count(PaginationConfiguration
	 *      config)
	 */

	public long count(PaginationConfiguration config) {
		List<String> fetchFields = config.getFetchFields();
		config.setFetchFields(null);
		QueryBuilder queryBuilder = getQuery(config);
		config.setFetchFields(fetchFields);
		return queryBuilder.count(getEntityManager());
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#count()
	 */

	public long count() {
		final Class<? extends E> entityClass = getEntityClass();
		QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null,
				null);
		return queryBuilder.count(getEntityManager());
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#detach
	 */

	public void detach(Object entity) {
		// TODO: Hibernate. org.hibernate.Session session = (Session)
		// getEntityManager().getDelegate();
		// session.evict(entity);
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#refresh(org.meveo.model.BaseEntity)
	 */

	public void refresh(BaseEntity entity) {
		// entity manager throws exception if trying to refresh not managed
		// entity (ejb spec requires this).
		/*
		 * TODO: Hibernate. org.hibernate.Session session = (Session)
		 * getEntityManager().getDelegate(); session.refresh(entity);
		 */
		// getEntityManager().getEntityManagerFactory().getCache().evict(entity.getClass(),
		// entity.getId());
		if (getEntityManager().contains(entity)) {
			getEntityManager().refresh(entity);
		}
	}

	/**
	 * Creates query to filter entities according data provided in pagination
	 * configuration.
	 * 
	 * @param config
	 *            PaginationConfiguration data holding object
	 * @return query to filter entities according pagination configuration data.
	 */
	@SuppressWarnings("rawtypes")
	private QueryBuilder getQuery(PaginationConfiguration config) {

		final Class<? extends E> entityClass = getEntityClass();

		QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a",
				config.getFetchFields(), getCurrentProvider());

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

	/**
	 * Check entity provider. If current provider is not same as entity provider
	 * exception is thrown since different provider should not be allowed to
	 * modify (update or delete) entity.
	 */
	private void checkProvider(E e) {
		if (getCurrentProvider() != null) {
			if (e instanceof BaseEntity) {
				Provider entityProvider = getEntityManager().find(
						Provider.class, ((BaseEntity) e).getProvider().getId());
				boolean notSameProvider = !(entityProvider != null && entityProvider
						.getId().equals(getCurrentProvider().getId()));
				log.debug(
						"CheckProvider getCurrentProvider() id={} code={}, entityProvider id={} code={}",
						new Object[] {
								getCurrentProvider().getId(),
								getCurrentProvider().getCode(),
								entityProvider != null ? entityProvider.getId()
										: null,
								entityProvider != null ? entityProvider
										.getCode() : null });
				if (notSameProvider) {
					throw new ProviderNotAllowedException();
				}
			}
		}
	}

	public Provider getCurrentProvider() {
		Provider result = provider;
		if (result == null && getCurrentUser() != null) {
			result = getCurrentUser().getProvider();
		}

		return result;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public BaseEntity attach(BaseEntity e) {
		return (BaseEntity) getEntityManager().merge(e);
	}

	protected EntityManager getEntityManager() {
		EntityManager result = emfForJobs;

		if (conversation != null) {
			try {
				conversation.isTransient();
				result = em;
			} catch (Exception e) {
			}
		}

		return result;
	}

	public EntityManager getEmfForJobs() {
		return emfForJobs;
	}

	public void updateAudit(E e) {
		updateAudit(e, getCurrentUser());
	}

	public void updateAudit(E e, User currentUser) {
		if (e instanceof AuditableEntity) {
			((AuditableEntity) e).updateAudit(currentUser);
		}
	}

}
