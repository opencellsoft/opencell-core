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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.CounterInstanceService;
import org.meveo.service.catalog.impl.CounterTemplateService;

/**
 * The Class CounterPeriodJobBean.
 * 
 * @author Mbarek-Ay
 */
@Stateless
public class CounterPeriodJobBean extends IteratorBasedJobBean<Long> {

    private static final long serialVersionUID = -1217889119746250685L;

    @Inject
    private CounterInstanceService counterInstanceService;

    @Inject
    private CounterTemplateService counterTemplateService;

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of counter instance ids
     */
    private Optional<Iterator<Long>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {
        List<CounterTemplate> counterTemplates = counterTemplateService.listAll();
        List<Long> ids = new ArrayList<>();
        if (!counterTemplates.isEmpty()) {
            for (CounterTemplate counterTemplate : counterTemplates) {
                ids.addAll(counterInstanceService.findByCounterAndAccount(counterTemplate.getCode(),counterTemplate.getCounterLevel()));
            }
        }
        return Optional.of(new SynchronizedIterator<Long>(ids));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, null, null, this::getOrCreateCounterPeriod, null, null, null);
    }

    /**
     * Create get or create counterPeriod
     * 
     * @param counterInstanceIds id
     * @param jobExecutionResult Job execution result
     * @throws BusinessException General business exception
     */
    private void getOrCreateCounterPeriod(List<Long> counterInstanceIds, JobExecutionResultImpl jobExecutionResult) throws BusinessException {
        Date applicationDate = (Date) this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "applicationDate");
        try {
            if (!counterInstanceIds.isEmpty()) {
                for (Long id : counterInstanceIds) {
                    // accumulator chargeInstance
                    CounterInstance counterInstance = counterInstanceService.findById(id);
                    for (ChargeInstance chargeInstance : counterInstance.getChargeInstances()) {
                        counterInstanceService.createCounterPeriodIfMissing(counterInstance, applicationDate != null ? applicationDate : new Date(), chargeInstance.getSubscription()!=null?chargeInstance.getSubscription().getSubscriptionDate():new Date(),
                            chargeInstance, null, null, true);
                        break;
                    }
                    // UsageChargeInstances
                    for (ChargeInstance chargeInstance : counterInstance.getUsageChargeInstances()) {
                        counterInstanceService.createCounterPeriodIfMissing(counterInstance, applicationDate != null ? applicationDate : new Date(), chargeInstance.getSubscription()!=null?chargeInstance.getSubscription().getSubscriptionDate():new Date(),
                            chargeInstance, null, null, true);
                        break;
                    }
                }
            }
        } catch (BusinessException e) {
            log.error("Failed to get or create counterPeriod", e);
        }

    }

}