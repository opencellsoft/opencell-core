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

package org.meveo.service.filter.processor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.meveo.admin.exception.FilterException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.filter.PrimitiveFilterCondition;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 *
 */
public class DateProcessor extends PrimitiveFilterProcessor {

    public static final String PREFIX = "date:";

    @Override
    public boolean canProccessCondition(PrimitiveFilterCondition condition) {
        return isPrefixInOperand(condition, PREFIX);
    }

    @Override
    public void process(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition) throws FilterException {
        try {
            ParamBeanFactory paramBeanFactory = (ParamBeanFactory) EjbUtils.getServiceInterface("ParamBeanFactory");

            ParamBean parameters = paramBeanFactory.getInstance();
            String strDateValue = condition.getOperand().substring(PREFIX.length());
            Date dateValue = null;

            SimpleDateFormat sdf = new SimpleDateFormat(parameters.getDateFormat());
            try {
                dateValue = sdf.parse(strDateValue);
            } catch (ParseException e) {
                try {
                    sdf = new SimpleDateFormat(parameters.getDateTimeFormat());
                    dateValue = sdf.parse(strDateValue);
                } catch (ParseException e1) {
                    throw new FilterException(e1.getMessage());
                }
            }
            buildQuery(queryBuilder, condition, dateValue);
        } catch (Exception e) {
            throw new FilterException(e);
        }
    }

    protected void buildQuery(FilteredQueryBuilder queryBuilder, PrimitiveFilterCondition condition, Date dateValue) {
        if ("=".equals(condition.getOperator())) {
            queryBuilder.addCriterionDateTruncatedToDay(condition.getFieldName(), dateValue);
        } else if (">=".equals(condition.getOperator())) {
            queryBuilder.addCriterionDateRangeFromTruncatedToDay(condition.getFieldName(), dateValue);
        } else if ("<=".equals(condition.getOperator())) {
            queryBuilder.addCriterionDateRangeToTruncatedToDay(condition.getFieldName(), dateValue);
        }
    }
}
