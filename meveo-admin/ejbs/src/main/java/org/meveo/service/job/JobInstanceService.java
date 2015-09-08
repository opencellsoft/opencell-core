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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.JobDoesNotExistsException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.JobInstanceInfoDto;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class JobInstanceService extends PersistenceService<JobInstance> {
	public static Map<JobCategoryEnum, HashMap<String, String>> jobEntries = new HashMap<JobCategoryEnum, HashMap<String, String>>();
	public static Map<Long, Timer> jobTimers = new HashMap<Long, Timer>();

	@Resource
	private TimerService timerService;

	@Inject
	private UserService userService;
	
	@EJB
	private JobExecutionService jobExecutionService;

	private static Logger log = LoggerFactory.getLogger(JobInstanceService.class);

	/* static boolean timersCleaned = false; */

	static ParamBean paramBean = ParamBean.getInstance();

	static Long defaultProviderId = Long.parseLong(paramBean.getProperty("jobs.autoStart.providerId", "1"));

	static boolean allTimerCleanded = false;

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
		synchronized (jobEntries) {
			if (jobEntries.containsKey(job.getJobCategory())) {
				Map<String, String> jobs = jobEntries.get(job.getJobCategory());
				if (!jobs.containsKey(job.getClass().getSimpleName())) {
					log.debug("registerJob " + job.getClass().getSimpleName() + " into existing category "
							+ job.getJobCategory());
					jobs.put(job.getClass().getSimpleName(), "java:global/"+paramBean.getProperty("meveo.moduleName", "meveo")+"/" + job.getClass().getSimpleName());
				}
			} else {
				log.debug("registerJob " + job.getClass().getSimpleName() + " into new category "
						+ job.getJobCategory());
				HashMap<String, String> jobs = new HashMap<String, String>();
				jobs.put(job.getClass().getSimpleName(), "java:global/"+paramBean.getProperty("meveo.moduleName", "meveo")+"/" + job.getClass().getSimpleName());
				jobEntries.put(job.getJobCategory(), jobs);
			}
			job.getJobExecutionService().getJobInstanceService().startTimers(job);
		}
	}

	public Collection<Timer> getTimers() {
		return timerService.getTimers();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void startTimers(Job job) {
		// job.cleanAllTimers();
		@SuppressWarnings("unchecked")
		List<JobInstance> jobInstances = getEntityManager().createQuery("from JobInstance ji where ji.jobTemplate=:jobName")
		.setParameter("jobName", job.getClass().getSimpleName()).getResultList();

		if (jobInstances != null) {
			int started=0;
			for (JobInstance jobInstance : jobInstances) {
				if(jobInstance.isActive() && jobInstance.getTimerEntity()!=null){
					jobTimers.put(jobInstance.getId(),job.createTimer(jobInstance.getTimerEntity().getScheduleExpression(), jobInstance));
					started++;
				}
			}
			log.debug("Found {} job instances for {}, started {}",jobInstances.size(),job.getClass().getSimpleName(),started);
		}
	}

	public Job getJobByName(String jobName){
		Job result=null;
		try {
			InitialContext ic=new InitialContext();
			result = (Job) ic.lookup("java:global/"+paramBean.getProperty("meveo.moduleName", "meveo")+"/"+jobName);
		} catch (NamingException e) {
			log.error("Failed to get job by name",e);
		}
		return result;
	}

	public void create(JobInstance jobInstance) throws BusinessException {
		InitialContext ic;
		try {
			ic = new InitialContext();
			if (jobEntries.containsKey(jobInstance.getJobCategoryEnum())) {

				if (getCurrentUser() == null) {
					throw new BusinessException("User must be logged in to perform this action.");
				}
				jobInstance.setUserId(getCurrentUser().getId());

				super.create(jobInstance);
				HashMap<String, String> jobs = jobEntries.get(jobInstance.getJobCategoryEnum());
				if (jobs.containsKey(jobInstance.getJobTemplate()) && jobInstance.isActive()) {
					log.debug("job created active, we schedule it and store it with id {}",jobInstance.getId());
					Job job = (Job) ic.lookup(jobs.get(jobInstance.getJobTemplate()));
					jobTimers.put(jobInstance.getId(),job.createTimer(jobInstance.getTimerEntity().getScheduleExpression(), jobInstance));
				} else {
					log.debug("job created inactive, we do not schedule it");
				}
			}
		} catch (NamingException e) {
			throw new BusinessException(e);
		}
	}

	public JobInstance update(JobInstance entity) {
		log.info("update timer {} , id=", entity.getJobTemplate(),entity.getId());
		InitialContext ic;
		try {
			ic = new InitialContext();
			if (jobEntries.containsKey(entity.getJobCategoryEnum())) {
				HashMap<String, String> jobs = jobEntries.get(entity.getJobCategoryEnum());
				if (jobs.containsKey(entity.getJobTemplate())) {
					if (entity.getId() == null) {
						log.info("updating timer entity with null id, something is wrong");
					} else if (jobTimers.containsKey(entity.getId())) {
						try {
							Timer timer = jobTimers.get(entity.getId());
							timer.cancel();
							jobTimers.remove(entity.getId());
							log.info("cancelled timer {} , id=", entity.getJobTemplate(),entity.getId());
						} catch (Exception ex) {
							log.info("cannot cancel timer {}", ex);
						}

					}

					super.update(entity);
					if(entity.isActive()){
						Job job = (Job) ic.lookup(jobs.get(entity.getJobTemplate()));
						log.info("Scheduling job {} : timer {}",job,entity.getId());
						jobTimers.put(entity.getId(),
								job.createTimer(entity.getTimerEntity().getScheduleExpression(), entity));
					}
				} else {
					throw new RuntimeException("cannot find job "+entity.getJobTemplate());
				}
			}
		} catch (NamingException e) {
			log.error("Failed to update job",e);
		}

		return entity;
	}

	public void remove(JobInstance entity) {// FIXME: throws BusinessException{
		log.info("remove timer {} , id=", entity.getJobTemplate(),entity.getId());
		if (entity.getId() == null) {
			log.info("removing timer entity with null id, something is wrong");
		} else if (jobTimers.containsKey(entity.getId())) {
			try {
				Timer timer = jobTimers.get(entity.getId());
				timer.cancel();
			} catch (Exception ex) {
				log.info("cannot cancel timer " + ex);
			}
			jobTimers.remove(entity.getId());
		} else {
			log.info("timer not found, cannot remove it");
		}
		super.remove(entity);
	}

	public void execute(JobInstance entity) throws BusinessException {
		log.info("execute {}", entity.getJobTemplate());
		InitialContext ic;
		try {
			ic = new InitialContext();
			if (jobEntries.containsKey(entity.getJobCategoryEnum())) {
				HashMap<String, String> jobs = jobEntries.get(entity.getJobCategoryEnum());
				if (entity.isActive() && jobs.containsKey(entity.getJobTemplate())) {

					Job job = (Job) ic.lookup(jobs.get(entity.getJobTemplate()));

					User currentUser = userService.findById(entity.getUserId());
					job.execute(entity, currentUser);
				}
			}
		} catch (NamingException e) {
			log.error("Failed to execute timerEntity job",e);
		}
	}

	public void triggerExecution(JobInstance entity, Map<String, String> params) throws BusinessException {
		log.info("triggerExecution jobInstance={} via api", entity.getJobTemplate());
		//TODO customize CF
		manualExecute( entity);
	}

	public void manualExecute(JobInstance entity) throws BusinessException {
		log.info("Manual execute a job {} of type {}", entity.getCode(), entity.getJobTemplate());

		try {

			// Retrieve a timer entity from registered job timers, so if job is launched manually and automatically at the same time, only one will run 
			if (jobTimers.containsKey(entity.getId())){
				entity = (JobInstance) jobTimers.get(entity.getId()).getInfo();
			}

			InitialContext ic = new InitialContext();
			User currentUser = userService.findById(entity.getUserId());
			if ( !currentUser.doesProviderMatch(getCurrentProvider())) {
				throw new BusinessException("Not authorized to execute this job");
			}

			if (jobEntries.containsKey(entity.getJobCategoryEnum())) {
				HashMap<String, String> jobs = jobEntries.get(entity.getJobCategoryEnum());
				if (jobs.containsKey(entity.getJobTemplate())) {
					Job job = (Job) ic.lookup(jobs.get(entity.getJobTemplate()));
					job.execute(entity, getCurrentUser());
				}
			} else {
				throw new BusinessException("Cannot find job category " + entity.getJobCategoryEnum());
			}
		} catch (NamingException e) {
			log.error("failed to manually execute ",e);

		} catch (Exception e){
			log.error("Failed to manually execute a job {} of type {}", entity.getCode(), entity.getJobTemplate(),e);
			throw e;
		}
	}

	public Long executeAPITimer(JobInstanceInfoDto jobInstanceInfoDTO, User currentUser) throws BusinessException {
		log.info("execute timer={} via api",
				StringUtils.isBlank(jobInstanceInfoDTO.getTimerName()) ? jobInstanceInfoDTO.getCode()
						: jobInstanceInfoDTO.getTimerName());
		JobInstance entity = null;
		
		if (!StringUtils.isBlank(jobInstanceInfoDTO.getCode())) {
			entity = findByCode(jobInstanceInfoDTO.getCode(), currentUser.getProvider());
		} else {
			try {
				entity = (JobInstance) getEntityManager()
						.createQuery("FROM JobInstance where code=:codeIN and provider=:providerIN")
						.setParameter("codeIN", jobInstanceInfoDTO.getTimerName())
						.setParameter("providerIN", currentUser.getProvider()).getSingleResult();
			} catch (NoResultException e) {
				log.warn("No job with name={} was found.", jobInstanceInfoDTO.getTimerName());
				entity = null;
			}
		}
		
		if (entity == null) {
			throw new JobDoesNotExistsException(
					StringUtils.isBlank(jobInstanceInfoDTO.getTimerName()) ? jobInstanceInfoDTO.getCode()
							: jobInstanceInfoDTO.getTimerName());
		}
		
		// lazy loading
		if (entity.getCustomFields() != null) {
			Map<String, CustomFieldInstance> map = entity.getCustomFields();
			for (Map.Entry<String, CustomFieldInstance> entry : map.entrySet()) {
				entry.getKey();
			}
		}
	
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		result.setJobInstance(entity);
		InitialContext ic;
		try {
			ic = new InitialContext();
			if (jobEntries.containsKey(entity.getJobCategoryEnum())) {
				HashMap<String, String> jobs = jobEntries.get(entity.getJobCategoryEnum());
				if (jobs.containsKey(entity.getJobTemplate())) {
					Job job = (Job) ic.lookup(jobs.get(entity.getJobTemplate()));
					jobExecutionService.create(result, currentUser, currentUser.getProvider());
					job.execute(entity, result, currentUser);
				}
			} else {
				throw new BusinessException("cannot find job category " + entity.getJobCategoryEnum());
			}
		} catch (NamingException e) {
			log.error("failed to execute API timer", e);
		}
		
		return result.getId();
	}

	public JobInstance getByTimer(Timer timer) {
		Set<Map.Entry<Long, Timer>> entrySet = jobTimers.entrySet();
		for (Map.Entry<Long, Timer> entry : entrySet) {
			if (entry.getValue() == timer) {
				return findById(entry.getKey());
			}
		}

		return null;
	}

	private QueryBuilder getFindQuery(PaginationConfiguration configuration) {
		String sql = "select distinct t from JobInstance t";
		QueryBuilder qb = new QueryBuilder(sql);// FIXME: .cacheable(); there is
		// no cacheable in MEVEO
		// QueryBuilder
		qb.addCriterionEntity("provider", getCurrentProvider());
		qb.addPaginationConfiguration(configuration);
		return qb;
	}

	@SuppressWarnings("unchecked")
	public List<JobInstance> find(PaginationConfiguration configuration) {
		return getFindQuery(configuration).find(getEntityManager());
	}

	public long count(PaginationConfiguration configuration) {
		return getFindQuery(configuration).count(getEntityManager());
	}

	/**
	 * Check if a timer, identifier by a timer id, is running
	 * 
	 * @param timerEntityId Timer entity id
	 * @return True if running
	 */
	public boolean isTimerRunning(Long timerEntityId) {
		Timer timer = jobTimers.get(timerEntityId);

		if (timer != null) {
			try {
				return ((JobInstance) timer.getInfo()).isRunning();
			} catch (Exception e) {
				log.error("Failed to access timer status {}", e);
			}
		}
		return false;
	}

	public JobInstance findByCode(String code,Provider provider) {
		QueryBuilder qb = new QueryBuilder(JobInstance.class, "t");
		qb.addCriterionWildcard("t.code", code, true); 
		qb.addCriterionEntity("provider", provider);
		try {
			return (JobInstance) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}