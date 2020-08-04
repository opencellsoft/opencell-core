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

/**
 * 
 */
package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.payments.PaymentScheduleTemplate;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;

/**
 * The Class PaymentScheduleTemplateService.
 *
 * @author anasseh
 */
@Stateless
public class PaymentScheduleTemplateService extends BusinessService<PaymentScheduleTemplate> {

    /**
     * Find by service template.
     *
     * @param serviceTemplate the service template
     * @return the payment schedule template
     */
    public PaymentScheduleTemplate findByServiceTemplate(ServiceTemplate serviceTemplate) {
        try {
            QueryBuilder qb = new QueryBuilder(PaymentScheduleTemplate.class, "ps");
            qb.addCriterionEntity("serviceTemplate", serviceTemplate);
            return (PaymentScheduleTemplate) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * Match expression.
     *
     * @param expression the expression
     * @param serviceInstance the service instance
     * @return true, if successful
     * @throws BusinessException the business exception
     */
    public boolean matchExpression(String expression, ServiceInstance serviceInstance) throws BusinessException {
        Boolean result = true;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put(ValueExpressionWrapper.VAR_SERVICE_INSTANCE, serviceInstance);
        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
        try {
            result = (Boolean) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
        }
        return result;
    }

    /**
     * Evaluate amount expression.
     *
     * @param expression the expression
     * @param serviceInstance the service instance
     * @return the big decimal
     */
    public BigDecimal evaluateAmountExpression(String expression, ServiceInstance serviceInstance) {
        BigDecimal result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put(ValueExpressionWrapper.VAR_SERVICE_INSTANCE, serviceInstance);

        Object res = null;
        try {
            res = ValueExpressionWrapper.evaluateExpression(expression, userMap, BigDecimal.class);

            if (res != null) {
                if (res instanceof BigDecimal) {
                    result = (BigDecimal) res;
                } else if (res instanceof Number) {
                    result = new BigDecimal(((Number) res).doubleValue());
                } else if (res instanceof String) {
                    result = new BigDecimal(((String) res));
                } else {
                    log.error("Amount Expression " + expression + " do not evaluate to number but " + res);
                }
            }
        } catch (BusinessException e1) {
            log.error("Amount Expression {} error", expression, e1);

        } catch (Exception e) {
            log.error("Error Amount Expression " + expression, e);
        }
        return result;
    }

    /**
     * Evaluate payment day in month expression.
     *
     * @param expression      the expression
     * @param serviceInstance the service instance
     * @return the big decimal
     */
    public Long evaluatePaymentDayInMonthExpression(String expression, ServiceInstance serviceInstance) {
        Long result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put(ValueExpressionWrapper.VAR_SERVICE_INSTANCE, serviceInstance);

        Object res = null;
        try {
            res = ValueExpressionWrapper.evaluateExpression(expression, userMap, BigDecimal.class);

            if (res != null) {
                if (res instanceof Long) {
                    result = (Long) res;
                } else if (res instanceof Number) {
                    result = new Long(((Number) res).longValue());
                } else if (res instanceof String) {
                    result = Long.valueOf(res.toString());
                } else {
                    log.error("PaymentDayInMonth Expression " + expression + " do not evaluate to number but " + res);
                }
            }
        } catch (BusinessException e1) {
            log.error("PaymentDayInMonth Expression {} error", expression, e1);

        } catch (Exception e) {
            log.error("Error PaymentDayInMonth Expression " + expression, e);
        }
        return result;
    }
}