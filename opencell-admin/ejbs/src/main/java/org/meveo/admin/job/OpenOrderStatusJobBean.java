package org.meveo.admin.job;

import static java.util.List.of;
import static org.meveo.model.ordering.OpenOrderStatusEnum.IN_USE;
import static org.meveo.model.ordering.OpenOrderStatusEnum.NEW;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.ordering.OpenOrder;
import org.meveo.service.order.OpenOrderService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.Iterator;
import java.util.Optional;

@Stateless
public class OpenOrderStatusJobBean extends IteratorBasedJobBean<Long> {

    @Inject
    private OpenOrderService openOrderService;

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance,
                this::initJobAndGetDataToProcess, this::processOpenOrder, null, null, null);
    }

    private Optional<Iterator<Long>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {
        return Optional.of(new SynchronizedIterator<>(openOrderService.listOpenOrderIdsByStatus(of(NEW, IN_USE))));
    }

    private void processOpenOrder(Long openOrderId, JobExecutionResultImpl jobExecutionResult) {
        OpenOrder openOrder = openOrderService.findById(openOrderId);
        openOrderService.changeStatus(openOrder);
    }
}