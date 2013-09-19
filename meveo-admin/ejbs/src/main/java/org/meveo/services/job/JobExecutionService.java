/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.services.job;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.base.PersistenceService;

@Stateless
public class JobExecutionService extends PersistenceService<JobExecutionResultImpl> {

	@Inject
	TimerEntityService timerEntityService;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void executeJob(String jobName, TimerInfo info, Provider provider) {
		try {
			Job jobInstance = TimerEntityService.jobEntries.get(jobName);
			JobExecutionResult result = jobInstance.execute(info.getParametres(), provider);
			persistResult(jobInstance, result, info, provider);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void persistResult(Job job, JobExecutionResult result, TimerInfo info,
			Provider provider) {
		try {
			log.info("JobExecutionService persistResult...");
			JobExecutionResultImpl entity = JobExecutionResultImpl.createFromInterface(job
					.getClass().getSimpleName(), result);
			if (!entity.isDone()
					|| (entity.getNbItemsCorrectlyProcessed()
							+ entity.getNbItemsProcessedWithError() + entity
								.getNbItemsProcessedWithWarning()) > 0) {

				create(entity, null, provider);
				log.info("persistResult entity.isDone()=" + entity.isDone());
				if (!entity.isDone()) {
					executeJob(job.getClass().getSimpleName(), info, provider);
				} else if(info.getFollowingTimerId()!=null && info.getFollowingTimerId()>0) {
					try{
						TimerEntity timerEntity = timerEntityService.findById(info.getFollowingTimerId());
						executeJob(timerEntity.getJobName(),(TimerInfo)timerEntity.getTimerHandle().getTimer().getInfo(),provider);
					} catch(Exception e){
						log.warn("persistResult cannot excute the following job.=" +info.getFollowingTimerId());
					}
				}
			} else {
				log.info(job.getClass().getName() + ": nothing to do");
				if(info.getFollowingTimerId()!=null && info.getFollowingTimerId()>0) {
					try{
						TimerEntity timerEntity = timerEntityService.findById(info.getFollowingTimerId());
						executeJob(timerEntity.getJobName(),(TimerInfo)timerEntity.getTimerHandle().getTimer().getInfo(),provider);
					} catch(Exception e){
						log.warn("persistResult cannot excute the following job.=" +info.getFollowingTimerId());
					}
				}
			}
		} catch (Exception e) {// FIXME:BusinessException e) {
			e.printStackTrace();
		}
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

	public long countJobsToDelete(String jobName, Date date) {
		long result = 0;
		if (date != null) {
			String sql = "select t from JobExecutionResultImpl t";
			QueryBuilder qb = new QueryBuilder(sql);// FIXME:.cacheable();
			if (StringUtils.isEmpty(jobName)) {
				qb.addCriterion("t.jobName", "=", jobName, false);
			}
			qb.addCriterion("t.startDate", "<", date, false);
			result = qb.count(getEntityManager());
		}
		return result;
	}

	public int delete(String jobName, Date date) {
		String sql = "delete from JobExecutionResultImpl t";
		QueryBuilder qb = new QueryBuilder(sql);// FIXME:.cacheable();
		qb.addCriterion("t.jobName", "=", jobName, false);
		qb.addCriterion("t.startDate", "<", date, false);
		return qb.getQuery(getEntityManager()).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public List<JobExecutionResultImpl> find(String jobName, PaginationConfiguration configuration) {
		return getFindQuery(jobName, configuration).find(getEntityManager());
	}

	public long count(String jobName, PaginationConfiguration configuration) {
		return getFindQuery(jobName, configuration).count(getEntityManager());
	}

}
