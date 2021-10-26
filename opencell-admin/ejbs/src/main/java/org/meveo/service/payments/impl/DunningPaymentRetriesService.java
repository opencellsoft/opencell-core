package org.meveo.service.payments.impl;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.model.dunning.DunningPaymentRetry;
import org.meveo.model.dunning.DunningStopReason;
import org.meveo.service.base.PersistenceService;

/**
 * Service implementation to manage DunningPaymentRetries entity.
 * It extends {@link PersistenceService} class
 *
 * @author Mbarek-Ay
 * @version 11.0
 *
 */
@Stateless
public class DunningPaymentRetriesService extends PersistenceService<DunningPaymentRetry> {

    public DunningPaymentRetry findByPaymentMethodAndPsp(DunningPaymentRetry dunningPaymentRetry) {
        try {
            return getEntityManager().createNamedQuery("DunningPaymentRetry.findByPaymentMethodAndPsp", DunningPaymentRetry.class)
                    .setParameter("paymentMethod", dunningPaymentRetry.getPaymentMethod()).setParameter("psp", dunningPaymentRetry.getPsp()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
