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
 * Checks for data records that have exceeded the maximum storage duration
 * specified in GDPRConfiguration. This job runs monthly.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Stateless
public class GDPRJob extends Job {

	@Inject
	private GDPRJobBean gdprJobBean;

	@Override
	protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
		gdprJobBean.execute(result, jobInstance.getParametres());
	}

	@Override
	public JobCategoryEnum getJobCategory() {
		return MeveoJobCategoryEnum.DWH;
	}
    
	@Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();

        CustomFieldTemplate customQueryListInvoice = new CustomFieldTemplate();
        customQueryListInvoice.setCode("GDPRcustomQueryListInvoice");
        customQueryListInvoice.setAppliesTo("JobInstance_GDPRJob");
        customQueryListInvoice.setActive(true);
        customQueryListInvoice.setDescription(resourceMessages.getString("GDPRJob.queryListInvoiceIDs"));
        customQueryListInvoice.setFieldType(CustomFieldTypeEnum.TEXT_AREA);
        customQueryListInvoice.setValueRequired(false);
        customQueryListInvoice.setDefaultValue("");
        customQueryListInvoice.setGuiPosition("tab:Custom fields:0;fieldGroup:Configuration:0;field:0");
        result.put("GDPRcustomQueryListInvoice", customQueryListInvoice);

        return result;
    }
}
