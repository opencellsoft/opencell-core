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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.meveo.admin.action.admin.CurrentProvider;
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

/**
 * Generic implementation that provides the default implementation for
 * persistence methods declared in the {@link IPersistenceService} interface.
 * 
 * 
 * @author Ignas
 * @created 2009.09.04
 */
public abstract class PersistenceService<E extends IEntity> extends BaseService implements
		IPersistenceService<E> {
	protected final Class<E> entityClass;

	@PersistenceContext(unitName = "MeveoAdmin")
	protected EntityManager em;

	// TODO move to places where it is needed
	@PersistenceContext(unitName = "MeveoDWH")
	protected EntityManager dwhEntityManager;

	@Inject
	@CurrentProvider
	protected Provider currentProvider;
	

	/**
	 * Constructor.
	 */
	@SuppressWarnings("unchecked")
	public PersistenceService() {

		Class clazz = getClass();
		while (!(clazz.getGenericSuperclass() instanceof ParameterizedType)) {
			clazz = clazz.getSuperclass();
		}
		Object o = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];

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
    public void create(E e) {
        create(e, getCurrentUser());
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#update(org.manaty.model.BaseEntity)
     */
    public void update(E e) {
        update(e, getCurrentUser());
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

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#findById(java.lang.Long,
	 *      boolean)
	 */
	public E findById(Long id, boolean refresh) {
		log.debug("start of find {0} by id (id={1}) ..", getEntityClass().getSimpleName(), id);
		final Class<? extends E> productClass = getEntityClass();
		E e = em.find(productClass, id);
		if (refresh) {
			log.debug("refreshing loaded entity");
			em.refresh(e);
		}
		log.debug("end of find {0} by id (id={1}). Result found={2}.", getEntityClass()
				.getSimpleName(), id, e != null);
		return e;
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#findById(java.lang.Long,
	 *      java.util.List, boolean)
	 */
	@SuppressWarnings("unchecked")
	public E findById(Long id, List<String> fetchFields, boolean refresh) {
		log.debug("start of find {0} by id (id={1}) ..", getEntityClass().getSimpleName(), id);
		final Class<? extends E> productClass = getEntityClass();
		StringBuilder queryString = new StringBuilder("from " + productClass.getName() + " a");
		if (fetchFields != null && !fetchFields.isEmpty()) {
			for (String fetchField : fetchFields) {
				queryString.append(" left join fetch a." + fetchField);
			}
		}
		queryString.append(" where a.id = :id");
		Query query = em.createQuery(queryString.toString());
		query.setParameter("id", id);

		E e = (E) query.getResultList().get(0);

		if (refresh) {
			log.debug("refreshing loaded entity");
			em.refresh(e);
		}
		log.debug("end of find {0} by id (id={1}). Result found={2}.", getEntityClass()
				.getSimpleName(), id, e != null);
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

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#remove(org.manaty.model.BaseEntity)
	 */
	public void remove(E e) {
		checkProvider(e);
		log.debug("start of remove {0} entity (id={1}) ..", getEntityClass().getSimpleName(),
				e.getId());
		em.remove(e);
		em.flush();
		log.debug("end of remove {0} entity (id={1}).", getEntityClass().getSimpleName(), e.getId());
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#remove(java.util.Set)
	 */
	public void remove(Set<Long> ids) {
		Query query = em.createQuery("delete from " + getEntityClass().getName()
				+ " where id in (:ids) and provider.id = :providerId");
		query.setParameter("ids", ids);
		query.setParameter("providerId", currentProvider != null ? currentProvider.getId() : null);
		query.executeUpdate();
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#update(org.manaty.model.BaseEntity,
	 *      org.manaty.model.user.User)
	 */
	public void update(E e, User updater) {
		log.debug("start of update {0} entity (id={1}) ..", e.getClass().getSimpleName(), e.getId());
		if (e instanceof AuditableEntity) {
			if (updater != null) {
				((AuditableEntity) e).updateAudit(updater);
			} else {
				((AuditableEntity) e).updateAudit(getCurrentUser());
			}
		}
		checkProvider(e);
		em.merge(e);
		log.debug("end of update {0} entity (id={1}).", e.getClass().getSimpleName(), e.getId());
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#create(org.manaty.model.BaseEntity,
	 *      org.manaty.model.user.User)
	 */
	public void create(E e, User creator) {
		create(e, creator, currentProvider);
	}

	public void create(E e, User creator, Provider provider) {
		log.debug("start of create {0} entity ..", e.getClass().getSimpleName());
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
		log.debug("end of create {0}. entity id={1}.", e.getClass().getSimpleName(), e.getId());
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#list()
	 */
	@SuppressWarnings("unchecked")
	public List<E> list() {
		final Class<? extends E> entityClass = getEntityClass();
		QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null, currentProvider);
		Query query = queryBuilder.getQuery(em);
		return query.getResultList();
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#list(org.meveo.admin.util.pagination.PaginationConfiguration)
	 */

	@SuppressWarnings({ "unchecked" })
	public List<E> list(PaginationConfiguration config) {
		QueryBuilder queryBuilder = getQuery(config);
		Query query = queryBuilder.getQuery(em);
		return query.getResultList();
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#count(PaginationConfiguration
	 *      config)
	 */

	public long count(PaginationConfiguration config) {
        
		QueryBuilder queryBuilder = getQuery(config);
		return queryBuilder.count(em);
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#count()
	 */

	public long count() {
		final Class<? extends E> entityClass = getEntityClass();
		QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null,null);
		
		return queryBuilder.count(em);
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#detach
	 */

	public void detach(Object entity) {
		// TODO: Hibernate. org.hibernate.Session session = (Session)
		// em.getDelegate();
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
		 * em.getDelegate(); session.refresh(entity);
		 */
	}

	/**
	 * Creates query to filter entities according data provided in pagination
	 * configuration.
	 * 
	 * @param config
	 *            PaginationConfiguration data holding object
	 * @return query to filter entities according pagination configuration data.
	 */
	private QueryBuilder getQuery(PaginationConfiguration config) {

		final Class<? extends E> entityClass = getEntityClass();
		QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", config.getFetchFields(),currentProvider);
		
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
								BigDecimal rationalNumber = new BigDecimal((Double) filter);
								queryBuilder.addCriterion("a." + parsedKey, " >= ", rationalNumber,
										true);
							} else if (filter instanceof Number) {
								queryBuilder.addCriterion("a." + parsedKey, " >= ", filter, true);
							} else if (filter instanceof Date) {
								queryBuilder.addCriterionDateRangeFromTruncatedToDay("a."
										+ parsedKey, (Date) filter);
							}
						} else if (key.contains("toRange-")) {
							String parsedKey = key.substring(8);
							if (filter instanceof Double) {
								BigDecimal rationalNumber = new BigDecimal((Double) filter);
								queryBuilder.addCriterion("a." + parsedKey, " <= ", rationalNumber,
										true);
							} else if (filter instanceof Number) {
								queryBuilder.addCriterion("a." + parsedKey, " <= ", filter, true);
							} else if (filter instanceof Date) {
								queryBuilder.addCriterionDateRangeToTruncatedToDay(
										"a." + parsedKey, (Date) filter);
							}
						} else if (key.contains("list-")) {
							// if searching elements from list
							String parsedKey = key.substring(5);
							queryBuilder.addSqlCriterion(":" + parsedKey + " in elements(a."
									+ parsedKey + ")", parsedKey, filter);
						}
						// if not ranged search
						else {
							if (filter instanceof String) {
								// if contains dot, that means join is needed
								String filterString = (String) filter;
								queryBuilder.addCriterionWildcard("a." + key, filterString, true);
							} else if (filter instanceof Date) {
								queryBuilder.addCriterionDateTruncatedToDay("a." + key,
										(Date) filter);
							} else if (filter instanceof Number) {
								queryBuilder.addCriterion("a." + key, " = ", filter, true);
							} else if (filter instanceof Boolean) {
								queryBuilder.addCriterion("a." + key, " is ", filter, true);
							} else if (filter instanceof Enum) {
								if (filter instanceof IdentifiableEnum) {
									String enumIdKey = new StringBuilder(key).append("Id")
											.toString();
									queryBuilder.addCriterion("a." + enumIdKey, " = ",
											((IdentifiableEnum) filter).getId(), true);
								} else {
									queryBuilder.addCriterionEnum("a." + key, (Enum) filter);
								}
							} else if (BaseEntity.class.isAssignableFrom(filter.getClass())) {
								queryBuilder.addCriterionEntity("a." + key, filter);
							} else if (filter instanceof UniqueEntity) {
								queryBuilder.addCriterionEntity("a." + key, filter);
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

		if (currentProvider != null) {
			if (e instanceof BaseEntity) {
				Provider provider = ((BaseEntity) e).getProvider();
				boolean notSameProvider = !(provider != null && provider.getId().equals(
						currentProvider.getId()));
				log.debug(
						"checkProvider  currentProvider id={0} code={1}, entityprovider id={2} code={3}",
						currentProvider.getId(), currentProvider.getCode(),
						provider != null ? provider.getId() : null,
						provider != null ? provider.getCode() : null);
				if (notSameProvider) {
					throw new ProviderNotAllowedException();
				}
			}
		}

	}
}
