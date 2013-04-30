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
import org.meveo.model.jobs.TimerEntity;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.base.PersistenceService;


@Stateless
public class TimerEntityService extends PersistenceService<TimerEntity> {

    static HashMap<String,Job> jobEntries = new HashMap<String, Job>();

    @Inject
    Identity identity;
    
    
    /**
     * Used by job instance classes to register themselves to the timer service
     * 
     * @param name unique name in the application, used by the admin to manage timers
     * @param description describe the task realized by the job
     * @param JNDIName used to instanciate the implementation to execute the job (instantiacion class must be a session EJB)
     */
    public static void registerJob(Job job){
        if(jobEntries.containsKey(job.getClass().getSimpleName())){
            throw new RuntimeException(job.getClass().getSimpleName()+
            		" already registered.");
        }
        jobEntries.put(job.getClass().getSimpleName(), job);
    }

    public void create(TimerEntity entity) {//FIXME: throws BusinessException{
    	if(jobEntries.containsKey(entity.getJobName())){
        	Job job=jobEntries.get(entity.getJobName());
        	entity.getInfo().setJobName(entity.getJobName());
        	TimerHandle timerHandle=job.createTimer(entity.getScheduleExpression(),entity.getInfo());
        	entity.setTimerHandle(timerHandle);
        	super.create(entity);
        } 
    }

    public void update(TimerEntity entity) {//FIXME: throws BusinessException{
        log.info("update "+entity.getJobName());
    	if(jobEntries.containsKey(entity.getJobName())){
        	Job job=jobEntries.get(entity.getJobName());
        	TimerHandle timerHandle=entity.getTimerHandle();
        	log.info("cancelling existing "+timerHandle.getTimer().getTimeRemaining()/1000+" sec");
            timerHandle.getTimer().cancel();
        	timerHandle=job.createTimer(entity.getScheduleExpression(),entity.getInfo());
        	entity.setTimerHandle(timerHandle);
        	super.update(entity);
        } 
    }
    
    public void remove(TimerEntity entity) {//FIXME: throws BusinessException{
    		TimerHandle timerHandle=entity.getTimerHandle();
        	timerHandle.getTimer().cancel();
        	super.remove(entity);
    }
    
    public void execute(TimerEntity entity) throws BusinessException{
        log.info("execute "+entity.getJobName());
        if(entity.getInfo().isActive() && jobEntries.containsKey(entity.getJobName())){
            Job job=jobEntries.get(entity.getJobName());
            job.execute(entity.getInfo()!=null?entity.getInfo().getParametres():null);
        } 
    }

    public void manualExecute(TimerEntity entity) throws BusinessException{
        log.info("manual execute "+entity.getJobName());
        if(jobEntries.containsKey(entity.getJobName())){
            Job job=jobEntries.get(entity.getJobName());
            job.execute(entity.getInfo()!=null?entity.getInfo().getParametres():null,true);
        } 
    }

    @SuppressWarnings("unchecked")
    public TimerEntity findByTimerHandle(TimerHandle timerHandle) {
        String sql = "select distinct t from TimerEntity t";
        QueryBuilder qb = new QueryBuilder(sql);//FIXME: .cacheable();
        qb.addCriterionEntity("t.timerHandle", timerHandle);
        List<TimerEntity> timers=qb.find(em);
        return timers.size()>0?timers.get(0):null;
    }
    
    private QueryBuilder getFindQuery(PaginationConfiguration configuration) {
        String sql = "select distinct t from TimerEntity t";
        QueryBuilder qb = new QueryBuilder(sql);//FIXME: .cacheable();  there is no cacheable in MEVEO QueryBuilder
        qb.addPaginationConfiguration(configuration);
        return qb;
    }

    @SuppressWarnings("unchecked")
	public List<TimerEntity> find(PaginationConfiguration configuration){
    	return getFindQuery(configuration).find(em);
    }
    
    public long count(PaginationConfiguration configuration){
    	return getFindQuery(configuration).count(em);
    }
    
    public List<Timer> getEjbTimers(){
    	List<Timer> timers=new ArrayList<Timer>();
    	
    		for(Job job:jobEntries.values()){
    			try {
    				//TODO: this class should not refer specific job
    				/*if(job instanceof JobImportDocs || job instanceof JobPurge || job instanceof JobDeletedPages
    						|| job instanceof JobExportDocs || job instanceof JobImportPieces || job instanceof JobPieceTraceability
    						|| job instanceof JobTransfertPrimo){*/
    					timers.addAll(job.getTimers());
    				//}
    				
        			
				} catch (Exception e) {
					log.error(e.getMessage());
				}
    			
    		} 
    	return timers;	
    }
    
}
