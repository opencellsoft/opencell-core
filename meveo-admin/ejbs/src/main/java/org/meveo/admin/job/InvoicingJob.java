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

import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

@Startup
@Singleton
public class InvoicingJob extends Job {

	@Inject
	private InvoicingJobBean invoicingJobBean;

	@Inject
	private ResourceBundle resourceMessages;
	
	@Override
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobInstance jobInstance, User currentUser) {
		super.execute(jobInstance, currentUser);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	protected void execute(JobExecutionResultImpl result, JobInstance jobInstance, User currentUser) throws BusinessException {
		invoicingJobBean.execute(result, currentUser,jobInstance);

	}

	@Override
	public JobCategoryEnum getJobCategory() {
		return JobCategoryEnum.INVOICING;
	}

	@Override
	public List<CustomFieldTemplate> getCustomFields(User currentUser) {
		List<CustomFieldTemplate> result = new ArrayList<CustomFieldTemplate>();

		CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
		customFieldNbRuns.setCode("InvoicingJob_nbRuns");
		customFieldNbRuns.setAccountLevel(AccountLevelEnum.TIMER);
		customFieldNbRuns.setActive(true);
		Auditable audit = new Auditable();
		audit.setCreated(new Date());
		audit.setCreator(currentUser);
		customFieldNbRuns.setAuditable(audit);
		customFieldNbRuns.setProvider(currentUser.getProvider());
		customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
		customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
		customFieldNbRuns.setValueRequired(false);
		customFieldNbRuns.setLongValue(new Long(1));
		result.add(customFieldNbRuns);

		CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
		customFieldNbWaiting.setCode("InvoicingJob_waitingMillis");
		customFieldNbWaiting.setAccountLevel(AccountLevelEnum.TIMER);
		customFieldNbWaiting.setActive(true);
		Auditable audit2 = new Auditable();
		audit2.setCreated(new Date());
		audit2.setCreator(currentUser);
		customFieldNbWaiting.setAuditable(audit2);
		customFieldNbWaiting.setProvider(currentUser.getProvider());
		customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
		customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
		customFieldNbWaiting.setLongValue(new Long(0));
		customFieldNbWaiting.setValueRequired(false);
		result.add(customFieldNbWaiting);

		return result;
	}
}