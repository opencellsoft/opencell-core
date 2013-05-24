package org.meveo.model.jobs;

import java.io.Serializable;

import javax.inject.Named;

import org.meveo.model.crm.Provider;

@Named
public class TimerInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5572229725635504448L;
	private boolean active=true;
	private String jobName;
	private String parametres;
	private Provider provider;
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getParametres() {
		return parametres;
	}
	public void setParametres(String parametres) {
		this.parametres = parametres;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public Provider getProvider() {
		return provider;
	}
	public void setProvider(Provider provider) {
		this.provider = provider;
	}
	
	
}
