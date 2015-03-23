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
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class TimerEntityService extends PersistenceService<TimerEntity> {
	public static Map<JobCategoryEnum, HashMap<String, String>> jobEntries = new HashMap<JobCategoryEnum, HashMap<String, String>>();
	public static Map<Long, Timer> jobTimers = new HashMap<Long, Timer>();

	@Resource
	private TimerService timerService;

	@Inject
	private UserService userService;

	private static Logger log = LoggerFactory.getLogger(TimerEntityService.class);

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
				if (!jobEntries.containsKey(job.getClass().getSimpleName())) {
					log.debug("registerJob " + job.getClass().getSimpleName() + " into existing category "
							+ job.getJobCategory());
					Map<String, String> jobs = jobEntries.get(job.getJobCategory());
					jobs.put(job.getClass().getSimpleName(), "java:global/meveo/"+job.getClass().getSimpleName());
				}
			} else {
				log.debug("registerJob " + job.getClass().getSimpleName() + " into new category " + job.getJobCategory());
				HashMap<String, String> jobs = new HashMap<String, String>();
				jobs.put(job.getClass().getSimpleName(), "java:global/meveo/"+job.getClass().getSimpleName());
				jobEntries.put(job.getJobCategory(), jobs);
			}
			job.getJobExecutionService().getTimerEntityService().startTimers(job);
		}
	}

	public Collection<Timer> getTimers() {
		return timerService.getTimers();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void startTimers(Job job) {
		// job.cleanAllTimers();
		@SuppressWarnings("unchecked")
		List<TimerEntity> timerEntities = getEntityManager().createQuery("from TimerEntity t where t.jobName=:jobName")
				.setParameter("jobName", job.getClass().getSimpleName()).getResultList();

		if (timerEntities != null) {
			log.debug("Starting " + timerEntities.size() + " timers for " + job.getClass().getSimpleName());

			for (TimerEntity timerEntity : timerEntities) {
				jobTimers.put(timerEntity.getId(),
						job.createTimer(timerEntity.getScheduleExpression(), timerEntity.getTimerInfo()));
			}
		}
	}

	
	public void create(TimerEntity entity) throws BusinessException {
		InitialContext ic;
		try {
			ic = new InitialContext();
			if (jobEntries.containsKey(entity.getJobCategoryEnum())) {
				HashMap<String, String> jobs = jobEntries.get(entity.getJobCategoryEnum());
				if (jobs.containsKey(entity.getJobName())) {
					Job job = (Job) ic.lookup(jobs.get(entity.getJobName()));
					jobTimers.put(entity.getId(), job.createTimer(entity.getScheduleExpression(), entity.getTimerInfo()));
				}
				entity.getTimerInfo().setJobName(entity.getJobName());
				if (getCurrentUser() == null) {
					throw new BusinessException("User must be logged in to perform this action.");
				}
				entity.getTimerInfo().setUserId(getCurrentUser().getId());
				if (entity.getFollowingTimer() != null) {
					entity.getTimerInfo().setFollowingTimerId(entity.getFollowingTimer().getId());
				}
				super.create(entity);
			}
		} catch (NamingException e) {
			throw new BusinessException(e);
		}
	}

	public TimerEntity update(TimerEntity entity) {
		log.info("update " + entity.getJobName());
		InitialContext ic;
		try {
			ic = new InitialContext();
			if (jobEntries.containsKey(entity.getJobCategoryEnum())) {
				HashMap<String, String> jobs = jobEntries.get(entity.getJobCategoryEnum());
				if (jobs.containsKey(entity.getJobName())) {
					Job job = (Job) ic.lookup(jobs.get(entity.getJobName()));
					jobTimers.put(entity.getId(), job.createTimer(entity.getScheduleExpression(), entity.getTimerInfo()));
				}
				Timer timer = jobTimers.get(entity.getId());
				log.info("Cancelling existing " + timer.getTimeRemaining() / 1000 + " sec");
				timer.cancel();
				if (entity.getFollowingTimer() != null) {
					entity.getTimerInfo().setFollowingTimerId(entity.getFollowingTimer().getId());
				}
				return super.update(entity);
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}

		return entity;
	}

	public void remove(TimerEntity entity) {// FIXME: throws BusinessException{
		Timer timer = jobTimers.get(entity.getId());
		timer.cancel();
		jobTimers.remove(entity.getId());

		super.remove(entity);
	}

	public void execute(TimerEntity entity) throws BusinessException {
		log.info("execute {}", entity.getJobName());
		InitialContext ic;
		try {
			ic = new InitialContext();
			if (jobEntries.containsKey(entity.getJobCategoryEnum())) {
				HashMap<String, String> jobs = jobEntries.get(entity.getJobCategoryEnum());
				if (entity.getTimerInfo().isActive() && jobs.containsKey(entity.getJobName())) {
	
					Job job = (Job) ic.lookup(jobs.get(entity.getJobName()));
	
					User currentUser = userService.findById(entity.getTimerInfo().getUserId());
					job.execute(entity.getTimerInfo() != null ? entity.getTimerInfo() : null, currentUser);
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public void manualExecute(TimerEntity entity) throws BusinessException {
		log.info("manual execute " + entity.getJobName());
		InitialContext ic;
		try {
			ic = new InitialContext();
			User currentUser = userService.findById(entity.getTimerInfo().getUserId());
			if (entity.getTimerInfo() != null && currentUser.getProvider().getId() != getCurrentProvider().getId()) {
				throw new BusinessException("Not authorized to execute this job");
			}
	
			if (jobEntries.containsKey(entity.getJobCategoryEnum())) {
				HashMap<String, String> jobs = jobEntries.get(entity.getJobCategoryEnum());
				if (jobs.containsKey(entity.getJobName())) {
					Job job = (Job) ic.lookup(jobs.get(entity.getJobName()));
					job.execute(entity.getTimerInfo() != null ? entity.getTimerInfo() : null, getCurrentUser());
				}
			} else {
				throw new BusinessException("cannot find job category " + entity.getJobCategoryEnum());
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public TimerEntity getByTimer(Timer timer) {
		Set<Map.Entry<Long, Timer>> entrySet = jobTimers.entrySet();
		for (Map.Entry<Long, Timer> entry : entrySet) {
			if (entry.getValue() == timer) {
				return findById(entry.getKey());
			}
		}

		return null;
	}

	private QueryBuilder getFindQuery(PaginationConfiguration configuration) {
		String sql = "select distinct t from TimerEntity t";
		QueryBuilder qb = new QueryBuilder(sql);// FIXME: .cacheable(); there is
												// no cacheable in MEVEO
												// QueryBuilder
		qb.addCriterionEntity("provider", getCurrentProvider());
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

}
