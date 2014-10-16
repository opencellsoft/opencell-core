/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
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
package org.meveo.services.job;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.ejb.TimerHandle;
import javax.inject.Inject;

import org.jboss.seam.security.Identity;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.crm.impl.ProviderService;

@Stateless
public class TimerEntityService extends PersistenceService<TimerEntity> {

	public static HashMap<String, Job> jobEntries = new HashMap<String, Job>();

	@Inject
	Identity identity;
	
	@Inject
	ProviderService providerService;
	
	static ParamBean paramBean = ParamBean.getInstance();

	static Long defaultProviderId = Long.parseLong(paramBean.getProperty("jobs.autoStart.providerId", "1"));
	
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
		job.getJobExecutionService().getTimerEntityService().cleanTimers(job);
	}
	
	@SuppressWarnings("unchecked")
	public void cleanTimers(Job job){
		String jobName=job.getClass().getSimpleName();
		log.info("cleanTimer " + jobName);
		List<TimerHandle> timerHandles=new ArrayList<TimerHandle>();
		Collection<Timer> timers = job.getTimers();
		if(timers!=null){
			log.debug("cleanTimer found " + timers.size()+" ejb timers");
			for(Timer timer : timers){
				TimerEntity timerEntity = findByTimerHandle(timer.getHandle());
				if(timerEntity==null){
					log.warn("EJB timer as no counterPart in database, we cancel the timer");
					timer.cancel();
				}
				else {
					timerHandles.add(timer.getHandle());
				}
			}
		}
		String sql = "select distinct t from TimerEntity t";
		QueryBuilder qb = new QueryBuilder(sql);// FIXME: .cacheable();
		qb.addCriterion("t.jobName", "=",jobName,false);
		List<TimerEntity> timerEntities = qb.find(getEntityManager());
		if(timerEntities!=null){
			log.debug("cleanTimer found " + timers.size()+" timer entities in database");
			for(TimerEntity timerEntity :timerEntities){
				if(!timerHandles.contains(timerEntity.getTimerHandle())){
					log.warn("Database timer as no counterPart in EJB, we delete it from the database");
					timerHandles.remove(timerEntity.getTimerHandle());
					super.remove(timerEntity);
				}
			}
		}
		//now if there is no timer and it is set in the properties that we must start one, then we start one
		String scheduleProperty = paramBean.getProperty("jobs.autoStart."+jobName, "");
		if(timerHandles.size()==0 && scheduleProperty.trim().length()>0){
			TimerEntity entity=new TimerEntity();
			entity.setName(jobName+"_auto");
			entity.setJobName(jobName);
			String[] values = scheduleProperty.split(" ");
			if(values!=null && (values.length==7||values.length==8)){
				entity.setYear(values[6]);
				entity.setMonth(values[5]);
				entity.setDayOfMonth(values[4]);
				entity.setDayOfWeek(values[3]);
				entity.setHour(values[2]);
				entity.setMinute(values[1]);
				entity.setSecond(values[0]);
				entity.setInfo(new TimerInfo());
				if(values.length==8){
					entity.getInfo().setParametres(values[7]);
				}
				entity.setProvider(providerService.findById(defaultProviderId));
				create(entity);
				log.warn("Timer as no instance and was started automatically.");
			} else {
				log.error("incorrect schedule Porperty jobs.autoStart."+jobName+"="+scheduleProperty);
			}
		}
	}


	public void create(TimerEntity entity) {// FIXME: throws BusinessException{
		if (jobEntries.containsKey(entity.getJobName())) {
			Job job = jobEntries.get(entity.getJobName());
			entity.getInfo().setJobName(entity.getJobName());
			entity.getInfo().setProviderId(getCurrentProvider()==null?defaultProviderId:getCurrentProvider().getId());
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
