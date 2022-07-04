package org.meveo.service.settings.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.BooleanUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.model.settings.OpenOrderSetting;
import org.meveo.service.base.BusinessService;

import javax.ejb.Stateless;

@Stateless
public class OpenOrderSettingService extends BusinessService<OpenOrderSetting> {

    @Override
    public void create(OpenOrderSetting entity) throws BusinessException {
        checkParameters(entity);
        super.create(entity);
    }


    @Override
    public OpenOrderSetting update(OpenOrderSetting entity) throws BusinessException {

        checkParameters(entity);
        return super.update(entity);
    }

    public void checkParameters(OpenOrderSetting entity) {

        if(entity.getUseOpenOrders() == true) {
            if (BooleanUtils.isTrue(entity.getApplyMaximumValidity()) && entity.getApplyMaximumValidityValue() == null)
                throw new InvalidParameterException("Apply maximum validity must not be null");

            if (BooleanUtils.isTrue(entity.getDefineMaximumValidity()) && entity.getDefineMaximumValidityValue() == null)
                throw new InvalidParameterException("Define maximum validity must not be null");

            if (entity.getApplyMaximumValidityValue() != null && entity.getApplyMaximumValidityValue() < 1)
                throw new InvalidParameterException("Apply maximum validity must be greater than 0");

            if (entity.getDefineMaximumValidityValue() != null && entity.getDefineMaximumValidityValue().longValue() < 1)
                throw new InvalidParameterException("Define maximum validity must be greater than 0");
        }
    }

    public OpenOrderSetting findLastOne() {
        try {
            TypedQuery<OpenOrderSetting> query = getEntityManager().createQuery("from OpenOrderSetting g order by g.id desc", entityClass).setMaxResults(1);
            return query.getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} found", getEntityClass().getSimpleName());
            return null;
        }
    }

}
