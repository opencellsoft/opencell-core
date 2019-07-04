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
        userMap.put("serviceInstance", serviceInstance);
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
        userMap.put("serviceInstance", serviceInstance);

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
}