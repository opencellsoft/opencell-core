package org.meveo.service.settings.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.model.settings.GlobalSettings;
import org.meveo.service.base.PersistenceService;

@Stateless
public class GlobalSettingsService extends PersistenceService<GlobalSettings> {

    @Override
    public void create(GlobalSettings entity) throws BusinessException {
        checkParameters(entity);
        super.create(entity);
    }

    @Override
    public GlobalSettings update(GlobalSettings entity) throws BusinessException {
        checkParameters(entity);
        return super.update(entity);
    }

    public GlobalSettings findLastOne() {
        try {
            TypedQuery<GlobalSettings> query = getEntityManager().createQuery("from GlobalSettings g order by g.id desc", entityClass).setMaxResults(1);
            return query.getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} found", getEntityClass().getSimpleName());
            return null;
        }
    }

    private void checkParameters(GlobalSettings entity) {
    	if (entity.getQuoteDefaultValidityDelay() == null) {
    		entity.setQuoteDefaultValidityDelay(30);
    	}else if (entity.getQuoteDefaultValidityDelay() <= 0) {
            throw new InvalidParameterException("QuoteDefaultValidityDelay must be greater than 0");
    	}
    }
}
