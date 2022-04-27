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

package org.meveocrm.services.dwh;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.dwh.MeasurableQuantity;
import org.meveo.service.base.BusinessService;

@Stateless
@Deprecated
public class MeasurableQuantityService extends BusinessService<MeasurableQuantity> {

    public Object[] executeMeasurableQuantitySQL(MeasurableQuantity mq) {
        try {
            Query q = getEntityManager().createNativeQuery(mq.getSqlQuery());
            return (Object[]) q.getSingleResult();
        } catch (Exception e) {
            log.error("failed run {} - {}", mq.getSqlQuery(), e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<MeasurableQuantity> listToBeExecuted(Date date) {
        QueryBuilder queryBuilder = new QueryBuilder(MeasurableQuantity.class, "a", null);
        queryBuilder.addCriterionDateRangeToTruncatedToDay("last_measure_date", date, false, false);
        Query query = queryBuilder.getQuery(getEntityManager());
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<MeasurableQuantity> listEditable() {
        QueryBuilder queryBuilder = new QueryBuilder(MeasurableQuantity.class, "a", null);
        queryBuilder.addBooleanCriterion("editable", true);
        Query query = queryBuilder.getQuery(getEntityManager());
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<MeasurableQuantity> listByCode(String code) {
        QueryBuilder queryBuilder = new QueryBuilder(MeasurableQuantity.class, "a", null);
        queryBuilder.addCriterion("code", "=", code, false);
        Query query = queryBuilder.getQuery(getEntityManager());
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<MeasurableQuantity> listByCodeAndDim(String measurableQuantityCode, String dimension1Filter, String dimension2Filter, String dimension3Filter,
            String dimension4Filter) {

        QueryBuilder queryBuilder = new QueryBuilder(MeasurableQuantity.class, "a", null);
        queryBuilder.addCriterion("code", "=", measurableQuantityCode, false);
        if (!StringUtils.isBlank(dimension1Filter)) {
            queryBuilder.addCriterion("dimension1", "=", dimension1Filter, false);
        }
        if (!StringUtils.isBlank(dimension2Filter)) {
            queryBuilder.addCriterion("dimension2", "=", dimension2Filter, false);
        }
        if (!StringUtils.isBlank(dimension3Filter)) {
            queryBuilder.addCriterion("dimension3", "=", dimension3Filter, false);
        }
        if (!StringUtils.isBlank(dimension4Filter)) {
            queryBuilder.addCriterion("dimension4", "=", dimension4Filter, false);
        }
        Query query = queryBuilder.getQuery(getEntityManager());
        return query.getResultList();
    }
}
