package org.meveo.model.cache;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class UsageChargeTemplateCache {

	
	private Date lastUpdate;
	private int priority;
	private String filterExpression;
	private String filter1;
	private String filter2;
	private String filter3;
	private String filter4;
	private Set<Long> subscriptionIds= new HashSet<Long>();

	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public String getFilterExpression() {
		return filterExpression;
	}
	public void setFilterExpression(String filterExpression) {
		this.filterExpression = filterExpression;
	}	
	public String getFilter1() {
		return filter1;
	}
	public void setFilter1(String filter1) {
		this.filter1 = filter1;
	}
	public String getFilter2() {
		return filter2;
	}
	public void setFilter2(String filter2) {
		this.filter2 = filter2;
	}
	public String getFilter3() {
		return filter3;
	}
	public void setFilter3(String filter3) {
		this.filter3 = filter3;
	}
	public String getFilter4() {
		return filter4;
	}
	public void setFilter4(String filter4) {
		this.filter4 = filter4;
	}
	public Set<Long> getSubscriptionIds() {
		return subscriptionIds;
	}
	public void setSubscriptionIds(Set<Long> subscriptionIds) {
		this.subscriptionIds = subscriptionIds;
	}

	public String toString(){
		return 	lastUpdate+","+priority+","+filterExpression+","+filter1+","+filter2+","+filter3+","+filter4;
	}
	
}
