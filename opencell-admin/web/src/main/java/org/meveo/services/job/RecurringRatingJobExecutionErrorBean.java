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

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.RecurringRatingJobExecutionError;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.job.RecurringRatingJobExecutionErrorService;

/**
 * @author Andrius Karpavicius
 * @lastModifiedVersion 10.0
 */
@Named
@ViewScoped
public class RecurringRatingJobExecutionErrorBean extends BaseBean<RecurringRatingJobExecutionError> {

    private static final long serialVersionUID = 1L;

    @Inject
    private RecurringRatingJobExecutionErrorService recurringRatingJobExecutionErrorService;

    @Inject
    private JobInstanceService jobInstanceService;

    private List<Boolean> columnVisibilitylist;

    public RecurringRatingJobExecutionErrorBean() {
        super(RecurringRatingJobExecutionError.class);
    }

    @Override
    protected IPersistenceService<RecurringRatingJobExecutionError> getPersistenceService() {
        return recurringRatingJobExecutionErrorService;
    }

    /**
     * @return A list of recurring charge rating job instances
     */
    public List<JobInstance> getRecurringChargeRatingJobs() {
        return jobInstanceService.listByJobType("RecurringRatingJob");
    }

    @PostConstruct
    public void init() {
        columnVisibilitylist = Arrays.asList(true, true, false, false, true, true, true);
    }

    public List<Boolean> getColumnVisibilitylist() {
        return columnVisibilitylist;
    }
}