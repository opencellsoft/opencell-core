package org.meveo.admin.job;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.event.qualifier.EndOfTerm;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionRenewal.EndOfTermActionEnum;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.SubscriptionService;
import org.slf4j.Logger;

@Stateless
public class SubscriptionStatusJobBean extends BaseJobBean {

    @Inject
    private Logger log;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    @EndOfTerm
    protected Event<Subscription> endOfTermEventProducer;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateSubscriptionStatus(JobExecutionResultImpl result, Long subscriptionId) throws BusinessException {
        try {
            Subscription subscription = subscriptionService.findById(subscriptionId);
            // Handle subscription renewal or termination
            if (subscription.isSubscriptionExpired() && (subscription.getStatus() == SubscriptionStatusEnum.ACTIVE || subscription.getStatus() == SubscriptionStatusEnum.CREATED)) {

                if (subscription.getSubscriptionRenewal().isAutoRenew()) {
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(subscription.getSubscribedTillDate());
                    calendar.add(subscription.getSubscriptionRenewal().getRenewForUnit().getCalendarField(), subscription.getSubscriptionRenewal().getRenewFor());
                    subscription.setSubscribedTillDate(calendar.getTime());
                    subscription.setRenewed(true);
                    subscription.setRenewalNotifiedDate(null);

                    if (subscription.getSubscriptionRenewal().isExtendAgreementPeriodToSubscribedTillDate()) {
                        subscription.setEndAgreementDate(subscription.getSubscribedTillDate());
                    }

                    subscription = subscriptionService.update(subscription);

                    log.debug("Subscription {} has beed renewed to date {} with end agreement date of {}", subscription.getCode(), subscription.getSubscribedTillDate(),
                        subscription.getEndAgreementDate());

                } else if (subscription.getSubscriptionRenewal().getEndOfTermAction() == EndOfTermActionEnum.SUSPEND) {
                    subscription = subscriptionService.subscriptionSuspension(subscription, subscription.getSubscribedTillDate());

                } else if (subscription.getSubscriptionRenewal().getEndOfTermAction() == EndOfTermActionEnum.TERMINATE) {
                    subscription = subscriptionService.terminateSubscription(subscription, subscription.getSubscribedTillDate(),
                        subscription.getSubscriptionRenewal().getTerminationReason(), null);
                }

                // Fire "soon to renew" notification
            } else if (subscription.isFireRenewalNotice()
                    && (subscription.getStatus() == SubscriptionStatusEnum.ACTIVE || subscription.getStatus() == SubscriptionStatusEnum.CREATED)) {
                subscription.setRenewalNotifiedDate(new Date());
                subscription = subscriptionService.update(subscription);
                endOfTermEventProducer.fire(subscription);
            }

            result.registerSucces();
        } catch (Exception e) {
            log.error("Failed to process status of subscription {} ", subscriptionId, e);
            result.registerError("Failed to process status of subscription " + subscriptionId + ":" + e.getMessage());
        }
    }
}