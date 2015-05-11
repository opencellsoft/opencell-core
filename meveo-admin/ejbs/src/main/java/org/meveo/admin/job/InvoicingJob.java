/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.job.Job;

@Startup
@Singleton
public class InvoicingJob extends Job {

	@Inject
	private InvoicingJobBean invoicingJobBean;

	@Inject
	private ResourceBundle resourceMessages;

	@Override
	protected void execute(JobExecutionResultImpl result, TimerEntity timerEntity, User currentUser) throws BusinessException {
		invoicingJobBean.execute(result, currentUser,timerEntity);

	}

	@Override
	public JobCategoryEnum getJobCategory() {
		return JobCategoryEnum.INVOICING;
	}

	@Override
	public List<CustomFieldTemplate> getCustomFields(User currentUser) {
		List<CustomFieldTemplate> result = new ArrayList<CustomFieldTemplate>();

		CustomFieldTemplate jobName = new CustomFieldTemplate();
		jobName.setCode("InvoicingJob_nbRuns");
		jobName.setAccountLevel(AccountLevelEnum.TIMER);
		jobName.setActive(true);
		Auditable audit = new Auditable();
		audit.setCreated(new Date());
		audit.setCreator(currentUser);
		jobName.setAuditable(audit);
		jobName.setProvider(currentUser.getProvider());
		jobName.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
		jobName.setFieldType(CustomFieldTypeEnum.LONG);
		jobName.setValueRequired(true);
		result.add(jobName);

		CustomFieldTemplate nbDays = new CustomFieldTemplate();
		nbDays.setCode("InvoicingJob_waitingMillis");
		nbDays.setAccountLevel(AccountLevelEnum.TIMER);
		nbDays.setActive(true);
		Auditable audit2 = new Auditable();
		audit2.setCreated(new Date());
		audit2.setCreator(currentUser);
		nbDays.setAuditable(audit2);
		nbDays.setProvider(currentUser.getProvider());
		nbDays.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
		nbDays.setFieldType(CustomFieldTypeEnum.LONG);
		nbDays.setValueRequired(true);
		result.add(nbDays);

		return result;
	}
}