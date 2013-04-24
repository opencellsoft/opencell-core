package org.meveo.services.job;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.lang.StringUtils;
import org.jboss.weld.Container;
import org.jboss.weld.context.bound.BoundConversationContext;
import org.jboss.weld.context.bound.BoundRequest;
import org.jboss.weld.context.bound.MutableBoundRequest;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.base.PersistenceService;

@Stateless
public class JobExecutionService extends PersistenceService<JobExecutionResultImpl> {
    BoundConversationContext conversationContext;
    BoundRequest request;
    
    public void initConversationContext() {         
        conversationContext = Container.instance().deploymentManager().instance().select(BoundConversationContext.class).get();
        if(!conversationContext.isActive()){
            request = new MutableBoundRequest(new HashMap<String, Object>(), new HashMap<String, Object>());
            conversationContext.associate(request);
            conversationContext.activate();     
        }
        }
    
    public void cleanupConversationContext() {         
        if(conversationContext != null && conversationContext.isActive()) {
            conversationContext.deactivate();
            if(request!=null){
            	conversationContext.dissociate(request);
            }
            
        }
    }
    
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void executeJob(String jobName,String parameter){
        try {
            Job jobInstance = TimerEntityService.jobEntries.get(jobName);
            JobExecutionResult result = jobInstance.execute(parameter);
            persistResult(jobInstance,result,parameter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void persistResult(Job job,JobExecutionResult result,String parameter){
    	 try {
    		 log.info("JobExecutionService persistResult...");
    		 initConversationContext();
             JobExecutionResultImpl entity = JobExecutionResultImpl.createFromInterface(job.getClass().getSimpleName(), result);
             if(!entity.isDone() || (entity.getNbItemsCorrectlyProcessed()+entity.getNbItemsProcessedWithError()+entity.getNbItemsProcessedWithWarning())>0){
            	
                 create(entity,null,null);
                 log.info("persistResult entity.isDone()="+entity.isDone());
                 if(!entity.isDone()){
                     executeJob(job.getClass().getSimpleName(), parameter);
                 }
             } else {
                 log.info(job.getClass().getName() +": nothing to do");
             }
         } catch (Exception e){//FIXME:BusinessException e) {
             e.printStackTrace();
         }finally{
        	 try {
        		 cleanupConversationContext();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
        	 
         }
    }
    
    
    private QueryBuilder getFindQuery(String jobName,PaginationConfiguration configuration) {
        String sql = "select distinct t from JobExecutionResultImpl t";
        QueryBuilder qb = new QueryBuilder(sql);//FIXME:.cacheable();
        if(!StringUtils.isEmpty(jobName)){
            qb.addCriterion("t.jobName", "=", jobName,false);
        }
        qb.addPaginationConfiguration(configuration);
        return qb;
    }

    public long countJobsToDelete(String jobName,Date date){
        long result=0;
        if(date!=null){
            String sql = "select t from JobExecutionResultImpl t";
            QueryBuilder qb = new QueryBuilder(sql);//FIXME:.cacheable();
            if(StringUtils.isEmpty(jobName)){
                qb.addCriterion("t.jobName", "=", jobName,false);
            }
            qb.addCriterion("t.startDate", "<", date,false);
            result = qb.count(em);
        } 
        return result;
    }
    
    public int delete(String jobName,Date date){
        String sql = "delete from JobExecutionResultImpl t";
        QueryBuilder qb = new QueryBuilder(sql);//FIXME:.cacheable();
        qb.addCriterion("t.jobName", "=", jobName,false);
        qb.addCriterion("t.startDate", "<", date,false);
        return qb.getQuery(em).executeUpdate();        
    }
    
    @SuppressWarnings("unchecked")
	public List<JobExecutionResultImpl> find(String jobName,PaginationConfiguration configuration){
    	return getFindQuery(jobName,configuration).find(em);
    }
    
    public long count(String jobName,PaginationConfiguration configuration){
    	return getFindQuery( jobName,configuration).count(em);
    }
    
    
}
