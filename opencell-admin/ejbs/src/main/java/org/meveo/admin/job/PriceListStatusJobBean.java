package org.meveo.admin.job;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.pricelist.PriceList;
import org.meveo.model.pricelist.PriceListStatusEnum;
import org.meveo.service.catalog.impl.PriceListService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class PriceListStatusJobBean extends IteratorBasedJobBean<Long> {

    @Inject
    private PriceListService priceListService;

    @Override
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::processPriceList, null, null, null);
    }

    private Optional<Iterator<Long>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {
        return Optional.of(new SynchronizedIterator<>(priceListService.getExpiredOpenPriceList()
                                                                      .stream()
                                                                      .map(PriceList::getId)
                                                                      .collect(Collectors.toList())));
    }

    private void processPriceList(Long priceListId, JobExecutionResultImpl jobExecutionResult) {
        PriceList priceListToUpdate = priceListService.findById(priceListId);
        priceListToUpdate.setStatus(PriceListStatusEnum.CLOSED);
        priceListService.update(priceListToUpdate);
    }
}
