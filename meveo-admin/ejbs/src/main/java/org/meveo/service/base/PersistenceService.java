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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.Conversation;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.TransactionSynchronizationRegistry;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ProviderNotAllowedException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.cache.CustomFieldsCacheContainerProvider;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.event.qualifier.Created;
import org.meveo.event.qualifier.Disabled;
import org.meveo.event.qualifier.Enabled;
import org.meveo.event.qualifier.Removed;
import org.meveo.event.qualifier.Updated;
import org.meveo.model.BaseEntity;
import org.meveo.model.EnableEntity;
import org.meveo.model.IAuditable;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.IdentifiableEnum;
import org.meveo.model.ObservableEntity;
import org.meveo.model.UniqueEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.filter.Filter;
import org.meveo.security.MeveoUser;
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

	public static String SEARCH_SKIP_PROVIDER_CONSTRAINT = "skipProviderConstraint";
	public static String SEARCH_CURRENT_USER = "currentUser";
	public static String SEARCH_CURRENT_PROVIDER = "currentProvider";
	public static String SEARCH_ATTR_TYPE_CLASS = "type_class";

	@Inject
	@MeveoJpa
	private EntityManager em;

	@Inject
	@MeveoJpaForJobs
	private EntityManager emfForJobs;

	@Inject
	private Conversation conversation;

	@Resource
	protected TransactionSynchronizationRegistry txReg;

	@Inject
	@Created
	protected Event<E> entityCreatedEventProducer;

	@Inject
	@Updated
	protected Event<E> entityUpdatedEventProducer;

	@Inject
	@Disabled
	protected Event<E> entityDisabledEventProducer;

	@Inject
	@Enabled
	protected Event<E> entityEnabledEventProducer;

	@Inject
	@Removed
	protected Event<E> entityRemovedEventProducer;

	@Inject
	private CustomFieldsCacheContainerProvider customFieldsCache;

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
	@Override
	public void create(E e) throws BusinessException {
		create(e, getCurrentUser());
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#update(org.manaty.model.BaseEntity)
	 */
	@Override
	public E update(E e) {
		return update(e, getCurrentUser());
	}

	public E updateNoCheck(E e) {
		log.debug("start of update {} entity (id={}) ..", e.getClass()
				.getSimpleName(), e.getId());

		E mergedEntity = getEntityManager().merge(e);

		if (e.getClass().isAnnotationPresent(ObservableEntity.class)) {
			entityUpdatedEventProducer.fire(e);
		}
		
		// Add/Update custom fields in cache if applicable 
        if (e instanceof ICustomFieldEntity) {
            customFieldsCache.addUpdateCustomFieldsInCache((ICustomFieldEntity) mergedEntity);
        }

		return mergedEntity;
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#findById(java.lang.Long)
	 */
	@Override
	public E findById(Long id) {
		return findById(id, false);
	}

	/**
	 * Use by API.
	 */
	@Override
	public E findById(Long id, Provider provider, boolean refresh) {
		log.debug("start of find {} by id (id={}) ..", getEntityClass()
				.getSimpleName(), id);
		final Class<? extends E> productClass = getEntityClass();
		E e = getEntityManager().find(productClass, id);
		if (e != null) {
			checkProvider(e, provider);
			if (refresh) {
				log.debug("refreshing loaded entity");
				getEntityManager().refresh(e);
			}
		}
		log.debug("end of find {} by id (id={}). Result found={}.",
				getEntityClass().getSimpleName(), id, e != null);
		return e;
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#findById(java.lang.Long,
	 *      java.util.List)
	 */
	@Override
	public E findById(Long id, List<String> fetchFields) {
		return findById(id, fetchFields, false);
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#findById(java.lang.Long,
	 *      boolean)
	 */
	public E findById(Long id, boolean refresh) {
		log.debug("start of find {} by id (id={}) ..", getEntityClass()
				.getSimpleName(), id);
		final Class<? extends E> productClass = getEntityClass();
		E e = getEntityManager().find(productClass, id);
		if (e != null) {
			checkProvider(e);
			if (refresh) {
				log.debug("refreshing loaded entity");
				getEntityManager().refresh(e);
			}
		}
		log.debug("end of find {} by id (id={}). Result found={}.",
				getEntityClass().getSimpleName(), id, e != null);
		return e;
	}

	@SuppressWarnings("unchecked")
	public E findById(Long id, Provider provider) {
		final Class<? extends E> entityClass = getEntityClass();
		QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null,
				provider);
		queryBuilder.addCriterion("id", "=", id, true);
		Query query = queryBuilder.getQuery(getEntityManager());
		return (E) query.getSingleResult();
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

		List<E> results = query.getResultList();
		E e = null;
		if (!results.isEmpty()) {
			e = (E) results.get(0);
			checkProvider(e);
			if (refresh) {
				log.debug("refreshing loaded entity");
				getEntityManager().refresh(e);
			}
		}
		log.debug("end of find {} by id (id={}). Result found={}.",
				getEntityClass().getSimpleName(), id, e != null);
		return e;
	}

	@Override
	public E findByIdNoCheck(Long id) {
		return findByIdNoCheck(id, false);
	}

	@Override
	public E findByIdNoCheck(Long id, boolean refresh) {
		return findByIdNoCheck(getEntityManager(), id, refresh);
	}

	@Override
	public E findByIdNoCheck(EntityManager em, Long id, boolean refresh) {
		log.debug("start of find {} by id (id={}) ..", getEntityClass()
				.getSimpleName(), id);
		final Class<? extends E> productClass = getEntityClass();
		E e = em.find(productClass, id);
		if (e != null) {
			if (refresh) {
				log.debug("refreshing loaded entity");
				em.refresh(e);
			}
		}
		log.debug("end of find {} by id (id={}). Result found={}.",
				getEntityClass().getSimpleName(), id, e != null);
		return e;
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#disable(java.lang.Long)
	 */
	@Override
	public void disable(Long id) {
		E e = findById(id);
		if (e != null) {
			disable(e);
		}
	}

	@Override
	public E disable(E e) {
		if (e instanceof EnableEntity && ((EnableEntity) e).isActive()) {
			log.debug("start of disable {} entity (id={}) ..", getEntityClass()
					.getSimpleName(), e.getId());
			((EnableEntity) e).setDisabled(true);
			if (e instanceof IAuditable) {
				((IAuditable) e).updateAudit(getCurrentUser());
			}
			checkProvider(e);
			e = getEntityManager().merge(e);
			if (e.getClass().isAnnotationPresent(ObservableEntity.class)) {
				entityDisabledEventProducer.fire(e);
			}
			log.debug("end of disable {} entity (id={}).", e.getClass()
					.getSimpleName(), e.getId());
		}
		return e;
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#enable(java.lang.Long)
	 */
	@Override
	public void enable(Long id) {
		E e = findById(id);
		if (e != null) {
			enable(e);
		}
	}

	@Override
	public E enable(E e) {
		if (e instanceof EnableEntity && ((EnableEntity) e).isDisabled()) {
			log.debug("start of enable {} entity (id={}) ..", getEntityClass()
					.getSimpleName(), e.getId());
			((EnableEntity) e).setDisabled(false);
			if (e instanceof IAuditable) {
				((IAuditable) e).updateAudit(getCurrentUser());
			}
			checkProvider(e);
			e = getEntityManager().merge(e);
			if (e.getClass().isAnnotationPresent(ObservableEntity.class)) {
				entityEnabledEventProducer.fire(e);
			}
			log.debug("end of enable {} entity (id={}).", e.getClass()
					.getSimpleName(), e.getId());
		}
		return e;
	}

	@Override
	public void remove(E e) {
		log.debug("start of remove {} entity (id={}) ..", getEntityClass()
				.getSimpleName(), e.getId());
		checkProvider(e);
		getEntityManager().remove(e);
		if (e.getClass().isAnnotationPresent(ObservableEntity.class)) {
			entityRemovedEventProducer.fire(e);
		}
		// getEntityManager().flush();

		// Remove custom field values from cache if applicable
		if (e instanceof ICustomFieldEntity) {
			customFieldsCache
					.removeCustomFieldsFromCache((ICustomFieldEntity) e);
		}

		log.debug("end of remove {} entity (id={}).", getEntityClass()
				.getSimpleName(), e.getId());
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
	 * @see org.meveo.service.base.local.IPersistenceService#remove(java.util.Set)
	 */
	@Override
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
	@Override
	public E update(E e, User updater) {
		log.debug("start of update {} entity (id={}) ..", e.getClass()
				.getSimpleName(), e.getId());

		if (e instanceof IAuditable) {
			if (updater != null) {
				((IAuditable) e).updateAudit(updater);
			} else {
				((IAuditable) e).updateAudit(getCurrentUser());
			}
		}
		checkProvider(e);
		e = getEntityManager().merge(e);
		if (e.getClass().isAnnotationPresent(ObservableEntity.class)) {
			entityUpdatedEventProducer.fire(e);
		}

		// Add/Update custom fields in cache if applicable
		if (e instanceof ICustomFieldEntity) {
			customFieldsCache
					.addUpdateCustomFieldsInCache((ICustomFieldEntity) e);
		}

		log.debug("end of update {} entity (id={}).", e.getClass()
				.getSimpleName(), e.getId());

		return e;
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#create(org.manaty.model.BaseEntity,
	 *      org.manaty.model.user.User)
	 */
	@Override
	public void create(E e, User creator) {
		create(e, creator, getCurrentProvider());
	}

	@Override
	public void create(E e, User creator, Provider provider) {
		log.debug("start of create {} entity={}", e.getClass().getSimpleName(),
				e);

		if (e instanceof IAuditable) {
			if (creator != null) {
				((IAuditable) e).updateAudit(creator);
			} else {
				((IAuditable) e).updateAudit(getCurrentUser());
			}
		}

		if (e instanceof BaseEntity && (((BaseEntity) e).getProvider() == null)) {
			((BaseEntity) e).setProvider(provider);
		}

		getEntityManager().persist(e);
		if (e.getClass().isAnnotationPresent(ObservableEntity.class)) {
			entityCreatedEventProducer.fire(e);
		}

		// Add/Update custom fields in cache if applicable
		if (e instanceof ICustomFieldEntity) {
			customFieldsCache
					.addUpdateCustomFieldsInCache((ICustomFieldEntity) e);
		}

		log.debug("end of create {}. entity id={}.", e.getClass()
				.getSimpleName(), e.getId());

	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#list()
	 */
	@Override
	public List<E> list() {
		return list(getCurrentProvider(), null);
	}

	@Override
	public List<E> listActive() {
		return list(getCurrentProvider(), true);
	}

	public List<E> list(Provider provider) {
		return list(provider, null);
	}

	@SuppressWarnings("unchecked")
	public List<E> list(Provider provider, Boolean active) {
		final Class<? extends E> entityClass = getEntityClass();
		QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null,
				provider);
		if (active != null && EnableEntity.class.isAssignableFrom(entityClass)) {
			queryBuilder.addBooleanCriterion("disabled", !active);
		}
		Query query = queryBuilder.getQuery(getEntityManager());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<E> findByCodeLike(String wildcode, Provider provider) {
		final Class<? extends E> entityClass = getEntityClass();
		QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null,
				provider);
		if (EnableEntity.class.isAssignableFrom(entityClass)) {
			queryBuilder.addBooleanCriterion("disabled", false);
		}
		queryBuilder.addCriterion("code", "like", "%" + wildcode + "%", true);
		return queryBuilder.getQuery(getEntityManager()).getResultList();
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#list(org.meveo.admin.util.pagination.PaginationConfiguration)
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public List<E> list(PaginationConfiguration config) {
		Map<String, Object> filters = config.getFilters();

		if (filters != null && filters.containsKey("$FILTER")) {
			Filter filter = (Filter) filters.get("$FILTER");
			FilteredQueryBuilder queryBuilder = (FilteredQueryBuilder) getQuery(config);
			queryBuilder.processOrderCondition(filter.getOrderCondition(),
					filter.getPrimarySelector().getAlias());
			Query query = queryBuilder.getQuery(getEntityManager());
			return query.getResultList();
		} else {
			QueryBuilder queryBuilder = getQuery(config);
			Query query = queryBuilder.getQuery(getEntityManager());
			return query.getResultList();
		}
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#count(PaginationConfiguration
	 *      config)
	 */
	@Override
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
	@Override
	public long count() {
		final Class<? extends E> entityClass = getEntityClass();
		QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null,
				null);
		return queryBuilder.count(getEntityManager());
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#detach
	 */
	@Override
	public void detach(E entity) {
		// TODO: Hibernate. org.hibernate.Session session = (Session)
		// getEntityManager().getDelegate();
		// session.evict(entity);
		getEntityManager().detach(entity);
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#refresh(org.meveo.model.BaseEntity)
	 */
	@Override
	public void refresh(E entity) {
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
     * @see org.meveo.service.base.local.IPersistenceService#refreshOrRetrieve(org.meveo.model.BaseEntity)
     */
    @Override
    public E refreshOrRetrieve(E entity) {

        if (getEntityManager().contains(entity)) {
            getEntityManager().refresh(entity);
            return entity;
        } else {
            return findById((Long) entity.getId());
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public QueryBuilder getQuery(PaginationConfiguration config) {

		final Class<? extends E> entityClass = getEntityClass();

		Map<String, Object> filters = config.getFilters();

		// If provider is not retrievable from service, get it from a filter
		Provider provider = getCurrentProvider();
		if (provider == null && filters.containsKey(SEARCH_CURRENT_PROVIDER)) {
			provider = (Provider) filters.get(SEARCH_CURRENT_PROVIDER);
		}

		// Ignore current provider constraint if "skipProviderConstraint"
		// parameter was passed to search
		if (filters != null
				&& filters.containsKey(SEARCH_SKIP_PROVIDER_CONSTRAINT)) {
			provider = null;
		}

		QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a",
				config.getFetchFields(), provider);

		if (filters != null && !filters.isEmpty()) {

			if (filters.containsKey("$FILTER")) {
				queryBuilder = new FilteredQueryBuilder(
						(Filter) filters.get("$FILTER"), false, false);
			} else {

				for (String key : filters.keySet()) {
					if (SEARCH_SKIP_PROVIDER_CONSTRAINT.equals(key)
							|| SEARCH_CURRENT_PROVIDER.equals(key)
							|| SEARCH_CURRENT_USER.equals(key)) {
						continue;
					}

					String[] fieldInfo = key.split(" ");
					String condition = fieldInfo.length == 1 ? null
							: fieldInfo[0];
					String fieldName = fieldInfo.length == 1 ? fieldInfo[0]
							: fieldInfo[1];

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
						} else if (key.contains("inList-")) {
							// if searching elements from list
							String parsedKey = key.substring(7);
							queryBuilder.addSql("a." + parsedKey + " in ("
									+ filter + ")");

							// Search by an entity type
						} else if (SEARCH_ATTR_TYPE_CLASS.equals(fieldName)) {
							if (filter instanceof Collection) {
								List classes = new ArrayList<Class>();
								for (String className : (Collection<String>) filter) {
									try {
										classes.add(Class.forName(className));
									} catch (ClassNotFoundException e) {
										log.error(
												"Search by a type will be ignored - unknown class {}",
												className);
									}
								}

								if (condition == null) {
									queryBuilder.addSqlCriterion(
											"a.type in (:typeClass)",
											"typeClass", classes);
								} else if ("ne".equalsIgnoreCase(condition)) {
									queryBuilder.addSqlCriterion(
											"a.type not in (:typeClass)",
											"typeClass", classes);
								}

							} else if (filter instanceof Class) {
								if (condition == null) {
									queryBuilder.addSqlCriterion(
											"a.type = :typeClass", "typeClass",
											filter);
								} else if ("ne".equalsIgnoreCase(condition)) {
									queryBuilder.addSqlCriterion(
											"a.type != :typeClass",
											"typeClass", filter);
								}

							} else if (filter instanceof String) {
								try {
									if (condition == null) {
										queryBuilder.addSqlCriterion(
												"type(a) = :typeClass",
												"typeClass",
												Class.forName((String) filter));
									} else if ("ne".equalsIgnoreCase(condition)) {
										queryBuilder.addSqlCriterion(
												"type(a) != :typeClass",
												"typeClass",
												Class.forName((String) filter));
									}
								} catch (ClassNotFoundException e) {
									log.error(
											"Search by a type will be ignored - unknown class {}",
											filter);
								}
							}
						} else if (key.contains("minmaxRange-")) {
							// if searching elements from list
							String parsedKey = key.substring(12);
							String[] kss = parsedKey.split("-");
							if (kss.length == 2) {
								if (filter instanceof Double) {
									BigDecimal rationalNumber = new BigDecimal(
											(Double) filter);
									queryBuilder.addCriterion("a." + kss[0],
											" <= ", rationalNumber, false);
									queryBuilder.addCriterion("a." + kss[1],
											" >= ", rationalNumber, false);
								} else if (filter instanceof Number) {
									queryBuilder.addCriterion("a." + kss[0],
											" <= ", filter, false);
									queryBuilder.addCriterion("a." + kss[1],
											" >= ", filter, false);
								}
								if (filter instanceof Date) {
									Date value = (Date) filter;
									Calendar c = Calendar.getInstance();
									c.setTime(value);
									int year = c.get(Calendar.YEAR);
									int month = c.get(Calendar.MONTH);
									int date = c.get(Calendar.DATE);
									c.set(year, month, date, 0, 0, 0);
									value = c.getTime();
									queryBuilder.addCriterion("a." + kss[0],
											"<=", value, false);
									queryBuilder.addCriterion("a." + kss[1],
											">=", value, false);
								}
							}
						} else if (key.contains("likeCriterias-")) {
							// if searching elements from list
							String parsedKey = key.substring(14);
							String[] fields = parsedKey.split("-");
							queryBuilder.startOrClause();
							for (String field : fields) {
								if (filter instanceof String) {
									String filterString = (String) filter;
									queryBuilder.addCriterionWildcard("a."
											+ field, filterString, true);
								}
							}
							queryBuilder.endOrClause();

							// if not ranged search
						} else {
							if (filter instanceof String) {
								// if contains dot, that means join is needed
								String filterString = (String) filter;
								boolean wildcard = (filterString.indexOf("*") != -1);
								if (wildcard) {
									queryBuilder.addCriterionWildcard("a."
											+ fieldName, filterString, true,
											"ne".equals(condition));
								} else {
									queryBuilder
											.addCriterion(
													"a." + fieldName,
													"ne".equals(condition) ? " != "
															: " = ",
													filterString, true);
								}

							} else if (filter instanceof Date) {
								queryBuilder.addCriterionDateTruncatedToDay(
										"a." + fieldName, (Date) filter);

							} else if (filter instanceof Number) {
								queryBuilder
										.addCriterion("a." + fieldName, "ne"
												.equals(condition) ? " != "
												: " = ", filter, true);

							} else if (filter instanceof Boolean) {
								queryBuilder.addCriterion("a." + fieldName,
										"ne".equals(condition) ? " not is"
												: " is ", filter, true);

							} else if (filter instanceof Enum) {
								if (filter instanceof IdentifiableEnum) {
									String enumIdKey = new StringBuilder(
											fieldName).append("Id").toString();
									queryBuilder
											.addCriterion(
													"a." + enumIdKey,
													"ne".equals(condition) ? " != "
															: " = ",
													((IdentifiableEnum) filter)
															.getId(), true);
								} else {
									queryBuilder
											.addCriterionEnum(
													"a." + fieldName,
													(Enum) filter,
													"ne".equals(condition) ? " != "
															: " = ");
								}

							} else if (BaseEntity.class.isAssignableFrom(filter
									.getClass())) {
								queryBuilder
										.addCriterionEntity("a." + fieldName,
												filter,
												"ne".equals(condition) ? " != "
														: " = ");

							} else if (filter instanceof UniqueEntity
									|| filter instanceof IEntity) {
								queryBuilder
										.addCriterionEntity("a." + fieldName,
												filter,
												"ne".equals(condition) ? " != "
														: " = ");
							}
						}
					}
				}
			}
		}

		if (filters != null && filters.containsKey("$FILTER")) {
			Filter filter = (Filter) filters.get("$FILTER");
			queryBuilder.addPaginationConfiguration(config, filter
					.getPrimarySelector().getAlias());
		} else {
			queryBuilder.addPaginationConfiguration(config, "a");
		}

		// log.debug("Filters is {}", filters);
		// log.debug("Query is {}", queryBuilder.getSqlString());
		return queryBuilder;
	}

	/**
	 * Check entity provider. If current provider is not same as entity provider
	 * exception is thrown since different provider should not be allowed to
	 * modify (update or delete) entity.
	 */
	protected void checkProvider(E e) {
		if (isConversationScoped() && getCurrentProvider() != null) {
			if (e instanceof BaseEntity) {
				boolean notSameProvider = !((BaseEntity) e)
						.doesProviderMatch(getCurrentProvider());
				if (notSameProvider) {
					log.debug(
							"CheckProvider getCurrentProvider() id={}, entityProvider id={}",
							new Object[] { getCurrentProvider().getId(),
									((BaseEntity) e).getProvider().getId() });
					throw new ProviderNotAllowedException();
				}
			}
		}
	}

	protected void checkProvider(E e, Provider provider) {
		if (getCurrentProvider() != null) {
			if (e instanceof BaseEntity) {
				boolean notSameProvider = !((BaseEntity) e)
						.doesProviderMatch(provider);
				if (notSameProvider) {
					log.debug(
							"CheckProvider currentUser.getProvider() id={}, entityProvider id={}",
							new Object[] { provider.getId(),
									((BaseEntity) e).getProvider().getId() });
					throw new ProviderNotAllowedException();
				}
			}
		}
	}

	public Provider getCurrentProvider() {

		Provider result = null;

		// This gets a current provider as set in MeveoUser uppon login
		try {
			if (result == null && identity.isLoggedIn()
					&& identity.getUser() != null) {
				result = ((MeveoUser) identity.getUser()).getCurrentProvider();
			}
		} catch (ContextNotActiveException e) {
			// Ignore this exception as in async methods no context is available

		} catch (Exception e) {
			log.error("failed to get current provider", e);
		}

		// This gets a provider directly from a current user
		if (result == null && getCurrentUser() != null) {
			result = getCurrentUser().getProvider();
		}
		return result;
	}

	public E attach(E e) {
		return (E) getEntityManager().merge(e);
	}

	private boolean isConversationScoped() {
		if (conversation != null) {
			try {
				conversation.isTransient();
				return true;
			} catch (Exception e) {
			}
		}

		return false;
	}

	public EntityManager getEntityManager() {
		EntityManager result = emfForJobs;
		if (conversation != null) {
			try {
				conversation.isTransient();
				result = em;
			} catch (Exception e) {
			}
		}

		// log.debug("em.txKey={}, em.hashCode={}", txReg.getTransactionKey(),
		// em.hashCode());
		return result;
	}

	public void updateAudit(E e) {
		updateAudit(e, getCurrentUser());
	}

	public void updateAudit(E e, User currentUser) {
		if (e instanceof IAuditable) {
			((IAuditable) e).updateAudit(currentUser);
		}
	}

	public void commit() {
		getEntityManager().flush();
	}

}
