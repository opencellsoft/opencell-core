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
import org.meveo.event.qualifier.EndOfTerm;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.SubscriptionRenewal;
import org.meveo.model.billing.SubscriptionRenewal.EndOfTermActionEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.job.JobExecutionService.JobSpeedEnum;

/**
 * Job implementation to handle service instance renewal or termination once service instance expires, fire handles renewal notice events
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class ServiceStatusJobBean extends IteratorBasedJobBean<Long> {

    private static final long serialVersionUID = 2226065462536318643L;

    @Inject
    private ServiceInstanceService serviceInstanceService;

    @Inject
    @EndOfTerm
    private Event<ServiceInstance> serviceEndOfTermEventProducer;

    private Date untilDate;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::updateServiceStatus, null, null, JobSpeedEnum.FAST);
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
        List<Long> ids = serviceInstanceService.getSubscriptionsToRenewOrNotify(untilDate);

        return Optional.of(new SynchronizedIterator<Long>(ids));
    }

    /**
     * Update service instance status
     * 
     * @param serviceId Service ID
     * @param jobExecutionResult Job execution result
     */
    private void updateServiceStatus(Long serviceId, JobExecutionResultImpl jobExecutionResult) {

        ServiceInstance serviceInstance = serviceInstanceService.findById(serviceId);
        // Handle subscription renewal or termination
        if (serviceInstance.isSubscriptionExpired() && serviceInstance.getStatus() == InstanceStatusEnum.ACTIVE) {

            if (serviceInstance.getServiceRenewal().isAutoRenew()) {

                while (serviceInstance.getSubscribedTillDate() != null && serviceInstance.getSubscribedTillDate().before(untilDate)) {
                    Date calendarDate = new Date();
                    Calendar calendar = new GregorianCalendar();
                    if (serviceInstance.getServiceRenewal().getRenewalTermType() == SubscriptionRenewal.RenewalTermTypeEnum.CALENDAR) {
                        org.meveo.model.catalog.Calendar calendarRenew = CalendarService.initializeCalendar(serviceInstance.getServiceRenewal().getCalendarRenewFor(), serviceInstance.getSubscribedTillDate(),
                            serviceInstance);
                        calendarDate = calendarRenew.nextCalendarDate(serviceInstance.getSubscribedTillDate());
                    } else {
                        calendar.setTime(serviceInstance.getSubscribedTillDate());
                        calendar.add(serviceInstance.getServiceRenewal().getRenewForUnit().getCalendarField(), serviceInstance.getServiceRenewal().getRenewFor());
                        calendarDate = calendar.getTime();
                    }

                    serviceInstance.setSubscribedTillDate(calendarDate);
                }

                serviceInstance.setRenewed(true);
                serviceInstance.setRenewalNotifiedDate(null);
                if (serviceInstance.getServiceRenewal().isExtendAgreementPeriodToSubscribedTillDate()) {
                    serviceInstance.setEndAgreementDate(serviceInstance.getSubscribedTillDate());
                }
                serviceInstance = serviceInstanceService.update(serviceInstance);

                log.debug("ServiceInstance {} has beed renewed to date {} with end agreement date of {}", serviceInstance.getCode(), serviceInstance.getSubscribedTillDate(), serviceInstance.getEndAgreementDate());

            } else if (serviceInstance.getServiceRenewal().getEndOfTermAction() == EndOfTermActionEnum.SUSPEND) {
                serviceInstanceService.serviceSuspension(serviceInstance, serviceInstance.getSubscribedTillDate());

            } else if (serviceInstance.getServiceRenewal().getEndOfTermAction() == EndOfTermActionEnum.TERMINATE) {
                serviceInstanceService.terminateService(serviceInstance, serviceInstance.getSubscribedTillDate(), serviceInstance.getServiceRenewal().getTerminationReason(), null);
            }

            // Fire "soon to renew" notification
        } else if (serviceInstance.isFireRenewalNotice() && serviceInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
            serviceInstance.setRenewalNotifiedDate(new Date());
            serviceInstance = serviceInstanceService.update(serviceInstance);
            serviceEndOfTermEventProducer.fire(serviceInstance);
        }
    }
}