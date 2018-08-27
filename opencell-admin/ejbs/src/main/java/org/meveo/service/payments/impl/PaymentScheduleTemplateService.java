/**
 * 
 */
package org.meveo.service.payments.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.payments.PaymentScheduleInstance;
import org.meveo.model.payments.PaymentScheduleStatusEnum;
import org.meveo.model.payments.PaymentScheduleTemplate;
import org.meveo.service.base.BusinessService;

/**
 * The Class PaymentScheduleTemplateService.
 *
 * @author anasseh
 */
@Stateless
public class PaymentScheduleTemplateService extends BusinessService<PaymentScheduleTemplate> {

    
    @Inject
    private PaymentScheduleInstanceService paymentScheduleInstanceService;
    
    public PaymentScheduleTemplate update(PaymentScheduleTemplate paymentScheduleTemplate) throws BusinessException {
        update(paymentScheduleTemplate);
        List<PaymentScheduleInstance> listInstances = paymentScheduleTemplate.getPaymentScheduleInstances();
        for( PaymentScheduleInstance paymentScheduleInstance : listInstances) {
            if(paymentScheduleInstance.getStatus() == PaymentScheduleStatusEnum.IN_PROGRESS) {
                paymentScheduleInstanceService.updateAndInstanciate(paymentScheduleInstance);
            }
        }
        
    return paymentScheduleTemplate;
    }
   
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