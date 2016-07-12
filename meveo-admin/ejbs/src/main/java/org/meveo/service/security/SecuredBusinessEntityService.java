package org.meveo.service.security;

import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.slf4j.Logger;

/**
 * SecuredBusinessEntity Service base class.
 *
 * @author Tony Alejandro
 */
@Stateless
public class SecuredBusinessEntityService extends PersistenceService<BusinessEntity>{
	@Inject
	protected Logger log;

	public BusinessEntity getEntityByCode(Class<? extends BusinessEntity> entityClass, String code, User user) {
		Provider provider = user.getProvider();
		try {
			Class<?> cleanClass = Class.forName(ReflectionUtils.getCleanClassName(entityClass.getName()));
			QueryBuilder qb = new QueryBuilder( cleanClass , "e", null, provider);
			qb.addCriterion("e.code", "=", code, true);
			qb.addCriterionEntity("e.provider", provider);
			return (BusinessEntity) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			log.debug("No {} of code {} for provider {} found", getEntityClass().getSimpleName(), code, provider.getId(), e);
			return null;
		} catch (NonUniqueResultException e) {
			log.error("More than one entity of type {} with code {} and provider {} found", entityClass, code, provider, e);
			return null;
		} catch (ClassNotFoundException e) {
			log.error("Unable to create entity class for query", e);
			return null;
		}
	}

	public boolean isEntityAllowed(BusinessEntity entity, User user, boolean isParentEntity) {
		// Doing this check first allows verification without going to DB.
		if (entityFoundInSecuredEntities(entity, user.getSecuredEntities())) {
			// Match was found authorization successful
			return true;
		}

		// Check if entity exists.
		if (entity != null) {
			// Check if entity's type is restricted to a specific group of
			// entities. i.e. only specific Customers, CA, BA, etc.
			Set<SecuredEntity> securedEntities = user.getSecuredEntitiesMap().get(entity.getClass());
			if (securedEntities != null && !securedEntities.isEmpty()) {
				// This means that the entity type is being restricted. Since
				// the entity did not match anything above, the authorization
				// automatically fails.
				return false;
			}
			// Get entity from DB to get parent entities as well.
			entity = getEntityByCode(entity.getClass(), entity.getCode(), user);
		}
		if (entity == null && !isParentEntity) {
			// If entity does not exist and it is not a parent entity, then
			// there's no need to check authorization, nothing will be returned
			// anyway.
			return true;
		}
		return entity != null && entity.getParentEntity() != null && isEntityAllowed(entity.getParentEntity(), user, true);
	}

	private static boolean entityFoundInSecuredEntities(BusinessEntity entity, List<SecuredEntity> securedEntities) {
		boolean found = false;
		for (SecuredEntity securedEntity : securedEntities) {
			if (securedEntity.equals(entity)) {
				found = true;
				break;
			}
		}
		return found;
	}

}