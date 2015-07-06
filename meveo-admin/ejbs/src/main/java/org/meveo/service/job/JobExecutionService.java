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
package org.meveo.service.job;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.naming.InitialContext;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.base.PersistenceService;

@Stateless
public class JobExecutionService extends PersistenceService<JobExecutionResultImpl> {

	@Inject
	private JobInstanceService jobInstanceService;

	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void executeJob(String jobName, JobInstance jobInstance, User currentUser, JobCategoryEnum jobCategory) {
		try {
			HashMap<String, String> jobs = JobInstanceService.jobEntries.get(jobCategory);
			InitialContext ic = new InitialContext();
			Job job = (Job) ic.lookup(jobs.get(jobName));
			job.execute(jobInstance, currentUser);
		} catch (Exception e) {
			log.error("failed to execute timer job",e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void persistResult(Job job, JobExecutionResult result, JobInstance jobInstance, User currentUser, JobCategoryEnum jobCategory) {
		try {
			log.info("JobExecutionService persistResult...");
			JobExecutionResultImpl entity = JobExecutionResultImpl.createFromInterface(job.getClass().getSimpleName(), result);
			if (!entity.isDone() || (entity.getNbItemsCorrectlyProcessed() + entity.getNbItemsProcessedWithError() + entity.getNbItemsProcessedWithWarning()) > 0) {

				create(entity, currentUser, currentUser.getProvider());
				log.info("PersistResult entity.isDone()=" + entity.isDone());
				if (!entity.isDone()) {
					executeJob(job.getClass().getSimpleName(), jobInstance, currentUser, jobCategory);
				} else if (jobInstance.getFollowingJob() != null) {
					try {
							executeJob(jobInstance.getFollowingJob().getJobTemplate(), jobInstance.getFollowingJob(), currentUser, jobInstance.getFollowingJob().getJobCategoryEnum());
						
					} catch (Exception e) {
						log.warn("PersistResult cannot excute the following jobs.");
					}
				}
			} else {
				log.info(job.getClass().getName() + ": nothing to do");

				if (jobInstance.getFollowingJob() != null ) {
					try {
						executeJob(jobInstance.getFollowingJob().getJobTemplate(), jobInstance.getFollowingJob(), currentUser, jobInstance.getFollowingJob().getJobCategoryEnum());
					
				} catch (Exception e) {
					log.warn("PersistResult cannot excute the following jobs.");
				}
				}
			}
		} catch (Exception e) {// FIXME:BusinessException e) {
			log.error("error on persistResult",e);
		}
		log.info("JobExecutionService persistResult End");
	}

	private QueryBuilder getFindQuery(String jobName, PaginationConfiguration configuration) {
		String sql = "select distinct t from JobExecutionResultImpl t";
		QueryBuilder qb = new QueryBuilder(sql);// FIXME:.cacheable();

		if (!StringUtils.isEmpty(jobName)) {
			qb.addCriterion("t.jobName", "=", jobName, false);
		}
		qb.addPaginationConfiguration(configuration);

		return qb;
	}

	public long countJobsToDelete(String jobName, Date date,Provider currentProvider) {
		long result = 0;
		if (date != null) {
			String sql = "select t from JobExecutionResultImpl t";
			QueryBuilder qb = new QueryBuilder(sql);// FIXME:.cacheable();
			if (!StringUtils.isEmpty(jobName)) {
				qb.addCriterion("t.jobName", "=", jobName, false);
			}
			qb.addCriterion("t.startDate", "<", date, false);
			qb.addCriterionEntity("t.provider", currentProvider);
			result = qb.count(getEntityManager());
		}

		return result;
	}

	public int delete(String jobName, Date date,Provider provider) {
		String sql = "delete from JobExecutionResultImpl t";
		QueryBuilder qb = new QueryBuilder(sql);// FIXME:.cacheable();
		qb.addCriterion("t.jobName", "=", jobName, false);
		qb.addCriterionDateRangeToTruncatedToDay("t.startDate", date);
		qb.addCriterionEntity("t.provider", provider);

		return qb.getQuery(getEntityManager()).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public List<JobExecutionResultImpl> find(String jobName, PaginationConfiguration configuration) {
		return getFindQuery(jobName, configuration).find(getEntityManager());
	}

	public long count(String jobName, PaginationConfiguration configuration) {
		return getFindQuery(jobName, configuration).count(getEntityManager());
	}

	public JobInstanceService getJobInstanceService() {
		return jobInstanceService;
	}

}
