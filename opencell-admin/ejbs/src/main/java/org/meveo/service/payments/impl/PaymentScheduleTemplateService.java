/**
 * 
 */
package org.meveo.service.payments.impl;

import javax.ejb.Stateless;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.payments.PaymentScheduleTemplate;
import org.meveo.service.base.BusinessService;

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

    
}