package org.meveo.service.security;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;
import org.slf4j.Logger;

/**
 * SecuredBusinessEntity Service implementation.
 *
 * @author Tony Alejandro
 */
public abstract class SecuredBusinessEntityService {
	@Inject
	protected Logger log;

	public abstract BusinessEntity getEntityByCode(String code, User user);

	public abstract List<? extends BusinessEntity> list();

	public abstract Class<? extends BusinessEntity> getEntityClass();

	public static boolean isEntityAllowed(BusinessEntity entity, User user, SecuredBusinessEntityServiceFactory factory, boolean isParentEntity) {
		// Doing this check first allows verification without going to DB.
		boolean found = entityFoundInSecuredEntities(entity, user.getSecuredEntities());
		if (found) {
			// Match was found authorization successful
			return true;
		}

		// Check if entity exists. This will also fetch the parent entity.
		String cleanClassName = ReflectionUtils.getCleanClassName(entity.getClass().getTypeName());
		SecuredBusinessEntityService service = factory.getService(cleanClassName);
		entity = service.getEntityByCode(entity.getCode(), user);
		if (entity == null && !isParentEntity) {
			// If entity does not exist, then there's no need to check
			// authorization, nothing will be returned anyway. This only applies
			// to the entity and not its parent.
			return true;
		}
		return entity.getParentEntity() != null && isEntityAllowed(entity.getParentEntity(), user, factory, true);
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