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

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;


/**
 * The Class UsageRatingJob rate all opened EDRs.
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class UsageRatingJob extends Job {

    /** The usage rating job bean. */
    @Inject
    private UsageRatingJobBean usageRatingJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        usageRatingJobBean.execute(result, jobInstance);
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.RATING;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate nbRuns = new CustomFieldTemplate();
        nbRuns.setCode(CF_NB_RUNS);
        nbRuns.setAppliesTo("JobInstance_UsageRatingJob");
        nbRuns.setActive(true);
        nbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        nbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        nbRuns.setValueRequired(false);
        nbRuns.setDefaultValue("-1");
        nbRuns.setGuiPosition("tab:Configuration:0;field:0");
        result.put(CF_NB_RUNS, nbRuns);

        CustomFieldTemplate waitingMillis = new CustomFieldTemplate();
        waitingMillis.setCode(Job.CF_WAITING_MILLIS);
        waitingMillis.setAppliesTo("JobInstance_UsageRatingJob");
        waitingMillis.setActive(true);
        waitingMillis.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        waitingMillis.setFieldType(CustomFieldTypeEnum.LONG);
        waitingMillis.setValueRequired(false);
        waitingMillis.setDefaultValue("0");
        waitingMillis.setGuiPosition("tab:Configuration:0;field:1");
        result.put(Job.CF_WAITING_MILLIS, waitingMillis);

        CustomFieldTemplate rateUntilDate = new CustomFieldTemplate();
        rateUntilDate.setCode("rateUntilDate");
        rateUntilDate.setAppliesTo("JobInstance_UsageRatingJob");
        rateUntilDate.setActive(true);
        rateUntilDate.setDescription(resourceMessages.getString("jobExecution.rateUntilDateUsage"));
        rateUntilDate.setFieldType(CustomFieldTypeEnum.DATE);
        rateUntilDate.setValueRequired(false);
        rateUntilDate.setMaxValue(50L);
        rateUntilDate.setGuiPosition("tab:Configuration:0;field:2");
        result.put("rateUntilDate", rateUntilDate);
        
        CustomFieldTemplate ratingGroup = new CustomFieldTemplate();
        ratingGroup.setCode("ratingGroup");
        ratingGroup.setAppliesTo("JobInstance_UsageRatingJob");
        ratingGroup.setActive(true);
        ratingGroup.setDescription(resourceMessages.getString("subscription.ratingGroup"));
        ratingGroup.setFieldType(CustomFieldTypeEnum.STRING);
        ratingGroup.setValueRequired(false);
        ratingGroup.setDefaultValue(null);
        ratingGroup.setMaxValue(50L);
        ratingGroup.setGuiPosition("tab:Configuration:0;field:3");
        result.put("ratingGroup", ratingGroup);

        return result;
    }

}