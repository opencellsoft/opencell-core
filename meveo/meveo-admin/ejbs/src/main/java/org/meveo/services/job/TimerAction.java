package org.meveo.services.job;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.context.conversation.End;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.jboss.solder.logging.Logger;
import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerEntity;

@ConversationScoped
@Named
public class TimerAction implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5578930292531038376L;
    

    @Inject
    @RequestParam
    private Instance<Long> timerId;
   

    
    @Inject
    TimerEntityService timerEntityService;
    
    @Inject
    private Logger log;

    @Inject
    private Messages messages;

    @Inject
    private Conversation conversation;


	private TimerEntity timerEntity;
	 
	@Inject
	private JobExecutionService jobExecutionService ;
	    
	private PaginationDataModel<JobExecutionResultImpl> jobResultsDataModel;

    
    @Produces
    @Named
    @ConversationScoped
    public TimerEntity getTimerEntity() {
        conversation.getId();
        if(timerEntity == null){
        if (timerId.get() != null) {
        	timerEntity = timerEntityService.findById(timerId.get());
        	timerEntity.setFieldsFromTimerHandler();
        } else {
            log.info("create new timerEntity");

        	timerEntity = new TimerEntity();
        }
        }
        return timerEntity;
    }

    @End
    public String create(){//FIXME: throws BusinessException {
    	   log.info("createTimer on job : "+timerEntity.getJobName());
           if(timerEntity.getJobName()==null){
               messages.error("Veuillez selectionner un job");
           } else if(!getJobNames().contains(timerEntity.getJobName())){
               messages.error("Veuillez selectionner un job");           
           } else {
        	   timerEntityService.create(timerEntity);
        	   messages.info(new BundleKey("messages", "info.entity.created"), timerEntity.getJobName());
           }
            return "/administration/job/jobs.xhtml?faces-redirect=true";
    }

    @End
    public String updateTimer() {

        try {
        	timerEntityService.update(timerEntity);
            messages.info(new BundleKey("messages", "info.entity.updated"), timerEntity.getJobName());
        } catch (Exception e) {
            messages.error(new BundleKey("messages", "error.user.usernameAlreadyExists"));
            return null;
        }

        return "/administration/job/jobs.xhtml?faces-redirect=true";
    }

    @End
    public String deleteTimer(){//FIXME: throws BusinessException {

    	timerEntityService.remove(timerEntity);
        messages.info(new BundleKey("messages", "info.entity.removed"),timerEntity.getJobName());

        return "/administration/job/jobs.xhtml?faces-redirect=true";
    }
    
    public String executeTimer(){
        try {
            timerEntityService.execute(timerEntity);
            messages.info(new BundleKey("messages", "info.entity.executed"), timerEntity.getJobName());
        } catch (Exception e) {
            messages.error(new BundleKey("messages", "error.execution"));
            return null;
        }
        return "/administration/job/jobs.xhtml?faces-redirect=true";
    }

	/*
     * to be used in picklist to select a job
     */
    public Set<String> getJobNames(){
        return TimerEntityService.jobEntries.keySet();
    }
    
    @Produces
    @RequestScoped
    @Named("jobResultsDataModel")
    public PaginationDataModel<JobExecutionResultImpl> find() {
        if (jobResultsDataModel == null) {
        	jobResultsDataModel = new JobResultsDataModel();
        }

        jobResultsDataModel.forceRefresh();

        return jobResultsDataModel;
    }
    /***********************************************************************************/
    /* DATATABLE MODEL */
    /***********************************************************************************/
    class JobResultsDataModel extends PaginationDataModel<JobExecutionResultImpl> {

        private static final long serialVersionUID = 1L;

        @Override
        protected int countRecords(PaginationConfiguration paginatingData) {
            int userCount = (int) jobExecutionService.count(timerEntity.getJobName(),paginatingData);
            return userCount;
        }

        @Override
        protected List<JobExecutionResultImpl> loadData(PaginationConfiguration configuration) {
            return jobExecutionService.find(timerEntity.getJobName(),configuration);
        }
    }
    
    
    

}
