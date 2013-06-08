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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.ejb.TimerHandle;
import javax.inject.Inject;

import org.jboss.seam.security.Identity;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.crm.impl.ProviderService;

@Stateless
public class TimerEntityService extends PersistenceService<TimerEntity> {

	public static HashMap<String, Job> jobEntries = new HashMap<String, Job>();

	@Inject
	Identity identity;
	
	@Inject
	ProviderService providerService;

	/**
	 * Used by job instance classes to register themselves to the timer service
	 * 
	 * @param name
	 *            unique name in the application, used by the admin to manage
	 *            timers
	 * @param description
	 *            describe the task realized by the job
	 * @param JNDIName
	 *            used to instanciate the implementation to execute the job
	 *            (instantiacion class must be a session EJB)
	 */
	public static void registerJob(Job job) {
		if (jobEntries.containsKey(job.getClass().getSimpleName())) {
			throw new RuntimeException(job.getClass().getSimpleName() + " already registered.");
		}
		jobEntries.put(job.getClass().getSimpleName(), job);
	}

	public void create(TimerEntity entity) {// FIXME: throws BusinessException{
		if (jobEntries.containsKey(entity.getJobName())) {
			Job job = jobEntries.get(entity.getJobName());
			entity.getInfo().setJobName(entity.getJobName());
			entity.getInfo().setProviderId(getCurrentProvider().getId());
			if(entity.getFollowingTimer()!=null){
				entity.getInfo().setFollowingTimerId(entity.getFollowingTimer().getId());
			}
			TimerHandle timerHandle = job.createTimer(entity.getScheduleExpression(),
					entity.getInfo());
			entity.setTimerHandle(timerHandle);
			super.create(entity);
		}
	}

	public void update(TimerEntity entity) {// FIXME: throws BusinessException{
		log.info("update " + entity.getJobName());
		if (jobEntries.containsKey(entity.getJobName())) {
			Job job = jobEntries.get(entity.getJobName());
			TimerHandle timerHandle = entity.getTimerHandle();
			log.info("cancelling existing " + timerHandle.getTimer().getTimeRemaining() / 1000
					+ " sec");
			timerHandle.getTimer().cancel();
			if(entity.getFollowingTimer()!=null){
				entity.getInfo().setFollowingTimerId(entity.getFollowingTimer().getId());
			}
			timerHandle = job.createTimer(entity.getScheduleExpression(), entity.getInfo());
			entity.setTimerHandle(timerHandle);
			super.update(entity);
		}
	}

	public void remove(TimerEntity entity) {// FIXME: throws BusinessException{
		TimerHandle timerHandle = entity.getTimerHandle();
		timerHandle.getTimer().cancel();
		super.remove(entity);
	}

	public void execute(TimerEntity entity) throws BusinessException {
		log.info("execute " + entity.getJobName());
		if (entity.getInfo().isActive() && jobEntries.containsKey(entity.getJobName())) {
			Job job = jobEntries.get(entity.getJobName());
			Provider provider = providerService.findById(entity.getInfo().getProviderId());
			job.execute(entity.getInfo() != null ? entity.getInfo().getParametres() : null,
					provider);
		}
	}

	public JobExecutionResult manualExecute(TimerEntity entity) throws BusinessException {
		JobExecutionResult result = null;
		log.info("manual execute " + entity.getJobName());
		if(entity.getInfo() != null && entity.getInfo().getProviderId()!=getCurrentProvider().getId()){
			throw new BusinessException("not authorized to execute this job");
		}
		if (jobEntries.containsKey(entity.getJobName())) {
			Job job = jobEntries.get(entity.getJobName());
			result = job.execute(
					entity.getInfo() != null ? entity.getInfo().getParametres() : null,
							getCurrentProvider());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public TimerEntity findByTimerHandle(TimerHandle timerHandle) {
		String sql = "select distinct t from TimerEntity t";
		QueryBuilder qb = new QueryBuilder(sql);// FIXME: .cacheable();
		qb.addCriterionEntity("t.timerHandle", timerHandle);
		List<TimerEntity> timers = qb.find(getEntityManager());
		return timers.size() > 0 ? timers.get(0) : null;
	}

	private QueryBuilder getFindQuery(PaginationConfiguration configuration) {
		String sql = "select distinct t from TimerEntity t";
		QueryBuilder qb = new QueryBuilder(sql);// FIXME: .cacheable(); there is
												// no cacheable in MEVEO
												// QueryBuilder
		qb.addPaginationConfiguration(configuration);
		return qb;
	}

	@SuppressWarnings("unchecked")
	public List<TimerEntity> find(PaginationConfiguration configuration) {
		return getFindQuery(configuration).find(getEntityManager());
	}

	public long count(PaginationConfiguration configuration) {
		return getFindQuery(configuration).count(getEntityManager());
	}

	public List<Timer> getEjbTimers() {
		List<Timer> timers = new ArrayList<Timer>();

		for (Job job : jobEntries.values()) {
			try {
				// TODO: this class should not refer specific job
				/*
				 * if(job instanceof JobImportDocs || job instanceof JobPurge ||
				 * job instanceof JobDeletedPages || job instanceof
				 * JobExportDocs || job instanceof JobImportPieces || job
				 * instanceof JobPieceTraceability || job instanceof
				 * JobTransfertPrimo){
				 */
				timers.addAll(job.getTimers());
				// }

			} catch (Exception e) {
				log.error(e.getMessage());
			}

		}
		return timers;
	}

}
