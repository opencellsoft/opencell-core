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
	private boolean edrTemplate;
	private String quantityEL;
	private String param1EL;
	private String param2EL;
	private String param3EL;
	private String param4EL;
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
	public boolean isEdrTemplate() {
		return edrTemplate;
	}
	public void setEdrTemplate(boolean edrTemplate) {
		this.edrTemplate = edrTemplate;
	}
	public String getQuantityEL() {
		return quantityEL;
	}
	public void setQuantityEL(String quantityEL) {
		this.quantityEL = quantityEL;
	}
	public String getParam1EL() {
		return param1EL;
	}
	public void setParam1EL(String param1el) {
		param1EL = param1el;
	}
	public String getParam2EL() {
		return param2EL;
	}
	public void setParam2EL(String param2el) {
		param2EL = param2el;
	}
	public String getParam3EL() {
		return param3EL;
	}
	public void setParam3EL(String param3el) {
		param3EL = param3el;
	}
	public String getParam4EL() {
		return param4EL;
	}
	public void setParam4EL(String param4el) {
		param4EL = param4el;
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
