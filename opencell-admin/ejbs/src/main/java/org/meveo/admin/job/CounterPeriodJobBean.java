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
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.CounterInstanceService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.slf4j.Logger;

/**
 * The Class CounterPeriodJobBean.
 * 
 * @author Mbarek-Ay
 */
@Stateless
public class CounterPeriodJobBean extends BaseJobBean {

    @Inject
    private Logger log;

    @Inject
    private CounterInstanceService counterInstanceService;

    @Inject
    private CounterTemplateService counterTemplateService;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)

    private List<Long> getCounterInstances() {
        List<CounterTemplate> counterTemplates = counterTemplateService.listAll();
        List<Long> counterInstances = new ArrayList<Long>();
        if (!counterTemplates.isEmpty()) {
            for (CounterTemplate counterTemplate : counterTemplates) {
                counterInstances.addAll(counterInstanceService.findByCounterAndAccount(counterTemplate.getCode(), counterTemplate.getCounterLevel()));
            }
        }
        return counterInstances;
    }

    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running with parameter={}", jobInstance.getParametres());

        Date applicationDate = (Date) this.getParamOrCFValue(jobInstance, "applicationDate");

        try {
            List<Long> counterInstances = getCounterInstances();
            if (!counterInstances.isEmpty()) {
                for (Long id : counterInstances) {
                    CounterInstance counterInstance = counterInstanceService.findById(id);

                    // accumulator chargeInstance
                    for (ChargeInstance chargeInstance : counterInstance.getChargeInstances()) {
                        counterInstanceService.createCounterPeriodIfMissing(counterInstance, applicationDate != null ? applicationDate : new Date(), counterInstance.getServiceInstance().getSubscriptionDate(),
                            chargeInstance);
                    }
                    // UsageChargeInstances
                    for (ChargeInstance chargeInstance : counterInstance.getUsageChargeInstances()) {
                        counterInstanceService.createCounterPeriodIfMissing(counterInstance, applicationDate != null ? applicationDate : new Date(), counterInstance.getServiceInstance().getSubscriptionDate(),
                            chargeInstance);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to get or create counterPeriod", e);
            result.registerError(e.getMessage());
            result.addReport(e.getMessage());
        }

    }

}