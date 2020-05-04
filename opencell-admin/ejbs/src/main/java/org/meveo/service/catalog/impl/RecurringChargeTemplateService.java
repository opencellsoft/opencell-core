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
package org.meveo.service.catalog.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.base.ValueExpressionWrapper;

/**
 * Charge Template service implementation.
 *
 * @author anasseh
 * @lastModifiedVersion 5.0.2
 */
@Stateless
public class RecurringChargeTemplateService extends ChargeTemplateService<RecurringChargeTemplate> {

    /** The calendar service. */
    @Inject
    private CalendarService calendarService;

    /**
     * Gets the nbr recurring chrg not associated.
     *
     * @return the nbr recurring chrg not associated
     */
    public int getNbrRecurringChrgNotAssociated() {
        return ((Long) getEntityManager().createNamedQuery("recurringChargeTemplate.getNbrRecurringChrgNotAssociated", Long.class).getSingleResult()).intValue();
    }

    /**
     * Gets the recurring chrg not associated.
     *
     * @return the recurring chrg not associated
     */
    public List<RecurringChargeTemplate> getRecurringChrgNotAssociated() {
        return (List<RecurringChargeTemplate>) getEntityManager().createNamedQuery("recurringChargeTemplate.getRecurringChrgNotAssociated", RecurringChargeTemplate.class).getResultList();
    }

    /**
     * Match expression.
     *
     * @param expression Expression to evaluate
     * @param serviceInstance Service instance
     * @param serviceTemplate Service template
     * @param recurringChargeTemplate Recurring charge template
     * @param recurringChargeInstance Recurring charge instance
     * @return true, if successful
     * @throws BusinessException the business exception
     */
    public boolean matchExpression(String expression, ServiceInstance serviceInstance, ServiceTemplate serviceTemplate, RecurringChargeTemplate recurringChargeTemplate, RecurringChargeInstance recurringChargeInstance)
            throws BusinessException {
        Boolean result = true;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = constructElContext(expression, serviceInstance, serviceTemplate, recurringChargeTemplate, recurringChargeInstance);

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
        try {
            result = res != null ? (Boolean) res : false;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
        }
        return result;
    }

    /**
     * Construct el context.
     *
     * @param expression Expression to evaluate
     * @param serviceInstance Service instance
     * @param serviceTemplate Service template
     * @param recurringChargeTemplate Recurring charge template
     * @param recurringChargeInstance Recurring charge instance
     * @return the context el map
     */
    private Map<Object, Object> constructElContext(String expression, ServiceInstance serviceInstance, ServiceTemplate serviceTemplate, RecurringChargeTemplate recurringChargeTemplate,
            RecurringChargeInstance recurringChargeInstance) {

        Map<Object, Object> userMap = new HashMap<Object, Object>();

        if (expression.indexOf(ValueExpressionWrapper.VAR_SERVICE_INSTANCE) >= 0) {
            if (serviceInstance == null && recurringChargeInstance != null) {
                serviceInstance = recurringChargeInstance.getServiceInstance();
            }
            userMap.put(ValueExpressionWrapper.VAR_SERVICE_INSTANCE, serviceInstance);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_SERVICE_TEMPLATE) >= 0) {
            if (serviceTemplate != null) {
                userMap.put(ValueExpressionWrapper.VAR_SERVICE_TEMPLATE, serviceTemplate);
            } else if (serviceInstance != null) {
                userMap.put(ValueExpressionWrapper.VAR_SERVICE_TEMPLATE, serviceInstance.getServiceTemplate());
            }
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_CHARGE_TEMPLATE) >= 0 || expression.indexOf(ValueExpressionWrapper.VAR_CHARGE_TEMPLATE_SHORT) >= 0) {

            if (recurringChargeTemplate != null) {
                userMap.put(ValueExpressionWrapper.VAR_CHARGE_TEMPLATE_SHORT, recurringChargeTemplate);
                userMap.put(ValueExpressionWrapper.VAR_CHARGE_TEMPLATE, recurringChargeTemplate);
            } else if (recurringChargeInstance != null) {
                userMap.put(ValueExpressionWrapper.VAR_CHARGE_TEMPLATE_SHORT, recurringChargeInstance.getRecurringChargeTemplate());
                userMap.put(ValueExpressionWrapper.VAR_CHARGE_TEMPLATE, recurringChargeInstance.getRecurringChargeTemplate());

            }
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_CHARGE_INSTANCE) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_CHARGE_INSTANCE, recurringChargeInstance);
        }

        if (expression.indexOf(ValueExpressionWrapper.VAR_PROVIDER) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_PROVIDER, appProvider);
        }

        return userMap;
    }

    /**
     * Evaluate string expression.
     *
     * @param expression the expression
     * @param serviceInstance the service instance
     * @param serviceTemplate the service template
     * @param recurringChargeTemplate the recurring charge template
     * @param recurringChargeInstance Recurring charge instance
     * @return the evaluated string
     * @throws BusinessException the business exception
     */
    public String evaluateStringExpression(String expression, ServiceInstance serviceInstance, ServiceTemplate serviceTemplate, RecurringChargeTemplate recurringChargeTemplate,
            RecurringChargeInstance recurringChargeInstance) throws BusinessException {
        String result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = constructElContext(expression, serviceInstance, serviceTemplate, recurringChargeTemplate, recurringChargeInstance);

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
        try {
            result = (String) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to String but " + res);
        }
        return result;
    }

    /**
     * Gets the calendar from el.
     *
     * @param calendarCodeEl the calendar code el
     * @param serviceInstance the service instance
     * @param serviceTemplate the service template
     * @param recurringChargeTemplate the recurring charge template
     * @param recurringChargeInstance Recurring charge instance
     * @return the calendar from calendar code el
     * @throws BusinessException the business exception
     */
    public Calendar getCalendarFromEl(String calendarCodeEl, ServiceInstance serviceInstance, ServiceTemplate serviceTemplate, RecurringChargeTemplate recurringChargeTemplate,
            RecurringChargeInstance recurringChargeInstance) throws BusinessException {
        String calendarCode = evaluateStringExpression(calendarCodeEl, serviceInstance, serviceTemplate, recurringChargeTemplate, recurringChargeInstance);
        Calendar calendar = calendarService.findByCode(calendarCode);
        if (calendar == null) {
            throw new BusinessException("Cant found calendar by code:" + calendarCode);
        }
        return calendar;
    }
}
