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

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.model.dunning.DunningCollectionPlan;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.payments.impl.DunningCollectionPlanService;

@Stateless
public class ResumeDunningCollectionPlanJobBean extends IteratorBasedJobBean<DunningCollectionPlan> {

	private static final long serialVersionUID = 1L;


    @Inject
    private DunningCollectionPlanService dunningCollectionPlanService;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::readData, null, null, this::processData, null, null, null);
    }

    private Optional<Iterator<DunningCollectionPlan>> readData(JobExecutionResultImpl jobExecutionResult) {
        List<DunningCollectionPlan> collectionPlansToResume = dunningCollectionPlanService.findDunningCollectionPlansToResume();
        return Optional.of(new SynchronizedIterator<DunningCollectionPlan>(collectionPlansToResume));
    }

	private void processData(List<DunningCollectionPlan> collectionPlansToResume, JobExecutionResultImpl jobExecutionResult) {
		collectionPlansToResume.stream().forEach(collectionPlanToResume -> dunningCollectionPlanService.resumeCollectionPlan(collectionPlanToResume, false));
	}
}