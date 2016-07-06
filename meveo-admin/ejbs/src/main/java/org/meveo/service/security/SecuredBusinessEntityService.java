package org.meveo.service.security;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;
import org.slf4j.Logger;

/**
 * SecuredBusinessEntity Service base class.
 *
 * @author Tony Alejandro
 */
public abstract class SecuredBusinessEntityService {
	@Inject
	protected Logger log;

	public abstract BusinessEntity getEntityByCode(String code, User user);

	public abstract List<? extends BusinessEntity> list();

	public abstract Class<? extends BusinessEntity> getEntityClass();

	public static boolean isEntityAllowed(BusinessEntity entity, User user, SecuredBusinessEntityServiceFactory factory, Map<Class<?>, Set<SecuredEntity>> securedEntitiesMap,
			boolean isParentEntity) {
		// Doing this check first allows verification without going to DB.
		if (entityFoundInSecuredEntities(entity, user.getSecuredEntities())) {
			// Match was found authorization successful
			return true;
		}

		// Check if entity exists.
		if (entity != null) {
			// Check if entity's type is restricted to a specific group of
			// entities. i.e. only specific Customers, CA, BA, etc.
			Set<SecuredEntity> securedEntities = securedEntitiesMap.get(entity.getClass());
			if (securedEntities != null && !securedEntities.isEmpty()) {
				// This means that the entity type is being restricted. Since
				// the entity did not match anything above, the authorization
				// automatically fails.
				return false;
			}
			// Get entity from DB to get parent entities as well.
			SecuredBusinessEntityService service = factory.getService(entity.getClass());
			entity = service.getEntityByCode(entity.getCode(), user);
		}
		if (entity == null && !isParentEntity) {
			// If entity does not exist and it is not a parent entity, then
			// there's no need to check authorization, nothing will be returned
			// anyway.
			return true;
		}
		return entity != null && entity.getParentEntity() != null && isEntityAllowed(entity.getParentEntity(), user, factory, securedEntitiesMap, true);
	}

	private static boolean entityFoundInSecuredEntities(BusinessEntity entity, Set<SecuredEntity> securedEntities) {
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