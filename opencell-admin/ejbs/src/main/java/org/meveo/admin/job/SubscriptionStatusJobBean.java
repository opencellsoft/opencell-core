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

package org.meveo.admin.job;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.event.qualifier.EndOfTerm;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionRenewal;
import org.meveo.model.billing.SubscriptionRenewal.EndOfTermActionEnum;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.catalog.impl.CalendarService;

/**
 * Job implementation to handle subscription renewal or termination once subscription expires, fire handles renewal notice events
 * 
 * @author HORRI Khalid
 * @author Abdellatif BARI
 * @author Andrius Karpavicius
 */
@Stateless
public class SubscriptionStatusJobBean extends IteratorBasedJobBean<Long> {

    private static final long serialVersionUID = -864194628969723081L;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    @EndOfTerm
    protected Event<Subscription> endOfTermEventProducer;

    private Date untilDate;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::updateSubscriptionStatus, null, null);
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of Subscription Ids to change status
     */
    private Optional<Iterator<Long>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

        untilDate = (Date) this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "untilDate");
        if (untilDate == null) {
            untilDate = new Date();
        }
        List<Long> ids = subscriptionService.getSubscriptionsToRenewOrNotify(untilDate);

        return Optional.of(new SynchronizedIterator<Long>(ids));
    }

    /**
     * Update subscription status
     * 
     * @param subscriptionId Subscription ID
     * @param jobExecutionResult Job execution result
     */
    public void updateSubscriptionStatus(Long subscriptionId, JobExecutionResultImpl jobExecutionResult) throws BusinessException {

        Subscription subscription = subscriptionService.findById(subscriptionId);
        // Handle subscription renewal or termination
        if (subscription.isSubscriptionExpired() && (subscription.getStatus() == SubscriptionStatusEnum.ACTIVE || subscription.getStatus() == SubscriptionStatusEnum.CREATED)) {

            if (subscription.getSubscriptionRenewal().isAutoRenew()) {
                Date subscribedTillDate = subscription.getSubscribedTillDate();
                while (subscribedTillDate.before(untilDate)) {
                    Date calendarDate = new Date();
                    Calendar calendar = new GregorianCalendar();
                    if (subscription.getSubscriptionRenewal().getRenewalTermType() == SubscriptionRenewal.RenewalTermTypeEnum.CALENDAR) {
                        org.meveo.model.catalog.Calendar calendarRenew = CalendarService.initializeCalendar(subscription.getSubscriptionRenewal().getCalendarRenewFor(), subscription.getSubscribedTillDate(),
                            subscription);
                        calendarDate = calendarRenew.nextCalendarDate(subscription.getSubscribedTillDate());
                    } else {
                        calendar.setTime(subscription.getSubscribedTillDate());
                        calendar.add(subscription.getSubscriptionRenewal().getRenewForUnit().getCalendarField(), subscription.getSubscriptionRenewal().getRenewFor());
                        calendarDate = calendar.getTime();
                    }
                    subscription.setSubscribedTillDate(calendarDate);
                    subscription.setRenewed(true);
                    subscription.setRenewalNotifiedDate(null);

                    if (subscription.getSubscriptionRenewal().isExtendAgreementPeriodToSubscribedTillDate()) {
                        subscription.setEndAgreementDate(subscription.getSubscribedTillDate());
                    }

                    subscription = subscriptionService.update(subscription);

                    log.debug("Subscription {} has beed renewed to date {} with end agreement date of {}", subscription.getCode(), subscription.getSubscribedTillDate(), subscription.getEndAgreementDate());
                    subscribedTillDate = subscription.getSubscribedTillDate();
                }

            } else if (subscription.getSubscriptionRenewal().getEndOfTermAction() == EndOfTermActionEnum.SUSPEND) {
                subscriptionService.subscriptionSuspension(subscription, subscription.getSubscribedTillDate());

            } else if (subscription.getSubscriptionRenewal().getEndOfTermAction() == EndOfTermActionEnum.TERMINATE) {
                subscriptionService.terminateSubscription(subscription, subscription.getSubscribedTillDate(), subscription.getSubscriptionRenewal().getTerminationReason(), null);
            }

            // Fire "soon to renew" notification
        } else if (subscription.isFireRenewalNotice() && (subscription.getStatus() == SubscriptionStatusEnum.ACTIVE || subscription.getStatus() == SubscriptionStatusEnum.CREATED)) {
            subscription.setRenewalNotifiedDate(new Date());
            subscription = subscriptionService.update(subscription);
            endOfTermEventProducer.fire(subscription);
        }
    }
}