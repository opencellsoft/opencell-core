package org.meveo.service.security;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.BusinessService;

@Stateless
public class SecuredEntityService extends BusinessService<SecuredEntity> {
	
	public SecuredEntity findByCodeAndUser(String code, User user, Provider provider) {
		QueryBuilder qb = new QueryBuilder(getEntityClass(), "be", null, provider);
        qb.addCriterion("be.code", "=", code, true);
        qb.addCriterionEntity("be.user", user);
        qb.addCriterionEntity("be.provider", provider);

		try {
			return (SecuredEntity) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} of code {} and user {} for provider {} found", getEntityClass().getSimpleName(), code, user.getId(), provider.getId());
            return null;
        } catch (NonUniqueResultException e) {
            log.error("More than one entity of type {} with code {}, user {} and provider {} found", entityClass, code, user, provider);
            return null;
        }
	}

}