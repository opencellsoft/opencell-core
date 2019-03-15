/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.admin.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.CustomGenericEntityCode;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.ServiceSingleton;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a Service that allows to automatically generate a custom code for a given entity
 *
 * @author Abdellatif BARI
 * @since 7.0
 */

@Stateless
public class CustomGenericEntityCodeService extends PersistenceService<CustomGenericEntityCode> {

    /**
     * The service singleton.
     */
    @Inject
    private ServiceSingleton serviceSingleton;

    public CustomGenericEntityCode findByClass(String entityClass) {
        if (entityClass == null) {
            return null;
        }
        QueryBuilder qb = new QueryBuilder(CustomGenericEntityCode.class, "c");
        qb.addCriterion("entityClass", "=", entityClass, false);

        try {
            return (CustomGenericEntityCode) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Get the custom generic entity code.
     *
     * @param businessEntity the entity.
     * @return the custom generic entity code.
     * @throws BusinessException the business exception
     */
    private String getCustomGenericEntityCode(BusinessEntity businessEntity) throws BusinessException {

        String customGenericCode = null;
        String entityClass = ReflectionUtils.getCleanClassName(businessEntity.getClass().getSimpleName());
        CustomGenericEntityCode customGenericEntityCode = serviceSingleton.getGenericCodeEntity(entityClass);

        if (customGenericEntityCode != null) {
            Map<Object, Object> contextMap = new HashMap<>();
            String sequenceNextValue = StringUtils.getLongAsNChar(customGenericEntityCode.getSequenceCurrentValue(), customGenericEntityCode.getSequenceSize());
            contextMap.put("entity", businessEntity);
            contextMap.put("sequenceNextValue", sequenceNextValue);

            Object value = ValueExpressionWrapper.evaluateExpression(customGenericEntityCode.getCodeEL(), contextMap, String.class);
            if (value instanceof String) {
                customGenericCode = (String) value;
            } else if (value != null) {
                customGenericCode = value.toString();
            }

        }
        return customGenericCode;
    }

    /**
     * Get the generic entity code.
     *
     * @return the generic entity code
     */
    public String getGenericEntityCode(BusinessEntity businessEntity) throws BusinessException {
        String genericEntityCode = UUID.randomUUID().toString();
        // TODO : check if the generated code does not already exist to be sure that the code is unique otherwise an exeption will be lifted.
        // if the code already exists, regenerate a new one until the verification is good.

        String customGenericCode = getCustomGenericEntityCode(businessEntity);
        if (!StringUtils.isBlank(customGenericCode)) {
            return customGenericCode;
        }
        return genericEntityCode;
    }


}