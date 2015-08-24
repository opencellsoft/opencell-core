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
import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
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
public class RatedTransactionsJob extends Job {

    @Inject
    private RatedTransactionsJobBean ratedTransactionsJobBean;

	@Inject
	private ResourceBundle resourceMessages;

    @Override
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(TimerEntity timerEntity, User currentUser) {
		super.execute(timerEntity, currentUser);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, TimerEntity timerEntity, User currentUser) throws BusinessException {
        ratedTransactionsJobBean.execute(result, currentUser,timerEntity);
    }
	
    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.INVOICING;
    }
    
	@Override
	public List<CustomFieldTemplate> getCustomFields(User currentUser) {
		List<CustomFieldTemplate> result = new ArrayList<CustomFieldTemplate>();

		CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
		customFieldNbRuns.setCode("RatedTransactionsJob_nbRuns");
		customFieldNbRuns.setAccountLevel(AccountLevelEnum.TIMER);
		customFieldNbRuns.setActive(true);
		customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
		customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
		customFieldNbRuns.setValueRequired(false);
		customFieldNbRuns.setLongValue(new Long(1));
		result.add(customFieldNbRuns);

		CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
		customFieldNbWaiting.setCode("RatedTransactionsJob_waitingMillis");
		customFieldNbWaiting.setAccountLevel(AccountLevelEnum.TIMER);
		customFieldNbWaiting.setActive(true);
		customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
		customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
		customFieldNbWaiting.setLongValue(new Long(0));
		customFieldNbWaiting.setValueRequired(false);
		result.add(customFieldNbWaiting);

		return result;
	}
}