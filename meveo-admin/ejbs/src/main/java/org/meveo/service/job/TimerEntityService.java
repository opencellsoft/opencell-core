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
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.model.jobs.TimerInfoDto;
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
			int started=0;
			for (TimerEntity timerEntity : timerEntities) {
			    if(timerEntity.getTimerInfo().isActive()){
			        jobTimers.put(timerEntity.getId(),
						job.createTimer(timerEntity.getScheduleExpression(), timerEntity));
			        started++;
			    }
			}
            log.debug("Found {} timers for {}, started {}",timerEntities.size(),job.getClass().getSimpleName(),started);
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

	public void create(TimerEntity entity) throws BusinessException {
		InitialContext ic;
		try {
			ic = new InitialContext();
			if (jobEntries.containsKey(entity.getJobCategoryEnum())) {
                entity.getTimerInfo().setJobName(entity.getJobName());
                if (getCurrentUser() == null) {
                    throw new BusinessException("User must be logged in to perform this action.");
                }
                entity.getTimerInfo().setUserId(getCurrentUser().getId());
                if (entity.getFollowingTimer() != null) {
                    entity.getTimerInfo().setFollowingTimerId(entity.getFollowingTimer().getId());
                }
                super.create(entity);
				HashMap<String, String> jobs = jobEntries.get(entity.getJobCategoryEnum());
				if (jobs.containsKey(entity.getJobName()) && entity.getTimerInfo().isActive()) {
				    log.debug("job created active, we schedule it and store it with id {}",entity.getId());
					Job job = (Job) ic.lookup(jobs.get(entity.getJobName()));
					jobTimers.put(entity.getId(),
							job.createTimer(entity.getScheduleExpression(), entity));
				} else {
				    log.debug("job created inactive, we do not schedule it");
				}
			}
		} catch (NamingException e) {
			throw new BusinessException(e);
		}
	}

	public TimerEntity update(TimerEntity entity) {
		log.info("update timer {} , id=", entity.getJobName(),entity.getId());
		InitialContext ic;
		try {
			ic = new InitialContext();
			if (jobEntries.containsKey(entity.getJobCategoryEnum())) {
				HashMap<String, String> jobs = jobEntries.get(entity.getJobCategoryEnum());
				if (jobs.containsKey(entity.getJobName())) {
                    if (entity.getId() == null) {
                        log.info("updating timer entity with null id, something is wrong");
                    } else if (jobTimers.containsKey(entity.getId())) {
                        try {
                            Timer timer = jobTimers.get(entity.getId());
                            timer.cancel();
                            jobTimers.remove(entity.getId());
                            log.info("cancelled timer {} , id=", entity.getJobName(),entity.getId());
                        } catch (Exception ex) {
                            log.info("cannot cancel timer {}", ex.getMessage());
                        }
                        
                    }
                    if (entity.getFollowingTimer() != null) {
                        entity.getTimerInfo().setFollowingTimerId(entity.getFollowingTimer().getId());
                    }
                    super.update(entity);
				    if(entity.getTimerInfo().isActive()){
    				    Job job = (Job) ic.lookup(jobs.get(entity.getJobName()));
    				    log.info("Scheduling job {} : timer {}",job,entity.getId());
    					jobTimers.put(entity.getId(),
    							job.createTimer(entity.getScheduleExpression(), entity));
				    }
				} else {
				    throw new RuntimeException("cannot find job "+entity.getJobName());
				}
			}
		} catch (NamingException e) {
			log.error("Failed to update job",e);
		}

		return entity;
	}

	public void remove(TimerEntity entity) {// FIXME: throws BusinessException{
        log.info("remove timer {} , id=", entity.getJobName(),entity.getId());
	    if (entity.getId() == null) {
            log.info("removing timer entity with null id, something is wrong");
        } else if (jobTimers.containsKey(entity.getId())) {
            try {
                Timer timer = jobTimers.get(entity.getId());
                timer.cancel();
            } catch (Exception ex) {
                log.info("cannot cancel timer " + ex.getMessage());
            }
            jobTimers.remove(entity.getId());
        } else {
            log.info("timer not found, cannot remove it");
        }
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
					job.execute(entity, currentUser);
				}
			}
		} catch (NamingException e) {
			log.error("Failed to execute timerEntity job",e);
		}
	}

	public void manualExecute(TimerEntity entity) throws BusinessException {
		log.info("Manual execute a job {} of type {}", entity.getName(), entity.getJobName());
		
		try {
		    
		    // Retrieve a timer entity from registered job timers, so if job is launched manually and automatically at the same time, only one will run 
		    if (jobTimers.containsKey(entity.getId())){
		        entity = (TimerEntity) jobTimers.get(entity.getId()).getInfo();
		    }
		    		    
		    InitialContext ic = new InitialContext();
			User currentUser = userService.findById(entity.getTimerInfo().getUserId());
			if (entity.getTimerInfo() != null && !currentUser.doesProviderMatch(getCurrentProvider())) {
				throw new BusinessException("Not authorized to execute this job");
			}

			if (jobEntries.containsKey(entity.getJobCategoryEnum())) {
				HashMap<String, String> jobs = jobEntries.get(entity.getJobCategoryEnum());
				if (jobs.containsKey(entity.getJobName())) {
					Job job = (Job) ic.lookup(jobs.get(entity.getJobName()));
					job.execute(entity, getCurrentUser());
				}
			} else {
				throw new BusinessException("Cannot find job category " + entity.getJobCategoryEnum());
			}
		} catch (NamingException e) {
			log.error(e.getMessage());
		
		} catch (Exception e){
		    log.error("Failed to manually execute a job {} of type {}", entity.getName(), entity.getJobName(),e);
		    throw e;
		}
	}

	public void executeAPITimer(TimerInfoDto timerInfoDTO, User currentUser) throws Exception {
		log.info("execute timer={} via api", timerInfoDTO.getTimerName());
		TimerEntity entity=(TimerEntity) getEntityManager().createQuery("FROM TimerEntity where name=:name and provider=:provider")
		.setParameter("name", timerInfoDTO.getTimerName()).setParameter("provider", currentUser.getProvider()).getSingleResult();
		InitialContext ic;
        try {
            ic = new InitialContext();
            if (jobEntries.containsKey(entity.getJobCategoryEnum())) {
                HashMap<String, String> jobs = jobEntries.get(entity.getJobCategoryEnum());
                if (jobs.containsKey(entity.getJobName())) {
                    Job job = (Job) ic.lookup(jobs.get(entity.getJobName()));
                    
                    if(timerInfoDTO.getInvoiceDate()!=null){
                    	entity.setDateCustomValue("BillingRunJob_invoiceDate",timerInfoDTO.getInvoiceDate());
                    }
                    if(timerInfoDTO.getLastTransactionDate()!=null){
                    	entity.setDateCustomValue("BillingRunJob_lastTransactionDate",timerInfoDTO.getLastTransactionDate());
                    }
                    if(timerInfoDTO.getBillingCycle()!=null){
                    	entity.setStringCustomValue("BillingRunJob_billingCycle",timerInfoDTO.getBillingCycle());
                    }
                    job.execute(entity, currentUser);
                }
            } else {
                throw new BusinessException("cannot find job category " + entity.getJobCategoryEnum());
            }
        } catch (NamingException e) {
            log.error(e.getMessage());
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
                return ((TimerEntity) timer.getInfo()).isRunning();
            } catch (Exception e) {
                log.error("Failed to access timer status {}", e.getMessage());
            }
        }
        return false;
    }
    
    public TimerEntity findByName(String name,Provider provider) {
		QueryBuilder qb = new QueryBuilder(TimerEntity.class, "t");
		qb.addCriterionWildcard("t.name", name, true); 
		qb.addCriterionEntity("provider", provider);
		try {
            return (TimerEntity) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
	}
}