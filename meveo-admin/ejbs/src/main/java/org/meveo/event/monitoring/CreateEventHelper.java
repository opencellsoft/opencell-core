package org.meveo.event.monitoring;

import java.util.Date;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;

@Stateless
public class CreateEventHelper {
	
	@Inject
	private Event<BusinessExceptionEvent> event;
	
	public void  register(BusinessException be){
		BusinessExceptionEvent bee = new BusinessExceptionEvent();
		bee.setBusinessException(be);
		bee.setDateTime(new Date());
		//TODO set meveoInstance code.
		bee.setMeveoInstanceCode("meveoInstanceCode");
		event.fire(bee);
	}
}
