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
import org.meveo.service.base.PersistenceService;
import org.slf4j.Logger;

/**
 * SecuredBusinessEntity Service base class.
 *
 * @author Tony Alejandro
 */
@Stateless
public class SecuredBusinessEntityService extends PersistenceService<BusinessEntity> {
    @Inject
    protected Logger log;

    public BusinessEntity getEntityByCode(Class<? extends BusinessEntity> entityClass, String code) {
        if (entityClass == null) {
            return null;
        }
        return getEntityByCode(entityClass.getName(), code);
    }

    public BusinessEntity getEntityByCode(String entityClassName, String code) {
        try {
            Class<?> businessEntityClass = Class.forName(ReflectionUtils.getCleanClassName(entityClassName));
            QueryBuilder qb = new QueryBuilder(businessEntityClass, "e", null);
            qb.addCriterion("e.code", "=", code, true);
            return (BusinessEntity) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} of code {} found", getEntityClass().getSimpleName(), code, e);
        } catch (NonUniqueResultException e) {
            log.error("More than one entity of type {} with code {} found", entityClass, code, e);
        } catch (ClassNotFoundException e) {
            log.error("Unable to create entity class for query", e);
        }
        return null;
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
            boolean isSameTypeAsParent = entity.getClass() == entity.getParentEntityType();
            if (!isSameTypeAsParent && securedEntities != null && !securedEntities.isEmpty()) {
                // This means that the entity type is being restricted. Since
                // the entity did not match anything above, the authorization
                // automatically fails.
                return false;
            }
            // Get entity from DB to get parent entities as well.
            entity = getEntityByCode(entity.getClass(), entity.getCode());
        }
        if (entity == null && !isParentEntity) {
            // If entity does not exist and it is not a parent entity, then
            // there's no need to check authorization, nothing will be returned
            // anyway.
            return true;
        }
        if (entity != null && entity.getParentEntity() != null) {
            BusinessEntity parentEntity = entity.getParentEntity();
            // Covers cases of class hierarchy, when parent class points to a generic class instead of a concrete subclass e.g. HierarchyLevel vs. UserHierarchyLevel
            if (!entity.getParentEntityType().isAssignableFrom(parentEntity.getClass())) {
                parentEntity = getEntityByCode(entity.getParentEntityType(), parentEntity.getCode());
            }

            return isEntityAllowed(parentEntity, user, true);
        } else {
            return false;
        }
    }

    private static boolean entityFoundInSecuredEntities(BusinessEntity entity, List<SecuredEntity> securedEntities) {
        if (entity == null) {
            return false;
        }
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