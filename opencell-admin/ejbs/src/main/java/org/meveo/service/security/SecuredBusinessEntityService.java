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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.meveo.admin.exception.ValidationException;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SecuredBusinessEntity Service base class.
 *
 * @author Tony Alejandro
 */
@Stateless
public class SecuredBusinessEntityService extends PersistenceService<SecuredEntity> {

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    protected ParamBeanFactory paramBeanFactory;
    
    @Inject
    private UserService userService;

    public BusinessEntity getEntityByCode(Class<? extends BusinessEntity> entityClass, String code) {
        if (entityClass == null) {
            return null;
        }
        return getEntityByCode(ReflectionUtils.getCleanClassName(entityClass.getName()), code);
    }

    public BusinessEntity getEntityByCode(String entityClassName, String code) {
        try {
            QueryBuilder qb = new QueryBuilder("from " + entityClassName + " e", "e");
            qb.addCriterion("e.code", "=", code, true);
            return (BusinessEntity) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} of code {} found", entityClassName, code, e);
        } catch (NonUniqueResultException e) {
            log.error("More than one entity of type {} with code {} found", entityClassName, code, e);
        }
        return null;
    }

    public boolean isEntityAllowed(BusinessEntity entity, Map<String, Set<org.meveo.security.SecuredEntity>> allSecuredEntitiesMap, boolean isParentEntity) {
        Set<org.meveo.security.SecuredEntity> securedEntities = null;
        // this because if the current entity is got as parent of a another first entity by
        // firstEntity.getParentEntity() then the real class of entity can be an hibernate proxy
        if (entity != null) {
            Class<?> entityClass = getEntityRealClass(entity);
            securedEntities = allSecuredEntitiesMap.get(entityClass.getSimpleName());
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
            if (isSameTypeAsParent && entity.getParentEntityType().equals(Seller.class) && !paramBeanFactory.getInstance().getBooleanValue("accessible.entity.allows.access.childs.seller", false)) {
                return false;
            }
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
        return object instanceof HibernateProxy ? ((HibernateProxy) object).getHibernateLazyInitializer().getPersistentClass() : object.getClass();
    }

    private static boolean entityFoundInSecuredEntities(BusinessEntity entity, Set<org.meveo.security.SecuredEntity> securedEntities) {
        if (entity == null || securedEntities == null) {
            return false;
        }
        boolean found = false;
        for (org.meveo.security.SecuredEntity securedEntity : securedEntities) {
            if (isSecuredEntityEqualBusinessEntity(securedEntity, entity)) {
                found = true;
                break;
            }
        }
        return found;
    }

    private static boolean isSecuredEntityEqualBusinessEntity(org.meveo.security.SecuredEntity securedEntity, BusinessEntity entity) {

        if (entity == null) {
            return false;
        }

        String thatCode = ((BusinessEntity) entity).getCode();
        String thatClass = ReflectionUtils.getCleanClassName(entity.getClass().getSimpleName());

        thatCode = thatClass + "-_-" + thatCode;
        String thisCode = securedEntity.getEntityClass() + "-_-" + securedEntity.getCode();

        if (!thisCode.equals(thatCode)) {
            return false;
        }
        return true;
    }

    public static Class<?> getEntityRealClass(Object entity) {
        if (entity instanceof HibernateProxy) {
            LazyInitializer lazyInitializer = ((HibernateProxy) entity).getHibernateLazyInitializer();
            return lazyInitializer.getPersistentClass();
        } else {
            return entity.getClass();
        }
    }

    /**
     * Get a list of secured entities for a current user
     * 
     * @return A list of secured entities for a current user
     */
    public List<org.meveo.security.SecuredEntity> getSecuredEntitiesForCurentUser() {
    	User user=userService.getUserFromDatabase(currentUser.getUserName());
    	if(user!=null) {
    		List<String> userRoles = user.getUserRoles().stream()
    				.map(role -> role.getName())
    				.collect(Collectors.toList());
    		if(!userRoles.isEmpty()) {
    			currentUser.getRoles().addAll(userRoles);
    		}
    	}
    	return getEntityManager().createNamedQuery("SecuredEntity.listForCurrentUser", org.meveo.security.SecuredEntity.class).setParameter("userName", currentUser.getUserName().toLowerCase())
    			.setParameter("roleNames", currentUser.getRoles()).getResultList();
    }

    
    
    /**
     * Get a list of secured entities applied to a given user.
     * 
     * @param username Username of a user to retrieve
     * @return A list of secured entities
     */
    public List<SecuredEntity> getSecuredEntitiesForUser(String username) {
        return getEntityManager().createNamedQuery("SecuredEntity.listByUserName", SecuredEntity.class).setParameter("userName", username.toLowerCase()).getResultList();
    }

    /**
     * Get a list of secured entities applied to a given role.
     * 
     * @param roleName Name of a role to retrieve
     * @return A list of secured entities
     */
    public List<SecuredEntity> getSecuredEntitiesForRole(String roleName) {
        return getEntityManager().createNamedQuery("SecuredEntity.listByRoleName", SecuredEntity.class).setParameter("roleName", roleName).getResultList();
    }

    /**
     * Synchronize secured entities with what a user currently has assigned
     * 
     * @param securedEntities A final list of secured entities that user should have
     * @param username Username of a user to assign secured entities to
     */
    public void syncSecuredEntitiesForUser(List<SecuredEntity> securedEntities, String username) {

        // Determine new secured entities to add or remove
        List<SecuredEntity> seToAdd = new ArrayList<>(securedEntities);
        List<SecuredEntity> seToDelete = new ArrayList<>();

        List<SecuredEntity> seCurrent = getSecuredEntitiesForUser(username);
        seToDelete.addAll(seCurrent);
        seToDelete.removeAll(seToAdd);
        seToAdd.removeAll(seCurrent);

        if (!seToDelete.isEmpty()) {
            for (SecuredEntity securedEntity : seToDelete) {
                remove(securedEntity);
            }
        }
        if (!seToAdd.isEmpty()) {
            for (SecuredEntity securedEntity : seToAdd) {
                try {
                    addSecuredEntityForUser(securedEntity, username);
                } catch (ValidationException e) {
                    log.warn("Failed to sync secured entity rule for user {}", username, e);
                }
            }
        }
    }

    /**
     * Synchronize secured entities with what a user currently has assigned
     * 
     * @param securedEntities A final list of secured entities that user should have
     * @param roleName Username of a user to assign secured entities to
     */
    public void syncSecuredEntitiesForRole(List<SecuredEntity> securedEntities, String roleName) {

        // Determine new secured entities to add or remove
        List<SecuredEntity> seToAdd = new ArrayList<>(securedEntities);
        List<SecuredEntity> seToDelete = new ArrayList<>();

        List<SecuredEntity> seCurrent = getSecuredEntitiesForRole(roleName);
        seToDelete.addAll(seCurrent);
        seToDelete.removeAll(seToAdd);
        seToAdd.removeAll(seCurrent);

        if (!seToDelete.isEmpty()) {
            for (SecuredEntity securedEntity : seToDelete) {
                remove(securedEntity);
            }
        }
        if (!seToAdd.isEmpty()) {
            for (SecuredEntity securedEntity : seToAdd) {
                try {
                    addSecuredEntityForRole(securedEntity, roleName);
                } catch (ValidationException e) {
                    log.warn("Failed to sync secured entity rule for role {}", roleName, e);
                }
            }
        }
    }

    /**
     * Associate a secured entity to a user
     * 
     * @param securedEntity Secured entity to add
     * @param username User name
     * @throws ValidationException A similar rule already exists
     */
    public void addSecuredEntityForUser(SecuredEntity securedEntity, String username) throws ValidationException {

        long count = getEntityManager().createNamedQuery("SecuredEntity.validateByUserName", Long.class).setParameter("userName", username).setParameter("entityCode", securedEntity.getEntityCode())
            .setParameter("entityClass", securedEntity.getEntityClass()).getSingleResult();

        if (count > 0) {
            throw new ValidationException("A similar rule already exists", "securedEntity.error.duplicate");
        }

        securedEntity.setUserName(username);
        create(securedEntity);
    }

    /**
     * Associate a secured entity to a role
     * 
     * @param securedEntity Secured entity to add
     * @param roleName Role name
     * @throws ValidationException A similar rule already exists
     */
    public void addSecuredEntityForRole(SecuredEntity securedEntity, String roleName) throws ValidationException {

        long count = getEntityManager().createNamedQuery("SecuredEntity.validateByRoleName", Long.class).setParameter("roleName", roleName).setParameter("entityCode", securedEntity.getEntityCode())
            .setParameter("entityClass", securedEntity.getEntityClass()).getSingleResult();

        if (count > 0) {
            throw new ValidationException("A similar rule already exists", "securedEntity.error.duplicate");
        }

        securedEntity.setRoleName(roleName);
        create(securedEntity);
    }
}