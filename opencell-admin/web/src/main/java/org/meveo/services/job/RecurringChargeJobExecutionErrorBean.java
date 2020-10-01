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

package org.meveo.services.job;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.RecurringChargeJobExecutionError;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.job.RecurringChargeJobExecutionErrorService;

/**
 * @author Andrius Karpavicius
 * @lastModifiedVersion 10.0
 */
@Named
@ViewScoped
public class RecurringChargeJobExecutionErrorBean extends BaseBean<RecurringChargeJobExecutionError> {

    private static final long serialVersionUID = 1L;

    @Inject
    private RecurringChargeJobExecutionErrorService recurringChargeJobExecutionErrorService;

    @Inject
    private JobInstanceService jobInstanceService;

    private List<Boolean> columnVisibilitylist;

    private List<JobInstance> jobs;

    public RecurringChargeJobExecutionErrorBean() {
        super(RecurringChargeJobExecutionError.class);
    }

    @Override
    protected IPersistenceService<RecurringChargeJobExecutionError> getPersistenceService() {
        return recurringChargeJobExecutionErrorService;
    }

    /**
     * @return A list of recurring charge related job instances
     */
    public List<JobInstance> getJobs() {

        if (jobs == null) {
            jobs = jobInstanceService.list();

            jobs = jobs.stream().filter(jobInstance -> {

                Class entityClassForErrorLog = jobInstanceService.getJobByName(jobInstance.getJobTemplate()).getTargetEntityClass(jobInstance);
                return entityClassForErrorLog != null && RecurringChargeInstance.class.isAssignableFrom(entityClassForErrorLog);

            }).collect(Collectors.toList());
        }
        return jobs;
    }

    /**
     * Add additional criteria for limiting only by Invoice related job instances
     */
    @Override
    protected Map<String, Object> supplementSearchCriteria(Map<String, Object> searchCriteria) {

        searchCriteria.put("inList jobInstance", getJobs());

        return searchCriteria;
    }

    @PostConstruct
    public void init() {
        columnVisibilitylist = Arrays.asList(true, true, false, false, true, true, true);
    }

    public List<Boolean> getColumnVisibilitylist() {
        return columnVisibilitylist;
    }
}