/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.security;

import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.service.base.PersistenceService;

/**
 * SecuredBusinessEntity Service base class.
 *
 * @author Tony Alejandro
 */
@Stateless
public class SecuredBusinessEntityService extends PersistenceService<BusinessEntity> {
    
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

    public boolean isEntityAllowed(BusinessEntity entity, Map<Class<?>, Set<SecuredEntity>> allSecuredEntitiesMap, boolean isParentEntity) {
        Set<SecuredEntity> securedEntities = null;
        //this because if the current entity is got as parent of a another fisrt entity by
        // fisrtEntity.getParentEntity() then the real class of entity can be an hibernate proxy
        if (entity != null) {
            Class<?> entityClass = getEntityRealClass(entity);
            securedEntities = allSecuredEntitiesMap.get(entityClass);
        }
        
        // Doing this check first allows verification without going to DB.
        if (entityFoundInSecuredEntities(entity, securedEntities)) {
            // Match was found authorization successful
            return true;
        }

        // Check if entity exists.
        if (entity != null) {
            // Check if entity's type is restricted to a specific group of
            // entities. i.e. only specific Customers, CA, BA, etc.
            boolean isSameTypeAsParent = getClassForHibernateObject(entity) == entity.getParentEntityType();
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

            return isEntityAllowed(parentEntity, allSecuredEntitiesMap, true);
        } else {
            return false;
        }
    }
    
	public static Class<?> getClassForHibernateObject(Object object) {
		return object instanceof HibernateProxy ? ((HibernateProxy) object).getHibernateLazyInitializer().getPersistentClass()
				: object.getClass();
	}

    private static boolean entityFoundInSecuredEntities(BusinessEntity entity, Set<SecuredEntity> securedEntities) {
    	if (entity == null || securedEntities == null) {
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

    public static Class<?> getEntityRealClass(Object entity) {
        if (entity instanceof HibernateProxy) {
			LazyInitializer lazyInitializer = ((HibernateProxy) entity).getHibernateLazyInitializer();
			return lazyInitializer.getPersistentClass();
		} else {
			return entity.getClass();
		}
	}
}