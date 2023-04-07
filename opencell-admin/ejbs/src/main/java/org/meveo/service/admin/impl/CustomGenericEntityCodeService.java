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
package org.meveo.service.admin.impl;

import static java.time.LocalDateTime.now;
import static java.util.Arrays.stream;
import static java.util.UUID.randomUUID;
import static org.meveo.commons.utils.EjbUtils.getServiceInterface;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.CustomGenericEntityCode;
import org.meveo.model.sequence.Sequence;
import org.meveo.service.base.BusinessEntityService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.ServiceSingleton;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

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

    @Inject
    private BusinessEntityService businessEntityService;

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
     * Get the generic entity code.
     *
     * @return the generic entity code
     */
    public String getGenericEntityCode(BusinessEntity businessEntity) throws BusinessException {
        String customGenericCode = null;
        CustomGenericEntityCode customGenericEntityCode = findByClass(businessEntity.getClass().getName());
        if (customGenericEntityCode != null) {
            customGenericCode = serviceSingleton.getGenericCode(customGenericEntityCode);
        }
        if (customGenericCode == null) {
            customGenericCode = randomUUID().toString();
            BusinessEntity entity =
                    ((BusinessService) getServiceInterface(businessEntity.getClass())).findByCode(customGenericCode);
            if (entity != null) {
                Optional<Parameter> parameter = stream(businessEntity.getClass()
                        .getAnnotation(GenericGenerator.class).parameters())
                        .findFirst();
                if(parameter.isPresent()) {
                    customGenericCode += businessEntityService.findNextSequenceId(parameter.get().value()).toString();
                } else {
                    customGenericCode += now().getNano();
                }
            }
        }
        return customGenericCode;
    }
}