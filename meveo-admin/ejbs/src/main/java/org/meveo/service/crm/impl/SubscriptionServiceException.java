package org.meveo.service.crm.impl;

public class SubscriptionServiceException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4233855493891428488L;
	org.meveo.model.jaxb.subscription.Subscription subscrip;
	org.meveo.model.jaxb.subscription.ServiceInstance serviceInst;
	String mess;
	public SubscriptionServiceException(org.meveo.model.jaxb.subscription.Subscription subscrip,org.meveo.model.jaxb.subscription.ServiceInstance serviceInst,
						String mess){
		this.mess=mess;
		this.serviceInst=serviceInst;
		this.subscrip=subscrip;
	}
	public org.meveo.model.jaxb.subscription.Subscription getSubscrip() {
		return subscrip;
	}
	public void setSubscrip(org.meveo.model.jaxb.subscription.Subscription subscrip) {
		this.subscrip = subscrip;
	}
	public org.meveo.model.jaxb.subscription.ServiceInstance getServiceInst() {
		return serviceInst;
	}
	public void setServiceInst(
			org.meveo.model.jaxb.subscription.ServiceInstance serviceInst) {
		this.serviceInst = serviceInst;
	}
	public String getMess() {
		return mess;
	}
	public void setMess(String mess) {
		this.mess = mess;
	}
	
}
