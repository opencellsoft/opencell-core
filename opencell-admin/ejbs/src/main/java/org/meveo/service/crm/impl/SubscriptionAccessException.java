package org.meveo.service.crm.impl;

public class SubscriptionAccessException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4233855493891428488L;
	org.meveo.model.jaxb.subscription.Subscription subscrip;
	org.meveo.model.jaxb.subscription.Access access;
	String mess;
	public SubscriptionAccessException(org.meveo.model.jaxb.subscription.Subscription subscrip,org.meveo.model.jaxb.subscription.Access access,
						String mess){
		this.mess=mess;
		this.access=access;
		this.subscrip=subscrip;
	}
	public org.meveo.model.jaxb.subscription.Subscription getSubscrip() {
		return subscrip;
	}
	public void setSubscrip(org.meveo.model.jaxb.subscription.Subscription subscrip) {
		this.subscrip = subscrip;
	}
	public org.meveo.model.jaxb.subscription.Access getAccess() {
		return access;
	}
	public void setAccess(
			org.meveo.model.jaxb.subscription.Access access) {
		this.access = access;
	}
	public String getMess() {
		return mess;
	}
	public void setMess(String mess) {
		this.mess = mess;
	}
	
}
