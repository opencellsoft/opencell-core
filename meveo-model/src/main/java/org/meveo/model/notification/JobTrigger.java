package org.meveo.model.notification;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.meveo.model.jobs.JobInstance;

@Entity
@Table(name="ADM_NOTIF_JOB")
public class JobTrigger extends Notification {
	
	private static final long serialVersionUID = -8948201462950547554L;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "ADM_NOTIF_JOB_PARAMS") 
	private Map<String, String> jobParams = new HashMap<String, String>();
	
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_INSTANCE_ID")
    private JobInstance jobInstance;
 
   public  JobTrigger(){
	   
   }


	
	/**
 * @return the jobParams
 */
public Map<String, String> getJobParams() {
	return jobParams;
}



/**
 * @param jobParams the jobParams to set
 */
public void setJobParams(Map<String, String> jobParams) {
	this.jobParams = jobParams;
}



	/**
	 * @return the jobInstance
	 */
	public JobInstance getJobInstance() {
		return jobInstance;
	}
	
	/**
	 * @param jobInstance the jobInstance to set
	 */
	public void setJobInstance(JobInstance jobInstance) {
		this.jobInstance = jobInstance;
	}
    
   
}